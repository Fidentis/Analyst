/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.observer;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author xferkova
 */
public class ProgressHandleMaster {
    List<Observable> observers;

    public ProgressHandleMaster() {
        observers = new LinkedList<>();
    }
    
    public void addObserver(Observable o){
        observers.add(o);
    }
    
    public void setObservers(List<Observable> os){
        this.observers = os;
    }
    
    public void updateObservers(){
        for(Observable o : observers){
            o.update();
        }
    }   
    
}
