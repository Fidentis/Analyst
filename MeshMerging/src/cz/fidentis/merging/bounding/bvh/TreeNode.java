package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Set;

/**
 *
 * @author matej
 */
public class TreeNode extends AbstractNode implements ParentNode {

    private AbstractNode leftNode;
    private AbstractNode rightNode;
    private final AABB boundingBox;

    public TreeNode(AbstractNode left, AbstractNode right, AABB aabb) {
        leftNode = left;
        rightNode = right;
        boundingBox = aabb;
        leftNode.setParrentWhenNone(this);
        rightNode.setParrentWhenNone(this);
    }

    @Override
    public boolean addIntersectedFaces(Ray ray, Set<AbstractHit> intersections) {
        if (!boundingBox.intersectsWith(ray)) {
            return false;
        }
        boolean result = leftNode.addIntersectedFaces(ray, intersections);
        result = rightNode.addIntersectedFaces(ray, intersections) || result;
        return result;
    }

    @Override
    public void replace(AbstractNode oldNode, AbstractNode newNode) {
        if (oldNode == leftNode) {
            leftNode.disconect();
            leftNode = newNode;
        }
        if (oldNode == rightNode) {
            rightNode.disconect();
            rightNode = newNode;
        }
    }

    @Override
    public double getMaxX() {
        return boundingBox.getMaxX();
    }

    @Override
    public double getMinX() {
        return boundingBox.getMinX();
    }

    @Override
    public double getMaxY() {
        return boundingBox.getMaxY();
    }

    @Override
    public double getMinY() {
        return boundingBox.getMinY();
    }

    @Override
    public double getMaxZ() {
        return boundingBox.getMaxZ();
    }

    @Override
    public double getMinZ() {
        return boundingBox.getMinZ();
    }

}
