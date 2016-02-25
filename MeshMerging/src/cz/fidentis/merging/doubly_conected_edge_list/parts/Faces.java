package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.MeshFace;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author xlobodas
 * @param <F>
 */
abstract class Faces<F extends AbstractFace> {

    private final HashSet<F> faces = new HashSet<>();
    private final AbstractDcel faceOwner;
    private final FaceCreator<F> creator;
    private int faceCounter = -1;
    private final OuterFace outerFace;

    Faces(AbstractDcel owner) {
        faceOwner = owner;
        creator = new FaceCreator(owner);
        outerFace = new OuterFace(this, null);
    }

    protected void remove(F face) {
        faces.remove(face);
    }

    F constructFace(MeshFace indices) {
        F face = creator.CreateFaceWithHalfEdges(indices);
        faces.add(face);
        return face;
    }

    final void bind(F face) {
        if (face.isOwned(faceOwner)) {
            faces.add(face);
        }
    }

    Collection<F> withoutOuterFace() {
        HashSet<F> result = new HashSet<>(faces);
        return result;
    }

    int reserveFaceId() {
        return faceCounter++;
    }

    AbstractDcel getOwner() {
        return faceOwner;
    }

    void updateOuterFace(HalfEdges halfEdges) {
        faceOwner.equals(halfEdges.getOwner());
        HalfEdge firstOuter = halfEdges.getFirstOuter();
        if (firstOuter != null) {
            outerFace.setIncidentEdge(firstOuter);
        }
    }

    AbstractFace getOuterFace() {
        return outerFace;
    }

    boolean isOuter(F face) {
        return outerFace.equals(face);
    }

    void appendsTo(StringBuilder sb) {
        for (AbstractFace face : faces) {
            if (face.isOuterFace()) {
                sb.append("Outer Face ");
            }
            face.appendTo(sb);
            sb.append(System.lineSeparator());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendsTo(sb);
        return sb.toString();
    }

    abstract F createFace(HalfEdgeId forHalfEdgeId, MeshFace meshFace);

}
