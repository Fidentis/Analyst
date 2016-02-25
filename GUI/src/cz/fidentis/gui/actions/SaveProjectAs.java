/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.controller.Project;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Mode",
id = "cz.fidentis.gui.actions.SaveProjectAs")
@ActionRegistration(
    displayName = "#CTL_SaveProjectAs")
@ActionReference(path = "Menu/File", position = 225)
@Messages("CTL_SaveProjectAs=Save Project As...")
public final class SaveProjectAs implements ActionListener {
private final Project context;

    public SaveProjectAs (Project context) {
        this.context = context;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
