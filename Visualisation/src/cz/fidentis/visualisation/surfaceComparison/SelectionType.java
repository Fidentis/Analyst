/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation.surfaceComparison;

/**
 *
 * @author xfurman
 */
public enum SelectionType {
    ELLIPSE ("Ellipse"),
    RECTANGLE ("Rectangle");
    
   
    private final String value;
    
    SelectionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
