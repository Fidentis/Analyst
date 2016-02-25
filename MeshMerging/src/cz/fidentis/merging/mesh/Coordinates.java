package cz.fidentis.merging.mesh;

import cz.fidentis.merging.scene.MeshDisplacment;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import javax.vecmath.Vector3f;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Coordinates implements ComposedValue {

    static DecimalFormat format = new DecimalFormat(" 00.00000;-00.00000");

    public static Coordinates sum(final Iterable<Coordinates> toSum) {
        return new Coordinates(sumArray(toSum));
    }

    protected static double[] sumArray(final Iterable<? extends Coordinates> toSum) {
        double[] sum = {0.d, 0.d, 0.d};
        for (Coordinates coordinates : toSum) {
            for (int i = 0; i < sum.length; i++) {
                sum[i] += coordinates.coords[i];
            }
        }
        return sum;
    }

    public static Coordinates weightedSum(Collection<Coordinates> toSum) {
        return weightedSum(toSum, toSum.size());
    }

    protected static Coordinates weightedSum(Collection<Coordinates> toSum, double weight) {
        double[] sumArray = sumArray(toSum);
        for (int i = 0; i < sumArray.length; i++) {
            sumArray[i] = sumArray[i] / weight;
        }
        return new Coordinates(sumArray);
    }

    public static Coordinates fromArray(double[] coords) {
        return new Coordinates(Arrays.copyOf(coords, DIMENSION));
    }

    protected final double[] coords;
    private int hash = 0;

    /**
     *
     */
    public static final int DIMENSION = 3;

    /**
     *
     * @param coordinates
     */
    public Coordinates(final double[] coordinates) {
        coords = coordinates;
    }

    /**
     * Creates coordinates on position 0,0,0
     */
    public Coordinates() {
        this(new double[]{0.d, 0.d, 0.d});
    }

    public Coordinates(Vector3f vect) {
        this(new double[]{vect.x, vect.y, vect.z});
    }

    /**
     *
     * @param coordinates
     */
    public Coordinates(final Coordinates coordinates) {
        this(coordinates.coords);
    }

    public Coordinates(final Coordinates from, double fromWeight,
            Coordinates to, double toWeight) {
        this(getWeighted(from, fromWeight, to, toWeight));
    }

    public Coordinates(Coordinates a, double weightA,
            Coordinates b, double weightB, Coordinates c, double weightC) {
        this(getWeighted(a, weightA, b, weightB, c, weightC));
    }

    private static double[] getWeighted(final Coordinates from, double fromWeight,
            Coordinates to, double toWeight) {

        double[] weighted = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            weighted[i] = from.coords[i] * fromWeight + to.coords[i] * toWeight;
        }
        return weighted;
    }

    private static double[] getWeighted(final Coordinates a, double weightA,
            Coordinates b, double weightB, Coordinates c, double weightC) {

        double[] weighted = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            weighted[i] = a.coords[i] * weightA + b.coords[i] * weightB
                    + c.coords[i] * weightC;
        }
        return weighted;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Coordinates(final double x, final double y, final double z) {
        this(new double[]{x, y, z});
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        for (double f : coords) {
            sb.append(format.format(f));
            sb.append(", ");
        }
        final int to = sb.length();
        sb.delete(to - 2, to);
        return sb.toString();
    }

    /**
     *
     * @param scale multiplier
     * @return Scaled coordinates with respect to center of coordinate system.
     */
    public final Coordinates scaled(final double scale) {
        return new Coordinates(scaledCoords(scale));
    }

    protected final double[] scaledCoords(final double scale) {
        double[] scaled = new double[DIMENSION];
        for (int i = 0; i < coords.length; i++) {
            scaled[i] = coords[i] * scale;
        }
        return scaled;
    }

    protected final double[] scaledCoordsDown(final double scale) {
        double[] scaled = new double[DIMENSION];
        for (int i = 0; i < coords.length; i++) {
            scaled[i] = coords[i] / scale;
        }
        return scaled;
    }

    /**
     *
     * @param addend Coordinates to add to this
     * @return New Coordinates
     */
    public Coordinates add(final Coordinates addend) {
        double[] sum = addInArray(addend);

        return new Coordinates(sum);
    }

    protected final double[] addInArray(final Coordinates addend) {
        double[] sum = new double[Coordinates.DIMENSION];
        for (int i = 0; i < sum.length; i++) {
            sum[i] = coords[i] + addend.coords[i];
        }
        return sum;
    }

    /**
     *
     * @param subtrahend
     * @return
     */
    protected double[] substractCoords(final Coordinates subtrahend) {

        double[] sum = new double[Coordinates.DIMENSION];

        for (int i = 0; i < sum.length; i++) {
            sum[i] = coords[i] - subtrahend.coords[i];
        }

        return sum;
    }

    /**
     *
     * @param subtrahend
     * @return
     */
    public Coordinates substract(final Coordinates subtrahend) {
        return new Coordinates(substractCoords(subtrahend));
    }

    @Override
    public final int hashCode() {
        if (hash == 0) {
            hash = Arrays.hashCode(coords);
        }
        return hash;
    }

    @Override
    public final boolean equals(final Object obj) {

        if (obj instanceof Coordinates) {
            Coordinates other = (Coordinates) obj;

            for (int i = 0; i < coords.length; i++) {
                if (coords[i] != other.coords[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     *
     * @param to
     * @return
     */
    public final double getDistance(final Coordinates to) {
        double sum = 0.d;
        for (int i = 0; i < DIMENSION; i++) {
            double difference = coords[i] - to.coords[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    /**
     *
     * @param other
     * @param epsilon
     * @return
     */
    public final boolean equals(final Coordinates other, final double epsilon) {
        for (int i = 0; i < coords.length; i++) {
            if (coords[i] - epsilon > other.coords[i]) {
                return false;
            }
            if (coords[i] + epsilon < other.coords[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public final double[] asArray() {
        return Arrays.copyOf(coords, DIMENSION);
    }

    /**
     *
     * @return
     */
    public final float[] asFloatArray() {
        float[] floatArray = new float[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            floatArray[i] = (float) coords[i];
        }
        return floatArray;
    }

    double[] getOpositeCoords() {
        double[] result = new double[3];
        for (int i = 0; i < DIMENSION; i++) {
            result[i] = -1 * coords[i];
        }
        return result;
    }

    public Coordinates getMiddlePoint(Coordinates to) {
        return add(to).scaled(0.5);
    }

    public final double getX() {
        return coords[0];
    }

    public final double getY() {
        return coords[1];
    }

    public final double getZ() {
        return coords[2];
    }

    Coordinates getDisplacment(MeshDisplacment dis) {

        Matrix3 mat = dis.getRotation();
        Coordinates multiplyed = mat.multiply(this);
        Vector3 move = dis.getTranslation();
        return move.translate(multiplyed);

    }

}
