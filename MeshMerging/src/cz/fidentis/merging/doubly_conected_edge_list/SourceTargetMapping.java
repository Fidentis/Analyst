package cz.fidentis.merging.doubly_conected_edge_list;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh_cutting.snake.SnakeInMesh;
import cz.fidentis.merging.mesh_cutting.snake.SnaxelInMesh;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class SourceTargetMapping {

    private final Set<HalfEdgeId> borderOfProjection = new HashSet<>();
    private final Set<Vertex> visitedVertecies = new HashSet<>();
    private final Set<Vertex> hits = new HashSet<>();
    private final HashMap<Vertex, Vertex> vertexMapping = new HashMap<>();
    private final HashMap<HalfEdgeId, HalfEdgeId> edgeMapping = new HashMap<>();
    private final HashMap<AbstractFace, AbstractFace> faceMapping = new HashMap<>();
    private final LinkedList<Coordinates> targetBoundery = new LinkedList<>();
    private final LinkedList<Coordinates> sourceBoundery = new LinkedList<>();
    private final AbstractDcel target;
    private final AbstractDcel source;

    public SourceTargetMapping(SnakeInMesh snake) {
        source = snake.getSource();
        target = snake.getTarget();
        for (SnaxelInMesh snaxel : snake) {
            addMapping(snaxel.getSourceVertex(), snaxel.getTargetVertex());
        }
    }

    /**
     *
     * @return
     */
    public Set<Vertex> hitted() {
        return Collections.unmodifiableSet(hits);
    }

    /**
     *
     * @param from
     * @param to
     */
    public void addBorder(Vertex from, Vertex to) {
        if (from.equals(to)) {
            return;
        }
        HalfEdgeId reverted = new HalfEdgeId(to, from);
        borderOfProjection.add(reverted);
    }

    /**
     *
     * @param sourceVertex
     * @param targetVertex
     */
    public void addMapping(Vertex sourceVertex, Vertex targetVertex) {
        if (visitedVertecies.add(sourceVertex)) {
            hits.add(targetVertex);
            vertexMapping.put(sourceVertex, targetVertex);
            targetBoundery.addLast(targetVertex.position());
            sourceBoundery.addLast(sourceVertex.position());
        }
    }

    /**
     *
     * @param first
     * @return
     */
    public Vertex getTarget(Vertex first) {
        return vertexMapping.get(first);
    }

    /**
     *
     * @param sourceList
     * @return
     */
    public List<Integer> getTarget(List<Vertex> sourceList) {
        List<Integer> result = new LinkedList<>();
        for (Vertex source : sourceList) {
            result.add(vertexMapping.get(source).getIndex());
        }
        return result;
    }

    /**
     *
     * @param source
     * @return
     */
    public AbstractFace getTarget(AbstractFace source) {
        return faceMapping.get(source);
    }

    /**
     *
     * @param source
     * @return
     */
    public AbstractFace getTargetsFace(HalfEdge source) {
        return faceMapping.get(source.getIncidentFace());
    }

    /**
     *
     * @param vertexOfSource
     * @return
     */
    public boolean isMapped(Vertex vertexOfSource) {
        return visitedVertecies.contains(vertexOfSource);
    }

    /**
     *
     * @param incident
     * @return
     */
    public HalfEdgeId getTarget(HalfEdge incident) {
        Vertex targetOrigin = vertexMapping.get(incident.getBegining());
        Vertex targetEnd = vertexMapping.get(incident.getEnd());
        return new HalfEdgeId(targetOrigin, targetEnd);
    }

    /**
     *
     * @param source
     * @param traget
     */
    public void add(AbstractFace source, AbstractFace traget) {
        faceMapping.put(source, traget);
    }

    /**
     *
     * @param owner
     * @return
     */
    public Set<HalfEdge> getBorders(AbstractDcel owner) {
        HashSet<HalfEdge> borders = new HashSet<>();
        for (HalfEdgeId halfEdgeId : borderOfProjection) {
            HalfEdge halfEdge = owner.getHalfEdge(halfEdgeId);
            borders.add(halfEdge);
        }
        return borders;
    }

    /**
     *
     * @param originalHalfEdge
     * @param newEdge
     */
    public void add(HalfEdge originalHalfEdge, HalfEdge newEdge) {
        edgeMapping.put(originalHalfEdge.getId(), originalHalfEdge.getId());
    }

    /**
     *
     * @return
     */
    public Boundary getSourceBoundary() {
        return new Boundary(sourceBoundery);
    }

    /**
     *
     * @return
     */
    public Boundary getTargetBoundary() {
        return new Boundary(targetBoundery);
    }

    /**
     *
     * @return
     */
    public Boundary getDiffBoundary() {
        ArrayList<Coordinates> differenceBoundary;
        differenceBoundary = new ArrayList<>(targetBoundery.size());
        for (int i = 0; i < targetBoundery.size(); i++) {
            differenceBoundary.add(i, getDifference(i));
        }
        return new Boundary(differenceBoundary);
    }

    private Coordinates getDifference(int i) {
        return targetBoundery.get(i).substract(sourceBoundery.get(i));
    }

    public void allignMapped() {
        source.allignAccordingMapping(this);
    }

    public void sewTogether() {
        target.append(source, this);
    }

}
