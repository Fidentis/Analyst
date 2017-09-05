package cz.fidentis.gui;

import com.jogamp.opengl.util.Animator;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.renderer.GeneralGLEventListener;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.TransferHandler;
import org.netbeans.api.progress.*;

/**
 * The basic canvas for displaying models containing GLJPanel and navigation for
 * camera.
 *
 * @author Katka
 */
public class Canvas extends javax.swing.JPanel {

    ProjectTopComponent projectTopComponent;

    protected long startClickTime = 0;
    protected TimerTask task;
    protected Timer timer;
    protected GLJPanel glJPanel;
    protected GLAnimatorControl glAnimatorControl;
    protected GeneralGLEventListener listener;
    protected GeneralGLEventListener listener2;
    protected boolean isPrimary = false;

    public Canvas() {
        this.isPrimary = false;
        GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        capabilities.setDoubleBuffered(true);

        //    capabilities.setAlphaBits(0);
        glJPanel = new GLJPanel(capabilities);
        glAnimatorControl = new Animator(glJPanel);
        glAnimatorControl.start();
        initComponents();

        jPanel2.add(glJPanel);
        this.validate();

        jLabel2.setVisible(false);

        this.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                for (DataFlavor f : support.getDataFlavors()) {
                    if (f.isFlavorJavaFileListType()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                List<File> modelFiles = new ArrayList<File>();
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : files) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".obj") || name.endsWith(".ply") || name.endsWith(".stl")) {
                            modelFiles.add(f);
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    return false;
                } catch (IOException ex) {
                    return false;
                }
                addModel(modelFiles.toArray(new File[modelFiles.size()]));

