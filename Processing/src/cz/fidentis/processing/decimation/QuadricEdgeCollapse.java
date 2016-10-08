/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.decimation;

import Jama.Matrix;
import cz.fidentis.model.Model;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import cz.fidentis.utils.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

/**
 *
 * @author Marek Zuzi
 */
public class QuadricEdgeCollapse {
    private final Model model;
    private float threshold = -1;
    
    private CornerTable mesh;
    
    private HashMap<Integer, Matrix> quadrics;
    
    private HashSet<Edge> pairs;
    private HashMap<Edge, Vector3f> pairTargets;
    private HashMap<Edge, Double> pairErrors;
    
    private PriorityQueue<Edge> queue;
    private HashSet<Integer> removedFaces;
    private HashSet<Integer> removedVerts;
    
    private HashMap<Integer, HashSet<Integer>> vertsToFaces;
    private HashMap<Integer, Integer> indexReorientations;
    
    public QuadricEdgeCollapse(Model model) {
        this.model = model;
    }
    
    public void decimate(int targetVertexCount) {
        while(decimateWhilePossible(targetVertexCount)) {
            
        }
    }
    
    public boolean decimateWhilePossible(int targetVertexCount) {
        mesh = new CornerTable(model);
        removedFaces = new HashSet<>();
        removedVerts = new HashSet<>();
        indexReorientations = new HashMap<>();
        
        // TEST
        vertsToFaces = new HashMap<>();
        for(int i=0;i<model.getVerts().size();i++) {
            if(!vertsToFaces.containsKey(i)) {
                vertsToFaces.put(i, new HashSet<Integer>());
            }
        }
        for(int triangleIdx=0;triangleIdx<model.getFaces().getNumFaces();triangleIdx++) {
            int[] triangle = model.getFaces().getFaceVertIdxs(triangleIdx);
            for(Integer vertIdx : triangle) {
                vertsToFaces.get(vertIdx-1).add(triangleIdx);
            }
        }
        
        // 1. determine quadrics for each vertex
        initializeVertexQuadrics();
        // 2. consider all valid pairs
        initializePairs();
        // 3. compute target V of pair and its error
        initializeTargets();
        // 4. construct heap keyed by pair errors
        initializeQueue();
        
        // 5. iterate until desired number of vertices is removed
        for(int i = model.getVerts().size() - targetVertexCount; i > 0; i--) {
            Edge edgeToContract = getEdgeToContract();
            if(edgeToContract == null) {
                break;
            }
            contractEdge(edgeToContract);
        }
        
        // 6. clean up model to remove detached verices and faces
        cleanupModel();
        
        return queue.isEmpty();
    }
    
    public void setThreshold(float t) {
        this.threshold = t;
    }

    public Model getModel() {
        return model;
    }
    
    /**
     * First it is needed to initialize error quadric for each vertex by looking
     * at its incident triangles.
     */
    private void initializeVertexQuadrics() {
        quadrics = new HashMap<>(model.getVerts().size());
        
        for(int i=0;i<model.getVerts().size();i++) {
            quadrics.put(i, computeErrorQuadric(i));
        }
    }
    
