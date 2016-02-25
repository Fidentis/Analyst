package cz.fidentis.merging.bounding;

import cz.fidentis.merging.bounding.bvh.FaceForBVH;
import cz.fidentis.merging.mesh_cutting.ParametricLine;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Collection;

/**
 *
 * @author matej
 */
public class AABB {

    private final Collection<Boundary> boundaries;
    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double maxZ;
    private double minZ;

    public AABB(Collection<FaceForBVH> faces) {

        boundaries = BoundaryFactory.boundariesOfAABB();

        for (FaceForBVH face : faces) {
            checkBorders(face);
        }

    }

    private void checkBorders(FaceForBVH face) {
        for (Boundary boundary : boundaries) {
            boundary.update(face);
            boundary.setMinMax(this);
        }
    }

    public boolean intersectsWith(Ray ray) {
        ParametricLine line = ray.getParametricLine();
        for (Boundary boundary : boundaries) {
            if (boundary.intersectsWith(line)) {
                return true;
            }
        }
        return false;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

}
