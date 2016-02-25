package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.doubly_conected_edge_list.AbstractSpliter;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFaceTwinSplit;

/**
 * @author Matej Lobodáš
 */
public class EdgeSplitter extends AbstractSpliter<HalfEdge> {

    private final boolean isInnerEdge;

    /**
     *
     * @param splitAt
     * @param halfEdge
     */
    public EdgeSplitter(Coordinates splitAt, HalfEdge halfEdge) {
        super(splitAt, halfEdge);
        isInnerEdge = !toSplit.getTwin().isOuter();
    }

    /**
     *
     */
    @Override
    protected void split() {

        MeshFaceTwinSplit twinSplit;
        twinSplit = new MeshFaceTwinSplit(vertexForSplit.getIndex(), toSplit.getId());
        splitFor(toSplit, twinSplit);
        if (isInnerEdge) {
            splitFor(toSplit.getTwin(), twinSplit);
        }
        owner.replaceBySplit(twinSplit);

    }

    private void splitFor(HalfEdge halfEdge, MeshFaceTwinSplit split) {

        AbstractFace face = halfEdge.getIncidentFace();
        split.addMapping(face, halfEdge.getNext().getId());
        split.addMapping(face, halfEdge.getPrevious().getId());

    }

}
