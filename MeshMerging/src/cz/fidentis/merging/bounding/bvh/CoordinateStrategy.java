package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public abstract class CoordinateStrategy {

    protected CoordinateStrategy() {

    }

    public abstract CoordinateStrategy getNext();

    public abstract double getMinValue(FaceForBVH face);

    public abstract double getMaxValue(FaceForBVH face);

    public abstract double getValue(Coordinates coordinates);

    public abstract double getValue(Vector3 vector);

    public final double getValue(Vertex vertex) {
        return getValue(vertex.position());
    }

    public static CoordinateStrategy createXstrategy() {
        return new Xstrategy();
    }

    public static CoordinateStrategy createYstrategy() {
        return new Ystrategy();
    }

    public static CoordinateStrategy createZstrategy() {
        return new Zstrategy();
    }

    public double getLeftMostValue(AbstractFace face) {
        double min = Double.MAX_VALUE;
        for (Vertex vertice : face.incidentVertices()) {
            double current = getValue(vertice);
            if (current < min) {
                min = current;
            }
        }
        return min;
    }

    public abstract void setMinMax(AABB aabb, double min, double max);

}
