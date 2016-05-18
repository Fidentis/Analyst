/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import java.io.File;
import java.util.ArrayList;


/**
 *
 * @author Katarína Furmanová
 */
public class Project {

    private ProjectTree tree;
    private int index;
    private String name = new String();
    private String location = new String();
    private int selectedPart;
  //  private ArrayList<Object> hierarchy = new ArrayList<Object>();
    private Composite selectedComposite;
    private Comparison2Faces selectedComparison2Faces;
    private BatchComparison selectedBatchComparison;
    private OneToManyComparison selectedOneToManyComparison;
    
    private Comparison selectedComparison;
    private Ageing selectedAgeing;
    private FeaturePoints selectedFeaturePoints;
    private Viewer selectedViewer;
    
    private File tempDirectory;

    
        /**
     * Adds project name to the top of hierarchy.
     */
    public Project(String name) {
        tree = new ProjectTree(name);
        this.name = name;
    }
    
    public void addAgeing(String name) {
        Ageing ageing = new Ageing();
        ageing.setName(name);
        selectedAgeing = ageing;
        ageing.setNode(tree.getRoot().addChild(ageing));
    }

    public void addComposite(String name){
        Composite composite = new Composite();
        composite.setName(name);
        selectedComposite = composite;
        composite.setNode(tree.getRoot().addChild(composite));
    }
    
    public ArrayList<String> getCompositeNames(){
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i< tree.getRoot().getChildren().size();i++){
            if(tree.getRoot().getChildren().get(i).getData() instanceof Composite){
                names.add(((Composite)tree.getRoot().getChildren().get(i).getData()).getName());
            }
        }
        return names;
    }
    
    public void add2FacesComparison(String name){
        Comparison2Faces comparison = new Comparison2Faces();
        comparison.setName(name);
        selectedComparison2Faces = comparison;
        comparison.setNode(tree.getRoot().addChild(comparison));
    }

    public Comparison2Faces getSelectedComparison2Faces() {
        return selectedComparison2Faces;
    }

    public void setSelectedComparison2Faces(Comparison2Faces selectedComparison2Faces) {
        this.selectedComparison2Faces = selectedComparison2Faces;
    }
    
    public void addOneToManyComparison(String name){
        OneToManyComparison comparison = new OneToManyComparison();
        comparison.setName(name);
        selectedOneToManyComparison = comparison;
        comparison.setNode(tree.getRoot().addChild(comparison));
    }

    public OneToManyComparison getSelectedOneToManyComparison() {
        return selectedOneToManyComparison;
    }

    public void setSelectedOneToManyComparison(OneToManyComparison selectedOneToManyComparison) {
        this.selectedOneToManyComparison = selectedOneToManyComparison;
    }
    
    public void addBatchComparison(String name){
        BatchComparison comparison = new BatchComparison();
        comparison.setName(name);
        selectedBatchComparison = comparison;
        comparison.setNode(tree.getRoot().addChild(comparison));
    }

    public BatchComparison getSelectedBatchComparison() {
        return selectedBatchComparison;
    }

    public void setSelectedBatchComparison(BatchComparison selectedBatchComparison) {
        this.selectedBatchComparison = selectedBatchComparison;
    }
    
    
    public ProjectTree getTree(){
        return tree;
    }

    
    /**
     *
     * @return selected View.
     */
//    public Viewer getSelectedViewer() {
//        return selectedViewer;
//    }

    /**
     * Set selected view.
     *
     * @param selectedviewer view to be selected.
     */
    public void setSelectedViewer(Viewer selectedviewer) {
        this.selectedViewer = selectedviewer;
    }

    /**
     *
     * @return selected Composite.
     */
    public Composite getSelectedComposite() {
        return selectedComposite;
    }

    /**
     *
     * @param selectedComposite composite to be selected.
     */
    public void setSelectedComposite(Composite selectedComposite) {
        this.selectedComposite = selectedComposite;
    }

    /**
     *
     * @return selected Comparison.
     */
    public Comparison getSelectedComparison() {
        return selectedComparison;
    }

    /**
     *
     * @param selectedComparison comparison to be selected.
     */
    public void setSelectedComparison(Comparison selectedComparison) {
        this.selectedComparison = selectedComparison;
    }

    /**
     *
     * @return selected Ageing.
     */
    public Ageing getSelectedAgeing() {
        return selectedAgeing;
    }

    /**
     *
     * @param selectedAgeing Ageing to be selected.
     */
    public void setSelectedAgeing(Ageing selectedAgeing) {
        this.selectedAgeing = selectedAgeing;
    }

    /**
     *
     * @return selected Feature Points.
     */
    public FeaturePoints getSelectedFeaturePoints() {
        return selectedFeaturePoints;
    }

    /**
     *
     * @param selectedFeaturePoints Feature points to be selected.
     */
    public void setSelectedFeaturePoints(FeaturePoints selectedFeaturePoints) {
        this.selectedFeaturePoints = selectedFeaturePoints;
    }

 
    /**
     *
     * @return 1 - composite, 2 - 2facescomparison, 3 - one to many comparison,
     * 4 - batch comparison, 6 - ageing
     */
    public int getSelectedPart() {
        return selectedPart;
    }

    /**
     *
     * @param SelectedPart 1 - composite, 2 - comparison, 3 - ageing, 4 -
     * feature points, 5 - viewer, 6 - ageing
     */
    public void setSelectedPart(int SelectedPart) {
        this.selectedPart = SelectedPart;
    }

    /**
     *
     * @return Location of the Project.
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location location of the Project.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return Name of the project.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name of the project.
     */
    public void setName(String name) {
        //hierarchy.remove(0);
        this.name = name;
        tree.getRoot().setData(name);
      //  hierarchy.add(0, this);

    }

    /**
     * Gets the temporary directory that *SHOULD* be used to store any temporary
     * files that are related to this project.
     * @return 
     */
    public File getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Sets the temporary directory that *SHOULD* be used to store any temporary
     * files that are related to this project.
     * @param tempDirectory 
     * @throws IllegalArgumentException if passed tempDirectory is not an existing
     * readable directory.
     */
    public void setTempDirectory(File tempDirectory) {
        if(tempDirectory == null || !tempDirectory.isDirectory()) {
            throw new IllegalArgumentException("TempDirectory must be an existing directory.");
        }
        this.tempDirectory = tempDirectory;
    }

    /**
     *
     * @return index of project.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set index of project.
     *
     * @param index index of project.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return name;
    }
}
