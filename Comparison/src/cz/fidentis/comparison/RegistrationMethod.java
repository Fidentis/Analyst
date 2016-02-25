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

public enum RegistrationMethod {
    PROCRUSTES ("Feature Points (GPA)"),
    HAUSDORFF ("Surface (ICP)") ,
    NO_REGISTRATION("No registration");
    
   
    private final String value;
    
    RegistrationMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
