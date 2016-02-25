package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public class SimpleTriangel {

    static final double EPSILLON = 0.00001;

    final Coordinates possA;
    final Vector3 fromAtoB;
    final Vector3 fromAtoC;

    double v;
    double u;
    boolean hitIsOutside;
    private final HalfEdge edge;
    private Coordinates position;

    /**
     *
     * @param opesedEdge
     * @param oposingVertex
     */
    public SimpleTriangel(HalfEdge opesedEdge, Coordinates oposingVertex) {
        possA = oposingVertex;
        fromAtoB = new Vector3(oposingVertex, opesedEdge.getBeginingPosition());
        fromAtoC = new Vector3(oposingVertex, opesedEdge.getEndPosition());
        edge = opesedEdge;
    }

    /**
     *
     * @param to
     * @return
     */
    protected AbstractIntersection getResult(Coordinates to) {
        if (v <= EPSILLON || u <= EPSILLON) {
            return new EmptyIntersection();
        } else if (1.0d <= v + u) {
            double scale = 1.d / (u + v);
            Vector3 fromAtoPosition = new Vector3(possA, to);
            Coordinates newPos = fromAtoPosition.translate(possA, scale);
            return new HittedEdge(newPos, edge, hitIsOutside);
        } else if (1.0d >= v + u) {
            return new HittedEdge(to, edge, hitIsOutside);
        }
        return new EmptyIntersection();
    }

    private void tryHit(Coordinates to) {
        Vector3 v0 = fromAtoC;
        Vector3 v1 = fromAtoB;
        Vector3 v2 = new Vector3(possA, to);
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

        hitIsOutside = (u <= -EPSILLON) || (v <= -EPSILLON) || (u + v >= 1 - EPSILLON);
    }

    /**
     *
     * @param to
     * @return
     */
    public AbstractIntersection getEdgeIntersectionFor(Coordinates to) {
        tryHit(to);
        return getResult(to);
    }

    @Override
    public String toString() {
        return String.format("A:%sAB:%sAC:%s",
                possA.toString(), fromAtoB.toString(), fromAtoC.toString());
    }

    protected Vector3 getNormal() {
        return fromAtoB.crossProduct(fromAtoC).normalized();
    }

}
