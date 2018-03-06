package cz.fidentis.featurepoints;

import cz.fidentis.*;
import cz.fidentis.featurepoints.AutoThresholder.Method;
import cz.fidentis.featurepoints.curvature.ColorType;
import cz.fidentis.featurepoints.curvature.CurvatureControl;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import cz.fidentis.featurepoints.symmetryplane.SymmetryPlane;
import cz.fidentis.model.corner_table.CornerTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.vecmath.PdVector;
import jvx.geom.PwSimplify;
import jvx.geom.PwSmooth;

/**
 *
 * @author Marek Galvanek
 *
 * This class provides all about feature points extraction
 */
public class FeaturePointsUniverse {

    private Model model;
    private Faces faces;
    private ArrayList<Vector3f> verts;
    private PgElementSet elementSet;
    private PgElementSet originalElementSet;
    private double[] curvatureValues;
    private BilateralDenoise biDenoise;
    private CurvatureControl curveControl;
    private ThresholdArea thresArea;
    private PwSimplify simplify;
    
    Set<Integer> noseTip;
    Set<Integer> mouth;
    Set<Integer> eyes;

    private double threshold;
    private Set<Integer> thresholdFaces;
    private Set<Integer> mathMorfoVertices;
    private Set<Integer> boundaryVertices;
    private Set<SimpleEdge> boundaryEdges;
    private Set<Integer> thresholdBigestRegion;
    private List<FacialPoint> facialPoints;
    private PdVector pronasale;
    private CornerTable cornerTable;
    private ColorType colorType = ColorType.Deviation;
    private Method thresholdMethod = Method.Default;
    private boolean isColor = false;

    public FeaturePointsUniverse(Model model) {
        this.model = model;
        this.faces = model.getFaces();
        this.verts = model.getVerts();
        FacesConvertor fc = new FacesConvertor();
        elementSet = fc.convert(faces, verts);
        boundaryVertices = fc.getBoundaryVertices();
        boundaryEdges = fc.getBoundaryEdges();
        //boundaryFacesIndices = fc.getBoundaryFacesIndices();

        cornerTable = new CornerTable(elementSet);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        biDenoise = new BilateralDenoise(elementSet, cornerTable);

        elementSet.makeElementNormals();
        elementSet.makeVertexNormals();

        originalElementSet = new PgElementSet();
        originalElementSet.copy(elementSet);
        
        facialPoints = new ArrayList<>();
    }

    public FeaturePointsUniverse(PgElementSet elementSet) {
        this.elementSet = elementSet;
        originalElementSet = new PgElementSet();
        originalElementSet.copy(elementSet);
    }
  

    //MESH DENOISE
    
    /*
    public PgElementSet calculateBilateralDenoise(double sigmaC, double sigmaS, int iter) {
        biDenoise = new BilateralDenoise(elementSet, cornerTable);
        elementSet = biDenoise.denoise(sigmaC, sigmaS);
        //elementSet = biDenoise.denoise(sigmaC, sigmaS, iter);
        return elementSet;
    }
    */

    public void calculateAnisotropicDenoise(int iter, int method, double featureDetect, boolean keepBoundary) {
        biDenoise = new BilateralDenoise(elementSet, cornerTable);
        elementSet = biDenoise.anisotropicDenoise(iter, method, featureDetect, keepBoundary);
    }
    
    //experimental method in new GUI
    public void computeAnisotropicDenoise(int numOfLoops, int method, double featureDetect, boolean keepBoundary) {
        elementSet = new BilateralDenoise(elementSet, cornerTable).anisotropicDenoise2(numOfLoops, method, featureDetect, keepBoundary);
    }
    
    
    //CURVATURE
    
    public void calculateCurvature(CurvatureType curveType) {
        curveControl = new CurvatureControl(boundaryVertices, elementSet);
        curvatureValues = curveControl.computeCurvature(curveType);
//        if (colorType == ColorType.NoColors) {
//            colorType = ColorType.Deviation;
//        }

        //setCurvatureColors(colorType);
    }

    public PgElementSet setCurvatureColors(ColorType colorType) {
        this.colorType = colorType;
        switch (colorType) {
            case Maximum:
//                isColor = true;
                elementSet = curveControl.setColorsFromMaxAbs(curvatureValues, curveControl.gethasNegative());
                break;
            case Deviation:
//                isColor = true;
                elementSet = curveControl.setColorsFromDeviation(curvatureValues);
                break;
        }
        return elementSet;
    }
    
