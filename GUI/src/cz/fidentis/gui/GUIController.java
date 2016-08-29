/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui;

import cz.fidentis.controller.Controller;
import cz.fidentis.gui.actions.ButtonHelper;
import cz.fidentis.gui.actions.newprojectwizard.ModelFileFilter;
import cz.fidentis.gui.actions.newprojectwizard.NewProjectVisualPanel1;
import cz.fidentis.gui.composite.CompositeConfiguration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;


/**
 *
 * @author Katka
 */
@SuppressWarnings("rawtypes")
@ServiceProvider(service = StatusLineElementProvider.class)
public class GUIController implements StatusLineElementProvider{

    private static ArrayList<ProjectTopComponent> projects = new ArrayList<ProjectTopComponent>();
    private static ProjectTopComponent selectedProjectTopComponent;
    private static NavigatorTopComponent navigator = null;
    private static String path;
    private static final JFileChooser jFileChooser1 = new JFileChooser();
    private static ConfigurationTopComponent configurationTopComponent;
    private static boolean deleteConfigFiles = true;
    
    private static JPanel buttonPanel;
    private static  JButton pauseButton;
    


    public static boolean isDeleteConfigFiles() {
        return deleteConfigFiles;
    }

    public static void setDeleteConfigFiles(boolean deleteConfigFiles) {
        GUIController.deleteConfigFiles = deleteConfigFiles;
    }
    
    
    public static String getPath() {
        return path;
    }


    @SuppressWarnings("LeakingThisInConstructor")
    public GUIController() {
        jFileChooser1.setApproveButtonText(org.openide.util.NbBundle.getMessage(NewProjectVisualPanel1.class, "NewProjectVisualPanel1.jFileChooser1.approveButtonText")); // NOI18N
        String[] extensions = new String[6];
        extensions[0] = "obj";
        extensions[1] = "stl";
        extensions[2] = "ply";
        extensions[3] = "OBJ";
        extensions[4] = "STL";
        extensions[5] = "PLY";
        ModelFileFilter filter = new ModelFileFilter(extensions, "*.obj,*.stl,*.ply");
        jFileChooser1.addChoosableFileFilter(filter);
        jFileChooser1.setFileFilter(filter);
        extensions = new String[2];
        extensions[0] = "obj";
        extensions[1] = "OBJ";
        filter = new ModelFileFilter(extensions, "*.obj");
        jFileChooser1.addChoosableFileFilter(filter);
        extensions = new String[2];
        extensions[0] = "stl";
        extensions[1] = "STL";
        filter = new ModelFileFilter(extensions, "*.stl");
        jFileChooser1.addChoosableFileFilter(filter);
        extensions = new String[2];
        extensions[0] = "ply";
        extensions[1] = "PLY";
        filter = new ModelFileFilter(extensions, "*.ply");
        jFileChooser1.addChoosableFileFilter(filter);
        
        
        jFileChooser1.setSelectedFile(new File(System.getProperty("user.home")));
        
        ButtonHelper.setExportEnabled(false);
        try {
            path = new java.io.File(".").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }    
        
        
        buttonPanel = new JPanel(new BorderLayout());
        pauseButton = new JButton("Pause");
        buttonPanel.add(pauseButton, BorderLayout.CENTER);
        buttonPanel.setVisible(false);
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProjectTopComponent tc = new ProjectTopComponent();
                tc.setName("New Project");
                tc.setDisplayName("New Project");
                GUIController.addProjectTopComponent(tc);
                tc.open();
            }
        });
    }
    
    
    @Override
    public Component getStatusLineElement() {
        return buttonPanel;
    }
    
    public static void setPauseButtonVisible(Boolean visible){
        //buttonPanel.setVisible(visible);
        buttonPanel.setVisible(false);
    }
    
    
    
