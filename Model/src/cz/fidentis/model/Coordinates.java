package cz.fidentis.model;

import java.util.Arrays;
import java.util.Collection;
import javax.vecmath.Vector3f;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Coordinates {

    private final double[] coords;
    private int hash = 0;
    public static final int DIMENSION = 3;

    public static Coordinates getZeroCoordinates() {
        return new Coordinates();
    }

    public Coordinates() {
        coords = new double[]{0.d, 0.d, 0.d};
    }

    public Coordinates(final Coordinates coordinates) {
        coords = coordinates.coords;
    }

    public Coordinates(final double[] coordinates) {
        coords = Arrays.copyOf(coordinates, DIMENSION);
    }

     public Coordinates(Vector3f coord) {
        coords = new double[]{coord.x,coord.y,coord.z};
    }

    
    public Coordinates(final double x, final double y, final double z) {
        coords = new double[]{x, y, z};
    }

    public final double[] getCoords() {
        return coords.clone();
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        for (double f : coords) {
            sb.append(f);
            sb.append(", ");
        }
        final int to = sb.length();
        sb.delete(to - 2, to);
        return sb.toString();
    }

    public final Coordinates scaled(final double multiplier) {
        double[] multiplyed = new double[DIMENSION];
        for (int i = 0; i < coords.length; i++) {
            multiplyed[i] = coords[i] * multiplier;
        }
        return new Coordinates(multiplyed);
    }

    public static Coordinates add(final Coordinates augend,
            final Coordinates addend) {
        double[] sum = new double[Coordinates.DIMENSION];

        for (int i = 0; i < sum.length; i++) {
            sum[i] = augend.coords[i] + addend.coords[i];
        }

        return new Coordinates(sum);
    }

    public static double dotProduct(final Coordinates multiplicand,
            final Coordinates multiplier) {

        double product = 0.d;

        for (int i = 0; i < DIMENSION; i++) {
            product += multiplicand.coords[i] * multiplier.coords[i];
        }

        return product;
    }

    public final double sumOfCoordinates() {
        double sum = 0.d;
        for (double coordinate : coords) {
            sum += coordinate;
        }
        return sum;
    }

    protected static double[] substractCoords(final Coordinates minuend,
            final Coordinates subtrahend) {

        double[] sum = new double[Coordinates.DIMENSION];

        for (int i = 0; i < sum.length; i++) {
            sum[i] = minuend.coords[i] - subtrahend.coords[i];
        }

        return sum;
    }

    public static Coordinates substract(final Coordinates minuend,
            final Coordinates subtrahend) {

        return new Coordinates(substractCoords(minuend, subtrahend));
    }

    public static Coordinates sum(final Collection<Coordinates> collection) {
        double[] sum = {0.d, 0.d, 0.d};
        for (Coordinates coordinates : collection) {
            for (int i = 0; i < sum.length; i++) {
                sum[i] += coordinates.coords[i];
            }
        }
        return new Coordinates(sum);
    }

    @Override
    public final int hashCode() {
        if (hash == 0) {
            hash = Arrays.hashCode(this.coords);
        }
        return hash;
    }

    @Override
    public final boolean equals(final Object obj) {

        if (!(obj instanceof Coordinates)) {
            return false;
        }

        final Coordinates other = (Coordinates) obj;
        for (int i = 0; i < coords.length; i++) {
            if (coords[i] != other.coords[i]) {
                return false;
            }
        }

        return true;
    }

    public final Coordinates crossProduct(final Coordinates with) {

        double[] cross = new double[DIMENSION];

        cross[0] = this.coords[1] * with.coords[2]
                - this.coords[2] * with.coords[1];
        cross[1] = this.coords[2] * with.coords[0]
                - this.coords[0] * with.coords[2];
        cross[2] = this.coords[0] * with.coords[1]
                - this.coords[1] * with.coords[0];

        return new Coordinates(cross);
    }

    public final double getDistance(final Coordinates to) {
        double sum = 0.d;
        for (int i = 0; i < coords.length; i++) {
            double difference = coords[i] * to.coords[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

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
    
    public void setCoords(Vector3f v){
        coords[0] = v.x;
        coords[1] = v.y;
        coords[2] = v.z;
        
    }
    
    public Vector3f getCoordsAsVector3f(){
        return new Vector3f((float)coords[0],(float)coords[1],(float)coords[2]);
    }

    public final double[] asArray() {
        return Arrays.copyOf(coords, DIMENSION);
    }

}
