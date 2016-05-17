/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opencv;

import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author xferkova
 */
public class LoadOpenCV {
    private static LoadOpenCV instance;
    
    public static void LoadLibrary() throws URISyntaxException{
        if(instance == null){
            instance = new LoadOpenCV();
        }      
    }
    

    private LoadOpenCV() throws URISyntaxException {
        File f = new File(LoadOpenCV.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.load(f.getParent() + File.separator + "lib" + File.separator + "opencv_java310.dll");
    }
    
}