    public void copyCurvatureColors(){
        if (elementSet.hasVertexColors())
            originalElementSet.setVertexColors(elementSet.getVertexColors());
    }
    
    public void removeCurvatureColors() {
        elementSet.removeVertexColors();
    }
    
    
    //THRESHOLD
    
    public double calculateThreshold(double thres) {
        thresArea = new ThresholdArea(boundaryVertices);
        threshold = thresArea.calculateThreshold(curvatureValues, thres);
        return threshold;
    }

    public double calculateAutoThreshold() {
        threshold = thresArea.calculateAutoThreshold(curvatureValues, thresholdMethod);
        return threshold;
    }

    public double calculateAutoThreshold(int type) {
        setThresholdMethod(type);
        threshold = thresArea.calculateAutoThreshold(curvatureValues, thresholdMethod);
        return threshold;
    }

    public void setThresholdMethod(int type) {
        switch (type) {
            case 0:
                thresholdMethod = Method.Default;
                break;
            case 1:
                thresholdMethod = Method.IJ_IsoData;
                break;
            case 2:
                thresholdMethod = Method.Huang;
                break;
            case 3:
                thresholdMethod = Method.Intermodes;
                break;
            case 4:
                thresholdMethod = Method.IsoData;
                break;
            case 5:
                thresholdMethod = Method.Li;
                break;
            case 6:
                thresholdMethod = Method.MaxEntropy;
                break;
            case 7:
                thresholdMethod = Method.Mean;
                break;
            case 8:
                thresholdMethod = Method.MinError;
                break;
            case 9:
                thresholdMethod = Method.Minimum;
                break;
            case 10:
                thresholdMethod = Method.Moments;
                break;
            case 11:
                thresholdMethod = Method.Otsu;
                break;
            case 12:
                thresholdMethod = Method.Percentile;
                break;
            case 13:
                thresholdMethod = Method.RenyiEntropy;
                break;
            case 14:
                thresholdMethod = Method.Shanbhag;
                break;
            case 15:
                thresholdMethod = Method.Triangle;
                break;
            case 16:
                thresholdMethod = Method.Yen;
                break;
        }
    }
    
    
    //THRESHOLD AREAS IN MESH

    public void calculateThresholdFaces(int type) {
        thresholdFaces = thresArea.getThresholdFaces(curvatureValues, calculateAutoThreshold(type));
    }
    
    //experimental method in new GUI
    public Set<Integer> computeThresholdFaces(int type) {
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        thresholdFaces = thresArea.getThresholdFaces(curvatureValues, calculateAutoThreshold(type));
        return thresholdFaces;
    }    
    
    public Set<Integer> getThresholdMinimalFaces() {

        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        thresholdFaces = thresArea.getThresholdMinimalFaces(curvatureValues, calculateAutoThreshold());
        return thresholdFaces;
    }

    public Set<Integer> getThresholdMinimalFaces(int type) {

        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        thresholdFaces = thresArea.getThresholdMinimalFaces(curvatureValues, calculateAutoThreshold(type));
        return thresholdFaces;
    }
    
    
    //FEATURE POINTS EXTRACTION
    
    public void findNoseTipArea() {
        noseTip = thresArea.findNoseTip(thresholdFaces);
        setFacialPoint(thresArea.getPronasaleFP());
        pronasale = thresArea.getPronasale();
    }

    public void findMouthArea() {
        thresArea.findMouth(thresholdFaces, threshold, curvatureValues, pronasale);
        setFacialPoint(thresArea.getLeftCheilionFP());
        setFacialPoint(thresArea.getRightCheilionFP());
        setFacialPoint(thresArea.getStomionFP());
    }

    public void findEyeArea(boolean isRightEye) {
        thresArea.findEyeArea(thresholdFaces, threshold, curvatureValues, pronasale, isRightEye);

        if (isRightEye) {
            setFacialPoint(thresArea.getRightEktokantionFP());
            setFacialPoint(thresArea.getRightEntokantionFP());
        } else {
            setFacialPoint(thresArea.getLeftEktokantionFP());
            setFacialPoint(thresArea.getLeftEntokantionFP());
        }
    }

    public Set<Integer> findSubnasaleArea() {
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        thresholdBigestRegion = thresArea.findSubnasaleArea(thresholdFaces, threshold, curvatureValues, pronasale);
        return thresholdBigestRegion;
    }
    
    
    //GETTERS AND SETTERS
    
