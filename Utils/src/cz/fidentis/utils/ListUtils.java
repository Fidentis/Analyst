/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class ListUtils {

    private static ListUtils instance;
    
    private ListUtils(){}
    
    public static ListUtils instance(){
        return instance != null ? instance : (instance = new ListUtils());
    }
    
    
    /**
     * Creates list containing integers 0 - (size - 1)
     * @param size - size of the list
     * @return list containing integers 0 - (size - 1)
     */
    public List<Integer> populateList(int size) {
        List<Integer> populatedList = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            populatedList.add(i);
        }
        return populatedList;
    }

    /**
     * Creates list of vertices based on pre-computed indeces.
     * @param indices - indeces of vertices to be picked from original mesh
     * @param mesh - original mesh
     * @return picked vertices
     */
    public List<Vector3f> getVectorList(List<Integer> indices, List<Vector3f> mesh) {
        if (indices == null || mesh == null) {
            return new ArrayList<>();
        }
        List<Vector3f> vectorList = new ArrayList<Vector3f>(indices.size());
        for (Integer i : indices) {
            vectorList.add(mesh.get(i));
        }
        return vectorList;
    }

    public List<Float> reverseList(List<Float> values) {
        List<Float> reversed = new ArrayList<>(values.size());
        for (int i = values.size() - 1; i >= 0; i--) {
            reversed.add(values.get(i));
        }
        return reversed;
    }
    
    public List<Vector3f> populateVectorList(int size){
        List<Vector3f> populatedList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            populatedList.add(new Vector3f());
        }
        return populatedList;
    }
    
    public ArrayList<ArrayList<Float>> populateListList(int size){
        ArrayList<ArrayList<Float>> res = new ArrayList<>(size);
        
        for(int i = 0; i < size; i++){
            res.add(new ArrayList<Float>(size));
        }
        
        return res;
    }
}
