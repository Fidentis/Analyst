/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import java.util.Map;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsBatch {
    private final Map<String, CNNDetectionResult> fps;

    public FpResultsBatch(Map<String, CNNDetectionResult> fps) {
        this.fps = fps;
    }

    public Map<String, CNNDetectionResult> getFps() {
        return fps;
    }
       
}
