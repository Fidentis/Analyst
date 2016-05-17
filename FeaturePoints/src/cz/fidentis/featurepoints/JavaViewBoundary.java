/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints;

import java.util.HashSet;
import java.util.Set;
import jv.geom.PgElementSet;
import jv.vecmath.PiVector;

/**
 *
 * @author Galvanizze
 */
public class JavaViewBoundary {

    private PgElementSet elementSet;
    private Set<SimpleEdge> boundaryEdges;
    private Set<Integer> boundaryVertices;

    public JavaViewBoundary(PgElementSet elementSet) {
        this.elementSet = elementSet;
        boundaryEdges = new HashSet<>();
        boundaryVertices = new HashSet<>();
    }

    private void setBoundaryEdges() {
        for (int i = 0; i < elementSet.getNumElements(); i++) {
            setBoundaryEdgesForElement(elementSet.getElement(i));
        }
    }

    private void setBoundaryEdgesForElement(PiVector elem) {
        int k;
        SimpleEdge edge;
        for (int j = 0; j < 3; j++) {
            if (j < 2) {
                k = j + 1;
            } else {
                k = 0;
            }

            edge = new SimpleEdge(elem.getEntry(j), elem.getEntry(k));

            if (!boundaryEdges.add(edge)) {
                boundaryEdges.remove(edge);
            }
        }

    }

    private void setBoundaryVertices() {
        for (SimpleEdge edge : boundaryEdges) {
            boundaryVertices.add(edge.getFirstPoint());
            boundaryVertices.add(edge.getSecondPoint());
        }
    }

    public Set<SimpleEdge> getBoundaryEdges() {
        setBoundaryEdges();
        return boundaryEdges;
    }

    public Set<Integer> getBoundaryVertices() {
        setBoundaryVertices();
        return boundaryVertices;
    }

}
