package cz.fidentis.merging.mesh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import static cz.fidentis.merging.mesh.Coordinates.DIMENSION;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javax.vecmath.Vector3f;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class Vector3 extends Coordinates {

    public static Vector3 weightedSumVector(Collection<Vector3> extarnalDisplacments) {
        return sumToVector(extarnalDisplacments).scaleDown(extarnalDisplacments.size());
    }

    public static Vector3 fromArrayV(double[] coords) {
        return new Vector3(Arrays.copyOf(coords, DIMENSION));
    }

    /**
     *
     */
    private Vector3() {
        super();
    }

    public final static Vector3 ZERO_VECTOR = new Vector3();

    /**
     *
     * @param coordsArray
     */
    public Vector3(final double[] coordsArray) {
        super(coordsArray);
    }

    /**
     *
     * @param vec
     */
    public Vector3(final Vector3f vec) {
        super(vec);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3(final double x, final double y, final double z) {
        super(x, y, z);
    }

    /**
     *
     * @param from
     * @param to
     */
    public Vector3(final Coordinates from, final Coordinates to) {
        this(to.substractCoords(from));
    }

    public Vector3(final Vector3 from, double fromWeight, final Vector3 to, double toWeight) {
        super(from, fromWeight, to, toWeight);
    }

    public Vector3(final Vector3 a, double weightA, final Vector3 b,
            double weightB, Vector3 c, double weightC) {

        super(a, weightA, b, weightB, c, weightC);
    }

    /**
     *
     * @param coordinates
     */
    public Vector3(final Coordinates coordinates) {
        super(coordinates);
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public static Vector3 cretaVector(final Vertex from, final Vertex to) {
        Objects.requireNonNull(from, "From vertex cannot be null");
        Objects.requireNonNull(to, "To vertex cannot be null");
        return new Vector3(from.position(), to.position());
    }

    /**
     *
     * @param fst
     * @param sec
     * @return
     */
    public static Vector3 add(final Vector3 fst, final Vector3 sec) {
        double[] addInArray = fst.addInArray(sec);
        return new Vector3(addInArray);
    }

    /**
     *
     * @param toSum
     * @return
     */
    public static Vector3 sumToVector(Iterable<Vector3> toSum) {
        return new Vector3(sumArray(toSum));
    }

    /**
     *
     * @param size
     * @return
     */
    public Vector3 scaleUp(final double size) {
        return new Vector3(scaledCoords(size));
    }

    /**
     *
     * @param size
     * @return
     */
    public Vector3 scaleDown(final double size) {
        return new Vector3(scaledCoordsDown(size));
    }

    /**
     *
     * @param with
     * @return
     */
    public Vector3 crossProduct(final Vector3 with) {

        double[] cross = new double[DIMENSION];

        cross[0] = coords[1] * with.coords[2] - coords[2] * with.coords[1];
        cross[1] = coords[2] * with.coords[0] - coords[0] * with.coords[2];
        cross[2] = coords[0] * with.coords[1] - coords[1] * with.coords[0];

        return new Vector3(cross);
    }

    /**
     *
     * @param with
     * @return
     */
    public double crossProductMagnitude(final Vector3 with) {

        double[] cross = new double[DIMENSION];

        cross[0] = coords[1] * with.coords[2] - coords[2] * with.coords[1];
        cross[1] = coords[2] * with.coords[0] - coords[0] * with.coords[2];
        cross[2] = coords[0] * with.coords[1] - coords[1] * with.coords[0];

        double dotProduct = 0.d;

        for (int i = 0; i < DIMENSION; i++) {
            dotProduct += (cross[i] * cross[i]);
        }

        return Math.sqrt(dotProduct);
    }

    /**
     *
     * @param point
     * @param t
     * @return
     */
    public Coordinates translate(final Coordinates point, final double t) {
        Vector3 vector3 = new Vector3(scaled(t));
        return vector3.translate(point);
    }

    /**
     *
     * @param translation
     * @return
     */
    public Coordinates translate(final Coordinates translation) {
        return translation.add(this);
    }

    /**
     *
     * @return
     */
    public double getLength() {
        return Math.sqrt(dotProduct(this));
    }

    /**
     *
     * @return
     */
    public double getSqaureLength() {
        return dotProduct(this);
    }

    /**
     *
     * @param other
     * @return
     */
    public double dotProduct(final Coordinates other) {

        double product = 0.d;

        for (int i = 0; i < DIMENSION; i++) {
            product += (coords[i] * other.coords[i]);
        }

        return product;
    }

    /**
     *
     * @return
     */
    public Vector3 getOposite() {
        return new Vector3(getOpositeCoords());
    }

    /**
     *
     * @param other
     * @return
     */
    public double cosineOfAngel(Vector3 other) {
        return dotProduct(other) / (getLength() * other.getLength());
    }

    /**
     *
     * @param first
     * @param second
     * @return
     */
    public static Vector3 subtract(Vector3 first, Vector3 second) {
        Coordinates substract;
        substract = first.substract(second);
        return new Vector3(substract);
    }

    /**
     *
     * @param edge
     * @return
     */
    public Coordinates getProjectionOf(HalfEdge edge) {
        Coordinates originalPoint = edge.getBeginingPosition();
        Vector3 cuttingVector = edge.vector();
        double scale = dotProduct(cuttingVector);
        scale /= cuttingVector.getSqaureLength();
        return cuttingVector.translate(originalPoint, scale);
    }

    public Vector3 normalized() {
        double length = getLength();
        return scaleDown(length);
    }

    public Vector3 getMiddlePoint(Vector3 to) {
        return Vector3.add(this, to).scaleDown(2.d);
    }

}
