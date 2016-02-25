/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import java.io.File;
import java.util.List;

/**
 *
 * @author Katka
 */
public class Comparison {
    private String name = new String();
    private String modelPath;
    private Model model;
    private HDpainting HDP;
    private String decription;
    private List<Float> hd;

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public HDpainting getHDP() {
        return HDP;
    }

    public void setHDP(HDpainting HDP) {
        this.HDP = HDP;
    }

    public List<Float> getHd() {
        return hd;
    }

    public void setHd(List<Float> hd) {
        this.hd = hd;
    }

    
    
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    
    
    
     public String getModelPath() {
        return modelPath;
    }

    /**
     *
     * @param modelPath
     */
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(new File(modelPath),true,true);
       // model.centralize();
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
     @Override
    public String toString() {
        return name;
    }
}
