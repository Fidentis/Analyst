package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.doubly_conected_edge_list.SourceTargetMapping;
import cz.fidentis.merging.mesh.MeshPoint;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class DcelConnecter<F extends AbstractFace> {

    private final AbstractDcel<F> target;

    DcelConnecter(AbstractDcel<F> target) {
        this.target = target;
    }

    synchronized void connect(AbstractDcel<F> source, SourceTargetMapping mapping) {
        AddVertices(source, mapping);
        AddFaces(source, mapping);
        AddEdges(source, mapping);
        FixEdgeFlow(source, mapping);
    }

    private void FixEdgeFlow(AbstractDcel<F> source, SourceTargetMapping mapping) {
        for (AbstractFace sourceFace : source.getFaces()) {
            FixEdgeFlowFace(sourceFace, mapping);
        }
    }

    private void FixEdgeFlowFace(AbstractFace sourceFace, SourceTargetMapping mapping) {
        for (HalfEdge halfEdge : sourceFace.incidentEdges()) {
            HalfEdgeId targetHalfEdgeId = mapping.getTarget(halfEdge);
            HalfEdge withoutNext = target.getHalfEdge(targetHalfEdgeId);
            HalfEdgeId halfEdgeIdOfNext = mapping.getTarget(halfEdge.getNext());
            HalfEdge next = target.getHalfEdge(halfEdgeIdOfNext);
            withoutNext.setNext(next);
        }
    }

    private void AddEdges(AbstractDcel<F> source, SourceTargetMapping mapping) {
        for (F sourceFace : source.getFaces()) {
            AddEdges(mapping, sourceFace);
        }
    }

    private void AddEdges(SourceTargetMapping mapping, AbstractFace sourceFace) {
        AbstractFace innerFace = mapping.getTarget(sourceFace);
        for (HalfEdge halfEdge : sourceFace.incidentEdges()) {
            HalfEdgeId newEdgeId = mapping.getTarget(halfEdge);
            AbstractFace outerFace = mapping.getTargetsFace(halfEdge.getTwin());
            target.addNewEdge(newEdgeId, innerFace, outerFace);
        }
    }

    private void AddFaces(AbstractDcel<F> source, SourceTargetMapping mapping) {
        for (F sourceFace : source.getFaces()) {
            HalfEdge sourceIncident = sourceFace.getIncidentHalfEdge();
            HalfEdgeId targetIncident = mapping.getTarget(sourceIncident);
            AbstractFace targetFaceFace;
            targetFaceFace = target.addFace(targetIncident, sourceFace.getMeshFace());
            mapping.add(sourceFace, targetFaceFace);
        }
    }

    private void AddVertices(AbstractDcel<F> source, SourceTargetMapping mapping) {
        for (Vertex sourceVertex : source.getVerecies()) {
            if (mapping.isMapped(sourceVertex)) {
                continue;
            }
            MeshPoint meshPoint = sourceVertex.getMeshPoint();
            Vertex targetVertex = target.createVertex(meshPoint.getPosition(), meshPoint.getNormal());
            mapping.addMapping(sourceVertex, targetVertex);
        }
    }

}
