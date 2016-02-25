/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.gui.Colorchooser;
import cz.fidentis.gui.GUIController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.Background"
)
@ActionRegistration(
        displayName = "#CTL_Background"
)
@ActionReference(path = "Menu/Options", position = 3333)
@Messages("CTL_Background=Choose background...")
public final class Background implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
    /*    JDialog d = new JDialog();
        d.setTitle("Background color");
        d.setModal(true);
        d.setLocationRelativeTo(GUIController.getSelectedProjectTopComponent());
        Colorchooser c = new Colorchooser();
        d.get
        d.add(c);
        d.setVisible(true);*/
        
      
            Colorchooser c = new Colorchooser();
             
            c.setLocationRelativeTo(GUIController.getSelectedProjectTopComponent());
            c.setTitle("Background color");
            c.setModal(true);
            c.setVisible(true);
    }
}
