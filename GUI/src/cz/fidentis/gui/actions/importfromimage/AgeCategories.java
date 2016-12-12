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
    //UNDER_18("Under 18"),
    //FROM_18_TO_29("18-29"),
    //FROM_30_TO_49("30-49"),
    //OVER_50("Over 50");
    CHILD("Child"),
    ADULT("Adult");

    private final String displayName;
    
    private AgeCategories(String s) {
        displayName = s;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
