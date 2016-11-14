/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class CompareUtils {
    private static CompareUtils instance;
    
    private final static int X_AXIS = 0;
    private final static int Y_AXIS = 1;
    private final static int Z_AXIS = 2;

    private CompareUtils() {
    }
    
    public static CompareUtils instance(){
        if(instance == null)
            instance = new CompareUtils();
        
        return instance;
    }
    
     /**
     * Compare two points based on the level of their nodes.
     * 
     * @param p1 - first point to compare
     * @param p2 - second point to compare
     * @param level - axis based on which comparison will be performed
     * @return - true if p1 value of axis level is smaller or equal to that of p2, false otherwise
     */
    public boolean comparePointsOnLevel(Vector3f p1, Vector3f p2, int level){
        if(level % 3 == X_AXIS){
            return p1.getX() <= p2.getX();
        }else if(level % 3 == Y_AXIS){
            return p1.getY() <= p2.getY();
        }else if(level % 3 == Z_AXIS){
            return p1.getZ() <= p2.getZ();
        }
        
        return false;
    } 
}
