/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import cz.fidentis.featurepoints.FacialPoint;
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
    private final Map<String, List<FacialPoint>> fps;

    public ExportFPButtonObserver(JButton exportFpButton, Map<String, List<FacialPoint>> fps) {
        this.exportFpButton = exportFpButton;
        this.fps = fps;
    }
    
    public ExportFPButtonObserver(JButton exportFpButton, List<FacialPoint> mainFp, String mainName,
            List<FacialPoint> secondaryFp, String secondaryName) {
        this.exportFpButton = exportFpButton;
        
        Map<String, List<FacialPoint>> fp = new HashMap<>();
        fp.put(mainName, mainFp);
        fp.put(secondaryName, secondaryFp);
        
        this.fps = fp;
    }    
    
    @Override
    public void update() {        
        //there has to be at least one point on single face loaded
        boolean exportFP = false;
        
        for(List<FacialPoint> fp : fps.values()){
            if(fp != null && !fp.isEmpty()){
                exportFP = true;
                break;
            }
        }
        
        exportFpButton.setEnabled(exportFP);
    } 
}
