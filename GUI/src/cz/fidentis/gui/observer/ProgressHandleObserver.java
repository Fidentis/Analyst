/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author xferkova
 */
public class ProgressHandleObserver implements Observable{
    private ProgressHandle p;
    private boolean isStarted;

    public ProgressHandleObserver(String handleMsg) {
        p = ProgressHandleFactory.createSystemHandle(handleMsg);
        isStarted = false;
    }
    
    public void startHandle(){
        p.start();
        isStarted = true;
    }
   
    @Override
    public void update() {
        if(isStarted)
            p.finish();
        
        isStarted = false;
    }
    
}
