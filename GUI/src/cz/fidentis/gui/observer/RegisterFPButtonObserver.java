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
 * Update enable for register button based on number of landmarks for 1:N and Batch
 * 
 * @author xferkova
 */
public class RegisterFPButtonObserver implements Observable{
    private final JButton registerFpButton;
    private final  Map<String, CNNDetectionResult> info;

    public RegisterFPButtonObserver(JButton registerFpButton, Map<String, CNNDetectionResult> info) {
        this.registerFpButton = registerFpButton;
        this.info = info;
    }
    
    public RegisterFPButtonObserver(JButton registerFpButton, CNNDetectionResult mainFp, String mainName, 
            CNNDetectionResult secondaryFp, String secondaryName){
        Map<String, CNNDetectionResult> info = new HashMap<>();
        info.put(mainName, mainFp);
        info.put(secondaryName, secondaryFp);
        
        this.registerFpButton = registerFpButton;
        this.info = info;
    }

    @Override
    public void update() {
        boolean enable = true;
        if (info.keySet().size() < 2) {     //less than 2 faces loaded
            enable = false;
        } else {
            for (CNNDetectionResult fp : info.values()) {
                if (fp == null || fp.getModelLandmarks() == null || 
                        fp.getModelLandmarks().size() < 3) {
                    enable = false;
                    break;
                }
            }
        }

        registerFpButton.setEnabled(enable);
    }

}
