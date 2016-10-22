/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import cz.fidentis.renderer.ComparisonGLEventListener;
import javax.swing.JButton;

/**
 *
 * @author zferkova
 */
public class ExportFPButtonObserver implements Observable{
    private final JButton exportFpButton;
    private final ComparisonGLEventListener listener2;
    private final ComparisonGLEventListener listener1;

    public ExportFPButtonObserver(JButton exportFpButton, ComparisonGLEventListener listener2, ComparisonGLEventListener listener1) {
        this.exportFpButton = exportFpButton;
        this.listener2 = listener2;
        this.listener1 = listener1;
    }
    
    
    @Override
    public void update() {        
        //refactor GUI and change this
        //sometimes listener2 can be null (batch), in that case second statment should be considered true and check whether FP are in first listener only instead
        boolean exportFP = listener1 != null && !listener1.getFacialPoints().isEmpty() && (listener2 == null || !listener2.getFacialPoints().isEmpty());
        exportFpButton.setEnabled(exportFP);
    } 
}
