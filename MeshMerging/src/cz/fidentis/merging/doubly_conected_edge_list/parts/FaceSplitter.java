package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.doubly_conected_edge_list.AbstractSpliter;
import cz.fidentis.merging.mesh.Coordinates;

/**
 *
 * @author mlobodas
 */
public class FaceSplitter extends AbstractSpliter<AbstractFace> {

    /**
     *
     * @param splitBy
     * @param toSplit
     */
    public FaceSplitter(Coordinates splitBy, AbstractFace toSplit) {
        super(splitBy, toSplit);
    }

    /**
     *
     */
    @Override
    public void split() {
        owner.replaceBySplit(toSplit, vertexForSplit);
    }

}
