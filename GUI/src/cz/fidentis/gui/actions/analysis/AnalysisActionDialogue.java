/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.analysis;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.procrustes.Procrustes2Models;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.landmarks.FPAnalysisMethods;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.LandmarkAnalysisWindow;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author rastoStefanko
 */

public final class AnalysisActionDialogue extends TopComponent {
    
    private List<FpModel> selectedFiles;
    private List<FpModel> selectedFilesSecond;
    private LandmarkAnalysisWindow landmarks;
    private static final String[] FP_EXTENSIONS = new String[]{"pp", "PP", "fp", "FP", "csv", "CSV", "pts", "PTS", "dta", "DTA"};

    public AnalysisActionDialogue() {
        this.landmarks = new LandmarkAnalysisWindow();
        this.selectedFiles = new ArrayList<>();
        this.selectedFilesSecond = new ArrayList<>();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        removeGroundTButton = new javax.swing.JButton();
        addComparisonDataButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        CDlist = new javax.swing.JList<>();
        removeComprButton = new javax.swing.JButton();
        computeComboBox = new javax.swing.JComboBox<>();
        computeButton = new javax.swing.JButton();
        addGroundThruthButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        GTlist = new javax.swing.JList<>();
        methodLabel = new java.awt.Label();
        GTsetsLabel = new java.awt.Label();
        ComparisonDataSetLabel = new java.awt.Label();
        LandmarkAnlaysisLabel = new java.awt.Label();

        setMinimumSize(new java.awt.Dimension(670, 500));

        jPanel1.setPreferredSize(new java.awt.Dimension(647, 505));

        org.openide.awt.Mnemonics.setLocalizedText(removeGroundTButton, org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.removeGroundTButton.text")); // NOI18N
        removeGroundTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeGroundTButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addComparisonDataButton, org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.addComparisonDataButton.text")); // NOI18N
        addComparisonDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComparisonDataButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(CDlist);

        org.openide.awt.Mnemonics.setLocalizedText(removeComprButton, org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.removeComprButton.text")); // NOI18N
        removeComprButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeComprButtonActionPerformed(evt);
            }
        });

        computeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Euclid", "NRMSE" }));
        computeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(computeButton, org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.computeButton.text")); // NOI18N
        computeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addGroundThruthButton, org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.addGroundThruthButton.text")); // NOI18N
        addGroundThruthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroundThruthButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(GTlist);

        methodLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        methodLabel.setText(org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.methodLabel.text")); // NOI18N

        GTsetsLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        GTsetsLabel.setText(org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.GTsetsLabel.text")); // NOI18N

        ComparisonDataSetLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ComparisonDataSetLabel.setText(org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.ComparisonDataSetLabel.text")); // NOI18N

        LandmarkAnlaysisLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        LandmarkAnlaysisLabel.setText(org.openide.util.NbBundle.getMessage(AnalysisActionDialogue.class, "AnalysisActionDialogue.LandmarkAnlaysisLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(LandmarkAnlaysisLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(277, 277, 277)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(computeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(computeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(203, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeGroundTButton)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addGroundThruthButton)
                            .addComponent(GTsetsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeComprButton)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addComparisonDataButton)
                            .addComponent(ComparisonDataSetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(LandmarkAnlaysisLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addGroundThruthButton)
                    .addComponent(addComparisonDataButton))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GTsetsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComparisonDataSetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeComprButton)
                    .addComponent(removeGroundTButton))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(computeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(computeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addGroundThruthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroundThruthButtonActionPerformed

        final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();
        List<FpModel> fpPoints = FPImportExport.instance().importPoints(tc, true);
        
        if(fpPoints != null){
            landmarks.addFilesHandler(fpPoints, selectedFiles, GTlist);
        }
    }//GEN-LAST:event_addGroundThruthButtonActionPerformed
    
    private void removeGroundTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeGroundTButtonActionPerformed
        DefaultListModel model = (DefaultListModel) GTlist.getModel();
        
        if(GTlist.getSelectedIndex() != -1){
            selectedFiles.remove(GTlist.getSelectedIndex());
            model.remove(GTlist.getSelectedIndex());
        }
    }//GEN-LAST:event_removeGroundTButtonActionPerformed

    private void addComparisonDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComparisonDataButtonActionPerformed
        final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();
        List<FpModel> fpPoints = FPImportExport.instance().importPoints(tc, true);
        
        if(fpPoints != null){
            landmarks.addFilesHandler(fpPoints, selectedFilesSecond, CDlist);
        }
    }//GEN-LAST:event_addComparisonDataButtonActionPerformed

    private void removeComprButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeComprButtonActionPerformed
        DefaultListModel model = (DefaultListModel) CDlist.getModel();
        
        if(CDlist.getSelectedIndex() != -1){
            selectedFilesSecond.remove(CDlist.getSelectedIndex());
            model.remove(CDlist.getSelectedIndex());
        }
    }//GEN-LAST:event_removeComprButtonActionPerformed

    private void computeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computeButtonActionPerformed

        FPAnalysisMethods selectedMethod = FPAnalysisMethods.EUCLID;
        
        if(computeComboBox.getSelectedIndex() == 1){
            selectedMethod = FPAnalysisMethods.NRMSE;
        }
        
        //make new window for computation
        final AnalysisResults dialog = new AnalysisResults(selectedFiles, selectedFilesSecond, selectedMethod);
        JFrame frame = new JFrame();
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true); 
    }//GEN-LAST:event_computeButtonActionPerformed

    private void computeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_computeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> CDlist;
    private java.awt.Label ComparisonDataSetLabel;
    private javax.swing.JList<String> GTlist;
    private java.awt.Label GTsetsLabel;
    private java.awt.Label LandmarkAnlaysisLabel;
    private javax.swing.JButton addComparisonDataButton;
    private javax.swing.JButton addGroundThruthButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton computeButton;
    private javax.swing.JComboBox<String> computeComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private java.awt.Label methodLabel;
    private javax.swing.JButton removeComprButton;
    private javax.swing.JButton removeGroundTButton;
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
