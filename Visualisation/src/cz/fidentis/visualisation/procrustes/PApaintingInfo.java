/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.procrustes;

import cz.fidentis.comparison.procrustes.GPA;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<FacialPoint> facialPoints;
    private boolean customConnections = false;
    private Set<PointConnection> pointConnections = PointConnection.getDefaultPointConnections();
    private PointConnection newConnection = null;
    private boolean highlightSameTypePoints = true;
    private int indexOfSelectedModel = -2;
    private int indexOfHoveredModel = -2;
    private int indexOfHoveredPoint = -1;
    private boolean showPointInfo = false;

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

    public int getIndexOfHoveredPoint() {
        return indexOfHoveredPoint;
    }
    
    public void setIndexOfHoveredPoint(int indexOfHoveredPoint) {
        this.indexOfHoveredPoint = indexOfHoveredPoint;
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
    
    public List<FacialPoint> getFacialPoints() {
        return facialPoints;
    }
    
    public void setFacialPoints(List<FacialPoint> facialPoints) {
        this.facialPoints = facialPoints;
}
    
    public void connectPoint() {
        Integer typeSelected = indexOfSelectedPoint;
        if (newConnection == null) {
            newConnection = new PointConnection(typeSelected);
            newConnection.setConfiguration(type == 2 ? indexOfSelectedModel : -1);
        } else if (type == 2 && !newConnection.isSameConfig(indexOfSelectedModel)) {
            newConnection = null;
        } else {
            newConnection.setEnd(typeSelected);
            pointConnections.add(newConnection);
            newConnection = null;
            indexOfSelectedPoint = -1;
        }
    }
      
    public void deleteConnection(PointConnection pc) {
        if (pointConnections.contains(pc)) {
            pointConnections.remove(pc);
        }
    }
    
    public void deleteNewConnection() {
        newConnection = null;
    }
    
    public Set<PointConnection> getPointConnections() {
        return pointConnections;
    }
    
    public void clearPointConnections() {
        pointConnections.clear();
    }
    
    public void setDefaultPointConnections() {
        pointConnections = PointConnection.getDefaultPointConnections();
    }

    public boolean isCustomConnections() {
        return customConnections;
    }

    public void setCustomConnections(boolean customCylinders) {
        this.customConnections = customCylinders;
    }

    public boolean isHighlightSameTypePoints() {
        return highlightSameTypePoints;
    }

    public void setHighlightSameTypePoints(boolean highlightSameTypePoints) {
        this.highlightSameTypePoints = highlightSameTypePoints;
    }

    public int getIndexOfSelectedModel() {
        return indexOfSelectedModel;
    }

    public void setIndexOfSelectedModel(int indexOfSelectedModel) {
        this.indexOfSelectedModel = indexOfSelectedModel;
    }
    
    public void setIndexOfHoveredModel(int indexOfHoveredModel) {
        this.indexOfHoveredModel = indexOfHoveredModel;
    }

    public int getIndexOfHoveredModel() {
        return indexOfHoveredModel;
    }

    public void setShowPointInfo(boolean selected) {
        this.showPointInfo = selected;
    }

    public boolean isShowPointInfo() {
        return showPointInfo;
    }
}
