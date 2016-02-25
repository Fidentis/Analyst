/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.comparison.icp;

import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 * 
 * Class represneting KdNode in KdTree.
 */
public class KdNode {
    
    private final int depth;
    private Vector3f id;
    private int index;
    private KdNode lesser;
    private KdNode greater;
    private KdNode parent;

    public KdNode(int depth, Vector3f id, int index, KdNode parent) {
        this.depth = depth;
        this.id = id;
        this.index = index;
        this.parent = parent;
    }

    public int getDepth() {
        return depth;
    }

    public Vector3f getId() {
        return id;
    }

    public void setId(Vector3f id) {
        this.id = id;
    }

    public KdNode getLesser() {
        return lesser;
    }

    public void setLesser(KdNode lesser) {
        this.lesser = lesser;
    }

    public KdNode getGreater() {
        return greater;
    }

    public void setGreater(KdNode greater) {
        this.greater = greater;
    }

    public KdNode getParent() {
        return parent;
    }

    public void setParent(KdNode parent) {
        this.parent = parent;
    }

    public int getIndex() {
        return index;
    }    
}
