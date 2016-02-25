package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.Objects;

/**
 *
 * @author Matej Lobodáš
 */
public final class HalfEdgeId {

    private final int fromIndex;
    private final int toIndex;
    private final int hash;
    private final HalfEdgeId idOfTwin;

    /**
     *
     */
    public static final int HASH_MULTIPLAYER = 29;

    /**
     *
     */
    public static final int HASH_BASE = 7;

    private HalfEdgeId(final HalfEdgeId twin) {
        fromIndex = twin.toIndex;
        toIndex = twin.fromIndex;
        idOfTwin = twin;
        hash = computeHash();
    }

    /**
     *
     * @param from
     * @param toward
     */
    public HalfEdgeId(final int from, final int toward) {
        fromIndex = from;
        toIndex = toward;
        idOfTwin = new HalfEdgeId(this);
        hash = computeHash();
    }

    /**
     *
     * @param from
     * @param to
     */
    public HalfEdgeId(final Vertex from, final Vertex to) {
        Objects.requireNonNull(from, "From should not be null");
        Objects.requireNonNull(to, "To should not be null");
        fromIndex = from.getIndex();
        toIndex = to.getIndex();
        idOfTwin = new HalfEdgeId(this);
        hash = computeHash();
    }

    /**
     *
     * @return
     */
    public int getFromIndex() {
        return fromIndex;
    }

    /**
     *
     * @return
     */
    public int getToIndex() {
        return toIndex;
    }

    private int computeHash() {
        int newHash = HASH_BASE;
        newHash = HASH_MULTIPLAYER * newHash + this.fromIndex;
        newHash = HASH_MULTIPLAYER * newHash + this.toIndex;
        return newHash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof HalfEdgeId)) {
            return false;
        }

        final HalfEdgeId other = (HalfEdgeId) obj;

        return fromIndex == other.fromIndex && toIndex == other.toIndex;
    }

    public boolean equals(final HalfEdgeId other) {
        return fromIndex == other.fromIndex && toIndex == other.toIndex;
    }

    /**
     *
     * @return
     */
    public HalfEdgeId getIdOfTwin() {
        return idOfTwin;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", fromIndex, toIndex);
    }

    /**
     *
     * @param sb
     */
    public void appendOrigin(StringBuilder sb) {
        sb.append(fromIndex);
    }

}
