/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import cz.fidentis.featurepoints.FacialPoint;
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
    private final  Map<String, List<FacialPoint>> info;

    public RegisterFPButtonObserver(JButton registerFpButton, Map<String, List<FacialPoint>> info) {
        this.registerFpButton = registerFpButton;
        this.info = info;
    }

    @Override
    public void update() {
        boolean enable = true;
        if (info.keySet().size() < 2) {     //less than 2 faces loaded
            enable = false;
        } else {
            for (List<FacialPoint> fp : info.values()) {
                if (fp == null || fp.size() < 3) {
                    enable = false;
                    break;
                }
            }
        }

        registerFpButton.setEnabled(enable);
    }

}
