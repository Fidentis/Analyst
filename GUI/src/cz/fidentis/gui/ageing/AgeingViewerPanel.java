/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.ageing;

import cz.fidentis.gui.Canvas;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.renderer.ComparisonGLEventListener;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

/**
 *
 * @author Marek Zuzi
 */
public class AgeingViewerPanel extends javax.swing.JPanel {
    private ProjectTopComponent topComponent;
    private ComparisonGLEventListener listenerOrigin;
    private ComparisonGLEventListener listenerTarget;
    private boolean manipulatePoint = false;
    private boolean editablePoints = false;
    private int indexOfSelectedPoint;
    private int nextIndexOfSelectedPoint;
    private float mouseDraggedX;
    private float mouseDraggedY;
    private boolean showInfo = false;
    
    /**
     * Creates new form AgeingViewerPanel
     */
    public AgeingViewerPanel() {
        initComponents();
    }
    
    public AgeingViewerPanel(ProjectTopComponent tc) {
        this.topComponent = tc;
        initComponents();
        listenerOrigin = new ComparisonGLEventListener();
        listenerTarget = new ComparisonGLEventListener();
        originCanvas.addGLEventListener(listenerOrigin);
        targetCanvas.addGLEventListener(listenerTarget);
        listenerOrigin.setDefaultCameraPosition();
        listenerTarget.setDefaultCameraPosition();

        originCanvas.setImportLabelVisible(true);
        targetCanvas.setImportLabelVisible(false);
        targetCanvas.setVisible(false);
    }

    public ProjectTopComponent getTopComponent() {
        return topComponent;
    }

    public ComparisonGLEventListener getListenerOrigin() {
        return listenerOrigin;
    }

    public ComparisonGLEventListener getListenerTarget() {
        return listenerTarget;
    }

    public Canvas getOriginCanvas() {
        return originCanvas;
    }

    public Canvas getTargetCanvas() {
        return targetCanvas;
    }
    
    public void setTextureRendering(boolean render) {
        listenerOrigin.reloadTextures();
        listenerOrigin.setDrawTextures(render);
        listenerTarget.reloadTextures();
        listenerTarget.setDrawTextures(render);
    }
    
    public void resizeSplitPane() {
        this.jSplitPane1.setDividerLocation(0.5);
    }
    
    public void showInfo(boolean show) {
        showInfo = show;
        originCanvas.setFeaturePointsPanelVisibility(showInfo && (listenerOrigin.getIndexOfSelectedPoint() != -1));
        targetCanvas.setFeaturePointsPanelVisibility(showInfo && (listenerTarget.getIndexOfSelectedPoint() != -1));
    }
    
    public void setEditablePoints(boolean edit) {
        this.editablePoints = edit;
    }
    
    private void mouseDrag(ComparisonGLEventListener listener, Canvas canvas, java.awt.event.MouseEvent evt) {
        if (manipulatePoint && editablePoints) {
            if (listener.editSelectedPoint(listener.checkPointInMesh(evt.getX(), evt.getY()))) {
                canvas.setCoordInfo(listener.getFacialPoint(indexOfSelectedPoint));
            }
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            float x = evt.getX();
            float y = evt.getY();
            Dimension size = evt.getComponent().getSize();
            float thetaY = 360.0f * ((x - mouseDraggedX) / (float) size.width);
            float thetaX = 360.0f * ((mouseDraggedY - y) / (float) size.height);

            listener.rotate(-thetaX, -thetaY);

            mouseDraggedX = x;
            mouseDraggedY = y;
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            float x = evt.getX();
            float y = evt.getY();
            Dimension size = evt.getComponent().getSize();
            float thetaX = 360.0f * ((x - mouseDraggedX) / (float) size.width);
            float thetaY = 360.0f * ((mouseDraggedY - y) / (float) size.height);

            listener.move(thetaX, -thetaY);

            mouseDraggedX = x;
            mouseDraggedY = y;
        }
    }
    
    private void mousePress(ComparisonGLEventListener listener, Canvas canvas, java.awt.event.MouseEvent evt, boolean allowManipulate) {
        mouseDraggedX = evt.getX();
        mouseDraggedY = evt.getY();
        
        
        manipulatePoint = listener.selectPoint(evt.getX(), evt.getY());
        if (allowManipulate && manipulatePoint) {
            nextIndexOfSelectedPoint = listener.getIndexOfSelectedPoint();
            if (indexOfSelectedPoint != nextIndexOfSelectedPoint) {
                indexOfSelectedPoint = nextIndexOfSelectedPoint;
            }
            canvas.setInfo(listener.getFacialPoint(indexOfSelectedPoint));
            if (showInfo) {
                canvas.setFeaturePointsPanelVisibility(true);

            }
        }
    }
    
    private void mouseWheel(ComparisonGLEventListener listener, int amount) {
        if (amount > 0) {
            listener.zoomIn(-5 * amount);
        } else {
            listener.zoomOut(5 * amount);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        originCanvas = new Canvas(topComponent, true);
        targetCanvas = new Canvas(topComponent, false);

        jSplitPane1.setResizeWeight(0.5);

        originCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                originCanvasMouseDragged(evt);
            }
        });
        originCanvas.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                originCanvasMouseWheelMoved(evt);
            }
        });
        originCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                originCanvasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                originCanvasMouseReleased(evt);
            }
        });
        jSplitPane1.setLeftComponent(originCanvas);

        targetCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                targetCanvasMouseDragged(evt);
            }
        });
        targetCanvas.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                targetCanvasMouseWheelMoved(evt);
            }
        });
        targetCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                targetCanvasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                targetCanvasMouseReleased(evt);
            }
        });
        jSplitPane1.setRightComponent(targetCanvas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 528, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jSplitPane1)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void originCanvasMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_originCanvasMouseDragged
        mouseDrag(listenerOrigin, originCanvas, evt);
    }//GEN-LAST:event_originCanvasMouseDragged

    private void originCanvasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_originCanvasMousePressed
        mousePress(listenerOrigin, originCanvas, evt, true);
    }//GEN-LAST:event_originCanvasMousePressed

    private void originCanvasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_originCanvasMouseReleased
        manipulatePoint = false;
    }//GEN-LAST:event_originCanvasMouseReleased

    private void originCanvasMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_originCanvasMouseWheelMoved
        mouseWheel(listenerOrigin, evt.getWheelRotation());
    }//GEN-LAST:event_originCanvasMouseWheelMoved

    private void targetCanvasMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_targetCanvasMouseDragged
        mouseDrag(listenerTarget, targetCanvas, evt);
    }//GEN-LAST:event_targetCanvasMouseDragged

    private void targetCanvasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_targetCanvasMousePressed
        mousePress(listenerTarget, targetCanvas, evt, false);
    }//GEN-LAST:event_targetCanvasMousePressed

    private void targetCanvasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_targetCanvasMouseReleased
        manipulatePoint = false;
    }//GEN-LAST:event_targetCanvasMouseReleased

    private void targetCanvasMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_targetCanvasMouseWheelMoved
        mouseWheel(listenerTarget, evt.getWheelRotation());
    }//GEN-LAST:event_targetCanvasMouseWheelMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private cz.fidentis.gui.Canvas originCanvas;
    private cz.fidentis.gui.Canvas targetCanvas;
    // End of variables declaration//GEN-END:variables
}
