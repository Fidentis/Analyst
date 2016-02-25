/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package cz.fidentis.gui.actions;

import cz.fidentis.gui.NavigatorTopComponent;
import cz.fidentis.gui.GUIController;
import java.awt.Component;
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
    id = "cz.fidentis.gui.actions.Viewer")
@ActionRegistration(
   lazy=false,
//   iconBase = "cz/fidentis/gui/resources/viewer48.png",
    displayName = "#CTL_Viewer")
    @ActionReferences({
        @ActionReference(path = "Menu/Mode", position = 40),
        @ActionReference(path = "Toolbars/Mode", position = 40)
})
@Messages("CTL_Viewer=Viewer")
public final class ViewerActionListener extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {

    JToggleButton abc =  ButtonHelper.getViewerButton();
    JMenuItem m = ButtonHelper.getViewerMenuItem();
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        ButtonHelper.getBg().add(abc);
        abc.setSelected(false);
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/viewer48.png")));
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
      //  JMenuItem m = new JMenuItem("Viewer", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/viewer48.png")));
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
               
            }
        });
        return m;
    }
    
    private void action(){
        if (GUIController.getSelectedProjectTopComponent()!=null){
              if( GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite()!=null){
                     //GUIController.getSelectedProjectTopComponent().getViewerPanel_2Faces().setReferenceModel(
                     //GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite().getModels());
                }
              GUIController.getNavigatorTopComponent().update();
              GUIController.getSelectedProjectTopComponent().show2FacesViewer();
          }
         abc.setSelected(true);

    }
}
 */