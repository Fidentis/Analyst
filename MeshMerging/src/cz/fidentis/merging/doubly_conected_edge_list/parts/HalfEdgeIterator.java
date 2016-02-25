package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.Iterator;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class HalfEdgeIterator implements Iterator<HalfEdge> {

    private HalfEdge current;
    private final HalfEdge last;
    private boolean isLast = false;

    HalfEdgeIterator(final HalfEdge firstEdge) {
        current = firstEdge;
        last = current.getPrevious();
    }

    @Override
    public boolean hasNext() {
        return !(current == null || isLast);
    }

    @Override
    public HalfEdge next() {
        HalfEdge result = current;
        isLast = last.equals(result);
        current = current.getNext();
        return result;
    }

    @Override
    public void remove() {
        HalfEdge toRemove = current;
        current = current.getPrevious();
        toRemove.removeFromDcel();
    }

}
