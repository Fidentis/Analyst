package cz.fidentis.featurepoints.curvature;

/*
 Copyright 2011 Milian Wolff <mail@milianw.de>
	
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
import cz.fidentis.model.Model;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import java.util.HashSet;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;

/**
 * Computation class for curvature at vertices of a geometry.
 */
public class Curvature_model {

    private Model m_model;
    private CornerTable m_cornerTable;
    private VertexCurvature[] m_vertexMap;
    private CotanCache m_cotanCache;
    private Model model;


    public Curvature_model(Model model) {

        m_model = model;
        m_cornerTable = new CornerTable(model);
        m_vertexMap = new VertexCurvature[model.getVerts().size()];
        m_cotanCache = new CotanCache(m_cornerTable.size());
        computeCurvature();
    }

    /**
     * @return CornerTable which was calculated from Geometry
     */
    public CornerTable cornerTable() {
        return m_cornerTable;
    }

    /**
     * @return curvature calculations for each vertex
     */
    VertexCurvature[] curvatures() {
        return m_vertexMap;
    }

    Model model() {
        return m_model;
    }

    public class VertexCurvature {

        public VertexCurvature() {
            area = 0;
            meanOp = new PdVector(0, 0, 0);
            gaussian = 0;
            // not computed by default
            B = null;
        }
        /**
         * mean curvature normal operator
         *
         * note: not normalized! i.e. misses 1/(2*area) see eq. 8.
         *
         */
        public PdVector meanOp;
        /**
         * gaussian curvature Operator note: not normalized! i.e. is just the
         * sum of angles, misses (2pi - ...)/area note: in degree!
         *
         * @see gaussianCurvature()
         *
         */
        public double gaussian;
        /**
         * mixed area see fig. 4
         */
        public double area;
        /**
         * Symmetric curvature tensor:
         *
         * a | b
         * ---|--- b | c
         */
        public PdMatrix B;

        public double gaussianCurvature() {
            return (2.0d * Math.PI - Math.toRadians(gaussian)) / area;
        }

        public double meanCurvature() {
            // note: 1/2 from K as vector, another 1/2 for K_H
            return 1.0d / (4.0 * area) * meanOp.length();
        }

        public double minimumCurvature() {
            //return meanCurvature() - Math.sqrt(Math.pow(meanCurvature(), 2) - gaussianCurvature());
            return meanCurvature() - Math.sqrt(delta());
        }

        public double maximumCurvature() {
            return meanCurvature() + Math.sqrt(delta());
        }

        public double delta() {
            return Math.max(0, Math.pow(meanCurvature(), 2) - gaussianCurvature());
        }
    }

