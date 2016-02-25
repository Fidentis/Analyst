/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;



/**
 *
 * @author Katka
 */
public class ModelInfo {
/**
     * Edit mode constatnt.
     */
    public static final int LEFT = 2;
    /**
     * Edit mode constatnt.
     */
    public static final int RIGHT = 1;
    /**
     * Edit mode constatnt.
     */
    
private int age = -1;
private SexType sex; 
private String part;
private File file;
private List<Vector3f> position;
private List<Vector3f> foreheadPosition;
private List<Vector3f> nosePosition;
private List<Vector3f> mouthPosition;
private List<Vector3f> chinPosition;
private List<Vector3f> leftearPosition;
private List<Vector3f> rightearPosition;
private List<Vector3f> lefteyePosition;
private List<Vector3f> righteyePosition;
private List<Vector3f> lefteyebrowPosition;
private List<Vector3f> righteyebrowPosition;


    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(String sex) {
        String lowerCase = sex.toLowerCase();
        SexType type = SexType.BOTH;
        
        if(lowerCase.equals("female")){
            type = SexType.FEMALE;
        }else if(lowerCase.equals("male")){
            type = SexType.MALE;
        }
        
        this.sex = type;
    }

    public List<Vector3f> getPosition() {
        return position;
    }

    public void addPosition(Vector3f position) {
        if(this.position == null){
            this.position = new ArrayList<Vector3f>();
        }
        
        this.position.add(position);
    }

    public List<Vector3f> getForeheadPosition() {
        return foreheadPosition;
    }

    public void addForeheadPosition(Vector3f foreheadPsition) {
        if(this.foreheadPosition == null){
            this.foreheadPosition = new ArrayList<Vector3f>();
        }
        
        this.foreheadPosition.add(foreheadPsition);
    }

    public List<Vector3f> getNosePosition() {
        return nosePosition;
    }

    public void setNosePosition(Vector3f nosePosition) {
        if(this.nosePosition == null){
            this.nosePosition = new ArrayList<Vector3f>();
        }
        
        this.nosePosition.add(nosePosition);
    }

    public List<Vector3f> getMouthPosition() {
        return mouthPosition;
    }

    public void addMouthPosition(Vector3f mouthPosition) {
        if(this.mouthPosition == null){
            this.mouthPosition = new ArrayList<Vector3f>();
        }
        
        this.mouthPosition.add(mouthPosition);
    }

    public List<Vector3f> getChinPosition() {
        return chinPosition;
    }

    public void addChinPosition(Vector3f chinPosition) {
        if(this.chinPosition == null){
            this.chinPosition = new ArrayList<>();
        }
        
        this.chinPosition.add(chinPosition);
    }

    public List<Vector3f> getLeftearPosition() {
        return leftearPosition;
    }

    public void addLeftearPosition(Vector3f leftearPosition) {
        if(this.leftearPosition == null){
            this.leftearPosition = new ArrayList<>();
        }
        
        this.leftearPosition.add(leftearPosition);
    }

    public List<Vector3f> getRightearPosition() {
        return rightearPosition;
    }

    public void addRightearPosition(Vector3f rightearPosition) {
        if(this.rightearPosition == null){
            this.rightearPosition = new ArrayList<>();
        }
        
        this.rightearPosition.add(rightearPosition);
    }

    public List<Vector3f> getLefteyePosition() {
        return lefteyePosition;
    }

    public void addLefteyePosition(Vector3f lefteyePosition) {
        if(this.lefteyePosition == null){
            this.lefteyePosition = new ArrayList<>();
        }
        
        this.lefteyePosition.add(lefteyePosition);
    }

    public List<Vector3f> getRighteyePosition() {
        return righteyePosition;
    }

    public void addRighteyePosition(Vector3f righteyePosition) {
        if(this.righteyePosition == null){
            this.righteyePosition = new ArrayList<>();
        }
        
        this.righteyePosition.add(righteyePosition);
    }

    public List<Vector3f> getLefteyebrowPosition() {
        return lefteyebrowPosition;
    }

    public void addLefteyebrowPosition(Vector3f lefteyebrowPosition) {
        if(this.lefteyebrowPosition == null){
            this.lefteyebrowPosition = new ArrayList<>();
        }
        
        this.lefteyebrowPosition.add(lefteyebrowPosition);
    }

    public List<Vector3f> getRighteyebrowPosition() {
        return righteyebrowPosition;
    }

    public void addRighteyebrowPosition(Vector3f righteyebrowPosition) {
        if(this.righteyebrowPosition == null){
            this.righteyebrowPosition = new ArrayList<>();
        }
        
        this.righteyebrowPosition.add(righteyebrowPosition);
    }

    public List<Vector3f> getPositionOfPart(FacePartType part){
        List<Vector3f> pickedPosition = null;
        
        switch (part) {
            case RIGHT_EYE:
                pickedPosition = righteyePosition;
                break;
            case LEFT_EYE:  
                pickedPosition = lefteyePosition;
                break;
            case RIGHT_EAR:
                pickedPosition = rightearPosition;
                break;
            case LEFT_EAR:        
                pickedPosition = leftearPosition;
                break;
            case RIGHT_EYEBROW:
                pickedPosition = righteyebrowPosition;
                break;
            case LEFT_EYEBROW:
                pickedPosition = lefteyebrowPosition;
                break;
            case FORHEAD:
                pickedPosition = foreheadPosition;
                break;
            case NOSE:
                pickedPosition = nosePosition;
                break;
            case MOUTH:
                pickedPosition = mouthPosition;
                break;
            case CHIN:
                pickedPosition = chinPosition;
                break;
        }
        
        return pickedPosition;
    }

}
