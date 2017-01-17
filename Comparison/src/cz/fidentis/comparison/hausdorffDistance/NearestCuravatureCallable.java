/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.comparison.icp.KdTree;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.featurepoints.curvature.Curvature_jv;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class NearestCuravatureCallable implements Callable<Float>{
    private double[] mainCurvature;
    private KdTreeIndexed mainF;
    private double secondaryCurvature;
    private Vector3f compareFpoint;

    public NearestCuravatureCallable(double[] mainCurvature, KdTreeIndexed mainF, double secondaryCurvature, Vector3f compareFpoint) {
        this.mainCurvature = mainCurvature;
        this.mainF = mainF;
        this.secondaryCurvature = secondaryCurvature;
        this.compareFpoint = compareFpoint;
    }
    
    
    
    @Override
    public Float call() throws Exception {
        int index = mainF.nearestIndex(compareFpoint);
        
        return (float) Math.abs(mainCurvature[index] - secondaryCurvature);
    }
    
}
