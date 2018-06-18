/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.landmarks;

import cz.fidentis.processing.featurePoints.PDM;
import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author xferkova
 */
public class PDMList {
    private List<PDM> pdms;
    private static PDMList instance;
    private List<String> pdmNames = new LinkedList<>();
    private boolean addedNewName = false;       //whether new values were added since list of PDM names was shown last time
    
    public static PDMList instance(){
        if(instance == null)
            instance = new PDMList();
        
        return instance;
    }
    

    private PDMList() {
        //load default model into options
        pdms = new LinkedList<>();
        try {
            addPdm(PDM.loadPDM((new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + "trainingModels" + separatorChar + "default.pdm")));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public List<PDM> getPdms() {
        return pdms;
    }
    
    public PDM getPdm(int index){
        if(pdms.size() >= index)
            return null;
        
        return pdms.get(index);
    }
    
    public List<String> getPdmNames(){
        return pdmNames;
    }
    
    public String[] getPdmNamesArray(){
        String[] list = new String[pdmNames.size()];
        list = pdmNames.toArray(list);
        
        addedNewName = false;
        
        return list;
    }
    
    public void addPdm(PDM pdm) {
        //TODO: better checking than just reference?
        if(pdms.contains(pdm))
            return;
        
        this.pdms.add(pdm);
        this.pdmNames.add(pdm.getModelName());
        addedNewName = true;
    }

    public boolean addedNewName() {
        return addedNewName;
    }
    
    
}
