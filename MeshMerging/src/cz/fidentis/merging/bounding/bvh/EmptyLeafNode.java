package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Set;

/**
 *
 * @author matej
 */
class EmptyLeafNode extends AbstractNode {

    public EmptyLeafNode() {
    }

    @Override
    public boolean addIntersectedFaces(Ray ray, Set<AbstractHit> intersections) {
        return false;
    }

    @Override
    public double getMaxX() {
        return 0.d;
    }

    @Override
    public double getMinX() {
        return 0.d;
    }

    @Override
    public double getMaxY() {
        return 0.d;
    }

    @Override
    public double getMinY() {
        return 0.d;
    }

    @Override
    public double getMaxZ() {
        return 0.d;
    }

    @Override
    public double getMinZ() {
        return 0.d;
    }

}