//    public static void openStartingScreen(){
//        ProjectTopComponent tc = new ProjectTopComponent();
//        tc.setName("New Project");
//       // tc.showStartingPanel();
//        tc.add(tc.getStartingPanel());
//        tc.validate();
//        tc.repaint();
//        GUIController.addProjectTopComponent(tc);
//
//        tc.open();
//        tc.requestActive();
//    }
    

    public static void addProjectTopComponent(ProjectTopComponent component) {
        projects.add(component);
    }
    


    public static JFileChooser getjFileChooser1() {
        return jFileChooser1;
    }

    public static void removeProjectTopComponent(ProjectTopComponent component) {       
        int i = projects.lastIndexOf(component);
        projects.remove(component);
        getNavigatorTopComponent().update();
    }

    public static ProjectTopComponent getSelectedProjectTopComponent() {
        return selectedProjectTopComponent;
    }

    public static void setSelectedProjectTopComponent(ProjectTopComponent selectedProjectTopComponent) {
        GUIController.selectedProjectTopComponent = selectedProjectTopComponent;
        if (selectedProjectTopComponent.getCompositePanel() != null) {
            ButtonHelper.setExportEnabled(true);
        } else {
            ButtonHelper.setExportEnabled(false);

        }
    }

    public static NavigatorTopComponent getNavigatorTopComponent() {
        if(navigator == null) {
            navigator = (NavigatorTopComponent) WindowManager.getDefault().findTopComponent("NavigatorTopComponent");
        }
        return navigator;

    }

    public static ConfigurationTopComponent getConfigurationTopComponent() {
        if(configurationTopComponent==null){
        configurationTopComponent = (ConfigurationTopComponent) WindowManager.getDefault().findTopComponent("ConfigurationTopComponent");
        
        }
        return configurationTopComponent;

    }

    public static List<ProjectTopComponent> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public static void selectFeaturePoints() {
        selectedProjectTopComponent.clearPanel();
        selectedProjectTopComponent.showFeaturePoints();
        ButtonHelper.selectFeaturePoints();
    }


    public static void selectComposite() {
        selectedProjectTopComponent.clearPanel();
        selectedProjectTopComponent.showComposite();
        ButtonHelper.selectComposite();

    }

    public static void select2FacesViewer() {
        selectedProjectTopComponent.clearPanel();
        selectedProjectTopComponent.show2FacesViewer();
        ButtonHelper.selectViewer();
    }
    
    public static void selectBatchViewer() {
        selectedProjectTopComponent.clearPanel();
        selectedProjectTopComponent.showBatchViewer();
        ButtonHelper.selectViewer();
    }
    
     public static void selectOneToManyViewer() {
        selectedProjectTopComponent.clearPanel();
        selectedProjectTopComponent.show1toNViewer();
        ButtonHelper.selectViewer();
    }
     
     public static void selectAgeing() {
         selectedProjectTopComponent.clearPanel();
         selectedProjectTopComponent.showAgeing();
         ButtonHelper.selectAgeing();
     }

    public static void updateSelectedComponent() {
        if (selectedProjectTopComponent.getProject() != null) {
            if (selectedProjectTopComponent.getProject().getSelectedComposite() == null) {
                ButtonHelper.setCompositeEnabled(false);
                ButtonHelper.setViewerEnabled(true);
                ButtonHelper.setFeaturePointsEnabled(true);
                ButtonHelper.setComparisonEnabled(true);
            } else {
                ButtonHelper.setCompositeEnabled(true);
                ButtonHelper.setViewerEnabled(true);
                ButtonHelper.setFeaturePointsEnabled(false);
                ButtonHelper.setComparisonEnabled(false);
            }

            int i = selectedProjectTopComponent.getProject().getSelectedPart();
            if (i == 3) {
                ButtonHelper.selectAgeing();
            } else if (i == 4) {
                ButtonHelper.selectFeaturePoints();
            } else if (i == 2) {
                ButtonHelper.selectComparison();

            } else if (i == 1) {
                ButtonHelper.selectComposite();
            } else if (i == 5) {
                ButtonHelper.selectViewer();
            } else if (i == 6) {
                ButtonHelper.selectAgeing();
            } else {
                selectedProjectTopComponent.showEmptyView();
                ButtonHelper.setCompositeEnabled(false);
                ButtonHelper.setViewerEnabled(false);
                ButtonHelper.setFeaturePointsEnabled(false);
                ButtonHelper.setAgeingEnabled(false);
                ButtonHelper.setComparisonEnabled(false);
                ButtonHelper.setTexturesEnabled(false);
            }
        }
    }
    
    /**
     * Returns ProjectTopComponent that will be used to create a new project. If
     * no such is present in current top components it is created.
     * @return top component with no project set so far
     */
    public static ProjectTopComponent getBlankProject() {
        for(ProjectTopComponent tc : projects) {
            if(tc.getProject() == null) {
                return tc;
            }
        }
        
        ProjectTopComponent ntc = new ProjectTopComponent();
        ntc.setName("New Project");
        ntc.setDisplayName("New Project");
        GUIController.addProjectTopComponent(ntc);
        ntc.showStartingPanel();
        ntc.open();
        return ntc;
    }
    
    public static void updateNavigator() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUIController.getNavigatorTopComponent().update();
            }
        });
    }
    
    //debug only
    public static void setPath(String path){
        GUIController.path = path;
    }
}
