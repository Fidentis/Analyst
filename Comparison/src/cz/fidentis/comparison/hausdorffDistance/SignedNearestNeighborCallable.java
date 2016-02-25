/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.comparison.icp.KdTree;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class SignedNearestNeighborCallable implements Callable<Float>{
    private final KdTree mainF;
    private final Vector3f compareFpoint;
    private final Vector3f compareFnormal;
    private final boolean useRelative;

    public SignedNearestNeighborCallable(KdTree mainF, Vector3f compareFpoint, Vector3f compareFnormal, boolean useRelative) {
        this.mainF = mainF;
        this.compareFpoint = compareFpoint;
        this.compareFnormal = compareFnormal;
        this.useRelative = useRelative;
    }
   
    
    @Override
    public Float call() throws Exception {
        float distance = 0;
        /*float sign = 1;
        Vector3f neighbour = mainF.nearestNeighbour(compareFpoint);

        /*if (compareFnormal != null && useRelative) {
            sign = getSign(compareFpoint, neighbour, compareFnormal);
        }
        distance = (float) (sign * MathUtils.instance().distancePoints(compareFpoint, neighbour));*/
        
        distance = (float) mainF.nearestDistance(compareFpoint, compareFnormal, useRelative);

        return distance;
    }    
}
