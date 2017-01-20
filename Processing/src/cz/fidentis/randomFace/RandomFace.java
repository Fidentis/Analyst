/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.randomFace;

import com.jogamp.graph.math.Quaternion;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.composite.CompositeModel;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.composite.ModelInfo;
import cz.fidentis.controller.Composite;
import cz.fidentis.utils.MeshUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Class containing logic behind Random Face generation in Composite window
 * 
 * @author Zuzana Ferkova
 */
public class RandomFace {
    private static RandomFace instance;
    private static HashSet<FacePartType> croppedFacePartTypes;
    
    private static HashMap<FacePartType, ModelInfo> currentComposite;
    
    private File modelPath;
    private File modelPath2;    //null if model is not paired (e.g. eyes, eyebrows, etc.)
    private ModelInfo info;
    private ModelInfo info2;    //null if model is not paired (e.g. eyes, eyebrows, etc.)
    
    //singelton
    private RandomFace(){
        currentComposite = new HashMap<>();
    };
    
    /**
     * Makes sure there's only one instance of RandomFace class created.
     * @return either newly created RandomFace instance ore the one that was created before
     */
    public static RandomFace instance(){
        if(instance == null){
            instance = new RandomFace();
            croppedFacePartTypes = sortEnums();
        }
        
        return instance;
    }
    
    public HashSet<FacePartType> getCroppedFacePartTypes(){
        return croppedFacePartTypes;
    }
    
    /**
     * Sets information about the models to be displayed. If face part we are trying to displayed is not paired
     * (e.g. eyes, eyebrows, etc.) then model2 is NULL. 
     * @param model1 if face part is paired (e.g. eyes, eyebrows, etc.) this will be containing first part of the pair, otherwise (with single model) it contains whole model
     * @param model2 if face part is paired (e.g. eyes, eyebrows, etc.) this will be containing second part of the pair, otherwise (with single model) it will be NULL
     */
     private void setModel(ModelInfo model1, ModelInfo model2) {        
         modelPath = model1.getFile();
         info = model1;
         
         if(model2 != null){
             modelPath2 = model2.getFile();
             info2 =  model2;
         }    
    }
     
    /**
     * Clear information about model loaded before.
     */
    private void clearModels(){
         modelPath = null;
         info = null;
         modelPath2 = null;
         info2 = null;
     }
     
    /**
     * Gets rid of enums in FacePartType that have no models assigned to them.
     * 
     * @return Set of enums without parts which have no map of models assigned to them in CompositePanel class
     */
     private static HashSet<FacePartType> sortEnums(){
         HashSet<FacePartType> sorted = new HashSet<>(Arrays.asList(FacePartType.values()));
         
         sorted.remove(FacePartType.LEFT_EYE);
         sorted.remove(FacePartType.LEFT_EAR);
         sorted.remove(FacePartType.LEFT_EYEBROW);
         sorted.remove(FacePartType.RIGHT_EAR);
         sorted.remove(FacePartType.RIGHT_EYE);
         sorted.remove(FacePartType.RIGHT_EYEBROW);
         
         return sorted;
     }
     
     public void addPart(FacePartType part, ModelInfo info){
         currentComposite.put(part, info);
     }
     
     public void clearParts(){
         currentComposite.clear();
     }
     
     public HashMap<FacePartType, ModelInfo> getCurrentCompositeMap(){
            return currentComposite;
     }
    
