/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller.data;

import java.awt.Color;
import javax.vecmath.Vector3f;

/**
 * Class holding configuration for crosscuts visualization
 * 
 * @author xferkova
 */
public class CrosscutConfig {
    private int crossCutPlaneIndex;
    private Vector3f arbitraryPlanePos;
    private Vector3f planePosition;
    private int crosscutSize;
    private int crosscutThickness;
    private Color crosscutColor;
    private boolean highlightCuts;
    private boolean showVector;
    private boolean allCuts;
    private boolean samplingRays;
    private boolean showPlane;

    public CrosscutConfig() {
    }

    public int getCrossCutPlaneIndex() {
        return crossCutPlaneIndex;
    }

    public void setCrossCutPlaneIndex(int crossCutPlaneIndex) {
        this.crossCutPlaneIndex = crossCutPlaneIndex;
    }

    public Vector3f getArbitraryPlanePos() {
        return arbitraryPlanePos;
    }

    public void setArbitraryPlanePos(float x, float y, float z) {
        this.arbitraryPlanePos.setX(x);
        this.arbitraryPlanePos.setY(y);
        this.arbitraryPlanePos.setZ(z);
    }

    public Vector3f getPlanePosition() {
        return planePosition;
    }

    public void setPlanePosition(float x, float y, float z) {
        this.planePosition.setX(x);
        this.planePosition.setY(y);
        this.planePosition.setZ(z);        
    }

    public int getCrosscutSize() {
        return crosscutSize;
    }

    public void setCrosscutSize(int crosscutSize) {
        this.crosscutSize = crosscutSize;
    }

    public int getCrosscutThickness() {
        return crosscutThickness;
    }

    public void setCrosscutThickness(int crosscutThickness) {
        this.crosscutThickness = crosscutThickness;
    }

    public Color getCrosscutColor() {
        return crosscutColor;
    }

    public void setCrosscutColor(Color crosscutColor) {
        this.crosscutColor = crosscutColor;
    }

    public boolean isHighlightCuts() {
        return highlightCuts;
    }

    public void setHighlightCuts(boolean highlightCuts) {
        this.highlightCuts = highlightCuts;
    }

    public boolean isShowVector() {
        return showVector;
    }

    public void setShowVector(boolean showVector) {
        this.showVector = showVector;
    }

    public boolean isAllCuts() {
        return allCuts;
    }

    public void setAllCuts(boolean allCuts) {
        this.allCuts = allCuts;
    }

    public boolean isSamplingRays() {
        return samplingRays;
    }

    public void setSamplingRays(boolean samplingRays) {
        this.samplingRays = samplingRays;
    }

    public boolean isShowPlane() {
        return showPlane;
    }

    public void setShowPlane(boolean showPlane) {
        this.showPlane = showPlane;
    }
    
    
}
