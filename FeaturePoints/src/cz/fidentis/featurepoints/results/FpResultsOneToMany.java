/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsOneToMany {
    private final List<FacialPoint> mainFfps;
    private final HashMap<String ,List<FacialPoint>> facialPoints;

    public FpResultsOneToMany(List<FacialPoint> mainFfps, HashMap<String, List<FacialPoint>> facialPoints) {
        this.mainFfps = mainFfps;
        this.facialPoints = facialPoints;
    }

    public List<FacialPoint> getMainFfps() {
        return mainFfps;
    }

    public HashMap<String, List<FacialPoint>> getFacialPoints() {
        return facialPoints;
    }
}