     //Copy pasted from CompositeButtonActionListener to get some OOP feel. 
     /**
      * Picks face part 'type' of index 'index' represented by array of models 'models' (either single model or a pair)
      * and places it to current face composite 'composite'.
      * 
      * @param models
      * @param composite
      * @param type
      * @param index 
      */
    public void pickRandomPart(ModelInfo[] models, Composite composite, FacePartType type, Object index){
        clearModels();
        
        setModel(models[0], models[1]);
                
        Vector3f position = new Vector3f();
        Vector3f position2 = new Vector3f();    //needed only in case of pair models, e.g. eyebrows, eyes...
        Vector3f shift;
        Vector3f shift2 = new Vector3f();   //needed only in case of pair models, e.g. eyebrows, eyes...
        
        //get the position of point on selected model for automatic placement
        if(info.getPosition()!= null){
            shift = new Vector3f(MeshUtils.instance().computeCentroid(info.getPosition()));
          if(info2!= null && info2.getPosition()!= null){
            shift2 = new Vector3f(MeshUtils.instance().computeCentroid(info2.getPosition()));
          }
        }
        else{
            shift = new Vector3f();
            shift2 = new Vector3f();
        }

        FacePartType type1 = type;
        FacePartType type2 = type;
        
        //get the custom adjustment of model position -  position of new model is calculated 
        //as 'automatic placement position' + 'custom adjustement made by user'
        //save the index of selected model
        switch (type) {
            case EYES:
                type1 = FacePartType.RIGHT_EYE;
                type2 = FacePartType.LEFT_EYE;
                position = new Vector3f(composite.getRighteyePosition());
                position2 = new Vector3f(composite.getLefteyePosition());
                composite.setSelectedEyes(index);
                break;
            case EYEBROWS:
                type1 = FacePartType.RIGHT_EYEBROW;
                type2 = FacePartType.LEFT_EYEBROW;
                position =new Vector3f(composite.getRighteyebrowPosition());
                position2 =new Vector3f(composite.getLefteyebrowPosition());
                composite.setSelectedEyebrows(index);
                break;
            case EARS:
                type1 = FacePartType.RIGHT_EAR;
                type2 = FacePartType.LEFT_EAR;
                position=new Vector3f(composite.getRightearPosition());
                position2=new Vector3f(composite.getLeftearPosition());
                composite.setSelectedEars(index);
                break;
            case HEAD:
                composite.setSelectedHead(index);
                break;
            case FORHEAD:
                composite.setSelectedForhead(index);
                position=new Vector3f(composite.getForeheadPosition());
                break;
            case NOSE:
                composite.setSelectedNose(index);
                position=new Vector3f(composite.getNosePosition());
                break;
            case MOUTH:
                composite.setSelectedMouth(index);
                position=new Vector3f(composite.getMouthPosition());                
                break;
            case CHIN:
                composite.setSelectedChin(index);
                position=new Vector3f(composite.getChinPosition());
                break;
        }
        
        //add or replace the model of given type with selected model (i.e. model bound to this button)
        composite.addModel(modelPath, type1, shift, position);
        if (modelPath2 != null) {
            composite.addModel(modelPath2, type2, shift2, position2);
        }

        //in case the model is of type HEAD, set the positions of points for automatic placement on head
        //(the points for automatic placement on head are paired with points on corresponding facial part models to calculate the placement) 
        if(info.getPart()!= null && info.getPart().equals("head")){
            composite.setChinPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getChinPosition())));
            composite.setForeheadPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getForeheadPosition())));
            composite.setMouthPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getMouthPosition())));
            composite.setNosePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getNosePosition())));
            composite.setRightearPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRightearPosition())));
            composite.setRighteyePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRighteyePosition())));
            composite.setLefteyePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLefteyePosition())));
            composite.setLeftearPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLeftearPosition())));
            composite.setLefteyebrowPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLefteyebrowPosition())));
            composite.setRighteyebrowPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRighteyebrowPosition())));
        }
        
        //add picked part to current composite in RandomFace class
        RandomFace.instance().addPart(type1, info);
        
        if(RandomFace.instance().isPair(type)){
            RandomFace.instance().addPart(type2, info2);
        }
    }
    
    /**
     * Check if given FacePartType is pair part
     * @param type face part to be checked for pair
     * @return true if face part is pair (like eyes, eyebrows, etc.), false otherwise
     */
    public boolean isPair(FacePartType type){
        boolean isPair;
        
        switch(type){
            case EYES:
                isPair = true;
                break;
            case EYEBROWS:
                isPair = true;
                break;
            case EARS:
                isPair = true;
                break;
            case HEAD:
                isPair = false;
                break;
            case FORHEAD:
                isPair = false;
                break;
            case NOSE:
                isPair = false;
                break;
            case MOUTH:
                isPair = false;
                break;
            case CHIN:
                isPair = false;
                break;
            default:
                isPair = false;
                break;
        }
        
        return isPair;
    }
    
    //Reset translation, rotation and scale of given part for given composite
    public void resetPartTransform(FacePartType part, Composite composite){
        
        composite.resetPartToInit(part, 1);
        
        if(isPair(part)){
            composite.resetPartToInit(part, 2);
        }
    }
    
    //apply computed translation to given compositeModel, by setting offset from original translation, scale and rotation (can then easily be rested)
    public void applyTransToCompositeModel(CompositeModel cm, ICPTransformation transformation, int numOfPoints, boolean allowScale){
        if (!Float.isNaN(transformation.getScaleFactor()) && allowScale) {
            float scale = transformation.getScaleFactor();
            cm.setScale(new Vector3f(scale, scale, scale));
        }

        transformation.getTranslation().add(cm.getTranslation());
        cm.setTranslation(transformation.getTranslation());

        if (numOfPoints > 1) {
            Quaternion q = transformation.getRotation();
            cm.setRotation(new Quat4f(q.getX(), q.getY(), q.getZ(), q.getW()));
        }
    }
    
    //returns KdTree respresenting corresponding points of given FacePart p on head, these points are already translated to correct position
    //hence, not the points parsed from .inf file
    //creates copy of original points, doesn't affect points in composite
    public KdTree getKdTreeOfHeadPoints(ModelInfo head, FacePartType p, Composite compositeData){
        List<Vector3f> pointsOnHead = head.getPositionOfPart(p);
        List<Vector3f> translatedHeadPoints = new ArrayList<Vector3f>();

        for (Vector3f cp : pointsOnHead) {
            Vector3f transformedCP = new Vector3f(cp.x, cp.y, cp.z);

            transformedCP.sub(compositeData.getHeadOriginalCenetr());
            translatedHeadPoints.add(transformedCP);
        }

        KdTree headTree = new KDTreeIndexed(translatedHeadPoints);

        return headTree;
    }
    
    //creates copy of translated points for single face part, partPosition represents points parsed from .inf file for given part
    public List<Vector3f> getTranslatedPartPoints(List<Vector3f> partPosition, CompositeModel cm) {
        List<Vector3f> transformedPoints = new ArrayList<Vector3f>();

        for (Vector3f v : partPosition) {
            transformedPoints.add(new Vector3f(v.x, v.y, v.z));
        }

        for (Vector3f pt : transformedPoints) {
            pt.sub(cm.getModel().getModelDims().getOriginalCenter());
        }

        return transformedPoints;
    }
    
    //returns FacePartType, based on passed parameter fp, if fp is pair part, it will return both LEFT_<FP> and RIGHT_<FP> 
    //so they can be processed separately
    public FacePartType[] getPickedFacePartTypes(FacePartType fp){
        FacePartType[] fpArray;
        
        switch(fp){
            case EYES:
                fpArray = new FacePartType[2];
                fpArray[0] = FacePartType.LEFT_EYE;
                fpArray[1] = FacePartType.RIGHT_EYE;
                break;
            case EYEBROWS:
                fpArray = new FacePartType[2];
                fpArray[0] = FacePartType.LEFT_EYEBROW;
                fpArray[1] = FacePartType.RIGHT_EYEBROW;
                break;
            case EARS:
                fpArray = new FacePartType[2];
                fpArray[0] = FacePartType.LEFT_EAR;
                fpArray[1] = FacePartType.RIGHT_EAR;
                break;
            default:
                fpArray = new FacePartType[1];
                fpArray[0] = fp;
                break;
        }
        
        return fpArray;
    }
}
