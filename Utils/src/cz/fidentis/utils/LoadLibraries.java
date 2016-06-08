/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import cz.fidentis.validator.OSValidator;
import java.net.URISyntaxException;
import org.opencv.LoadOpenCV;

/**
 *
 * @author xferkova
 */
public class LoadLibraries {
    private static boolean openCVLoaded = false;
    
    
    public static void loadOpenCV() throws URISyntaxException{
        
        if(OSValidator.isWindows()){
            LoadOpenCV.loadWindowsLib();
        }else if(OSValidator.isMac()){
            LoadOpenCV.loadOSXlib();
        }
        
        openCVLoaded = true;
    }
}
