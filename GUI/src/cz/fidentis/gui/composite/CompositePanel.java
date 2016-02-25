/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.composite;

import cz.fidentis.merging.TextureMerger;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.composite.Manipulator;
import cz.fidentis.composite.ModelInfo;
import cz.fidentis.composite.SexType;
import cz.fidentis.controller.Composite;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.merging.doubly_conected_edge_list.parts.DcelMerger;
import cz.fidentis.merging.doubly_conected_edge_list.parts.ProgressObserver;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangularDCEL;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.GraphicMeshBuilderFromModel;
import cz.fidentis.model.Materials;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.randomFace.RandomFacePlacement;
import cz.fidentis.renderer.CompositeGLEventListener;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.vecmath.Vector3f;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author Katarína Furmanová
 */
@SuppressWarnings("rawtypes")
public class CompositePanel extends javax.swing.JPanel {
    private ProjectTopComponent projectComponent;

    private int ageBottomLimit = 0;
    private int ageTopLimit = 200;
    private SexType sex = SexType.BOTH;
    private Point mousePosition;
    private float mouseDraggedX, mouseDraggedY;
    private Boolean manipulatingObject = false;
    private Composite compositeData;
    private CompositeGLEventListener listener;
    // private Manipulator manipulator;
    private Map<Object, ImageIcon> forheadIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> forheadModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> eyesIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> eyesModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> earsIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> earsModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> eyebrowsIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> eyebrowsModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> noseIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> noseModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> mouthIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> mouthModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> chinIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> chinModels = new HashMap<Object, ModelInfo>();
    private Map<Object, ImageIcon> headIcons = new HashMap<Object, ImageIcon>();
    private Map<Object, ModelInfo> headModels = new HashMap<Object, ModelInfo>();
    private RandomFacePlacement facePlacement = null;
    
    private static final boolean ALLOW_RANDOMFACE_SCALE = true;
    private static final String numPattern = "[^0-9]";
    

