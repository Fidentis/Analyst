/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.icp;

import cz.fidentis.comparison.kdTree.KdTree;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class NearestNeighborCallable implements Callable<Vector3f>{

    private final KdTree mainF;
    private final Vector3f p;

    public NearestNeighborCallable(KdTree mainF, Vector3f p) {
        this.mainF = mainF;
        this.p = p;
    }
    
    
    
    @Override
    public Vector3f call() throws Exception {
       Vector3f nn = mainF.nearestNeighbour(p);
       
       return nn;
    }
    
}
