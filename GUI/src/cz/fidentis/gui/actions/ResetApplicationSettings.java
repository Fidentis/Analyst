/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Settings",
        id = "cz.fidentis.gui.actions.ResetApplicationSettings"
)
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_ResetApplicationSettings"
)
@ActionReference(path = "Menu/File", position = 410, separatorAfter = 415)
@Messages("CTL_ResetApplicationSettings=Reset application settings")

public final class ResetApplicationSettings extends AbstractAction implements Presenter.Menu {

     JMenuItem m = ButtonHelper.getResetAppSettingsMenuItem();
     
    @Override
    public void actionPerformed(ActionEvent e) {
 
    }
    
     @Override
    public JMenuItem getMenuPresenter() {
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIController.setDeleteConfigFiles(true);
            }
        });
        return m;
    }
}
