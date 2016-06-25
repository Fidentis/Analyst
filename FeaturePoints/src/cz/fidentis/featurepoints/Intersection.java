package cz.fidentis.featurepoints;

import cz.fidentis.model.Model;
import cz.fidentis.featurepoints.symmetryplane.PlaneEquation;
import cz.fidentis.featurepoints.symmetryplane.Triangle;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Intersection {

    public static float THRESHOLD = 0.1f;
    public static float THRESHOLD2 = 0.00001f;

    public static Point3d getIntersectionBetweenLineAndPlane(PlaneEquation eq, Tuple3f one, Tuple3f two) {

        double Xa = one.x;
        double Ya = one.y;
        double Za = one.z;
        double k;

        //A,B,C,D from Ax+By+Cz+d=0
        double A = eq.getA();
        double B = eq.getB();
        double C = eq.getC();
        double D = eq.getD();

        //case when plane and polygon have the common tangent
        //ignore this?
        if (isPointWithinPlane(eq, one) && isPointWithinPlane(eq, two)) {
            return null;
        }

        try {
            //for parallel it'will be an exception 
            k = -(A * Xa + B * Ya + C * Za + D) / (A * (two.x - one.x)
                    + B * (two.y - one.y)
                    + C * (two.z - one.z));
        } catch (Exception e) {
            return null;
        }

        if (Math.abs(k) > 1 || k < 0) {
            return null;
        }

        double x0 = k * (two.x - one.x) + Xa;
        double y0 = k * (two.y - one.y) + Ya;
        double z0 = k * (two.z - one.z) + Za;

        Point3d resPoint = new Point3d(x0, y0, z0);

        return resPoint;
    }

    public static boolean isPointWithinPlane(PlaneEquation eq, Tuple3f point) {
        double value = (eq.getA() * point.x
                + eq.getB() * point.y
                + eq.getC() * point.z
                + eq.getD());

        return (Math.abs(value) / eq.getMAX() < THRESHOLD2);
    }

    public static LinkedList<Point3d> computeSymmetryPlanePoints(Model model, CornerTable cornerTable, List<Vector3f> centerPoints) {

        ArrayList<Vector3f> verts = model.getVerts();
        PlaneEquation planeEq = createPlaneEquation(centerPoints);

        Corner highestIntersectCorner = null;
        Point3d highestIntersectPoint = null;

        LinkedList<Point3d> intersectPoints = new LinkedList<>();

        for (Corner corner : cornerTable.corners()) {
            if (corner.isBoundary()) {
                Vector3f point1 = verts.get(corner.vertex);
                Vector3f point2 = verts.get(corner.next.vertex);

                Point3d intersectPoint = getIntersectionBetweenLineAndPlane(planeEq, point1, point2);

                if (intersectPoint != null) {
                    if (highestIntersectPoint == null || intersectPoint.y > highestIntersectPoint.y) {
                        highestIntersectPoint = intersectPoint;
                        highestIntersectCorner = corner;
                    }
                }
            }
        }

        intersectPoints.add(highestIntersectPoint);
        Corner intersectCorner = highestIntersectCorner;
        while (intersectCorner != null) {

            intersectCorner = getIntersectionBetweenCornerAndPlane(planeEq, intersectCorner, intersectPoints, verts);          
            intersectCorner = intersectCorner.twin;
        }
        
        return intersectPoints;
    }

    public static Corner getIntersectionBetweenCornerAndPlane(PlaneEquation planeEq, Corner corner, LinkedList<Point3d> intersectPoints, ArrayList<Vector3f> verts) {
        Vector3f point1 = verts.get(corner.vertex);
        Vector3f point2 = verts.get(corner.next.vertex);
        Vector3f point3 = verts.get(corner.prev.vertex);

        Point3d intersectPoint;
        Corner intersectCorner = null;

        intersectPoint = getIntersectionBetweenLineAndPlane(planeEq, point2, point3);
        if (intersectPoint != null) {
            intersectCorner = corner.next;
        } else {
            intersectPoint = getIntersectionBetweenLineAndPlane(planeEq, point3, point1);
            if (intersectPoint != null) {
                intersectCorner = corner.prev;
            }
        }
        if (intersectPoint != null) {
            intersectPoints.add(intersectPoint);
        }

        return intersectCorner;
    }

    public static PlaneEquation createPlaneEquation(List<Vector3f> centerPoints) {

        Vector3f minYpoint = null;
        Vector3f maxYpoint = null;
        Vector3f maxZpoint = null;

        for (Vector3f vect : centerPoints) {
            if (minYpoint == null) {
                minYpoint = maxYpoint = maxZpoint = vect;
            } else {
                if (vect.y < minYpoint.y) {
                    minYpoint = vect;
                }
                if (vect.y > maxYpoint.y) {
                    maxYpoint = vect;
                }
                if (vect.z > maxZpoint.z) {
                    maxZpoint = vect;
                }
            }
        }

        maxZpoint = new Vector3f(maxZpoint.x, maxZpoint.y, maxZpoint.z + Math.abs(maxZpoint.z));
        maxYpoint = new Vector3f(maxYpoint.x, maxYpoint.y + Math.abs(maxYpoint.y), maxYpoint.z);
        minYpoint = new Vector3f(minYpoint.x, minYpoint.y - Math.abs(minYpoint.y), minYpoint.z);

        return new PlaneEquation(new Triangle(minYpoint, maxZpoint, maxYpoint));
    }

    public static List<Vector3f> computeFiniteDerivative(List<Vector3f> symmetryPlanePoints) {
        ArrayList<Vector3f> newList = new ArrayList<>();
//        for (int i = 1; i < symmetryPlanePoints.size(); i++) {
//            Vector3f p1 = symmetryPlanePoints.get(i);
//            Vector3f p2 = symmetryPlanePoints.get(i - 1);
//
//            float deriv = Math.abs(Math.abs(p1.z) - Math.abs(p2.z)) / Math.abs(Math.abs(p1.y) - Math.abs(p2.y));
//            System.out.println("'Derivácia' v bode " + i + "-> " + deriv);
//            if (deriv > 1) {
//                newList.add(p2);
//            }
//
//        }

        for (int i = 1; i < symmetryPlanePoints.size() - 1; i++) {
            Vector3f p1 = symmetryPlanePoints.get(i - 1);
            Vector3f p2 = symmetryPlanePoints.get(i);
            Vector3f p3 = symmetryPlanePoints.get(i + 1);

            Vector3f v1 = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
            Vector3f v2 = new Vector3f(p2.x - p3.x, p2.y - p3.y, p2.z - p3.z);

            float angle = v1.angle(v2);
            float angleInDeg = angle * (float) 57.2957795;
            System.out.println("Uhol v bode " + i + "-> " + angle + "rad. -> " + angleInDeg + "°");
            if (angleInDeg < 160.f) {
                newList.add(p2);
            }
        }
        return newList;
    }
}