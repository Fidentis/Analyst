/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.newprojectwizard;

import java.io.File;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

@SuppressWarnings("rawtypes")
public class NewProjectWizardPanel2 implements WizardDescriptor.ValidatingPanel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NewProjectVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public NewProjectVisualPanel2 getComponent() {
        if (component == null) {
            component = new NewProjectVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }



    @Override
    public void validate() throws WizardValidationException {
      String name = component.getNameField().getText();
        String loc = component.getLocationField().getText();
        if (name.trim().equals("")){
             throw new WizardValidationException(null, "Invalid Name", null);
        }
      /*    
         File f = new File(loc);
        if (loc.trim().equals("") || !f.exists()){
             throw new WizardValidationException(null, "Invalid Location, no such directory exists", null);
        }
        
     
        f = new File(loc + File.separator + name);
        if (f.exists() && f.isDirectory()){
             throw new WizardValidationException(null, "Project directory already exists", null);
        
        }*/
    }

    @Override
    public void readSettings(Object data) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void storeSettings(Object data) {
        ((WizardDescriptor)data).putProperty("name", getComponent().getNameField().getText());
        ((WizardDescriptor)data).putProperty("project_path", getComponent().getLocationField().getText());
    }
}
