/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.landmarks.FPAnalysisMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rasto1
 */
public class LandmarkUtils {

    private static StringBuilder notIn = new StringBuilder();
    
    /**
     * Compute analysis results
     *
     * @param method what method is used, true for euclid, false for NRMSE
     * @return string of files which have not match
     */
    public static Map<String, List<Double>> computeFp(FPAnalysisMethods method, List<FpModel> selectedFiles, List<FpModel> selectedFilesSecond) {
        boolean isIn = false;
        Map<String, List<Double>> results = new HashMap<>();
        
        
        //try to find match, if there is not write to the given text field
        for (int i = 0; i < selectedFiles.size(); i++) {
            for (int j = 0; j < selectedFilesSecond.size(); j++) {
                if (selectedFiles.get(i).getModelName().equals(selectedFilesSecond.get(j).getModelName())) {
                    results.put(selectedFiles.get(i).getModelName(), computeDistances(selectedFiles.get(i).getFacialPoints(), selectedFilesSecond.get(j).getFacialPoints(), method));
                    isIn = true;
                }
            }

            //if helpCounter is equal 0 means that is no match for given file
            if (!isIn) {
                notIn.append(selectedFiles.get(i).getModelName()).append(", ");
            }

            isIn = false;
        }

        return results;
    }
    
    public static String getNotIn(){
        return notIn.toString();
    }
    
    /**
     * Compute distances Euclid or Normalized Root Mean Square Error
     *
     * @param one first element for computation
     * @param two second element
     * @param method what method is used, 0 for euclid, 1 for NRMSE
     * @return list of distances
     */
    public static List<Double> computeDistances(List<FacialPoint> one, List<FacialPoint> two,  
        FPAnalysisMethods method) {
        MathUtils math = MathUtils.instance();
        List<Double> results = new ArrayList<>();

        for (int i = 0; i < one.size(); i++) {
            switch (method) {
                case EUCLID:
                    results.add(math.distancePoints(one.get(i).getPosition(), two.get(i).getPosition()));
                    break;
                case NRMSE:
                    results.add(math.distancePoints(one.get(i).getPosition(), two.get(i).getPosition()) / math.distancePoints(one.get(3).getPosition(), one.get(4).getPosition()));
                    break;
                default:
                    break;
            }
        }

        return results;
    }
}
