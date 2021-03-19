/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsPair {
    private final CNNDetectionResult mainFps;
    private final CNNDetectionResult secondaryFps;

    public FpResultsPair(CNNDetectionResult mainFps, CNNDetectionResult secondaryFps) {
        this.mainFps = mainFps;
        this.secondaryFps = secondaryFps;
    }

    public CNNDetectionResult getMainFps() {
        return mainFps;
    }

    public CNNDetectionResult getSecondaryFps() {
        return secondaryFps;
    }
    
}
