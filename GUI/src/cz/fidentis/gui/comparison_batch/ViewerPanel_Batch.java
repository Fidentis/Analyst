/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.comparison_batch;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.gui.Canvas;
import cz.fidentis.gui.ConfigurationTopComponent;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.landmarks.EditLandmarkID;
import cz.fidentis.gui.observer.ObservableMaster;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.renderer.ComparisonGLEventListener;
import cz.fidentis.utils.IntersectionUtils;
import cz.fidentis.utils.MathUtils;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import static java.io.File.separatorChar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector3f;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Katka
 */
public class ViewerPanel_Batch extends javax.swing.JPanel {

    private ProjectTopComponent projectComponent;

    private float mouseDraggedX, mouseDraggedY;
    private ComparisonGLEventListener listener;
    private ComparisonGLEventListener listener2;
    private boolean manipulatePoint;
    private int nextIndexOfSelectedPoint;
    private int indexOfSelectedPoint;
    private boolean showInfo = true;
    private boolean editablePoints = false;
    private boolean selection;
    private boolean dragging;
    private Point draggingStart;
    private Point startGizmoCenter2D;
    private Vector3f startGizmoCenter3D;
    private Vector3f startPlanePoint;

    private Canvas canvas3;
    private Canvas canvas4;
    
    private boolean removePoints = false;
    private boolean addPoints = false;
    private ObservableMaster fpExportEnable;        //to check whether FPs can be exported once they are added, removed
    private LocalAreasJPanel pointer;
    
    /**
     * Creates new form ViewerPanel4
     */
    public ViewerPanel_Batch(ProjectTopComponent tc) {
        projectComponent = tc;
        initComponents();
        //    jSplitPane1 = new javax.swing.JSplitPane();
        //    canvas2 = new cz.fidentis.gui.Canvas(projectComponent);
        listener = new ComparisonGLEventListener();
        listener2 = new ComparisonGLEventListener();
        canvas1.addGLEventListener(listener);
        listener.setCameraPosition(0, 0, 300);
        String path = GUIController.getPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar;

        Model model = ModelLoader.instance().loadModel(new File(path + "xShift.obj"), false, false);
        listener.setGizmo(model);

        canvas1.setImportLabelVisible(true);
    }

    public Canvas getCanvas1() {
        return canvas1;
    }

    public Canvas getCanvas3() {
        return canvas3;
    }

    public Canvas getCanvas4() {
        return canvas4;
    }
    

    public ComparisonGLEventListener getListener() {
        return listener;
    }
    
    public void checkFpAvaialable(){
        fpExportEnable.updateObservers();
    }

    public void setModel(Model model) {
        listener.setModels(model);
    }
    
    public void setLocalAreasJPanel(LocalAreasJPanel localAreasJPanel){
        this.pointer = localAreasJPanel;
    }

    public void setResultButtonVisible(boolean b) {
        canvas1.setResultButtonVisible(b);
    }

    public void setTextureRendering(Boolean b) {
        listener.setDrawTextures(b);
    }

    public void showInfo(boolean show) {
        showInfo = show;
        canvas1.setFeaturePointsPanelVisibility(showInfo && (listener.getIndexOfSelectedPoint() != -1));

    }

    public void setFpExportEnable(ObservableMaster fpExportEnable) {
        this.fpExportEnable = fpExportEnable;
    }

    public ObservableMaster getFpExportEnable() {
        return fpExportEnable;
    }
    

    public void setEditablePoints(boolean b) {
        editablePoints = b;
        removePoints = false;
        addPoints = false;
    }

    public void setRemovePoints(boolean removePoints) {
        this.removePoints = removePoints;
        editablePoints = false;
        addPoints = false;
    }
    
    public void setAddPoints(boolean b){
        this.addPoints = b;
        editablePoints = false;
        removePoints = false;
    }

    public void resizeCanvas() {
        canvas1.resizeCanvas(this.getSize());
    }

    void setPlaneNormal(Vector3f vector3f, boolean recountEverything) {
        listener.setPlaneNormal(vector3f);
        listener2.setPlaneNormal(vector3f);

        if (listener.getModels() != null) {
            if (recountEverything) {
                ArrayList<LinkedList<LinkedList<Vector3f>>> lists = new ArrayList<>();

                for (Model m : listener2.getModels()) {
                    lists.add(IntersectionUtils.findModelPlaneIntersection(m, listener.getPlaneNormal(), listener.getPlanePoint()));
                }
                listener2.setLists(lists, true);
                listener.setLists(lists);
            } else {
                ArrayList<LinkedList<LinkedList<Vector3f>>> lists = new ArrayList<>();
                lists.add(IntersectionUtils.findModelPlaneIntersection(listener.getModels().get(0), listener.getPlaneNormal(), listener.getPlanePoint()));
                listener.setLists(lists);
                listener2.setLists(lists, true);
            }
        }
    }