    /**
     * Creates new form CompositePanel
     */
    public CompositePanel(ProjectTopComponent tc) {
        projectComponent = tc;
        loadModels();
        initComponents();
        listener = new CompositeGLEventListener();
        Manipulator manipulator = new Manipulator();
        listener.setManipulator(manipulator);
        listener.setCameraPosition(0, 0, 300);
        compositeCanvas2.addGLEventListener(listener);
        ModelLoader loader = new ModelLoader();

        Model model;

        String path = GUIController.getPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar;

        model = loader.loadModel(new File(path + "xShift.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "YShift.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "zShift.obj"), false, true);
        listener.getManipulator().addModel(model);

        model = loader.loadModel(new File(path + "xRotation.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "yRotation.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "zRotation.obj"), false, true);
        listener.getManipulator().addModel(model);

        model = loader.loadModel(new File(path + "xScale.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "yScale.obj"), false, true);
        listener.getManipulator().addModel(model);
        model = loader.loadModel(new File(path + "zScale.obj"), false, true);
        listener.getManipulator().addModel(model);
    }

    public void selectTemplates() {
        headToggleButton.doClick();

    }

    public void setCompositeData(Composite compositeData) {
        this.compositeData = compositeData;
        listener.setCompositeData(compositeData);
    }

    private JToggleButton createGridButton(ImageIcon icon) {
        JToggleButton button = new JToggleButton();
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setIcon(icon);

        //  button.setText(text);
        return button;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        headToggleButton = new javax.swing.JToggleButton();
        forheadToggleButton = new javax.swing.JToggleButton();
        eyebrowsToggleButton = new javax.swing.JToggleButton();
        eyesToggleButton = new javax.swing.JToggleButton();
        noseToggleButton = new javax.swing.JToggleButton();
        mouthToggleButton = new javax.swing.JToggleButton();
        chinToggleButton = new javax.swing.JToggleButton();
        earsToggleButton = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        sexComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        ageComboBox = new javax.swing.JComboBox();
        gridPanel = new cz.fidentis.gui.composite.GridPanel();
        randomPartButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        viewToggleButton = new javax.swing.JToggleButton();
        moveToggleButton = new javax.swing.JToggleButton();
        rotateToggleButton = new javax.swing.JToggleButton();
        scaleToggleButton = new javax.swing.JToggleButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(35, 0), new java.awt.Dimension(35, 0), new java.awt.Dimension(7, 32767));
        compositeCanvas2 = new cz.fidentis.gui.Canvas(projectComponent);

        jSplitPane1.setResizeWeight(0.5);

        jPanel1.setMinimumSize(new java.awt.Dimension(520, 0));

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMinimumSize(new java.awt.Dimension(520, 73));

        buttonGroup1.add(headToggleButton);
        headToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/head.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(headToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.headToggleButton.text")); // NOI18N
        headToggleButton.setFocusable(false);
        headToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        headToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        headToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        headToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(headToggleButton);

        buttonGroup1.add(forheadToggleButton);
        forheadToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/forehead.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(forheadToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.forheadToggleButton.text")); // NOI18N
        forheadToggleButton.setFocusable(false);
        forheadToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        forheadToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        forheadToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        forheadToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forheadToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(forheadToggleButton);

        buttonGroup1.add(eyebrowsToggleButton);
        eyebrowsToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/eyebrows.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(eyebrowsToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.eyebrowsToggleButton.text")); // NOI18N
        eyebrowsToggleButton.setFocusable(false);
        eyebrowsToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eyebrowsToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        eyebrowsToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eyebrowsToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eyebrowsToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(eyebrowsToggleButton);

        buttonGroup1.add(eyesToggleButton);
        eyesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/eyes.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(eyesToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.eyesToggleButton.text")); // NOI18N
        eyesToggleButton.setFocusable(false);
        eyesToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eyesToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        eyesToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eyesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eyesToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(eyesToggleButton);

        buttonGroup1.add(noseToggleButton);
        noseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/nose.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(noseToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.noseToggleButton.text")); // NOI18N
        noseToggleButton.setFocusable(false);
        noseToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        noseToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        noseToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        noseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noseToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(noseToggleButton);

        buttonGroup1.add(mouthToggleButton);
        mouthToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/mouth.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(mouthToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.mouthToggleButton.text")); // NOI18N
        mouthToggleButton.setFocusable(false);
        mouthToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mouthToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        mouthToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mouthToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mouthToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(mouthToggleButton);

        buttonGroup1.add(chinToggleButton);
        chinToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/chin.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(chinToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.chinToggleButton.text")); // NOI18N
        chinToggleButton.setFocusable(false);
        chinToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chinToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        chinToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chinToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chinToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(chinToggleButton);

        buttonGroup1.add(earsToggleButton);
        earsToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/ears.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(earsToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.earsToggleButton.text")); // NOI18N
        earsToggleButton.setFocusable(false);
        earsToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        earsToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        earsToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        earsToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                earsToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(earsToggleButton);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.jLabel1.text")); // NOI18N

        sexComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Male & Female", "Male", "Female" }));
        sexComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sexComboBoxItemStateChanged(evt);
            }
        });
        sexComboBox.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                sexComboBoxInputMethodTextChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.jLabel2.text")); // NOI18N

        ageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Under 18", "18–29", "30–49", "Over 50" }));
        ageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ageComboBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(randomPartButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.randomPartButton.text")); // NOI18N
        randomPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomPartButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sexComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(randomPartButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sexComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(ageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(randomPartButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        jPanel2.setMinimumSize(new java.awt.Dimension(400, 73));
        jPanel2.setName(""); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setMinimumSize(new java.awt.Dimension(300, 73));
        jToolBar2.setPreferredSize(new java.awt.Dimension(300, 75));

        buttonGroup2.add(viewToggleButton);
        viewToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/view.png"))); // NOI18N
        viewToggleButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(viewToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.viewToggleButton.text")); // NOI18N
        viewToggleButton.setFocusable(false);
        viewToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        viewToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewToggleButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(viewToggleButton);

        buttonGroup2.add(moveToggleButton);
        moveToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/move.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(moveToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.moveToggleButton.text")); // NOI18N
        moveToggleButton.setFocusable(false);
        moveToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        moveToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveToggleButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(moveToggleButton);

        buttonGroup2.add(rotateToggleButton);
        rotateToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/rotate.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(rotateToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.rotateToggleButton.text")); // NOI18N
        rotateToggleButton.setFocusable(false);
        rotateToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rotateToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        rotateToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rotateToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateToggleButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(rotateToggleButton);

        buttonGroup2.add(scaleToggleButton);
        scaleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/scale.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(scaleToggleButton, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.scaleToggleButton.text")); // NOI18N
        scaleToggleButton.setFocusable(false);
        scaleToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scaleToggleButton.setPreferredSize(new java.awt.Dimension(65, 75));
        scaleToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        scaleToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleToggleButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(scaleToggleButton);
        jToolBar2.add(filler1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/create_composite.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.jButton1.text")); // NOI18N
        jButton1.setFocusTraversalPolicyProvider(true);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/import_composite.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(CompositePanel.class, "CompositePanel.jButton2.text")); // NOI18N
        jButton2.setFocusTraversalPolicyProvider(true);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);
        jToolBar2.add(filler2);

        jPanel2.add(jToolBar2, java.awt.BorderLayout.NORTH);

        compositeCanvas2.setMinimumSize(new java.awt.Dimension(200, 0));
        compositeCanvas2.setPreferredSize(new java.awt.Dimension(800, 0));
        compositeCanvas2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                compositeCanvas2MouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                compositeCanvas2MouseDragged(evt);
            }
        });
        compositeCanvas2.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                compositeCanvas2MouseWheelMoved(evt);
            }
        });
        compositeCanvas2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                compositeCanvas2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                compositeCanvas2MouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                compositeCanvas2MouseClicked(evt);
            }
        });
        jPanel2.add(compositeCanvas2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 927, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setConfigurations(String text, Boolean comboBoxEnabled, Boolean selectedComponent, int editMode) {
        CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
        compositeConfiguratin.setFacePart(text);
        compositeConfiguratin.setEnabledEditComboBox(comboBoxEnabled, editMode);

        compositeConfiguratin.setListener(listener);

        //  compositeConfiguratin.setMlistener(manipulator);
        if (selectedComponent) {
            compositeConfiguratin.setParameters();
            if (moveToggleButton.isSelected() || scaleToggleButton.isSelected() || rotateToggleButton.isSelected()) {
                // listener.getManipulator().showManipulators(compositeConfiguratin.getPosition());
            }
        } else {
            compositeConfiguratin.setIsEditing(false);
            compositeConfiguratin.resetValues();
            // manipulator.hideManipulators();
        }
        listener.updateSelectedModel();

    }

    public void resizeCanvas() {
        compositeCanvas2.resizeCanvas(compositeCanvas2.getSize());
        gridPanel.resizeGrid(gridPanel.getSize());
        // System.out.println(gridPanel.getSize().toString());
        // gridPanel.setSize(null);
    }

    private void compositeCanvas2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compositeCanvas2MouseReleased
        if (manipulatingObject) {
            if (listener.getManipulator().isShowShift()) {
                compositeData.logAction("tra " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(0).getTranslation());
                if (compositeData.getSelectedPart(1) != null) {
                    compositeData.logAction("tra " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(1).getTranslation());
                }
            }
            if (listener.getManipulator().isShowRotation()) {
                //  compositeData.logAction("rot " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(0).getRotation());
                if (compositeData.getSelectedPart(1) != null) {
                    //     compositeData.logAction("rot " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(1).getRotation());
                }
            }
            if (listener.getManipulator().isShowScale()) {
                compositeData.logAction("sca " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(0).getScale());
                if (compositeData.getSelectedPart(1) != null) {
                    compositeData.logAction("sca " + compositeData.getCurrentPart() + " " + compositeData.getSelectedPart(1).getScale());
                }
            }

        }
        listener.getManipulator().stopTransforming();
        manipulatingObject = false;

    }//GEN-LAST:event_compositeCanvas2MouseReleased

    private void compositeCanvas2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compositeCanvas2MouseClicked
        CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
        if (listener.pickObject(evt.getX(), evt.getY())) {
            compositeConfiguratin.setIsEditing(true);
            if (compositeData.getSelectedPart(1) != null) {

                switch (compositeData.getCurrentPart()) {
                    case EYES:
                        if (!eyesToggleButton.isSelected()) {
                            eyesToggleButton.doClick();
                        }
                        compositeConfiguratin.setEnabledEditComboBox(Boolean.TRUE, 0);
                        break;
                    case EYEBROWS:
                        if (!eyebrowsToggleButton.isSelected()) {
                            eyebrowsToggleButton.doClick();
                        }
                        compositeConfiguratin.setEnabledEditComboBox(Boolean.TRUE, 0);
                        break;
                    case EARS:
                        if (!earsToggleButton.isSelected()) {
                            earsToggleButton.doClick();
                        }
                        compositeConfiguratin.setEnabledEditComboBox(Boolean.TRUE, 0);
                        break;
                    case HEAD:
                        if (!headToggleButton.isSelected()) {
                            headToggleButton.doClick();
                        }
                        break;
                    case FORHEAD:
                        if (!forheadToggleButton.isSelected()) {
                            forheadToggleButton.doClick();
                        }
                        break;
                    case NOSE:
                        if (!noseToggleButton.isSelected()) {
                            noseToggleButton.doClick();
                        }
                        break;
                    case MOUTH:
                        if (!mouthToggleButton.isSelected()) {
                            mouthToggleButton.doClick();
                        }
                        break;
                    case CHIN:
                        if (!chinToggleButton.isSelected()) {
                            chinToggleButton.doClick();
                        }
                        break;
                }

                if (moveToggleButton.isSelected() || scaleToggleButton.isSelected() || rotateToggleButton.isSelected()) {
                    listener.getManipulator().showManipulators(compositeConfiguratin.getPosition());
                }

            }
            compositeConfiguratin.setParameters();
        } else {
            compositeConfiguratin.setIsEditing(false);
            compositeConfiguratin.resetValues();
        }
        listener.getManipulator().shiftManipulators(compositeConfiguratin.getPosition());
    }//GEN-LAST:event_compositeCanvas2MouseClicked

    private void compositeCanvas2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compositeCanvas2MousePressed

        manipulatingObject = listener.pickManipulator(evt.getX(), evt.getY());
        if (manipulatingObject) {
            listener.getManipulator().setScale(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getScale());
            mousePosition = evt.getPoint();
        }
        mouseDraggedX = evt.getX();
        mouseDraggedY = evt.getY();
    }//GEN-LAST:event_compositeCanvas2MousePressed

    private void compositeCanvas2MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compositeCanvas2MouseMoved
    }//GEN-LAST:event_compositeCanvas2MouseMoved

    private void compositeCanvas2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compositeCanvas2MouseDragged
        if (manipulatingObject) {
            CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
            //  listener.doManipulation(mousePosition, evt.getPoint(), GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getRotation(), compositeConfiguratin.keepProportions());

            if (compositeData.getSelectedPart(1).getEditMode() == 1) {
                listener.doManipulation(mousePosition, evt.getPoint(), compositeData.getSelectedPart(1).getRotQuat(), compositeData.getSelectedPart(1).getRotQuat(), compositeConfiguratin.keepProportions());
            }
            if (compositeData.getSelectedPart(1).getEditMode() == 2) {
                listener.doManipulation(mousePosition, evt.getPoint(), compositeData.getSelectedPart(2).getRotQuat(), compositeData.getSelectedPart(2).getRotQuat(), compositeConfiguratin.keepProportions());
            }
            if (compositeData.getSelectedPart(1).getEditMode() == 0) {
                listener.doManipulation(mousePosition, evt.getPoint(), compositeData.getSelectedPart(1).getRotQuat(), compositeData.getSelectedPart(2).getRotQuat(), compositeConfiguratin.keepProportions());
            }

            compositeData.updateSelectedPart(listener.getManipulator().getShift(), listener.getManipulator().getRotation(1), listener.getManipulator().getRotation(-1), listener.getManipulator().getScale());
            compositeConfiguratin.setParameters();

        } else {
            float x = evt.getX();
            float y = evt.getY();
            Dimension size = evt.getComponent().getSize();
            float thetaY = 360.0f * ((x - mouseDraggedX) / (float) size.width);
            float thetaX = 360.0f * ((mouseDraggedY - y) / (float) size.height);

            listener.rotate(-thetaX, -thetaY);

            mouseDraggedX = x;
            mouseDraggedY = y;

        }
    }//GEN-LAST:event_compositeCanvas2MouseDragged

    public void setModels(Map<Object, ImageIcon> icons, Map<Object, ModelInfo> models, Boolean isPair, FacePartType part, String partName, Object selected) {
        ButtonGroup buttonGroup = new ButtonGroup();
        gridPanel.clear();
        compositeData.setCurrentPart(part);

        JToggleButton button;
        button = noneButton();
        buttonGroup.add(button);
        gridPanel.addComponent(button);
        if (selected == null) {
            button.setSelected(true);
        }

        for (int i = 0; i < icons.size(); i++) {
            ModelInfo info;
            if (isPair) {
                info = models.get((i + 1) + "a");
            } else {
                info = models.get(i + 1);
            }
            if (((info.getAge() < ageTopLimit && info.getAge() > ageBottomLimit) || info.getAge() == -1)
                    && (sex == SexType.BOTH || info.getSex() == null || info.getSex() == sex)) {

                CompositeButtonActionListener buttonListener = new CompositeButtonActionListener();
                if (isPair) {
                    buttonListener.setModel(models.get((i + 1) + "a"));
                    buttonListener.setModel(models.get((i + 1) + "b"));
                } else {
                    buttonListener.setModel(models.get(i + 1));
                }
                button = createGridButton(icons.get(i + 1));
                buttonListener.setButtonIndex(i + 1);
                buttonListener.setListener(listener);
                // buttonListener.setListener2(manipulator);
                buttonListener.setComposite(compositeData);
                buttonListener.setType(part);
                button.addActionListener(buttonListener);
                gridPanel.addComponent(button);
                buttonGroup.add(button);

                Boolean comboBoxenabled = false;
                if (part.equals(FacePartType.EARS) || part.equals(FacePartType.EYES) || part.equals(FacePartType.EYEBROWS)) {
                    comboBoxenabled = true;
                }

                if (selected != null && compositeData.getSelectedPart(0).getVisible()) {
                    if (selected.equals(i + 1)) {
                        button.setSelected(true);
                        setConfigurations(partName, comboBoxenabled, true, compositeData.getSelectedPart(1).getEditMode());

                    }
                } else {

                    setConfigurations(partName, comboBoxenabled, false, 0);

                }
            }
        }
        listener.updateModelList();
        listener.updateSelectedModel();

    }

    public void setEnabledTransformations(Boolean enabled) {
        rotateToggleButton.setEnabled(enabled);
        moveToggleButton.setEnabled(enabled);
        scaleToggleButton.setEnabled(enabled);
        CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
        compositeConfiguratin.setEnabledTransformations(enabled);
        listener.getManipulator().hideManipulators();
    }

    private void earsToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_earsToggleButtonActionPerformed
        setModels(earsIcons, earsModels, true, FacePartType.EARS, "Ears", compositeData.getSelectedEars());
        setEnabledTransformations(true);
    }//GEN-LAST:event_earsToggleButtonActionPerformed

