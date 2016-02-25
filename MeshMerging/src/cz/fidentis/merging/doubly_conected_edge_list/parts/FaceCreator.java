package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.MeshFace;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Matej Lobodáš
 */
class FaceCreator<F extends AbstractFace> {

    private F face;
    private final Deque<HalfEdge> innerLoop = new LinkedList<>();
    private final Deque<HalfEdge> outerLoop = new LinkedList<>();
    private final AbstractDcel<F> owner;
    private final HashMap<HalfEdgeId, Boolean> isNew = new HashMap<>();

    protected FaceCreator(final AbstractDcel<F> ownerOfFace) {
        assert ownerOfFace != null;
        owner = ownerOfFace;

    }

    public F CreateFaceWithHalfEdges(final MeshFace meshFace) {

        innerLoop.clear();
        outerLoop.clear();

        Integer previous = meshFace.getLastIndex();
        HalfEdgeId firstHalfEdge;
        firstHalfEdge = new HalfEdgeId(previous, meshFace.getFirstIndex());
        face = owner.addFace(firstHalfEdge, meshFace);

        for (Integer vertexIndex : meshFace) {
            createEdgeOfFace(previous, vertexIndex);
            previous = vertexIndex;
        }

        correctEdgeFlow();
        correctIncidentFaces();
        return face;
    }

    private void correctIncidentFaces() {
        for (HalfEdge halfEdge : innerLoop) {
            halfEdge.changeIncidentFace(face);
        }
    }

    private void createEdgeOfFace(int from, int to) {
        HalfEdgeId haldEdgeId = new HalfEdgeId(from, to);
        Boolean isNewEdge = owner.addNewEdge(haldEdgeId, face);
        isNew.put(haldEdgeId.getIdOfTwin(), isNewEdge);
        isNew.put(haldEdgeId, isNewEdge);
        innerLoop.addLast(owner.getHalfEdge(haldEdgeId));
        outerLoop.addFirst(owner.getHalfEdge(haldEdgeId.getIdOfTwin()));
    }

    private void correctEdgeFlow() {
        correctOuterEdgeFlow();
        correctInnerEdgeFlow();
    }

    private void correctOuterEdgeFlow() {
        HalfEdge previus = outerLoop.getLast();
        for (HalfEdge current : outerLoop) {
            boolean isPreviosNew = isNew.get(previus.getId());
            boolean isCurrentNew = isNew.get(current.getId());
            if (isPreviosNew && isCurrentNew) {
                previus.setNext(current);
            } else if (isPreviosNew) {
                previus.setNext(current.getTwin().getNext());
            } else if (isCurrentNew) {
                current.setPrevios(previus.getTwin().getPrevious());
            }
            previus = current;
        }
    }

    private void correctInnerEdgeFlow() {
        HalfEdge previus = innerLoop.getLast();
        for (HalfEdge current : innerLoop) {
            boolean isPreviosNew = isNew.get(previus.getId());
            boolean isCurrentNew = isNew.get(current.getId());
            if (!isPreviosNew && !isCurrentNew
                    && !previus.getNext().equals(current)) {
                current.getPrevious().setNext(previus.getNext());
            }
            current.setPrevios(previus);
            previus = current;
        }
    }

}
