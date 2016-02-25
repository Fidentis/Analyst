package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public final class Ystrategy extends CoordinateStrategy {

    protected Ystrategy() {

    }

    @Override
    public CoordinateStrategy getNext() {
        return new Zstrategy();
    }

    @Override
    public double getValue(Coordinates coordinates) {
        return coordinates.getY();
    }

    @Override
    public double getValue(Vector3 vector) {
        return vector.getY();
    }

    @Override
    public double getMinValue(FaceForBVH face) {
        return face.getMinY();
    }

    @Override
    public double getMaxValue(FaceForBVH face) {
        return face.getMaxY();
    }

    @Override
    public void setMinMax(AABB aabb, double min, double max) {
        aabb.setMaxY(max);
        aabb.setMinY(min);
    }

}
