/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation;

/**
 *
 * @author xfurman
 */
public enum ColorScheme {    
    GREEN_BLUE ("Sequential"),
    DIVERGING ("Diverging"),
    RAINBOW ("Rainbow") ;
   
    private final String value;
    
    ColorScheme(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }  
    

}

