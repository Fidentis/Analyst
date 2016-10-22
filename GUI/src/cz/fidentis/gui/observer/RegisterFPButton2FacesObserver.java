/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import cz.fidentis.controller.Comparison2Faces;
import javax.swing.JButton;

/**
 *
 * @author xferkova
 */
public class RegisterFPButton2FacesObserver implements Observable{
    private final JButton registerFpButton;
    private final Comparison2Faces info;

    public RegisterFPButton2FacesObserver(JButton registerFpButton, Comparison2Faces info) {
        this.registerFpButton = registerFpButton;
        this.info = info;
    }
    

    @Override
    public void update() {
        registerFpButton.setEnabled(info.getMainFp() != null && info.getSecondaryFp() != null &&
                info.getMainFp().size() >= 3 && info.getSecondaryFp().size() >= 3);
    }
    
}
