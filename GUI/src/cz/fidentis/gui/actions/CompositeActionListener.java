/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import java.awt.Component;
import java.awt.ComponentOrientation;
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
id = "cz.fidentis.gui.actions.CompositeActionListener")
@ActionRegistration(
lazy=false,
 // iconBase = "cz/fidentis/gui/resources/composite48.png",
displayName = "#CTL_CompositeActionListener")
@ActionReferences({
    @ActionReference(path = "Menu/Mode", position = 0),
    @ActionReference(path = "Toolbars/Mode", position = 30)
})
@Messages("CTL_CompositeActionListener=Composite")
public final class CompositeActionListener extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {
    JToggleButton abc = ButtonHelper.getCompositeButton();
    JMenuItem m = ButtonHelper.getCompositeMenuItem();
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        ButtonHelper.getBg().add(abc);
        abc.setSelected(false);
        abc.setAlignmentX(JToggleButton.LEFT_ALIGNMENT);
        abc.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/composite48.png")));
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
       // JMenuItem m = new JMenuItem("Composite", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/composite48.png")));      
        
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
                
            }
        });
        return m;
    }
        public void action(){        
            if (GUIController.getSelectedProjectTopComponent()!=null){
              GUIController.getSelectedProjectTopComponent().showComposite();
          }
          abc.setSelected(true);
    }
}
 */