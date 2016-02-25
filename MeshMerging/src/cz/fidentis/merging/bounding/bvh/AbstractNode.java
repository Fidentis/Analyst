package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.Ray;
import java.util.Set;

/**
 *
 * @author matej
 */
public abstract class AbstractNode {

    private TreeNode parrent;

    protected void disconect() {
        parrent = null;
    }

    protected void replaceBy(AbstractNode newNode) {
        parrent.replace(this, newNode);
    }

    protected void setParrentWhenNone(TreeNode newParent) {
        if (parrent == null) {
            parrent = newParent;
        }
    }

    public abstract boolean addIntersectedFaces(Ray ray, Set<AbstractHit> intersections);

    public abstract double getMaxX();

    public abstract double getMinX();

    public abstract double getMaxY();

    public abstract double getMinY();

    public abstract double getMaxZ();

    public abstract double getMinZ();

}
