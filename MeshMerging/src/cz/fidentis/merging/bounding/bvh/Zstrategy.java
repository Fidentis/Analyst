package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public final class Zstrategy extends CoordinateStrategy {

    protected Zstrategy() {
    }

    @Override
    public CoordinateStrategy getNext() {
        return new Xstrategy();
    }

    @Override
    public double getValue(Coordinates coordinates) {
        return coordinates.getZ();
    }

    @Override
    public double getValue(Vector3 vector) {
        return vector.getZ();
    }

    @Override
    public double getMinValue(FaceForBVH face) {
        return face.getMinZ();
    }

    @Override
    public double getMaxValue(FaceForBVH face) {
        return face.getMaxZ();
    }

    @Override
    public void setMinMax(AABB aabb, double min, double max) {
        aabb.setMaxZ(max);
        aabb.setMinZ(min);
    }

}
