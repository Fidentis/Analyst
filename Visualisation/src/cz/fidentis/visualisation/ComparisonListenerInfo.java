/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.model.Model;
import cz.fidentis.model.VertexInfo;
import cz.fidentis.visualisation.procrustes.PApainting;
import cz.fidentis.visualisation.procrustes.PApaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class ComparisonListenerInfo {
    private HDpainting hdPaint;
    private HDpaintingInfo hdInfo;
    private boolean paintHD = false;
    private FeaturePointsUniverse fpUniverse;
    private List<FacialPoint> facialPoints;
    private List<ICPTransformation> transformations;
    private ArrayList<Model> models = new ArrayList<>();
    private int indexOfSelectedPoint = -1;
    private float facialPointRadius = 2;
    private float[] colorOfPoint = new float[]{1f, 0f, 0f, 1.0f};
    private float[] colorOfInactivePoint = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
    private float[] colorOfCut = new float[]{1f, 1f, 1f, 1.0f};
    private float cutThickness = 1;
    private PApainting paPainting;
    private PApaintingInfo paInfo;
    private boolean procrustes = false;
    
    private boolean contours = true;
    private boolean showAllCuts;
    private boolean showSamplingRays;
    private boolean showVectors = true;
    private boolean showBoxplot = false;
    private boolean showBoxplotFunction = true;
    private boolean render;
    private Vector3f planePoint = new Vector3f(0, 0, 0);
    private Vector3f planeNormal = new Vector3f(1, 0, 0);
    
    private LinkedList<Vector3f> plane;
    private ArrayList<LinkedList<LinkedList<Vector3f>>> lists;
    private ArrayList<LinkedList<LinkedList<Vector2f>>> lists2;
    private ArrayList<Vector2f> samplePoints;
    private ArrayList<Vector2f> sampleNormals;
    private ArrayList<ArrayList<Vector2f>> distancePoints;
    private ArrayList<ArrayList<Float>> pointDistances; 
    private ArrayList<Vector2f> averagedistancePoints;
    
    private ArrayList<float[]> sampleVetices = new ArrayList<>();
    private ArrayList<ArrayList<VertexInfo>> verticesInfo = new ArrayList<>();
    
    private float[] primaryColor = {51f / 255f, 153f / 255f, 1f, 1.0f};
    private float[] secondaryColor = {1f, 1f, 0f, 1.0f};
    private float[] fogColor = {0f, 0f, 255f, 1.0f};

    public ComparisonListenerInfo() {
        models = new ArrayList<>();
        facialPoints = new ArrayList<>();
        
        plane = new LinkedList<>();
        lists = new ArrayList<>();
        lists2 = new ArrayList<>();
        samplePoints = new ArrayList<>();
        distancePoints = new ArrayList<>();
        averagedistancePoints = new ArrayList<>();
    }

    public HDpainting getHdPaint() {
        return hdPaint;
    }

    public void setHdPaint(HDpainting hdPaint) {
        this.hdPaint = hdPaint;
    }

    public HDpaintingInfo getHdInfo() {
        return hdInfo;
    }

    public void setHdInfo(HDpaintingInfo hdInfo) {
        this.hdInfo = hdInfo;
    }

    public boolean isPaintHD() {
        return paintHD;
    }

    public void setPaintHD(boolean paintHD) {
        this.paintHD = paintHD;
    }

    public FeaturePointsUniverse getFpUniverse() {
        return fpUniverse;
    }

    public void setFpUniverse(FeaturePointsUniverse fpUniverse) {
        this.fpUniverse = fpUniverse;
    }
    
    public void initFpUniverse(Model m){
        this.fpUniverse = new FeaturePointsUniverse(m);
        this.facialPoints = new ArrayList<>();
    }
    
    public void initFpUniverse(){
        this.fpUniverse = new FeaturePointsUniverse(this.models.get(0));
        this.facialPoints = new ArrayList<>();
    }
    
    public void initFpUniverse(List<FacialPoint> points){
        this.fpUniverse = new FeaturePointsUniverse(this.models.get(0));
        this.fpUniverse.setFacialPoints(points);
        this.facialPoints = points;
    }
    
    public void initFpUniverse(List<FacialPoint> points, Model m){
        this.fpUniverse = new FeaturePointsUniverse(m);
        this.fpUniverse.setFacialPoints(points);
        this.facialPoints = points;
    }

    public List<FacialPoint> getFacialPoints() {
        return facialPoints;
    }

    public void setFacialPoints(List<FacialPoint> facialPoints) {
        this.facialPoints = facialPoints;
    }
    
    public boolean containsFP(int i){
        for(FacialPoint fp : facialPoints){
            if(fp.getType() == i)
                return true;
        }
        
        return false;
    }
    
    public void addFacialPoint(FacialPoint fp){
        this.facialPoints.add(fp);
    }
    
    public void removeFacialPoint(int id){
        for(int i = 0; i < facialPoints.size(); i++){
            if(facialPoints.get(i).getType() == id){
                facialPoints.remove(i);
                break;
            }  
        }
    }
    
    public int getNextFreeFPID(){
        //Indexing starts from 1
        int i = 1;
        while(containsFP(i)){
            i++;
        }
        
        return i;
    }   

    public List<ICPTransformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<ICPTransformation> transformations) {
        this.transformations = transformations;
    }
    
    public void addTransformations(List<ICPTransformation> trans){
        if(this.transformations == null){
            this.transformations = new ArrayList<>();
        }
        
        this.transformations.addAll(trans);
    }
    
    public void addTransfromation(ICPTransformation tran){
        if(this.transformations == null){
            this.transformations = new ArrayList<>();
        }
        
        this.transformations.add(tran);
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }
    
    public void setModel(Model model){
        this.models.clear();
        this.models.add(model);
    }
    
    public void addModel(Model model){
        this.models.add(model);
    }
    
    public void removesModel(){
        fpUniverse = null;
        facialPoints = new ArrayList<FacialPoint>();

        indexOfSelectedPoint = -1;
        facialPointRadius = 2;

        models.clear();
        paintHD = false;
    }

    public int getIndexOfSelectedPoint() {
        return indexOfSelectedPoint;
    }

    public void setIndexOfSelectedPoint(int indexOfSelectedPoint) {
        this.indexOfSelectedPoint = indexOfSelectedPoint;
    }

    public float getFacialPointRadius() {
        return facialPointRadius;
    }

    public void setFacialPointRadius(float facialPointRadius) {
        this.facialPointRadius = facialPointRadius;
    }

    public float getCutThickness() {
        return cutThickness;
    }

    public void setCutThickness(float cutThickness) {
        this.cutThickness = cutThickness;
    }
    
    public float[] getColorOfCut() {
        return colorOfCut;
    }

    public void setColorOfCut(float[] colorOfCut) {
        this.colorOfCut = colorOfCut;
    }

    public float[] getColorOfPoint() {
        return colorOfPoint;
    }

    public void setColorOfPoint(float[] colorOfPoint) {
        this.colorOfPoint = colorOfPoint;
    }
    
    public float[] getColorOfInactivePoint() {
        return colorOfInactivePoint;
    }
    
    public void setColorOfInactivePoint(float[] colorOfInactivePoint) {
        this.colorOfInactivePoint = colorOfInactivePoint;
    }

    public PApainting getPaPainting() {
        return paPainting;
    }

    public void setPaPainting(PApainting paPainting) {
        this.paPainting = paPainting;
    }

    public PApaintingInfo getPaInfo() {
        return paInfo;
    }

    public void setPaInfo(PApaintingInfo paInfo) {
        this.paInfo = paInfo;
    }

    public boolean isProcrustes() {
        return procrustes;
    }

    public void setProcrustes(boolean procrustes) {
        this.procrustes = procrustes;
    }

    public boolean isContours() {
        return contours;
    }

    public void setContours(boolean contours) {
        this.contours = contours;
    }

    public boolean isShowAllCuts() {
        return showAllCuts;
    }

    public void setShowAllCuts(boolean showAllCuts) {
        this.showAllCuts = showAllCuts;
    }

    public boolean isShowSamplingRays() {
        return showSamplingRays;
    }

    public void setShowSamplingRays(boolean showSamplingRays) {
        this.showSamplingRays = showSamplingRays;
    }

    public boolean isShowVectors() {
        return showVectors;
    }

    public void setShowVectors(boolean showVectors) {
        this.showVectors = showVectors;
    }

    public boolean isShowBoxplot() {
        return showBoxplot;
    }

    public void setShowBoxplot(boolean showBoxplot) {
        this.showBoxplot = showBoxplot;
    }

    public boolean isShowBoxplotFunction() {
        return showBoxplotFunction;
    }

    public void setShowBoxplotFunction(boolean showBoxplotFunction) {
        this.showBoxplotFunction = showBoxplotFunction;
    }
    
    

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public Vector3f getPlanePoint() {
        return planePoint;
    }

    public void setPlanePoint(Vector3f planePoint) {
        this.planePoint = planePoint;
    }

    public Vector3f getPlaneNormal() {
        return planeNormal;
    }

    public void setPlaneNormal(Vector3f planeNormal) {
        this.planeNormal = planeNormal;
    }

    public LinkedList<Vector3f> getPlane() {
        return plane;
    }

    public void setPlane(LinkedList<Vector3f> plane) {
        this.plane = plane;
    }

    public ArrayList<LinkedList<LinkedList<Vector3f>>> getLists() {
        return lists;
    }

    public void setLists(ArrayList<LinkedList<LinkedList<Vector3f>>> lists) {
        this.lists = lists;
    }

    public ArrayList<LinkedList<LinkedList<Vector2f>>> getLists2() {
        return lists2;
    }

    public void setLists2(ArrayList<LinkedList<LinkedList<Vector2f>>> lists2) {
        this.lists2 = lists2;
    }

    public ArrayList<Vector2f> getSamplePoints() {
        return samplePoints;
    }

    public void setSamplePoints(ArrayList<Vector2f> samplePoints) {
        this.samplePoints = samplePoints;
    }

    public ArrayList<Vector2f> getSampleNormals() {
        return sampleNormals;
    }

    public void setSampleNormals(ArrayList<Vector2f> sampleNormals) {
        this.sampleNormals = sampleNormals;
    }

    public ArrayList<ArrayList<Vector2f>> getDistancePoints() {
        return distancePoints;
    }

    public void setDistancePoints(ArrayList<ArrayList<Vector2f>> distancePoints) {
        this.distancePoints = distancePoints;
    }

    public ArrayList<ArrayList<Float>> getPointDistances() {
        return pointDistances;
    }

    public void setPointDistances(ArrayList<ArrayList<Float>> pointDistances) {
        this.pointDistances = pointDistances;
    }  


    public ArrayList<Vector2f> getAveragedistancePoints() {
        return averagedistancePoints;
    }

    public void setAveragedistancePoints(ArrayList<Vector2f> averagedistancePoints) {
        this.averagedistancePoints = averagedistancePoints;
    }

    public float[] getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(float[] primaryColor) {
        this.primaryColor = primaryColor;
    }

    public float[] getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(float[] secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public float[] getFogColor() {
        return fogColor;
    }

    public void setFogColor(float[] fogColor) {
        this.fogColor = fogColor;
    }

    public ArrayList<float[]> getSampleVetices() {
        return sampleVetices;
    }

    public void setSampleVetices(ArrayList<float[]> sampleVetices) {
        this.sampleVetices = sampleVetices;
    }

    public ArrayList<ArrayList<VertexInfo>> getVerticesInfo() {
        return verticesInfo;
    }

    public void setVerticesInfo(ArrayList<ArrayList<VertexInfo>> verticesInfo) {
        this.verticesInfo = verticesInfo;
    }
    
}
