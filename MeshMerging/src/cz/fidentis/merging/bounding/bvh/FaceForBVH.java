package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;

/**
 *
 * @author matej
 */
public class FaceForBVH {

    private final AbstractFace underlyingFace;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double minZ;
    private double maxZ;

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    FaceForBVH(AbstractFace face) {
        underlyingFace = face;
        for (Vertex incidentVertex : underlyingFace.incidentVertices()) {
            update(incidentVertex.position());
        }
    }

    AbstractFace getFace() {
        return underlyingFace;
    }

    public void update(Coordinates vertex) {
        double newValue = vertex.getX();
        if (maxX < newValue) {
            maxX = newValue;
        }
        if (minX > newValue) {
            minX = newValue;
        }
        newValue = vertex.getY();
        if (maxY < newValue) {
            maxY = newValue;
        }
        if (minY > newValue) {
            minY = newValue;
        }
        newValue = vertex.getZ();
        if (maxZ < newValue) {
            maxZ = newValue;
        }
        if (minZ > newValue) {
            minZ = newValue;
        }
    }
}
