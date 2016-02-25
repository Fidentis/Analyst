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
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Mode",
id = "cz.fidentis.gui.actions.SaveAll")
@ActionRegistration(
    displayName = "#CTL_SaveAll")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "DS-S")
})
@Messages("CTL_SaveAll=Save All")
public final class SaveAll implements ActionListener {

    private final Project context;

    public SaveAll (Project context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
