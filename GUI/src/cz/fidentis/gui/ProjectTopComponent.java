/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.controller.Controller;
import cz.fidentis.controller.Project;
import cz.fidentis.gui.actions.ButtonHelper;
import cz.fidentis.gui.ageing.AgeingViewerPanel;
import cz.fidentis.gui.composite.CompositePanel;
import cz.fidentis.gui.featurepoints.FeaturePointsPanel;
import cz.fidentis.gui.comparison_two_faces.ViewerPanel_2Faces;
import cz.fidentis.gui.comparison_batch.ViewerPanel_Batch;
import cz.fidentis.gui.comparison_one_to_many.ViewerPanel_1toN;
import gui.fidentis.gui.enums.ProjectType;
import javax.swing.JOptionPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//cz.fidentis.gui//Project//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ProjectTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "cz.fidentis.gui.ProjectTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ProjectAction",
        preferredID = "ProjectTopComponent")
@Messages({
    "CTL_ProjectAction=New Project",
    "CTL_ProjectTopComponent=New Project",
    "HINT_ProjectTopComponent=This is a Project1 window"
})
public final class ProjectTopComponent extends TopComponent {

    private Project project;
    private FeaturePointsPanel featurePointsPanel;
    private CompositePanel compositePanel;
    private ViewerPanel_2Faces viewerPanel;
    private ViewerPanel_Batch batchViewerPanel;
    private StartingPanel startingPanel;
    private ViewerPanel_1toN oneToManyViewerPanel;
    private AgeingViewerPanel ageingViewerPanel;

    public ProjectTopComponent(){
        this(ProjectType.DEFAULT);
    }
    
    public ProjectTopComponent(ProjectType t) {
        initComponents();
        
        createProjectType(t);

        setName(Bundle.CTL_ProjectTopComponent());
        setToolTipText(Bundle.HINT_ProjectTopComponent());
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

        this.setFocusable(true);

        if (Controller.getProjects().isEmpty()) {
            GUIController.setSelectedProjectTopComponent(this);
        }
    }
    
