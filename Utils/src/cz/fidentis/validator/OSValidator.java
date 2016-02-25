/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.validator;

/**
 * Class validating which OS is user using.
 * 
 * @author Zuzana Ferkova
 */
public class OSValidator {
    
    private static String os = System.getProperty("os.name").toLowerCase();
    
    public static boolean isWindows(){
        return (os.indexOf("win") >= 0);
    }

    public static boolean isMac(){
        return (os.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0);
    }

    public static boolean isSolaris(){
        return (os.indexOf("sunos") >= 0);
    }
}
