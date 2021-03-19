/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import java.util.HashMap;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsOneToMany {
    private final HashMap<String, CNNDetectionResult> facialPoints;

    public FpResultsOneToMany(HashMap<String, CNNDetectionResult> facialPoints) {
        this.facialPoints = facialPoints;
    }

    public HashMap<String, CNNDetectionResult> getFacialPoints() {
        return facialPoints;
    }
}
