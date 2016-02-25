package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.HittedEdge;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class HalfEdge extends AbstractDcelPart {

    private final Vertex begining;
    private final HalfEdgeId id;
    private HalfEdge twin;
    private HalfEdge next;
    private HalfEdge previous;
    private AbstractFace incidentFace;
    private final int hash;

    /**
     *
     * @param haldEdgeId
     * @param incident
     * @param twinface
     */
    protected HalfEdge(final HalfEdgeId haldEdgeId, final AbstractFace incident, final AbstractFace twinface) {
        this(haldEdgeId, incident);
        createTwin(twinface);
    }

    /**
     *
     * @param haldEdgeId
     * @param incident
     */
    protected HalfEdge(final HalfEdgeId haldEdgeId, final AbstractFace incident) {
        super(incident.getDCEL());

        assert haldEdgeId != null;

        id = haldEdgeId;
        incidentFace = incident;

        begining = getDCEL().getVertex(id.getFromIndex());
        assert begining != null;

        begining.setIncidentEdge(this);
        if (!incidentFace.hasIncidentHalfEdgeId()) {
            incidentFace.setIncidentEdge(this);
        }
        hash = HASH_STEP * super.hashCode() + id.hashCode();
    }

    private HalfEdge createTwin(AbstractFace face) {
        assert twin == null;
        twin = new HalfEdge(id.getIdOfTwin(), face);
        twin.twin = this;
        return twin;
    }

    /**
     *
     * @param face
     */
    public void changeIncidentFace(final AbstractFace face) {
        incidentFace = face;
    }

    /**
     *
     * @return
     */
    public HalfEdgeId getId() {
        return id;
    }

    /**
     *
     * @param nextEdge
     */
    protected void setNext(final HalfEdge nextEdge) {
        if (twin.haveSameOrigin(nextEdge)) {
            next = nextEdge;
            nextEdge.previous = this;
        }

    }

    /**
     *
     * @param previouEdge
     */
    protected void setPrevios(final HalfEdge previouEdge) {
        if (haveSameOrigin(previouEdge.twin)) {
            previous = previouEdge;
            previouEdge.next = this;
        }
    }

    /**
     *
     * @param halfEdge
     * @return
     */
    public boolean haveSameOrigin(final HalfEdge halfEdge) {
        if (halfEdge == null) {
            return false;
        }

        return begining.equals(halfEdge.begining);
    }

    /**
     *
     * @param vertex
     * @return
     */
    public boolean haveOrigin(final Vertex vertex) {
        return begining.equals(vertex);
    }

    /**
     *
     * @param vertex
     * @return
     */
    public boolean haveEnd(final Vertex vertex) {
        return twin.begining.equals(vertex);
    }

    /**
     *
     * @return
     */
    protected AbstractList<HalfEdge> withSameOrigin() {

        AbstractList<HalfEdge> result = new LinkedList<>();

        HalfEdge current = this;
        do {
            current = current.twin.next;
            result.add(current);
        } while (!this.equals(current));

        return result;
    }

    /**
     *
     * @return
     */
    public boolean isOuter() {
        return incidentFace.isOuterFace();
    }

    /**
     *
     * @param face
     * @return
     */
    public boolean isOfFace(final AbstractFace face) {
        return incidentFace.equals(face);
    }

    /**
     *
     * @return
     */
    public AbstractFace getIncidentFace() {
        return incidentFace;
    }

    @Override
    public Iterable<AbstractFace> getNeighboringFaces() {
        LinkedList<AbstractFace> neighbors = new LinkedList<>();
        neighbors.add(incidentFace);
        neighbors.add(twin.incidentFace);
        return neighbors;
    }

    /**
     *
     * @return
     */
    public HalfEdge getTwin() {
        return twin;
    }

    /**
     *
     * @return
     */
    public HalfEdge getNext() {
        return next;
    }

    /**
     *
     * @return
     */
    public HalfEdge getPrevious() {
        return previous;
    }

    /**
     *
     * @return
     */
    public AbstractFace getTwinsFace() {
        return twin.incidentFace;
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

        if (!(obj instanceof HalfEdge)) {
            return false;
        }
        final HalfEdge other = (HalfEdge) obj;

        return this.id.equals(other.id);
    }

    public boolean equals(final HalfEdge other) {
        return this.id.equals(other.id);
    }

    Integer getBeginingIndex() {
        return begining.getIndex();
    }

    /**
     *
     * @return
     */
    public Vertex getBegining() {
        return begining;
    }

    public Coordinates getBeginingPosition() {
        return begining.position();
    }

    public Vertex getEnd() {
        return twin.getBegining();
    }

    public Coordinates getEndPosition() {
        return getEnd().position();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb);
        return sb.toString();
    }

    /**
     *
     * @param sb
     */
    public void appendTo(StringBuilder sb) {
        sb.append(id.toString());
        sb.append(" P:");
        sb.append(previous != null ? previous.id.toString() : "-");
        sb.append(" N:");
        sb.append(next != null ? next.id.toString() : "-");
        sb.append(getIncidentFace().getIndex());
        sb.append(isOuter() ? " outer" : " inner");
    }

    void assureLoopHaveSameFace() {
        incidentFace.corretctEdgeToFaceMapping();
    }

    /**
     *
     * @return
     */
    public Vector3 vector() {
        return new Vector3(begining.position(), twin.begining.position());
    }

    /**
     *
     * @return
     */
    /**
     *
     * @param at
     * @return
     */
    @Override
    public Vertex split(Coordinates at) {
        EdgeSplitter spliter = new EdgeSplitter(at, this);
        return spliter.splitIt();
    }

    HalfEdge getNextFromOrigin() {
        return twin.next;
    }

    void replaceHalfEdgeByTwo(HalfEdge newEdgeFirst, HalfEdge newEdgeSecond) {
        previous.setNext(newEdgeFirst);
        next.setPrevios(newEdgeSecond);
        newEdgeFirst.setNext(newEdgeSecond);
    }

    void replaceEdgeByTwo(HalfEdge newEdgeFirst, HalfEdge newEdgeSecond) {
        replaceHalfEdgeByTwo(newEdgeFirst, newEdgeSecond);
        twin.replaceHalfEdgeByTwo(newEdgeSecond.twin, newEdgeFirst.twin);
        removeFromDcel();
    }

    /**
     *
     */
    @Override
    public void removeFromDcel() {
        if (getDCEL().haveEdge(this.id)) {
            if (isOuter()) {
                if (getDCEL().haveEdge(next.id)) {
                    incidentFace.setIncidentEdge(next);
                } else if (getDCEL().haveEdge(previous.id)) {
                    incidentFace.setIncidentEdge(previous);
                }
            }
            fixOriginIncidenOnRemove();
            twin.fixOriginIncidenOnRemove();
            getDCEL().remove(this);
        }
    }

    /**
     *
     */
    public void removeFromDcelAndReconect() {
        if (getDCEL().haveEdge(this.id)) {
            fixOriginIncidenOnRemove();
            twin.fixOriginIncidenOnRemove();
            HalfEdge nextOfThis = next;
            HalfEdge nextOfTwin = twin.next;
            HalfEdge prevOfThis = previous;
            HalfEdge prevOfTwin = twin.previous;

            prevOfTwin.setNext(nextOfThis);
            prevOfThis.setNext(nextOfTwin);
            getDCEL().remove(this);
        }
    }

    private void fixOriginIncidenOnRemove() {
        if (begining.getIncidentHalfEdge().equals(this)) {
            if (twin.next.equals(this)) {
                getDCEL().remove(begining);
            } else {
                begining.setIncidentEdge(twin.next);
            }
        }
    }

    /**
     *
     */
    protected void connectFaces() {
        previous.setNext(twin.next);
        next.setPrevios(twin.previous);
    }

    /**
     *
     * @return
     */
    @Override
    public Vector3 getNormal() {
        if (isOuter()) {
            return twin.getIncidentFace().getNormal();
        } else if (twin.isOuter()) {
            return getIncidentFace().getNormal();
        }
        Vector3 normalOfFace = getIncidentFace().getNormal();
        Vector3 normalOfTwinFace = twin.getIncidentFace().getNormal();
        return normalOfFace.getMiddlePoint(normalOfTwinFace);
    }

    /**
     *
     * @param edgeToProject
     * @return
     */
    public boolean sameOrigin(final HalfEdge edgeToProject) {
        return begining.equals(edgeToProject.begining);
    }

    /**
     *
     * @param Vertex
     * @return
     */
    public boolean contains(Vertex Vertex) {
        return haveEnd(Vertex) || haveOrigin(Vertex);

    }

    HalfEdgeId getTwinIndex() {
        return twin.getId();
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public HittedEdge projectEdgeOn(Vertex from, Vertex to) {
        Vector3 projectedVector = Vector3.cretaVector(from, to);
        Coordinates positionOfNew = projectedVector.getProjectionOf(this);
        return new HittedEdge(positionOfNew, twin);
    }

    /**
     *
     * @return
     */
    public Vertex[] getVerteciesFromOrigin(final int count) {
        Vertex[] vertices = new Vertex[count];
        HalfEdge halfEdge = this;
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = halfEdge.begining;
            halfEdge = halfEdge.next;
        }
        return vertices;
    }

    Iterator<HalfEdge> iterator() {
        return new HalfEdgeIterator(this);
    }

    /**
     *
     * @return
     */
    public Iterable<HalfEdge> getLoop() {
        return new Iterable<HalfEdge>() {

            @Override
            public Iterator<HalfEdge> iterator() {
                return HalfEdge.this.iterator();
            }
        };
    }

    Iterable<Vertex> getVertexLoop() {
        return new Iterable<Vertex>() {

            @Override
            public Iterator<Vertex> iterator() {
                return new VertexIterator(HalfEdge.this);
            }
        };
    }

    @Override
    public Vertex createAproximatedVertexAt(Coordinates point) {
        ApproximationOnHalfEdge aproximation;
        aproximation = new ApproximationOnHalfEdge(this, point);
        return aproximation.aproximateNewVertex();
    }

    @Override
    public Collection<Coordinates> getRelatedPositions() {
        ArrayList<Coordinates> coordinates = new ArrayList<>(2);
        coordinates.add(getBegining().position());
        coordinates.add(getEnd().position());
        return coordinates;
    }

}