    private void chinToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chinToggleButtonActionPerformed
        setModels(chinIcons, chinModels, false, FacePartType.CHIN, "Chin", compositeData.getSelectedChin());
        setEnabledTransformations(true);
    }//GEN-LAST:event_chinToggleButtonActionPerformed

    private void mouthToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mouthToggleButtonActionPerformed
        setModels(mouthIcons, mouthModels, false, FacePartType.MOUTH, "Mouth", compositeData.getSelectedMouth());
        setEnabledTransformations(true);
    }//GEN-LAST:event_mouthToggleButtonActionPerformed

    private void noseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noseToggleButtonActionPerformed
        setModels(noseIcons, noseModels, false, FacePartType.NOSE, "Nose", compositeData.getSelectedNose());
        setEnabledTransformations(true);
    }//GEN-LAST:event_noseToggleButtonActionPerformed

    private void eyesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eyesToggleButtonActionPerformed
        setModels(eyesIcons, eyesModels, true, FacePartType.EYES, "Eyes", compositeData.getSelectedEyes());
        setEnabledTransformations(true);
    }//GEN-LAST:event_eyesToggleButtonActionPerformed

    private void eyebrowsToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eyebrowsToggleButtonActionPerformed
        setModels(eyebrowsIcons, eyebrowsModels, true, FacePartType.EYEBROWS, "Eyebrows", compositeData.getSelectedEyebrows());
        setEnabledTransformations(true);
    }//GEN-LAST:event_eyebrowsToggleButtonActionPerformed

    private void forheadToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forheadToggleButtonActionPerformed
        setModels(forheadIcons, forheadModels, false, FacePartType.FORHEAD, "Forehead", compositeData.getSelectedForhead());
        setEnabledTransformations(true);
    }//GEN-LAST:event_forheadToggleButtonActionPerformed

    private void headToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headToggleButtonActionPerformed
        setModels(headIcons, headModels, false, FacePartType.HEAD, "Head", compositeData.getSelectedHead());
        viewToggleButton.setSelected(true);
        setEnabledTransformations(false);
    }//GEN-LAST:event_headToggleButtonActionPerformed

    private void rotateToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateToggleButtonActionPerformed
        listener.getManipulator().showManipulators(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getPosition());
        listener.getManipulator().showRotation();
    }//GEN-LAST:event_rotateToggleButtonActionPerformed

    private void moveToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveToggleButtonActionPerformed
        listener.getManipulator().showManipulators(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getPosition());
        listener.getManipulator().showShift();
    }//GEN-LAST:event_moveToggleButtonActionPerformed

    private void scaleToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleToggleButtonActionPerformed
        listener.getManipulator().showManipulators(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getPosition());
        listener.getManipulator().showScale();
    }//GEN-LAST:event_scaleToggleButtonActionPerformed

    private void viewToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewToggleButtonActionPerformed
        listener.getManipulator().hideManipulators();
    }//GEN-LAST:event_viewToggleButtonActionPerformed

    private void sexComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sexComboBoxItemStateChanged
        if (sexComboBox.getSelectedIndex() == 0) {
            sex = SexType.BOTH;
        } else if (sexComboBox.getSelectedIndex() == 1) {
            sex = SexType.MALE;
        } else {
            sex = SexType.FEMALE;
        }
        resetModels();
    }//GEN-LAST:event_sexComboBoxItemStateChanged

    private void ageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ageComboBoxItemStateChanged
        if (ageComboBox.getSelectedIndex() == 0) {
            ageBottomLimit = 0;
            ageTopLimit = 200;
        } else if (ageComboBox.getSelectedIndex() == 1) {
            ageBottomLimit = 0;
            ageTopLimit = 18;
        } else if (ageComboBox.getSelectedIndex() == 2) {
            ageBottomLimit = 17;
            ageTopLimit = 30;
        } else if (ageComboBox.getSelectedIndex() == 3) {
            ageBottomLimit = 29;
            ageTopLimit = 50;
        } else {
            ageBottomLimit = 49;
            ageTopLimit = 200;
        }
        resetModels();
    }//GEN-LAST:event_ageComboBoxItemStateChanged

    private void compositeCanvas2MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_compositeCanvas2MouseWheelMoved
        if (evt.getWheelRotation() > 0) {
            listener.zoomIn(-5 * evt.getWheelRotation());
        } else {
            listener.zoomOut(5 * evt.getWheelRotation());

        }
    }//GEN-LAST:event_compositeCanvas2MouseWheelMoved

    //Randomly picks one of chosen parts of face and places it to composite
    private void randomPartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomPartButtonActionPerformed
        Runnable run = new Runnable() {

            @Override
            public void run() {
                if(facePlacement == null){
                    createFacePlacement();
                }
                
                facePlacement.pickRandomPart(sex, ageBottomLimit, ageTopLimit);
                
                GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().setIsEditing(true);

                updateListener();
            }
        ;
        };
        Thread t = new Thread(run);
        t.start();

    }//GEN-LAST:event_randomPartButtonActionPerformed

    //Generate whole random face
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Runnable run = new Runnable() {

            @Override
            public void run() {
                 if(facePlacement == null){
                    createFacePlacement();
                }
                
                facePlacement.createRandomFace(sex, ageBottomLimit, ageTopLimit);
 
                updateListener();

                GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().setIsEditing(true);
            }
        };

        Thread t = new Thread(run);
        t.start();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        Runnable run;
        run = new Runnable() {
            @Override
            public void run() {

                ProjectTopComponent selectedProjectTopComponent;
                selectedProjectTopComponent = GUIController.getSelectedProjectTopComponent();
                Composite selectedComposite = selectedProjectTopComponent.
                        getProject().getSelectedComposite();
                ArrayList< Model> models = selectedComposite.getModels();

                StringBuilder mod = new StringBuilder();

                LinkedList<TriangularDCEL> list = new LinkedList<>();
                for (Model model : models) {
                    GraphicMeshBuilderFromModel graphicMeshBuilderFromModel;
                    graphicMeshBuilderFromModel = new GraphicMeshBuilderFromModel(model);

                    GraphicMesh graphicMesh = new GraphicMesh(graphicMeshBuilderFromModel);
                    TriangularDCEL dcel = TriangularDCEL.fromMesh(graphicMesh);
                    mod.append(model.getName());
                    mod.append(' ');
                    mod.append(model.getFaces().getNumFaces());
                    mod.append(' ');
                    mod.append(dcel.getOuterFace().getRelatedPositions().size());
                    mod.append('\n');
                    list.add(dcel);
                }

                LinkedList<String> materialFileNames = new LinkedList<>();
                LinkedList<File> objectFiles = new LinkedList<>();
                for (Model model : models) {
                    objectFiles.add(model.getFile());
                    materialFileNames.add(model.getMatrials().getMaterialFileName());
                }
                Materials materials = new Materials(materialFileNames, objectFiles);

                DcelMerger merger = new DcelMerger(list.removeFirst());
                final ProgressHandle p;
                p = ProgressHandleFactory.createHandle("Merging ");

                ProgressObserver progressObserver = new ProgressObserver() {

                    public StringBuilder m;

                    @Override
                    public void updateTotalUnits(int units) {
                        p.start(units);
                    }

                    @Override
                    public void updateProgress(String description, int currentUnits) {
                        p.progress(description, currentUnits);
                    }

                    public void setMeasurement(StringBuilder measurement) {
                        m = measurement;
                    }

                    public String getMeasurement() {
                        return m.toString();
                    }
                };

                merger.merge(list, progressObserver);
                Model result = merger.getResult().toModel(materials);
                p.switchToIndeterminate();
                TextureMerger mrgr = new TextureMerger(result);
                try {
                    mrgr.mergeModel();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                p.finish();
                selectedComposite.setHeadAndClearParts(result);
                listener.updateModelList();

                //uncment belowe code to get measuremts of performance during merging
                /*JTextArea textarea = new JTextArea(mod.toString() + "\n\n" + progressObserver.getMeasurement());
                 textarea.setEditable(true);
                 JOptionPane.showMessageDialog(null, textarea);*/
            }

        };
        Thread t = new Thread(run);
        t.start();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void sexComboBoxInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_sexComboBoxInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_sexComboBoxInputMethodTextChanged

    private void createFacePlacement(){
        facePlacement = new RandomFacePlacement(compositeData, ALLOW_RANDOMFACE_SCALE,
                                                forheadModels, eyesModels, earsModels,
                                                eyebrowsModels, noseModels, mouthModels, 
                                                chinModels, headModels);
    }
    
    //Updates composite listener to draw new model
    private void updateListener() {
        //update models in GLEvaneListener
        listener.updateModelList();

        //set the parameters in configuration window
        CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
        compositeConfiguratin.setComposite(compositeData);
        compositeConfiguratin.setParameters();

        //update selected model and gizmo position in GLEventListener
        listener.updateSelectedModel();
        listener.getManipulator().shiftManipulators(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getPosition());
    }

    public void resetModels() {
        if (eyesToggleButton.isSelected()) {
            eyesToggleButton.doClick();
        }
        if (eyebrowsToggleButton.isSelected()) {
            eyebrowsToggleButton.doClick();
        }
        if (earsToggleButton.isSelected()) {
            earsToggleButton.doClick();
        }
        if (headToggleButton.isSelected()) {
            headToggleButton.doClick();
        }
        if (forheadToggleButton.isSelected()) {
            forheadToggleButton.doClick();
        }
        if (noseToggleButton.isSelected()) {
            noseToggleButton.doClick();
        }
        if (mouthToggleButton.isSelected()) {
            mouthToggleButton.doClick();
        }
        if (chinToggleButton.isSelected()) {
            chinToggleButton.doClick();
        }
    }

    public void setTextureRendering(Boolean b) {
        listener.setDrawTextures(b);
    }

    public JToggleButton noneButton() {
        JToggleButton button = new JToggleButton(new ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/remove.png")));
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (compositeData.getCurrentPart() != null) {

                    if (compositeData.getCurrentPart().equals(FacePartType.EYES) || compositeData.getCurrentPart().equals(FacePartType.EARS) || compositeData.getCurrentPart().equals(FacePartType.EYEBROWS)) {
                        compositeData.removeFacePart(compositeData.getSelectedPart(0));
                        compositeData.removeFacePart(compositeData.getSelectedPart(1));
                    } else {
                        compositeData.removeFacePart(compositeData.getSelectedPart(0));
                    }
                    GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().setIsEditing(false);
                    GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().resetValues();
                }
                listener.updateModelList();
                listener.updateSelectedModel();
            }
        });
        return button;

    }

    private void loadModels() {
        // System.out.println("resource" + getClass().getResource("/cz/fidentis/gui/previews/"));

        int counter = 1;
        /*       String current = "";
         try {
         current = new java.io.File(".").getCanonicalPath();
         System.out.println("Current dir:" + current);
         } catch (Exception e) {
         }*/
        //  current = "C:" + separatorChar + "HCI" + separatorChar + "Fidentis";
        //  String path = current + separatorChar + "GUI" + separatorChar + "src" + separatorChar + "cz" + separatorChar + "fidentis" + separatorChar + "gui" + separatorChar + "models" + separatorChar;

        String path = GUIController.getPath() + separatorChar + "models" + separatorChar;
        File f = new File(path + separatorChar + "models" + separatorChar + "head");
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith(".inf")) {
                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 4);

                // headIcons.put(counter, new ImageIcon(getClass().getResource("/cz/fidentis/gui/previews/" + name + ".jpg")));
                headIcons.put(counter, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                headModels.put(counter, loadModel(f.listFiles()[i]));
                counter++;
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "forehead");
        counter = 1;
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith(".inf")) {
                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 4);
                forheadIcons.put(counter, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                forheadModels.put(counter, loadModel(f.listFiles()[i]));
                counter++;
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "nose");
        counter = 1;
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith(".inf")) {
                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 4);
                noseIcons.put(counter, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                noseModels.put(counter, loadModel(f.listFiles()[i]));
                counter++;
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "chin");
        counter = 1;
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith(".inf")) {
                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 4);
                chinIcons.put(counter, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                chinModels.put(counter, loadModel(f.listFiles()[i]));
                counter++;
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "mouth");
        counter = 1;
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith(".inf")) {
                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 4);
                mouthIcons.put(counter, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                mouthModels.put(counter, loadModel(f.listFiles()[i]));
                counter++;
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "eyes");

        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith("right.inf")) {

                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 9);
                int index = getIndexOfPairPart(name);
                eyesIcons.put(index, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                eyesModels.put(index + "a", loadModel(f.listFiles()[i]));
            }
            if (f.listFiles()[i].toString().toLowerCase().endsWith("left.inf")) {

                int index = getIndexOfPairPart(f.listFiles()[i].getName());
                eyesModels.put(index + "b", loadModel(f.listFiles()[i]));
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "ears");
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith("right.inf")) {

                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 9);
                int index = getIndexOfPairPart(name);

                earsIcons.put(index, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                earsModels.put(index + "a", loadModel(f.listFiles()[i]));
            }
            if (f.listFiles()[i].toString().toLowerCase().endsWith("left.inf")) {
                int index = getIndexOfPairPart(f.listFiles()[i].getName());

                earsModels.put(index + "b", loadModel(f.listFiles()[i]));
            }
        }

        f = new File(path + separatorChar + "models" + separatorChar + "eyebrows");
        for (int i = 0; i < f.listFiles().length; i++) {
            if (f.listFiles()[i].toString().toLowerCase().endsWith("right.inf")) {

                String name = f.listFiles()[i].getName().substring(0, f.listFiles()[i].getName().length() - 9);
                int index = getIndexOfPairPart(name);

                eyebrowsIcons.put(index, new ImageIcon(path + separatorChar + "previews" + separatorChar + name + ".jpg"));
                eyebrowsModels.put(index + "b", loadModel(f.listFiles()[i]));
            }
            if (f.listFiles()[i].toString().toLowerCase().endsWith("left.inf")) {
                int index = getIndexOfPairPart(f.listFiles()[i].getName());

                eyebrowsModels.put(index + "a", loadModel(f.listFiles()[i]));
            }
        }

    }

    private int getIndexOfPairPart(String fileName) {
        int index = Integer.parseInt(fileName.replaceAll(numPattern, ""));

        return index;
    }

    public ModelInfo loadModel(File fnm) {
        String file = fnm.getPath();
        ModelInfo i = new ModelInfo();
        i.setFile(new File(file.substring(0, file.lastIndexOf(".")) + ".obj"));
        try {
            BufferedReader br = new BufferedReader(new FileReader(fnm));

            boolean isLoaded = true;
            String line;

            try {
                while (((line = br.readLine()) != null) && isLoaded) {
                    if (line.length() > 0) {
                        line = line.trim();
                        if (line.startsWith("<sex>")) {
                            i.setSex(line.substring(5, line.lastIndexOf('<')));
                        } else if (line.startsWith("<age>")) {
                            i.setAge(Integer.parseInt(line.substring(5, line.lastIndexOf('<'))));
                        } else if (line.startsWith("<part")) {
                            i.setPart(line.substring(line.indexOf('>') + 1, line.lastIndexOf('<')));
                        } else if (line.startsWith("<position>") && (i.getPart() != null) && (!i.getPart().equals("head"))) {
                            i.addPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<nose>")) {
                            i.setNosePosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<forehead>")) {
                            i.addForeheadPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<mouth>")) {
                            i.addMouthPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<chin>")) {
                            i.addChinPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<lefteye>")) {
                            i.addLefteyePosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<righteye>")) {
                            i.addRighteyePosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<lefteyebrow>")) {
                            i.addLefteyebrowPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<righteyebrow>")) {
                            i.addRighteyebrowPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<leftear>")) {
                            i.addLeftearPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        } else if (line.startsWith("<rightear>")) {
                            i.addRightearPosition(parsePosition(br.readLine(), br.readLine(), br.readLine()));
                        }
                    }

                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);     //???
            }

            if (!isLoaded) {
                System.out.println("Error loading model data");
                System.exit(1);     //??
            }

            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return i;
    }  // end of loadModel()

    private Vector3f parsePosition(String x, String y, String z) {
        Vector3f position = new Vector3f();
        position.setX(Float.parseFloat(x.substring(x.indexOf('>') + 1, x.lastIndexOf('<'))));
        position.setY(Float.parseFloat(y.substring(y.indexOf('>') + 1, y.lastIndexOf('<'))));
        position.setZ(Float.parseFloat(z.substring(z.indexOf('>') + 1, z.lastIndexOf('<'))));
        return position;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox ageComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JToggleButton chinToggleButton;
    private cz.fidentis.gui.Canvas compositeCanvas2;
    private javax.swing.JToggleButton earsToggleButton;
    private javax.swing.JToggleButton eyebrowsToggleButton;
    private javax.swing.JToggleButton eyesToggleButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JToggleButton forheadToggleButton;
    private cz.fidentis.gui.composite.GridPanel gridPanel;
    private javax.swing.JToggleButton headToggleButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToggleButton mouthToggleButton;
    private javax.swing.JToggleButton moveToggleButton;
    private javax.swing.JToggleButton noseToggleButton;
    private javax.swing.JButton randomPartButton;
    private javax.swing.JToggleButton rotateToggleButton;
    private javax.swing.JToggleButton scaleToggleButton;
    private javax.swing.JComboBox sexComboBox;
    private javax.swing.JToggleButton viewToggleButton;
    // End of variables declaration//GEN-END:variables
}
