/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.symmetryplane;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Triangle {

    private Point3f a, b, c;
    
    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this(new Point3d(a), new Point3d(b), new Point3d(c));
    }

    public Triangle(Tuple3d a, Tuple3d b, Tuple3d c) {
        this.a = new Point3f(a);
        this.b = new Point3f(b);
        this.c = new Point3f(c);
    }

    public Point3f getA() {
        return a;
    }

    public void setA(Tuple3d a) {
        this.a = new Point3f(a);
    }

    public Point3f getB() {
        return b;
    }

    public void setB(Tuple3d b) {
        this.b = new Point3f(b);
    }

    public Point3f getC() {
        return c;
    }

    public void setC(Tuple3d c) {
        this.c = new Point3f(c);
    }
}
