/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
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

    private static StringBuilder unpairedModels = new StringBuilder();
    
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
                    results.put(selectedFiles.get(i).getModelName(), computeDistances(selectedFiles.get(i), selectedFilesSecond.get(j), method));
                    isIn = true;
                }
            }

            //if helpCounter is equal 0 means that is no match for given file
            if (!isIn) {
                unpairedModels.append(selectedFiles.get(i).getModelName()).append(", ");
            }

            isIn = false;
        }

        return results;
    }
    
    //Returns names of models that were unpaired during analysis
    public static String getUnpairedModels(){
        return unpairedModels.toString();
    }
    
    /**
     * Compute distances Euclid or Normalized Root Mean Square Error
     *
     * @param one first element for computation
     * @param two second element
     * @param method what method is used, 0 for euclid, 1 for NRMSE
     * @return list of distances
     */
    public static List<Double> computeDistances(FpModel one, FpModel two,  
        FPAnalysisMethods method) {
        MathUtils math = MathUtils.instance();
        List<Double> results = new ArrayList<>();
        List<FacialPoint> mainPoints = one.getFacialPoints();

        for (int i = 0; i < one.getPointsNumber(); i++) {
            FacialPoint mainPoint = mainPoints.get(i);
            int index = mainPoint.getType();
            
            if(!two.containsPoint(index))
                continue;
            
            FacialPoint secondaryPoint = two.getFacialPoint(index);
            
            switch (method) {
                case EUCLID:
                    results.add(math.distancePoints(mainPoint.getPosition(), secondaryPoint.getPosition()));
                    break;
                case NRMSE:
                    if(!one.containsPoint(FacialPointType.EN_L.ordinal()) || 
                            !one.containsPoint(FacialPointType.EN_R.ordinal())){
                        //TODO: throw error first!
                        return null;
                    }
                        
                    results.add(math.distancePoints(mainPoint.getPosition(), secondaryPoint.getPosition()) / 
                            math.distancePoints(one.getFacialPoint(FacialPointType.EN_L.ordinal()).getPosition(),
                            one.getFacialPoint(FacialPointType.EN_R.ordinal()).getPosition()));
                    break;
                default:
                    break;
            }
        }

        return results;
    }
}
