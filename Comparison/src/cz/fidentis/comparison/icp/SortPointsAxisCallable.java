/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.icp;

import cz.fidentis.utils.CompareUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class SortPointsAxisCallable implements Callable<List<Integer>>{

    private final List<Vector3f> points;
    private final int axis;
    private final List<Integer> indices;

    public SortPointsAxisCallable(List<Vector3f> points, int axis, List<Integer> indices) {
        this.points = points;
        this.axis = axis;
        this.indices = indices;
    }
        
    
    @Override
    public List<Integer> call() throws Exception {
        List<Integer> res = sortPoints(indices, axis, points);
        
        return res;
    }
    
    /**
     * Sort points based on their axis values.
     * 
     * @param points - list of the points to be sorted
     * @param level - axis based on which short will be performed
     * @return - list of sorted points
     */
    private List<Integer> sortPoints(List<Integer> points, int level, List<Vector3f> p){
        List<Integer> sortedList = new ArrayList<>(points.size());
        
        sortedList.addAll(points);
        
        sortedList = mergeSort(sortedList,level, p);
        
        return sortedList;
        
    }
    
    /**
     * Performing Merge Sort algorithm recursively, based on the axis given.
     * 
     * @param points - points to be sorted
     * @param level - axis based on which sort will be performed
     * @return - list of sorted points
     */
    private List<Integer> mergeSort(List<Integer> points, int level, List<Vector3f> p){        
        if(points.size() <= 1){
            return points;
        }
        
        List<Integer> left;
        List<Integer> right;
        
        int mid = (points.size()/2);      
        
        left = points.subList(0, mid);
        right = points.subList(mid, points.size());
      
        
        left = mergeSort(left, level, p);
        right = mergeSort(right, level, p);
        
        return merge(left, right, level, p);
    }
    
    /**
     * Merging split list as defined by Merge Sort alogrithm
     * 
     * @param left - left list
     * @param right - right list
     * @param level - axis based on which merging will be performed
     * @return 
     */
    private List<Integer> merge(List<Integer> left, List<Integer> right, int level, List<Vector3f> points){
        List<Integer> mergedList = new ArrayList<>(left.size() + right.size());
        
        int fromLeft = 0;
        int fromRight = 0;
      
        while(fromLeft < left.size() || fromRight < right.size()){
           if(fromLeft < left.size() && fromRight < right.size()){
               if(CompareUtils.instance().comparePointsOnLevel(points.get(left.get(fromLeft)), points.get(right.get(fromRight)), level)){
                   mergedList.add(left.get(fromLeft));
                   fromLeft++;
               }else{
                   mergedList.add(right.get(fromRight));
                   fromRight++;
               }
           }else if(fromLeft < left.size()){
               mergedList.add(left.get(fromLeft));
               fromLeft++;
           }else if(fromRight < right.size()){
               mergedList.add(right.get(fromRight));
               fromRight++;
           }
        }
        
        return mergedList;
    }
        
}
