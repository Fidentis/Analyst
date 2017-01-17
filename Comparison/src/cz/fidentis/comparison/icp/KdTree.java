/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.icp;

import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public interface KdTree {

    /**
     * Finds nearest neighbor of Point p in KdTree given by root.
     *
     * @param p - point of which we want to find nearest neighbor in the tree.
     * @return  - point representing nearest neighbor of point p
     */
    Vector3f nearestNeighbour(Vector3f p);
    
    /**
     * Finds nearest KdNode of point p in KdTree given by root.
     * 
     * @param p - point of which we want to find nearest neighbor in the tree.
     * @return KdNode representing nearest neighbor of point p
     */
    KdNode nearestNeighborNode(Vector3f p);
    
    /**
     * Computes distance to nearest neighbor. If useRelative is true, it will 
     * compute signed distance.
     * 
     * @param p - point to compute the distance to nearest neighbor from
     * @param pNormal - normal of the point p
     * @param useRelative - whether to use signed distance or not
     * @return distance from point p to its nearest neighbor in tree. If useRelative is true,
     * then signed distance will be computed
     */
    double nearestDistance(Vector3f p, Vector3f pNormal, boolean useRelative);
    
}
