/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.undersampling;

import cz.fidentis.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class RandomUndersampling {
    private static RandomUndersampling instance;
    
    private RandomUndersampling(){
        
    }
    
    public static RandomUndersampling instance(){
        return instance != null ? instance : (instance = new RandomUndersampling());
    }
    
    /**
     * Creates list of integers representing indexes of vertices which were chosen to be used for random undersampling.
     * Size of returned list is equal to given percentage of mesh size.
     * @param percentage - percentage of vertices to be used for random undersampling.
     * @param mesh - list of vertices to randomly pick vertices for undersampling from
     * @return list of indeces representing picked vertices
     */
    public List<Integer> randomPercentage(int percentage, List<Vector3f> mesh){
        float scale = percentage / 100f;
        
        int size = (int) (mesh.size() * scale);
        
        List<Integer> undersamples = randomNumberOfSamples(size, mesh);
        
        return undersamples;
    }
    
    /**
     * Creates list of vertices to be used for random undersampling. 
     * Size of the returned list is equal to samples value
     * @param samples - number of vertices to be used for random undersampling. If this values is greater than number of vertices than all vertices are used
     * @param mesh - list of vertices to randomly pick vertices for undersampling from
     * @return list of indeces representing picked vertices
     */
    public List<Integer> randomNumberOfSamples(int samples, List<Vector3f> mesh){
        int size = samples;
        
        if(samples > mesh.size()){
            size = samples;
        }
        
        Random r = new Random();
        
        List<Integer> undersamples = new ArrayList<Integer>(size);
        List<Integer> randomPick = ListUtils.instance().populateList(mesh.size());
        
        for(int i = 0; i < size; i++){
            int pick = r.nextInt(randomPick.size());
            undersamples.add(pick);
            randomPick.remove(pick);
        }
        
        return undersamples;
    }
}
