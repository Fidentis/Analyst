/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.composite;

import cz.fidentis.composite.CompositeModel;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.controller.Composite;
//import cz.fidentis.model.Vector3f;
import cz.fidentis.renderer.CompositeGLEventListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katka
 */
public class CompositeConfiguration extends javax.swing.JPanel {

    private Vector3f previousAngle = new Vector3f();
    private long stateChangeTime = 0;

    /**
     * Creates new form CompositeConfiguration
     */
    public CompositeConfiguration() {
        initComponents();
    }

    public void setFacePart(String text) {
        facePartLabel.setText(text);
    }

     public void setEnabledTransformations(Boolean enabled){
         sizeXSpinner.setEnabled(enabled);
         sizeYSpinner.setEnabled(enabled);
         sizeZSpinner.setEnabled(enabled);
         rotationXSpinner.setEnabled(enabled);
         rotationYSpinner.setEnabled(enabled);
         rotationZSpinner.setEnabled(enabled);
         positionXSpinner.setEnabled(enabled);
         positionYSpinner.setEnabled(enabled);
         positionZSpinner.setEnabled(enabled);
     }
    
    public void setParameters() {
        if (composite != null) {
            Vector3f position;
            Vector3f rotation;
            Vector3f scale;
            CompositeModel part = composite.getSelectedPart(1);
            if (part != null) {
                if (part.getPart().equals(FacePartType.RIGHT_EYE) || part.getPart().equals(FacePartType.RIGHT_EAR) || part.getPart().equals(FacePartType.RIGHT_EYEBROW)) {
                    CompositeModel part2 = composite.getSelectedPart(2);
                    if (part.getEditMode() == 0) {
                        position = new Vector3f((part.getTranslation().getX() + part2.getTranslation().getX() + part.getInitialShift().getX() + part2.getInitialShift().getX()) / 2,
                                (part.getTranslation().getY() + part2.getTranslation().getY() + part.getInitialShift().getY() + part2.getInitialShift().getY()) / 2,
                                (part.getTranslation().getZ() + part2.getTranslation().getZ() + part.getInitialShift().getZ() + part2.getInitialShift().getZ()) / 2);


                        //  rotation = new Vector3f(part.getRotation().getX(), part.getRotation().getY(),part.getRotation().getZ()) ;
                        rotation = new Vector3f((part.getRotation().getX() + part2.getRotation().getX() + part.getInitialRotation().getX() + part2.getInitialRotation().getX()) / 2,
                                (part.getRotation().getY() - part2.getRotation().getY() + part.getInitialRotation().getY() - part2.getInitialRotation().getY()) / 2,
                                (part.getRotation().getZ() - part2.getRotation().getZ() + part.getInitialRotation().getZ() - part2.getInitialRotation().getZ()) / 2);


                        scale = new Vector3f((part.getScale().getX() + part2.getScale().getX()) / 2,
                                (part.getScale().getY() + part2.getScale().getY()) / 2,
                                (part.getScale().getZ() + part2.getScale().getZ()) / 2);

                        //  scale = new Vector3f(part.getScale().getX(), part.getScale().getY(), part.getScale().getZ());


                    } else if (part.getEditMode() == 2) {

                        position = new Vector3f(part2.getTranslation().getX() + part2.getInitialShift().getX(),
                                part2.getTranslation().getY() + part2.getInitialShift().getY(),
                                part2.getTranslation().getZ() + part2.getInitialShift().getZ());
                        rotation = new Vector3f(part2.getRotation().getX(), part2.getRotation().getY(), part2.getRotation().getZ());
                        scale = new Vector3f(part2.getScale().getX(), part2.getScale().getY(), part2.getScale().getZ());

                    } else {

                        position = new Vector3f(part.getTranslation().getX() + part.getInitialShift().getX(),
                                part.getTranslation().getY() + part.getInitialShift().getY(),
                                part.getTranslation().getZ() + part.getInitialShift().getZ());
                        rotation = new Vector3f(part.getRotation().getX(), part.getRotation().getY(), part.getRotation().getZ());

                        scale = new Vector3f(part.getScale().getX(), part.getScale().getY(), part.getScale().getZ());
                    }
                } else {
                    position = new Vector3f(part.getTranslation().getX() + part.getInitialShift().getX(),
                            part.getTranslation().getY() + part.getInitialShift().getY(),
                            part.getTranslation().getZ() + part.getInitialShift().getZ());
                    rotation = new Vector3f(part.getRotation().getX(), part.getRotation().getY(), part.getRotation().getZ());

                    scale = new Vector3f(part.getScale().getX(), part.getScale().getY(), part.getScale().getZ());
                }

                Boolean kp = keepProportions;
                keepProportions = false;
                isEditing = false;

                positionXSpinner.setValue(position.getX());
                positionYSpinner.setValue(position.getY());
                positionZSpinner.setValue(position.getZ());


                rotationXSpinner.setValue(rotation.getX());
                rotationYSpinner.setValue(rotation.getY());
                rotationZSpinner.setValue(rotation.getZ());
                previousAngle = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));


                sizeXSpinner.setValue(scale.getX());
                sizeYSpinner.setValue(scale.getY());
                sizeZSpinner.setValue(scale.getZ());
                isEditing = true;
                keepProportions = kp;
            }
        }
    }

    public void setListener(CompositeGLEventListener listener) {
        this.listener = listener;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    public void setEnabledEditComboBox(Boolean state, int editMode) {
        /*     leftRadioButton.setEnabled(state);
         rightRadioButton.setEnabled(state);
         symetricRadioButton.setEnabled(state); */

        editLabel.setVisible(state);
        editComboBox.setVisible(state);
        editComboBox.setSelectedIndex(editMode);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        positionXSpinner = new javax.swing.JSpinner();
        positionYSpinner = new javax.swing.JSpinner();
        positionZSpinner = new javax.swing.JSpinner();
        jPanel19 = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        facePartLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel17 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sizeXSpinner = new javax.swing.JSpinner();
        sizeYSpinner = new javax.swing.JSpinner();
        sizeZSpinner = new javax.swing.JSpinner();
        jPanel15 = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        rotationXSpinner = new javax.swing.JSpinner();
        rotationYSpinner = new javax.swing.JSpinner();
        rotationZSpinner = new javax.swing.JSpinner();
        jPanel16 = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        editLabel = new javax.swing.JLabel();
        editComboBox = new javax.swing.JComboBox();

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 300));

        jPanel20.setPreferredSize(new java.awt.Dimension(20, 130));
        jPanel20.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel5.text")); // NOI18N
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel20.add(jLabel5);

        positionXSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        positionXSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        positionXSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        positionXSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        positionXSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStateChanged(evt);
            }
        });
        jPanel20.add(positionXSpinner);

        positionYSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        positionYSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        positionYSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        positionYSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        positionYSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStateChanged(evt);
            }
        });
        jPanel20.add(positionYSpinner);

        positionZSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        positionZSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        positionZSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        positionZSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        positionZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerStateChanged(evt);
            }
        });
        jPanel20.add(positionZSpinner);

        jPanel19.setPreferredSize(new java.awt.Dimension(15, 130));
        jPanel19.setLayout(new java.awt.GridLayout(4, 0, 0, 10));
        jPanel19.add(filler7);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel15.text")); // NOI18N
        jPanel19.add(jLabel15);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel16.text")); // NOI18N
        jPanel19.add(jLabel16);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel17.text")); // NOI18N
        jPanel19.add(jLabel17);

        facePartLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(facePartLabel, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.facePartLabel.text")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel2.text")); // NOI18N

        jCheckBox1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jCheckBox1.text")); // NOI18N
        jCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBox1.setMinimumSize(new java.awt.Dimension(50, 23));
        jCheckBox1.setPreferredSize(new java.awt.Dimension(50, 23));
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jPanel17.setPreferredSize(new java.awt.Dimension(15, 130));
        jPanel17.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel1.text")); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel17.add(jLabel1);

        sizeXSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0010f), null, Float.valueOf(0.1f)));
        sizeXSpinner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sizeXSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        sizeXSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        sizeXSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        sizeXSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeXSpinnerStateChanged(evt);
            }
        });
        jPanel17.add(sizeXSpinner);

        sizeYSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0010f), null, Float.valueOf(0.1f)));
        sizeYSpinner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sizeYSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        sizeYSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        sizeYSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        sizeYSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeYSpinnerStateChanged(evt);
            }
        });
        jPanel17.add(sizeYSpinner);

        sizeZSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0010f), null, Float.valueOf(0.1f)));
        sizeZSpinner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sizeZSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        sizeZSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        sizeZSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        sizeZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeZSpinnerStateChanged(evt);
            }
        });
        jPanel17.add(sizeZSpinner);

        jPanel15.setPreferredSize(new java.awt.Dimension(15, 130));
        jPanel15.setLayout(new java.awt.GridLayout(4, 0, 0, 10));
        jPanel15.add(filler5);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel3.text")); // NOI18N
        jPanel15.add(jLabel3);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel13.text")); // NOI18N
        jPanel15.add(jLabel13);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel11.text")); // NOI18N
        jPanel15.add(jLabel11);

        jPanel18.setPreferredSize(new java.awt.Dimension(15, 130));
        jPanel18.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel4.text")); // NOI18N
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel18.add(jLabel4);

        rotationXSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        rotationXSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        rotationXSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        rotationXSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        rotationXSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xRotationSpinnerStateChanged(evt);
            }
        });
        jPanel18.add(rotationXSpinner);

        rotationYSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        rotationYSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        rotationYSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        rotationYSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        rotationYSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yRotationSpinnerStateChanged(evt);
            }
        });
        jPanel18.add(rotationYSpinner);

        rotationZSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        rotationZSpinner.setMaximumSize(new java.awt.Dimension(50, 30));
        rotationZSpinner.setMinimumSize(new java.awt.Dimension(20, 10));
        rotationZSpinner.setPreferredSize(new java.awt.Dimension(20, 10));
        rotationZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zRotationSpinnerStateChanged(evt);
            }
        });
        jPanel18.add(rotationZSpinner);

        jPanel16.setPreferredSize(new java.awt.Dimension(15, 130));
        jPanel16.setLayout(new java.awt.GridLayout(4, 0, 0, 10));
        jPanel16.add(filler6);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel14.text")); // NOI18N
        jPanel16.add(jLabel14);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel10.text")); // NOI18N
        jPanel16.add(jLabel10);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.jLabel12.text")); // NOI18N
        jPanel16.add(jLabel12);

        org.openide.awt.Mnemonics.setLocalizedText(editLabel, org.openide.util.NbBundle.getMessage(CompositeConfiguration.class, "CompositeConfiguration.editLabel.text")); // NOI18N

        editComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Both", "Right", "Left" }));
        editComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(editLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(editComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(facePartLabel))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(11, 11, 11)
                .addComponent(facePartLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editLabel)
                    .addComponent(editComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        keepProportions = jCheckBox1.isSelected();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void spinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerStateChanged
        if (isEditing) {
            updateValues();
            previousAngle = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));
        }
    }//GEN-LAST:event_spinnerStateChanged

    private void sizeXSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeXSpinnerStateChanged
        if (keepProportions) {
            keepProportions = false;
            float value = 0;
            if ((composite != null) && (composite.getSelectedPart(1) != null)) {
                if (composite.getCurrentPart().equals(FacePartType.EYES) || composite.getCurrentPart().equals(FacePartType.EYEBROWS) || composite.getCurrentPart().equals(FacePartType.EARS)) {
                    if (editComboBox.getSelectedIndex() == 0 && composite.getSelectedPart(1) != null && composite.getSelectedPart(2) != null) {
                        value = (composite.getSelectedPart(1).getScale().getX() + composite.getSelectedPart(2).getScale().getX()) / 2;
                    } else if (editComboBox.getSelectedIndex() == 1 && composite.getSelectedPart(1) != null) {
                        value = composite.getSelectedPart(1).getScale().getX();
                    } else if (composite.getSelectedPart(2) != null) {
                        value = composite.getSelectedPart(2).getScale().getX();
                    }
                } else {
                    value = composite.getSelectedPart(1).getScale().getX();
                }

                Float ratio = Float.valueOf(sizeXSpinner.getValue().toString()) / value;
                sizeYSpinner.setValue(ratio * Float.valueOf(sizeYSpinner.getValue().toString()));

                sizeZSpinner.setValue(ratio * Float.valueOf(sizeZSpinner.getValue().toString()));

            }
            keepProportions = true;
        }
        if (isEditing) {
            updateValues();
        }

    }//GEN-LAST:event_sizeXSpinnerStateChanged

    private void sizeYSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeYSpinnerStateChanged
        if (keepProportions) {
            keepProportions = false;
            float value = 0;
            if ((composite != null) && (composite.getSelectedPart(1) != null)) {
                if (composite.getCurrentPart().equals(FacePartType.EYES) || composite.getCurrentPart().equals(FacePartType.EYEBROWS) || composite.getCurrentPart().equals(FacePartType.EARS)) {
                    if (editComboBox.getSelectedIndex() == 0 && composite.getSelectedPart(1) != null && composite.getSelectedPart(2) != null) {
                        value = (composite.getSelectedPart(1).getScale().getY() + composite.getSelectedPart(2).getScale().getY()) / 2;
                    } else if (editComboBox.getSelectedIndex() == 1 && composite.getSelectedPart(1) != null) {
                        value = composite.getSelectedPart(1).getScale().getY();
                    } else if (composite.getSelectedPart(2) != null) {
                        value = composite.getSelectedPart(2).getScale().getY();
                    }
                } else {
                    value = composite.getSelectedPart(1).getScale().getY();
                }
                Float ratio = Float.valueOf(sizeYSpinner.getValue().toString()) / value;
                sizeXSpinner.setValue(ratio * Float.valueOf(sizeXSpinner.getValue().toString()));

                sizeZSpinner.setValue(ratio * Float.valueOf(sizeZSpinner.getValue().toString()));

            }
            keepProportions = true;
        }
        if (isEditing) {
            updateValues();
        }
    }//GEN-LAST:event_sizeYSpinnerStateChanged

    private void sizeZSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeZSpinnerStateChanged
        if (keepProportions) {
            keepProportions = false;
            float value = 0;
            if ((composite != null) && (composite.getSelectedPart(1) != null)) {
                if (composite.getCurrentPart().equals(FacePartType.EYES) || composite.getCurrentPart().equals(FacePartType.EYEBROWS) || composite.getCurrentPart().equals(FacePartType.EARS)) {
                    if (editComboBox.getSelectedIndex() == 0 && composite.getSelectedPart(1) != null && composite.getSelectedPart(2) != null) {
                        value = (composite.getSelectedPart(1).getScale().getZ() + composite.getSelectedPart(2).getScale().getZ()) / 2;
                    } else if (editComboBox.getSelectedIndex() == 1 && composite.getSelectedPart(1) != null) {
                        value = composite.getSelectedPart(1).getScale().getZ();
                    } else if (composite.getSelectedPart(2) != null) {
                        value = composite.getSelectedPart(2).getScale().getZ();
                    }
                } else {
                    value = composite.getSelectedPart(1).getScale().getZ();
                }


                Float ratio = Float.valueOf(sizeZSpinner.getValue().toString()) / value;
                sizeYSpinner.setValue(ratio * Float.valueOf(sizeYSpinner.getValue().toString()));

                sizeXSpinner.setValue(ratio * Float.valueOf(sizeXSpinner.getValue().toString()));

            }
            keepProportions = true;
        }

        if (isEditing) {
            updateValues();
        }

    }//GEN-LAST:event_sizeZSpinnerStateChanged

    private void editComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editComboBoxActionPerformed
        if (composite != null) {
            composite.setEditMode(editComboBox.getSelectedIndex());
            listener.updateSelectedModel();

            setParameters();
            listener.getManipulator().shiftManipulators(getPosition());
        }
    }//GEN-LAST:event_editComboBoxActionPerformed

    private void xRotationSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xRotationSpinnerStateChanged
        if (isEditing) {
            updateValues();
            previousAngle = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));
        }
    }//GEN-LAST:event_xRotationSpinnerStateChanged

    private void yRotationSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yRotationSpinnerStateChanged
        if (isEditing) {
            updateValues();
            previousAngle = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));

        }
    }//GEN-LAST:event_yRotationSpinnerStateChanged

    private void zRotationSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zRotationSpinnerStateChanged
        if (isEditing) {
            updateValues();
            previousAngle = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));
            
        }
    }//GEN-LAST:event_zRotationSpinnerStateChanged
    private void updateValues() {
        if (composite != null) {
            composite.updateSelectedPart(
                    new Vector3f(Float.valueOf(positionXSpinner.getValue().toString()), Float.valueOf(positionYSpinner.getValue().toString()), Float.valueOf(positionZSpinner.getValue().toString())),
                    getRotation(1),
                    getRotation(-1),
                    // new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString())-previousAngle.x, Float.valueOf(rotationYSpinner.getValue().toString())-previousAngle.y, Float.valueOf(rotationZSpinner.getValue().toString())-previousAngle.z),               
                    new Vector3f(Float.valueOf(sizeXSpinner.getValue().toString()), Float.valueOf(sizeYSpinner.getValue().toString()), Float.valueOf(sizeZSpinner.getValue().toString())));

            listener.getManipulator().shiftManipulators(getPosition());
            // setParameters();
        }
    }

    public Vector3f getScale() {
        return new Vector3f(Float.valueOf(sizeXSpinner.getValue().toString()), Float.valueOf(sizeYSpinner.getValue().toString()), Float.valueOf(sizeZSpinner.getValue().toString()));
    }

    public Vector3f getPosition() {
        return new Vector3f(Float.valueOf(positionXSpinner.getValue().toString()), Float.valueOf(positionYSpinner.getValue().toString()), Float.valueOf(positionZSpinner.getValue().toString()));
    }

    public Quat4f getRotation(int i) {

        Vector3f rotation = new Vector3f(Float.valueOf(rotationXSpinner.getValue().toString()), Float.valueOf(rotationYSpinner.getValue().toString()), Float.valueOf(rotationZSpinner.getValue().toString()));

        Quat4f rotQuat = new Quat4f(0, 0, 0, 1);

        Quat4f xRot = new Quat4f();
        xRot.set(new AxisAngle4f(1f, 0f, 0f, (float) Math.toRadians(rotation.x)));
        rotQuat.mul(xRot, rotQuat);


        Quat4f zRot = new Quat4f();
        zRot.set(new AxisAngle4f(0f, 0f, 1f, i * (float) Math.toRadians(rotation.z)));
        rotQuat.mul(zRot, rotQuat);

        Quat4f yRot = new Quat4f();
        yRot.set(new AxisAngle4f(0f, 1f, 0f, i * (float) Math.toRadians(rotation.y)));
        rotQuat.mul(yRot, rotQuat);

        return rotQuat; //
    }

    public Boolean keepProportions() {
        return keepProportions;
    }

    public Boolean getIsEditing() {
        return isEditing;
    }

    public void setIsEditing(Boolean isEditing) {
        this.isEditing = isEditing;
    }

    public void resetValues() {
        positionXSpinner.setValue(0.f);
        positionYSpinner.setValue(0.f);
        positionZSpinner.setValue(0.f);

        rotationXSpinner.setValue(0.f);
        rotationYSpinner.setValue(0.f);
        rotationZSpinner.setValue(0.f);

        sizeXSpinner.setValue(1.f);
        sizeYSpinner.setValue(1.f);
        sizeZSpinner.setValue(1.f);
    }
    @SuppressWarnings("rawtypes")
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox editComboBox;
    private javax.swing.JLabel editLabel;
    private javax.swing.JLabel facePartLabel;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner positionXSpinner;
    private javax.swing.JSpinner positionYSpinner;
    private javax.swing.JSpinner positionZSpinner;
    private javax.swing.JSpinner rotationXSpinner;
    private javax.swing.JSpinner rotationYSpinner;
    private javax.swing.JSpinner rotationZSpinner;
    private javax.swing.JSpinner sizeXSpinner;
    private javax.swing.JSpinner sizeYSpinner;
    private javax.swing.JSpinner sizeZSpinner;
    // End of variables declaration//GEN-END:variables
    private Composite composite;
    private Boolean keepProportions = true;
    private Boolean isEditing = true;
    private CompositeGLEventListener listener;
}
