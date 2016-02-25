/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.gui.GUIController;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
    category = "Mode",
id = "cz.fidentis.gui.actions.Textures")
@ActionRegistration(
lazy=false,  
displayName = "#CTL_Textures")
@ActionReferences({
    @ActionReference(path = "Menu/Options"),
    @ActionReference(path = "Shortcuts", name = "D-T")
})
@Messages("CTL_Textures=Textures")
public final class Textures extends AbstractAction implements Presenter.Menu {
    JCheckBoxMenuItem abc = ButtonHelper.getTexturesMenuItem();
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getMenuPresenter() {
        //abc.setIcon(new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/texture.png")));
        abc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             action();
                
        }
            
        });
        return abc;
    }



    public void action(){
        System.out.println(GUIController.getProjects().size());
        for (int i = 0; i< GUIController.getProjects().size(); i++){
            GUIController.getProjects().get(i).setTextureRendering(abc.isSelected());
        }
        
    }
}

