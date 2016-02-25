/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*package cz.fidentis.gui.actions;

import cz.fidentis.controller.Controller;
import cz.fidentis.gui.GUIController;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.CreateComposite")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_CreateComposite")
@ActionReference(path = "Toolbars/Mode", position = 0)
@Messages("CTL_CreateComposite=Create composite")
public final class CreateComposite extends AbstractAction implements Presenter.Toolbar {

    JButton abc = ButtonHelper.getCreateButton();

    public void action() {*/
        /*
        ButtonHelper.setCompositeEnabled(true);
        ButtonHelper.setViewerEnabled(true);
        //    ButtonHelper.setFeaturePointsEnabled(true);
        //   ButtonHelper.setAgeingEnabled(true);
        //   ButtonHelper.setComparisonEnabled(true);
        ButtonHelper.setTexturesEnabled(true);
        ButtonHelper.texturesButton.setSelected(true);
      //  GUIController.selectedProjectTopComponent.getProject().addComposite();
        GUIController.selectedProjectTopComponent.getProject().getSelectedComposite().setName("Composite");
        GUIController.selectedProjectTopComponent.getCompositePanel().setCompositeData(GUIController.selectedProjectTopComponent.getProject().getSelectedComposite());
        GUIController.selectedProjectTopComponent.getCompositePanel().selectTemplates();
        GUIController.selectedProjectTopComponent.getProject().setSelectedPart(1);

        GUIController.selectedProjectTopComponent.setTextureRendering(ButtonHelper.getTexturesButton().isSelected());
        GUIController.getNavigatorTopComponent().update();
        GUIController.selectComposite();*/

    /*}

    @Override
    public Component getToolbarPresenter() {
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/create_composite.png")));
        abc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {*/
              /*  if (GUIController.projects.size() > 0) {
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
                }*/
                /*ButtonHelper.newProjectMenuItem.doClick();
            }
                
        });
        return abc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}*/