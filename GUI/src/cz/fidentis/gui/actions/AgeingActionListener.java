/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.controller.Controller;
import cz.fidentis.controller.Project;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utilsException.FileManipulationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.AgeingActionListener")
@ActionRegistration(
        displayName = "#CTL_AgeingActionListener")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 75, separatorAfter = 100),
    @ActionReference(path = "Shortcuts", name = "D-A")
})
@NbBundle.Messages("CTL_AgeingActionListener=New Ageing")
public final class AgeingActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectTopComponent tc = GUIController.getBlankProject();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Project project = new Project("Project " + dateFormat.format(date));
        project.setName("Project " + dateFormat.format(date));
        try {
            project.setTempDirectory(FileUtils.instance()
                    .createTMPmoduleFolder(String.valueOf(System.currentTimeMillis())));
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }

        tc.setProject(project);
        tc.setDisplayName(project.getName());
        tc.setToolTipText("This is a " + project.getName() + " window");
        tc.setName(String.valueOf(Controller.getProjects().size()));
        project.setIndex(Controller.getProjects().size());
        tc.setTextureRendering(ButtonHelper.getTexturesMenuItem().isSelected());
        Controller.addProjcet(project);

        GUIController.getBlankProject(); // adds another "New Project" panel

        GUIController.setSelectedProjectTopComponent(tc);
        
        ButtonHelper.setTexturesEnabled(true);
        tc.getProject().addAgeing(NbBundle.getMessage(Controller.class, "tree.node.ageing"));
        tc.getProject().setSelectedPart(6);
        GUIController.selectAgeing();
        GUIController.getNavigatorTopComponent().update();
        tc.requestActive();
    }

}
