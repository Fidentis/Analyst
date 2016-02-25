/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.procrustes;

import cz.fidentis.comparison.procrustes.GPA;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;

/**
 *
 * 
 */
public class PApaintingInfo {
    private int type = 0;
    private GPA gpa;
    private float enhance = 0;
    private float pointSize = 2;
    private ProcrustesAnalysis pa;
    private ProcrustesAnalysis pa2;
    private int indexOfSelectedPoint = -1;
    private int indexOfSelectedConfig = -1;
    private float facialPointRadius = 2;

    public PApaintingInfo(GPA gpa, ProcrustesAnalysis pa, int type) {
        this.gpa = gpa;
        this.pa = pa;
        this.type = type;
    }

    public ProcrustesAnalysis getPa2() {
        return pa2;
    }

    public void setPa2(ProcrustesAnalysis pa2) {
        this.pa2 = pa2;
    }
    
    
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GPA getGpa() {
        return gpa;
    }

    public void setGpa(GPA gpa) {
        this.gpa = gpa;
    }

    public float getEnhance() {
        return enhance;
    }

    public void setEnhance(float enhance) {
        this.enhance = enhance / 100f;
    }

    public float getPointSize() {
        return pointSize;
    }

    public void setPointSize(float pointSize) {
        this.pointSize = pointSize / 25f;
    }

    public ProcrustesAnalysis getPa() {
        return pa;
    }

    public void setPa(ProcrustesAnalysis pa) {
        this.pa = pa;
    }

    public int getIndexOfSelectedPoint() {
        return indexOfSelectedPoint;
    }

    public void setIndexOfSelectedPoint(int indexOfSelectedPoint) {
        this.indexOfSelectedPoint = indexOfSelectedPoint;
    }

    public int getIndexOfSelectedConfig() {
        return indexOfSelectedConfig;
    }

    public void setIndexOfSelectedConfig(int indexOfSelectedConfig) {
        this.indexOfSelectedConfig = indexOfSelectedConfig;
    }

    public float getFacialPointRadius() {
        return facialPointRadius;
    }

    public void setFacialPointRadius(float facialPointRadius) {
        this.facialPointRadius = facialPointRadius;
    }
    
    
}
