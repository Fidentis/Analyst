/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Zuzana Ferkova
 */
public class DialogUtils {
    private static DialogUtils instance;
    private static String lastPath = System.getProperty("user.home");
    
    private DialogUtils(){}
    
    public static DialogUtils instance(){
       if(instance == null){
           instance = new DialogUtils();
       }
       
       return instance;
    }
    
    /**
     * Opens dialog with given component, get the path to which to save file, ensures that user
     * wants to overwrite existing files if necessary, and that user really wants to save
     * the files and returns to path to which to save files. Only allows to save CSV files.
     * 
     * If user decided to cancel the choice, returns null.
     * 
     * @param tc - componenet through which the dialog is opened
     * @param filterName - name of the filter to be shown in opened dialog (like 'CSV files')
     * @param filterShortcut - format abreviation for given format (like '.csv')
     * @param createDir - whether to create directory or just folders
     * @return path to save file, or null if no path was chosen.
     */
    public String openDialogueSaveFile(Component tc, String filterName, String[] filterShortcut, boolean createDir){
        JFileChooser chooser;
        
        if(createDir){
            chooser = createChooserForDir();
        }else{
            chooser = createChooserForFile();
        }

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                filterName, filterShortcut);
        
        chooser.setFileFilter(filter);
        
        if(createDir){
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }else{
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(new File(lastPath));
        
        int returnVal = chooser.showSaveDialog(tc);

        if(returnVal == JFileChooser.APPROVE_OPTION)  {
            lastPath = chooser.getSelectedFile().getPath();
            return lastPath;
        }    
        
        return null;
    }
    
    //creates chooser for directory
    private JFileChooser createChooserForDir(){
        JFileChooser chooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (!f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "The folder doesn't exists. Create?", "Create folder",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            f.mkdir();
                            super.approveSelection();
                            return;
                        default:
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        
        return chooser;
    }
    
    //creates chooser for folder
    private JFileChooser createChooserForFile(){
        JFileChooser chooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "The file exists. Overwrite?", "Existing file",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                        default:
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        
        return chooser;
    }
    
    /**
     * Creates dialog which will allow user to choose whether to rewrite single file,
     * rewrite all files, don't rewrite the file or cancel the operation.
     * 
     * @param fileName - name of the file to be rewritten.
     * @return choice the user made. 0 - Yes, 1 - Yes to All, 2 - No, 3 - Cancel
     */
    public int rewriteFile(String fileName){
        String[] buttons = {"Yes", "Yes to all", "No", "Cancel"};
                    int result = JOptionPane.showOptionDialog(null,
                            "Do you really want to overwrite file \"" + fileName + "\"?",
                            "Confirmation",
                            JOptionPane.WARNING_MESSAGE,
                            0,
                            null,
                            buttons,
                            buttons[2]);
                    
        return result;
    }
    
    /**
     * Opens dialogue to load files from the disk.
     * 
     * @param tc - componenet through which the dialog is opened
     * @param filterName - name of the filter to be shown in opened dialog (like 'CSV files')
     * @param filterShortcut - format abreviation for given format (like '.csv')
     * @param selectMultiple - whether to enable choice of multiple files.
     * @return array of File containing information about loaded models.
     */
    public File[] openDialogueLoadFiles(Component tc, String filterName, String[] filterShortcut, boolean selectMultiple){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                filterName, filterShortcut);
        File[] loadedFiles;
        int result;
        
        chooser.setMultiSelectionEnabled(selectMultiple);       
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setSelectedFile(new File(lastPath));
        
        result = chooser.showOpenDialog(tc);
        if(result == JFileChooser.APPROVE_OPTION){
            if(selectMultiple){
                loadedFiles = chooser.getSelectedFiles();
            }else{
                loadedFiles = new File[1];
                loadedFiles[0] = chooser.getSelectedFile();
            }
            
            lastPath = chooser.getSelectedFile().getPath();
            
            return loadedFiles;
        }
        
        //if user didn't choose to pick any files
        return null;
    }
    
}
