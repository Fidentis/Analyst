/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.renderer;

import cz.fidentis.composite.FacePartType;
import cz.fidentis.composite.Manipulator;
import cz.fidentis.composite.ModelSelector;
import cz.fidentis.controller.Composite;
import cz.fidentis.model.Model;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class CompositeGLEventListener extends GeneralGLEventListener {

    private Manipulator manipulator;
    private ArrayList<Model> models = new ArrayList<Model>();
    private Composite composite;
    private Model selectedModel;
    private Model selectedModel2;
    private int[] viewport = new int[4];
    private double[] modelViewMatrix = new double[16];
    private double[] projectionMatrix = new double[16];
    private ArrayList<Integer> dropF = new ArrayList<Integer>();

    @Override
    public void display(GLAutoDrawable glad) {
        //  gl = (GL2) glad.getGL();
        gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 1);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(xCameraPosition, yCameraPosition, zCameraPosition, 0, 0, 0, xUpPosition, yUpPosition, zUpPosition);

        gl.glPushMatrix();

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        gl.glShadeModel(GL2.GL_SMOOTH);
        for (int i = 0; i < models.size(); i++) {

            if (models.get(i) != null) {
                gl.glPushMatrix();
                float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
                //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                float[] colorKs = {0, 0, 0, 1.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                if (drawTextures) {
                    models.get(i).draw(gl);

                } else {
                    /*   float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
                     //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
                     gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                     gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                     gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                     float[] colorKs = {0, 0, 0, 1.0f};
                     gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);*/
                    if (composite.getHeadIndex() == i) {
                        models.get(i).drawWithoutTextures(gl, dropF);
                    } else {
                        models.get(i).drawWithoutTextures(gl, null);
                    }

                    //models.get(i).drawWithoutTextures(gl, null);
                }

                gl.glPopMatrix();
            }
        }
        drawSelectionCage();

        if (selectedModel != null) {

            gl.glEnable(GL2.GL_CULL_FACE);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            manipulator.drawModels(gl);
            gl.glDisable(GL2.GL_CULL_FACE);

        }
        gl.glPopMatrix();

        gl.glFlush();

    }

    /**
     *
     */
    @Override
    public void reloadTextures() {
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i) != null) {
                for (int j = 0; j < models.get(i).getMatrials().getMatrials().size(); j++) {
                    models.get(i).getMatrials().reloadTextures(gl);
                }
            }
        }
    }

    /**
     * Draw box around selected model.
     */
    public void drawSelectionCage() {
        if (selectedModel != null) {

            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glDisable(GL2.GL_LIGHTING);

            float[] rgb = {1f, 1f, 1f};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, rgb, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, rgb, 0);
            //
            //gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 1);
            gl.glBegin(GL.GL_LINES);

            ArrayList<Vector3f> bb = selectedModel.getModelDims().getBoundingBox();
            for (int i = 0; i < 7; i++) {
                gl.glVertex3f(bb.get(i).getX(), bb.get(i).getY(), bb.get(i).getZ());
                gl.glVertex3f(bb.get(i + 1).getX(), bb.get(i + 1).getY(), bb.get(i + 1).getZ());
            }

            gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
            gl.glVertex3f(bb.get(3).getX(), bb.get(3).getY(), bb.get(3).getZ());

            gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
            gl.glVertex3f(bb.get(5).getX(), bb.get(5).getY(), bb.get(5).getZ());

            gl.glVertex3f(bb.get(1).getX(), bb.get(1).getY(), bb.get(1).getZ());
            gl.glVertex3f(bb.get(6).getX(), bb.get(6).getY(), bb.get(6).getZ());

            gl.glVertex3f(bb.get(2).getX(), bb.get(2).getY(), bb.get(2).getZ());
            gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());

            gl.glVertex3f(bb.get(4).getX(), bb.get(4).getY(), bb.get(4).getZ());
            gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());

            if (selectedModel2 != null) {

                bb = selectedModel2.getModelDims().getBoundingBox();
                for (int i = 0; i < 7; i++) {
                    gl.glVertex3f(bb.get(i).getX(), bb.get(i).getY(), bb.get(i).getZ());
                    gl.glVertex3f(bb.get(i + 1).getX(), bb.get(i + 1).getY(), bb.get(i + 1).getZ());
                }

                gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
                gl.glVertex3f(bb.get(3).getX(), bb.get(3).getY(), bb.get(3).getZ());

                gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
                gl.glVertex3f(bb.get(5).getX(), bb.get(5).getY(), bb.get(5).getZ());

                gl.glVertex3f(bb.get(1).getX(), bb.get(1).getY(), bb.get(1).getZ());
                gl.glVertex3f(bb.get(6).getX(), bb.get(6).getY(), bb.get(6).getZ());

                gl.glVertex3f(bb.get(2).getX(), bb.get(2).getY(), bb.get(2).getZ());
                gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());

                gl.glVertex3f(bb.get(4).getX(), bb.get(4).getY(), bb.get(4).getZ());
                gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());

            }

            gl.glEnd();
            gl.glEnable(GL2.GL_TEXTURE_2D);
            //     gl.glDisable(GL2.GL_LIGHTING);

        }
    }

    /**
     * Reload list of models that should be rendered.
     */
    public void updateModelList() {
        models = composite.getModels();
    }

    /**
     *
     * @param composite composite data asociated with rendered composite
     */
    public void setCompositeData(Composite composite) {
        this.composite = composite;
    }

    /**
     * Refresh data about selected model.
     */
    public void updateSelectedModel() {
        if (composite.getSelectedPart(1) != null && composite.getSelectedPart(1).getVisible()) {
            if (composite.getCurrentPart().equals(FacePartType.EYES) || composite.getCurrentPart().equals(FacePartType.EYEBROWS) || composite.getCurrentPart().equals(FacePartType.EARS)) {
                if (composite.getSelectedPart(1).getEditMode() == 0) {
                    selectedModel = composite.getSelectedPart(1).getModel();
                    selectedModel2 = composite.getSelectedPart(2).getModel();
                } else if (composite.getSelectedPart(1).getEditMode() == 1) {
                    selectedModel = composite.getSelectedPart(1).getModel();
                    selectedModel2 = null;
                } else {
                    selectedModel = composite.getSelectedPart(2).getModel();
                    selectedModel2 = null;
                }
            } else {
                selectedModel = composite.getSelectedPart(1).getModel();
                selectedModel2 = null;
            }
        } else {
            selectedModel = null;
            selectedModel2 = null;
        }

    }

    /**
     * Select model on which user clicked.
     *
     * @param mouseReleasedX x position of mouse
     * @param mouseReleasedY z position of mouse
     * @return true if any model was selected.
     */
    public Boolean pickObject(double mouseReleasedX, double mouseReleasedY) {
        ModelSelector picker = new ModelSelector(glu);
        Model model = picker.pickModel(mouseReleasedX, mouseReleasedY, models, viewport, modelViewMatrix, projectionMatrix, new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition));

        if (model != null) {
            composite.selectFacePart(model);
            updateSelectedModel();
            return true;
        } else {
            selectedModel = null;
            selectedModel2 = null;
            return false;
        }
    }

    /**
     *
     * @param manipulator gizmo data
     */
    public void setManipulator(Manipulator manipulator) {
        this.manipulator = manipulator;
    }

    /**
     * Select gizmo on which user clicked
     *
     * @param x x mouse position
     * @param y y mouse position
     * @return true if gizmo was selected
     */
    public Boolean pickManipulator(int x, int y) {
        return manipulator.pickObject(x, y, viewport, modelViewMatrix, projectionMatrix, glu);
    }

    /**
     * Transform object acording to mouse motion.
     *
     * @param start strating position of mouse.
     * @param destination finishing point of mouse.
     * @param startRotation rotation of object (right in case of pair)
     * @param startRotation2 rotation of left object in case of pair
     * @param keepProportions keep proportion vhen resizing
     */
    public void doManipulation(Point start, Point destination, Quat4f startRotation, Quat4f startRotation2, Boolean keepProportions) {
        manipulator.doManipulation(start, destination, startRotation, startRotation2, keepProportions, viewport, modelViewMatrix, projectionMatrix, glu);
    }

    /**
     *
     * @return gizmo data
     */
    public Manipulator getManipulator() {
        return manipulator;
    }

    /**
     *
     * @param position new camera position
     */
    @Override
    public void setNewCameraPosition(Vector3f position) {
        xCameraPosition = position.getX();
        yCameraPosition = position.getY();
        zCameraPosition = position.getZ();
        manipulator.setCameraPosition(position);
        //  manipulator.setUpPosition(new Vector3f((float) xUpPosition, (float) yUpPosition, (float) zUpPosition));
    }

    /**
     * Restart rotation and size
     */
    @Override
    public void rotationAndSizeRestart() {

        xUpPosition = 0;
        yUpPosition = 1;
        zUpPosition = 0;

        setNewCameraPosition(defaultPosition);

        manipulator.resetModelSize();
    }

    /**
     *
     * @param distance distantce by which camera should zooom in
     */
    @Override
    public void zoomIn(double distance) {

        double sqrt = Math.sqrt(xCameraPosition * xCameraPosition + yCameraPosition * yCameraPosition + zCameraPosition * zCameraPosition);
        if (sqrt > 0) {
            Vector3f camera = new Vector3f(
                    (float) ((sqrt - distance) * xCameraPosition / sqrt),
                    (float) ((sqrt - distance) * yCameraPosition / sqrt),
                    (float) ((sqrt - distance) * zCameraPosition / sqrt));

            setNewCameraPosition(camera);
        }

        manipulator.adjustModels((float) ((sqrt - distance) / sqrt));

    }

    /**
     *
     * @param distance distantce by which camera should zooom out
     */
    @Override
    public void zoomOut(double distance) {

        double sqrt = Math.sqrt(xCameraPosition * xCameraPosition + yCameraPosition * yCameraPosition + zCameraPosition * zCameraPosition);
        if (sqrt == 0) {
            sqrt = 1;
        }
        Vector3f camera = new Vector3f(
                (float) ((sqrt + distance) * xCameraPosition / sqrt),
                (float) ((sqrt + distance) * yCameraPosition / sqrt),
                (float) ((sqrt + distance) * zCameraPosition / sqrt));

        setNewCameraPosition(camera);

        manipulator.adjustModels((float) ((sqrt + distance) / sqrt));

    }
    
    public void setDefaultCameraPosition(){
        setCameraPosition(0, 0, 300);
    }
}
