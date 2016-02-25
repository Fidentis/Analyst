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
public class NewProjectWizardPanel1 implements WizardDescriptor.ValidatingPanel  {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NewProjectVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public NewProjectVisualPanel1 getComponent() {
        if (component == null) {
            component = new NewProjectVisualPanel1();
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


    
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty("composite_from_file", getComponent().getLoadRadioButton().isSelected());
        wiz.putProperty("model_path", getComponent().getModelLocationField().getText());
    }

    @Override
    public void validate() throws WizardValidationException {
        File f = new File(component.getModelLocationField().getText());
        if (component.getLoadRadioButton().isSelected() && !(f.exists() && f.isFile())){
             throw new WizardValidationException(null, "Invalid File", null);        
        }
        String suffix = f.getPath().substring(f.getPath().lastIndexOf(".") + 1);
        if (component.getLoadRadioButton().isSelected() && !(suffix.toLowerCase().equals("obj") || suffix.toLowerCase().equals("stl"))){
             throw new WizardValidationException(null, "File is not *.stl or *.obj model", null);        
        }

    }

    @Override
    public void readSettings(Object data) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void storeSettings(Object data) {
        ((WizardDescriptor) data).putProperty("composite_from_file", getComponent().getLoadRadioButton().isSelected());
        ((WizardDescriptor) data).putProperty("model_path", getComponent().getModelLocationField().getText());
    }
}
