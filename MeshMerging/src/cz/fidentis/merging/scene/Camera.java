package cz.fidentis.merging.scene;

import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class Camera {

    /**
     *
     */
    public static final float DEFAULT_DISTANCE = -500.0f;
    private final RotationVector upVector;
    private final RotationVector position;
    private final RotationVector product;
    private float zoom = 1.0f;
    private int height;
    private int width;
    private boolean resetProjection;

    /**
     *
     */
    public Camera() {
        upVector = new RotationVector(0.0f, 1.0f, 0.0f);
        upVector.setRotation(0);
        position = new RotationVector(0.0f, 0.0f, -DEFAULT_DISTANCE);
        product = new RotationVector(1.0f, 0.0f, 0.0f);
    }

    /**
     *
     * @param openGl
     */
    public void useCamera(final GL2 openGl) {
        if (resetProjection) {
            resetProjection = false;
            setProjection(openGl);
        }

        openGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{1000.f,1000.f,1000.f}, 0);
        openGl.glEnable(GL2.GL_LIGHT0);

        openGl.glMatrixMode(GL2.GL_MODELVIEW);
        openGl.glLoadIdentity();
        position.translateBy(openGl);
        product.rotateAround(openGl);
        upVector.rotateAround(openGl);
        
    }

    /**
     *
     * @param byangle
     */
    public void turnLeft(final int byangle) {
        upVector.addRotation(-byangle);
    }

    /**
     *
     * @param angle
     */
    public void setRotation(final int angle) {
        upVector.setRotation(angle);
    }

    /**
     *
     * @param angle
     */
    public void setHight(final int angle) {
        product.setRotation(angle);
    }

    /**
     *
     * @param byangle
     */
    public void turnRigth(final int byangle) {
        upVector.addRotation(byangle);
    }

    /**
     *
     * @param byDistance
     */
    public void moveOnX(final float byDistance) {
        position.setX(byDistance);
    }

    /**
     *
     * @param byDistance
     */
    public void moveOnZ(final float byDistance) {
        position.setZ(byDistance);
    }

    /**
     *
     * @param as
     */
    public void setZoom(final float as) {
        this.zoom = as;
        resetProjection = true;
    }
    
    
    public void setHorizontal(float value) {
        position.setX(value);
    }

    public void setVertical(float value) {
        position.setY(value);
    }

    /**
     *
     * @return
     */
    public int getRotation() {
        return upVector.getRotation();
    }

    /**
     *
     * @return
     */
    public float getZoom() {
        return zoom;
    }

    /**
     *
     * @param width
     * @param height
     * @param openGl
     */
    public void setProjection(int width, int height, GL2 openGl) {
        if (height <= 0) {
            height = 1;
        }
        this.height = height;
        this.width = width;
        setProjection(openGl);
    }

    private void setProjection(GL2 openGl) {
        float halfOfHorizont = width / zoom;
        float halfOfVertical = height / zoom;
        openGl.glViewport(0, 0, width, height);
        openGl.glMatrixMode(GL2.GL_PROJECTION);
        openGl.glLoadIdentity();
        openGl.glOrtho(-halfOfHorizont, halfOfHorizont, -halfOfVertical, halfOfVertical, -1000, +1000);
        openGl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return product.getRotation();
    }



}
