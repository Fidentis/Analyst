/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

/**
 *
 * @author Katka
 */
public class Ageing {
    private String name = new String();

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
     @Override
    public String toString() {
        return name;
    }
}
