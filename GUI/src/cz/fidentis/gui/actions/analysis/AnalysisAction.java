/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Rasto1
 */

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.analysis.AnalysisAction"
)
@ActionRegistration(
        displayName = "#CTL_AnalysisAction"
)
@ActionReference(path = "Menu/Analysis", position = -100)
@NbBundle.Messages("CTL_AnalysisAction=Landmark analysis")
public class AnalysisAction implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final AnalysisActionDialogue dialog = new AnalysisActionDialogue();
        JFrame frame = new JFrame();
        frame.add(dialog);
        frame.setSize(dialog.getPreferredSize());
        frame.setVisible(true);   
    }
}
