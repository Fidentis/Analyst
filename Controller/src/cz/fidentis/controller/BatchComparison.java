package cz.fidentis.controller;

import cz.fidentis.*;
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
public class BatchComparison {
    private String name = new String();         //name of view(??)
    private HDpainting HDP;                     //object to draw surface comparison results (color map currently through shaders)
    private HDpaintingInfo HDinfo;              //information needed for visualization of results
    private List<Float> hd;                 //distances used for visualization
    private List<Float> sortedHd;           //sorted values of hd
    private List<File> registrationResults; //URLs to models registered using ICP
    private File hdCSVresults;          //URL to directory where computed numerical results in csv format are stored in
    private ArrayList<ArrayList<Float>> hdVisualResults;    //visual results, num of inner array == num of models, num of outer arrays == num of vertices of avgFace
    private ArrayList<File> models = new ArrayList<>();     //URLs to models stored on disk
    private ArrayList<Model> preRegiteredModels;            //loaded preregistered models, used for feature point computation
    private HashMap<String ,List<FacialPoint>> facialPoints = new HashMap<>();  //feature points associated with their model
    private int state = 1; // 1 - registration, 2 - registration results, 3 - comparison, 4/ results
    
    private List<ICPTransformation> trans;
    
    private boolean showPointInfo = true;   //whether to show description of the feature points
    private Color pointColor = Color.red;   //color of displayed feature points
    private Color hdColor1 = Color.green;   //redundant? take color from HDinfo instead eventually?
    private Color hdColor2 = Color.red;
    private int valuesTypeIndex = 0;        //relative, absolute
    private int metricTypeIndex = 0;        //RMS, min, max etc.
    private int hausdorfTreshold = 100;     //threshold value in % (HDPainting info contains actual computed distance threshold)
    private boolean fpScaling;          //whether feature points configuration were scaled
    private int fpTreshold = 30;        //threshold for feature points (still no clue what it's for)
    private int fpSize = 20;            //size of displayed feature points
    private float ICPerrorRate = 0.05f; //error rate used in ICP algorithm --used when editing registration criteria
    private int ICPmaxIteration = 10;      //max number of iteration used in ICP algorithm -- used when editing registration criteria
    private int ICPnumberOfHeads = 3;   //number of average faces created -- used when editing registration criteria
    private int templateIndex = 0;      //index of mesh picked as a base for average face
    private boolean useSymmetry;
    private RegistrationMethod RegMethod ;  //used registration method
    private ComparisonMethod CompareMethod; //used comparison method
    private ICPmetric icpMetric;        //ICP metric used for registration
    private int fpDistance;         //distance factor for feature points
    
    private int method;         //undersampling method
    private int type;           //undersampling type    
    private float value;          //undersampling value


    private boolean compareButtonEnabled = true;    //comparison button enabled means all computation of registration were finished (all threads are done)
    private boolean registerButtonEnabled = true;   //whether registration button was enabled
    private String numericalResults;            //table value of computed results
    private String distanceToMeanConfiguration; //table value of distance to mean configuration for GPA
    private Model averageFace;      //computed average face
    private boolean scaleEnabled;   //whether scaling was enabled with ICP algorithm

    
    private Node node;
    private Node node_models;
    private Node node_registered;
    private Node node_average;
    private Node node_result;
    private final ResourceBundle strings = ResourceBundle.getBundle("cz.fidentis.controller.Bundle");

    public ArrayList<Model> getPreregiteredModels() {
        return preRegiteredModels;
    }

    public void setPreregiteredModels(ArrayList<Model> regiteredModels) {
        this.preRegiteredModels = regiteredModels;
    }  

    public HDpaintingInfo getHDinfo() {
        return HDinfo;
    }

    public void setHDinfo(HDpaintingInfo HDinfo) {
        this.HDinfo = HDinfo;
    }

    public ICPTransformation getTrans(int i) {
        return trans.get(i);
    }
    
    public void setTransSize(int i){
        trans = new ArrayList<>(i);
        for(int j = 0; j < i; j++){
            trans.add(null);
        }
    }

    public void addTrans(ICPTransformation trans, int i) {
        this.trans.set(i,trans);
    }
    
    public void clearTrans(){
        this.trans.clear();
    }
    
    public List<ICPTransformation> getTrans(){
        return this.trans;
    }
    
    public void setTrans(List<ICPTransformation> trans){
        this.trans = trans;
    }
    
    public String getNumericalResults() {
        return numericalResults;
    }

    public void setNumericalResults(String numericalResults) {
        this.numericalResults = numericalResults;
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

    public List<Float> getSortedHd() {
        return sortedHd;
    }

    public void setSortedHd(List<Float> sortedHd) {
        this.sortedHd = sortedHd;
    }

    public boolean isRegisterButtonEnabled() {
        return registerButtonEnabled;
    }

    public void setRegisterButtonEnabled(boolean registerButtonEnabled) {
        this.registerButtonEnabled = registerButtonEnabled;
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

    public String getDistanceToMeanConfiguration() {
        return distanceToMeanConfiguration;
    }

    public void setDistanceToMeanConfiguration(String distanceToMeanConfiguration) {
        this.distanceToMeanConfiguration = distanceToMeanConfiguration;
    }

    public File getHdCSVresults() {
        return hdCSVresults;
    }

    public void setHdCSVresults(File hdCSVresults) {
        this.hdCSVresults = hdCSVresults;
    }

    
    public ArrayList<ArrayList<Float>> getHdVisualResults() {
        return hdVisualResults;
    }

    public void setHdVisualResults(ArrayList<ArrayList<Float>> hdVisualResults) {
        this.hdVisualResults = hdVisualResults;
    }
    

    public int getICPnumberOfHeads() {
        return ICPnumberOfHeads;
    }

    public Model getAverageFace() {
        return averageFace;
    }

    public void setAverageFace(Model averageFace) {
        this.averageFace = averageFace;
    }
    

    public void setICPnumberOfHeads(int ICPnumberOfHeads) {
        this.ICPnumberOfHeads = ICPnumberOfHeads;
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

    public boolean isUseSymmetry() {
        return useSymmetry;
    }

    public void setUseSymmetry(boolean useSymmetry) {
        this.useSymmetry = useSymmetry;
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
    
    
    public BatchComparison() {}

    /**
     *
     * @return Model that is displayed.
     */
    public List<File> getModels() {
        return models;
    }
    
    public File getModel(String name){
        for (File model : models) {
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }
    public File getModel(int i) {
        return models.get(i);
    }

    public void addModel(File model){
        models.add(model);
        if(node_models == null) {
            node_models = node.addChild(strings.getString("tree.node.comparedModels"));
        }
        node_models.addChild(model);
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

    public List<File> getRegistrationResults() {
        return registrationResults;
    }

    public void setRegistrationResults(List<File> registrationResults) {
        if(node_registered == null) {
            node_registered = node.addChild(strings.getString("tree.node.registeredModels"));
        } else {
            node_registered.removeChildren();
        }
        this.registrationResults = registrationResults;
        for(File mod : registrationResults) {
            node_registered.addChild(mod);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void setScaleEnabled(boolean selected) {
        scaleEnabled = selected;
    }
    
    public boolean getScaleEnabled() {
        return scaleEnabled;
    }
   
}
