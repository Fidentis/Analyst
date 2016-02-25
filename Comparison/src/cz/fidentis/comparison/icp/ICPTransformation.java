/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.comparison.icp;

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
    
    public ICPTransformation(Vector3f translation, float scaleFactor, Quaternion rotation, float meanD){
        this.translation = translation;
        this.scaleFactor = scaleFactor;
        this.rotation = rotation;
        this.meanD = meanD;
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
