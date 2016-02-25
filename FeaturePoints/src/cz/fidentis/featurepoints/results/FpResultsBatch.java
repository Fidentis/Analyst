/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.results;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpResultsBatch {
    private final Map<String, List<FacialPoint>> fps;
    private final List<Model> registeredModels;

    public FpResultsBatch(Map<String, List<FacialPoint>> fps, List<Model> registeredModels) {
        this.fps = fps;
        this.registeredModels = registeredModels;
    }

    public Map<String, List<FacialPoint>> getFps() {
        return fps;
    }

    public List<Model> getRegisteredModels() {
        return registeredModels;
    }
       
}
