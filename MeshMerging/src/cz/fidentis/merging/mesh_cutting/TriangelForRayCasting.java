package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangleFace;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Objects;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class TriangelForRayCasting extends Triangle {

    /**
     *
     * @param face
     */
    public TriangelForRayCasting(TriangleFace face) {
        super(face, face.getIncidentHalfEdge());
    }

    /**
     *
     * @return
     */
    @Override
    protected final Coordinates getImpact() {
        final Vector3 movementU = fromAtoB.scaleUp(u);
        final Vector3 movementV = fromAtoC.scaleUp(v);
        return movementV.translate(movementU.translate(possA));
    }

    /**
     *
     * @param toIntersect
     * @return
     */
    public AbstractIntersection intersectWith(final Ray toIntersect) {
        Objects.requireNonNull(toIntersect, "toIntersec cannot be null");
        final Ray ray = new Ray(toIntersect);
        final Vector3 pVec = ray.crossProduct(fromAtoC);
        final double determinant = fromAtoB.dotProduct(pVec);

        if (determinant > -EPSILLON && determinant < EPSILLON) {
            return new EmptyIntersection();
        }
        final double invdeterminant = 1.0d / determinant;

        final Vector3 toOrigin = ray.vectorToOrigin(possA);
        u = toOrigin.dotProduct(pVec) * invdeterminant;
        if (u < 0.0d || u > 1.0d) {
            return new EmptyIntersection();
        }

        final Vector3 qVec = toOrigin.crossProduct(fromAtoB);
        v = ray.dotProduct(qVec) * invdeterminant;
        if (v < 0.0d || u + v > 1.0d) {
            return new EmptyIntersection();
        }

        return getInisdeResult();
    }

}
