/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.controller.Project;
import cz.fidentis.gui.GUIController;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lollkosk
 */
public class ResolveMissingModels extends javax.swing.JDialog {
    private List<File> files;
    private Map<File, File> result;
    private static final String STR_REMOVE = "removed";
    private static final String STR_NOT_RESOLVED = "not resolved";

    /**
     * Creates new form ResolveMissingModels
     */
    public ResolveMissingModels(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                but_find.setEnabled(true);
                but_ignore.setEnabled(true);
            }
        });
    }
    
    public void setFiles(List<File> files) {
        this.files = files;
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        for(File f : files) {
            if(!f.isFile()) {
                model.addRow(new Object[]{f, STR_NOT_RESOLVED});
            }
        }
    }
    
    public List<File> getFiles() {
        return this.files;
    }
    
    public Map<File, File> getResult() {
        return this.result;
    }
    
    private void checkResolvedAll() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        boolean allResolved = true;
        for(int i=0;i<model.getRowCount();i++) {
            allResolved = allResolved && !model.getValueAt(i, 1).equals(STR_NOT_RESOLVED);
        }
        but_ok.setEnabled(allResolved);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        but_find = new javax.swing.JButton();
        but_ignore = new javax.swing.JButton();
        but_ok = new javax.swing.JButton();
        but_cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Missing file", "Resolved by"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.table.columnModel.title0_1")); // NOI18N
            table.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.table.columnModel.title1_1")); // NOI18N
        }

        org.openide.awt.Mnemonics.setLocalizedText(but_find, org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.but_find.text")); // NOI18N
        but_find.setEnabled(false);
        but_find.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                but_findActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(but_ignore, org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.but_ignore.text")); // NOI18N
        but_ignore.setEnabled(false);
        but_ignore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                but_ignoreActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(but_ok, org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.but_ok.text")); // NOI18N
        but_ok.setEnabled(false);
        but_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                but_okActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(but_cancel, org.openide.util.NbBundle.getMessage(ResolveMissingModels.class, "ResolveMissingModels.but_cancel.text")); // NOI18N
        but_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                but_cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(but_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(but_ok))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(but_find)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(but_ignore)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(but_find)
                    .addComponent(but_ignore))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(but_ok)
                    .addComponent(but_cancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void but_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_but_cancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_but_cancelActionPerformed

    private void but_ignoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_but_ignoreActionPerformed
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setValueAt(STR_REMOVE, table.getSelectedRow(), 1);
        checkResolvedAll();
    }//GEN-LAST:event_but_ignoreActionPerformed

    private void but_findActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_but_findActionPerformed
        JFileChooser chooser = GUIController.getjFileChooser1();
        chooser.setMultiSelectionEnabled(false);
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            model.setValueAt(chooser.getSelectedFile().getAbsolutePath(), table.getSelectedRow(), 1);
            checkResolvedAll();
        }
    }//GEN-LAST:event_but_findActionPerformed

    private void but_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_but_okActionPerformed
        this.result = new HashMap<File, File>();
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        for(int i=0;i<model.getRowCount();i++) {
            if(!model.getValueAt(i, 1).equals(STR_REMOVE)) {
                result.put((File)model.getValueAt(i, 0), new File((String)model.getValueAt(i, 1)));
            }
        }
        this.setVisible(false);
    }//GEN-LAST:event_but_okActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton but_cancel;
    private javax.swing.JButton but_find;
    private javax.swing.JButton but_ignore;
    private javax.swing.JButton but_ok;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
