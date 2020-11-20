/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.results.FpResultsBatch;
import cz.fidentis.featurepoints.results.FpResultsOneToMany;
import cz.fidentis.featurepoints.results.FpResultsPair;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpProcessing {

    private static FpProcessing instance;

    
    public static FpProcessing instance() {
        if (instance == null) {
            instance = new FpProcessing();
        }

        return instance;
    }

    private FpProcessing() {
    }

    /**
     * Computes Feature Points for two faces.
     *
     * @param cancelTask - to be able to cancel current thread
     * @param mainModel - main model of pair mode
     * @param secondaryModel - secondary model of pair mode
     * @param registerButton - button to allow registration step
     * @param exportFpButton - button to export computed FP to file
     * @param calculateAutoButton - button to calculate FPs automatically
     * @return FpResultsPair class containing computed Fps for main and
     * secondary face
     */
    public FpResultsPair calculatePointsPair(Cancellable cancelTask, Model mainModel, Model secondaryModel,
            JButton registerButton, JButton exportFpButton, JButton calculateAutoButton, PDM pdm, Component mainComponent) {
        List<FacialPoint> mainFP = new ArrayList<>();
        List<FacialPoint> secondaryFP = new ArrayList<>();
        FpResultsPair res = null;

        ProgressHandle p;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...", cancelTask);
        p.start();
        Icp.instance().setP(p);
        
        List<File> usedModels = new LinkedList<>();
        usedModels.add(mainModel.getFile());
        usedModels.add(secondaryModel.getFile());
        
        HashMap<String, List<FacialPoint>> allFPs = computePointsForMany(p, usedModels, pdm);
        
        if(allFPs != null) {
            res = new FpResultsPair(allFPs.get(mainModel.getName()), allFPs.get(secondaryModel.getName()));
        } else {
            showNoCNNMessage(mainComponent);
            //compute FPs for main face
        if (computePointsForSingleFace(p, mainModel, mainFP, registerButton, exportFpButton, calculateAutoButton, mainFP, secondaryFP, pdm)) {
            return res;
        }

        //compute FPs for secondary face
        if (computePointsForSingleFace(p, secondaryModel, secondaryFP, registerButton, exportFpButton, calculateAutoButton, mainFP, secondaryFP, pdm)) {
            return res;
        }

        res = new FpResultsPair(mainFP, secondaryFP);
        }
    
        finish(registerButton, exportFpButton, calculateAutoButton, p,
                mainFP, secondaryFP);

        return res;
    }

    //computes points for single face, returns false if all computations were performed correctly, true otherwise
    private boolean computePointsForSingleFace(ProgressHandle p, Model model, List<FacialPoint> computedPoints,
            JButton registerButton, JButton exportFpButton, JButton calculateAutoButton,
            List<FacialPoint> mainFP, List<FacialPoint> secondaryFP, PDM pdm) {
       
        p.progress("Computing feature points of face " + model.getName(), 100);
        p.switchToIndeterminate();
        
        computedPoints.addAll(computePointsForSingleFace(p, model, pdm));

        if (checkThreadInteruption(registerButton, exportFpButton, calculateAutoButton, p, mainFP, secondaryFP)) {
            return true;
        }
        return false;
    }

    //check if thread was interrupted, if so, set up buttons appropriately
    private boolean checkThreadInteruption(JButton registerButton, JButton exportFpButton, JButton calculateAutoButton, ProgressHandle p, List<FacialPoint> mainFP, List<FacialPoint> secondaryFP) {
        if (Thread.currentThread().isInterrupted()) {
            finish(registerButton, exportFpButton, calculateAutoButton, p,
                    mainFP, secondaryFP);
            return true;
        }
        return false;
    }

    //check if FPs were calculated if task was interrupted, return false if there were not, true otherwise
    private Boolean areFPCalculated(List<FacialPoint> mainFP, List<FacialPoint> secondaryFP) {
        if (mainFP == null || mainFP.isEmpty()
                || secondaryFP == null || secondaryFP.isEmpty()) {
            return false;
        }
        return true;
    }


    //set up buttons appropriatelly, if all FPs were computed, enable export and registre button
    private void finish(JButton registerButton, JButton exportFpButton, JButton calculateAutoButton, ProgressHandle p,
            List<FacialPoint> mainFP, List<FacialPoint> secondaryFP) {
        p.finish();
        registerButton.setEnabled(areFPCalculated(mainFP, secondaryFP));
        exportFpButton.setEnabled(areFPCalculated(mainFP, secondaryFP));
        calculateAutoButton.setEnabled(true);
    }


    /**
     * Computes and returns feature points for all loaded faces in 1:N mode.
     * Faces are reverted to original position after computation of points is
     * finished (since ICP is used to align faces to frankfurt position in
     * process).
     *
     * @param models - list of URLs pointing to where models are stored on the
     * disk
     * @param mainF - model of main face in 1:N mode
     * @return feature points for N models and main face, along with list of
     * registered models
     */
    public FpResultsOneToMany calculatePointsOneToMany(List<File> models, Model mainF, PDM pdm, Component mainComponent) {
        ProgressHandle p;
        FpResultsOneToMany results = null;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...");
        p.start();

        try {

            p.setDisplayName("Computing Feature Points...");

            Model model;
            List<FacialPoint> facialPoints;
            Map<String, List<FacialPoint>> allFPs = null;   
            List<File> usedModels = new LinkedList<>();
            usedModels.addAll(models);
            usedModels.add(mainF.getFile());

            allFPs = computePointsForMany(p, usedModels, pdm);

            if (allFPs == null) {   
                showNoCNNMessage(mainComponent);
                
                 allFPs = new HashMap<>();
                // Compute on mesh
                for (int i = 0; i < models.size(); i++) {
                    model = ModelLoader.instance().loadModel(models.get(i), true, true);

                    facialPoints = computePointsForSingleFace(p, model, pdm);
                    allFPs.put(model.getName(), facialPoints);
                }

                facialPoints = computePointsForSingleFace(p, mainF, pdm);
                allFPs.put(mainF.getName(), facialPoints);

            }

            p.finish();
            results = new FpResultsOneToMany((HashMap<String, List<FacialPoint>>) allFPs);

        } catch (Exception ex) {
            p.finish();
        }
        
        return results;
    }

    //computes points for single face
    private List<FacialPoint> computePointsForSingleFace(ProgressHandle p, Model model, PDM pdm) {

        p.progress("Computing feature points of face " + model.getName());
        p.switchToIndeterminate();

        LandmarkLocalization localization = LandmarkLocalization.instance();

        return localization.localizationOfLandmarks(model, pdm); 
    }
    
    private HashMap<String, List<FacialPoint>> computePointsForMany(ProgressHandle p, List<File> models, PDM pdm) {
         p.progress("Computing feature points for faces,");
        p.switchToIndeterminate();

        LandmarkLocalization localization = LandmarkLocalization.instance();

        return localization.landmarkDetectionTexture(models, pdm);
    }

    /**
     * Computes and returns feature points for all loaded faces in Batch mode.
     * Faces are reverted to original position after computation of points is
     * finished (since ICP is used to align faces to frankfurt position in
     * process).
     *
     * @param cancelTask - to be able to cancel current thread
     * @param models - list of URLs pointing to where models are stored on the
     * disk
     * @return feature points for N models, along with list of registered models
     */
    public FpResultsBatch calculatePointsBatch(Cancellable cancelTask, List<File> models, PDM pdm, Component mainComponent) {
        int size = models.size();

        ProgressHandle p;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...", cancelTask);
        p.start();

        Model model;
        List<FacialPoint> facialPoints;
        Map<String, List<FacialPoint>> allFPs = null;
        FpResultsBatch res = null;
        
        allFPs = computePointsForMany(p, models, pdm);
        
        if(allFPs == null){
            showNoCNNMessage(mainComponent);
           allFPs = new HashMap<>();
          for (int i = 0; i < size; i++) {
            if (Thread.currentThread().isInterrupted()) {
                p.finish();
                return res;
            }
            model = ModelLoader.instance().loadModel(models.get(i), true, true);

            facialPoints = computePointsForSingleFace(p, model, pdm);
            allFPs.put(model.getName(), facialPoints);

         }
        }

        res = new FpResultsBatch(allFPs);
        p.finish();
        return res;
    }
   
    
    private void showNoCNNMessage(Component parent) {
        JOptionPane.showMessageDialog(parent, 
                "Programme couldn't run Python, landmarks will be computed without use of texture.", "Couldn't run Python", 
                JOptionPane.INFORMATION_MESSAGE);
       
    }
}
