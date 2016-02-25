package cz.fidentis.merging.scene;

import cz.fidentis.merging.mesh.Matrix3;
import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class RotationVector {

    public static final int WEIGHT = 3;
    private static final int COORD4D = 4;
    private static final float FULL_ANGEL = 360.f;
    protected static final int HALF_ANGLE = 180;

    /**
     *
     */
    public static final int SIZE = 3;
    private final float[] point;
    private int rotation = 0;

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public RotationVector(final float x, final float y, final float z) {
        point = new float[SIZE];
        point[0] = x;
        point[1] = y;
        point[2] = z;
    }

    /**
     *
     * @param byAngel
     */
    public void setRotation(final int byAngel) {
        rotation = byAngel;
    }

    /**
     *
     * @param angel
     */
    public void addRotation(final int angel) {
        rotation += angel;
        if (rotation >= FULL_ANGEL) {
            rotation -= FULL_ANGEL;
        } else if (rotation <= -FULL_ANGEL) {
            rotation += FULL_ANGEL;
        }
    }

    /**
     *
     * @param openGl
     */
    public void rotateAround(final GL2 openGl) {
        openGl.glRotatef((float) rotation, point[0], point[1], point[2]);
    }

    /**
     *
     * @param openGl
     */
    public void translateBy(final GL2 openGl) {
        openGl.glTranslatef(point[0], point[1], point[2]);
    }

    /**
     *
     * @return
     */
    public float getX() {
        return point[0];
    }

    /**
     *
     * @return
     */
    public float getY() {
        return point[1];
    }

    /**
     *
     * @return
     */
    public float getZ() {
        return point[2];
    }

    /**
     *
     * @param delta
     */
    public void setX(final float delta) {
        point[0] = delta;
    }
    
        
    
    public void setY(final float value) {
        point[1] = value;
    }

    /**
     *
     * @param delta
     */
    public void setZ(final float delta) {
        point[2] = delta;
    }


    /**
     *
     * @return
     */
    public float[] toArray() {
        float[] result = new float[COORD4D];
        System.arraycopy(point, 0, result, 0, point.length);
        result[WEIGHT] = 1.0f;
        return result;
    }

    /**
     *
     * @return
     */
    public int getRotation() {
        return rotation;
    }

    /**
     *
     * @return
     */
    public Matrix3 getXrotation() {
        double radians = rotationInRadians();
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Matrix3(
                new double[]{1.d, 0.d, 0.d},
                new double[]{0.d, cos, sin},
                new double[]{0.d, -1.d * sin, cos});
    }

    /**
     *
     * @return
     */
    public Matrix3 getYrotation() {
        double radians = rotationInRadians();
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        return new Matrix3(
                new double[]{cos, 0.d, -1.d * sin},
                new double[]{0.d, 1.d, 0.d},
                new double[]{sin, 0.d, cos});
    }

    /**
     *
     * @return
     */
    public Matrix3 getZrotation() {
        double radians = rotationInRadians();
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Matrix3(
                new double[]{cos, sin, 0.d},
                new double[]{-1.d * sin, cos, 0.d},
                new double[]{0.d, 0.d, 1.d}
        );
    }

    /**
     *
     * @return
     */
    protected double rotationInRadians() {
        return rotation * Math.PI / HALF_ANGLE;
    }


}