    public void sliceViewerVisible(boolean show) {
        if (show) {
            if (canvas3 == null || canvas4 == null) {
                setCutCanvas();
            }

            jPanel1.removeAll();

            canvas4.setMinimumSize(new java.awt.Dimension(300, 0));
            canvas4.setPreferredSize(new java.awt.Dimension(300, 0));
            // jSplitPane2.setLeftComponent(canvas3);
            canvas4.addGLEventListener(listener);
            canvas3.setMinimumSize(new java.awt.Dimension(300, 0));
            canvas3.setPreferredSize(new java.awt.Dimension(300, 0));
            canvas3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseDragged(java.awt.event.MouseEvent evt) {
                    canvas2MouseDragged(evt);
                }
            });
            canvas3.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                @Override
                public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                    canvas2MouseWheelMoved(evt);
                }

            });
            canvas3.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    Vector3f p = listener2.testArrowClicked(evt.getX(), evt.getY());
                    if (p != null) {
                        Vector3f[] selectionCube = new Vector3f[5];
                        selectionCube[0] = new Vector3f(p.x - 10, p.y - 10, p.z);
                        selectionCube[1] = new Vector3f(p.x - 10, p.y + 10, p.z);
                        selectionCube[2] = new Vector3f(p.x + 10, p.y + 10, p.z);
                        selectionCube[3] = new Vector3f(p.x + 10, p.y - 10, p.z);
                        Vector3f n = new Vector3f(listener.getPlaneNormal());
                        n.scale(2);
                        p.sub(n);
                        selectionCube[4] = new Vector3f(0, 0, 300);

                        listener.setSelectionBox(selectionCube);
                    } else {
                        listener.clearSelection();
                    }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    canvas2MousePressed(evt);
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    canvas2MouseReleased(evt);
                }
            });
            canvas3.addGLEventListener(listener2);
            listener2.setCameraPosition(0, 0, 300);

            //   jSplitPane2.setRightComponent(canvas4);
            jPanel1.add(jSplitPane2);
            this.revalidate();
            this.repaint();
        } else {
            jPanel1.removeAll();
            jPanel1.add(canvas1);
        }
        this.revalidate();
        this.repaint();

    }

    public void setCutCanvas() {
        canvas3 = new Canvas();
        canvas4 = new Canvas();

        jSplitPane2.setRightComponent(canvas3);

        canvas4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvas4MouseDragged(evt);
            }
        });
        canvas4.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                canvas4MouseWheelMoved(evt);
            }
        });
        canvas4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvas4MousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                canvas4MouseReleased(evt);
            }
        });

        jSplitPane2.setLeftComponent(canvas4);
    }

    public ComparisonGLEventListener getListener2() {
        return listener2;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        canvas1 = new cz.fidentis.gui.Canvas(projectComponent);

        jSplitPane2.setResizeWeight(0.5);
        jSplitPane2.setToolTipText(org.openide.util.NbBundle.getMessage(ViewerPanel_Batch.class, "ViewerPanel_Batch.jSplitPane2.toolTipText")); // NOI18N

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        canvas1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvas1MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                canvas1MouseMoved(evt);
            }
        });
        canvas1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                canvas1MouseWheelMoved(evt);
            }
        });
        canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                canvas1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                canvas1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                canvas1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvas1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                canvas1MouseReleased(evt);
            }
        });
        jPanel1.add(canvas1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void canvas1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_canvas1MouseClicked

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public void clearSelection() {
        listener.clearSelection();        
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().updateHistograms();
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().adjustThresholds();
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().getHistogram().resetSlider();

    }

    private void canvas2MouseDragged(MouseEvent evt) {
        if (SwingUtilities.isLeftMouseButton(evt)) {

            float x = evt.getX();
            float y = evt.getY();
            Dimension size = evt.getComponent().getSize();
            float thetaY = 360.0f * ((x - mouseDraggedX) / (float) size.width);
            float thetaX = 360.0f * ((mouseDraggedY - y) / (float) size.height);

            listener2.rotate(-thetaX, -thetaY);

            mouseDraggedX = x;
            mouseDraggedY = y;

        } else if (SwingUtilities.isRightMouseButton(evt)) {
            float x = evt.getX();
            float y = evt.getY();
            Dimension size = evt.getComponent().getSize();
            float thetaX = 360.0f * ((x - mouseDraggedX) / (float) size.width);
            float thetaY = 360.0f * ((mouseDraggedY - y) / (float) size.height);

            listener2.move(thetaX, -thetaY);

            mouseDraggedX = x;
            mouseDraggedY = y;
        }
    }

    private void canvas2MouseWheelMoved(MouseWheelEvent evt) {
        if (evt.getWheelRotation() > 0) {
            listener2.zoomIn(-5 * evt.getWheelRotation());
        } else {
            listener2.zoomOut(5 * evt.getWheelRotation());

        }
    }

    private void canvas2MousePressed(MouseEvent evt) {
        mouseDraggedX = evt.getX();
        mouseDraggedY = evt.getY();
        if (listener.getModel() != null && listener.pickManipulator(evt.getX(), evt.getY())) {
            dragging = true;
            draggingStart = evt.getPoint();
            startGizmoCenter2D = listener.getPlaneCenter2D();
            startGizmoCenter3D = listener.getPlaneCenter();
            startPlanePoint = listener.getPlanePoint();
        }
    }

    private void canvas2MouseReleased(MouseEvent evt) {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void canvas1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MousePressed
        if (pointer != null){
            pointer.setMousePositionToSelectArea((double)evt.getX(), (double)evt.getY());
        }
        canvasClicked(evt, listener, canvas1);

    }//GEN-LAST:event_canvas1MousePressed

    private void canvasClicked(MouseEvent evt, ComparisonGLEventListener listener, Canvas canvas) {
        mouseDraggedX = evt.getX();
        mouseDraggedY = evt.getY();
        
        manipulatePoint = listener.selectPoint(evt.getX(), evt.getY());
        
        if (manipulatePoint) {
            //update selected point
            setPointInfo(canvas, listener);
          
            if (editablePoints) {
                canvas.setInfo(listener.getFacialPoint(indexOfSelectedPoint));
                if (showInfo) {
                    canvas.setFeaturePointsPanelVisibility(true);

                }
                
                if(evt.getButton() == MouseEvent.BUTTON3){   //edit window
                    EditLandmarkID d = new EditLandmarkID(listener.getFacialPoint(indexOfSelectedPoint), listener.getInfo(), canvas);
                    d.setVisible(true);
                }
            }else if(removePoints){
                listener.getFacialPoints().remove(nextIndexOfSelectedPoint);
                listener.setIndexOfSelectedPoint(-1);
                
                if (showInfo) {        //no point is selected
                    canvas.setFeaturePointsPanelVisibility(false);

                }
            }
            
        } else if (listener.getModel() != null) {        //pick point on the mesh
            Vector3f pos = listener.checkPointInMesh(evt.getX(), evt.getY());

            if (pos == null) {  //not on mesh, deselect
                listener.setIndexOfSelectedPoint(indexOfSelectedPoint = -1);
                if (showInfo) {
                    canvas.setFeaturePointsPanelVisibility(false);
                }
            } else if (addPoints) { //on mesh plus adding points is turned on
                int id = listener.getInfo().getNextFreeFPID();
                FacialPoint fp = new FacialPoint(id, pos);
                listener.getInfo().addFacialPoint(fp);
                listener.setIndexOfSelectedPoint(listener.getInfo().getFacialPoints().size() - 1);

                if (showInfo) {
                    canvas.setFeaturePointsPanelVisibility(true);
                }

                setPointInfo(canvas, listener);
            }

        } 
        if (selection && SwingUtilities.isLeftMouseButton(evt)) {
            clearSelection();
            listener.setSelectionStart(evt.getPoint());
        }

        //update points in data model
        if(removePoints || addPoints){
            String modelName = listener.getModel().getName();    
            GUIController.getSelectedProjectTopComponent().getProject().getSelectedBatchComparison().addFacialPoints(modelName, listener.getFacialPoints());
            fpExportEnable.updateObservers();
        }
        
    }
    
    private void setPointInfo(Canvas canvas, ComparisonGLEventListener listener) {
        nextIndexOfSelectedPoint = listener.getIndexOfSelectedPoint();
        
        //update selected point
        if (indexOfSelectedPoint != nextIndexOfSelectedPoint) {
            indexOfSelectedPoint = nextIndexOfSelectedPoint;
        }
        canvas.setInfo(listener.getFacialPoint(indexOfSelectedPoint));
    }
    
    private void canvas1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseReleased
        if (dragging && SwingUtilities.isLeftMouseButton(evt)) {
            setPlaneNormal(listener.getPlaneNormal(), true);
        } else if (dragging && SwingUtilities.isRightMouseButton(evt)) {
            setPlanePoint(listener.getPlanePoint(), true);
        }
        manipulatePoint = false;
        dragging = false;
        if (selection && SwingUtilities.isLeftMouseButton(evt)) {
            listener.setSelectionEnd(evt.getPoint(), canvas1.getWidth(), canvas1.getHeight());
            listener.setSelectionFinished(true);
            final ConfigurationTopComponent tc = GUIController.getConfigurationTopComponent();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    tc.getBatchComparisonResults().updateHistograms();
                    tc.getBatchComparisonResults().adjustThresholds();
                    tc.getBatchComparisonResults().getHistogram().resetSlider();                    
                }
            };
            Timer t = new Timer();
            t.schedule(tt, 500);
        }
    }//GEN-LAST:event_canvas1MouseReleased

    private void canvas1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseDragged
        if (manipulatePoint && editablePoints) {
            if (listener.editSelectedPoint(listener.checkPointInMesh(evt.getX(), evt.getY()))) {
                canvas1.setCoordInfo(listener.getFacialPoint(indexOfSelectedPoint));
            }
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            if (selection) {
                listener.setSelectionEnd(evt.getPoint(), canvas1.getWidth(), canvas1.getHeight());
            } else {
                float x = evt.getX();
                float y = evt.getY();
                Dimension size = evt.getComponent().getSize();
                float thetaY = 360.0f * ((x - mouseDraggedX) / (float) size.width);
                float thetaX = 360.0f * ((mouseDraggedY - y) / (float) size.height);
                mouseDraggedX = x;
                mouseDraggedY = y;

                if (dragging) {
                    Vector3f xAxe = listener.getXaxis();
                    Vector3f yAxe = listener.getYaxis();
                    Vector3f n = new Vector3f(listener.getPlaneNormal());
                    n = listener.rotateAroundAxe(n, yAxe, Math.toRadians(thetaY));
                    n = listener.rotateAroundAxe(n, xAxe, Math.toRadians(thetaX));
                    n.normalize();
                    setPlaneNormal(n, false);
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().setValuesModified(true);
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().setPlaneNormal(n);
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().enableArbitraryNormal();
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().setValuesModified(false);
                } else {
                    listener.rotate(-thetaX, -thetaY);
                }

            }
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            if (dragging) {
                shift(evt.getPoint());
            } else {
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


    }//GEN-LAST:event_canvas1MouseDragged

    private void shift(Point destination) {
        double x = startGizmoCenter2D.x - draggingStart.x;
        double y = startGizmoCenter2D.x - draggingStart.y;
        double initialDistance = Math.sqrt(x * x + y * y);

        double distanceRatio = 1;
        distanceRatio = MathUtils.instance().distancePoints(listener.getGizmoIntersection(), startGizmoCenter3D) / initialDistance;

        double k = (destination.x * x - draggingStart.x * x + destination.y * y - draggingStart.y * y) / (x * x + y * y);
        double distance = Math.sqrt(((-k * x) * (-k * x)) + ((-k * y) * (-k * y))) * distanceRatio;

        if (k > 0) {
            distance = -distance;
        }

        Vector3f n = new Vector3f(listener.getPlaneNormal());
        n.normalize();
        n.scale((float) distance);

        Vector3f p = new Vector3f(startPlanePoint);
        p.add(n);
        setPlanePoint(p, true);
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().setValuesModified(true);
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().setPlanePoint(p);
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().enableArbitraryNormal();
        GUIController.getConfigurationTopComponent().getBatchComparisonResults().setValuesModified(false);
    }

    void setPlanePoint(Vector3f vector3f, boolean recountEverything) {
        listener.setPlanePoint(vector3f);
        listener2.setPlanePoint(vector3f);

        if (listener.getModels() != null) {
            if (recountEverything) {
                ArrayList<LinkedList<LinkedList<Vector3f>>> lists = new ArrayList<>();

                for (Model m : listener2.getModels()) {
                    lists.add(IntersectionUtils.findModelPlaneIntersection(m, listener.getPlaneNormal(), listener.getPlanePoint()));
                }
                listener2.setLists(lists, true);
                listener.setLists(lists);

            } else {
                ArrayList<LinkedList<LinkedList<Vector3f>>> lists = new ArrayList<>();
                lists.add(IntersectionUtils.findModelPlaneIntersection(listener.getModels().get(0), listener.getPlaneNormal(), listener.getPlanePoint()));
                listener.setLists(lists);
                listener2.setLists(lists, true);
            }
        }
    }

    private void canvas4MousePressed(java.awt.event.MouseEvent evt) {
        mouseDraggedX = evt.getX();
        mouseDraggedY = evt.getY();

        if (listener.getModel() != null && listener.pickManipulator(evt.getX(), evt.getY())) {
            dragging = true;
            draggingStart = evt.getPoint();
            startGizmoCenter2D = listener.getPlaneCenter2D();
            startGizmoCenter3D = listener.getPlaneCenter();
            startPlanePoint = listener.getPlanePoint();
        } else if (listener.getModel() != null && listener.checkPointInMesh(evt.getX(), evt.getY()) == null) {
            listener.setIndexOfSelectedPoint(indexOfSelectedPoint = -1);
            if (showInfo) {
                canvas4.setFeaturePointsPanelVisibility(false);
            }
        } else if (selection && SwingUtilities.isLeftMouseButton(evt)) {
            clearSelection();
            listener.setSelectionStart(evt.getPoint());
        }
    }

    private void canvas4MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        if (evt.getWheelRotation() > 0) {
            listener.zoomIn(-5 * evt.getWheelRotation());
        } else {
            listener.zoomOut(5 * evt.getWheelRotation());

        }
    }

    private void canvas4MouseDragged(java.awt.event.MouseEvent evt) {
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (selection) {
                listener.setSelectionEnd(evt.getPoint(), canvas4.getWidth(), canvas4.getHeight());
            } else {
                float x = evt.getX();
                float y = evt.getY();
                Dimension size = evt.getComponent().getSize();
                float thetaY = 360.0f * ((x - mouseDraggedX) / (float) size.width);
                float thetaX = 360.0f * ((mouseDraggedY - y) / (float) size.height);
                mouseDraggedX = x;
                mouseDraggedY = y;

                if (dragging) {
                    Vector3f xAxe = listener.getXaxis();
                    Vector3f yAxe = listener.getYaxis();
                    Vector3f n = new Vector3f(listener.getPlaneNormal());
                    n = listener.rotateAroundAxe(n, yAxe, Math.toRadians(thetaY));
                    n = listener.rotateAroundAxe(n, xAxe, Math.toRadians(thetaX));
                    n.normalize();
                    setPlaneNormal(n, true);
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().setPlaneNormal(n);
                    GUIController.getConfigurationTopComponent().getBatchComparisonResults().enableArbitraryNormal();
                } else {
                    listener.rotate(-thetaX, -thetaY);
                }

            }
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            if (dragging) {
                shift(evt.getPoint());
            } else {
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

    }

    private void canvas4MouseReleased(java.awt.event.MouseEvent evt) {
        manipulatePoint = false;
        dragging = false;
        if (selection && SwingUtilities.isLeftMouseButton(evt)) {
            listener.setSelectionEnd(evt.getPoint(), canvas4.getWidth(), canvas4.getHeight());
            listener.setSelectionFinished(true);
            final ConfigurationTopComponent tc = GUIController.getConfigurationTopComponent();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    tc.getBatchComparisonResults().updateHistograms();
                    tc.getBatchComparisonResults().getHistogram().resetSlider();
                }
            };
            Timer t = new Timer();
            t.schedule(tt, 500);
        }
    }
    private void canvas1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_canvas1MouseWheelMoved
        if (evt.getWheelRotation() > 0) {
            listener.zoomIn(-5 * evt.getWheelRotation());
        } else {
            listener.zoomOut(5 * evt.getWheelRotation());

        }
    }//GEN-LAST:event_canvas1MouseWheelMoved

    private void canvas1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseEntered
        if (pointer != null) {
            pointer.startMousePositionDetectionOnCanvas(true);
        }
    }//GEN-LAST:event_canvas1MouseEntered

    private void canvas1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseExited
        if (pointer != null) {
            pointer.startMousePositionDetectionOnCanvas(false);
        }
    }//GEN-LAST:event_canvas1MouseExited

    private void canvas1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseMoved
        if (pointer != null){
            pointer.setMousePosition(evt.getX(), evt.getY(), Calendar.getInstance());
        }
    }//GEN-LAST:event_canvas1MouseMoved

    // private JSplitPane jSplitPane1;
    // private Canvas canvas2;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private cz.fidentis.gui.Canvas canvas1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane2;
    // End of variables declaration//GEN-END:variables
}