                return false;
            }

        });
        this.jLabelPointCSVvalue.setVisible(false);
    }

    /**
     * Creates new form Canvas
     */
    public Canvas(ProjectTopComponent tc, boolean primary) {
        this(tc);
        this.isPrimary = primary;
        this.jLabelPointCSVvalue.setVisible(false);

    }

    public Canvas(ProjectTopComponent tc) {
        this.projectTopComponent = tc;

        this.isPrimary = false;
        GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        capabilities.setDoubleBuffered(true);

        //    capabilities.setAlphaBits(0);
        glJPanel = new GLJPanel(capabilities);
        glAnimatorControl = new Animator(glJPanel);
        glAnimatorControl.start();
        initComponents();

        jPanel2.add(glJPanel);
        this.validate();

        jLabel2.setLocation(this.getWidth() / 2 - 35, this.getHeight() / 2 - 40);

        this.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                for (DataFlavor f : support.getDataFlavors()) {
                    if (f.isFlavorJavaFileListType()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                List<File> modelFiles = new ArrayList<File>();
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : files) {
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".obj") || name.endsWith(".ply") || name.endsWith(".stl")) {
                            modelFiles.add(f);
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    return false;
                } catch (IOException ex) {
                    return false;
                }
                addModel(modelFiles.toArray(new File[modelFiles.size()]));

                return false;
            }

        });
        
        this.jLabelPointCSVvalue.setVisible(false);
    }

    /**
     * @param d - new size of canvas
     */
    public void resizeCanvas(Dimension d) {
        jPanel2.setSize(d);
        glJPanel.setSize(d);
        this.validate();
        this.repaint();
        jLabel2.setLocation(this.getWidth() / 2 - 35, this.getHeight() / 2 - 40);

    }

    public void addGLEventListener(GeneralGLEventListener generalGLlistener) {
        listener = generalGLlistener;
        glJPanel.addGLEventListener(listener);
    }

    public void setImportLabelVisible(Boolean v) {
        jLabel2.setVisible(v);
    }

    public void setFeaturePointsPanelVisibility(boolean bool) {
        featurePointsPanel.setVisible(bool);
    }

    public void setResultButtonVisible(boolean b) {
        jButton1.setVisible(b);
    }

    public void setInfo(FacialPoint fp) {
        jLabel3.setText(fp.getName());
        xTextField.setText(Float.toString(fp.getPosition().x));
        yTextField.setText(Float.toString(fp.getPosition().y));
        zTextField.setText(Float.toString(fp.getPosition().z));
        jTextArea1.setText(fp.getInfo());
    }

    public void setCoordInfo(FacialPoint fp) {
        xTextField.setText(Float.toString(fp.getPosition().x));
        yTextField.setText(Float.toString(fp.getPosition().y));
        zTextField.setText(Float.toString(fp.getPosition().z));
    }

    public void resetInfo() {
        jLabel3.setText("-");
        xTextField.setText("-");
        yTextField.setText("-");
        zTextField.setText("-");
        jTextArea1.setText("-");
    }

    public void triggerAdd() {
        this.jLabel2MouseClicked(null);
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
        jLayeredPane = new javax.swing.JLayeredPane();
        rightNavigationButton = new javax.swing.JButton();
        upNavigationButton = new javax.swing.JButton();
        minusNavigationButton = new javax.swing.JButton();
        leftnavigationButton = new javax.swing.JButton();
        homeNavigationButton = new javax.swing.JButton();
        plusNavigationButton = new javax.swing.JButton();
        downNavigationButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        featurePointsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        xTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        zTextField = new javax.swing.JTextField();
        yTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        textPane = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jLabelPointCSVvalue = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jLayeredPaneComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jLayeredPaneComponentShown(evt);
            }
        });

        rightNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/right22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(rightNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.rightNavigationButton.text")); // NOI18N
        rightNavigationButton.setBorder(null);
        rightNavigationButton.setBorderPainted(false);
        rightNavigationButton.setContentAreaFilled(false);
        rightNavigationButton.setFocusPainted(false);
        rightNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/right22pressed.png"))); // NOI18N
        rightNavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rightNavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rightNavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(rightNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(rightNavigationButton);
        rightNavigationButton.setBounds(91, 60, 22, 22);

        upNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/up22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(upNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.upNavigationButton.text")); // NOI18N
        upNavigationButton.setBorder(null);
        upNavigationButton.setContentAreaFilled(false);
        upNavigationButton.setFocusPainted(false);
        upNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/up22pressed.png"))); // NOI18N
        upNavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                upNavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                upNavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(upNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(upNavigationButton);
        upNavigationButton.setBounds(63, 32, 22, 22);

        minusNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/minus.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(minusNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.minusNavigationButton.text")); // NOI18N
        minusNavigationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        minusNavigationButton.setBorderPainted(false);
        minusNavigationButton.setContentAreaFilled(false);
        minusNavigationButton.setFocusPainted(false);
        minusNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/minusPressed.png"))); // NOI18N
        minusNavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                minusNavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                minusNavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(minusNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(minusNavigationButton);
        minusNavigationButton.setBounds(62, 150, 22, 22);

        leftnavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/left22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(leftnavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.leftnavigationButton.text")); // NOI18N
        leftnavigationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        leftnavigationButton.setContentAreaFilled(false);
        leftnavigationButton.setFocusPainted(false);
        leftnavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/left22pressed.png"))); // NOI18N
        leftnavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                leftnavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                leftnavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(leftnavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(leftnavigationButton);
        leftnavigationButton.setBounds(33, 60, 22, 22);

        homeNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/home22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(homeNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.homeNavigationButton.text")); // NOI18N
        homeNavigationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        homeNavigationButton.setContentAreaFilled(false);
        homeNavigationButton.setFocusPainted(false);
        homeNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/home22pressed.png"))); // NOI18N
        homeNavigationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeNavigationButtonActionPerformed(evt);
            }
        });
        jLayeredPane.setLayer(homeNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(homeNavigationButton);
        homeNavigationButton.setBounds(63, 60, 22, 22);

        plusNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/plus.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(plusNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.plusNavigationButton.text")); // NOI18N
        plusNavigationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        plusNavigationButton.setBorderPainted(false);
        plusNavigationButton.setContentAreaFilled(false);
        plusNavigationButton.setFocusPainted(false);
        plusNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/plusPressed.png"))); // NOI18N
        plusNavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                plusNavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                plusNavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(plusNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(plusNavigationButton);
        plusNavigationButton.setBounds(62, 126, 22, 22);

        downNavigationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/down22.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(downNavigationButton, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.downNavigationButton.text")); // NOI18N
        downNavigationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        downNavigationButton.setContentAreaFilled(false);
        downNavigationButton.setFocusPainted(false);
        downNavigationButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/down22pressed.png"))); // NOI18N
        downNavigationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                downNavigationButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                downNavigationButtonMouseReleased(evt);
            }
        });
        jLayeredPane.setLayer(downNavigationButton, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(downNavigationButton);
        downNavigationButton.setBounds(63, 90, 22, 22);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/navigationBackground.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel1.text")); // NOI18N
        jLayeredPane.setLayer(jLabel1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(jLabel1);
        jLabel1.setBounds(30, 30, 86, 86);

        jLabel2.setVisible(false);
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/resources/import_composite.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jLayeredPane.setLayer(jLabel2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(jLabel2);
        jLabel2.setBounds(190, 110, 90, 80);

        jPanel2.setLayout(new java.awt.BorderLayout());
        jLayeredPane.setLayer(jPanel2, javax.swing.JLayeredPane.PALETTE_LAYER);
        jLayeredPane.add(jPanel2);
        jPanel2.setBounds(0, 0, 0, 0);
        jPanel2.setBounds(0, 0,jLayeredPane.getWidth(),jLayeredPane.getHeight());

        featurePointsPanel.setBackground(new java.awt.Color(255, 255, 255));
        featurePointsPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel3.text")); // NOI18N
        jLabel3.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel4.text")); // NOI18N

        xTextField.setEditable(false);
        xTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        xTextField.setText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.xTextField.text")); // NOI18N
        xTextField.setBorder(null);
        xTextField.setPreferredSize(new java.awt.Dimension(12, 22));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel6.text")); // NOI18N

        zTextField.setEditable(false);
        zTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        zTextField.setText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.zTextField.text")); // NOI18N
        zTextField.setBorder(null);
        zTextField.setPreferredSize(new java.awt.Dimension(12, 22));

        yTextField.setEditable(false);
        yTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        yTextField.setText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.yTextField.text")); // NOI18N
        yTextField.setBorder(null);
        yTextField.setPreferredSize(new java.awt.Dimension(12, 22));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabel7.text")); // NOI18N

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jTextArea1.text")); // NOI18N
        jTextArea1.setToolTipText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jTextArea1.toolTipText")); // NOI18N
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(null);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout featurePointsPanelLayout = new javax.swing.GroupLayout(featurePointsPanel);
        featurePointsPanel.setLayout(featurePointsPanelLayout);
        featurePointsPanelLayout.setHorizontalGroup(
            featurePointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(featurePointsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(featurePointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(featurePointsPanelLayout.createSequentialGroup()
                        .addGroup(featurePointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(featurePointsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        featurePointsPanelLayout.setVerticalGroup(
            featurePointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(featurePointsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(featurePointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(zTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
        );

        jLayeredPane.setLayer(featurePointsPanel, javax.swing.JLayeredPane.POPUP_LAYER);
        jLayeredPane.add(featurePointsPanel);
        featurePointsPanel.setBounds(10, 170, 250, 120);
        featurePointsPanel.setVisible(false);

        textPane.setEditable(false);
        textPane.setBorder(null);
        textPane.setText(org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.textPane.text")); // NOI18N
        textPane.setEnabled(false);
        textPane.setPreferredSize(new java.awt.Dimension(300, 100));
        textPane.setVisible(false);
        jLayeredPane.setLayer(textPane, javax.swing.JLayeredPane.POPUP_LAYER);
        jLayeredPane.add(textPane);
        textPane.setBounds(10, 180, 250, 110);

        jButton1.setVisible(false);
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jButton1.text")); // NOI18N
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jLayeredPane.setLayer(jButton1, javax.swing.JLayeredPane.POPUP_LAYER);
        jLayeredPane.add(jButton1);
        jButton1.setBounds(303, 10, 100, 120);

        jLabelPointCSVvalue.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelPointCSVvalue.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPointCSVvalue, org.openide.util.NbBundle.getMessage(Canvas.class, "Canvas.jLabelPointCSVvalue.text")); // NOI18N
        jLayeredPane.setLayer(jLabelPointCSVvalue, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane.add(jLabelPointCSVvalue);
        jLabelPointCSVvalue.setBounds(290, 200, 110, 16);

        add(jLayeredPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void homeNavigationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeNavigationButtonActionPerformed
        listener.rotationAndSizeRestart();
        if (listener2 != null) {
            listener2.rotationAndSizeRestart();
        }
    }//GEN-LAST:event_homeNavigationButtonActionPerformed

    private void jLayeredPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLayeredPaneComponentResized
        jPanel2.setBounds(0, 0, jLayeredPane.getWidth(), jLayeredPane.getHeight());
        glJPanel.setBounds(jLayeredPane.getX(), jLayeredPane.getY(), jLayeredPane.getWidth(), jLayeredPane.getHeight());
        textPane.setLocation(10, jLayeredPane.getHeight() - textPane.getHeight() - 10);
        featurePointsPanel.setLocation(10, jLayeredPane.getHeight() - textPane.getHeight() - 10);
        jButton1.setLocation(jLayeredPane.getWidth() - jButton1.getWidth() - 10, 10);
    }//GEN-LAST:event_jLayeredPaneComponentResized

    private void jLayeredPaneComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLayeredPaneComponentShown
        jPanel2.setBounds(0, 0, jLayeredPane.getWidth(), jLayeredPane.getHeight());
        glJPanel.setBounds(jLayeredPane.getX(), jLayeredPane.getY(), jLayeredPane.getWidth(), jLayeredPane.getHeight());
        textPane.setLocation(10, jLayeredPane.getHeight() - textPane.getHeight() - 10);
        featurePointsPanel.setLocation(10, jLayeredPane.getHeight() - textPane.getHeight() - 10);
        jButton1.setLocation(jLayeredPane.getWidth() - jButton1.getWidth() - 5, 5);
    }//GEN-LAST:event_jLayeredPaneComponentShown


    private void leftnavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftnavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.rotateLeft(2);
                if (listener2 != null) {
                    listener2.rotateLeft(2);
                }
            }
        };
        timer.schedule(task, 500, 100);
    }//GEN-LAST:event_leftnavigationButtonMousePressed

    private void upNavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upNavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.rotateUp(2);
                if (listener2 != null) {
                    listener2.rotateUp(2);
                }
            }
        };
        timer.schedule(task, 500, 100);

    }//GEN-LAST:event_upNavigationButtonMousePressed

    private void upNavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upNavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.rotateUp(22.5);
            if (listener2 != null) {
                listener2.rotateUp(22.5);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_upNavigationButtonMouseReleased

    private void rightNavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightNavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.rotateRight(2);
                if (listener2 != null) {
                    listener2.rotateRight(2);
                }
            }
        };
        timer.schedule(task, 500, 100);
    }//GEN-LAST:event_rightNavigationButtonMousePressed

    private void rightNavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightNavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.rotateRight(22.5);
            if (listener2 != null) {
                listener2.rotateRight(22.5);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_rightNavigationButtonMouseReleased

    private void leftnavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftnavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.rotateLeft(22.5);
            if (listener2 != null) {
                listener2.rotateLeft(22.5);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_leftnavigationButtonMouseReleased

    private void downNavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downNavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.rotateDown(2);
                if (listener2 != null) {
                    listener2.rotateDown(2);
                }
            }
        };
        timer.schedule(task, 500, 100);
    }//GEN-LAST:event_downNavigationButtonMousePressed

    private void downNavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downNavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.rotateDown(22.5);
            if (listener2 != null) {
                listener2.rotateDown(22.5);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_downNavigationButtonMouseReleased

    private void plusNavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plusNavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.zoomIn(3);
                if (listener2 != null) {
                    listener2.zoomIn(3);
                }
            }
        };
        timer.schedule(task, 500, 100);
    }//GEN-LAST:event_plusNavigationButtonMousePressed

    private void plusNavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plusNavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.zoomIn(30);
            if (listener2 != null) {
                listener2.zoomIn(30);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_plusNavigationButtonMouseReleased

    private void minusNavigationButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minusNavigationButtonMousePressed
        timer = new Timer();
        startClickTime = System.currentTimeMillis();
        task = new TimerTask() {
            @Override
            public void run() {
                listener.zoomOut(3);
                if (listener2 != null) {
                    listener2.zoomOut(3);
                }
            }
        };
        timer.schedule(task, 500, 100);
    }//GEN-LAST:event_minusNavigationButtonMousePressed

    private void minusNavigationButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minusNavigationButtonMouseReleased
        timer.cancel();
        if ((System.currentTimeMillis() - startClickTime) < 500) {
            listener.zoomOut(30);
            if (listener2 != null) {
                listener2.zoomOut(30);
            }
        }
        startClickTime = 0;
    }//GEN-LAST:event_minusNavigationButtonMouseReleased

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jLabel2.setLocation(this.getWidth() / 2 - 35, this.getHeight() / 2 - 40);
    }//GEN-LAST:event_formComponentResized

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        final JFileChooser jFileChooser1 = GUIController.getjFileChooser1();
        if ((projectTopComponent.getProject().getSelectedPart() == 4)
                || (projectTopComponent.getProject().getSelectedPart() == 3 && !isPrimary)) {
            jFileChooser1.setMultiSelectionEnabled(true);
        } else {
            jFileChooser1.setMultiSelectionEnabled(false);
        }
        int result = jFileChooser1.showOpenDialog(projectTopComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
            if (jFileChooser1.isMultiSelectionEnabled()) {
                this.addModel(jFileChooser1.getSelectedFiles());
            } else {
                File[] fileArray = new File[1];
                fileArray[0] = jFileChooser1.getSelectedFile();
                this.addModel(fileArray);
            }
        }
    }//GEN-LAST:event_jLabel2MouseClicked
    public void setDescriptionText(String text) {
        textPane.setText(text);
        textPane.setVisible(true);
        textPane.setEnabled(true);
    }

    public String getDescriptionText() {
        return textPane.getText();
    }

    public void showResultIcon() {
        jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getResultIcon());
    }

    public void showModelIcon() {
        jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getModelIcon());
    }

    public void createResultIcon() {
        int width = glJPanel.getWidth();
        int height = glJPanel.getHeight();
        glJPanel.display();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        glJPanel.printAll(g);
        img.flush();
        ImageIcon i = new ImageIcon(img.getScaledInstance(90, 108, Image.SCALE_SMOOTH));
        projectTopComponent.getProject().getSelectedComparison2Faces().setResultIcon(i);

    }

    private void addModel(final File[] files) {
        if (files.length <= 0) {
            return;
        }

        if (projectTopComponent.getProject().getSelectedPart() == 2) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ProgressHandle p;
                    p = ProgressHandleFactory.createHandle("Loading...");
                    p.start();
                    p.switchToIndeterminate();

                    try {

                        if (isPrimary) {
                            Model model = ModelLoader.instance().loadModel(new File(files[0].getPath()), true, true);

                            projectTopComponent.getProject().getSelectedComparison2Faces().setModel1(model);
                            projectTopComponent.getViewerPanel_2Faces().setModel1(model);
                            projectTopComponent.getProject().setSelectedPart(2);
                            GUIController.getConfigurationTopComponent().getRegistrationConfiguration().updateRegisterButtonEnabled();
                            
                            int width = glJPanel.getWidth();
                            int height = glJPanel.getHeight();
                            glJPanel.display();
                            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g = img.createGraphics();
                            glJPanel.printAll(g);
                            img.flush();
                            ImageIcon i = new ImageIcon(img.getScaledInstance(90, 108, Image.SCALE_SMOOTH));
                            jButton1.setIcon(i);
                            projectTopComponent.getProject().getSelectedComparison2Faces().setModelIcon(i);

                        } else {
                           
                            Model model = ModelLoader.instance().loadModel(new File(files[0].getPath()), true, true);

                            projectTopComponent.getProject().getSelectedComparison2Faces().setModel2(model);
                            projectTopComponent.getViewerPanel_2Faces().setModel2(model);
                            projectTopComponent.getProject().setSelectedPart(2);
                            GUIController.getConfigurationTopComponent().getRegistrationConfiguration().updateRegisterButtonEnabled();
                            
                        }

                        p.finish();

                        GUIController.updateNavigator();
                    } catch (Exception ex) {
                        p.finish();
                    }
                }
            };

            Thread t = new Thread(run);
            t.start(); // start the task and progress visualisation

        } else if (projectTopComponent.getProject().getSelectedPart() == 3) {

            if (isPrimary) {
                
                Model model = ModelLoader.instance().loadModel(new File(files[0].getPath()), true, true);

                projectTopComponent.getProject().getSelectedOneToManyComparison().setPrimaryModel(model);
                projectTopComponent.getOneToManyViewerPanel().setModel1(model);
                projectTopComponent.getProject().setSelectedPart(3);
                GUIController.getConfigurationTopComponent().getOneToManyRegistrationConfiguration().updateRegisterButtonEnabled();
            } else {
                for (File file : files) {
                    projectTopComponent.getProject().getSelectedOneToManyComparison().addModel(file);

                }
                File file = projectTopComponent.getProject().getSelectedOneToManyComparison().getModel(0);
                
                Model model = ModelLoader.instance().loadModel(file, true, true);

                projectTopComponent.getOneToManyViewerPanel().getListener2().setModels(model);
                GUIController.getConfigurationTopComponent().getOneToManyRegistrationConfiguration().updateRegisterButtonEnabled();
            }

        } else if (projectTopComponent.getProject().getSelectedPart() == 4) {
            for (File file : files) {
                projectTopComponent.getProject().getSelectedBatchComparison().addModel(file);

            }
            File file = projectTopComponent.getProject().getSelectedBatchComparison().getModel(0);
            
            Model model = ModelLoader.instance().loadModel(file, true, true);
            projectTopComponent.getViewerPanel_Batch().getListener().setModels(model);
            GUIController.getConfigurationTopComponent().getBatchRegistrationConfiguration().updateRegisterButtonEnabled();
            GUIController.getConfigurationTopComponent().getBatchRegistrationConfiguration().populateFacesComboBox();
        } else if (projectTopComponent.getProject().getSelectedPart() == 6) {
            
            Model m = ModelLoader.instance().loadModel(files[0], true, true);
            
            projectTopComponent.getProject().getSelectedAgeing().setOriginModel(m);
            projectTopComponent.getAgeingViewerPanel().getListenerOrigin().setModels(m);
            GUIController.getConfigurationTopComponent().getAgeingConfiguration().setConfiguration();
        }
        jLabel2.setVisible(false);
        GUIController.getNavigatorTopComponent().update();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (projectTopComponent.getProject().getSelectedComparison2Faces().getState() == 2) {

            if (projectTopComponent.getViewerPanel_2Faces().getListener1().getNumberOfModels() > 1) {
                Model model = projectTopComponent.getViewerPanel_2Faces().getListener1().getModel();
                projectTopComponent.getViewerPanel_2Faces().getListener1().setModels(model);
                jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getResultIcon());
            } else {
                //   ModelLoader l = new ModelLoader();
                //  Model model = l.loadModel(projectTopComponent.getProject().getSelectedComparison2Faces().getModel1().getFile(), false, true);
                Model model = projectTopComponent.getViewerPanel_2Faces().getListener2().getModel();
                projectTopComponent.getViewerPanel_2Faces().getListener1().addModel(model);
                jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getModelIcon());

            }
        } else {
            if (projectTopComponent.getViewerPanel_2Faces().getListener1().isPaintHD()) {
                jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getResultIcon());
            } else {
                jButton1.setIcon(projectTopComponent.getProject().getSelectedComparison2Faces().getModelIcon());
            }
            projectTopComponent.getViewerPanel_2Faces().getListener1().setPaintHD(
                    !projectTopComponent.getViewerPanel_2Faces().getListener1().isPaintHD());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public void showPointValue(boolean visible, String text, int x, int y){
        jLabelPointCSVvalue.setVisible(visible);
        jLabelPointCSVvalue.setBounds(x, y, 250, 16);
        jLabelPointCSVvalue.setText(text);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    protected javax.swing.JButton downNavigationButton;
    private javax.swing.JPanel featurePointsPanel;
    protected javax.swing.JButton homeNavigationButton;
    private javax.swing.JButton jButton1;
    protected javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelPointCSVvalue;
    protected javax.swing.JLayeredPane jLayeredPane;
    protected javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    protected javax.swing.JButton leftnavigationButton;
    protected javax.swing.JButton minusNavigationButton;
    protected javax.swing.JButton plusNavigationButton;
    protected javax.swing.JButton rightNavigationButton;
    private javax.swing.JTextPane textPane;
    protected javax.swing.JButton upNavigationButton;
    private javax.swing.JTextField xTextField;
    private javax.swing.JTextField yTextField;
    private javax.swing.JTextField zTextField;
    // End of variables declaration//GEN-END:variables
}
