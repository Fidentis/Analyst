/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison;

/**
 *
 * @author xferkova
 */
public enum ICPmetric {
    VERTEX_TO_VERTEX ("Vertex to vertex"),
    VERTEX_TO_MESH ("Vertex to mesh");
   
    private final String value;
    
    ICPmetric(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
