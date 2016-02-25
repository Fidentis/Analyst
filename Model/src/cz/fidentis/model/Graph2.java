/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.model;

import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import javax.vecmath.Vector3f;

/**
 *
 * @author Jakub Palenik <422453>
 * 
 * Class represents model as valued graph. Where node is tuple of <VertexIndex, NormalIndex>
 * and edge is valued by Float. Every node stores map of neighbor nodes and value 
 * of edges connecting them.
 * 
 * To use graph it is necessary to call createGrapg() else it returns empty collection.
 */
public class Graph2 {

    HashMap<TupleVN, HashMap<TupleVN, Float>> graph = new HashMap<>();
    List<Vector3f> mesh;
    Faces faces;
    int[] faceInd;
    int[] faceNormInd;

    public Graph2(Model m) {
        mesh = new ArrayList<>(m.getVerts());
        faces = m.getFaces();
    }

    /**
     * creates graph from faces
     */
    public void createGraph() {

        for (int m = 0; m < faces.getNumFaces(); m++) {

            faceInd = faces.getFaceVertIdxs(m);
            faceNormInd = faces.getFaceNormalIdxs(m);

            for (int i = 0; i < faceInd.length; i++) {
                if (!graph.keySet().contains(new TupleVN(faceInd[i], faceNormInd[i]))) {
                    TupleVN t = new TupleVN(faceInd[i], faceNormInd[i]);
                    HashMap<TupleVN, Float> h = new HashMap<>();
                    graph.put(t, h);
                }
            }

            for (int i = 0; i < faceInd.length; i = i + 3) {
                Vector3f a = mesh.get(faceInd[i] - 1);
                Vector3f b = mesh.get(faceInd[i + 1] - 1);
                Vector3f c = mesh.get(faceInd[i + 2] - 1);

                Vector3f ab = vectorize(a, b);
                Vector3f bc = vectorize(b, c);
                Vector3f ca = vectorize(c, a);

                float lenght_ab = ab.length();
                float lenght_bc = bc.length();
                float lenght_ca = ca.length();

              //  System.out.println(new TupleVN(faceInd[i], faceNormInd[i]).equals(new TupleVN(faceInd[i], faceNormInd[i])));

                graph.get(new TupleVN(faceInd[i], faceNormInd[i])).put(new TupleVN(faceInd[i + 1], faceNormInd[i + 1]), lenght_ab);
                graph.get(new TupleVN(faceInd[i], faceNormInd[i])).put(new TupleVN(faceInd[i + 2], faceNormInd[i + 2]), lenght_ca);

                graph.get(new TupleVN(faceInd[i + 1], faceNormInd[i + 1])).put(new TupleVN(faceInd[i], faceNormInd[i]), lenght_ab);
                graph.get(new TupleVN(faceInd[i + 1], faceNormInd[i + 1])).put(new TupleVN(faceInd[i + 2], faceNormInd[i + 2]), lenght_bc);

                graph.get(new TupleVN(faceInd[i + 2], faceNormInd[i + 2])).put(new TupleVN(faceInd[i + 1], faceNormInd[i + 1]), lenght_bc);
                graph.get(new TupleVN(faceInd[i + 2], faceNormInd[i + 2])).put(new TupleVN(faceInd[i], faceNormInd[i]), lenght_ca);

            }

        }
    }

    /**
     * Method extract Indices on points from faces that satisfy a condition that every 2 Vertexes
     * are spread at least dens from each other on graph distance.
     * 
     * @param dens
     * @return arrays for VertexIndices to draw, and NormalIndices to draw which are
     * stored in 2D array where int[0][] - are VertInd. and int[1][0] - are NormInd.
     */
    public int[][] indicesFordDensityNormals(float dens) {
        List<Pair<TupleVN,Float>> queue = new ArrayList<>();
        //TreeMap<TupleVN, Float> stack = new TreeMap<>();
        ArrayList<TupleVN> indicesToReturn = new ArrayList<>();
        Set<TupleVN> activeIndices = new HashSet<>();

        for (int m = 0; m < faces.getNumFaces(); m++) {

            faceInd = faces.getFaceVertIdxs(m);
            faceNormInd = faces.getFaceNormalIdxs(m);

            for (int i = 0; i < faceInd.length; i++) {
                activeIndices.add(new TupleVN(faceInd[i], faceNormInd[i]));
            }
        }

        ArrayList<TupleVN> activeInd = new ArrayList<>();
        activeInd.addAll(activeIndices);

        HashMap<TupleVN, Float> toSum = new HashMap<>();

        while (!activeInd.isEmpty()) {
            TupleVN ind = activeInd.get(0);
            activeInd.remove(0);
            Set<TupleVN> used = new HashSet<>();
            indicesToReturn.add(ind);
            used.add(ind);

            //stack.putAll(graph.get(ind));
            for(TupleVN temp : graph.get(ind).keySet()){
                Pair<TupleVN,Float> p = new Pair<>(temp, graph.get(ind).get(temp));
                queue.add(p);
            }

            //while (!stack.isEmpty()) {
            while (!queue.isEmpty()){
            
                //TupleVN key = stack.firstKey();
                //float value = stack.get(key);
                
                TupleVN key = queue.get(0).first;
                float value = queue.get(0).second;

                //stack.remove(stack.firstKey());
                queue.remove(0);
                if (!used.contains(key)) {
                    used.add(key);

                    if (value < dens) {
                        activeInd.remove(key);
                        toSum.putAll(graph.get(key));

                        for (TupleVN j : toSum.keySet()) {
                            if (!used.contains(j)) {
                                //stack.put(j, toSum.get(j) + value);
                                Pair<TupleVN,Float> toAdd = new Pair<>(j, toSum.get(j) + value);
                                queue.add(toAdd);
                            }
                        }
                        toSum.clear();
                    }
                }
            }
            used.clear();
        }
        int[][] ret = new int[2][indicesToReturn.size()];
        for (int i = 0; i < indicesToReturn.size(); i++) {
            ret[0][i] = indicesToReturn.get(i).x;
            ret[1][i] = indicesToReturn.get(i).y;
        }

        return ret;
    }

    /**
     * Method create vector from 2 points
     *  
     * @param a
     * @param b
     * @return vector made from 2 points
     */
    private Vector3f vectorize(Vector3f a, Vector3f b) {
        return new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);
    }

    /**
     * Node of graph
     * Tuple of <VertefirstIndefirst, NormalIndefirst>
    */
    public class TupleVN implements Comparable<TupleVN> {

        public final int x;
        public final int y;

        public TupleVN(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object a) {
            if (a == null) {
                return false;
            }
            if (getClass() != a.getClass()) {
                return false;
            }
            final TupleVN t = (TupleVN) a;

            if (this.x != t.x) {
                return false;
            }
            if (this.y != t.y) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public int compareTo(TupleVN o) {
            return (int) Math.signum(this.x - o.x);
        }
    }

    /**
     * Simple representation of C++ pair
     * @param <X>
     * @param <Y> 
     */
      public class Pair<X, Y> {

        public final X first;
        public final Y second;

        public Pair(X x, Y y) {
            this.first = x;
            this.second = y;
        }
    }
    
}
