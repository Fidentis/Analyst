package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public final class Xstrategy extends CoordinateStrategy {

    protected Xstrategy() {

    }

    @Override
    public CoordinateStrategy getNext() {
        return new Ystrategy();
    }

    @Override
    public double getValue(Coordinates coordinates) {
        return coordinates.getX();
    }

    @Override
    public double getValue(Vector3 vector) {
        return vector.getX();
    }

    @Override
    public double getMinValue(FaceForBVH face) {
        return face.getMinX();
    }

    @Override
    public double getMaxValue(FaceForBVH face) {
        return face.getMaxX();
    }

    @Override
    public void setMinMax(AABB aabb, double min, double max) {
        aabb.setMaxX(max);
        aabb.setMinX(min);
    }

}
