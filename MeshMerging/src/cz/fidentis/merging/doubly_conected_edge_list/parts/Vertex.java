package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.MeshPoint;
import cz.fidentis.merging.mesh.Vector3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Vertex extends AbstractDcelPart {

    private final Integer index;
    private HalfEdge incidentHalfEdge;
    private final int hash;

    protected Vertex(final Vertices vertecies, final Integer index) {
        super(vertecies.getOwner());
        this.index = index;
        hash = HASH_STEP * super.hashCode() + this.index;
    }

    /**
     *
     * @return
     */
    public Integer getIndex() {
        return index;
    }

    /**
     *
     * @return
     */
    public HalfEdge getIncidentHalfEdge() {
        return incidentHalfEdge;
    }

    /**
     *
     * @param halfEdge
     */
    protected void setIncidentEdge(final HalfEdge halfEdge) {
        assert halfEdge != null;
        if (halfEdge.haveOrigin(this)) {
            incidentHalfEdge = halfEdge;
        }
    }

    /**
     *
     * @return
     */
    public Coordinates position() {
        return getDCEL().getVertexPosition(index);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Vertex)) {
            return false;
        }

        return Objects.equals(this.index, ((Vertex) obj).index);
    }

    @Override
    public String toString() {
        return String.format(
                "Vertex of index %d. On %s with incident half ege %s", index,
                position().toString(), incidentHalfEdge == null ? ""
                        : incidentHalfEdge.toString());
    }

    @Override
    public Iterable<AbstractFace> getNeighboringFaces() {
        return getIncidentFaces();
    }

    /**
     *
     * @return
     */
    public Collection<AbstractFace> getIncidentFaces() {
        Collection<AbstractFace> incidentFaces = new LinkedList<>();
        HalfEdge first = incidentHalfEdge;
        incidentFaces.add(first.getIncidentFace());
        HalfEdge current = first.getNextFromOrigin();
        while (!current.equals(first)) {
            incidentFaces.add(current.getIncidentFace());
            current = current.getNextFromOrigin();
        }
        return incidentFaces;
    }

    /**
     *
     */
    @Override
    public void removeFromDcel() {
        for (HalfEdge halfEdge : incidentHalfEdge.withSameOrigin()) {
            halfEdge.getIncidentFace().removeFromDcel();
        }
        for (HalfEdge halfEdge : incidentHalfEdge.withSameOrigin()) {
            halfEdge.removeFromDcel();
        }
        getDCEL().remove(this);
        incidentHalfEdge = null;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector3 getNormal() {
        return getDCEL().getVertexNormal(index);
    }

    /**
     *
     */
    public void recalcNormal() {
        LinkedList<Vector3> norms = new LinkedList<>();
        for (AbstractFace incidentFace : getIncidentFaces()) {
            norms.add(incidentFace.getNormal());
        }
        Vector3 newNormal = Vector3.sumToVector(norms).normalized();
        getDCEL().SetVertexNormal(index, newNormal);
    }

    void appendTo(StringBuilder sb) {
        sb.append(index);
        sb.append(',');
        sb.append(getMeshPoint().toString());
        sb.append(System.lineSeparator());
    }

    /**
     *
     * @param vertex
     * @return
     */
    public boolean haveEdgeTo(Vertex vertex) {
        return getDCEL().haveEdge(this, vertex);
    }

    public MeshPoint getMeshPoint() {
        return getDCEL().getMeshPoint(index);
    }

    public void move(Coordinates movement) {
        getDCEL().moveVertex(index, movement);
    }

    void setFaceCollorDefault() {
        getDCEL().setVertexColorDefault(index);
    }

    public HalfEdge getOwnersHalfEdge(Vertex originVertex, Vertex originVertex0) {
        return getOwnersHalfEdge(new HalfEdgeId(originVertex, originVertex0));
    }

    Coordinates getTextureCoordinates(MeshFace meshFace) {
        return getMeshPoint().getTextureCoordinates(meshFace);
    }

    void createTextureCoordinate(Coordinates texture, MeshFace meshFace) {
        getDCEL().addTextureCoordinates(index, texture, meshFace);
    }

    @Override
    public Vertex createAproximatedVertexAt(Coordinates point) {
        return this;
    }

    @Override
    public Vertex split(Coordinates hitPosition) {
        return this;
    }

    @Override
    public Collection<Coordinates> getRelatedPositions() {
        ArrayList<Coordinates> coordinates = new ArrayList<>(2);
        coordinates.add(position());
        return coordinates;
    }

    boolean haveTexturesFor(MeshFace meshFace) {
        return getMeshPoint().haveTexturesFor(meshFace);
    }

    public boolean haveOnlyTwoOutgoing() {
        return incidentHalfEdge.withSameOrigin().size() < 3;
    }

}
