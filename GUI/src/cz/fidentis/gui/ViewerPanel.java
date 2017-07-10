/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui;

import cz.fidentis.renderer.ComparisonGLEventListener;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Vector3f;

/**
 *
 * @author xfurman
 */
public interface ViewerPanel {
    
    public void setPlaneNormal(Vector3f n, boolean recount);
    public void setPlanePoint(Vector3f n, boolean recount);
    public ComparisonGLEventListener getListener1();
    public ComparisonGLEventListener getListener2();
    
}
