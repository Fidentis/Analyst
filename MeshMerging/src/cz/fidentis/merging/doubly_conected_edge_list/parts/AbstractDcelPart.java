package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;

/**
 * @author Matej Lobodáš
 */
public abstract class AbstractDcelPart {

    private final AbstractDcel owner;
    private final int hash;

    /**
     *
     */
    protected static final int HASH_STEP = 97;

    /**
     *
     * @param list
     */
    protected AbstractDcelPart(final AbstractDcel list) {
        assert list != null;
        owner = list;
        hash = this.owner.hashCode();
    }

    /**
     *
     * @return
     */
    public final AbstractDcel getDCEL() {
        return owner;
    }

    /**
     *
     * @param index
     * @return
     */
    public HalfEdge getOwnersHalfEdge(final HalfEdgeId index) {
        return owner.getHalfEdge(index);
    }

    /**
     *
     * @param part
     * @return
     */
    public final boolean sameOwner(final AbstractDcelPart part) {
        assert part != null;
        return owner.equals(part.owner);
    }

    /**
     *
     * @param dcel
     * @return
     */
    protected final boolean isOwned(final AbstractDcel dcel) {
        return owner.equals(dcel);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof AbstractDcelPart)) {
            return false;
        }

        return owner.equals(((AbstractDcelPart) obj).owner);
    }

    public abstract Iterable<AbstractFace> getNeighboringFaces();

    public abstract Vertex createAproximatedVertexAt(Coordinates point);

    public abstract Vertex split(Coordinates hitPosition);

    /**
     *
     */
    protected abstract void removeFromDcel();

    /**
     *
     * @return
     */
    public abstract Vector3 getNormal();

    public abstract Collection<Coordinates> getRelatedPositions();
}
