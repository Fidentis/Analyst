package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;

/**
 *
 * @author matej
 * @param <T>
 * @param <E>
 */
public abstract class AbstractSnaxel<T extends AbstractSnaxel, E extends AbstractSnake> {

    private AbstractSnaxel<T, E> previos;
    private AbstractSnaxel<T, E> next;
    private final E snake;
    private final int id;

    protected AbstractSnaxel(E isPartOf) {
        snake = isPartOf;
        id = snake.getSize();
        previos = this;
        next = this;
    }

    protected AbstractSnaxel(T previos) {
        this((E) previos.getSnake());
        insertInSnakeAfter(previos);
    }

    private void insertInSnakeAfter(T snaxel) {
        this.previos = snaxel;
        this.next = snaxel.getNext();
        this.next.previos = this;
        this.previos.next = this;
    }

    public T getNext() {
        return (T) next;
    }

    public T getPrevios() {
        return (T) previos;
    }

    public E getSnake() {
        return snake;
    }

    public void setPrevios(T previos) {
        this.previos = previos;
    }

    public void setNext(T next) {
        this.next = next;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    protected void appendTo(StringBuilder sb) {
        sb.append("Id: ");
        sb.append(id);
    }

    protected String getIndexIfAny(Vertex vertex) {
        return vertex == null ? "-" : String.valueOf(vertex.getIndex());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractSnaxel other = (AbstractSnaxel) obj;
        return this.id == other.id;
    }

    public abstract Coordinates getPosition();

}
