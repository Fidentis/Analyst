/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.symmetryplane;

import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Triangle {

    private Vector3f a, b, c;
//    public int Id;
//    public SceneObject ReferenceObject; //reference to the object

    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Vector3f getA() {
        return a;
    }

    public void setA(Vector3f a) {
        this.a = a;
    }

    public Vector3f getB() {
        return b;
    }

    public void setB(Vector3f b) {
        this.b = b;
    }

    public Vector3f getC() {
        return c;
    }

    public void setC(Vector3f c) {
        this.c = c;
    }
    
    

}
