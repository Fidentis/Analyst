/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ex3me
 */
public class SortUtils {
    private static SortUtils instance;
    
    public static SortUtils instance(){
        if(instance == null){
            instance = new SortUtils();
        }
        
        return instance;
    }

    private SortUtils() {
    }
    
    
     /**
     * Sorts values in given list from min to max
     * 
     * @param values - list of the values to be sorted
     * @return - list of sorted points
     */
    public List<Float> sortValues(List<Float> values){
        List<Float> sortedList = new ArrayList<Float>(values.size());
        
        sortedList.addAll(values);
        
        sortedList = mergeSort(sortedList);
        
        return sortedList;
        
    }
    
    /**
     * Performing Merge Sort algorithm recursively, based on the axis given.
     * 
     * @param points - points to be sorted
     * @param level - axis based on which sort will be performed
     * @return - list of sorted points
     */
    private List<Float> mergeSort(List<Float> values){        
        if(values.size() <= 1){
            return values;
        }
        
        List<Float> left;
        List<Float> right;
        
        int mid = values.size()/2;      
        
        left = values.subList(0, mid);
        right = values.subList(mid, values.size());
      
        
        left = mergeSort(left);
        right = mergeSort(right);
        
        return merge(left, right);
    }
    
    /**
     * Merging split list as defined by Merge Sort alogrithm
     * 
     * @param left - left list
     * @param right - right list
     * @return 
     */
    private List<Float> merge(List<Float> left, List<Float> right){
        List<Float> mergedList = new ArrayList<Float>(left.size() + right.size());
        
        int fromLeft = 0;
        int fromRight = 0;
      
        while(fromLeft < left.size() || fromRight < right.size()){
           if(fromLeft < left.size() && fromRight < right.size()){
               if(left.get(fromLeft) < right.get(fromRight)){
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
    
     /**
     * Sorts values in given list from min to max
     * 
     * @param values - list of the values to be sorted
     * @param indices - list of indices of values (from 0 to values.size())
     * @return - list of sorted points
     */
    public List<Integer> sortIndices(List<Float> values, List<Integer> indices){        
        indices = mergeSortIndices(values, indices);
        
        return indices;
        
    }
    
    /**
     * Performing Merge Sort algorithm recursively, based on the axis given.
     * 
     * @param points - points to be sorted
     * @param level - axis based on which sort will be performed
     * @return - list of sorted points
     */
    private List<Integer> mergeSortIndices(List<Float> values, List<Integer> indices){        
        if(indices.size() <= 1){
            return indices;
        }
        
        List<Integer> leftIndices;
        List<Integer> rightIndices;
        
        int mid = indices.size()/2;      
        
        leftIndices = indices.subList(0, mid);
        rightIndices = indices.subList(mid, indices.size());
        
      
        
        leftIndices = mergeSortIndices(values, leftIndices);
        rightIndices = mergeSortIndices(values, rightIndices);
        
        return mergeIndices(values, leftIndices, rightIndices);
    }
    
    /**
     * Merging split list as defined by Merge Sort alogrithm
     * 
     * @param left - left list
     * @param right - right list
     * @return 
     */
    private List<Integer> mergeIndices(List<Float> values, List<Integer> leftIndices, List<Integer> rightIndices){
        List<Integer> mergedListIndices = new ArrayList<>(leftIndices.size() + rightIndices.size());
        
        int fromLeft = 0;
        int fromRight = 0;
      
        while(fromLeft < leftIndices.size() || fromRight < rightIndices.size()){
           if(fromLeft < leftIndices.size() && fromRight < rightIndices.size()){
               if(values.get(leftIndices.get(fromLeft)) < values.get(rightIndices.get(fromRight))){
                   mergedListIndices.add(leftIndices.get(fromLeft));
                   fromLeft++;
               }else{
                   mergedListIndices.add(rightIndices.get(fromRight));
                   fromRight++;
               }
           }else if(fromLeft < leftIndices.size()){
               mergedListIndices.add(leftIndices.get(fromLeft));
               fromLeft++;
           }else if(fromRight < rightIndices.size()){
               mergedListIndices.add(rightIndices.get(fromRight));
               fromRight++;
           }
        }
       
        return mergedListIndices;
    }
    
    public List<Float> sortListFromIndices(List<Float> values, List<Integer> indices){
        List<Float> sorted = new ArrayList<>(values.size());
        
        for(Integer i : indices){
            sorted.add(values.get(i));
        }
        
        return sorted;
    }
    
    
}