    /**
     * compute curvature values of each vertex in m_geometry
     */
    private void computeCurvature() {
        System.out.println("computing curvature");
//        System.out.println("computing curvature of " + m_geometry.getName());
        // iterate over all corners, each time adding the partial 
        // contribution to the mixed area and mean curvature normal operator
        // note: each corner is one summand of the sums in eq. 8 / fig 4.
        // we take xi = corner.vertex
        // and xj = corner.prev.vertex
        // hence the angles are:
        // alpha = angle(corner.next.vertex)
        // beta = angle(corner.next.opposite.vertex)
        // note: we must take obtuse triangles into account and
        // can only sum parts of the voronoi cell up at each time
        // the e.q. for that is given in sec. 3.3 on page 8
        // for bad geometries, like the hand
        HashSet<Integer> blackList = new HashSet<>();
        for (Corner corner : m_cornerTable.corners()) {
            Corner cno = corner.next.opposite;
            if (cno == null) {
                ///TODO: what to do in such cases?
                continue;
            }

            //note: alpha, beta, gamma are all in corner.triangle
            //note: all values are apparently in degrees!
            // alpha: angle at x_i in T, between AB and AC
            // compare to angle(P) in paper
            double alpha = model.getVertAngle(corner.triangle, corner.localVertexIndex);
            // beta: angle at prev corner, between AB and BC
            // compare to angle(Q)
            double beta = model.getVertAngle(corner.triangle, corner.prev.localVertexIndex);
            // gamma: angle at next corner, between AC and BC
            // compare to angle(R)
            double gamma = model.getVertAngle(corner.triangle, corner.next.localVertexIndex);

            if (alpha == 0 || beta == 0 || gamma == 0) {
                System.err.println("Zero-angle encountered in triangle, skipping: " + corner.triangle);
                blackList.add(corner.vertex);
                blackList.add(corner.prev.vertex);
                blackList.add(corner.next.vertex);
                continue;
            }

            double cotGamma = m_cotanCache.cotan(gamma);

            // edge between A and B, angle is beta
            // compare to PQ
            PdVector AB = Utils.subNewVector(model.getVerts().get(corner.vertex),
                    model.getVerts().get(corner.prev.vertex));

            double area = -1;
            // check for obtuse angle
            if (alpha >= 90 || beta >= 90 || gamma >= 90) {
                area = model.getAreaOfElement(corner.triangle);
                assert area > 0;
                // check if angle of T at x is obtuse
                if (alpha > 90) {
                    area /= 2.0d;
                } else {
                    area /= 4.0d;
                }
            } else {
                // voronoi region of x in t:
                // edge between A and C, angle is gamma
                // compare to PR
                PdVector AC = Utils.subNewVector(model.getVerts().get(corner.vertex),
                        model.getVerts().get(corner.next.vertex));
                double cotBeta = m_cotanCache.cotan(beta);
                area = 1.0d / 8.0d * (AB.sqrLength() * cotGamma + AC.sqrLength() * cotBeta);
                assert area > 0;
            }

            VertexCurvature cache = m_vertexMap[corner.vertex];
            if (cache == null) {
                cache = new VertexCurvature();
                m_vertexMap[corner.vertex] = cache;
            }
            // now e.q. 8, with alpha = our gamma from above, and beta = cnoAngle
            double cnoAngle = model.getVertAngle(cno.triangle, cno.localVertexIndex);
            if (cnoAngle == 0) {
                System.err.println("Zero-Angle encountered in triangle " + cno.triangle + ", vertex: " + corner.vertex);
                blackList.add(corner.vertex);
                continue;
            }
            double cotCnoAngle = m_cotanCache.cotan(cnoAngle);
            cache.meanOp.add(cotGamma + cotCnoAngle, AB);
            cache.gaussian += alpha;
            cache.area += area;
        }
        for (int i : blackList) {
            m_vertexMap[i] = null;
        }
        System.out.println("done");
    }

    
    
    
    public double[] getCurvature(CurvatureType type) {
        
        System.out.println("computing curvature: " + type);
        Curvature_model.VertexCurvature[] curvatures = curvatures();
        double values[] = new double[curvatures.length];
        double totalGaussian = 0;
        double maxValue = -1000;
        double minValue = 1000;
        for (int i = 0; i < curvatures.length; ++i) {
            Curvature_model.VertexCurvature c = curvatures[i];
            if (c == null) {
                values[i] = 0;
                continue;
            }
            assert c.area > 0;
            if (type == CurvatureType.Mean) {
                values[i] = c.meanCurvature();
                assert values[i] >= 0;
            } else if (type == CurvatureType.Minimum) {
                values[i] = c.minimumCurvature();
            } else if (type == CurvatureType.Maximum) {
                values[i] = c.maximumCurvature();
                assert values[i] >= 0;
            } else if (type == CurvatureType.Gaussian) {
                values[i] = c.gaussianCurvature();
            }
            totalGaussian += Math.toRadians(c.gaussian);

            if (values[i] > maxValue) {
                maxValue = values[i];
            } else if (values[i] < minValue) {
                minValue = values[i];
            }

            //System.out.println(i + ".value: " + values[i]);
        }
     
        System.out.println("sum of theta_ij: " + totalGaussian);
        System.out.println("divided by 2pi: " + (totalGaussian / (2.0d * Math.PI)));
        System.out.println("max value: " + maxValue + ", min value: " + minValue);
        System.out.println("total angle deficit:" + (model.getVerts().size() - totalGaussian / (2.0d * Math.PI)));
        System.out.println("done");

        return values;

    }
}
