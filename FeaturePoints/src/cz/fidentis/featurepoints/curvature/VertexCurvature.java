/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.curvature;

import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;

/**
 *
 * @author xferkova
 */
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
