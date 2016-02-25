package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangleFace;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class TriangelForInnerVector extends Triangle {

    private Coordinates possition;
    private Vector3 fromAtoPosition;

    /**
     *
     * @param face
     * @param startingEdge
     */
    public TriangelForInnerVector(AbstractFace face, HalfEdge startingEdge) {
        super(face, startingEdge);
    }

    public TriangelForInnerVector(TriangleFace face) {
        super(face, face.getIncidentHalfEdge());
    }

    /**
     *
     * @param inner
     * @return
     */
    public synchronized AbstractIntersection intresection(Vector3 inner) {
        fromAtoPosition = inner;
        tryHit();
        return hitIsOutside ? getOutsideResult() : getInisdeResult();
    }

    /**
     *
     * @param inner
     * @return
     */
    public synchronized AbstractIntersection intresection(Coordinates inner) {
        fromAtoPosition = new Vector3(possA, inner);
        tryHit();
        return hitIsOutside ? getOutsideResult() : getInisdeResult();
    }

    /**
     *
     * @param innerPoint
     * @param to
     * @return
     */
    public synchronized AbstractIntersection intresection(Coordinates innerPoint, Coordinates to) {
        fromAtoPosition = new Vector3(possA, to);
        tryHit();
        return hitIsOutside ? getEdgeIntersection(innerPoint, to) : getInisdeResult();
    }

    private AbstractIntersection getOutsideResult() {
        if (v < EPSILLON && v > -EPSILLON && u > 1 - EPSILLON) {
            return new HittedVertex(vertexC, hitIsOutside);
        } else if (u < EPSILLON && u > -EPSILLON && v > 1 - EPSILLON) {
            return new HittedVertex(vertexB, hitIsOutside);
        } else if (u > -EPSILLON && v > -EPSILLON && u + v > 1 + EPSILLON) {
            return getHitEdge(vertexC, vertexB);
        } else {
            return new EmptyIntersection();
        }
    }

    private void tryHit() {
        possition = fromAtoPosition.translate(possA);
        Vector3 v0 = fromAtoC;
        Vector3 v1 = fromAtoB;
        Vector3 v2 = fromAtoPosition;
        double dot00 = v0.dotProduct(v0);
        double dot01 = v0.dotProduct(v1);
        double dot02 = v0.dotProduct(v2);
        double dot11 = v1.dotProduct(v1);
        double dot12 = v1.dotProduct(v2);

        double m0011 = dot00 * dot11;
        double m0101 = dot01 * dot01;
        double m1102 = dot11 * dot02;
        double m0112 = dot01 * dot12;
        double m0012 = dot00 * dot12;
        double m0102 = dot01 * dot02;

        double invDenom = 1 / (m0011 - m0101);
        v = (m1102 - m0112) * invDenom;
        u = (m0012 - m0102) * invDenom;

        hitIsOutside = (u <= -EPSILLON) || (v <= -EPSILLON) || (u + v >= 1 + EPSILLON);
    }

    /**
     *
     * @return
     */
    @Override
    protected Coordinates getImpact() {
        if (hitIsOutside) {
            possition = shortenProjected();
        }
        return possition;
    }

    private Coordinates shortenProjected() {
        double scale = 1.d / (u + v);
        return fromAtoPosition.translate(possA, scale);
    }

    private AbstractIntersection getEdgeIntersection(Coordinates innerPoint, Coordinates to) {

        for (HalfEdge edge : getOwner().incidentEdges()) {
            SimpleTriangel simpleTriangel = new SimpleTriangel(edge, innerPoint);
            AbstractIntersection edgeIntersectionFor;
            edgeIntersectionFor = simpleTriangel.getEdgeIntersectionFor(to);
            if (edgeIntersectionFor.successful()) {
                return edgeIntersectionFor;
            }
        }
        return new EmptyIntersection();
    }
}
