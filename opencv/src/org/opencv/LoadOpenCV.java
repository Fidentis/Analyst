/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opencv;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 *
 * @author xferkova
 */
public class LoadOpenCV {
    
    public static boolean openCVLoaded = false;
    
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
        ProtectionDomain a = LoadOpenCV.class.getProtectionDomain();
        CodeSource b = a.getCodeSource();
        URL c = b.getLocation();
        URI d = c.toURI();
        String s = d.toString();
        
        if(!s.startsWith("/")){
            s = "/" + s.split("/", 2)[1];
        }
        
        /*String s;
        s = .getCodeSource().getLocation().toURI().getPath();*/
        File f = new File(s);
        String root = f.getAbsoluteFile().getParent() + File.separator + "lib" + File.separator;
        
        return root;
    }
    
}
