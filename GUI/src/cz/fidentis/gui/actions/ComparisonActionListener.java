/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
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

@ActionID(
    category = "Mode",
id = "cz.fidentis.gui.actions.ComparisonActionListener")
@ActionRegistration(
 lazy=false,   
displayName = "#CTL_ComparisonActionListener")

@ActionReferences({
    @ActionReference(path = "Menu/Mode", position = 60),
    @ActionReference(path = "Toolbars/Mode", position = 60)
})
@Messages("CTL_ComparisonActionListener=Comparison")
public final class ComparisonActionListener extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {

    JToggleButton abc = ButtonHelper.getComparisonButton();
    JMenuItem m = ButtonHelper.getComparisonMenuItem();

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        ButtonHelper.getBg().add(abc);
        abc.setSelected(false);
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/comparison48.png")));
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
        //    JMenuItem m = new JMenuItem("Comparison", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/comparison48.png")));


        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
                abc.setSelected(true);
            }
        });
        return m;
    }

    public void action() {
        if (GUIController.getSelectedProjectTopComponent() != null) {
            if (GUIController.getSelectedProjectTopComponent().getProject().getSelectedComparison() == null) {
               // GUIController.getSelectedProjectTopComponent().getProject().addResultContainer();
               // GUIController.getSelectedProjectTopComponent().getProject().addComparisonContainer();
            }
            //    if( GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer()!=null){
           //          GUIController.getSelectedProjectTopComponent().getComparisonPanel().setReferenceModel(
              //            GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer().getModel1());
           //     }
                 if( GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite()!=null){
                     GUIController.getSelectedProjectTopComponent().getComparisonPanel().setReferenceModel(
                          GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite().getModels());
                }
                GUIController.getNavigatorTopComponent().update();

            

            GUIController.getSelectedProjectTopComponent().showComparison();
        }
         abc.setSelected(true);
        /*       NavigatorTopComponent navigatorTopComponent = (NavigatorTopComponent) WindowManager.getDefault().findTopComponent("NavigatorTopComponent");
         if (navigatorTopComponent.getCurrentProjectTopComponent() != null) {
         navigatorTopComponent.getCurrentProjectTopComponent().showComparison();*/
 /* 
    }
}
 */