    private void createProjectType(ProjectType t){
        switch(t){
            case AGEING:
               ageingViewerPanel = new AgeingViewerPanel(this);
               break;
            case BATCH:
                batchViewerPanel = new ViewerPanel_Batch(this);
                break;
            case ONE_TO_MANY:
                oneToManyViewerPanel = new ViewerPanel_1toN(this);
                break;
            case PAIR:
                viewerPanel = new ViewerPanel_2Faces(this);
                break;
            case COMPOSITE:
               compositePanel = new CompositePanel(this);
               break;
            default:
                startingPanel = new StartingPanel();
                this.add(startingPanel);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {

    }

    @Override
    public void componentClosed() {
        GUIController.getConfigurationTopComponent().clear();
        Controller.removeProjcet(project);
        GUIController.removeProjectTopComponent(this);
    }

    @Override
    public boolean canClose() {
        int answer = JOptionPane.showConfirmDialog(null,
                "Do you really want to close this window?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    public void triggerAddModel(boolean primary) {
        switch (project.getSelectedPart()) {
            case 2:
                if (primary) {
                    viewerPanel.getCanvas1().triggerAdd();
                } else {
                    viewerPanel.getCanvas2().triggerAdd();
                }
                break;
            case 3:
                if (primary) {
                    oneToManyViewerPanel.getCanvas1().triggerAdd();
                } else {
                    oneToManyViewerPanel.getCanvas2().triggerAdd();
                }
                break;
            case 4:
                batchViewerPanel.getCanvas1().triggerAdd();
                break;
            case 6:
                ageingViewerPanel.getOriginCanvas().triggerAdd();
                GUIController.getConfigurationTopComponent().getAgeingConfiguration().setConfiguration();
                break;
        }
    }

    public void showComposite() {
        if(compositePanel == null)
            createProjectType(ProjectType.COMPOSITE);       //should probably do without switch though
        
        ButtonHelper.setExportEnabled(true);
        project.setSelectedPart(1);
        this.removeAll();
        this.add(compositePanel);

        GUIController.getConfigurationTopComponent().addCompositeComponent();

        this.validate();
        this.repaint();

        compositePanel.resizeCanvas();
    }

    public void showFeaturePoints() {
        if(featurePointsPanel == null)
            featurePointsPanel = new FeaturePointsPanel(this);
        
        ButtonHelper.setExportEnabled(false);
        project.setSelectedPart(4);
        this.removeAll();

        this.add(featurePointsPanel);
        GUIController.getConfigurationTopComponent().addFeaturePointsComponent();

        this.validate();
        this.repaint();
    }

    public void show2FacesViewer() {
        if(viewerPanel == null)
            createProjectType(ProjectType.PAIR);
        
        ButtonHelper.setExportEnabled(false);
        //  project.setSelectedPart(5);
        this.removeAll();

        this.add(viewerPanel);
        viewerPanel.resizeCanvas();
        switch (project.getSelectedComparison2Faces().getState()) {
            case 1:
                GUIController.getConfigurationTopComponent().addRegistrationComponent();
                break;
            case 2:
                GUIController.getConfigurationTopComponent().addComparisonComponent();
                break;
            case 3:
                GUIController.getConfigurationTopComponent().addPairComparisonResults();
                break;

        }

        this.validate();
        this.repaint();
    }

    public void showBatchViewer() {
        if(batchViewerPanel == null)
            createProjectType(ProjectType.BATCH);
        
        ButtonHelper.setExportEnabled(false);
        this.removeAll();

        this.add(batchViewerPanel);
        batchViewerPanel.resizeCanvas();

        switch (project.getSelectedBatchComparison().getState()) {
            case 1:
                GUIController.getConfigurationTopComponent().addBatchRegistrationComponent();
                break;
            case 2:
                GUIController.getConfigurationTopComponent().addBatchComparisonComponent();
                break;
            case 3:
                GUIController.getConfigurationTopComponent().addBatchComparisonResults();
                break;

        }

        this.validate();
        this.repaint();
    }

    public void show1toNViewer() {
        if(oneToManyViewerPanel == null)
            createProjectType(ProjectType.ONE_TO_MANY);
        
        ButtonHelper.setExportEnabled(false);
        this.removeAll();

        this.add(oneToManyViewerPanel);
        oneToManyViewerPanel.resizeCanvas();

        switch (project.getSelectedOneToManyComparison().getState()) {
            case 1:
                GUIController.getConfigurationTopComponent().addOneToManyRegistrationComponent();
                break;
            case 2:
                GUIController.getConfigurationTopComponent().addOneToManyComparisonComponent();
                break;
            case 3:
                GUIController.getConfigurationTopComponent().addOneToManyComparisonResults();

        }

        this.validate();
        this.repaint();
    }
    
    public void showAgeing() {
        if(ageingViewerPanel == null)
            createProjectType(ProjectType.AGEING);
        
        ButtonHelper.setExportEnabled(false);
        this.removeAll();

        this.add(ageingViewerPanel);
        GUIController.getConfigurationTopComponent().addAgeingComponent();

        this.validate();
        this.repaint();
    }

    public void showEmptyView() {
        if(viewerPanel == null)
            createProjectType(ProjectType.PAIR);
        
        this.add(viewerPanel);
        viewerPanel.resizeCanvas();
        GUIController.getConfigurationTopComponent().clear();
        this.validate();
        this.repaint();
    }

    public void showStartingPanel() {
        if(startingPanel == null)
            createProjectType(ProjectType.DEFAULT);
        
        this.add(startingPanel);
        GUIController.getConfigurationTopComponent().clear();
        this.validate();
        this.repaint();
    }

    public StartingPanel getStartingPanel() {
        return startingPanel;
    }

    public void showComponents() {
        if (project != null) {
            switch (project.getSelectedPart()) {
                case 1:
                    showComposite();
                    break;
                case 2:
                    show2FacesViewer();
                    break;
                case 3:
                    show1toNViewer();
                    break;
                case 4:
                    showBatchViewer();
                    break;
                case 6:
                    showAgeing();
                    break;
            }
        } else {
            showStartingPanel();
        }
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public FeaturePointsPanel getFeaturePointsPanel() {
        return featurePointsPanel;
    }

    public void setFeaturePointsPanel(FeaturePointsPanel featurePointsPanel) {
        this.featurePointsPanel = featurePointsPanel;
    }

    public CompositePanel getCompositePanel() {
        return compositePanel;
    }

    public void setCompositePanel(CompositePanel compositePanel) {
        this.compositePanel = compositePanel;
    }

    public ViewerPanel_2Faces getViewerPanel_2Faces() {
        return viewerPanel;
    }

    public ViewerPanel_Batch getViewerPanel_Batch() {
        return batchViewerPanel;
    }

    public ViewerPanel_1toN getOneToManyViewerPanel() {
        return oneToManyViewerPanel;
    }

    public void setViewerPanel(ViewerPanel_2Faces viewerPanel) {
        this.viewerPanel = viewerPanel;
    }
    
    public AgeingViewerPanel getAgeingViewerPanel() {
        return this.ageingViewerPanel;
    }
    
    public void setAgeingViewerPanel(AgeingViewerPanel panel) {
        this.ageingViewerPanel = panel;
    }

    public void clearPanel() {
        this.removeAll();
    }

    public void setTextureRendering(Boolean b) {
        if(compositePanel != null)
            compositePanel.setTextureRendering(b);
        if(featurePointsPanel != null)
            featurePointsPanel.setTextureRendering(b);
        if(viewerPanel != null)
            viewerPanel.setTextureRendering(b);
        if(batchViewerPanel != null)
            batchViewerPanel.setTextureRendering(b);
        if(oneToManyViewerPanel != null)
            oneToManyViewerPanel.setTextureRendering(b);
        if(ageingViewerPanel != null)
            ageingViewerPanel.setTextureRendering(b);
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        if (GUIController.getSelectedProjectTopComponent() != this) {
            GUIController.setSelectedProjectTopComponent(this);
            this.showComponents();
        }
        GUIController.updateSelectedComponent();
        GUIController.getNavigatorTopComponent().clearSelectionIfNeeded(this);
    }
}
