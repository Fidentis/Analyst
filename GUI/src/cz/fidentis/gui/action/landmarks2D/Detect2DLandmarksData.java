/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.action.landmarks2D;

import cz.fidentis.featurepoints.FpModel;
import java.io.File;
import java.util.List;

/**
 *
 * @author xferkova
 */
public class Detect2DLandmarksData {
    private File[] loadedFiles;
    private List<FpModel> detectedLandmarks;

    public Detect2DLandmarksData() {
    }

    public File[] getLoadedFiles() {
        return loadedFiles;
    }

    public void setLoadedFiles(File[] loadedFiles) {
        this.loadedFiles = loadedFiles;
    }

    public List<FpModel> getDetectedLandmarks() {
        return detectedLandmarks;
    }

    public void setDetectedLandmarks(List<FpModel> detectedLandmarks) {
        this.detectedLandmarks = detectedLandmarks;
    }
    
    
    
}
