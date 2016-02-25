package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Objects;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public abstract class Triangle {

    static final double EPSILLON = 0.00001;

    private final AbstractFace owner;
    private final Vertex vertexA;
    final Vertex vertexB;
    final Vertex vertexC;
    final Coordinates possA;
    final Vector3 fromAtoB;
    final Vector3 fromAtoC;

    double v;
    double u;
    boolean hitIsOutside;

    /**
     *
     * @param face
     * @param startingEdge
     */
    public Triangle(final AbstractFace face, HalfEdge startingEdge) {
        Objects.requireNonNull(face);
        Vertex[] vertices = startingEdge.getVerteciesFromOrigin(3);
        vertexA = vertices[0];
        vertexB = vertices[1];
        vertexC = vertices[2];
        owner = face;
        possA = vertexA.position();
        fromAtoB = new Vector3(possA, vertexB.position());
        fromAtoC = new Vector3(possA, vertexC.position());

    }

    /**
     *
     * @return
     */
    protected abstract Coordinates getImpact();

    /**
     *
     * @return
     */
    protected AbstractHit getInisdeResult() {
        if (v <= EPSILLON) {
            if (u <= EPSILLON) {
                return new HittedVertex(vertexA, hitIsOutside);
            } else if (1.0d - EPSILLON <= u) {
                return new HittedVertex(vertexC, hitIsOutside);
            } else {
                return getHitEdge(vertexA, vertexB);
            }
        } else if (u <= EPSILLON) {
            if (1.0d - EPSILLON <= v) {
                return new HittedVertex(vertexB, hitIsOutside);
            } else {
                return getHitEdge(vertexC, vertexA);
            }
        } else if (1.0d + EPSILLON >= v + u && 1.0d - EPSILLON <= v + u) {
            return getHitEdge(vertexB, vertexC);
        }
        return new HittedFace(getImpact(), owner);
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    protected HittedEdge getHitEdge(final Vertex from, final Vertex to) {
        HalfEdge hit = owner.getOwnersHalfEdge(new HalfEdgeId(from, to));
        return new HittedEdge(getImpact(), hit, hitIsOutside);
    }

    @Override
    public String toString() {
        return String.format("A:%sAB:%sAC:%s",
                possA.toString(), fromAtoB.toString(), fromAtoC.toString());
    }

    protected Vector3 getNormal() {
        return fromAtoB.crossProduct(fromAtoC).normalized();
    }

    protected AbstractFace getOwner() {
        return owner;
    }

}
