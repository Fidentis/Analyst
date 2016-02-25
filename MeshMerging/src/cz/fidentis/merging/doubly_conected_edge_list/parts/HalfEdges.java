/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.HashMap;

/**
 *
 * @author xlobodas
 */
public class HalfEdges{

    private final HashMap<HalfEdgeId, HalfEdge> halfEdges = new HashMap<>();
    private final AbstractDcel halfEdgeOwner;

    HalfEdges(AbstractDcel owner) {
        halfEdgeOwner = owner;
    }

    boolean contains(HalfEdgeId id) {
        return halfEdges.containsKey(id);
    }

    void removeEdgeOf(HalfEdgeId id) {
        halfEdges.remove(id);
        halfEdges.remove(id.getIdOfTwin());
    }

    HalfEdge get(HalfEdgeId halfEdgeId) {
        return halfEdges.get(halfEdgeId);
    }

    void addEdgeFor(HalfEdge halfEdge) {
        halfEdges.put(halfEdge.getId(), halfEdge);
        halfEdges.put(halfEdge.getTwinIndex(), halfEdge.getTwin());
    }

    HalfEdge getFirstOuter(){
        for (HalfEdge halfEdge : halfEdges.values()) {
            if (halfEdge.isOuter()) {
                return halfEdge;
            }
        }
        return null;
    }

    AbstractDcel getOwner() {
        return halfEdgeOwner;
    }
    
    public void appentTo(StringBuilder sb) {
        for (HalfEdge value : halfEdges.values()) {
            value.appendTo(sb);
            sb.append( System.lineSeparator());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appentTo(sb);
        return sb.toString();
    }
    
    

}
