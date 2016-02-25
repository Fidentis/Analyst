/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.icp;

import cz.fidentis.model.Faces;
import cz.fidentis.utils.IntersectionUtils;
import cz.fidentis.utils.MathUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class KdTreeFaces implements KdTree{
    
    private final KdNode root;          //root of the kD-tree
    private final List<Vector3f> indices; //vertices of model stored in kD-tree
    private Faces faces;        //faces stored in kD-tree

    public KdTreeFaces(List<Vector3f> indices, Faces faces) {
        this.indices = indices;
        this.faces = faces;
        
        List<Vector3f> mids = faceMids(indices, faces.getFacesVertIdxs());
        
        KdTreeIndexed tree = new KdTreeIndexed(mids);
        root = tree.getRoot();
    }
    
    //find middles of faces
    private List<Vector3f> faceMids(List<Vector3f> indices, ArrayList<int[]> faces){
        List<Vector3f> mids = new LinkedList<>();
        
        for(int[] f: faces){
            Vector3f mid = new Vector3f();
            
            for(int i : f){
                mid.add(indices.get(i - 1));
            }
            
            mids.add(MathUtils.instance().divideVectorByNumber(mid, f.length));
        }
        
        return mids;
    }    
    

    @Override
    public Vector3f nearestNeighbour(Vector3f p) {
        int[] triangle = nearestTriangle(p);
        Vector3f[] t = {indices.get(triangle[0] - 1), indices.get(triangle[1] - 1), indices.get(triangle[2] - 1)};
        
        Vector3f normal = MathUtils.instance().getNormalOfTriangle(indices.get(triangle[0] - 1), indices.get(triangle[1] - 1), indices.get(triangle[2] - 1));      
        Vector3f projection = IntersectionUtils.findLinePlaneIntersection(p, normal, normal, t[0]);
        
        if(!IntersectionUtils.pointInTriangle(t, projection)){
            projection = IntersectionUtils.projectionToTriangleEdges(projection, 
                indices.get(triangle[0] - 1), indices.get(triangle[1] - 1), indices.get(triangle[2] - 1));
        }
        
        return projection;
    }

    @Override
    public double nearestDistance(Vector3f p, Vector3f pNormal, boolean useRelative) {
        int[] triangle = nearestTriangle(p);
        float sign = 1f;

        Vector3f[] t = {indices.get(triangle[0] - 1), indices.get(triangle[1] - 1), indices.get(triangle[2] - 1)};
        
        Vector3f normal = MathUtils.instance().getNormalOfTriangle(indices.get(triangle[0] - 1), indices.get(triangle[1] - 1), indices.get(triangle[2] - 1));      
        Vector3f projection = IntersectionUtils.findLinePlaneIntersection(p, normal, normal, t[0]);
        
        if(useRelative){
            sign = getSign(p, projection, pNormal);
        }
        
        return sign * MathUtils.instance().distanceToTriangle(p, projection, t);
    }
    
    /**
     * Returns the sign for given points, depending on whether the point is
     * 'ín front' (+) or 'behind' (-) the mesh.
     * 
     * @param point - vertex for which we want to get the sign
     * @param nearest - nearest neighbour for 'point' in reference mesh
     * @param pointNormal - normal of 'point'
     * @return sign, either plus (+) if vertex is 'in front' of reference mesh or minus (-) if 
     *          vertex is 'behind' reference mesh.
     */
    private float getSign(Vector3f point, Vector3f nearest, Vector3f pointNormal){
        Vector3f pointToNearest = new Vector3f(point.x - nearest.x, point.y - nearest.y, point.z - nearest.z);
        
        return Math.signum(pointToNearest.dot(pointNormal));
    }
    
    @Override
    public KdNode nearestNeighborNode(Vector3f p) {
       if(p == null || root == null){
            return null;
        }
        
        double minDistance = Double.MAX_VALUE;
        double distOnAxis;
        KdNode near = root;
        KdNode searched = root;
        Queue<KdNode> queue = new LinkedList<KdNode>();
        
        while(searched != null){
            float dist = checkDistanceToTriangle(searched, p);
            
            if(dist < minDistance){
                minDistance = dist;
                near = searched;
            }
            
            if(comparePointsOnLevel(p, searched.getId(), searched.getDepth())){
                searched = searched.getLesser();
            }else{
                searched = searched.getGreater();
            }
        }
            
            queue.add(root);
            
        while (!queue.isEmpty()) {
            if (minDistance == 0) {
                break;
            }

            searched = queue.poll();

            
            float dist = checkDistanceToTriangle(searched, p);

            if (dist < minDistance) {
                minDistance = dist;
                near = searched;
            }

            distOnAxis = minDistanceIntersection(searched.getId(), p, searched.getDepth());

            if (distOnAxis > minDistance) {
                if (comparePointsOnLevel(p, searched.getId(), searched.getDepth())) {
                    if (searched.getLesser() != null) {
                        queue.add(searched.getLesser());
                    }
                } else if (searched.getGreater() != null) {
                    queue.add(searched.getGreater());
                }
            } else {
                if (searched.getLesser() != null) {
                    queue.add(searched.getLesser());
                }
                if (searched.getGreater() != null) {
                    queue.add(searched.getGreater());
                }
            }
                
            }
        
        return near;
    }
    
    //finds nearest triangle to vector p
    private int[] nearestTriangle(Vector3f p){

        KdNode near = nearestNeighborNode(p);
        
        return faces.getFaceVertIdxs(near.getIndex());
       
    }

    //computes distance to given triangle, donted by index of KdNode
    private float checkDistanceToTriangle(KdNode searched, Vector3f p) {
        return checkDistanceToTriangle(searched.getIndex(), p);
    }
    
    //computes distance to given triangle denoted by index of the triangle
    private float checkDistanceToTriangle(int searched, Vector3f p) {
        int[] triangleId = faces.getFaceVertIdxs(searched);
        Vector3f[] triangle = {indices.get(triangleId[0] - 1), indices.get(triangleId[1] - 1), indices.get(triangleId[2] - 1)};
        Vector3f normal = MathUtils.instance().getNormalOfTriangle(indices.get(triangleId[0] - 1), indices.get(triangleId[1] - 1), indices.get(triangleId[2] - 1));

        Vector3f projection = IntersectionUtils.findLinePlaneIntersection(p, normal, normal, triangle[0]);
        
        float dist = MathUtils.instance().distanceToTriangle(p, projection, triangle);

        return dist;
    }
    
    /**
     * Compare two points based on the level of their nodes.
     * 
     * @param p1 - first point to compare
     * @param p2 - second point to compare
     * @param level - axis based on which comparison will be performed
     * @return - true if p1 value of axis level is smaller or equal to that of p2, false otherwise
     */
    private boolean comparePointsOnLevel(Vector3f p1, Vector3f p2, int level){
        if(level % 3 == 0){
            return p1.getX() <= p2.getX();
        }else if(level % 3 == 1){
            return p1.getY() <= p2.getY();
        }else if(level % 3 == 2){
            return p1.getZ() <= p2.getZ();
        }
        
        return false;
    } 
    
    /**
     * Defines distance between currently searched node and point to which we want to find nearest neighbor in the tree,
     * based on the axis given by level.
     * 
     * @param searched - currently searched node
     * @param p2 - point to which we want to find nearest neighbor in the tree
     * @param level - axis based on which distance will be computed
     * @return float value of distance on the axis given by level
     */
     private float minDistanceIntersection(Vector3f searched, Vector3f p2, int level){
        if(level % 3 == 0){
            return Math.abs(searched.x - p2.x);
        }else if(level % 3 == 1){
             return Math.abs(searched.y - p2.y);
        }else{
             return Math.abs(searched.z - p2.z);
        }
    } 

    
    
}
