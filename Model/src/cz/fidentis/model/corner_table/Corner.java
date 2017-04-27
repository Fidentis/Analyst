package cz.fidentis.model.corner_table;

/*
 Copyright 2011 Milian Wolff <mail@milianw.de>

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Corner {

    public Corner prev;
    public Corner next;
    public Corner opposite;
    public Corner twin;
    public int vertex;
    public int triangle;
    public int localVertexIndex;

    /**
     * find neighbors of current vertex by iterating over the corner table
     * starting with prev and then jumping to .o.p of that corner until we reach
     * next and quit.
     *
     * If we reach a corner with .o.p == null, we reverse the direction and
     * start with next until we reach .o.n == null
     *
     * @return array of corners that indicate the neighbors of the current
     * vertex
     */
    public Corner[] vertexNeighbors() {
        ArrayList<Corner> neighbors = new ArrayList<>(10);
        Corner i = prev;
        boolean usePrev = true;
        while (true) {
            neighbors.add(i);
            if ((usePrev && i.vertex == next.vertex)
                    || (!usePrev && i.vertex == prev.vertex)) {
                // we just handled the last neighbor - stop
                break;
            } else if (usePrev) {
                i = i.prev.opposite;
                if (i == null) {
                    i = next;
                    usePrev = false;
                }
            } else {
                i = i.next.opposite;
                if (i == null) {
                    break;
                }
            }
        }
        Corner[] ret = new Corner[neighbors.size()];
        neighbors.toArray(ret);
        return ret;
    }

    /**
     * Find indexes of adjacent triangles
     *
     * @return indexes of adjacent triangles of current vertex
     */
    public Integer[] adjacentTriangles() {

        Set<Integer> triangles = new HashSet<>();
        Corner[] neighbors = vertexNeighbors();
        for (Corner neighbor : neighbors) {
            triangles.add(neighbor.triangle);
        }

        Integer[] adjacentTriangles = new Integer[triangles.size()];
        triangles.toArray(adjacentTriangles);
        return adjacentTriangles;
    }
    
    /**
     * Returns an array of all corners belonging to the triangle of this corner.
     * @return 
     */
    public Corner[] triangleCorners() {
        ArrayList<Corner> neighbors = new ArrayList<>(3);
        neighbors.add(this);
        
        Corner current = this.next;
        while(current != this) {
            neighbors.add(current);
            current = current.next;
        }
        
        Corner[] result = new Corner[neighbors.size()];
        neighbors.toArray(result);
        return result;
    }

    public boolean isBoundary() {
        return this.twin == null;
    }

    @Override
    public String toString() {
        String str = "Corner-> triangle: " + triangle + ", vertex: " + vertex
                + ", next: " + next.vertex + ", previous: " + prev.vertex;

        if (opposite != null) {
            str += ", opposite: " + opposite.vertex;
        }
        if (twin != null) {
            str += ", twin: " + twin.vertex;
        } else {
            str += ", is boundary";
        }

        return str;
    }
}
