/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import org.openide.util.*;

/**
 *
 * @author Katka
 */
public class ButtonHelper {
    public static  JButton createButton = new JButton();
    public static  JButton importButton = new JButton();
    public static  JToggleButton compositeButton = new JToggleButton();
    public static  JToggleButton viewerButton = new JToggleButton();
    public static  JToggleButton ageingButton = new JToggleButton();
    public static  JToggleButton featurePointsButton = new JToggleButton();
    public static  JToggleButton comparisonButton = new JToggleButton();
    
    public static  JCheckBoxMenuItem texturesMenuItem = new JCheckBoxMenuItem("Textures", false);
    public static  JMenuItem newProjectMenuItem = new JMenuItem("New Project...");  
    public static  JMenuItem exportMenuItem = new JMenuItem("Export...");     
    public static  JMenuItem ResetAppSettingsMenuItem = new JMenuItem("Reset application settings");   
    public static  JMenuItem exitMenuItem = new JMenuItem("Exit");   
    public static  JMenuItem compositeMenuItem = new JMenuItem("Composite", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/composite20.png")));      
    public static JMenuItem viewerMenuItem = new JMenuItem("Viewer", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/viewer20.png")));
    public static JMenuItem ageingMenuItem = new JMenuItem("Ageing", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/ageing20.png")));      
    public static JMenuItem comparisonMenuItem = new JMenuItem("Comparison", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/comparison20.png")));        
    public static JMenuItem featurePointsMenuItem = new JMenuItem("Feature Points", new ImageIcon(ImageUtilities.loadImage("cz/fidentis/gui/resources/featurepoints20.png")));             
    public static ButtonGroup bg = new ButtonGroup();

    
    
    public static ButtonGroup getBg() {
        return bg;
    }
        
    public static void selectComposite(){
        compositeButton.setSelected(true);
    }   
    
    public static void selectComparison(){
        comparisonButton.setSelected(true);
    } 
    public static void selectAgeing(){
        ageingButton.setSelected(true);
    } 
    public static void selectFeaturePoints(){
        featurePointsButton.setSelected(true);
    } 
    public static void selectViewer(){
        viewerButton.setSelected(true);
    } 

    public static JButton getCreateButton() {
        createButton.setToolTipText("New project");
        return createButton;
    }

    public static JButton getImportButton() {
        importButton.setToolTipText("Import model");
        return importButton;
    }
    
    
    
    
    public static JToggleButton getCompositeButton() {
        compositeButton.setToolTipText("Composite mode");
        return compositeButton;
    }

    public static JToggleButton getViewerButton() {
        viewerButton.setToolTipText("Viewer mode");
        return viewerButton;
    }

    public static JToggleButton getAgeingButton() {
        ageingButton.setToolTipText("Ageing mode");
        return ageingButton;
    }

    public static JToggleButton getFeaturePointsButton() {

        featurePointsButton.setToolTipText("Feature Points mode");
        return featurePointsButton;
    }

    public static JToggleButton getComparisonButton() {
        comparisonButton.setToolTipText("Comparison mode");
        return comparisonButton;
    }

    public static JCheckBoxMenuItem getTexturesMenuItem() {
        texturesMenuItem.setToolTipText("Turn textures on/off");
        return texturesMenuItem;
    }
    
    
     public static void setTexturesEnabled(Boolean b){
        texturesMenuItem.setEnabled(b);
    }

    public static void setViewerEnabled(Boolean b){
        viewerButton.setEnabled(b);
        viewerMenuItem.setEnabled(b);
    }
    
    public static void setCompositeEnabled(Boolean b){
        compositeButton.setEnabled(b);
        compositeMenuItem.setEnabled(b);
    }
    
    public static void setComparisonEnabled(Boolean b){
        comparisonButton.setEnabled(b);
        comparisonMenuItem.setEnabled(b);
    }
    
    public static void setAgeingEnabled(Boolean b){
        ageingButton.setEnabled(b);
        ageingMenuItem.setEnabled(b);
    }
    
    public static void setFeaturePointsEnabled(Boolean b){
        featurePointsButton.setEnabled(b);
        featurePointsMenuItem.setEnabled(b);
    }
    
    
    public static JMenuItem getCompositeMenuItem() {
        return compositeMenuItem;
    }

    public static JMenuItem getViewerMenuItem() {
        return viewerMenuItem;
    }

    public static JMenuItem getAgeingMenuItem() {
        return ageingMenuItem;
    }

    public static JMenuItem getComparisonMenuItem() {
        return comparisonMenuItem;
    }

    public static JMenuItem getFeaturePointsMenuItem() {
        return featurePointsMenuItem;
    }

    public static JMenuItem getExportMenuItem() {
        return exportMenuItem;
    }

    public static void setExportEnabled(Boolean b){
        exportMenuItem.setEnabled(b);
    }

    public static JMenuItem getNewProjectMenuItem() {
        return newProjectMenuItem;
    }

    public static JMenuItem getResetAppSettingsMenuItem() {
        return ResetAppSettingsMenuItem;
    }

    public static JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }
    
    
}
