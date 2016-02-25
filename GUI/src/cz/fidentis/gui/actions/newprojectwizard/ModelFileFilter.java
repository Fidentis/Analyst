/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.newprojectwizard;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Katka
 */
public class ModelFileFilter extends FileFilter {
    
    private String[] extension;
    private String description;
    
    public ModelFileFilter(String[] extension, String description)
    {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f)
    {
        boolean accepted = false;
        for (int i=0; i<extension.length;i++) {
             if(f.isDirectory() || f.getName().endsWith(extension[i])) {
                accepted = true;
            }
        }
        return accepted;
    }

    /*
     * @see javax.swing.filechooser.FileFilter
     */

    @Override
    public String getDescription() {
        return description;
    }



}
