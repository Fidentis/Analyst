package cz.fidentis.merging.doubly_conected_edge_list;

import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Boundary implements Iterable<Coordinates> {

    private final Coordinates[] boundaries;
    private final int vertexCount;

    /**
     *
     * @param counterClockWise
     */
    public Boundary(Collection<Coordinates> counterClockWise) {
        vertexCount = counterClockWise.size();
        boundaries = new Coordinates[vertexCount + 2];
        counterClockWise.toArray(boundaries);
        boundaries[vertexCount] = boundaries[0];
        boundaries[vertexCount + 1] = boundaries[1];
    }

    int getVertexCount() {
        return vertexCount;
    }

    Coordinates getVertex(int i) {
        return boundaries[i];
    }

    @Override
    public Iterator<Coordinates> iterator() {
        return new Iterator<Coordinates>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current <= vertexCount + 1;
            }

            @Override
            public Coordinates next() {
                return boundaries[current++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    public void moveForDiference(Boundary diff, Vertex vertex) {
        CoordWithBoundary with = new CoordWithBoundary(this, vertex);
        with.moveForDifernece(diff);
    }

}
