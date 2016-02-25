/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.composite;

import cz.fidentis.model.Model;
//import cz.fidentis.model.Vector3f;
import java.awt.Point;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class Manipulator {

    //   private GLU glu;
    private Vector3f rotA=new Vector3f();;
    private Vector3f rotB=new Vector3f();;
    private Vector3f cameraPosition;
    private ArrayList<CompositeModel> models = new ArrayList<CompositeModel>();
    private Vector3f scale = new Vector3f(1, 1, 1);
    private int selectedToolIndex = -1;
    private Vector3f lastIntersection;
    private Vector3f startPoint;
    private Point center;
    private Vector3f startScale;
    private Quat4f rotQuat;
    private Quat4f rotQuat2;
    private float previousAngle;
    
    private Vector3f rotation = new Vector3f();
    private Boolean isTransforming = false;
    private boolean showRotation;
    private boolean showScale;
    private boolean showShift;
    private boolean show = false;

    //  public void setGlu(GLU glu) {
    //      this.glu = glu;
    //  }
    /**
     * Sets the position of gizmo.
     * @param destination position of gizmo.
     */
    public void shiftManipulators(Vector3f destination) {
        for (int i = 0; i < 9; i++) {
            if (models.get(i) != null) {
                models.get(i).setTranslation(destination);
            }
        }

    }

    /**
     * Shows the gizmo for positioning.
     */
    public void showShift() {
        showRotation = false;
        showScale = false;
        showShift = true;

    }

    /**
     * Shows the gizmo for rotation.
     */
    public void showRotation() {
        showRotation = true;
        showScale = false;
        showShift = false;

    }

    /**
     * Shows the gizmo for resizing.
     */
    public void showScale() {
        showRotation = false;
        showScale = true;
        showShift = false;

    }

    /**
     * Adds models of gizmo.
     * 
     * @param  model of part of the gizmo.
     */
    public void addModel(Model model) {
        CompositeModel m = new CompositeModel();
        m.setModel(model);

        if (models.isEmpty()) {
            m.setInitialShift(new Vector3f(39, 0.5f, 0));
        }
        if (models.size() == 1) {
            m.setInitialShift(new Vector3f(0, 39, 0));
        }
        if (models.size() == 2) {
            m.setInitialShift(new Vector3f(0, 0, -39));
        }

        if (models.size() == 6) {
            m.setInitialShift(new Vector3f(39.3f, 0.3f, 0));
        }
        if (models.size() == 7) {
            m.setInitialShift(new Vector3f(0, 39, 0));
        }
        if (models.size() == 8) {
            m.setInitialShift(new Vector3f(0, 0, -39));
        }
        float scaleF = 0.8f;
        m.setScale(new Vector3f(scaleF, scaleF, scaleF));
        m.setInitialShift(new Vector3f(m.getInitialShift().getX() * scaleF, m.getInitialShift().getY() * scaleF, m.getInitialShift().getZ() * scaleF));

        models.add(m);

    }

    /**
     * 
     * @return Returns true, iif gizmo is visible.
     */
    public boolean isShowing() {
        return show;
    }

    /**
     * Hides gizmo.
     */
    public void hideManipulators() {
        show = false;
    }

    /**
     * Sets the position on gizmo.
     * @param center position of center of a gizmo.
     */
    public void showManipulators(Vector3f center) {
        show = true;
        shiftManipulators(center);
    }

    /**
     * Transform object.
     * 
     * @param start starting position of mouse
     * @param destination final position of mouse
     * @param rotation rotation of object (right in case of pair)
     * @param rotation2 rotation of left object in case of pair
     * @param keepProportions keep proportion vhen resizing
     * @param viewport viewport
     * @param modelViewMatrix modelViewMatrix
     * @param projectionMatrix projectionMatrix
     * @param glu glu
     */
    public void doManipulation(Point start, Point destination, Quat4f rotation,Quat4f rotation2, Boolean keepProportions, int[] viewport,
            double[] modelViewMatrix, double[] projectionMatrix, GLU glu) {

        if (showShift) {
            if (!isTransforming) {               
                this.rotation = new Vector3f();
                isTransforming = true;
                startScale = scale;
                rotQuat = rotation;
                rotQuat2 = rotation2;
            //    this.rotation = startRotation;
                startPoint = models.get(selectedToolIndex).getTranslation();
                center = get2DPoint(startPoint, viewport, modelViewMatrix, projectionMatrix, glu);
            }
            shift(start, destination);

        }
        if (showRotation) {
            if (!isTransforming) {
                startScale = scale;
                previousAngle =0;
                rotQuat = rotation;
                rotQuat2 = rotation2;
 //               this.rotation = startRotation;
                isTransforming = true;
                startPoint = models.get(selectedToolIndex).getTranslation();
                center = get2DPoint(startPoint, viewport, modelViewMatrix, projectionMatrix, glu);
            }
            rotate(start, destination,viewport, modelViewMatrix, projectionMatrix,glu);
        }
        if (showScale) {
            if (!isTransforming) {
                this.rotation = new Vector3f();
                isTransforming = true;
                startScale = scale;
                rotQuat = rotation;
                rotQuat2 = rotation2;
//                startRotation = rotation;
//                this.rotation = startRotation;
                startPoint = models.get(selectedToolIndex).getTranslation();
                center = get2DPoint(startPoint, viewport, modelViewMatrix, projectionMatrix, glu);
            }
            scale(start, destination, keepProportions);
        }
    }

    /**
     * Stop transformation.
     */
    public void stopTransforming() {
        isTransforming = false;
        selectedToolIndex = -1;
    }

    private void rotate(Point start, Point destination,int[] viewport,
            double[] modelViewMatrix, double[] projectionMatrix, GLU glu) {
        double angle;
        if (selectedToolIndex == 3) {
            lastIntersection.x=startPoint.x;
            Vector3f bv = new Vector3f(0, lastIntersection.z-startPoint.z,startPoint.y-lastIntersection.y);
            rotA = lastIntersection;
            rotB= new Vector3f(lastIntersection.x, lastIntersection.y+bv.y,lastIntersection.z + bv.z);
        }
        else if (selectedToolIndex == 4) {
            lastIntersection.y=startPoint.y;
            Vector3f bv = new Vector3f(lastIntersection.z-startPoint.z,0,startPoint.x-lastIntersection.x);
            rotA = lastIntersection;
            rotB= new Vector3f(lastIntersection.x+bv.x,lastIntersection.y,lastIntersection.z + bv.z); 
        }
        else if (selectedToolIndex == 5) {
            lastIntersection.z=startPoint.z;
            Vector3f bv = new Vector3f(lastIntersection.y-startPoint.y,startPoint.x-lastIntersection.x,0);
            rotA = lastIntersection;
            rotB= new Vector3f(lastIntersection.x+bv.x,lastIntersection.y + bv.y,lastIntersection.z); 
        }
        Point a = get2DPoint(rotA, viewport, modelViewMatrix, projectionMatrix, glu);
        Point b = get2DPoint(rotB, viewport, modelViewMatrix, projectionMatrix, glu);
      
        double x = b.x - a.x;
        double y = b.y - a.y;
        double k = (destination.x * x - a.x * x + destination.y * y - a.y * y) / (x * x + y * y);
        Point p = new Point((int) (start.x + k * x), (int) (start.y + k * y));
        angle = Math.sqrt((start.x - p.x) * (start.x - p.x) + (start.y - p.y) * (start.y - p.y)) / 100 * 90;
        
        if (k < 0) {
                angle = -angle;
        }
        
        if (selectedToolIndex == 3) {
            rotation = new Vector3f((float) angle - previousAngle, 0, 0);
           // rotation = new Vector3f(startRotation.getX() + (float) angle, startRotation.getY(), startRotation.getZ());
        } else if (selectedToolIndex == 4) {
            angle = -angle;
            rotation = new Vector3f(0,(float) angle- previousAngle,  0);
           // rotation = new Vector3f(startRotation.getX(), startRotation.getY() + (float) angle, startRotation.getZ());
        } else {
            rotation = new Vector3f(0,  0, (float) angle- previousAngle);
          //  rotation = new Vector3f(startRotation.getX(), startRotation.getY(), startRotation.getZ() + (float) angle);
        }
        previousAngle = (float) angle;
    }

    private void shift(Point start, Point destination) {
        double x = center.x - start.x;
        double y = center.y - start.y;
        double initialDistance = Math.sqrt(x * x + y * y);

        double distanceRatio = 1;
        if (selectedToolIndex == 0) {
            distanceRatio = Math.abs(startPoint.getX() - lastIntersection.getX()) / initialDistance;
        }
        if (selectedToolIndex == 1) {
            distanceRatio = Math.abs(startPoint.getY() - lastIntersection.getY()) / initialDistance;
        }
        if (selectedToolIndex == 2) {
            distanceRatio = Math.abs(startPoint.getZ() - lastIntersection.getZ()) / initialDistance;
        }

       
        double k = (destination.x * x - start.x * x + destination.y * y - start.y * y) / (x * x + y * y);
        double distance = Math.sqrt((( - k * x) * (- k * x)) + ((- k * y) * (- k * y))) * distanceRatio;

        if (k > 0) {
            distance = -distance;
        }

        if (selectedToolIndex == 0) {
            for (int i = 0; i < 9; i++) {
                if (models.get(i) != null) {
                    models.get(i).setTranslation(new Vector3f((float) (startPoint.getX() + distance), models.get(i).getTranslation().getY(), models.get(i).getTranslation().getZ()));
                }
            }
        }
        if (selectedToolIndex == 1) {
            for (int i = 0; i < 9; i++) {
                if (models.get(i) != null) {
                    models.get(i).setTranslation(new Vector3f(models.get(i).getTranslation().getX(), (float) (startPoint.getY() + distance), models.get(i).getTranslation().getZ()));
                }
            }
        }
        if (selectedToolIndex == 2) {
            for (int i = 0; i < 9; i++) {
                if (models.get(i) != null) {
                    models.get(i).setTranslation(new Vector3f(models.get(i).getTranslation().getX(), models.get(i).getTranslation().getY(), (float) (startPoint.getZ() - distance)));
                }
            }
        }


    }

    private void scale(Point start, Point destination, Boolean keepProportions) {
        double x = center.x - start.x;
        double y = center.y - start.y;
        double initialDistance = Math.sqrt(x *x + y *y);

        double distanceRatio = 1;
        if (selectedToolIndex == 6) {
            distanceRatio = startScale.getX() / initialDistance;
        }
        if (selectedToolIndex == 7) {
            distanceRatio = startScale.getY() / initialDistance;
        }
        if (selectedToolIndex == 8) {
            distanceRatio = startScale.getZ() / initialDistance;
        }

        double k = (destination.x * x - start.x * x + destination.y * y - start.y * y) / (x * x + y * y);
        double distance = Math.sqrt((( - k * x) * (- k * x)) + ((- k * y) * (- k * y))) * distanceRatio;

        if (k > 0) {
            distance = -distance;
        }



        if (selectedToolIndex == 6) {
            float d = (float) distance + startScale.getX();
            if (d <= 0) {
                d = 0.001f;
            }
            if (keepProportions) {
                float ratio = d / startScale.getX();
                scale = new Vector3f(d, ratio * startScale.getY(), ratio * startScale.getZ());
            } else {
                scale = new Vector3f(d, startScale.getY(), startScale.getZ());
            }
        }
        if (selectedToolIndex == 7) {

            float d = (float) distance + startScale.getY();
            if (d <= 0) {
                d = 0.001f;
            }
            if (keepProportions) {
                float ratio = d / startScale.getY();
                scale = new Vector3f(ratio * startScale.getX(), d, ratio * startScale.getZ());
            } else {
                scale = new Vector3f(startScale.getX(), d, startScale.getZ());
            }
        }
        if (selectedToolIndex == 8) {
            float d = (float) distance + startScale.getZ();
            if (d <= 0) {
                d = 0.001f;
            }
            if (keepProportions) {
                float ratio = d / startScale.getZ();
                scale = new Vector3f(ratio * startScale.getX(), ratio * startScale.getY(), d);
            } else {
                scale = new Vector3f(startScale.getX(), startScale.getY(), d);
            }
        }
    }

    /**
     * Select model (gizmo) that was clicked on.
     * 
     * @param mouseReleasedX  x position of mouse
     * @param mouseReleasedY  y position of mouse
     * @param viewport viewport 
     * @param modelViewMatrix modelViewMatrix
     * @param projectionMatrix projectionMatrix
     * @param glu glu
     * @return Returns true if any model was selected.
     */
    public Boolean pickObject(double mouseReleasedX, double mouseReleasedY, int[] viewport,
        double[] modelViewMatrix, double[] projectionMatrix, GLU glu) {
        ModelSelector picker = new ModelSelector(glu);
         Model model = null;
        if (show) {
            ArrayList<Model> m = new ArrayList<Model>();
            if (showShift) {
                for (int i = 0; i < 3; i++) {
                    m.add(models.get(i).getModel());
                }
                model = picker.pickBoundingBoxModel(mouseReleasedX, mouseReleasedY, m, viewport, modelViewMatrix, projectionMatrix,cameraPosition);
                       
            }
            if (showScale) {
                for (int i = 6; i < 9; i++) {
                    m.add(models.get(i).getModel());
                }
                model = picker.pickBoundingBoxModel(mouseReleasedX, mouseReleasedY, m, viewport, modelViewMatrix, projectionMatrix,cameraPosition);
            
            }
            if (showRotation) {
                for (int i = 3; i < 6; i++) {
                    m.add(models.get(i).getModel());
                }
                model = picker.pickModel(mouseReleasedX, mouseReleasedY, m, viewport, modelViewMatrix, projectionMatrix,cameraPosition);
            }

            if (model != null) {
                if (showShift) {
                    selectedToolIndex = m.lastIndexOf(model);
                }
                if (showScale) {
                    selectedToolIndex = m.lastIndexOf(model) + 6;
                }
                if (showRotation) {
                    selectedToolIndex = m.lastIndexOf(model) + 3;
                }
                lastIntersection = picker.getIntersection();
                return true;
            }
        }
        return false;
    }

    private Point get2DPoint(Vector3f coords, int[] viewport,double[] modelViewMatrix, double[] projectionMatrix, GLU glu) {
        double coordsTransformed[] = new double[3];
        glu.gluProject(
                coords.getX(),
                coords.getY(),
                coords.getZ(),
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                coordsTransformed, 0);
        coordsTransformed[1] = viewport[3] - coordsTransformed[1] - 1;
        return new Point((int) coordsTransformed[0], (int) coordsTransformed[1]);
    }

    /**
     *
     * @return
     */
    public Vector3f getShift() {
        return models.get(0).getTranslation();
    }

    /**
     *
     * @param i set 1 if you want to get rotation of right model in case of pair, 2 to get left model rotation
     * @return Rotation of model.
     */
    public Quat4f getRotation(int i) {    
        Quat4f quat;
        if(i==1){
            quat = new Quat4f(rotQuat);
        }
        else{
            quat = new Quat4f(rotQuat2);
        }
        
        Vector3f newRotation = new Vector3f(-rotation.x,-i*rotation.y,-i*rotation.z);
        if (newRotation.x != 0) {
            Quat4f xRot = new Quat4f();           
            xRot.set(new AxisAngle4f(1f,0f,0f, (float) Math.toRadians(newRotation.x)));  
            quat.mul(xRot,quat);
        }
        if (newRotation.y != 0) {
            Quat4f yRot = new Quat4f();
            yRot.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(newRotation.y)));
            quat.mul(yRot,quat);
        }

        if (newRotation.z != 0) {
            Quat4f zRot = new Quat4f();
            zRot.set(new AxisAngle4f(0f, 0f, 1f, (float) Math.toRadians(newRotation.z)));
            quat.mul(zRot,quat);
        }
        if (i==1) {
            rotQuat=quat;
        }
        else{
            rotQuat2=quat;
        }

        return quat; 
    }

    /**
     *
     * @return Scale of the gizmo.
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Sets scale of model.
     * @param scale scale of model.
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    /**
     *
     * @return true if rotation gizmo is showing.
     */
    public boolean isShowRotation() {
        return showRotation;
    }

    /**
     *
     * @return true if resizing gizmo is showing.
     */
    public boolean isShowScale() {
        return showScale;
    }

    /**
     *
     * @return true if positioning gizmo is showing.
     */
    public boolean isShowShift() {
        return showShift;
    }

    /**
     *
     * @param position camera position.
     */
    public void setCameraPosition(Vector3f position) {
        cameraPosition = position;
    }


    /**
     *
     * @param ratio scaling ration for gizmo size.
     */
    public void adjustModels(float ratio) {
        for (int i = 0; i < 9; i++) {
            models.get(i).setScale(new Vector3f((models.get(i).getScale().getX() * ratio), models.get(i).getScale().getY() * ratio, models.get(i).getScale().getZ() * ratio));
            models.get(i).setInitialShift(new Vector3f(models.get(i).getInitialShift().getX() * ratio, models.get(i).getInitialShift().getY() * ratio, models.get(i).getInitialShift().getZ() * ratio));

        }
    }
    
    /**
     * Resets size of gizmo.
     */
    public void resetModelSize() {
        float size = 0.8f;
        for (int i = 0; i < 9; i++) {
            float shift = size/models.get(i).getScale().getX();
            models.get(i).setScale(new Vector3f(size,size,size));
            models.get(i).setInitialShift(new Vector3f(models.get(i).getInitialShift().getX() * shift, models.get(i).getInitialShift().getY() *shift, models.get(i).getInitialShift().getZ() *shift));
        }
    }

    /**
     * 
     * @param gl
     */
    public void drawModels(GL2 gl) {
        if (models.size() > 0) {
          //  gl.glBegin(GL.GL_LINES);

           //     gl.glVertex3f(rotA.x, rotA.y, rotA.z);
           //     gl.glVertex3f(rotB.x, rotB.y, rotB.z);
           //  gl.glEnd();
            
            gl.glShadeModel(GL2.GL_SMOOTH);
            if (show && showShift) {
                for (int i = 0; i < 3; i++) {
                    if (models.get(i) != null) {
                        if (selectedToolIndex == i) {
                            float[] color = {1f, 1f, 0f, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                            float[] colorKs = {0, 0, 0, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                            models.get(i).getModel().drawWithoutTextures(gl,null);
                        } else {
                            models.get(i).getModel().draw(gl);
                        }
                    }
                }
            }
            if (show && showScale) {
                for (int i = 6; i < 9; i++) {
                    if (models.get(i) != null) {
                        if (selectedToolIndex == i) {
                            float[] color = {1f, 1f, 0f, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                            float[] colorKs = {0, 0, 0, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                            models.get(i).getModel().drawWithoutTextures(gl);
                        } else {
                            models.get(i).getModel().draw(gl);
                        }
                    }
                }
            }
            if (show && showRotation) {
                for (int i = 3; i < 6; i++) {


                    if (models.get(i) != null) {

                        if (selectedToolIndex == i) {
                            float[] color = {1f, 1f, 0f, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                            float[] colorKs = {0, 0, 0, 1.0f};
                            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                            models.get(i).getModel().drawWithoutTextures(gl,null);
                        } else {
                            models.get(i).getModel().draw(gl);
                        }
                    }
                }
            }
        }
    }
}
