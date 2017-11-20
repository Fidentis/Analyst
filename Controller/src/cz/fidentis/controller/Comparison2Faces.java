package cz.fidentis.controller;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.comparison.RegistrationMethod;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.controller.ProjectTree.Node;
import cz.fidentis.controller.data.ColormapConfig;
import cz.fidentis.controller.data.TransparencyConfig;
import cz.fidentis.controller.data.VectorsConfig;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import cz.fidentis.visualisation.ColorScheme;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Katka
 */
public class Comparison2Faces {
    private String name = new String();                 //name of the view (??)
    private HDpainting HDP;                             //object to draw surface comparison (color map currently through shader)
    private HDpaintingInfo hdPaintingInfo;              //information needed for visualization of results
    private KdTree mainFace;            //main model as kd-tree
    private List<Float> hd;             //relative distances of surface coparison (HDpaintingInfo contains currently displayed values)
    private List<Float> sortedHdValuesRelative;         //sorted results of hausdorff distance, relative values (for thresholding)
    private List<Float> sortedHdValuesAbs;              //sorted results of hausdorff distance, absolute values (for thresholding)
    private Model model1;                       //primary model
    private Model model2;                       //secondary model
    private Node node;
    private Node node_1;
    private Node node_2;
    private ResourceBundle strings = ResourceBundle.getBundle("cz.fidentis.controller.Bundle");
    
    private List<ICPTransformation> compFTransformations;
    
    private ArrayList<List<FacialPoint>> databaseFacialPoints = new ArrayList<List<FacialPoint>>();         //database of FPs is currently not used within the application
    private List<FacialPoint> mainFp = new ArrayList<>();
    private List<FacialPoint> secondaryFp = new ArrayList<>();
    private List<FacialPoint> originalMainFp;
    private List<FacialPoint> originalSecondaryFp;
    private boolean editPoints;         //DON'T need to save in project
    private int state = 1; // 1 - registration, 2 - registration results, 3 - comparison
    private boolean continueComparison = false;
    
    private boolean showPointInfo = true;           //whether to show description of the feature points
    private Color pointColor = Color.red;           //color for displayed feature points
    
    private float lowerHDTreshold;          //safes lower threhsold for HD, from which the numerical results are currently computed
    private float upperHDTreshold;           //safes upper threhsold for HD, from which the numerical results are currently computed
    private boolean fpScaling;          //whether feature points are scaled or not  
    private int useDatabase;        //0 - dont use, 1 - default, 2 - data file, 3 - create  -- database currently not used in software
    private ArrayList<File> databaseFiles;      //database of feature points stored on disk -- database currently not used in software 
    private int fpTreshold = 30;            //threshold for feature points (still no clue what it is for)
    private int fpSize = 20;                //size of displayed feature points
    private float ICPerrorRate = 0.05f;     //used error rate during for ICP algorithm -- used when editing registration criteria
    private int ICPmaxIteration = 15;       //used number of iterations for ICP -- used when editing registration criteria
    private RegistrationMethod RegMethod;   //registration method used
    private ComparisonMethod CompareMethod; //comparison method used
    private ICPmetric icpMetric;            //either vertex-to-vert or vertex-to-mesh
    private boolean useSymmetry;
    private int fpDistance;             //distance factor for feature points
    private boolean compareButtonEnabled = true;    //comparison button enabled means all computation of registration were finished (all threads are done)
    private String numericalResults;            //table format of numeric results
    private boolean scaleEnabled;       //was scale enabled while ICP was performed
    private ImageIcon modelIcon;        //thumbnail of original model
    private ImageIcon resultIcon;       //thumbnail of comparison (overlaid model or color map or what not)
    private int valuesTypeIndex = 0;            //relative or absolute
    
    private int method;         //undersampling method
    private int type;           //undersampling type
    private float value;          //undersampling value
    
    private boolean firstCreated = true;
    
    
    //comparison results
    private VisualizationType visualization;
    
    private TransparencyConfig transparencyViz = new TransparencyConfig();
    private VectorsConfig vectorsViz = new VectorsConfig();
    private ColormapConfig colormapViz = new ColormapConfig();
    
    

    public TransparencyConfig getTransparencyViz() {
        return transparencyViz;
    }

    public void setTransparencyViz(TransparencyConfig transparencyViz) {
        this.transparencyViz = transparencyViz;
    }

    public VectorsConfig getVectorsViz() {
        return vectorsViz;
    }

    public void setVectorsViz(VectorsConfig vectorsViz) {
        this.vectorsViz = vectorsViz;
    }

