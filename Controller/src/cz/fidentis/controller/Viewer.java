/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import cz.fidentis.model.Model;

/**
 *
 * @author Katarína Furmanová
 */
public class Viewer {

    private String name;

    private Model model1;
    private Model model2;

    /**
     *
     * @return Model that is displayed.
     */
    public Model getModel1() {
        return model1;
    }
    
    public Model getModel2() {
        return model2;
    }


    /**
     *
     * @return Name of the View.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name of the View.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public void setModel1(Model model) {
        model1 = model;
    }
    
    public void setModel2(Model model) {
        model2 = model;
    }
    
}
