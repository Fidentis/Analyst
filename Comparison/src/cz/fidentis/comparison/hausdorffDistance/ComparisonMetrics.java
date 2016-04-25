/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.utils.SortUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zuzana Ferkova
 */
public class ComparisonMetrics {
    private static ComparisonMetrics instance;
    
    private ComparisonMetrics(){}
    
    public static ComparisonMetrics instance(){
        if(instance == null){
            instance = new ComparisonMetrics();
        }
        
        return instance;
    }

    /**
     * Computes Geometric Mean.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return Geometric Mean computed from Hausdorff Distances
     */
    public float geometricMean(List<Float> hDistance, boolean useRelative) {
        float mean = 1;
        float tmp;
        float size = (float) Math.pow(hDistance.size(), -1);
        float k;
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            tmp = (float) Math.pow(k, size);
            mean *= tmp;
        }
        return mean;
    }

    /**
     * Finds maximal value in the given list.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return minimal value of Hausdorff Distance in the list.
     */
    public float findMaxDistance(List<Float> hDistance, boolean useRelative) {
        float max = hDistance.get(0);
        if (!useRelative) {
            max = Math.abs(max);
        }
        float k;
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            if (k > max) {
                max = k;
            }
        }
        return max;
    }

    /**
     * Computes Arithmetic Mean.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return Arithmetic Mean computed from Hausdorff Distances
     */
    public float aritmeticMean(List<Float> hDistance, boolean useRelative) {
        float mean = 0;
        float k;
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            mean += k;
        }
        return mean / hDistance.size();
    }

    /**
     * Returns 75 percentil of given list. It sorts values in
     * list internally (hence original list is not changed) and
     * picks value on 0.75 * n -th index of ordered list (n being size of list).
     *
     * @param hDistance - list to compute 75 percentile from
     * @return 75 percentil computed from given list.
     */
    public float percentileSeventyFive(List<Float> hDistance, boolean useRelative) {
        List<Float> absolute = new ArrayList<Float>(hDistance.size());
        float k;
        if (!useRelative) {
            for (Float f : hDistance) {
                k = f;
                absolute.add(Math.abs(k));
            }
        } else {
            absolute.addAll(hDistance);
        }
        List<Float> result = SortUtils.instance().sortValues(absolute);
        return Math.abs(result.get((int) ((result.size() * 0.75) - 0.5)));
    }

    /**
     * Finds minimal value in the given list.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return minimal value of Hausdorff Distance in the list.
     */
    public float findMinDistance(List<Float> hDistance, boolean useRelative) {
        float min = hDistance.get(0);
        if (!useRelative) {
            min = Math.abs(min);
        }
        float k;
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            if (k < min) {
                min = k;
            }
        }
        return min;
    }

    /**
     * Computes percentage of difference.
     *
     * This method is not fully functional.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return Percentage of difference computed from Hausdorff Distance.
     */
    public float percLikeness(List<Float> hDistance, boolean useRelative) {
        if (findMaxDistance(hDistance, useRelative) == 0) {
            return 0;
        }
        return (aritmeticMean(hDistance, useRelative) / (findMaxDistance(hDistance, useRelative))) * 100;
    }

    /**
     * Computes Root Mean Square.
     *
     * @param hDistance List of computed Hausdorff Distances
     * @return Root Mean Square computed from the Hausdorff Distances
     */
    public float rootMeanSqr(List<Float> hDistance, boolean useRelative) {
        float mean = 0;
        float k;
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            mean = mean + k * k;
        }
        mean = mean * 1 / hDistance.size();
        return (float) Math.sqrt((double) mean);
    }

    /**
     * Computes variance from values in given list of HD values.
     * Does not root the value (hence result is powered to second).
     *
     * @param hDistance - list of values to compute the variance from
     * @return variance computed from given list, as a single float number.
     */
    public float variance(List<Float> hDistance, boolean useRelative) {
        float expectedValue = computeExpectedValue(hDistance, useRelative);
        float mean = 0;
        float k;
        if (expectedValue == 0) {
            return 0.0F;
        }
        for (Float f : hDistance) {
            k = f;
            if (!useRelative) {
                k = Math.abs(k);
            }
            mean += Math.pow(k - expectedValue, 2);
        }
        mean = mean / hDistance.size();
        return mean;
    }

    public float procrustesImmitation(List<Float> values) {
        float dist = 0.0F;
        for (Float f : values) {
            dist += (f * f);
        }
        return (float) Math.sqrt(dist);
    }

    /**
     * Comptes expected value (from probability theory) from given list of floats.
     * Does not offer possibility to adjust probabilities of values, instead
     * gives each value same probability.
     *
     * @param values - list of floats from which the expected value is to be computed, should be the same as the list of values for variance
     * @param useRelative - denotes whether relative values are used or not
     * @return computed expected value
     */
    private float computeExpectedValue(List<Float> values, boolean useRelative) {
        float expectedValue = 0;
        float probability = 1.0F / values.size();
        float num;
        for (Float f : values) {
            num = f;
            if (!useRelative) {
                num = Math.abs(num);
            }
            expectedValue += num * probability;
        }
        return expectedValue;
    }

    /**
     * Returns list of thresholded HD values to be used as numerical results.
     * This way final result, such as Root Mean Square can only be computed from certain percentage
     * of results and not from 100% of vertices of the mesh.
     * Method sorts values in least from smallest to largest and changes ordering in the given list (does NOT create a copy)
     * Does NOT sort values if threshold == 1.0
     * If upperTreshold is <= to lowerTreshold, list containing single value (0.0) is returned.
     *
     * @param values - list containing values to be thresholded
     * @param upperTreshold - percentage of entries to be thresholded from right side, in interval [0..1]
     * @param lowerTreshold - percentage of entries to be thresholded from left side, in interval [0..1]
     * @param useRelative - defines whether relative values were used when computing list or not
     * @return thresholded list
     */
    public List<Float> thresholdValues(List<Float> values, float upperTreshold, float lowerTreshold, boolean useRelative) {
        if (upperTreshold == 1.0 && lowerTreshold == 0.0) {
            return values;
        } else if (upperTreshold <= lowerTreshold) {
            List<Float> empty = new ArrayList<Float>();
            empty.add(0.0F);
            return empty;
        }
        List<Float> sortedValues = new ArrayList<Float>();
        if (!useRelative) {
            for (Float value : values) {
                sortedValues.add(Math.abs(value));
            }
        } else {
            sortedValues.addAll(values);
        }
        
        sortedValues = SortUtils.instance().sortValues(sortedValues);
        int threshIndexMin = (int) ((sortedValues.size() * lowerTreshold) - 0.5);
        int threshIndexMax = (int) ((sortedValues.size() * upperTreshold) - 0.5);
        
        /*if (useRelative) {
            threshIndexMin = (int) ((sortedValues.size() * ((1.0 - upperTreshold) / 2.0)) + 0.5);
            threshIndexMax = (int) ((sortedValues.size() * ((1.0 + upperTreshold) / 2.0)) - 0.5);
        } else {
            threshIndexMin = 0;
            threshIndexMax = (int) ((sortedValues.size() * upperTreshold) - 0.5);
        }*/
        
        
        return sortedValues.subList(threshIndexMin, threshIndexMax);
    }
    
    public List<Float> thresholdValuesKeepSort(List<Float> values, float upperTreshold, float lowerTreshold, boolean useRelative){
        List<Float> sorted = thresholdValues(values, upperTreshold, lowerTreshold, useRelative);
        List<Float> thresholded = new ArrayList<>();
        
        for(Float f: values){
            if(sorted.contains(f)){
                thresholded.add(f);
            }else{
                thresholded.add(null);
            }
        }
        
        return thresholded;
    }
}
