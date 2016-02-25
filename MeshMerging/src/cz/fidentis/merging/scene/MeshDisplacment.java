package cz.fidentis.merging.scene;

import cz.fidentis.merging.mesh.Matrix3;
import cz.fidentis.merging.mesh.Vector3;
import javax.media.opengl.GL2;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class MeshDisplacment implements DisplacmentSettings {

    private final RotationVector xAxis = new RotationVector(1.f, 0.f, 0.f);
    private final RotationVector yAxis = new RotationVector(0.f, 1.f, 0.f);
    private final RotationVector zAxis = new RotationVector(0.f, 0.f, 1.f);

    private final int[] position = {0, 0, 0};

    /**
     *
     * @param openGl
     */
    public void displace(final GL2 openGl) {
        openGl.glPushMatrix();
        openGl.glTranslatef(position[0], position[1], position[2]);
        xAxis.rotateAround(openGl);
        yAxis.rotateAround(openGl);
        zAxis.rotateAround(openGl);

    }

    /**
     *
     * @param openGl
     */
    public void putBack(final GL2 openGl) {
        openGl.glPopMatrix();
    }

    /**
     *
     * @return
     */
    @Override
    public int getXRotation() {
        return xAxis.getRotation();
    }

    /**
     *
     * @return
     */
    @Override
    public int getYRotation() {
        return yAxis.getRotation();
    }

    /**
     *
     * @return
     */
    @Override
    public int getZRotation() {
        return zAxis.getRotation();
    }

    /**
     *
     * @param value
     */
    @Override
    public void setXRotation(final int value) {
        xAxis.setRotation(value);
    }

    /**
     *
     * @param value
     */
    @Override
    public void setYRotation(final int value) {
        yAxis.setRotation(value);
    }

    /**
     *
     * @param value
     */
    @Override
    public void setZRotation(final int value) {
        zAxis.setRotation(value);
    }

    /**
     *
     * @return
     */
    @Override
    public int getXPosition() {
        return position[0];
    }

    /**
     *
     * @return
     */
    @Override
    public int getYPosition() {
        return position[1];
    }

    /**
     *
     * @return
     */
    @Override
    public int getZPosition() {
        return position[2];
    }

    /**
     *
     * @param value
     */
    @Override
    public void setXPosition(final int value) {
        position[0] = value;
    }

    /**
     *
     * @param value
     */
    @Override
    public void setYPosition(final int value) {
        position[1] = value;
    }

    /**
     *
     * @param value
     */
    @Override
    public void setZPosition(final int value) {
        position[2] = value;
    }

    /**
     *
     * @return
     */
    public Vector3 getTranslation() {
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     *
     * @return
     */
    public Matrix3 getRotation() {

        Matrix3 base = xAxis.getXrotation();
        base.multiply(yAxis.getYrotation());
        base.multiply(zAxis.getZrotation());
        return base;
    }

}
