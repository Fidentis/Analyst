package cz.fidentis.merging.mesh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class MeshFaceTwinSplit
        implements Iterable<AbstractFace> {

    HashMap<AbstractFace, LinkedList<HalfEdgeId>> splitInfo;
    private final Integer splitingMeshPoint;
    private final HalfEdgeId splitingHalfEdge;

    public Integer getSplitingMeshPoint() {
        return splitingMeshPoint;
    }

    public MeshFaceTwinSplit(Integer splitPoint, HalfEdgeId common) {
        splitInfo = new HashMap<AbstractFace, LinkedList<HalfEdgeId>>();
        splitingMeshPoint = splitPoint;
        splitingHalfEdge = common;
    }

    public HalfEdgeId getSplitingHalfEdge() {
        return splitingHalfEdge;
    }

    public void addMapping(AbstractFace toReplace, HalfEdgeId halfEdgeId) {
        if (!splitInfo.containsKey(toReplace)) {
            splitInfo.put(toReplace, new LinkedList<HalfEdgeId>());
        }
        splitInfo.get(toReplace).add(halfEdgeId);

    }

    @Override
    public Iterator<AbstractFace> iterator() {
        return splitInfo.keySet().iterator();
    }

    LinkedList<HalfEdgeId> getNew(AbstractFace toSplit) {
        return splitInfo.get(toSplit);
    }
}
