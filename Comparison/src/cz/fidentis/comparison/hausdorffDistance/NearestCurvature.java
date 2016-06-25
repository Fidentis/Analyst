/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.comparison.icp.KdTreeIndexed;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class NearestCurvature {
    private static NearestCurvature instance;
    private static final int USED_THREADS = Runtime.getRuntime().availableProcessors();
    
    private NearestCurvature(){}
    
    public static NearestCurvature instance(){
        if(instance == null){
            instance = new NearestCurvature();
        }
        
        return instance;
    }
    
    public List<Float> nearestCurvature(KdTreeIndexed mainF, List<Vector3f> comparedF, double[] mainCurvature, double[] secondaryCurvature){
        List<Future<Float>> computDist = new LinkedList<>();
        ExecutorService executor = Executors.newFixedThreadPool(USED_THREADS);
        List<Float> distance = new ArrayList<>(comparedF.size());
        
        for(int i = 0; i < secondaryCurvature.length; i++){
            Future<Float> dist = executor.submit(new NearestCuravatureCallable(mainCurvature, mainF, secondaryCurvature[i], comparedF.get(i)));
            computDist.add(dist);
        }
        
         for(Future<Float> f : computDist){
            try {
                distance.add(f.get());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(HausdorffDistance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
         executor.shutdown();

        return distance;
        
    }
}
