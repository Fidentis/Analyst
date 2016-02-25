package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcelPart;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnMesh;
import java.util.Collection;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 * @param <P>
 */
public abstract class AbstractHit<P extends AbstractDcelPart>
        extends AbstractIntersection {

    private final Coordinates hit;
    private final P partHitted;
    private final boolean goesOut;
    private double distanceToOrigin;

    /**
     *
     * @param placeOfHit
     * @param hitted
     * @param goesOut
     */
    public AbstractHit(final Coordinates placeOfHit, final P hitted, final boolean goesOut) {
        hit = placeOfHit;
        partHitted = hitted;
        this.goesOut = goesOut;
    }

    /**
     *
     * @param placeOfHit
     * @param hitted
     */
    public AbstractHit(final Coordinates placeOfHit, final P hitted) {
        this(placeOfHit, hitted, false);
    }

    /**
     *
     * @return
     */
    public final P getPartHitted() {
        return partHitted;
    }

    /**
     *
     * @return
     */
    public final Coordinates getPosition() {
        return hit;
    }

    /**
     *
     * @return
     */
    protected abstract Coordinates getColor();

    /**
     *
     * @return
     */
    @Override
    public final boolean successful() {
        return true;
    }

    public Vertex insertVertexOnHit() {
        return getPartHitted().split(getPosition());
    }

    /**
     *
     * @return true when the projection continues out of face.
     */
    public boolean goesOut() {
        return goesOut;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(hit);
        sb.append(' ');
        sb.append(partHitted.getClass());
        sb.append(' ');
        return sb.toString();
    }

    public Collection<Coordinates> getRelatedPositionsOfPart() {
        return partHitted.getRelatedPositions();
    }

    public Iterable<AbstractFace> getNeighboringFaces() {
        return partHitted.getNeighboringFaces();
    }

    public abstract AbstractFace getFaceForDisplacment(Vector3 displacment);

    public abstract PositionOnMesh getPositionOnMesh();

    public static AbstractHit getHit(AbstractDcelPart part, Coordinates position) {
        if (part instanceof HalfEdge) {
            return new HittedEdge(position, (HalfEdge) part);
        }
        if (part instanceof AbstractFace) {
            return new HittedFace(position, (AbstractFace) part);
        }
        if (part instanceof Vertex) {
            return new HittedVertex((Vertex) part, false);
        }
        throw new UnsupportedClassVersionError();
    }

    public static AbstractHit getHit(PositionOnMesh positionOnMesh) {
        AbstractDcelPart part = positionOnMesh.getUnderlyingPart();
        Coordinates position = positionOnMesh.getPosition();

        if (part instanceof HalfEdge) {
            return new HittedEdge(position, (HalfEdge) part);
        }
        if (part instanceof AbstractFace) {
            return new HittedFace(position, (AbstractFace) part);
        }
        if (part instanceof Vertex) {
            return new HittedVertex((Vertex) part, false);
        }

        throw new UnsupportedClassVersionError();
    }

    void setDistance(double distance) {
        distanceToOrigin = distance;
    }

    double getDistance() {
        return distanceToOrigin;
    }
}
