/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.Exit")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_Exit")
@ActionReference(path = "Menu/File", position = 420)
@Messages("CTL_Exit=Exit")
public final class Exit extends AbstractAction implements Presenter.Menu {

    JMenuItem m = ButtonHelper.getExitMenuItem();

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public JMenuItem getMenuPresenter() {
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LifecycleManager.getDefault().exit();
            }
        });
        return m;
    }
}