    public List<FacialPoint> getFacialPoints() {
        return facialPoints;
    }
    
    public void setFacialPoints(List<FacialPoint> facialPoints) {
        this.facialPoints = facialPoints;
    }
    

    public void setFacialPoint(FacialPoint facialPoint) {
        facialPoints.add(facialPoint);
    }

    public PgElementSet getElementSet() {
        return elementSet;
    }

    public void setElementSet(PgElementSet elementSet) {
        this.elementSet.copy(elementSet);
    }
    
    public void resetElementSet(){
        elementSet = new PgElementSet();
        elementSet.copy(originalElementSet);
    }

    public boolean isColor() {
        return isColor;
    }

    public void setIsColor(boolean bool) {
        isColor = bool;
    }

    public Set<Integer> getBoundaryVertices() {
        return boundaryVertices;
    }
    
    public void setBoundaryVertices(Set<Integer> boundaryVertices) {
        this.boundaryVertices = boundaryVertices;
    }
    
    public void resetBoundaryVertices() {
        this.boundaryVertices.clear();
    }

    public PgElementSet getOriginalElementSet() {
        return originalElementSet;
    }

    public void setOriginalElementSet(PgElementSet originalElementSet) {
        this.originalElementSet.copy(originalElementSet);
    }

    public Set<Integer> getThresholdFaces() {
        return thresholdFaces;
    }

    public void setThresholdFaces(Set<Integer> thresholdFaces) {
        this.thresholdFaces = thresholdFaces;
    }

    public Set<Integer> getMathMorfoVertices() {
        return mathMorfoVertices;
    }

    public void setMathMorfoVertices(Set<Integer> mathMorfoFaces) {
        this.mathMorfoVertices = mathMorfoFaces;
    }
 
    //CONTROL METHODS
    
