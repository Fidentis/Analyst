/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class NearestCuravatureCallable implements Callable<Float>{
    private double[] mainCurvature;
    private KDTreeIndexed mainF;
    private double secondaryCurvature;
    private Vector3f compareFpoint;

    public NearestCuravatureCallable(double[] mainCurvature, KDTreeIndexed mainF, double secondaryCurvature, Vector3f compareFpoint) {
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
