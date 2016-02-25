/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*

package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.actions.newprojectwizard.ModelFileFilter;
import cz.fidentis.gui.actions.newprojectwizard.NewProjectVisualPanel1;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.ImportComposite")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_ImportComposite")
@ActionReference(path = "Toolbars/Mode", position = 10)
@Messages("CTL_ImportComposite=Import composite")
public final class ImportComposite extends AbstractAction implements Presenter.Toolbar {

    JButton abc = ButtonHelper.getImportButton();

    public void action() {

        final JFileChooser jFileChooser1 = new JFileChooser();
        jFileChooser1.setApproveButtonText(org.openide.util.NbBundle.getMessage(NewProjectVisualPanel1.class, "NewProjectVisualPanel1.jFileChooser1.approveButtonText")); // NOI18N
        String[] extensions = new String[4];
        extensions[0] = "obj";
        extensions[1] = "stl";
        extensions[2] = "OBJ";
        extensions[3] = "STL";
        ModelFileFilter filter = new ModelFileFilter(extensions, "*.obj and *.stl");
        jFileChooser1.setFileFilter(filter);
        jFileChooser1.setSelectedFile(new File(System.getProperty("user.home")));

        int result = jFileChooser1.showOpenDialog(GUIController.selectedProjectTopComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
           // GUIController.selectedProjectTopComponent.getProject().addViewer();
    //        GUIController.selectedProjectTopComponent.getProject().getSelectedViewer().setName("Model");
            GUIController.select2FacesViewer();

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    System.out.println("loading progress");
                    ProgressHandle p;
                    p = ProgressHandleFactory.createHandle("Loading...");
                    p.start();
                    p.switchToIndeterminate();
                    ModelLoader loader = new ModelLoader();
                        Model model = loader.loadModel(new File(jFileChooser1.getSelectedFile().getPath()),true);
                        model.centralize();
                  //      GUIController.selectedProjectTopComponent.getProject().getSelectedViewer().setModel1(model);
                  // GUIController.selectedProjectTopComponent.getProject().getSelectedViewer().setModel2(model);
                   
                 //   GUIController.selectedProjectTopComponent.getViewerPanel_2Faces().setViewerData(GUIController.selectedProjectTopComponent.getProject().getSelectedViewer());
                    GUIController.selectedProjectTopComponent.getProject().setSelectedPart(5);

                    p.finish();
                    System.out.println("loading progress end");
                }
            };

            Thread t = new Thread(run);
            t.start(); // start the task and progress visualisation 

            ButtonHelper.setCompositeEnabled(false);
            ButtonHelper.setViewerEnabled(true);
            ButtonHelper.setFeaturePointsEnabled(true);
            ButtonHelper.setAgeingEnabled(true);
            ButtonHelper.setComparisonEnabled(true);
            ButtonHelper.setTexturesEnabled(true);
            ButtonHelper.texturesButton.setSelected(true);
            GUIController.selectedProjectTopComponent.setTextureRendering(ButtonHelper.getTexturesButton().isSelected());
            GUIController.getNavigatorTopComponent().update();
        }

    }

    @Override
    public Component getToolbarPresenter() {
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/import48.png")));
        abc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUIController.projects.size() > 0) {
                    if (GUIController.selectedProjectTopComponent.getProject() != null) {
                        int i = GUIController.selectedProjectTopComponent.getProject().getSelectedPart();
                        if (i < 1 || i > 5) {
                            action();
                        }
                    } else {
                        int j = GUIController.projects.size();
                        ButtonHelper.newProjectMenuItem.doClick();
                        if (GUIController.projects.size() > j) {
                            action();
                        }
                    }
                } else {
                    int j = GUIController.projects.size();
                    ButtonHelper.newProjectMenuItem.doClick();
                    if (GUIController.projects.size() > j) {
                        action();
                    }
                }
            }
        });
        return abc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // action();
    }
}
 */