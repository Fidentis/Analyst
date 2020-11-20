/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import cz.fidentis.featurepoints.FacialPoint;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsOneToMany {
    private final HashMap<String ,List<FacialPoint>> facialPoints;

    public FpResultsOneToMany(HashMap<String, List<FacialPoint>> facialPoints) {
        this.facialPoints = facialPoints;
    }

    public HashMap<String, List<FacialPoint>> getFacialPoints() {
        return facialPoints;
    }
}
