/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.results.CNNDetectionResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;

/**
 *
 * @author zferkova
 */
public class ExportFPButtonObserver implements Observable{
    private final JButton exportFpButton;
    private final JButton exportTextureFpButton;
    private final Map<String, CNNDetectionResult> fps;

    public ExportFPButtonObserver(JButton exportFpButton, JButton exportTextureFpButton, 
            Map<String, CNNDetectionResult> fps) {
        this.exportFpButton = exportFpButton;
        this.exportTextureFpButton = exportTextureFpButton;
        this.fps = fps;
    }
    
    public ExportFPButtonObserver(JButton exportFpButton, JButton exportTextureFpButton, CNNDetectionResult mainFp, 
            String mainName,
            CNNDetectionResult secondaryFp, String secondaryName) {
        this.exportFpButton = exportFpButton;
        this.exportTextureFpButton = exportTextureFpButton;
        
        Map<String, CNNDetectionResult> fp = new HashMap<>();
        fp.put(mainName, mainFp);
        fp.put(secondaryName, secondaryFp);
        
        this.fps = fp;
    }    
    
    @Override
    public void update() {        
        //there has to be at least one point on single face loaded
        boolean exportFP = false;
        boolean exportTexture = false;
        
        for(CNNDetectionResult fp : fps.values()){
            if(fp != null && fp.getModelLandmarks() != null && 
                    !fp.getModelLandmarks().isEmpty()){
                exportFP = true;
                break;
            }
        }
        
        for(CNNDetectionResult fp : fps.values()){
            if(fp != null && fp.getTextureLandmarks() != null && 
                    !fp.getTextureLandmarks().isEmpty()){
                exportTexture = true;
                break;
            }
        }
        
        exportTextureFpButton.setEnabled(exportTexture);
        exportFpButton.setEnabled(exportFP);
    } 
}
