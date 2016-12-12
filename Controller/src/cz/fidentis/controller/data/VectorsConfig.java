/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller.data;

/**
 * Class holding configuration for vectors visualization
 * 
 * @author xferkova
 */
public class VectorsConfig {
    private int vectorDensity;
    private int vectorLength;
    private int cylinderRadius;

    public VectorsConfig() {
    }

    public int getVectorDensity() {
        return vectorDensity;
    }

    public void setVectorDensity(int vectorDensity) {
        this.vectorDensity = vectorDensity;
    }

    public int getVectorLength() {
        return vectorLength;
    }

    public void setVectorLength(int vectorLength) {
        this.vectorLength = vectorLength;
    }

    public int getCylinderRadius() {
        return cylinderRadius;
    }

    public void setCylinderRadius(int cylinderRadius) {
        this.cylinderRadius = cylinderRadius;
    }
}
