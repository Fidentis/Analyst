/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.comparison.procrustes;

/**
 *
 * @author Zuzana Ferkova
 */
public class ProcrustesProcessing {
    private ProcrustesProcessing instance;
    
    public ProcrustesProcessing instance(){
        if(instance == null){
            instance = new ProcrustesProcessing();
        }
        
        return instance;
    }
    
    private ProcrustesProcessing(){}
}
