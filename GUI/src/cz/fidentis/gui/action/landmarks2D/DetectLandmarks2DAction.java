/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.action.landmarks2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.action.landmarks2D.detectLandmarks2DAction"
)
@ActionRegistration(
        displayName = "#CTL_detectLandmarks2DAction"
)
@ActionReference(path = "Menu/Landmarks/2D", position = -200)
@Messages("CTL_detectLandmarks2DAction=Detect Landmarks...")
public final class DetectLandmarks2DAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Detect2DLandmarksWindow dialog = new Detect2DLandmarksWindow();
        JFrame frame = new JFrame();
             
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true);  
    }
}
