/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import cz.fidentis.featurepoints.FacialPoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xferkova
 */
public class CNNDetectionResult {
    private List<FacialPoint> modelLandmarks;
    private final List<FacialPoint> textureLandmarks;

    public CNNDetectionResult() {
        modelLandmarks = new ArrayList<>();
        textureLandmarks = new ArrayList<>();
    }

    public CNNDetectionResult(List<FacialPoint> modelLandmarks) {
        this.modelLandmarks = modelLandmarks;
        this.textureLandmarks = new ArrayList<>();
    }
    
    
    public CNNDetectionResult(List<FacialPoint> modelLandmarks, List<FacialPoint> textureLandmarks) {
        this.modelLandmarks = modelLandmarks;
        this.textureLandmarks = textureLandmarks;
    }

    public List<FacialPoint> getModelLandmarks() {
        return modelLandmarks;
    }

    public List<FacialPoint> getTextureLandmarks() {
        return textureLandmarks;
    }

    public void setModelLandmarks(List<FacialPoint> modelLandmarks) {
        this.modelLandmarks = modelLandmarks;
    }
}
