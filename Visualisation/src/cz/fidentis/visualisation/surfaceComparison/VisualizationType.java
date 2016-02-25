/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.surfaceComparison;

/**
 *
 * @author Jakub Palenik
 * 11.03.2015
 * ENUM for HDPainting sets visualization type for rendering
 */
public enum VisualizationType {
    COLORMAP ("Color map") ,
    VECTORS ("Vectors"),
    CROSSSECTION("Cross-sections"),
    TRANSPARENCY("Transparency+Fog");
   
    private final String value;
    
    VisualizationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
