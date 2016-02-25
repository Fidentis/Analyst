/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import cz.fidentis.featurepoints.FacialPoint;
import java.util.List;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsPair {
    private final List<FacialPoint> mainFps;
    private final List<FacialPoint> secondaryFps;

    public FpResultsPair(List<FacialPoint> mainFps, List<FacialPoint> secondaryFps) {
        this.mainFps = mainFps;
        this.secondaryFps = secondaryFps;
    }

    public List<FacialPoint> getMainFps() {
        return mainFps;
    }

    public List<FacialPoint> getSecondaryFps() {
        return secondaryFps;
    }
    
}
