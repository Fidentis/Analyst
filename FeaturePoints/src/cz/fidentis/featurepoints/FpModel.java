package cz.fidentis.featurepoints;

import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class FpModel implements Serializable{
    private List<FacialPoint> facialPoints;
    private Map<Integer, Integer> typeIndexes;      //id of landmark, index in facialPoints
    private String modelName;

    public FpModel() {
        facialPoints = new ArrayList<>();
        typeIndexes = new HashMap<>();
    }
    
    public FpModel(String modelName) {
        facialPoints = new ArrayList<>();
        typeIndexes = new HashMap<>();
        this.modelName = modelName;
    }
    
    public List<FacialPoint> getFacialPoints(){
        return facialPoints;
    }
    
    public void setFacialpoints(List<FacialPoint> facialPoints){
        this.facialPoints = facialPoints;
        createTypeIndexes();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public void addFacialPoint(FacialPoint facialPoint) {
        facialPoints.add(facialPoint);
        addTypeIndex(facialPoint.getType());
    }
    
    public int getPointsNumber() {
        return facialPoints.size();
    }
    
    public String toCSVstring(String sep) {     //TODO: make sure it adds custom points
        String csvString = modelName;
        for (int i = 0; i < FacialPointType.values().length - 1; i++) {
            FacialPoint fp = getFacialPoint(i);
            if (fp != null ) {
                csvString = csvString + sep + fp.toCSVstring(sep);
            } else {
                csvString = csvString + sep + sep + sep;
            }
        }
        return csvString;
    }

    public FacialPoint getFacialPoint(Integer type) {
        if (getTypeIndex(type) == null) {
            return null;
        } else {
            return facialPoints.get(getTypeIndex(type));
        }
    }

    private void createTypeIndexes() {
        typeIndexes.clear();
        Integer i = 0;
        for (FacialPoint fp : facialPoints) {
            typeIndexes.put(fp.getType(), i);
            i++;
        }
    }

    private void addTypeIndex(Integer type) {
        int lastIndex = facialPoints.size() - 1;
        typeIndexes.put(type, lastIndex);
    }

    private Integer getTypeIndex(Integer type) {
        return typeIndexes.get(type);
    }
    
    public boolean containsPoints(){
        return !facialPoints.isEmpty();
    }
    
    public boolean containsPoint(Integer type) {
        return getFacialPoint(type) != null;
    }
    
    //centralize points based on centralization performed on given model
    public void centralizeToModel(Model m){
        List<Vector3f> fps = listOfFP();
        
        m.centralize(fps);
    }
    
    //decentralize points based on centralization of given model
    public void decentralizeToModel(Model m){
        copyFPList();
        List<Vector3f> fps = listOfFP();
        
        m.decentralize(fps);
    }
    
    //load model, centralize it and decentralize points based on it
    public void decentralizeToFile(File f){
        Model model = ModelLoader.instance().loadModel(f, false, Boolean.TRUE);
        
        decentralizeToModel(model);
    }
    
    //creates list of positiong of FP
    public List<Vector3f> listOfFP(){
        List<Vector3f> fps = new ArrayList<>();
        
        for(FacialPoint fp : facialPoints){
            fps.add(fp.getPosition());
        }
        
        return fps;
    }
    
    //creates deep copy of FacialPoint
    public void copyFPList(){
        List<FacialPoint> copy = new ArrayList<>();
        
        for(FacialPoint fp: facialPoints){
            FacialPoint copied = new FacialPoint(fp.getType(), new Vector3f(fp.getPosition()));
            copy.add(copied);
        }
        
        this.facialPoints = copy;        
    }
    
    public List<FacialPoint> createListFp(){
        List<FacialPoint> copy = new ArrayList<>();
        
        for(FacialPoint fp: facialPoints){
            FacialPoint copied = new FacialPoint(fp.getType(), new Vector3f(fp.getPosition()));
            copy.add(copied);
        }
        
        return copy;
    }
    
}
