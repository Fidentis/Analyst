package cz.fidentis.merging.doubly_conected_edge_list;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcelPart;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import java.util.Objects;

/**
 *
 * @author mlobodas
 * @param <T>
 */
public abstract class AbstractSpliter<T extends AbstractDcelPart> {

    protected final AbstractDcel owner;
    protected final Coordinates splitPoint;
    protected final T toSplit;
    protected final Vertex vertexForSplit;
    private boolean splited = false;

    protected abstract void split();

    public AbstractSpliter(Coordinates splitAt, T toSplit) {

        Objects.requireNonNull(splitAt, "Vertex is null");
        Objects.requireNonNull(toSplit, "Part to split is null");
        this.owner = toSplit.getDCEL();
        this.splitPoint = splitAt;
        this.toSplit = toSplit;
        vertexForSplit = toSplit.createAproximatedVertexAt(splitAt);
    }

    public T toSplit() {
        return toSplit;
    }

    public synchronized Vertex splitIt() {
        if (!splited) {
            splited = true;
            split();
        }
        return vertexForSplit;
    }

}
