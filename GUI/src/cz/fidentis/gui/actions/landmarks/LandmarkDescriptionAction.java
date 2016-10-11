/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.landmarks;

import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.landmarks.LandmarkDescriptionAction"
)
@ActionRegistration(
        displayName = "#CTL_LandmarkDescriptionAction"
)
@ActionReference(path = "Menu/Landmarks", position = -100)
@Messages("CTL_LandmarkDescriptionAction=Description")
public final class LandmarkDescriptionAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        final LandmarkDescriptionDialogue dialog = new LandmarkDescriptionDialogue();
        JFrame frame = new JFrame();
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true);   
    }
}
