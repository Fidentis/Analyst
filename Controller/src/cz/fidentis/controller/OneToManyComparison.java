package cz.fidentis.controller;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.comparison.RegistrationMethod;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.controller.ProjectTree.Node;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Katka
 */
public class OneToManyComparison {
    private String name = new String();             //name of view(??)
    private HDpainting HDP;                         //object to draw surface comparison results (color map currently through shaders)
    private HDpaintingInfo hdPaintingInfo;          //information needed for visualization of results
    private List<Float> hd;                         //relative distances of surface comparison (HdpaintingInfo contains currently displayed values) comparison avgFace to mainFace
    private List<Float> sortedHdRel;                //sorted results of hausdorff distance, relative values (for thresholding)
    private List<Float> sortedHdAbs;                //sorted results of hausdorff distance, absolute values (for thresholding)
    private List<ArrayList<Float>> numResults;      //computed numerical results (comparison of all loaded face to mainFace)
    private ArrayList<File> models;                 //URLs to models stored on the disk
    private List<File> registeredModels;            //models after registration, stored on the disk
    private Model primaryModel;                     //mainFace
    private Model avgFace;                          //avgFace
    private ArrayList<Model> preRegiteredModels;            //only used in feature points calculation
    private HashMap<String ,List<FacialPoint>> facialPoints = new HashMap<String, List<FacialPoint>>();         //feature points associated with the model of given name
    private int state = 1; // 1 - registration, 2 - registration results, 3 - comparison
    private Node node;
    private Node node_primaryModel;
    private Node node_models;
    private Node node_registered;
    private Node node_result;
    private ResourceBundle strings = ResourceBundle.getBundle("cz.fidentis.controller.Bundle");
      
    private boolean showPointInfo = true;           //whether to show description of the feature points
    private Color pointColor = Color.red;           //color for displayed feature points
    private Color hdColor1 = Color.green;           //redundant? take color from HDinfo instead eventually?
    private Color hdColor2 = Color.red;
    private int hausdorfTreshold = 100;            //threshold value in % (HDPainting info contains actual computed distance thresholded)
    private boolean fpScaling;                     //whether feature points are scaled or not
    private int fpTreshold = 30;                   //threshold for feature points (still no clue what it is for)
    private int fpSize = 20;                       //size of displayed feature points
    private float ICPerrorRate = 0.05f;            //used error rate during alignment for ICP -- used when editing registration criteria
    private int ICPmaxIteration = 10;               //used number of iteration for ICP -- used when editing registration criteria
    private int templateIndex = 0;                 //index of mesh chosen to be a base for the avgFace
    private RegistrationMethod RegMethod;           //registration method used
    private ComparisonMethod CompareMethod;         //comparison method used
    private ICPmetric icpMetric = null;                    //ICP metric used for registration
    private int fpDistance;                         //distance factor for feature points
    private boolean compareButtonEnabled = true;  //comparison button enabled means all computation of registration were finished (all threads are done)
    private String numericalResults;            //table format of numerical results
    private boolean scaleEnabled;               //was scaling for ICp enabled
    private int valuesTypeIndex = 0;            //relative or absolute
    private int metricTypeIndex = 0;            //RMS, min, max, etc.
    
    private int method;             //undersampling method
    private int type;               //undersampling type
    private float value;              //undersampling value

    
    
    public ArrayList<Model> getPreregiteredModels() {
        return preRegiteredModels;
    }

    public void setPreregiteredModels(ArrayList<Model> regiteredModels) {
        this.preRegiteredModels = regiteredModels;
    }  
    
    
    public String getNumericalResults() {
        return numericalResults;
    }

    public void setNumericalResults(String numericalResults) {
        this.numericalResults = numericalResults;
    }

    public List<Float> getSortedHdRel() {
        return sortedHdRel;
    }

    public void setSortedHdRel(List<Float> sortedHdRel) {
        this.sortedHdRel = sortedHdRel;
    }

    public List<Float> getSortedHdAbs() {
        return sortedHdAbs;
    }

    public void setSortedHdAbs(List<Float> sortedHdAbs) {
        this.sortedHdAbs = sortedHdAbs;
    }

    public HDpaintingInfo getHdPaintingInfo() {
        return hdPaintingInfo;
    }

