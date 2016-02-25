/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package cz.fidentis.gui.actions;

import cz.fidentis.gui.ConfigurationTopComponent;
import cz.fidentis.gui.NavigatorTopComponent;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.GUIController;
import cz.fidentis.model.Model;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.FeaturePointsActionListener")
@ActionRegistration(
        lazy=false,
        //  iconBase = "cz/fidentis/gui/resources/featurepoints48.png",
        displayName = "#CTL_FeaturePointsActionListener")
@ActionReferences({
    @ActionReference(path = "Menu/Mode", position = 100),
    @ActionReference(path = "Toolbars/Mode", position = 50)
})
@Messages("CTL_FeaturePointsActionListener=Feature Points")

public final class FeaturePointsActionListener extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {

    JToggleButton abc = ButtonHelper.getFeaturePointsButton();
    JMenuItem m = ButtonHelper.getFeaturePointsMenuItem();

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        ButtonHelper.getBg().add(abc);
        abc.setSelected(false);
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/featurepoints48.png")));
        abc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
            }
        });
        return abc;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        //  JMenuItem m = new JMenuItem("Feature Points", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/featurepoints48.png")));        
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();

            }
        });
        return m;
    }

    private void action() {
        if (GUIController.getSelectedProjectTopComponent() != null) {
            if (GUIController.getSelectedProjectTopComponent().getProject().getSelectedFeaturePoints() == null) {
              //  GUIController.getSelectedProjectTopComponent().getProject().addResultContainer();
              //  GUIController.getSelectedProjectTopComponent().getProject().addFeaturePoints();
             /*   if (GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer() != null) {
                    GUIController.getSelectedProjectTopComponent().getFeaturePointsPanel().setReferenceModel(
                            GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer().getModel1());

                }*/
/*                if (GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite() != null) {
                    GUIController.getSelectedProjectTopComponent().getFeaturePointsPanel().setReferenceModel(
                            GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite().getModels());
/*
   }

                GUIController.getNavigatorTopComponent().update();

            }
            GUIController.getSelectedProjectTopComponent().showFeaturePoints();
        }
        abc.setSelected(true);

        /* NavigatorTopComponent navigatorTopComponent = (NavigatorTopComponent) WindowManager.getDefault().findTopComponent("NavigatorTopComponent");
        
         if (navigatorTopComponent.getCurrentProjectTopComponent() != null) {
         navigatorTopComponent.getCurrentProjectTopComponent().showFeaturePoints();
         }*/
        //     ProjectTopComponent projectTopComponent = (ProjectTopComponent) WindowManager.getDefault().findTopComponent("ProjectTopComponent"); 
        //      projectTopComponent.addFeaturePointsComponent();
 /*    }
}
*/