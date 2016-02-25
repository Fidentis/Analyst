package cz.fidentis.merging.scene;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public interface CameraSettings {

    /**
     *
     * @return
     */
    int getRotation();

    /**
     *
     * @return
     */
    int getPitch();

    /**
     *
     * @return
     */
    int getZoom();
    
    int getVertical();
    
    int getHorizontal();

    /**
     *
     * @param value
     */
    void setRotation(int value);

    /**
     *
     * @param value
     */
    void setPitch(int value);

    /**
     *
     * @param value
     */
    void setZoom(int value);

    void setVertical(int value);
    
    void setHorizontal(int value);
    /**
     *
     * @param camera
     * @return
     */
    boolean registerObserver(Camera camera);

    /**
     *
     * @param camera
     * @return
     */
    boolean unRegisterObserver(Camera camera);

}