    /**
     * Initially sets all valid pairs to be considered in contractions. A valid
     * pair is either an edge in mesh or two disconnected points that are closer
     * to each other than current threshold;
     */
    private void initializePairs() {
        pairs = new HashSet<>();
        
        // add a pair for each edge in model, make sure there are no duplicates
        for(int triangleIdx=0;triangleIdx<model.getFaces().getNumFaces();triangleIdx++) {
            int[] triangle = model.getFaces().getFaceVertIdxs(triangleIdx);
            
            for(int i=0;i<triangle.length;i++) {
                Edge e = new Edge(triangle[i]-1, triangle[(i+1)%triangle.length] -1);
                if(!pairs.contains(e)) {
                    pairs.add(e);
                }
            }
        }
        
        // also add pairs which are valid because of threshold
        if(threshold > 0.0f) {
            for(int i=0;i<model.getVerts().size();i++) {
                Vector3f from = model.getVerts().get(i);
                for(int j=0;j<model.getVerts().size();j++) {
                    Vector3f to = new Vector3f(model.getVerts().get(j));
                    to.sub(from);
                    if(to.length() < threshold) {
                        Edge e = new Edge(i, j);
                        if(!pairs.contains(e)) {
                            pairs.add(e);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * For each pair considered for contraction we need to determine the position
     * of contracted vertex.
     */
    private void initializeTargets() {
        pairTargets = new HashMap<>(pairs.size());
        
        for(Edge e : pairs) {
            pairTargets.put(e, computeTargetV(e));
        }
    }
    
    /**
     * Finally, initialised pairs should be sorted by the cost of contraction so
     * that the least errors would be produced.
     */
    private void initializeQueue() {
        pairErrors = new HashMap<>(pairs.size());
        EdgeComparator c = new EdgeComparator(pairErrors);
        queue = new PriorityQueue<>(pairs.size(), c);
        
        for(Edge e : pairs) {
            // get weight of this pair
            double weight = computeWeight(e);
            pairErrors.put(e, weight);
            
            // add it to priority queue keyed by weight
            queue.add(e);
        }
    }
    
    /**
     * Removes all vertices and faces that were detached during collapses. Also
     * recompute normals.
     */
    private void cleanupModel() {
        // TODO do this
        ArrayList<Vector3f> newVerts = new ArrayList<>();
        
        for(int i=0;i<model.getVerts().size();i++) {
            if(!removedVerts.contains(i)) {
                newVerts.add(model.getVerts().get(i));
            }
        }
    }
    
    /**
     * Gets the edge that adds the least significant error if collapsed, according
     * to the priority queue keyed by error quadric of edge.
     * @return 
     */
    private Edge getEdgeToContract() {
        return queue.poll();
    }
    
    /**
     * Contracts given edge to one vertex on a position which creates the least
     * error. Then updates mesh, error quadrics and weights accordingly.
     * @param e 
     */
    private void contractEdge(Edge e) {
        System.out.println("Starting edge " + e.getFrom() + " to " + e.getTo());
        int fromIdx = getModifiedVertexIndex(e.getFrom());
        int toIdx = getModifiedVertexIndex(e.getTo());
        
        // move TO to the target collapsed location
        Vector3f target = getTargetV(e);
        Vector3f toVertex = model.getVerts().get(toIdx);
        toVertex.x = target.x;
        toVertex.y = target.y;
        toVertex.z = target.z;
        
        // reattach all triangles adjacent to FROM to the moved TO
        // FROM should be removed, with faces that would degenerate to 2D
        // TODO: actually remove correct faces and vertices
        // TODO: update topology
        for(Integer triangleIdx : vertsToFaces.get(fromIdx)) {
            // skip any potential faces that were previously removed
            if(removedFaces.contains(triangleIdx)) {
                continue;
            }
            
            int[] triangle = model.getFaces().getFaceVertIdxs(triangleIdx);
            int fromFoundOn = -1;
            int toFoundOn = -1;
            for(int i=0;i<triangle.length;i++) {
                if(triangle[i]-1 == fromIdx) {
                    fromFoundOn = i;
                }
                if(triangle[i]-1 == toIdx) {
                    toFoundOn = i;
                }
                // TEST
                Edge duplicateEdge = new Edge(triangle[i]-1, triangle[(i+1)%triangle.length]-1);
                queue.remove(duplicateEdge);
            }
            
            if(toFoundOn != -1) {
                // if TO was found, remove the whole face
                for(int i=0;i<triangle.length;i++) {
                    if(i != toFoundOn && i != fromFoundOn) {
                        // remove duplicate edge from queue
                        Edge duplicateEdge = new Edge(triangle[fromFoundOn]-1, triangle[i]-1);
                        queue.remove(duplicateEdge);
                    }
                    triangle[i] = toIdx+1;
                }
                removedFaces.add(triangleIdx);
            } else {
                // if only FROM was found, we just change FROM to TO
                triangle[fromFoundOn] = toIdx+1;
            }
        }
        removedVerts.add(fromIdx);
        indexReorientations.put(fromIdx, toIdx);
        vertsToFaces.get(fromIdx).addAll(vertsToFaces.get(toIdx));
        vertsToFaces.get(toIdx).addAll(vertsToFaces.get(fromIdx));
        System.out.println("DONE, queue lenght: " + queue.size());
    }
    
    /**
     * Computes error quadric of a given vertex by considering planes of all triangles
     * incident to it.
     * @param vertexIdx
     * @return matrix storing error quadric
     */
    private Matrix computeErrorQuadric(int vertexIdx) {
        Matrix Q = new Matrix(4, 4, 0);
        Matrix triQ = new Matrix(4, 4, 0);
        
        // for each triangle
        for(int triangleIdx : vertsToFaces.get(vertexIdx)) {
            int[] triangle = model.getFaces().getFaceVertIdxs(triangleIdx);

            // get normal of current triangle
            Vector3f n = MathUtils.instance().getNormalOfTriangle(
                    model.getVerts().get(triangle[0] - 1),
                    model.getVerts().get(triangle[1] - 1),
                    model.getVerts().get(triangle[2] - 1));
            n.normalize();
            
            float d = getPlaneD(n, model.getVerts().get(triangle[0] - 1));
            
            triQ.set(0, 0, n.x * n.x);
            triQ.set(0, 1, n.x * n.y);
            triQ.set(0, 2, n.x * n.z);
            triQ.set(0, 3, n.x * d);
            
            triQ.set(1, 0, n.y * n.x);
            triQ.set(1, 1, n.y * n.y);
            triQ.set(1, 2, n.y * n.z);
            triQ.set(1, 3, n.y * d);
            
            triQ.set(2, 0, n.z * n.x);
            triQ.set(2, 1, n.z * n.y);
            triQ.set(2, 2, n.z * n.z);
            triQ.set(2, 3, n.z * d);
            
            triQ.set(3, 0, d * n.x);
            triQ.set(3, 1, d * n.y);
            triQ.set(3, 2, d * n.z);
            triQ.set(3, 3, d * d);
            
            // add quadric for this triangle to the result
            Q = Q.plus(triQ);
        }
        return Q;
    }
    
    /**
     * Computes position of contracted vertex from given edge which causes minimal error.
     * @param e edge to contract
     * @return position of vertex to which the edge should be contracted.
     */
    private Vector3f computeTargetV(Edge e) {
        Vector3f from = model.getVerts().get(e.getFrom());
        Vector3f to = model.getVerts().get(e.getTo());

        Vector3f result = new Vector3f(0, 0, 0);
        result.add(from);
        result.add(to);
        result.scale(0.5f);
        /*Matrix Q = quadrics.get(e.getFrom()).plus(quadrics.get(e.getTo()));
        if (Q.det() == 0.0f) {
            result.add(from);
            result.add(to);
            result.scale(0.5f);
        } else {
            Q = Q.inverse();

            result.x = (float) Q.get(0, 3);
            result.y = (float) Q.get(1, 3);
            result.z = (float) Q.get(2, 3);
        }*/

        return result;
    }
    
    private Vector3f getTargetV(Edge e) {
        return pairTargets.get(e);
    }
    
    /**
     * Simply does VERTEX * QUADRIC * VERTEX^T
     * @param vertex vector of the vertex to compute
     * @param quadric error quadric of the vertex
     * @return error of the given vertex and quadric
     */
    private double computeError(Vector3f vertex, Matrix quadric) {
        Vector4d result = new Vector4d(0, 0, 0, 0);
        
        result.x = vertex.x * quadric.get(0, 0) + vertex.y * quadric.get(1, 0) +
                vertex.z * quadric.get(2, 0) + 1 * quadric.get(3, 0);
        
        result.y = vertex.x * quadric.get(0, 1) + vertex.y * quadric.get(1, 1) +
                vertex.z * quadric.get(2, 1) + 1 * quadric.get(3, 1);
        
        result.z = vertex.x * quadric.get(0, 2) + vertex.y * quadric.get(1, 2) +
                vertex.z * quadric.get(2, 2) + 1 * quadric.get(3, 2);
        
        result.w = vertex.x * quadric.get(0, 3) + vertex.y * quadric.get(1, 3) +
                vertex.z * quadric.get(2, 3) + 1 * quadric.get(3, 3);
        
        double cost = result.x * vertex.x + result.y * vertex.y + result.z * vertex.z + result.w * 1;
        
        return cost;
    }
    
    /**
     * Compute d from implicit plane equation a*x + b*y + c*z + d = 0.
     * @param normal normal of plane, (a, b, c)
     * @param vertex any vertex lying in plane, (x, y, z)
     * @return d
     */
    private float getPlaneD(Vector3f normal, Vector3f vertex) {
        float d = normal.dot(vertex);
        return -d;
    }
    
    /**
     * Computes weight of contracting particular edge. The weight represents amount
     * of error introduced to mesh if the pair is contracted.
     * @param edge
     * @return 
     */
    private double computeWeight(Edge edge) {
        // sum quadrics for edge vertices
        Matrix Q1 = quadrics.get(edge.getFrom());
        Matrix Q2 = quadrics.get(edge.getTo());
        Matrix Q = Q1.plus(Q2);
        
        // get target of contraction
        Vector3f target = pairTargets.get(edge);
        
        return computeError(target, Q);
    }
    
    private int getModifiedVertexIndex(int index) {
        int finalIndex = index;
        while(indexReorientations.containsKey(finalIndex)) {
            finalIndex = indexReorientations.get(finalIndex);
        }
        return finalIndex;
    }
    
    /**
     * Get any corner of mesh assigned to given vertex from model.
     * @param vertIdx
     * @return 
     */
    private Corner getCorner(int vertIdx) {
        for(Corner c : mesh.corners()) {
            if(c.vertex == vertIdx) {
                return c;
            }
        }
        return null;
    }
}
