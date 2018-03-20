/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.importfromimage;

/**
 *
 * @author Marek Zuzi
 */
public enum AgeCategories {
    ALL("All"),
    UNDER20("Under 20"),
    FROM20TO29("20-29"),
    FROM30TO39("30-39"),
    FROM40TO49("40-49"),
    ABOVE50("Over 50");

    private final String displayName;
    
    private AgeCategories(String s) {
        displayName = s;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
