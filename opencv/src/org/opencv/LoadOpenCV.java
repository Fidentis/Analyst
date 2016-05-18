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

    private static void loadOpenCV(String url) throws URISyntaxException {
        
        //System.load(root + "opencv_java310.dll");
        
        System.load(url);
    }
    
    public static void loadWindowsLib() throws URISyntaxException{
        String root = getRoot();
        
        loadOpenCV(root + "opencv_java310.dll");
    }
    
    public static void loadOSXlib() throws URISyntaxException{
        String root = getRoot();
        
        loadOpenCV(root + "mac" + File.separator + "libopencv_java300.dylib");
    }
    
    private static String getRoot() throws URISyntaxException{
        File f = new File(LoadOpenCV.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String root = f.getParent() + File.separator + "lib" + File.separator;
        
        return root;
    }
    
}
