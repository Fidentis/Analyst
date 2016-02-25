package cz.fidentis.merging.mesh_cutting.snake;

import java.util.Iterator;

/**
 *
 * @author matej
 * @param <T>
 */
public class SnakeIterator<T extends AbstractSnaxel<T, ?>> implements Iterator<T> {

    private T firstReturned;
    private T privioslyReturmed;

    public SnakeIterator(T first) {
        privioslyReturmed = first == null ? null : first.getPrevios();
    }

    @Override
    public boolean hasNext() {
        return privioslyReturmed != null
                && !toReturn().equals(firstReturned);
    }

    @Override
    public T next() {
        if (firstReturned == null) {
            firstReturned = toReturn();
        }
        T current = toReturn();
        movePointer();
        return current;
    }

    private void movePointer() {
        privioslyReturmed = toReturn();
    }

    private T toReturn() {
        return privioslyReturmed.getNext();
    }

    @Override
    public void remove() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
