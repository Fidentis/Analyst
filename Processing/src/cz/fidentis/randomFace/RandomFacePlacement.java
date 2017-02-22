/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.randomFace;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.composite.CompositeModel;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.composite.ModelInfo;
import cz.fidentis.composite.SexType;
import cz.fidentis.controller.Composite;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class RandomFacePlacement {
    private final Composite compositeData;
    private boolean allowScale;
    private Map<Object, ModelInfo> forheadModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> eyesModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> earsModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> eyebrowsModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> noseModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> mouthModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> chinModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ModelInfo> headModels = new HashMap<Object, ModelInfo>();
    
    private static final float ERROR_RATE = 0.05f;
    private static final int MAX_ITERATIONS = 10;
    
    public RandomFacePlacement(Composite compositeData, boolean scaleModels,
            Map<Object, ModelInfo> forheadModels, Map<Object, ModelInfo> eyesModels, Map<Object, ModelInfo> earsModels,
            Map<Object, ModelInfo> eyebrowsModels, Map<Object, ModelInfo> noseModels, Map<Object, ModelInfo> mouthModels,
            Map<Object, ModelInfo> chinModels, Map<Object, ModelInfo> headModels) {
        this.compositeData = compositeData;
        this.allowScale = scaleModels;
        
        this.forheadModels = forheadModels;
        this.eyesModels = eyesModels;
        this.earsModels = earsModels;
        this.eyebrowsModels = eyebrowsModels;
        this.noseModels = noseModels;
        this.mouthModels = mouthModels;
        this.chinModels = chinModels;
        this.headModels = headModels;
    }

    public void setAllowScale(boolean allowScale) {
        this.allowScale = allowScale;
    }
    
    

    /**
     * Picks part based on user's choice and put into into scene.
     * If there's head in the scene the part will be aligned to the head
     * If picked part is head, all parts currently in scene will be aligned to the head.
     * Method is able to filter parts it will randomly choose based on sex and age range
     * 
     * @param sex - sex of the part to be picked, used for filtering
     * @param ageBottomLimit - bottom limit of age of part to be picked, used for filtering
     * @param ageTopLimit  - top limit of age of part to be picked, used for filtering
     */
    public void pickRandomPart(SexType sex, int ageBottomLimit, int ageTopLimit) {
        //get currently picked part type
        FacePartType pickedPart = compositeData.getCurrentPart();

        placePart(pickedPart, sex, ageBottomLimit, ageTopLimit);

        HashMap<FacePartType, ModelInfo> currentComposite = RandomFace.instance().getCurrentCompositeMap();
        ModelInfo head = currentComposite.get(FacePartType.HEAD);

        if (head != null) {        //if there's head in scene

            if (pickedPart != FacePartType.HEAD) {      //if this isn't head, align part to head
                alignToHead(head, currentComposite, pickedPart);
                
            } else {        //if head was added align all parts to it
                Set<FacePartType> k = currentComposite.keySet();

                for (FacePartType p : k) {      //separate key for pair face parts, no need to run getPickedFacePartTypes method
                    if (p == FacePartType.HEAD) {
                        continue;
                    }
                    
                    alignToHead(head, currentComposite, p);
                }
            }
        }
    }
    
    /**
     * Creates entire face, by randomly picking from each part type. Is able to filter parts
     * which are picked based on selected sex and age range.
     * 
     * @param sex - sex of the part to be picked, used for filtering
     * @param ageBottomLimit - bottom limit of age of part to be picked, used for filtering
     * @param ageTopLimit  - top limit of age of part to be picked, used for filtering
     */
    public void createRandomFace(SexType sex, int ageBottomLimit, int ageTopLimit) {
        HashSet<FacePartType> parts = RandomFace.instance().getCroppedFacePartTypes(); //to avoid using LeftEye and RightEye and just use Eye instead (and other pair parts)

        for (FacePartType pickedPart : parts) {
            placePart(pickedPart, sex, ageBottomLimit, ageTopLimit);
        }

        HashMap<FacePartType, ModelInfo> currentComposite = RandomFace.instance().getCurrentCompositeMap();

        ModelInfo head = currentComposite.get(FacePartType.HEAD);
        Set<FacePartType> k = currentComposite.keySet();

        for (FacePartType p : k) {
            if (p == FacePartType.HEAD) {
                continue;
            }
            
            alignToHead(head, currentComposite, p);
        }
    }
    
    //randomly picks part based on chosen type. Filters parts to satisfy sex and age range requirements
    private void placePart(FacePartType pickedPart, SexType sex, int ageBottomLimit, int ageTopLimit){
        pickSingleRandomPart(pickedPart, sex, ageBottomLimit, ageTopLimit);
        RandomFace.instance().resetPartTransform(pickedPart, compositeData);     //reset to inital position to counter all previous user input
    }
    
    //align given face part to current head
    private void alignToHead(ModelInfo head, HashMap<FacePartType, ModelInfo> currentComposite, FacePartType pickedPart){
        List<Vector3f> transformedPoints;
        List<Vector3f> movedPoints;
        FacePartType[] fp = RandomFace.instance().getPickedFacePartTypes(pickedPart);

                //in case there are pair parts, peform ICP for each part separately
                for (FacePartType p : fp) {
                    ModelInfo currentModelInfo = currentComposite.get(p);
                    
                    if(currentModelInfo == null){
                        //part wasn't added to composite, this is because 
                        //ears and forhead are currently not placed onto the model automatically
                        break;
                    }
                    
                    KdTree headTree = RandomFace.instance().getKdTreeOfHeadPoints(head, p, compositeData);
                    List<Vector3f> partPosition = currentModelInfo.getPosition();
                    CompositeModel cm = compositeData.getPartCompositeModel(p);
                    

                    transformedPoints = RandomFace.instance().getTranslatedPartPoints(partPosition, cm);
                    movedPoints = cm.applyTransform(transformedPoints);
                    Icp.instance().icp(headTree, movedPoints, partPosition, ERROR_RATE, MAX_ITERATIONS, allowScale);
                }
    }
    
    
    //Randonly picks one of the models for given part doesn't pick forhead or ears atm
    private void pickSingleRandomPart(FacePartType part, SexType sex, int ageBottomLimit, int ageTopLimit) {
        if (part == FacePartType.FORHEAD || part == FacePartType.EARS) {
            return;
        }

        Composite composite = compositeData;

        Random randomPick = new Random();

        //check if picked part is paired(eyes, eybrows, etc.) or not
        boolean isPair = RandomFace.instance().isPair(part);
        ModelInfo[] model = new ModelInfo[2];

        //get all models for given face part
        Map<Object, ModelInfo> models = pickCorrectModels(part, sex, ageBottomLimit, ageTopLimit);
        
        //no models to choose from
        if(models.size() <= 0){
            return;
        }

        //pick one of filtered keys
        int i = randomPick.nextInt(models.size());
        Object pickedKey = models.keySet().toArray()[i];
        String keyName = pickedKey.toString();
        
       

        if (isPair) {            
            keyName = keyName.substring(0, keyName.length() - 1);

            model[0] = models.get(keyName  + "a");
            model[1] = models.get(keyName + "b");
        } else {
            model[0] = models.get(keyName);
            model[1] = null;
        }

        //Resolve placing given model into scene
        RandomFace.instance().pickRandomPart(model, composite, part, i);
    }

    //Picks all the models for given part of face(all are set as attributes of this class)
    private Map<Object, ModelInfo> pickCorrectModels(FacePartType part, SexType sex, int ageBottomLimit, int ageTopLimit) {
        Map<Object, ModelInfo> pickedPart;

        switch (part) {
            case EYES:
                pickedPart = filterParts(eyesModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case EYEBROWS:
                pickedPart = filterParts(eyebrowsModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case EARS:
                pickedPart = filterParts(earsModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case HEAD:
                pickedPart = filterParts(headModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case FORHEAD:
                pickedPart = filterParts(forheadModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case NOSE:
                pickedPart = filterParts(noseModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case MOUTH:
                pickedPart = filterParts(mouthModels, sex, ageBottomLimit, ageTopLimit);
                break;
            case CHIN:
                pickedPart = filterParts(chinModels, sex, ageBottomLimit, ageTopLimit);
                break;
            default:
                pickedPart = null;
                break;
        }

        return pickedPart;
    }
    
    //filters parts based on sex and age range requirements, Returns map with filtered results
    private Map<Object,ModelInfo> filterParts(Map<Object, ModelInfo> pickedParts, SexType sex, int ageBottom, int ageTop){
        Map<Object, ModelInfo> filteredParts = new HashMap<Object, ModelInfo>();
        Set<Object> keySet = pickedParts.keySet();
        
        for(Object key : keySet){
            ModelInfo currentPart = pickedParts.get(key);
            
            if((sex == SexType.BOTH || currentPart.getSex() == sex) && (currentPart.getAge() < 0 ||
                    (currentPart.getAge() >= ageBottom && currentPart.getAge() <= ageTop))){
                filteredParts.put(key.toString(), currentPart);
            }
        }
        
        return filteredParts;
    } 
}
