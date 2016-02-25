package cz.fidentis.merging.bounding.bvh;

/**
 *
 * @author matej
 */
interface ParentNode {
    void replace(AbstractNode oldNode, AbstractNode newNode);
}
