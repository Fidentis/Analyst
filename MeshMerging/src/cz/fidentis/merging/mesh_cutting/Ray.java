package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Objects;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Ray {

    private final Coordinates origin;
    private final Vector3 direction;
    private final ParametricLine equation;

    /**
     *
     * @param point
     * @param vector
     */
    public Ray(final Coordinates point, final Vector3 vector) {
        Objects.requireNonNull(point, "Point cannot be null");
        Objects.requireNonNull(vector, "Vector canot be null");
        this.origin = point;
        this.direction = vector;
        equation = new ParametricLine(direction, origin);
    }

    /**
     *
     * @param from
     * @param towards
     */
    public Ray(final Coordinates from, final Coordinates towards) {
        this(from, new Vector3(from, towards));
    }

    /**
     *
     * @param ray
     */
    public Ray(final Ray ray) {
        this(ray.origin, ray.direction);
    }

    /**
     *
     * @param normal
     * @return
     */
    public final double dotProduct(final Vector3 normal) {
        return direction.dotProduct(normal);
    }

    /**
     *
     * @return
     */
    public final Coordinates getOrigin() {
        return origin;
    }

    /**
     *
     * @param with
     * @return
     */
    public final Vector3 crossProduct(final Vector3 with) {
        return direction.crossProduct(with);
    }

    /**
     *
     * @param t
     * @return
     */
    public final Coordinates getPoint(final double t) {
        return direction.translate(origin, t);
    }

    /**
     *
     * @param from
     * @return
     */
    public final Vector3 vectorToOrigin(final Coordinates from) {
        return new Vector3(from, origin);
    }

    /**
     *
     * @return
     */
    public final Vector3 getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return origin.toString();
    }

    /**
     *
     * @param sb
     */
    public void append(StringBuilder sb) {
        sb.append(origin);
        sb.append(' ');
    }

    public double getDistance(AbstractHit hit) {
        return origin.getDistance(hit.getPosition());
    }

    public ParametricLine getParametricLine() {
        return equation;
    }

}
