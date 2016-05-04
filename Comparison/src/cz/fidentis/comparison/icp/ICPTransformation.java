/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.comparison.icp;

import Jama.Matrix;
import com.jogamp.graph.math.Quaternion;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class ICPTransformation {
    private Quaternion rotation;
    private float scaleFactor;
    private Vector3f translation;
    private float meanD;
    private Matrix transMatrix;
    
    /**
     * Data holding transformation parameters, in case procrustes was performed everything besides Matrix may be null or have invalid data
     * In case ICP was performed Matrix will be null.
     * 
     * @param translation
     * @param scaleFactor
     * @param rotation
     * @param meanD
     * @param transformationMatrix 
     */
    public ICPTransformation(Vector3f translation, float scaleFactor, Quaternion rotation, float meanD, Matrix transformationMatrix){
        this.translation = translation;
        this.scaleFactor = scaleFactor;
        this.rotation = rotation;
        this.meanD = meanD;
        this.transMatrix = transformationMatrix;
    }

    public Matrix getTransMatrix() {
        return transMatrix;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public float getMeanD() {
        return meanD;
    }
    
    
}
