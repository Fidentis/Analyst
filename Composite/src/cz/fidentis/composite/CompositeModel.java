/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.composite;

import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * This class represents a somatoscopic sign.
 * 
 * @author Katarína Furmanová
 */
public class CompositeModel {

    private Quat4f rotQuat = new Quat4f(0, 0, 0, 1);
    private Quat4f initRotQuat = new Quat4f(0, 0, 0, 1);
    private ArrayList<Vector3f> originalVerts = new ArrayList<Vector3f>();
    private ArrayList<Vector3f> originalNormals = new ArrayList<Vector3f>();
    private Model model;
    private FacePartType part;
    private Vector3f shift = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Vector3f initialShift;
    private Vector3f initialRotation;
    private int editMode = 0;
    private Boolean visible = true;

    /**
     * 
     */
    public CompositeModel() {
        initialShift = new Vector3f();
        initialRotation = new Vector3f();

    }

    /**
     *Sets initial rotation for the model and transforms it.
     */
    public void initRotation() {
        float rotationX = 0.0f;
        float rotationY = 0.0f;
        float rotationZ = 0.0f;
      switch (part) {
            case LEFT_EAR:
                rotationY = 40.0f;
                break;
            case RIGHT_EAR:
                rotationY = -40.0f;
                break;
        }
        initialRotation = new Vector3f(rotationX, rotationY, rotationZ);
         
        initRotQuat = new Quat4f(0, 0, 0, 1);
        if (initialRotation.x != 0) {
            Quat4f xRot = new Quat4f();           
            xRot.set(new AxisAngle4f(1f,0f,0f, (float) Math.toRadians(initialRotation.x)));  
            initRotQuat.mul(xRot,initRotQuat);
        }
        if (initialRotation.y != 0) {
            Quat4f yRot = new Quat4f();
            yRot.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(initialRotation.y)));
            initRotQuat.mul(yRot,initRotQuat);
        }

        if (initialRotation.z != 0) {
            Quat4f zRot = new Quat4f();
            zRot.set(new AxisAngle4f(0f, 0f, 1f, (float) Math.toRadians(initialRotation.z)));
            initRotQuat.mul(zRot,initRotQuat);
        }/* */
        transform();
    }


    private void transform() {
        float shiftX = shift.getX() + initialShift.getX();
        float shiftY = shift.getY() + initialShift.getY();
        float shiftZ = shift.getZ() + initialShift.getZ();

        Quat4f rotationQuat = new  Quat4f(0,0,0,1) ;
        rotationQuat.mul(rotQuat,initRotQuat);
        
        for (int i = 0; i < model.getVerts().size(); i++) {
            if (originalVerts.size() <= i) {
                originalVerts.add(new Vector3f(model.getVerts().get(i).getX(), model.getVerts().get(i).getY(), model.getVerts().get(i).getZ()));
            }
            Vector3f vert = new Vector3f();
            vert.setX(originalVerts.get(i).getX());
            vert.setY(originalVerts.get(i).getY());
            vert.setZ(originalVerts.get(i).getZ());

            vert = rotate(vert,rotationQuat);

            model.getVerts().get(i).setX(vert.getX() * scale.getX() + shiftX);
            model.getVerts().get(i).setY(vert.getY() * scale.getY() + shiftY);
            model.getVerts().get(i).setZ(vert.getZ() * scale.getZ() + shiftZ);
        }
        for (int i = 0; i < model.getNormals().size(); i++) {
            if (originalNormals.size() <= i) {
                originalNormals.add(new Vector3f(model.getNormals().get(i).getX(), model.getNormals().get(i).getY(), model.getNormals().get(i).getZ()));
            }

            Vector3f vert = new Vector3f();
            vert.setX(originalNormals.get(i).getX());
            vert.setY(originalNormals.get(i).getY());
            vert.setZ(originalNormals.get(i).getZ());

            vert = rotate(vert,rotationQuat);

            model.getNormals().get(i).setX(vert.getX());
            model.getNormals().get(i).setY(vert.getY());
            model.getNormals().get(i).setZ(vert.getZ());
        }



        ArrayList<Vector3f> bb = new ArrayList<Vector3f>();
        for (int i = 0; i < model.getModelDims().getBoundingBox().size(); i++) {
            Vector3f vert = new Vector3f();
            vert.setX(model.getModelDims().getCentralizedBoundingBox().get(i).getX());
            vert.setY(model.getModelDims().getCentralizedBoundingBox().get(i).getY());
            vert.setZ(model.getModelDims().getCentralizedBoundingBox().get(i).getZ());

            vert = rotate(vert,rotationQuat);

            vert.setX(vert.getX() * scale.getX() + shiftX);
            vert.setY(vert.getY() * scale.getY() + shiftY);
            vert.setZ(vert.getZ() * scale.getZ() + shiftZ);


            bb.add(vert);
        }
        model.getModelDims().setBoundingBox(bb);
    }
    
    //doesn't probably work on subsequent transforms
    public void applyInitTransform(List<Vector3f> helperPoints) {
        float shiftX = shift.getX() + initialShift.getX();
        float shiftY = shift.getY() + initialShift.getY();
        float shiftZ = shift.getZ() + initialShift.getZ();

        Quat4f rotationQuat = new  Quat4f(0,0,0,1) ;
        rotationQuat.mul(rotQuat,initRotQuat);
        
        for (int i = 0; i < helperPoints.size(); i++) {
            Vector3f vert = new Vector3f();
            vert.setX(helperPoints.get(i).getX());
            vert.setY(helperPoints.get(i).getY());
            vert.setZ(helperPoints.get(i).getZ());

            vert = rotate(vert,rotationQuat);

            helperPoints.get(i).setX(vert.getX() * scale.getX() + shiftX);
            helperPoints.get(i).setY(vert.getY() * scale.getY() + shiftY);
            helperPoints.get(i).setZ(vert.getZ() * scale.getZ() + shiftZ);
        }
    }
    
    public List<Vector3f> applyTransform(List<Vector3f> helperPoints) {
        List<Vector3f> toMove = new ArrayList<Vector3f>();
        
        for(Vector3f v : helperPoints){
            toMove.add(new Vector3f(v.x, v.y, v.z));
        }
        
        applyInitTransform(toMove);
        
        return toMove;
    }

    private Vector3f rotate(Vector3f vert, Quat4f rotQuat) {
        if(rotQuat.x != 0 || rotQuat.y != 0 || rotQuat.z != 0){  
        float size = vert.length();    
        Quat4f vertQuat = new Quat4f(vert.x,vert.y,vert.z,0);
            Quat4f conjungQuat = new Quat4f(0,0,0,1);
            conjungQuat.conjugate(rotQuat);
            vertQuat.mul(rotQuat, vertQuat);
            vertQuat.mul(vertQuat,conjungQuat);
            vert.x = vertQuat.x*size;
            vert.y = vertQuat.y*size;
            vert.z = vertQuat.z*size;
        }
         return vert;
    }
    
    
    /**
     *
     * @return Model object of the somatoscopic sign.
     */
    public Model getModel() {
        return model;
    }

    /**
     * Sets model of this somatoscopis sign.
     * 
     * @param model model object.
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     *
     * @return Returns type of the somatoscopic sign this object represents (NOSE, MOUTH, ...).
     */
    public FacePartType getPart() {
        return part;
    }

    /**
     *
     * @param part  type of the somatoscopic sign this object represents (NOSE, MOUTH, ...).
     */
    public void setPart(FacePartType part) {
        this.part = part;
    }

    /**
     *
     * @return Returns the shift agints basic position (initial shift) of the model.
     */
    public Vector3f getTranslation() {
        return shift;
    }

    /**
     *
     * @param shift shift agints basic position (initial shift) of the model.
     */
    public void setTranslation(Vector3f shift) {

        this.shift = shift;
        transform();


    }

    /**
     *
     * @param scale scale of the object. 
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
        transform();

    }

    /**
     *
     * @return Returns scale of the object.
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     *
     * @return Returns rotation of the object.
     */
    public Vector3f getRotation() {
        rotation = new Vector3f();
        rotation = quaternionToEuler(rotQuat);
        return rotation;
    }

    /**
     *
     * @return Returns rotation quaternion of the object.
     */
    public Quat4f getRotQuat() {
        return rotQuat;
    }

    /**
     * Sets rotation quaternion of the model.
     * 
     * @param rotQuat rotation quaternion of the model.
     */
    public void setRotQuat(Quat4f rotQuat) {
        this.rotQuat = rotQuat;
    }

    /**
     * Sets rotation quaternion of the model and transforms th model.
     * 
     * @param rotQuat rotation quaternion of the object.
     */
    public void setRotation(Quat4f rotQuat) {
        this.rotQuat = rotQuat;
        transform();
    }

    /**
     * If this object represents par somatoscopic sign (eyes, ears or eyebrows), edit mode decides which part shoud be edited.
     * 
     * @return Returns 0 - if edit mode is BOTH, 1 - if edit mode is RIGHT, 2 - if edit mode is LEFT 
     */
    public int getEditMode() {
        return editMode;
    }

    /**
     * If this object represents par somatoscopic sign (eyes, ears or eyebrows), edit mode decides which part shoud be edited.
     * 
     * @param editMode set 0 - if edit mode is BOTH, 1 - if edit mode is RIGHT, 2 - if edit mode is LEFT 
     */
    public void setEditMode(int editMode) {
        this.editMode = editMode;
    }

    /**
     * Sets the initial shift (basic position) of model.
     * 
     * @param initialShift initial shift of model.
     */
    public void setInitialShift(Vector3f initialShift) {
        this.initialShift = initialShift;
        transform();
    }

    /**
     * Sets the initial rotation (basic position) of model.
     * 
     * @param initialRotation initial rotation
     */
    public void setInitialRotation(Vector3f initialRotation) {
        Quat4f xRot = new Quat4f();
        xRot.set(new AxisAngle4f(1f, 0f, 0f, (float) Math.toRadians(initialRotation.x)));
        Quat4f yRot = new Quat4f();
        yRot.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(initialRotation.y)));
        Quat4f zRot = new Quat4f();
        zRot.set(new AxisAngle4f(0f, 0f, 1f, (float) Math.toRadians(initialRotation.z)));
        initRotQuat = new Quat4f(0, 0, 0, 1);
        initRotQuat.mul(xRot,initRotQuat);
        initRotQuat.mul(yRot,initRotQuat);
        initRotQuat.mul(zRot,initRotQuat);
        this.initialRotation = initialRotation;
    }

    /**
     *
     * @return Returns initial shift (basic position) of model.
     */
    public Vector3f getInitialShift() {
        return initialShift;
    }

    /**
     *
     * @return Returns initial rotation (basic position) of model.
     */
    public Vector3f getInitialRotation() {
       return new Vector3f();
       // return initialRotation;
    }

    /**
     * Says, whether the modle is visible.
     * 
     * @return Returns true if model is visible.
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * Sets visibility of the model.
     * 
     * @param visible true, if model should be visible.
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    
    private Vector3f quaternionToEuler(Quat4f quat) {
        Vector3f p = new Vector3f();

        double sqw = quat.w * quat.w;
        double sqx = quat.x * quat.x;
        double sqy = quat.y * quat.y;
        double sqz = quat.z * quat.z;

        double unit = sqx + sqy + sqz + sqw; 
        double test = quat.x * quat.y + quat.z * quat.w;
        if (test > 0.499 * unit) { // singularity at north pole
            p.y = (float) (2 * Math.atan2(quat.x, quat.w));
            p.z = (float) (Math.PI * 0.5);
            p.x = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            p.y = (float) (-2 * (Math.atan2(quat.x, quat.w)));
            p.z = (float) (-Math.PI * 0.5);
            p.x = 0;
        } else {
            p.y = (float) Math.toDegrees((Math.atan2(2 * quat.y * quat.w - 2 * quat.x * quat.z, sqx - sqy - sqz + sqw)));
            p.z = (float) Math.toDegrees((Math.asin(2 * test / unit)));
            p.x = (float) Math.toDegrees((Math.atan2(2 * quat.x * quat.w - 2 * quat.y * quat.z, -sqx + sqy - sqz + sqw)));
        }

        return p;
    }
    
    @Override
    public String toString() {
        switch (part) {
            case HEAD:
                return "Head";

            case LEFT_EYE:
                return "Eyes";
            case RIGHT_EYE:
                return "Eyes";
            case LEFT_EYEBROW:
                return "Eyebrows";
            case RIGHT_EYEBROW:
                return "Eyebrows";
            case NOSE:
                return "Nose";

            case LEFT_EAR:
                return "Ears";

            case RIGHT_EAR:
                return "Ears";

            case FORHEAD:
                return "Forhead";

            case MOUTH:
                return "Mouth";

            case CHIN:
                return "Chin";



        }
        return null;
    }
}
