package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.EmptyIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author matej
 */
public class BVH {

    private final AbstractNode root;

    public BVH(Collection<AbstractFace> faces) {
        NodeSplit split = new NodeSplit();
        LinkedList<FaceForBVH> bvhFaces = new LinkedList<>();
        for (AbstractFace face : faces) {
            FaceForBVH bvhFace = new FaceForBVH(face);
            bvhFaces.add(bvhFace);
        }
        root = split.createNode(bvhFaces);
    }

    public AbstractIntersection getNearestHit(Ray ray) {
        double minDistance = Double.MAX_VALUE;
        AbstractIntersection nearest = null;

        for (AbstractHit hit : getIntersections(ray)) {
            double distance = ray.getDistance(hit);
            if (minDistance > distance) {
                minDistance = distance;
                nearest = hit;
            }
        }

        return nearest == null ? new EmptyIntersection() : nearest;
    }

    public Set<AbstractHit> getIntersections(Ray ray) {
        Set<AbstractHit> intersectedFaces = new HashSet<>();
        root.addIntersectedFaces(ray, intersectedFaces);
        return intersectedFaces;
    }

    public double getMaxX() {
        return root.getMaxX();
    }

    public double getMinX() {
        return root.getMinX();
    }

    public double getMaxY() {
        return root.getMaxY();
    }

    public double getMinY() {
        return root.getMinY();
    }

    public double getMaxZ() {
        return root.getMaxZ();
    }

    public double getMinZ() {
        return root.getMinZ();
    }

}
