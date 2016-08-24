package cz.fidentis.controller;

import cz.fidentis.*;
import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.comparison.RegistrationMethod;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.controller.ProjectTree.Node;
import cz.fidentis.controller.data.ColormapData;
import cz.fidentis.controller.data.CrosscutData;
import cz.fidentis.controller.data.VectorsData;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import cz.fidentis.visualisation.ColorScheme;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.vecmath.Vector3f;

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
    
    private List<List<ICPTransformation>> trans;
    
    private boolean showPointInfo = true;   //whether to show description of the feature points
    private Color pointColor = Color.red;   //color of displayed feature points
    private int valuesTypeIndex = 0;        //relative, absolute
    private int metricTypeIndex = 0;        //RMS, min, max etc.
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
    
    private boolean continueComparison = false;
    private boolean firstCreated = true;
    
    private VisualizationType visualization;   

    private VectorsData vectorsViz = new VectorsData();
    private CrosscutData crosscutViz = new CrosscutData();
    private ColormapData colormapViz = new ColormapData();

    public VectorsData getVectorsViz() {
        return vectorsViz;
    }

    public void setVectorsViz(VectorsData vectorsViz) {
        this.vectorsViz = vectorsViz;
    }

    public CrosscutData getCrosscutViz() {
        return crosscutViz;
    }

    public void setCrosscutViz(CrosscutData crosscutViz) {
        this.crosscutViz = crosscutViz;
    }

    public ColormapData getColormapViz() {
        return colormapViz;
    }

    public void setColormapViz(ColormapData colormapViz) {
        this.colormapViz = colormapViz;
    }
    

    public boolean isShowPlane() {
        return crosscutViz.isShowPlane();
    }

    public void setShowPlane(boolean showPlane) {
        crosscutViz.setShowPlane(showPlane);
    }
    

    public VisualizationType getVisualization() {
        return visualization;
    }

    public void setVisualization(VisualizationType visualization) {
        this.visualization = visualization;
    }

    public int getCrossCutPlaneIndex() {
        return crosscutViz.getCrossCutPlaneIndex();
    }

    public void setCrossCutPlaneIndex(int crossCutPlaneIndex) {
        crosscutViz.setCrossCutPlaneIndex(crossCutPlaneIndex);
    }

    public Vector3f getArbitraryPlanePos() {
        return crosscutViz.getArbitraryPlanePos();
    }

    public void setArbitraryPlanePos(Vector3f arbitraryPlanePos) {
        crosscutViz.setArbitraryPlanePos(arbitraryPlanePos);
    }

    public Vector3f getPlanePosition() {
        return crosscutViz.getPlanePosition();
    }

    public void setPlanePosition(Vector3f planePosition) {
        crosscutViz.setPlanePosition(planePosition);
    }

    public int getCrosscutSize() {
        return crosscutViz.getCrosscutSize();
    }

    public void setCrosscutSize(int crosscutSize) {
        crosscutViz.setCrosscutSize(crosscutSize);
    }

    public int getCrosscutThickness() {
        return crosscutViz.getCrosscutThickness();
    }

    public void setCrosscutThickness(int crosscutThickness) {
        crosscutViz.setCrosscutThickness(crosscutThickness);
    }

    public Color getCrosscutColor() {
        return crosscutViz.getCrosscutColor();
    }

    public void setCrosscutColor(Color crosscutColor) {
        crosscutViz.setCrosscutColor(crosscutColor);
    }

    public boolean isHighlightCuts() {
        return crosscutViz.isHighlightCuts();
    }

    public void setHighlightCuts(boolean highlightCuts) {
        crosscutViz.setHighlightCuts(highlightCuts);
    }

    public boolean isShowVectors() {
        return crosscutViz.isShowVector();
    }

    public void setShowVectors(boolean showVectors) {
        crosscutViz.setShowVector(showVectors);
    }

    public boolean isAllCuts() {
        return crosscutViz.isAllCuts();
    }

    public void setAllCuts(boolean allCuts) {
        crosscutViz.setAllCuts(allCuts);
    }

    public boolean isSamplingRays() {
        return crosscutViz.isSamplingRays();
    }

    public void setSamplingRays(boolean samplingRays) {
        crosscutViz.setSamplingRays(samplingRays);
    }

    public int getVectorDensity() {
        return vectorsViz.getVectorDensity();
    }

    public void setVectorDensity(int vectorDensity) {
        vectorsViz.setVectorDensity(vectorDensity);
    }

    public int getVectorLength() {
        return vectorsViz.getVectorLength();
    }

    public void setVectorLength(int vectorLength) {
        vectorsViz.setVectorLength(vectorLength);
    }

    public int getCylinderRadius() {
        return vectorsViz.getCylinderRadius();
    }

    public void setCylinderRadius(int cylinderRadius) {
        vectorsViz.setCylinderRadius(cylinderRadius);
    }

    public ColorScheme getUsedColorScheme() {
        return colormapViz.getUsedColorScheme();
    }

    public void setUsedColorScheme(ColorScheme usedColorScheme) {
        colormapViz.setUsedColorScheme(usedColorScheme);
    }

    public boolean isContinueComparison() {
        return continueComparison;
    }

    public void setContinueComparison(boolean continueComparison) {
        this.continueComparison = continueComparison;
    }

    public boolean isFirstCreated() {
        return firstCreated;
    }

    public void setFirstCreated(boolean firstCreated) {
        this.firstCreated = firstCreated;
    }

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

    public List<ICPTransformation> getTrans(int i) {
        return trans.get(i);
    }
    
    public void setTransSize(int i){
        trans = new ArrayList<>(i);
        for(int j = 0; j < i; j++){
            trans.add(null);
        }
    }

    public void addTrans(List<ICPTransformation> trans, int i) {
        this.trans.set(i,trans);
    }
    
    public void clearTrans(){
        this.trans.clear();
    }
    
    public List<List<ICPTransformation>> getTrans(){
        return this.trans;
    }
    
    public void setTrans(List<List<ICPTransformation>> trans){
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

    public int getHausdorfMaxTreshold() {
        return colormapViz.getHausdorfMaxTreshold();
    }

    public void setHausdorfMaxTreshold(int hausdorfMaxTreshold) {
        colormapViz.setHausdorfMaxTreshold(hausdorfMaxTreshold);
    }

    public int getHausdorfMinTreshold() {
        return colormapViz.getHausdorfMinTreshold();
    }

    public void setHausdorfMinTreshold(int hausdorfMinTreshold) {
        colormapViz.setHausdorfMinTreshold(hausdorfMinTreshold);
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
