package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.bounding.AABB;
import java.util.Collection;

/**
 *
 * @author matej
 */
public class NodeSplit {

    private final CoordinateStrategy splitStrategy;

    public NodeSplit() {
        splitStrategy = new Xstrategy();
    }

    private NodeSplit(final CoordinateStrategy splitStrategy) {
        this.splitStrategy = splitStrategy;
    }

    private NodeSplit getNext() {
        return new NodeSplit(splitStrategy.getNext());
    }

    public AbstractNode createNode(Collection<FaceForBVH> faces) {

        SortedFaces faceOrdering = new SortedFaces(faces, splitStrategy);

        Collection<FaceForBVH> left = faceOrdering.getLeftFaces();
        Collection<FaceForBVH> right = faceOrdering.getRightFaces();

        return new TreeNode(getNode(left), getNode(right), new AABB(faces));
    }

    private AbstractNode getNode(Collection<FaceForBVH> faces) {

        switch (faces.size()) {
            case 0:
                return new EmptyLeafNode();
            case 1:
                return new LeafNode(faces.iterator().next(), getNext());
            default: {
                NodeSplit split = getNext();
                return split.createNode(faces);
            }
        }

    }

}
