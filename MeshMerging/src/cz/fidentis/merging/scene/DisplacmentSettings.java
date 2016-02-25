package cz.fidentis.merging.scene;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public interface DisplacmentSettings {

    /**
     *
     * @return
     */
    int getXRotation();

    /**
     *
     * @return
     */
    int getYRotation();

    /**
     *
     * @return
     */
    int getZRotation();

    /**
     *
     * @param value
     */
    void setXRotation(int value);

    /**
     *
     * @param value
     */
    void setYRotation(int value);

    /**
     *
     * @param value
     */
    void setZRotation(int value);

    /**
     *
     * @return
     */
    int getXPosition();

    /**
     *
     * @return
     */
    int getYPosition();

    /**
     *
     * @return
     */
    int getZPosition();

    /**
     *
     * @param value
     */
    void setXPosition(int value);

    /**
     *
     * @param value
     */
    void setYPosition(int value);

    /**
     *
     * @param value
     */
    void setZPosition(int value);

}
