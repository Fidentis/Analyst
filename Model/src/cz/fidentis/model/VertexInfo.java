/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.model;

import javax.vecmath.Vector3f;

/**
 *
 * @author Katka
 */
public class VertexInfo {
   private Vector3f sampleVertex;
   private Vector3f sampleNormal;
   private Vector3f samplePrincipalCurvature;
   private Vector3f sampleSecondaryCurvature;

    public Vector3f getSampleVertex() {
        return sampleVertex;
    }

    public void setSampleVertex(Vector3f sampleVertex) {
        this.sampleVertex = sampleVertex;
    }

    public Vector3f getSampleNormal() {
        return sampleNormal;
    }

    public void setSampleNormal(Vector3f sampleNormal) {
        this.sampleNormal = sampleNormal;
        this.sampleNormal.normalize();
    }

    public Vector3f getSamplePrincipalCurvature() {
        return samplePrincipalCurvature;
    }

    public void setSamplePrincipalCurvature(Vector3f samplePrincipalCurvature) {
        this.samplePrincipalCurvature = samplePrincipalCurvature;
        this.samplePrincipalCurvature.normalize();
    }

    public Vector3f getSampleSecondaryCurvature() {
        return sampleSecondaryCurvature;
    }

    public void setSampleSecondaryCurvature(Vector3f sampleSecondaryCurvature) {
        this.sampleSecondaryCurvature = sampleSecondaryCurvature;
        this.sampleSecondaryCurvature.normalize();
    }
  
   
   
   
}