    public ColormapConfig getColormapViz() {
        return colormapViz;
    }

    public void setColormapViz(ColormapConfig colormapViz) {
        this.colormapViz = colormapViz;
    }
    
    public boolean isFirstCreated() {
        return firstCreated;
    }

    public void setFirstCreated(boolean firstCreated) {
        this.firstCreated = firstCreated;
    }

    public VisualizationType getVisualization() {
        return visualization;
    }

    public void setVisualization(VisualizationType visualization) {
        this.visualization = visualization;
    }

    public ColorScheme getColorScheme() {
        return colormapViz.getUsedColorScheme();
    }

    public void setColorScheme(ColorScheme colorScheme) {
        colormapViz.setUsedColorScheme(colorScheme);
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
       
    
    public int getValuesTypeIndex() {
        return valuesTypeIndex;
    }

    public void setValuesTypeIndex(int valuesTypeIndex) {
        this.valuesTypeIndex = valuesTypeIndex;
    }

    public List<ICPTransformation> getCompFTransformations() {
        return compFTransformations;
    }

    public void setCompFTransformations(List<ICPTransformation> compFTransformations) {
        this.compFTransformations = compFTransformations;
    }

    public HDpaintingInfo getHdPaintingInfo() {
        return hdPaintingInfo;
    }

    public void setHdPaintingInfo(HDpaintingInfo hdPaintingInfo) {
        this.hdPaintingInfo = hdPaintingInfo;
    }

    public boolean isIsPrimarySolid() {
        return transparencyViz.isIsPrimarySolid();
    }

    public void setIsPrimarySolid(boolean isPrimarySolid) {
        transparencyViz.setIsPrimarySolid(isPrimarySolid);
    }

    public boolean isIsSecondarySolid() {
        return transparencyViz.isIsSecondarySolid();
    }

    public void setIsSecondarySolid(boolean isSecondarySoldi) {
        transparencyViz.setIsSecondarySolid(isSecondarySoldi);
    }

    public boolean isInnerSurfaceSolid() {
        return transparencyViz.isInnerSurfaceSolid();
    }

    public void setInnerSurfaceSolid(boolean innerSurfaceSolid) {
        transparencyViz.setInnerSurfaceSolid(innerSurfaceSolid);
    }  

    public int getFogVersion() {
        return transparencyViz.getFogVersion();
    }

    public void setFogVersion(int fogVersion) {
        transparencyViz.setFogVersion(fogVersion);
    }

    public Color getFogColor() {
        return transparencyViz.getFogColor();
    }

    public void setFogColor(Color fogColor) {
        transparencyViz.setFogColor(fogColor);
    }

    public float getOverlayTransparency() {
        return transparencyViz.getOverlayTransparency();
    }

    public void setOverlayTransparency(float overlayTransparency) {
        transparencyViz.setOverlayTransparency(overlayTransparency);
    }

    public boolean isUseGlyphs() {
        return transparencyViz.isUseGlyphs();
    }

    public void setUseGlyphs(boolean useGlyphs) {
        transparencyViz.setUseGlyphs(useGlyphs);
    }

    public boolean isUseContours() {
        return transparencyViz.isUseContours();
    }

    public void setUseContours(boolean useContours) {
        transparencyViz.setUseContours(useContours);
    }

    public float getLowerHDTreshold() {
        return lowerHDTreshold;
    }

    public void setLowerHDTreshold(float lowerHDTreshold) {
        this.lowerHDTreshold = lowerHDTreshold;
    }

    public float getUpperHDTreshold() {
        return upperHDTreshold;
    }

    public void setUpperHDTreshold(float upperHDTreshold) {
        this.upperHDTreshold = upperHDTreshold;
    }
    
    
    public String getNumericalResults() {
        return numericalResults;
    }

    public void setNumericalResults(String numericalResults) {
        this.numericalResults = numericalResults;
    }

    public List<Float> getSortedHdValuesRelative() {
        return sortedHdValuesRelative;
    }

    public boolean isEditPoints() {
        return editPoints;
    }

    public void setEditPoints(boolean editPoints) {
        this.editPoints = editPoints;
    }

    public void setSortedHdValuesRelative(List<Float> sortedHdValuesRelative) {
        this.sortedHdValuesRelative = sortedHdValuesRelative;
    }

    public List<Float> getSortedHdValuesAbs() {
        return sortedHdValuesAbs;
    }

    public void setSortedHdValuesAbs(List<Float> sortedHdValuesAbs) {
        this.sortedHdValuesAbs = sortedHdValuesAbs;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public boolean isContinueComparison() {
        return continueComparison;
    }

    public void setContinueComparison(boolean continueComparison) {
        this.continueComparison = continueComparison;
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

    public List<FacialPoint> getOriginalMainFp() {
        return originalMainFp;
    }

    public void setOriginalMainFp(List<FacialPoint> originalMainFp) {
        this.originalMainFp = originalMainFp;
    }

    public List<FacialPoint> getOriginalSecondaryFp() {
        return originalSecondaryFp;
    }

    public void setOriginalSecondaryFp(List<FacialPoint> originalSecondaryFp) {
        this.originalSecondaryFp = originalSecondaryFp;
    }
    

    public List<FacialPoint> getMainFp() {
        return mainFp;
    }

    public void setMainFp(List<FacialPoint> mainFp) {
        this.mainFp.clear();
        this.mainFp.addAll(mainFp);
    }
    
    public void addMainFp(FacialPoint fp){
        this.mainFp.add(fp);
    }

    public List<FacialPoint> getSecondaryFp() {
        return secondaryFp;
    }

    public void setSecondaryFp(List<FacialPoint> secondaryFp) {
        this.secondaryFp.clear();
        this.secondaryFp.addAll(secondaryFp);
    }
    
    public void addSecondaryFp(FacialPoint fp){
        this.secondaryFp.add(fp);
    }
    
    public boolean isCompareButtonEnabled() {
        return compareButtonEnabled;
    }

    public void setCompareButtonEnabled(boolean compareButtonEnabled) {
        this.compareButtonEnabled = compareButtonEnabled;
    }

    public Color getPrimaryColor() {
        return transparencyViz.getPrimaryColor();
    }

    public void setPrimaryColor(Color primaryColor) {
        transparencyViz.setPrimaryColor(primaryColor);
    }

    public Color getSecondaryColor() {
        return transparencyViz.getSecondaryColor();
    }

    public void setSecondaryColor(Color secondaryColor) {
        transparencyViz.setSecondaryColor(secondaryColor);
    }
            

    /**
     *
     * @return Model that is displayed.
     */
    public Model getModel1() {
        return model1;
    }
    
    public Model getModel2() {
        return model2;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public KdTree getMainFace() {
        return mainFace;
    }

    public void setMainFace(KdTree mainFace) {
        this.mainFace = mainFace;
    }
    
    public void addFacialPoints(List<FacialPoint> points){
        databaseFacialPoints.add(points);
    }
    
    public ArrayList<List<FacialPoint>> getDatabasePoints(){
        return databaseFacialPoints;
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

    public void setModel1(Model model) {
        model1 = model;
        if(node_1 == null) {
            this.node_1 = this.node.addChild(strings.getString("tree.node.primaryModel"));
        } else {
            node_1.removeChildren();
        }
        node_1.addChild(model.getFile());
    }
    
    public void setModel2(Model model) {
        model2 = model;
        if(node_2 == null) {
            this.node_2 = this.node.addChild(strings.getString("tree.node.secondaryModel"));
        } else {
            node_2.removeChildren();
        }
        node_2.addChild(model.getFile());
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

     public void setNode(ProjectTree.Node node){
        this.node = node;
    }

    public ArrayList<List<FacialPoint>> getDatabaseFacialPoints() {
        return databaseFacialPoints;
    }

    public void setDatabaseFacialPoints(ArrayList<List<FacialPoint>> databaseFacialPoints) {
        this.databaseFacialPoints = databaseFacialPoints;
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

    public void setHausdorfMaxTreshold(int hausdorfTreshold) {
        colormapViz.setHausdorfMaxTreshold(hausdorfTreshold);
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

    public int getUseDatabase() {
        return useDatabase;
    }

    public void setUseDatabase(int useDatabase) {
        this.useDatabase = useDatabase;
    }

    public ArrayList<File> getDatabaseFiles() {
        return databaseFiles;
    }

    public void setDatabaseFiles(ArrayList<File> databaseFiles) {
        this.databaseFiles = databaseFiles;
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

    public boolean isUseSymmetry() {
        return useSymmetry;
    }

    public void setUseSymmetry(boolean useSymmetry) {
        this.useSymmetry = useSymmetry;
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

    public void setModelIcon(ImageIcon i) {
       modelIcon = i;
    }
    public void setResultIcon(ImageIcon i) {
       resultIcon = i;
    }
    
    public ImageIcon getModelIcon() {
       return modelIcon;
    }
    public ImageIcon getResultIcon() {
       return resultIcon;
    }

}
