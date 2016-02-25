/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*package cz.fidentis.gui.actions;

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
id = "cz.fidentis.gui.actions.AgeingActionListener")
@ActionRegistration(

//    iconBase = "cz/fidentis/gui/resources/ageing48.png",
displayName = "#CTL_AgeingActionListener")
@ActionReferences({
    @ActionReference(path = "Menu/Mode", position = 250),
    @ActionReference(path = "Toolbars/Mode", position = 250)
})
@Messages("CTL_AgeingActionListener=Ageing")

public final class AgeingActionListener extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {
    JToggleButton abc = ButtonHelper.getAgeingButton();
    JMenuItem m = ButtonHelper.getAgeingMenuItem();
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        ButtonHelper.getBg().add(abc);
        abc.setSelected(false);
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/ageing48.png")));
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
       // JMenuItem m = new JMenuItem("Ageing", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/ageing48.png")));      
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
                abc.setSelected(true);
            }
        });
        return m;
    }

    public void action(){
        if (GUIController.getSelectedProjectTopComponent()!=null){
            if(GUIController.getSelectedProjectTopComponent().getProject().getSelectedAgeing()==null){
              GUIController.getSelectedProjectTopComponent().getProject().addResultContainer();  
              GUIController.getSelectedProjectTopComponent().getProject().addAgeingContainer();  
              if( GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer()!=null){
                     GUIController.getSelectedProjectTopComponent().getAgeingPanel().setReferenceModel(
                          GUIController.getSelectedProjectTopComponent().getProject().getSelectedViewer().getModel());
                          
              }
              if( GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite()!=null){
                     GUIController.getSelectedProjectTopComponent().getAgeingPanel().setReferenceModel(
                          GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite().getModels());
                }
              GUIController.getNavigatorTopComponent().update();

            }
            GUIController.getSelectedProjectTopComponent().showAgeing();
          }
         abc.setSelected(true);
        
        
      /*     NavigatorTopComponent navigatorTopComponent = (NavigatorTopComponent) WindowManager.getDefault().findTopComponent("NavigatorTopComponent");
        if (navigatorTopComponent.getCurrentProjectTopComponent() != null) {
            navigatorTopComponent.getCurrentProjectTopComponent().showAgeing();
            * 
}*/
 //   }
//}
