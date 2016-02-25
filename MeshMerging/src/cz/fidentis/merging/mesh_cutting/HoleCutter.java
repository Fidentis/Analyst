/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.mesh_cutting.snake.SnakeInMesh;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author xlobodas
 */
public class HoleCutter {

    private final AbstractDcel target;
    private final Deque<AbstractFace> uncheckedFaces = new LinkedList<>();
    private final Set<HalfEdge> edgesToRemove = new HashSet<>();
    private final Set<AbstractFace> facesToRemove = new HashSet<>();
    private final Set<AbstractFace> visitedFaces = new HashSet<>();
    private Set<HalfEdge> borders;

    public HoleCutter(AbstractDcel toCut) {
        target = toCut;
    }

    public synchronized void cut(SnakeInMesh mapping) {
        getBorders(mapping);
        getBorderFaces();
        findAllFacesToRemove();
        removeFaces();
        removeEdges();
        assignHole();
    }

    private void assignHole() {
        AbstractFace outerFace = target.getOuterFace();
        outerFace.setIncidentEdge(borders.iterator().next());
        for (HalfEdge border : borders) {
            border.changeIncidentFace(outerFace);
        }
    }

    private Set<HalfEdge> getBorders(SnakeInMesh mapping) throws IllegalArgumentException {
        borders = mapping.getBorders();
        if (borders.size() < 3) {
            throw new IllegalArgumentException("Border is less than tree edges");
        }
        return borders;
    }

    private void removeEdges() {
        for (HalfEdge halfEdge : edgesToRemove) {
            halfEdge.removeFromDcelAndReconect();
        }
    }

    private void findAllFacesToRemove() {
        while (!uncheckedFaces.isEmpty()) {
            AbstractFace faceToRemove = uncheckedFaces.removeFirst();
            for (HalfEdge halfEdge : faceToRemove.incidentEdges()) {
                checkEdge(halfEdge);
            }
            facesToRemove.add(faceToRemove);
        }
    }

    private void checkEdge(HalfEdge halfEdge) {
        if (borders.contains(halfEdge)) {
            return;
        }
        addToRemove(halfEdge);
        addToCheck(halfEdge.getTwinsFace());
    }

    private void addToCheck(AbstractFace currentFace) {
        if (visitedFaces.add(currentFace)) {
            uncheckedFaces.add(currentFace);
        }
    }

    private void addToRemove(HalfEdge halfEdge) {
        if (shouldRemove(halfEdge)) {
            edgesToRemove.add(halfEdge);
        }
    }

    private boolean shouldRemove(HalfEdge halfEdge) {
        return !edgesToRemove.contains(halfEdge)
                && !edgesToRemove.contains(halfEdge.getTwin());
    }

    private void getBorderFaces() {
        for (HalfEdge halfEdge : borders) {
            AbstractFace innerFace = halfEdge.getIncidentFace();
            addToCheck(innerFace);
        }
    }

    private void removeFaces() {
        for (AbstractFace faceToRemove : facesToRemove) {
            faceToRemove.removeFromDcel();
        }
    }

}
