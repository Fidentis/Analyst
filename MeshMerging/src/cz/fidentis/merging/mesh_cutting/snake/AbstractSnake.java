package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.LineLoop;
import java.util.Iterator;

/**
 *
 * @author matej
 * @param <T>
 */
public abstract class AbstractSnake<T extends AbstractSnaxel<T, ?>>
        implements Iterable<T> {

    private T first;
    private LineLoop loop;
    private int size = 0;

    public T getFirst() {
        return first;
    }

    public LineLoop getLoop() {
        return loop;
    }

    public void setLoop(LineLoop loop) {
        this.loop = loop;
    }

    public int getSize() {
        return size;
    }

    protected void addSnaxelAt(PositionOnMesh hit, Vertex orignal) {
        if (first == null) {
            first = createFirst(hit, orignal);
            size++;
        } else {
            addSnaxel(first.getPrevios(), hit, orignal);
        }
    }

    protected T addSnaxel(T previos, PositionOnMesh hit, Vertex vertex) {
        T newSnaxel = AbstractSnake.this.createSnaxel(previos, hit, vertex);
        size++;
        refreshLoop();
        return newSnaxel;
    }

    protected void refreshLoop() {
        if (loop != null) {
            loop.refresh(this);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new SnakeIterator<T>(first);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T snaxel : this) {
            snaxel.appendTo(sb);
        }
        return sb.toString();
    }

    void refresh(int currentId) {
        if (loop != null) {
            loop.refresh(this, currentId);
        }
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    protected abstract T createFirst(PositionOnMesh hit, Vertex orignal);

    protected abstract T createSnaxel(T previos, PositionOnMesh hit, Vertex vertex);

}
