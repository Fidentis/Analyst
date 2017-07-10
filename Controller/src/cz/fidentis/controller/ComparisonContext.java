/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import java.awt.Color;
import javax.vecmath.Vector3f;

/**
 *
 * @author xfurman
 */
public interface ComparisonContext {
    
    public int getCrossCutPlaneIndex();

    public void setCrossCutPlaneIndex(int crossCutPlaneIndex);

    public Vector3f getArbitraryPlanePos();

    public void setArbitraryPlanePos(float x, float y, float z);

    public Vector3f getPlanePosition();

    public void setPlanePosition(float x, float y, float z);

    public int getCrosscutSize();

    public void setCrosscutSize(int crosscutSize);
    
    public int getCrosscutThickness();

    public void setCrosscutThickness(int crosscutThickness);

    public Color getCrosscutColor();

    public void setCrosscutColor(Color crosscutColor);

    public boolean isHighlightCuts();

    public void setHighlightCuts(boolean highlightCuts);

    public boolean isShowVectors();

    public void setShowVectors(boolean showVectors);

    public boolean isAllCuts();

    public void setAllCuts(boolean allCuts);

    public boolean isSamplingRays();

    public void setSamplingRays(boolean samplingRays);
    
    public boolean isShowPlane();

    public void setShowPlane(boolean showPlane);

    public boolean isShowBoxplot();

    public void setShowBoxplot(boolean showBoxplot);

    public boolean isShowBoxplotFunction();

    public void setShowBoxplotFunction(boolean showBoxplotFunction);
}