    public void setHdPaintingInfo(HDpaintingInfo hdPaintingInfo) {
        this.hdPaintingInfo = hdPaintingInfo;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public HashMap<String, List<FacialPoint>> getFacialPoints() {
        return facialPoints;
    }

    public void setFacialPoints(HashMap<String, List<FacialPoint>> facialPoints) {
        this.facialPoints = facialPoints;
    }

    public boolean isShowPointInfo() {
        return showPointInfo;
    }

    public void setShowPointInfo(boolean showPointInfo) {
        this.showPointInfo = showPointInfo;
    }

    public Color getPointColor() {
        return pointColor;
    }

    public void setPointColor(Color pointColor) {
        this.pointColor = pointColor;
    }

    public Color getHdColor1() {
        return hdColor1;
    }

    public void setHdColor1(Color hdColor1) {
        this.hdColor1 = hdColor1;
    }

    public Color getHdColor2() {
        return hdColor2;
    }

    public void setHdColor2(Color hdColor2) {
        this.hdColor2 = hdColor2;
    }

    public int getHausdorfTreshold() {
        return hausdorfTreshold;
    }

    public void setHausdorfTreshold(int hausdorfTreshold) {
        this.hausdorfTreshold = hausdorfTreshold;
    }

    public List<ArrayList<Float>> getNumResults() {
        return numResults;
    }

    public void setNumResults(List<ArrayList<Float>> numResults) {
        this.numResults = numResults;
    }
    

    public boolean isFpScaling() {
        return fpScaling;
    }

    public void setFpScaling(boolean fpScaling) {
        this.fpScaling = fpScaling;
    }

    public int getFpTreshold() {
        return fpTreshold;
    }

    public void setFpTreshold(int fpTreshold) {
        this.fpTreshold = fpTreshold;
    }

    public int getFpSize() {
        return fpSize;
    }

    public void setFpSize(int fpSize) {
        this.fpSize = fpSize;
    }

    public float getICPerrorRate() {
        return ICPerrorRate;
    }

    public void setICPerrorRate(float ICPerrorRate) {
        this.ICPerrorRate = ICPerrorRate;
    }

    public int getICPmaxIteration() {
        return ICPmaxIteration;
    }

    public void setICPmaxIteration(int ICPmaxIteration) {
        this.ICPmaxIteration = ICPmaxIteration;
    }

    public RegistrationMethod getRegistrationMethod() {
        return RegMethod;
    }

    public void setRegistrationMethod(RegistrationMethod RegMethod) {
        this.RegMethod = RegMethod;
    }

    public ICPmetric getIcpMetric() {
        return icpMetric;
    }

    public void setIcpMetric(ICPmetric icpMetric) {
        this.icpMetric = icpMetric;
    }

    public ComparisonMethod getComparisonMethod() {
        return CompareMethod;
    }

    public void setComparisonMethod(ComparisonMethod CompareMethod) {
        this.CompareMethod = CompareMethod;
    }

    public int getValuesTypeIndex() {
        return valuesTypeIndex;
    }

    public void setValuesTypeIndex(int valuesTypeIndex) {
        this.valuesTypeIndex = valuesTypeIndex;
    }

    public int getMetricTypeIndex() {
        return metricTypeIndex;
    }

    public void setMetricTypeIndex(int metricTypeIndex) {
        this.metricTypeIndex = metricTypeIndex;
    }

    public int getTemplateIndex() {
        return templateIndex;
    }

    public void setTemplateIndex(int templateIndex) {
        this.templateIndex = templateIndex;
    }
   

    public int getFpDistance() {
        return fpDistance;
    }

    public void setFpDistance(int fpDistance) {
        this.fpDistance = fpDistance;
    }

    public boolean isCompareButtonEnabled() {
        return compareButtonEnabled;
    }

    public void setCompareButtonEnabled(boolean compareButtonEnabled) {
        this.compareButtonEnabled = compareButtonEnabled;
    }
      

    public Model getPrimaryModel() {
        return primaryModel;
    }

    public void setPrimaryModel(Model primaryModel) {
        if(node_primaryModel == null) {
            node_primaryModel = node.addChild(strings.getString("tree.node.primaryModel"));
        } else {
            node_primaryModel.removeChildren();
        }
        this.primaryModel = primaryModel;
        this.node_primaryModel.addChild(primaryModel.getFile());
    }
   
    
    
    public int getState() {
        return state;
    }

     
    public void setState(int state) {
        this.state = state;
        if (state >= 3) {
            if (this.node_result == null) {
                this.node_result = this.node.addChild(strings.getString("tree.node.results"));
            }
        } else if (this.node_result != null) {
            this.node.removeChild(this.node.getChildren().indexOf(this.node_result));
        }
    }
    

    public OneToManyComparison() {
        models = new ArrayList<File>();
    }
  


    /**
     *
     * @return Model that is displayed.
     */
    public List<File> getModels() {
        return models;
    }
    
    public File getModel(String name){
        for (int i = 0; i< models.size();i++){
            if(models.get(i).getName().equals(name)){
                return models.get(i);
            }
        }
        return null;
    }
    public File getModel(int i) {
        return models.get(i);
    }
    
    public void removeModel(int index) {
        if(index < 0 || index >= models.size()) return;
        models.remove(index);
        node_models.removeChild(index);
        if(models.isEmpty()) {
            node.removeChild(node.getChildren().indexOf(node_models));
            node_models = null;
        }
    }

    public void addModel(File model){
        models.add(model);
        if(node_models == null) {
            node_models = node.addChild(strings.getString("tree.node.comparedModels"));
        }
        node_models.addChild(model);
    }

    public List<File> getRegisteredModels() {
        return registeredModels;
    }

    public void setRegisteredModels(List<File> registeredModels) {
        if(node_registered == null) {
            node_registered = node.addChild(strings.getString("tree.node.registeredModels"));
        } else {
            node_registered.removeChildren();
        }
        this.registeredModels = registeredModels;
        for(File mod : registeredModels) {
            node_registered.addChild(mod);
        }
    }
    
    
    public void addFacialPoints(String model, List<FacialPoint> FP){
        facialPoints.put(model, FP);
        
    }
    
    public void clearFacialPoints(){
        facialPoints.clear();
    }
    
    public List<FacialPoint> getFacialPoints(String model){
        return facialPoints.get(model);
        
    }
    
      public void setNode(ProjectTree.Node node){
        this.node = node;
    }

    /**
     *
     * @return Name of the View.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name of the View.
     */
    public void setName(String name) {
        this.name = name;
    }


    public HDpainting getHDP() {
        return HDP;
    }

    public void setHDP(HDpainting HDP) {
        this.HDP = HDP;
    }

    public List<Float> getHd() {
        return hd;
    }

    public void setHd(List<Float> hd) {
        this.hd = hd;
    }

    public void setScaleEnabled(boolean selected) {
        scaleEnabled = selected;
    }
    
    public boolean getScaleEnabled() {
        return scaleEnabled;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public Model getAvgFace() {
        return avgFace;
    }

    public void setAvgFace(Model avgFace) {
        this.avgFace = avgFace;
    }
    
    
    


}
