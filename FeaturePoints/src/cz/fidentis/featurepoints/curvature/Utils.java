package cz.fidentis.featurepoints.curvature;

/*
 Copyright 2012 Milian Wolff <mail@milianw.de>
	
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of 
 the License, or (at your option) any later version.
	
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
	
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import jv.number.PuComplex;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;

class Utils {

    public static PdMatrix principleDirections2x2(PdMatrix matrix2x2) {
        return solveEigen2x2(matrix2x2, null, false);
    }

    /**
     * Calculate eigenvalues and vectors of a 2x2 matrix.
     *
     * see also:
     * http://en.wikipedia.org/wiki/Eigenvalue_algorithm#Eigenvalues_of_2.C3.972_matrices
     *
     * @param A 2x2 matrix for with the eigen problem should be solved
     * @param eigenValues optional 2dim vector that will hold the eigen values
     * @param allowNonOrthogonal set to true if non-orthogonal eigen vectors
     * should be allowed
     * @return matrix of eigen vectors, row 0 contains the major, row 1 the
     * minor
     */
    public static PdMatrix solveEigen2x2(PdMatrix A, PdVector eigenValues, boolean allowNonOrthogonal) {
        assert eigenValues == null || eigenValues.getSize() == 2;
        assert A.getNumCols() == 2;
        assert A.getNumRows() == 2;

        double trace = A.getEntry(0, 0) + A.getEntry(1, 1);
        double root = Math.sqrt(trace * trace - 4 * A.det());
        double L_1 = 0.5 * (trace + root);
        double L_2 = 0.5 * (trace - root);
        if (eigenValues != null) {
            eigenValues.setEntry(0, L_1);
            eigenValues.setEntry(1, L_2);
        }
        boolean singular = Double.isNaN(L_1) || Double.isNaN(L_2);
        assert L_2 <= L_1 || singular : "L_1: " + L_1 + ", L_2: " + L_2;

        PdVector major = new PdVector(2);
        PdVector minor = new PdVector(2);
        if (A.getEntry(1, 0) != 0 && !singular) {
            major.setEntry(0, L_1 - A.getEntry(1, 1));
            major.setEntry(1, A.getEntry(1, 0));
            minor.setEntry(0, L_2 - A.getEntry(1, 1));
            minor.setEntry(1, A.getEntry(1, 0));
            major.normalize();
            minor.normalize();
        } else if (A.getEntry(0, 1) != 0 && !singular) {
            major.setEntry(0, A.getEntry(0, 1));
            major.setEntry(1, L_1 - A.getEntry(0, 0));
            minor.setEntry(0, A.getEntry(0, 1));
            minor.setEntry(1, L_2 - A.getEntry(0, 0));
            major.normalize();
            minor.normalize();
        }
        if (minor.equals(major) || singular
                || Double.isNaN(minor.length()) || minor.length() == 0
                || Double.isNaN(major.length()) || major.length() == 0) {
            // singular
            major.setEntry(0, 1);
            major.setEntry(1, 0);
            minor.setEntry(0, 0);
            minor.setEntry(1, 1);
        }
        if (!allowNonOrthogonal && minor.dot(major) > 1E-10) {
            System.err.println("Non-Orthogonal principle directions computed:");
            System.err.println("Minor: " + minor.toShortString());
            System.err.println("Major: " + major.toShortString());
            System.err.println("Dot: " + minor.dot(major));
        }
        PdMatrix ret = new PdMatrix(2, 2);
        ret.setRow(0, major);
        ret.setRow(1, minor);
        return ret;
    }

    public static PdVector solveCramer(PdMatrix A, PdVector b) {
        // see e.g.: http://en.wikipedia.org/wiki/Cramer%27s_rule
        assert A.isSquare();
        assert b.getSize() == A.getSize() : b.getSize() + " VS " + A.getSize();
        double det = A.det();
        // x is vector of [l, m, n]
        PdVector x = new PdVector(A.getSize());
        for (int k = 0; k < A.getSize(); ++k) {
            PdMatrix A2 = PdMatrix.copyNew(A);
            A2.setColumn(k, b);
            x.setEntry(k, A2.det() / det);
        }
        return x;
    }
    // theta in radians!

    public static PdMatrix rotationMatrix(double theta) {
        PdMatrix R = new PdMatrix(2, 2);
        R.setEntry(0, 0, Math.cos(theta));
        R.setEntry(0, 1, -Math.sin(theta));
        R.setEntry(1, 0, Math.sin(theta));
        R.setEntry(1, 1, Math.cos(theta));
        return R;
    }
    // theta in radians!

    public static PdMatrix reflectionMatrix(double theta) {
        PdMatrix R = rotationMatrix(theta);
        R.setEntry(0, 1, R.getEntry(0, 1) * -1);
        R.setEntry(1, 1, R.getEntry(1, 1) * -1);
        return R;
    }

    public static boolean onSameSide(PdVector p1, PdVector a, PdVector b, PdVector c) {
        PdVector c_min_b = PdVector.subNew(c, b);
        PdVector cp1 = PdVector.crossNew(c_min_b, PdVector.subNew(p1, b));
        PdVector cp2 = PdVector.crossNew(c_min_b, PdVector.subNew(a, b));
        return cp1.dot(cp2) >= 0;
    }

    public static boolean inTriangle(PdVector p, PdVector[] triangle) {
        assert triangle.length == 3;
        PdVector a = triangle[0];
        PdVector b = triangle[1];
        PdVector c = triangle[2];
        return onSameSide(p, a, b, c) && onSameSide(p, b, a, c) && onSameSide(p, c, a, b);
    }

    /**
     * Find roots of a cubic polynomial
     *
     * see also:
     * http://en.wikipedia.org/wiki/Cubic_function#Roots_of_a_cubic_function
     *
     * @param a coefficient of x^3
     * @param b coefficient of x^2
     * @param c coefficient of x^1
     * @param d coefficient of x^0
     *
     * @return list of potentially complex roots
     */
    public static ArrayList<PuComplex> cubicRoots(double a, double b, double c, double d) {
        ArrayList<PuComplex> ret = new ArrayList<PuComplex>(3);
        assert a != 0;
        final double discriminant = 18d * a * b * c * d - 4d * b * b * b * d
                + b * b * c * c - 4d * a * c * c * c
                - 27d * a * a * d * d;
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);
        final PuComplex Q = PuComplex.sqrt(new PuComplex(-27d * a * a * discriminant));
        PuComplex C = PuComplex.mult(PuComplex.add(Q, 2d * b * b * b - 9d * a * b * c + 27d * a * a * d), 0.5d);
        C.pow(1d / 3d);

        final double alpha = -b / (3d * a);
        final PuComplex gamma = PuComplex.div(b * b - 3d * a * c, C);
        final PuComplex betaPlus = new PuComplex(1d / (6d * a), +Math.sqrt(3) / (6d * a));
        final PuComplex betaMinus = new PuComplex(1d / (6d * a), -Math.sqrt(3) / (6d * a));

        {
            PuComplex x1 = new PuComplex(alpha);
            x1.sub(PuComplex.div(C, 3d * a));
            x1.sub(PuComplex.div(gamma, 3d * a));
            ret.add(x1);
        }
        {
            PuComplex x2 = new PuComplex(alpha);
            x2.add(PuComplex.mult(C, betaPlus));
            x2.add(PuComplex.mult(gamma, betaMinus));
            ret.add(x2);
        }
        {
            // NOTE: actually just the complex conjugate of x2...
            PuComplex x3 = new PuComplex(alpha);
            x3.add(PuComplex.mult(C, betaMinus));
            x3.add(PuComplex.mult(gamma, betaPlus));
            ret.add(x3);
        }
        /*
         double innerRoot = Math.sqrt(innerRadicand);
         assert !Double.isNaN(innerRoot);
         double cubicRootOperand = 2d * b*b*b - 9d * a*b*c + 27d * a*a*d;
         double cubicRootPlus = Math.pow(0.5d*(cubicRootOperand + innerRoot), 1d/3d);
         assert !Double.isNaN(cubicRootPlus);
         double cubicRootMinus = Math.pow(0.5d*(cubicRootOperand - innerRoot), 1d/3d);
         assert !Double.isNaN(cubicRootMinus);
         ret.add(new PuComplex(negBOver3a - cubicRootPlus / (3d*a) - cubicRootMinus / (3d*a)));
         */
        /*
         PuComplex innerRoot = new PuComplex(-27d * a * a * discriminant);
         innerRoot.sqrt();
         PuComplex cubicRootPlus = new PuComplex(2d * b*b*b - 9d * a*b*c + 27d*a*a*d);
         cubicRootPlus.add(innerRoot);
         cubicRootPlus.mult(0.5d);
         cubicRootPlus.pow(1d/3d);
         PuComplex cubicRootMinus = new PuComplex(2d * b*b*b - 9d * a*b*c + 27d*a*a*d);
         cubicRootMinus.sub(innerRoot);
         cubicRootMinus.mult(0.5d);
         cubicRootMinus.pow(1d/3d);
		
         {
         PuComplex x1 = new PuComplex(negBOver3a);
         x1.sub(PuComplex.mult(cubicRootPlus, 1d/(3d*a)));
         x1.sub(PuComplex.mult(cubicRootMinus, 1d/(3d*a)));
         ret.add(x1);
         }
         {
         PuComplex x2 = new PuComplex(negBOver3a);
         x2.add(PuComplex.mult(cubicRootPlus, imagPlus));
         x2.add(PuComplex.mult(cubicRootMinus, imagNeg));
         ret.add(x2);
         }
         {
         PuComplex x3 = new PuComplex(negBOver3a);
         x3.add(PuComplex.mult(cubicRootPlus, imagNeg));
         x3.add(PuComplex.mult(cubicRootMinus, imagPlus));
         ret.add(x3);
         }
         */
        ret.trimToSize();
        System.out.println(ret);
        return ret;
    }
    
    public static PdVector subNewVector(Vector3f v1, Vector3f v2) {
        return new PdVector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }
}
