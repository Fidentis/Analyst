package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.bounding.bvh.LeafNode;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public abstract class AbstractFace extends AbstractDcelPart {

    private HalfEdgeId outerComponent;
    private final Integer index;
    private final int hash;
    private final Faces faceOwner;
    private LeafNode boundingNode;
    private final MeshFace meshFace;

    protected AbstractFace(final Faces faces, final HalfEdgeId incidentEdge, MeshFace meshFace) {
        super(faces.getOwner());
        this.meshFace = meshFace;
        faceOwner = faces;
        index = faces.reserveFaceId();
        outerComponent = incidentEdge;
        hash = HASH_STEP * super.hashCode() + index;
    }

    /**
     *
     * @param lastHit
     * @param direction
     * @return
     */
    public abstract AbstractIntersection project(Vertex lastHit, Vector3 direction);

    public abstract AbstractIntersection projectDisplacment(Coordinates original, Vector3 displacment);

    public abstract boolean isOuterFace();

    public abstract AbstractIntersection getIntersection(Ray ray);

    /**
     *
     * @return
     */
    public Integer getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     *
     * @return
     */
    protected boolean hasIncidentHalfEdgeId() {
        return outerComponent != null;
    }

    /**
     *
     * @return
     */
    public HalfEdgeId getIncidentHalfEdgeId() {
        return outerComponent;
    }

    /**
     *
     * @return
     */
    public HalfEdge getIncidentHalfEdge() {
        return getOwnersHalfEdge(outerComponent);
    }

    /**
     *
     * @param halfEdge
     */
    public void setIncidentEdge(HalfEdge halfEdge) {
        outerComponent = halfEdge.getId();
    }

    /**
     *
     * @param halfEdge
     */
    public void setIncidentEdge(HalfEdgeId halfEdge) {
        outerComponent = halfEdge;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb);
        return sb.toString();
    }

    /**
     *
     * @param sb
     */
    protected void appendTo(StringBuilder sb) {
        sb.append(index);
        sb.append(':');
        if (!hasIncidentHalfEdgeId()) {
            sb.append('-');
            return;
        }
        for (HalfEdge halfEdge : incidentEdges()) {
            halfEdge.getId().appendOrigin(sb);
            sb.append(',');
        }
    }

    /**
     * Iterable loop of incident half edges of face that will reflect concurrent
     * changes in topology.
     *
     * @return
     */
    public Iterable<HalfEdge> incidentEdges() {
        return getOwnersHalfEdge(outerComponent).getLoop();
    }

    /**
     * Iterable loop of incident half edges of face that will reflect concurrent
     * changes in topology.
     *
     * @return
     */
    public Iterable<Vertex> incidentVertices() {
        return getOwnersHalfEdge(outerComponent).getVertexLoop();
    }

    /**
     * Create new collection of all incident edges of the face that won't
     * reflect further changes of topology.
     *
     * @return Current incident edges.
     */
    Collection<HalfEdge> getBorders() {
        LinkedList<HalfEdge> borders = new LinkedList<>();
        for (HalfEdge halfEdge : incidentEdges()) {
            borders.add(halfEdge);
        }
        return borders;
    }

    /**
     * It makes sure that all incident edges point back to the face
     */
    void corretctEdgeToFaceMapping() {
        for (HalfEdge halfEdge : incidentEdges()) {
            halfEdge.changeIncidentFace(this);
        }
    }

    /**
     * Replace the face in DCEL by triangle fan of faces with common vertex at
     * hitPosition and the edges of faces become outer edges of fan.
     *
     * @param hitPosition
     * @return
     */
    @Override
    public final Vertex split(Coordinates hitPosition) {
        FaceSplitter splitter = new FaceSplitter(hitPosition, this);
        return splitter.splitIt();
    }

    /**
     *
     */
    protected void justRemove() {
        getDCEL().remove(this);
    }

    /**
     *
     */
    @Override
    public void removeFromDcel() {
        for (HalfEdge halfEdge : incidentEdges()) {
            halfEdge.changeIncidentFace(faceOwner.getOuterFace());
        }
        justRemove();
    }

    /**
     *
     * @return
     */
    @Override
    public Vector3 getNormal() {
        return getVertcial().normalized();
    }

    /**
     *
     * @return
     */
    public Vector3 getVertcial() {
        HalfEdge halfEdge = getOwnersHalfEdge(outerComponent);
        HalfEdge previous = halfEdge.getPrevious();
        return previous.vector().getOposite().crossProduct(halfEdge.vector());
    }

    @Override
    public boolean equals(Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof AbstractFace)) {
            return false;
        }
        final AbstractFace other = (AbstractFace) obj;

        return this.index.equals(other.index);
    }

    /**
     *
     * @param direction
     * @return
     */
    public Vector3 projectOn(Vector3 direction) {
        Vector3 normal = getNormal();
        double cosAngle = normal.cosineOfAngel(direction);
        double displacmentLength = direction.getLength();
        Vector3 scaledNormal = normal.scaleUp(cosAngle * displacmentLength);
        Vector3 projected = Vector3.subtract(direction, scaledNormal);
        return projected;
    }

    public void unSetForChecking() {
        for (HalfEdge halfEdge : incidentEdges()) {
            halfEdge.getBegining().setFaceCollorDefault();
        }
    }

    public void setBoundings(LeafNode boundingNode) {
        this.boundingNode = boundingNode;
    }

    @Override
    public Iterable<AbstractFace> getNeighboringFaces() {
        LinkedList<AbstractFace> neighbors = new LinkedList<>();
        neighbors.add(this);
        return neighbors;
    }

    public MeshFace getMeshFace() {
        return meshFace;
    }

    @Override
    public Vertex createAproximatedVertexAt(Coordinates point) {
        ApproximationOnFace approximation = new ApproximationOnFace(this);
        return approximation.createApproximatedVertex(point);
    }

    @Override
    public Collection<Coordinates> getRelatedPositions() {
        ArrayList<Coordinates> coordinates = new ArrayList<>(3);
        for (Vertex incidentVertice : incidentVertices()) {
            coordinates.add(incidentVertice.position());
        }
        return coordinates;
    }

    boolean haveTextures() {
        for (Vertex vertexLoop : getIncidentHalfEdge().getVertexLoop()) {
            if (!vertexLoop.haveTexturesFor(meshFace)) {
                return false;
            }
        }
        return true;
    }

}
