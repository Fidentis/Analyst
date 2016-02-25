/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import java.util.ArrayList;

/**
 *
 * @author Katarína Furmanová
 */
public class Controller {
    private static ArrayList<Project> projects = new ArrayList<Project>();

     /**
     * Removes project.
     * @param p project to be removed
     */
    public static void removeProjcet(Project p){
        projects.remove(p);
    }
    
    /**
     * Adds project.
     * @param p project to be added.
     */
    public static void addProjcet(Project p){
        projects.add(p);
    }

    /**
     * 
     * @return List of project opened.
     */
    public static ArrayList<Project> getProjects() {
        return projects;
    }
    
    
    
}
