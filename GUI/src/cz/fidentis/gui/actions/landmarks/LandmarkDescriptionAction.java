/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.landmarks;

import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.utils.DialogUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt){
                //Make sure to notify user if they try to close window without saving landmarks
                if(dialog.hasUnsavedChanges()){
                    int result = DialogUtils.instance().createMessageDialog(new String[]{"Yes", "No"},
                                                1, dialog, 
                                                "Changes to landmark description were not saved. Do you want to save before closing the window?", 
                                                "Unsaved changes.", JOptionPane.INFORMATION_MESSAGE);
                    
                    if(result == 0){
                    dialog.saveCurrentLandmarks();
                    }
                }
                
                
            }
        });
        
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true);  
        
        
    }
}
