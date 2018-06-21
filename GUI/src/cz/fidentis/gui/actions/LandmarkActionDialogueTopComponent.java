/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.landmarks.PDMList;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import cz.fidentis.processing.featurePoints.LandmarkLocalization;
import cz.fidentis.processing.featurePoints.PDM;
import cz.fidentis.processing.featurePoints.TrainingModel;
import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

public final class LandmarkActionDialogueTopComponent extends TopComponent {

    public LandmarkActionDialogueTopComponent() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        trainingModelLabel = new java.awt.Label();
        actualModelLabel = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        infoLabel = new java.awt.Label();
        loadFromButton = new java.awt.Label();
        loadButton = new javax.swing.JButton();
        trainNewLabel = new java.awt.Label();
        trainButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        loadButton1 = new javax.swing.JButton();
        loadFromButton1 = new java.awt.Label();
        trainingModelLabel1 = new java.awt.Label();
        trainingModelLabel2 = new java.awt.Label();
        pdmsComboBox = new javax.swing.JComboBox<>();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setMinimumSize(new java.awt.Dimension(650, 300));
        setPreferredSize(new java.awt.Dimension(650, 500));

        trainingModelLabel.setAlignment(java.awt.Label.CENTER);
        trainingModelLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trainingModelLabel.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainingModelLabel.text")); // NOI18N

        actualModelLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        actualModelLabel.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.actualModelLabel.text")); // NOI18N

        infoTextArea.setEditable(false);
        infoTextArea.setColumns(20);
        infoTextArea.setRows(5);
        jScrollPane1.setViewportView(infoTextArea);

        infoLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        infoLabel.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.infoLabel.text")); // NOI18N

        loadFromButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        loadFromButton.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.loadFromButton.text")); // NOI18N

        loadButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(loadButton, org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.loadButton.text")); // NOI18N
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        trainNewLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        trainNewLabel.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainNewLabel.text")); // NOI18N

        trainButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(trainButton, org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainButton.text")); // NOI18N
        trainButton.setActionCommand(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainButton.actionCommand")); // NOI18N
        trainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        loadButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(loadButton1, org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.loadButton1.text")); // NOI18N
        loadButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButton1ActionPerformed(evt);
            }
        });

        loadFromButton1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        loadFromButton1.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.loadFromButton1.text")); // NOI18N

        trainingModelLabel1.setAlignment(java.awt.Label.CENTER);
        trainingModelLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trainingModelLabel1.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainingModelLabel1.text")); // NOI18N

        trainingModelLabel2.setAlignment(java.awt.Label.CENTER);
        trainingModelLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trainingModelLabel2.setText(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainingModelLabel2.text")); // NOI18N

        pdmsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "default" }));
        pdmsComboBox.setSelectedItem("default");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trainingModelLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trainingModelLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trainingModelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(actualModelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(66, 66, 66))
                        .addComponent(loadFromButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pdmsComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadFromButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trainNewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trainButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(103, 103, 103))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(trainingModelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trainingModelLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(trainingModelLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(loadFromButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(actualModelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(pdmsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(9, 9, 9)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(trainNewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(trainButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(loadFromButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(loadButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        trainButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainButton.AccessibleContext.accessibleName")); // NOI18N
        loadFromButton1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.loadFromButton1.AccessibleContext.accessibleName")); // NOI18N
        trainingModelLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainingModelLabel1.AccessibleContext.accessibleName")); // NOI18N
        trainingModelLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LandmarkActionDialogueTopComponent.class, "LandmarkActionDialogueTopComponent.trainingModelLabel2.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButton1ActionPerformed
        final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();

        PDM selectedPdm = PDMList.instance().getPdm(pdmsComboBox.getSelectedIndex());
        
        FPImportExport.instance().exportPDM(tc, selectedPdm);
    }//GEN-LAST:event_loadButton1ActionPerformed

    private void trainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainButtonActionPerformed
        final LandmarkTrainDialogueTopComponent dialog = new LandmarkTrainDialogueTopComponent(this);
        JFrame frame = new JFrame();
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true);
    }//GEN-LAST:event_trainButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();
        PDM pdm = FPImportExport.instance().importPDM(tc);

        if(pdm == null || pdm.getMeanShape().getFacialPoints().isEmpty())
        return;

        StringBuilder infoText = new StringBuilder();
        infoText.append("Model name: ").append(pdm.getModelName()).append("\n");
        infoText.append("Points number: ").append(pdm.getMeanShape().getPointsNumber());
        infoTextArea.setText(infoText.toString());

        boolean added = PDMList.instance().addPdm(pdm);
        if(added)
            addItemToPDMBox(pdm.getModelName());
        
        
    }//GEN-LAST:event_loadButtonActionPerformed
    protected void addItemToPDMBox(String item){
        pdmsComboBox.addItem(item);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label actualModelLabel;
    private java.awt.Label infoLabel;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton loadButton1;
    private java.awt.Label loadFromButton;
    private java.awt.Label loadFromButton1;
    private javax.swing.JComboBox<String> pdmsComboBox;
    private javax.swing.JButton trainButton;
    private java.awt.Label trainNewLabel;
    private java.awt.Label trainingModelLabel;
    private java.awt.Label trainingModelLabel1;
    private java.awt.Label trainingModelLabel2;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }
}
