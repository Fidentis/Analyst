/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.newprojectwizard;

import cz.fidentis.controller.Controller;
import cz.fidentis.controller.Project;
import cz.fidentis.gui.ConfigurationTopComponent;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.NavigatorTopComponent;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.ButtonHelper;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.newprojectwizard.NewProjectWizardAction")
@ActionRegistration(
        // iconBase = "cz/fidentis/gui/resources/ageing48.png",
        lazy = false,
        displayName = "New project...")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 0),
    @ActionReference(path = "Shortcuts", name = "D-N")
})
@NbBundle.Messages("Open NewProject Wizard=New File")
public final class NewProjectWizardAction extends AbstractAction implements Presenter.Menu {

    JMenuItem m = ButtonHelper.getNewProjectMenuItem();

    @Override
    public void actionPerformed(ActionEvent e) {
        action();
    }

    @Override
    public JMenuItem getMenuPresenter() {
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
            }
        });
        return m;
    }

    @SuppressWarnings("unchecked")
    private void action() {

        List<WizardDescriptor.Panel<WizardDescriptor>> panels;
        panels = new ArrayList<>();

        panels.add((WizardDescriptor.Panel<WizardDescriptor>) new NewProjectWizardPanel2());
        //   panels.add(new NewProjectWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("cz/fidentis/gui/resources/wizardbanner.png", true));

        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("New Project");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            Project project = new Project((String) wiz.getProperty("name"));

            ProjectTopComponent projectTopComponent = GUIController.getBlankProject();

            project.setName((String) wiz.getProperty("name"));
            project.setLocation((String) wiz.getProperty("project_path"));

          //  projectTopComponent.showEmptyView();

            /*     if((Boolean) wiz.getProperty("composite_from_file")) {
             ButtonHelper.setViewerEnabled(true);
             ButtonHelper.setCompositeEnabled(false);
             project.addViewer();
             project.getSelectedViewer().setModelPath((String) wiz.getProperty("model_path"));
             project.getSelectedViewer().setName("Model");
             projectTopComponent.getViewerPanel_2Faces().setViewerData(project.getSelectedViewer());
             project.setSelectedPart(5);
             }
             else {
             ButtonHelper.setCompositeEnabled(true);
             // ButtonHelper.setViewerEnabled(false);
             project.addComposite();
             project.getSelectedComposite().setName("Composite");
             projectTopComponent.getCompositePanel().setCompositeData(project.getSelectedComposite());
             projectTopComponent.getCompositePanel().selectTemplates();
             project.setSelectedPart(1);
             }
             */
            projectTopComponent.setProject(project);

            projectTopComponent.setDisplayName(project.getName());
            projectTopComponent.setToolTipText("This is a " + project.getName() + " window");

            projectTopComponent.setName(String.valueOf(Controller.getProjects().size()));
            project.setIndex(Controller.getProjects().size());

            projectTopComponent.setTextureRendering(ButtonHelper.getTexturesMenuItem().isSelected());

            Controller.addProjcet(project);
            GUIController.updateNavigator();

            projectTopComponent.open();
            projectTopComponent.requestActive();
            projectTopComponent.showStartingPanel();
            //  projectTopComponent.showComponents();
            //  projectTopComponent.validate();
            //  projectTopComponent.repaint();

            ButtonHelper.setCompositeEnabled(false);
            ButtonHelper.setFeaturePointsEnabled(false);
            ButtonHelper.setAgeingEnabled(false);
            ButtonHelper.setComparisonEnabled(false);
            ButtonHelper.setTexturesEnabled(false);
            ButtonHelper.setViewerEnabled(false);
           
            
            ProjectTopComponent ntc = new ProjectTopComponent();
            ntc.setName("New Project");
            ntc.showStartingPanel();
            GUIController.addProjectTopComponent(ntc);

            ntc.open();
            
            GUIController.setSelectedProjectTopComponent(projectTopComponent);

        }
    }
}
