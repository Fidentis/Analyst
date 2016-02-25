/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.symmetryplane;

/**
 *
 * @author Galvanizze
 */
public class PlaneEquation {

    private float A;
    private float B;
    private float C;
    private float D;
    private float MAX;

    public PlaneEquation(Triangle plane) {
        float max;

        //A,B,C,D from Ax+BgetY()+Cz+d=0 (plane equation)
        this.A = plane.getA().getY()
                * (plane.getB().getZ() - plane.getC().getZ()) + plane.getB().getY()
                * (plane.getC().getZ() - plane.getA().getZ()) + plane.getC().getY()
                * (plane.getA().getZ() - plane.getB().getZ());

        max = Math.abs(this.A) > 1 ? Math.abs(this.A) : 1;

        this.B = plane.getA().getZ()
                * (plane.getB().getX() - plane.getC().getX()) + plane.getB().getZ()
                * (plane.getC().getX() - plane.getA().getX()) + plane.getC().getZ()
                * (plane.getA().getX() - plane.getB().getX());

        if (Math.abs(this.B) > max) {
            max = Math.abs(this.B);
        }

        this.C = plane.getA().getX()
                * (plane.getB().getY() - plane.getC().getY()) + plane.getB().getX()
                * (plane.getC().getY() - plane.getA().getY()) + plane.getC().getX()
                * (plane.getA().getY() - plane.getB().getY());

        if (Math.abs(this.C) > max) {
            max = Math.abs(this.C);
        }

        this.D = -plane.getA().getX()
                * (plane.getB().getY() * plane.getC().getZ() - plane.getC().getY()
                * plane.getB().getZ()) - plane.getB().getX()
                * (plane.getC().getY() * plane.getA().getZ() - plane.getA().getY()
                * plane.getC().getZ()) - plane.getC().getX()
                * (plane.getA().getY() * plane.getB().getZ() - plane.getB().getY()
                * plane.getA().getZ());

        if (Math.abs(this.D) > max) {
            max = Math.abs(this.D);
        }

        this.MAX = max; //i'm targeting to minimize calculating errors

    }

    public float getA() {
        return A;
    }

    public void setA(float A) {
        this.A = A;
    }

    public float getB() {
        return B;
    }

    public void setB(float B) {
        this.B = B;
    }

    public float getC() {
        return C;
    }

    public void setC(float C) {
        this.C = C;
    }

    public float getD() {
        return D;
    }

    public void setD(float D) {
        this.D = D;
    }

    public float getMAX() {
        return MAX;
    }

    public void setMAX(float MAX) {
        this.MAX = MAX;
    }
    
}