    public void findNose() {  
        calculateAnisotropicDenoise(10, PwSmooth.METHOD_ANISOTROPIC, 2.0, false);
        calculateCurvature(CurvatureType.Minimum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(15);
        findNoseTipArea();
    }

    public void findMouth() {
        elementSet = originalElementSet;
        calculateAnisotropicDenoise(3, PwSmooth.METHOD_ANSIO_PRECRIBED, 5.0, false);
        calculateCurvature(CurvatureType.Maximum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(6);
        findMouthArea();
    }

    public void findEyes() {
        elementSet = originalElementSet;
        calculateCurvature(CurvatureType.Maximum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(13);
        findEyeArea(true);
        findEyeArea(false);
    }
    
    public Set<Integer> findMouthRegion() {
        elementSet = originalElementSet;
        calculateAnisotropicDenoise(3, PwSmooth.METHOD_ANSIO_PRECRIBED, 5.0, false);
        calculateCurvature(CurvatureType.Maximum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(6);
        return thresArea.findMouth(thresholdFaces, threshold, curvatureValues, pronasale);
    }
    
    public Set<Integer> findEyesRegion() {
        elementSet = originalElementSet;
        calculateCurvature(CurvatureType.Maximum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(13);
        Set<Integer> eyesRegion = new HashSet<>();
        eyesRegion.addAll(thresArea.findEyeArea(thresholdFaces, threshold, curvatureValues, pronasale, true));
        eyesRegion.addAll(thresArea.findEyeArea(thresholdFaces, threshold, curvatureValues, pronasale, false));
        return eyesRegion;
    }
    
    public Set<Integer> findNoseRegion() {
        calculateAnisotropicDenoise(10, PwSmooth.METHOD_ANISOTROPIC, 2.0, false);
        calculateCurvature(CurvatureType.Minimum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(15);
        return thresArea.findNoseTip(thresholdFaces);
    }
    
    //MATH MORPHOLOGY
    
    public Set<Integer> mathMorfo(int structElement1, int structElement2, MathMorfoType mmType, Set <Integer> area) {        
        Set<Integer> newArea = new HashSet<>();
        switch (mmType) {
            case Dilation:
                newArea = thresArea.dilation(structElement1, area);
                break;
            case Erosion:
                newArea = thresArea.erosion(structElement1, area);
                break;
            case Opening:
                newArea = thresArea.opening(structElement1, structElement2, area);
                break;
            case Closing:
                newArea = thresArea.closing(structElement1, structElement2, area);
                break;    
        }
        return newArea;
    }
    
    Set<Integer> mathMorfoFaces = new HashSet<>();
    public Set<Integer> computeMathMorfo(int structElement1, int structElement2, MathMorfoType mmType) { 
        setMathMorfoVertices();
        mathMorfoVertices = mathMorfo(structElement1, structElement2, mmType, mathMorfoVertices);
        mathMorfoFaces = thresArea.thresholdVerticesToFaces(mathMorfoVertices);
        return mathMorfoFaces;
    }
    
    public void setMathMorfoVertices() {
        Set<Integer> morfoFaces;
        if (mathMorfoFaces.isEmpty() || mathMorfoFaces == null) {
            morfoFaces = new HashSet<>(thresholdFaces);
        } else {
            morfoFaces = new HashSet<>(mathMorfoFaces);
        }
        mathMorfoVertices = thresArea.getThresholdVertices(morfoFaces);
    }
    
    //MESH SIMPLIFY
    public void computeSimplify() {
        // Ak je pocet polygonov vacsi ako 12000 tak zmensit
        if (elementSet.getNumElements() > 7000) {
            computeSimplify(false, false, false, false, true, 10000);
        }
    
        JavaViewBoundary jVboundary = new JavaViewBoundary(elementSet);
        boundaryEdges = jVboundary.getBoundaryEdges();
        boundaryVertices = jVboundary.getBoundaryVertices();

        cornerTable = new CornerTable(elementSet);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        biDenoise = new BilateralDenoise(elementSet, cornerTable);

        elementSet.makeElementNormals();
        elementSet.makeVertexNormals();

        // Aj do povodneho modelu skopirovat zmenseny
        originalElementSet.copy(elementSet);
    }

    public void computeSimplify(boolean checkAngles, boolean checkNormals, boolean flipEdges,
                                boolean forceSimplify, boolean keepBoundary, int remainElements){
        initSimplify(); 
        
        simplify.setEnabledCheckAngles(checkAngles);
        simplify.setEnabledCheckNormals(checkNormals);
        simplify.setEnabledFlipEdges(flipEdges);
        simplify.setEnabledForceSimplify(forceSimplify);
        simplify.setEnabledKeepBoundary(keepBoundary);
        
        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);
        
        simplify.simplify(remainElements);
        simplify.simplify(remainElements);
        
        PsDebug.getConsole().setVisible(false);       
    }
    
    public void initSimplify() {
        simplify = new PwSimplify();
        simplify.setGeometry(elementSet);  
        simplify.init();
    }
    
    public void resetSimplify(){
        simplify.reset();
    }
    
    // SYMETRY PLANE 
    public LinkedList<Point3d> getSymmetryPlanePoints(ArrayList<Vector3f> centerPoints) {
        LinkedList<Point3d> symmetryPlanePoints = Intersection.computeSymmetryPlanePoints(model, cornerTable, centerPoints);
        
        return symmetryPlanePoints;
    }
    
    public List<FacialPoint> getSymmetryPlaneFPs(ArrayList<Vector3f> centerPoints) {
        LinkedList<Point3d> symmetryPlanePoints = Intersection.computeSymmetryPlanePoints(model, cornerTable, centerPoints);
        SymmetryPlane symmetryPlane = new SymmetryPlane(facialPoints, symmetryPlanePoints);
        facialPoints = symmetryPlane.findAllSymmetryPlaneFPs();
        return facialPoints;
    }

    public List<FacialPoint> getAllPoints() {
        return facialPoints;
}
    
    //PDM methods
    
    public double[] calculateCurvature(CurvatureType curveType, boolean f) {
        curveControl = new CurvatureControl(boundaryVertices, elementSet);
        return curveControl.computeCurvature(curveType);
    }
    
    public CornerTable getCornerTable() {
        return this.cornerTable;
    }
    
    public Set<Set<Integer>> findNose(boolean notUsed) {  
        calculateAnisotropicDenoise(10, PwSmooth.METHOD_ANISOTROPIC, 2.0, true);
        calculateCurvature(CurvatureType.Minimum);
        thresArea = new ThresholdArea(elementSet, cornerTable, boundaryVertices);
        calculateThresholdFaces(15);
        return findNoseTipArea(notUsed);
    }
    
    public Set<Set<Integer>> findNoseTipArea(boolean notUsed) {
        return thresArea.findAllNoseTipAreas(thresholdFaces, notUsed);
    }

    public ArrayList<Vector3f> getVerts() {
        return verts;
    }
}
