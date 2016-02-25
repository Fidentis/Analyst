/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import cz.fidentis.model.ModelExporter;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
    category = "Mode",
id = "cz.fidentis.gui.actions.Export")
@ActionRegistration(
 lazy=false,       
displayName = "#CTL_Export")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 375, separatorAfter = 400),
    @ActionReference(path = "Shortcuts", name = "D-E")
})
@Messages("CTL_Export=Export...")
public final class Export extends AbstractAction implements Presenter.Menu {

    JMenuItem m = ButtonHelper.getExportMenuItem();

    @Override
    public void actionPerformed(ActionEvent ev) {
    }

    public Boolean validate(ExportPanel ex) {
        String path = ex.getFilePath();
        String name = ex.getFileName();

        if (name.length() <= 0) {
            return false;
        } else if (!new File(path).exists()) {
            return false;
        } else {
            return true;
        }
    }

    public void exportModel(ExportPanel ex) {
        String fileName;
        ModelExporter exporter = new ModelExporter(GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite().getModels());
        String filePath = ex.getFilePath();
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        filePath = filePath + ex.getFileName();

        if (filePath.lastIndexOf(".") > -1) {
            String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
            if (suffix.toLowerCase().equals("obj") || suffix.toLowerCase().equals("stl")|| suffix.toLowerCase().equals("ply")) {
                fileName = filePath.substring(0, filePath.lastIndexOf("."));
            } else {
                fileName = filePath;
            }
        } else {
            fileName = filePath;
        }
        if (ex.isSTLSelected()) {
            fileName = fileName + ".stl";
            File f = new File(fileName);
            exporter.exportModelToStl(f, true);
        } else if (ex.isOBJSelected()){
            fileName = fileName + ".obj";
            File f = new File(fileName);
            exporter.exportModelToObj(f, ex.isTextureExportSelected());
        }else{
            fileName = fileName + ".ply";
            File f = new File(fileName);
            exporter.exportModelToPLY(f);
        }

    }

    @Override
    public JMenuItem getMenuPresenter() {
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ExportPanel ex = new ExportPanel();
                ArrayList<String> options = new ArrayList<String>();
                ex.setExportOptions(options);

                JButton okButton = new JButton("OK");

                final DialogDescriptor dd = new DialogDescriptor(
                        ex,
                        "Export",
                        true,
                        new Object[]{okButton,
                            DialogDescriptor.CANCEL_OPTION},
                        DialogDescriptor.CANCEL_OPTION,
                        DialogDescriptor.DEFAULT_ALIGN,
                        null,
                        null);

                Object[] cl = {DialogDescriptor.CANCEL_OPTION};
                dd.setClosingOptions(cl);

                final NotificationLineSupport supp = dd.createNotificationLineSupport();
                final Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);

                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (validate(ex)) {
                            exportModel(ex);
                            supp.setErrorMessage("");
                            dlg.setVisible(false);

                        } else {
                            // DialogDisplayer.getDefault().notify(dd);
                            supp.setErrorMessage("Invalid path or file name.");
                        }
                    }
                });

                dlg.setVisible(true);

            }
        });
        return m;
    }
}
