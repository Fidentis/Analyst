/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.pdm;

/**
 *
 * @author Rasto1
 */
public class CurvatureMaps {
    
    /**
     * Compute shape index
     * @param minCurv 
     * @param maxCurv
     * @return shape index of point
     */
    public float shapeIndex(float minCurv, float maxCurv) {
        float up = maxCurv + minCurv;
        float down = maxCurv - minCurv;

        return (float) ((2 / Math.PI) * Math.atan2(up, down));
    }

    /**
     * compute curvedness index
     * @param minCurv
     * @param maxCurv
     * @return curvedness indexo of point
     */
    public float curvedness(float minCurv, float maxCurv) {
        return (float) ((Math.sqrt(minCurv * minCurv + maxCurv * maxCurv)) / 2);
    }
    
    
}
