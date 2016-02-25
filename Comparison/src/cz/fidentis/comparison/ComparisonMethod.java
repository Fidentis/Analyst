/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison;

/**
 *
 * @author Katka
 */

public enum ComparisonMethod {
    HAUSDORFF_DIST ("Nearest Neighbor Distance") ,
    PROCRUSTES ("Procrustes Analysis"),
    HAUSDORFF_CURV ("Nearest Neighbor Curvature");
   
    private final String value;
    
    ComparisonMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
