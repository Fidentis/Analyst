/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.icp.KdTree;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.featurepoints.FpDetector;
import cz.fidentis.featurepoints.results.FpResultsBatch;
import cz.fidentis.featurepoints.results.FpResultsOneToMany;
import cz.fidentis.featurepoints.results.FpResultsPair;
import cz.fidentis.featurepoints.symmetryplane.MirroredModel;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import cz.fidentis.utils.MeshUtils;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.vecmath.Vector3f;
import jv.object.PsDebug;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;

/**
 *
 * @author Zuzana Ferkova
 */
public class FpProcessing {

    private static FpProcessing instance;
    private static KdTree mainF;

    public static FpProcessing instance() {
        if (instance == null) {
            instance = new FpProcessing();
        }

        return instance;
    }

    private FpProcessing() {
        String genericFacePath = "";
        try {
            genericFacePath = new java.io.File(".").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (!genericFacePath.equals("")) {
            genericFacePath = genericFacePath + separatorChar + "models" + separatorChar + "resources" + separatorChar + "average_face.obj";
            Model genericFace = ModelLoader.instance().loadModel(new File(genericFacePath), false, true);
            mainF = new KdTreeIndexed(genericFace.getVerts());
        }
    }

    //register face to generic face in frankfurt position, returns transformations performed during ICP process
    public List<ICPTransformation> faceRegistration(Model compareFace) {
        if (mainF == null) {
            //error
            return null;
        }

        List<ICPTransformation> trans = Icp.instance().icp(mainF, compareFace.getVerts(), compareFace.getVerts(), 0.f, 20, true);
        return trans;

    }

    //computes center of the given face and its mirror
    private ArrayList<Vector3f> getCenterOfFace(Model model, boolean registrate) {
        Model mirroredModel = MeshUtils.instance().getMirroredModel(model);

        if (registrate) {
            //KdTree mainF = new KdTreeIndexed(model.getVerts());
            Icp.instance().icp(mainF, mirroredModel.getVerts(), mirroredModel.getVerts(), 0.f, 20, true);
        }

        return MirroredModel.getCenterPoints(model, mirroredModel);
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
            JButton registerButton, JButton exportFpButton, JButton calculateAutoButton) {
        List<FacialPoint> mainFP = new ArrayList<>();
        List<FacialPoint> secondaryFP = new ArrayList<>();
        FpResultsPair res = null;

        ProgressHandle p;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...", cancelTask);
        p.start();
        Icp.instance().setP(p);

        //compute FPs for main face
        if (computePointsForSingleFace(p, mainModel, mainFP, registerButton, exportFpButton, calculateAutoButton, mainFP, secondaryFP, 1)) {
            return res;
        }

        //compute FPs for secondary face
        if (computePointsForSingleFace(p, secondaryModel, secondaryFP, registerButton, exportFpButton, calculateAutoButton, mainFP, secondaryFP, 2)) {
            return res;
        }

        res = new FpResultsPair(mainFP, secondaryFP);
        finish(registerButton, exportFpButton, calculateAutoButton, p,
                mainFP, secondaryFP);

        return res;
    }

    //computes points for single face, returns false if all computations were performed correctly, true otherwise
    private boolean computePointsForSingleFace(ProgressHandle p, Model model, List<FacialPoint> computedPoints,
            JButton registerButton, JButton exportFpButton, JButton calculateAutoButton,
            List<FacialPoint> mainFP, List<FacialPoint> secondaryFP, int faceNumber) {
        List<ICPTransformation> trans;
        ArrayList<Vector3f> centerPoints;

        p.switchToDeterminate(100);
        p.progress("Registering model" + faceNumber + " to generic face", 0);

        //register face to generic model
        trans = faceRegistration(model);
        if (checkThreadInteruption(registerButton, exportFpButton, calculateAutoButton, p, mainFP, secondaryFP)) {      //if thread was interrupted by user during computation finish here
            return true;
        }
        p.switchToDeterminate(100);
        p.progress("Getting center of face " + faceNumber, 0);

        //compute center of the face
        centerPoints = getCenterOfFace(model, true);
        if (checkThreadInteruption(registerButton, exportFpButton, calculateAutoButton, p, mainFP, secondaryFP)) {
            return true;
        }

        p.progress("Computing feature points of face " + faceNumber, 100);
        p.switchToIndeterminate();

        //compute all facial points
        computedPoints.addAll(computeAllFacialPoints(centerPoints, model, trans));
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

    //checks if FPs were calculated for 1:N mode
    private Boolean areFPCalculated(Map<String, List<FacialPoint>> allFPs, List<FacialPoint> mainFp, List<File> models) {
        for (File f : models) {
            List<FacialPoint> fp = allFPs.get(f.getName());            //??
            if (fp == null || fp.isEmpty()) {
                return false;
            }
        }

        return !(mainFp == null || mainFp.isEmpty());
    }

    //set up buttons appropriatelly, if all FPs were computed, enable export and registre button
    private void finish(JButton registerButton, JButton exportFpButton, JButton calculateAutoButton, ProgressHandle p,
            List<FacialPoint> mainFP, List<FacialPoint> secondaryFP) {
        p.finish();
        registerButton.setEnabled(areFPCalculated(mainFP, secondaryFP));
        exportFpButton.setEnabled(areFPCalculated(mainFP, secondaryFP));
        calculateAutoButton.setEnabled(true);
    }

    //computes all facial points that software is currently capable of computing and reverts ICP transformations performed during FP computation
    private List<FacialPoint> computeAllFacialPoints(ArrayList<Vector3f> centerPoints, Model m, List<ICPTransformation> transformations) {
        //computation moved to FpDetector class
//        FeaturePointsUniverse fpUniverse = new FeaturePointsUniverse(m);
//        setUpJavaViewConsole();
//        facialPoints = computePointsFromFpUniverse(fpUniverse, centerPoints);        

        FpDetector fpDetector = new FpDetector(m);
        List<FacialPoint> facialPoints = fpDetector.computeAllFPs(centerPoints);

        revertPerformedTransformations(facialPoints, m, transformations);

        return facialPoints;
    }

    private void revertPerformedTransformations(List<FacialPoint> facialPoints, Model m, List<ICPTransformation> transformations) {
        //revert transformations computed during computing FP
        FpModel model = FPImportExport.instance().getFpModelFromFP(facialPoints,
                m.getName());

        if (model != null) {          //if there was no problem with creating FP mode

            //apply reverse ICP transformation to computed FP
            List<Vector3f> modelFP = model.listOfFP();
            Icp.instance().reverseAllTransformations(transformations, modelFP, true);
            Icp.instance().reverseAllTransformations(transformations, m.getVerts(), true);
        }

        //return facialPoints;
    }

    //computes all points from fpUniverse
    private List<FacialPoint> computePointsFromFpUniverse(FeaturePointsUniverse fpUniverse, ArrayList<Vector3f> centerPoints) {
        List<FacialPoint> facialPoints;
        fpUniverse.findNose();
        fpUniverse.findMouth();
        fpUniverse.findEyes();
        facialPoints = fpUniverse.getFacialPoints();

        if (centerPoints != null) {
            facialPoints = fpUniverse.getSymmetryPlaneFPs(centerPoints);
        }
        return facialPoints;
    }

    //sets up Java View console so that it doesn't pop up
    private void setUpJavaViewConsole() {
        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);
        PsDebug.getConsole().setVisible(false);
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
    public FpResultsOneToMany calculatePointsOneToMany(List<File> models, Model mainF) {
        ProgressHandle p;
        FpResultsOneToMany results = null;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...");
        p.start();

        try {

            p.setDisplayName("Computing Feature Points...");

            Model model;
            List<FacialPoint> facialPoints;
            //List<ICPTransformation> transformations;
            //FeaturePointsUniverse fpUniverse;
            Map<String, List<FacialPoint>> allFPs = new HashMap<>();
            int size = models.size();
            ArrayList<Model> registeredModels = new ArrayList<Model>();

            for (int i = 0; i < size; i++) {
                model = ModelLoader.instance().loadModel(models.get(i), false, true);

                facialPoints = computePointsForSingleFace(p, model);
                registeredModels.add(model);            //needed?
                allFPs.put(model.getName(), facialPoints);

                //p.progress(i * 100 / size);
            }

            facialPoints = computePointsForSingleFace(p, mainF);

            results = new FpResultsOneToMany(facialPoints, (HashMap<String, List<FacialPoint>>) allFPs, registeredModels);

            p.finish();
            

        } catch (Exception ex) {
            p.finish();
        }

        return results;
    }

    //computes points for single face
    private List<FacialPoint> computePointsForSingleFace(ProgressHandle p, Model model) {
        List<ICPTransformation> trans;
        ArrayList<Vector3f> centerPoints;
        List<FacialPoint> fps;

        p.switchToDeterminate(100);
        p.progress("Registering model" + model.getName() + " to generic face", 0);

        //register face to generic model
        trans = faceRegistration(model);

        p.switchToDeterminate(100);
        p.progress("Getting center of face " + model.getName(), 0);

        //compute center of the face
        centerPoints = getCenterOfFace(model, true);

        p.progress("Computing feature points of face " + model.getName(), 100);
        p.switchToIndeterminate();

        //compute all facial points
        fps = computeAllFacialPoints(centerPoints, model, trans);

        return fps;
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
    public FpResultsBatch calculatePointsBatch(Cancellable cancelTask, List<File> models) {
        int size = models.size();

        ProgressHandle p;
        p = ProgressHandleFactory.createHandle("Computing Feature Points...", cancelTask);
        p.start();

        Model model;
        List<FacialPoint> facialPoints;
        List<Model> registeredModels = new ArrayList<>();
        Map<String, List<FacialPoint>> allFPs = new HashMap<>();
        FpResultsBatch res = null;

        for (int i = 0; i < size; i++) {
            if (Thread.currentThread().isInterrupted()) {
                p.finish();
                return res;
            }
            model = ModelLoader.instance().loadModel(models.get(i), false, true);

            facialPoints = computePointsForSingleFace(p, model);
            registeredModels.add(model);            //needed?
            allFPs.put(model.getName(), facialPoints);

            //p.progress((int) (unit * (i + 1)));
        }

        res = new FpResultsBatch(allFPs, registeredModels);
        p.finish();
        return res;
    }

}
