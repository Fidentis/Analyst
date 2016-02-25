package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.Iterator;

/**
 *
 * @author matej
 */
public class VertexIterator implements Iterator<Vertex> {

    private HalfEdge current;
    private final Vertex last;
    private boolean isLast = false;

    VertexIterator(final HalfEdge firstEdge) {
        current = firstEdge;
        last = current.getPrevious().getBegining();
    }

    @Override
    public boolean hasNext() {
        return !(current == null || isLast);
    }

    @Override
    public Vertex next() {
        Vertex result = current.getBegining();
        isLast = last.equals(result);
        current = current.getNext();
        return result;
    }

    @Override
    public void remove() {
    }

}
