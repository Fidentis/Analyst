package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author xlobodas
 */
public class Vertices implements Iterable<Vertex> {

    private final HashMap<Integer, Vertex> vertices = new HashMap<>();
    private final AbstractDcel vertexOwner;
    private int vertexCounnter = 0;

    Vertices(AbstractDcel owner) {
        vertexOwner = owner;
    }

    Vertex addVertex(Integer index) {
        Vertex newVertex = new Vertex(this, index);
        vertices.put(newVertex.getIndex(), newVertex);
        return newVertex;
    }

    boolean canSupport(HalfEdgeId halfEdgeId) {
        return vertices.containsKey(halfEdgeId.getFromIndex())
                && vertices.containsKey(halfEdgeId.getToIndex());
    }

    int count() {
        return vertexCounnter;
    }

    Vertex get(int vertexId) {
        return vertices.get(vertexId);
    }

    void remove(int index) {
        vertices.remove(index);
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new ArrayList<>(vertices.values()).iterator();
    }

    AbstractDcel getOwner() {
        return vertexOwner;
    }

    int reserveId() {
        return vertexCounnter++;
    }

    void appendsTo(StringBuilder sb) {
        for (Vertex vertex : vertices.values()) {
            vertex.appendTo(sb);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendsTo(sb);
        return sb.toString();
    }

    Iterable<Vertex> getVertecies() {
        ArrayList<Vertex> arrayList = new ArrayList<Vertex>(vertices.size());
        for (Vertex value : vertices.values()) {
            arrayList.add(value);
        }
        return arrayList;
    }

}
