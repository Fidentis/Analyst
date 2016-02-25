package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Set;

/**
 *
 * @author matej
 */
public class LeafNode extends AbstractNode {

    private final FaceForBVH face;
    private final NodeSplit possibleSplit;

    LeafNode(FaceForBVH face, NodeSplit split) {
        this.face = face;
        possibleSplit = split;
        this.face.getFace().setBoundings(this);
    }

    @Override
    public boolean addIntersectedFaces(Ray ray, Set<AbstractHit> intersections) {
        AbstractIntersection intersection = face.getFace().getIntersection(ray);
        if (intersection.successful()) {
            intersections.add((AbstractHit) intersection);
            return true;
        }
        return false;
    }

    public void faceWasRemoved(AbstractFace faceToRemove) {
        if (face.equals(faceToRemove)) {
            replaceBy(new EmptyLeafNode());
        }
    }

    @Override
    public double getMaxX() {
        return face.getMaxX();
    }

    @Override
    public double getMinX() {
        return face.getMinX();
    }

    @Override
    public double getMaxY() {
        return face.getMaxY();
    }

    @Override
    public double getMinY() {
        return face.getMinY();
    }

    @Override
    public double getMaxZ() {
        return face.getMaxZ();
    }

    @Override
    public double getMinZ() {
        return face.getMinZ();
    }

}
