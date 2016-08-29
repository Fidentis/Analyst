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
// Edited by Marek Galvánek, 2015
import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jv.geom.PgElementSet;
import jv.vecmath.PiVector;

public class CornerTable {

    private ArrayList<Corner> corners;
    private Map<Integer, List<Corner>> triangles;
    private Map<Integer, Corner> vertices;

    public CornerTable(PgElementSet geometry) {
        buildCornerTable(geometry);
        setTwins();
    }

    public CornerTable(Model model) {
        buildCornerTable(model);
        setTwins();
    }

    private void buildCornerTable(Model model) {
        Faces faces = model.getFaces();
        corners = new ArrayList<>(faces.getNumFaces() * 3);
        triangles = new HashMap<>();
        vertices = new HashMap<>();

        ArrayList<CTRow> table = new ArrayList<>(faces.getNumFaces() * 3);
        for (int i = 0; i < faces.getNumFaces(); ++i) {
            int[] faceVertIdxs = faces.getFaceVertIdxs(i);
            Corner a = new Corner();
            a.vertex = faceVertIdxs[0] - 1;
            a.localVertexIndex = 0;
            a.triangle = i;
            Corner b = new Corner();
            b.vertex = faceVertIdxs[1] - 1;
            b.localVertexIndex = 1;
            b.triangle = i;
            Corner c = new Corner();
            c.vertex = faceVertIdxs[2] - 1;
            c.localVertexIndex = 2;
            c.triangle = i;

            a.prev = c;
            a.next = b;
            b.prev = a;
            b.next = c;
            c.prev = b;
            c.next = a;

            List<Corner> triangleCorners = new ArrayList<>();
            triangleCorners.add(a);
            triangleCorners.add(b);
            triangleCorners.add(c);
            corners.addAll(triangleCorners);

            triangles.put(i, triangleCorners);
            vertices.put(i, a);

            table.add(new CTRow(a));
            table.add(new CTRow(b));
            table.add(new CTRow(c));
        }

        // sort table by min index, see CTRow::compareTo
        Collections.sort(table);
        // find pairs and associate c.opposite
        // thanks to sorting, every two consecutive rows
        // are opposite to each other
        for (int i = 0; i < table.size() - 1; i++) {
            CTRow a = table.get(i);
            CTRow b = table.get(i + 1);
            if (a.min != b.min || a.max != b.max) {
                continue;
            }
            assert a.max == b.max;
            a.c.opposite = b.c;
            b.c.opposite = a.c;
            ++i;
        }
    }

    private void buildCornerTable(PgElementSet geometry) {
        corners = new ArrayList<>(geometry.getNumElements() * 3);
        triangles = new HashMap<>();
        vertices = new HashMap<>();

        ArrayList<CTRow> table = new ArrayList<>(geometry.getNumElements() * 3);
        for (int i = 0; i < geometry.getNumElements(); ++i) {
            PiVector verts = geometry.getElement(i);
            Corner a = new Corner();
            a.vertex = verts.getEntry(0);
            a.localVertexIndex = 0;
            a.triangle = i;
            Corner b = new Corner();
            b.vertex = verts.getEntry(1);
            b.localVertexIndex = 1;
            b.triangle = i;
            Corner c = new Corner();
            c.vertex = verts.getEntry(2);
            c.localVertexIndex = 2;
            c.triangle = i;

            a.prev = c;
            a.next = b;
            b.prev = a;
            b.next = c;
            c.prev = b;
            c.next = a;

            List<Corner> triangleCorners = new ArrayList<>();
            triangleCorners.add(a);
            triangleCorners.add(b);
            triangleCorners.add(c);
            corners.addAll(triangleCorners);

            triangles.put(i, triangleCorners);
            vertices.put(i, a);

            table.add(new CTRow(a));
            table.add(new CTRow(b));
            table.add(new CTRow(c));
        }

        // sort table by min index, see CTRow::compareTo
        Collections.sort(table);
        // find pairs and associate c.opposite
        // thanks to sorting, every two consecutive rows
        // are opposite to each other
        for (int i = 0; i < table.size() - 1; i++) {
            CTRow a = table.get(i);
            CTRow b = table.get(i + 1);
            if (a.min != b.min || a.max != b.max) {
                continue;
            }
            assert a.max == b.max;
            a.c.opposite = b.c;
            b.c.opposite = a.c;
            ++i;
        }
    }

    private void setTwins() {
        for (Corner corner : corners) {
            if (corner.prev.opposite != null) {
                Corner twinCand = corner.prev.opposite.next;
                corner.twin = twinCand;
                twinCand.twin = corner;
            }
        }
    }

    public ArrayList<Corner> corners() {
        return corners;
    }

    public int size() {
        return corners.size();
    }

    public Corner getCorner(int i) {
        return vertices.get(i);
    }

    // deprecated, do not use
    public Corner corner_(int i) {
        return corners.get(i);
    }

    /**
     * Find indexes of adjacent triangles
     *
     * @param triangle index of face
     * @return indexes of adjacent triangles of face
     */
    public Set<Integer> getAdjacentTriangles(int triangle) {
        Set<Integer> adjacentTriangles = new HashSet<>();
        List<Corner> triangleCorners = triangles.get(triangle);

        for (Corner triangleCorner : triangleCorners) {
            Corner[] vertexNeighbors = triangleCorner.vertexNeighbors();
            for (Corner neighbor : vertexNeighbors) {
                adjacentTriangles.add(neighbor.triangle);
            }
        }

        adjacentTriangles.remove(triangle);

        return adjacentTriangles;
    }

    /**
     * Find corner neighbors by vertex index
     *
     * @param vertex index of vertex
     * @return corner neighbors
     */
    public Corner[] getCornerNeighbors(int vertex) {
        return getCorner(vertex).vertexNeighbors();
    }
    
    /**
     * 
     * Find index neighbors by vertex
     * 
     * @param index primary vertex
     * @param model model
     * @return array of indexes of neighbors
     */
    public int[] getIndexNeighbors(int index, Model model) {
        Corner main = new Corner();
        int k = -1, c = 0;
        int[] result;
        
        while(c == 0) {
            k++;
            if(index == model.getCornerTable().corners.get(k).vertex) {
                main = model.getCornerTable().corners.get(k);
                c++;
            }
            
        }
        
        result = new int[main.vertexNeighbors().length];
        
        for(int i = 0; i < main.vertexNeighbors().length; i++){
            result[i] = main.vertexNeighbors()[i].vertex;
        }
        
        return result;
    }
}
