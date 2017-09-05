/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.comparison.surfaceComparison;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.comparison.hausdorffDistance.ComparisonMetrics;
import cz.fidentis.comparison.hausdorffDistance.HausdorffDistance;
import cz.fidentis.comparison.hausdorffDistance.NearestCurvature;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.icp.KdTreeFaces;
import cz.fidentis.comparison.icp.NearestNeighborCallable;
import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.controller.BatchComparison;
import cz.fidentis.controller.Comparison2Faces;
import cz.fidentis.controller.OneToManyComparison;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.featurepoints.curvature.Curvature_jv;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.processing.fileUtils.ProcessingFileUtils;
import cz.fidentis.undersampling.Methods;
import cz.fidentis.undersampling.Type;
import cz.fidentis.undersampling.Undersampling;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utils.ListUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.MeshUtils;
import cz.fidentis.utilsException.FileManipulationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.vecmath.Vector3f;
import org.netbeans.api.progress.*;
import org.openide.util.Exceptions;

/**
 * Handling all types of comparison provided by application. Class deals with
 * surface comparison
 *
 * @author Zuzana Ferkova
 */
public class SurfaceComparisonProcessing {

    private static SurfaceComparisonProcessing instance;
    private static final File tmpModuleFile = new File("compF");
    private static ProgressHandle p;

    private SurfaceComparisonProcessing() {

    }

    public static SurfaceComparisonProcessing instance() {
        if (instance == null) {
            instance = new SurfaceComparisonProcessing();
        }

        return instance;
    }

    public static ProgressHandle getP() {
        return p;
    }

    public static void setP(ProgressHandle p) {
        SurfaceComparisonProcessing.p = p;
    }

    /**
     * Aligns two faces, main and secondary (compF), using ICP algorithm. User
     * can decided how many vertices will be used for alignment (hence not all
     * vertices will need to be considered when computing parameters for
     * alignment)
     *
     * Does not create local copies of either main or secondary face
     *
     * @param mainF - KdTree representation of main face to which secondary face
     * will be aligned
     * @param compF - point cloud representation of secondary face which will be
     * aligned to main face
     * @param numberOfIterations - number of iterations performed during ICP
     * algorithm
     * @param scale - whether to use scale during ICP
     * @param error - plausible error rate, to stop ICP before
     * numberOfIterations is reached
     * @param method - method of undersampling
     * @param t - type of undersampling (samples, percentage -- so far only
     * valid for Random Undersampling)
     * @param value - value of undersampling from GUI
     */
    public void processOneToOne(KdTree mainF, Model compF, int numberOfIterations, boolean scale, float error,
            Methods method, Type t, float value, Comparison2Faces data) {
        List<Vector3f> samples = getUndersampledMesh(method, t, value, compF);
        
        p.setDisplayName("Aligning faces.");

        List<ICPTransformation> trans = Icp.instance().icp(mainF, compF.getVerts(), samples, error, numberOfIterations, scale);
        
        data.setCompFTransformations(trans);
    }

    /**
     * Aligns set of faces (compFs) to main face using ICP algorithm. Can decide
     * how many vertices will be used to compute alignment parameters. Number of
     * vertices used is same for each face in set (unless number is larger than
     * number of vertices, in that case all vertices are used). Uses pair
     * alignment; iteratively aligns each face to main face.
     *
     * Creates local copies of aligned faces (compFs).
     *
     * @param mainF - KdTree representation of main face
     * @param compFs - list containing Files with all the faces to be aligned to
     * main face
     * @param numberOfIterations - number of iterations performed during ICP
     * algorithm
     * @param scale - whether to use scale during ICP
     * @param error - plausible error rate, to stop ICP before
     * numberOfIterations is reached
     * @param m - method of undersampling
     * @param t - type of undersampling (samples, percentage -- so far only
     * valid for Random Undersampling)
     * @param value - value of undersampling from GUI
     * @return list containing files with aligned faces
     */
    public List<File> processOneToMany(KdTree mainF, List<File> compFs, int numberOfIterations, boolean scale, float error,
            Methods m, Type t, float value, OneToManyComparison data) {
        
        Model compF;
        List<File> results = new ArrayList<File>(compFs.size());
        int i = 0;
        
        String projectId = "" + System.currentTimeMillis();
        String tmpLoc = projectId + File.separator + tmpModuleFile.getName()/* + File.separator + tmpModuleFile.getName()*/;
        
        try {
            FileUtils.instance().createTMPmoduleFolder(new File(tmpLoc));
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }

        for (File compF1 : compFs) {
            p.setDisplayName("Registrating face number " + (i + 1));
            compF = ModelLoader.instance().loadModel(compF1, Boolean.FALSE, true);
            List<Vector3f> samples = getUndersampledMesh(m, t, value, compF);

            List<ICPTransformation> trans = Icp.instance().icp(mainF, compF.getVerts(), samples, error, numberOfIterations, scale);
            data.addTrans(trans);

            results.add(ProcessingFileUtils.instance().saveModelToTMP(compF, new File(tmpLoc), -2, i, Boolean.FALSE));

            i++;
        }
        return results;
    }

    /**
     * Compare main face with average face created from set of faces. As the
     * result of comparison list of Hausdorff distances are returned. Allows to
     * use relative, or absolute coordinates.
     *
     * Normals of average face are recomputed in process
     *
     * @param mainF - KdTree representation of main face
     * @param avarage - Model class containing information about average face
     * @param useRelative - defines whether algorithm should computed relative
     * or absolute values of HD
     * @return list containing resluts of comparison, as float number
     * representing hausdorff metric for each point in average face
     */
    public List<Float> compareOneToMany(KdTree mainF, Model avarage, boolean useRelative, double[] mainCurv, ComparisonMethod method) {
        List<Float> results;
        List<Vector3f> recomputedVertexNormals = null;
        if (useRelative) {
            recomputedVertexNormals = recomputeVertexNormals(avarage);
            avarage.setNormals((ArrayList<Vector3f>) recomputedVertexNormals);
        }

        if (method == ComparisonMethod.HAUSDORFF_DIST) {
            results = HausdorffDistance.instance().hDistance(mainF, avarage.getVerts(), recomputedVertexNormals, useRelative);
        } else {
            Curvature_jv curv = new Curvature_jv(avarage);
            double[] secondaryCurv = curv.getCurvature(CurvatureType.Gaussian);
            results = NearestCurvature.instance().nearestCurvature((KDTreeIndexed) mainF, avarage.getVerts(), mainCurv, secondaryCurv);
        }

        return results;
    }

    /**
     * Computes numeric results for 1:N surface comparison
     *
     * @param mainF - main face to which all models are aligned
     * @param models - N loaded models
     * @param useRelative - whether to use signed distance or not
     * @return numeric results of 1:N surface comparison
     */
    public List<ArrayList<Float>> compareOneToManyNumeric(Model mainF, List<File> models, boolean useRelative, ComparisonMethod method) {
        List<ArrayList<Float>> results = new ArrayList<ArrayList<Float>>();
        Model current;
        KdTree currentTree;
        Curvature_jv secondarCurv;
        double[] secondaryCurvature = new double[]{};

        if (method == ComparisonMethod.HAUSDORFF_DIST) {
            secondarCurv = new Curvature_jv(mainF);
            secondaryCurvature = secondarCurv.getCurvature(CurvatureType.Gaussian);
        }

        
        for (File f : models) {
            current = ModelLoader.instance().loadModel(f, Boolean.FALSE, Boolean.FALSE);
            currentTree = new KDTreeIndexed(current.getVerts());

            if (method == ComparisonMethod.HAUSDORFF_DIST) {
                results.add((ArrayList<Float>) HausdorffDistance.instance().hDistance(currentTree, mainF.getVerts(), mainF.getNormals(), useRelative));
            } else {
                Curvature_jv mainCurv = new Curvature_jv(current);
                double[] mainCurvature = mainCurv.getCurvature(CurvatureType.Gaussian);
                results.add((ArrayList<Float>) NearestCurvature.instance().nearestCurvature((KDTreeIndexed) currentTree, mainF.getVerts(), mainCurvature, secondaryCurvature));
            }

        }

        return results;
    }

    /**
     * Computes thresholded variance from pre-computed numeric values.
     *
     * @param results - pre-computed 1:N numeric results
     * @param upperTreshold - threshold value
     * @param variationMethod - method of variation to compute
     * @param useRelative - whether to use signed distance or not
     * @return recomputed numeric results
     */
    public List<Float> compareOneToManyVariation(List<ArrayList<Float>> results, float upperTreshold, float lowerTreshold, int variationMethod, boolean useRelative) {
        List<Float> res = new ArrayList<Float>();
        List<Float> thresholdedValues;

        for (ArrayList<Float> list : results) {
            thresholdedValues = ComparisonMetrics.instance().thresholdValues(list, upperTreshold, lowerTreshold, useRelative);

            res.add(computeSingleVariation(thresholdedValues, variationMethod, useRelative));
        }

        return res;
    }
    
    /**
     * Creates String version of given numerical results, ready for JTable, given the results of measured Hausdorff distance.
     * First line contains variance method used, along with lower and upper thresholds (in percentage), plus names of all secondary models
     * Second line contains name of the main model and numerical results of comparison on regards to each of the secondary models
     * 
     * @param hdDistance - computed Hausdorff distances
     * @param models - secondary models used
     * @param mainFace - name of the main model
     * @param upperThreshold - value of upper threshold used. This should be in percentage, in interval [0, 100]
     * @param lowerThreshold - value of lower threshold used. This should be in percentage, in interval [0, 100]
     * @param varianceMethod - variance method used to compute numerical results of comparison
     * @return formated String, ready to be used for JTable, containing information about numerical results
     */
    public String formatedNumResOneToMany(List<Float> hdDistance, List<File> models, String mainFace, float upperThreshold, float lowerThreshold, int varianceMethod) {
        StringBuilder strResults = new StringBuilder(SurfaceComparisonProcessing.instance().getNameOfVarianceMethod(varianceMethod) + " Upper: " + upperThreshold + 
                "% Lower: " + lowerThreshold + "% treshold;");
        
        for(int i = 0; i < hdDistance.size(); i++){
            strResults.append(models.get(i).getName()).append(';');
        }
        
        strResults.append("\n").append(mainFace).append(";");
        
        for(Float f : hdDistance){
            strResults.append(f).append(';');    
        }
        
        return strResults.toString();
    }

    /**
     * Given the template, method alignes all faces in 'compFs' to template and
     * computes new average face. 'numberOfBatchIterations' is number of average
     * faces created in process. Each face from 'compFs' is aligned to template
     * by performing 'numberOfICPiteration' ICP iterations. It is possible to
     * undersample the model by only performing ICP on 'usedSamples' % of
     * vertices.
     *
     * @param template - template face to be used to create average faces
     * @param compFs - list of faces to be aligned to template face and to
     * compute new average faces from
     * @param numberOfBatchIterations - number of average faces to be created
     * @param numberOfICPiteration - number of ICP iterations to be performed
     * for each face in compFs during each ICP alignment
     * @param scale - whether to use scale during ICP
     * @param error - error rate, for ICP algorithm, if change between two
     * iterations is smaller than error, ICP stops
     * @param m - method of undersampling
     * @param t - type of undersampling (Percentage, Number of Samples, None)
     * @param value - value of undersampling
     * @param metric - ICP metric used for registration
     * @return list of aligned models
     * @throws FileManipulationException if the method was unable to write down
     * temporary files on disk
     */
    public List<File> processManyToMany(Model template, List<File> compFs, int numberOfBatchIterations, int numberOfICPiteration, boolean scale, float error,
            Methods m, Type t, float value, ICPmetric metric, BatchComparison data) throws FileManipulationException {

        List<Vector3f> trans;
        
        //create template tree based on metric
        KdTree templateTree = createTemplateTree(metric, template);        

        //temporary folder on disk where temporary files (like aligned faces) will be stored until application is closed
        String projectId = "" + System.currentTimeMillis();
        File tmpLocFile = new File(projectId + File.separator + tmpModuleFile.getName());   

        //copy all loaded models to tmp and work with copied version of models
        copyModelToTMP(compFs, projectId);

        //start computing algorithm
        for (int i = 0; i < numberOfBatchIterations; i++) {              
            trans = alignBatchIteration(compFs, i, m, t, value, template, templateTree, error, numberOfICPiteration, scale, tmpLocFile, metric, data);
            templateTree = createNewAvgFace(template, trans, compFs.size());    
        }
        
        //last iteration to last avg model
        List<File> alignedModels = lastBatchAlignment(compFs.size(), numberOfBatchIterations, templateTree, error, numberOfICPiteration, scale, tmpLocFile,
                m, t, value);
        return alignedModels;
    }

    /**
      * Creates new average face from computed translations and returns new average face stored in KdTree data structure.
      * 
      * @param template - model from which the new average is created (current average model)
      * @param trans - computed translations to be applied to create new average model
      * @param compFsSize - number of models from which new average is computed
      * @return new average model stored in KdTree data structure
      */
    private KdTree createNewAvgFace(Model template, List<Vector3f> trans, int compFsSize) {
        KdTree templateTree = null;
        int templateSize = template.getVerts().size();
        
        ProgressHandle k;
        k = ProgressHandleFactory.createHandle("Computing Average Face.");
        k.start();
        try {
            //average translation vector for each vertex by number of faces aligned to template and apply it, creating new average face
            computeMeanTranslationToModel(template, trans, templateSize, compFsSize);
            
            //creates new kd-tree for just created average face
            templateTree = new KDTreeIndexed(template.getVerts());
            k.finish();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }finally{
            k.finish();
        }
        return templateTree;
    }

    /**
      * Performs one iteration for batch alignment, consisting of aligning all models to current average model and adding all computed translations
      * to create new average model, to a single list. 
      * 
      * @param compFs - faces which need to be aligned to the template
      * @param currentBatchIteartion - current batch iteration
      * @param m - method for undersampling
      * @param t - type of undersampling
      * @param value - value of undersampling
      * @param template - model representing current average model
      * @param templateTree - KdTree of current average model
      * @param error - error rate, stopping criterium for ICP
      * @param numberOfICPiteration - max. number of ICP iterations performed
      * @param scale - whether to use scale during ICP or not
      * @param tmpLocFile - URL to current folder where tmp files are stored
      * @param metric - ICP metric used
      * @param data - data model for current Batch
      * @return list of translations to be applied to average mdoel
      */
    private List<Vector3f> alignBatchIteration(List<File> compFs, int currentBatchIteration, Methods m, Type t, float value, Model template, 
            KdTree templateTree, float error, int numberOfICPiteration, boolean scale, File tmpLocFile, ICPmetric metric, BatchComparison data) {
        
        //change to directory of current iteration
        String currentTMP = FileUtils.instance().getTempDirectoryPath() + File.separator +  tmpLocFile.getPath() + File.separator + 
                 tmpModuleFile.getName() + "_" + currentBatchIteration + "_";
        
        //get list with empty vectors of size of template to add translations to
        List<Vector3f> trans = ListUtils.instance().populateVectorList(template.getVerts().size());  

        for(int j = 0; j < compFs.size(); j++){
            List<Vector3f> translations = batchAlign2Faces(currentTMP, j, currentBatchIteration, m, t, value, template, templateTree, 
                    error, numberOfICPiteration, scale, tmpLocFile, metric, data);
            addTranslationToModel(trans, translations);
        }
        
        return trans;
    }

    /**
      * Perform last alignment of batch registration, by aligning all the face to last average model and returning list of URLs of aligned models stored on disk.
      * 
      * @param compFsize - number of models to be aligned to average model
      * @param numberOfBatchIterations - number of batch iterations performed up to this point
      * @param templateTree - average model stored in KdTree data structure
      * @param error - ICP error threshold
      * @param numberOfICPiteration - max. number of ICP iterations performed
      * @param scale - whether to use scaling during ICP or not
      * @param tmpLocFile - URL to current folder where tmp files are stored
      * @param m - method to be used for undersampling
      * @param t - type of undersampling
      * @param value - value of undersampling
      * @return URLs to aligned models to be used in comparison
      */
    private List<File> lastBatchAlignment(int compFsize, int numberOfBatchIterations, KdTree templateTree, float error, int numberOfICPiteration, 
            boolean scale, File tmpLocFile,
            Methods m, Type t, float value) {
        
        List<File> results = new ArrayList<>(compFsize); 
        
        //change to directory of current iteration
         String currentTMP = FileUtils.instance().getTempDirectoryPath() + File.separator +  tmpLocFile.getPath() + File.separator + 
                 tmpModuleFile.getName() + "_" + numberOfBatchIterations + "_"; 
        
        
        //last registration to last batch
        for (int i = 0; i < compFsize; i++) {
            Model currentModel = ModelLoader.instance().loadModel(new File(currentTMP + i + File.separator + tmpModuleFile.getName() + "_" + numberOfBatchIterations + "_" + i + ".obj"), Boolean.FALSE, false);
            List<Vector3f> samples = getUndersampledMesh(m, t, value, currentModel);            
            
            File f = new BatchRegistrationLastCallable(templateTree, currentModel, samples, error, numberOfICPiteration, scale, tmpLocFile, numberOfBatchIterations + 1, i).call();
            results.add(f);
        }
        
        return results;
    }

    /**
      * Create KdTree for given model based on the metric provided
      * 
      * @param metric - metric for KdTree, either to use point-to-point or point-to-triangle distance
      * @param template - model to create the KdTree from
      * @return created KdTree
      */
    private KdTree createTemplateTree(ICPmetric metric, Model template) {
        //kd-tree for template
        KdTree templateTree;
        if (metric == ICPmetric.VERTEX_TO_VERTEX) {
            templateTree = new KDTreeIndexed(template.getVerts());
        } else {
            templateTree = new KdTreeFaces(template.getVerts(), template.getFaces());
        }
        return templateTree;
    }

    /**
      * Copy all models to tmp folder.
      * 
      * @param compFs - models to copy
      * @param projectId - ID of the project
      */
    private void copyModelToTMP(List<File> compFs, String projectId) {
        //progress bar message
         ProgressHandle k = ProgressHandleFactory.createHandle("Creating local files.");
         k.start();
        
        try {
            ProcessingFileUtils.instance().copyModelsToTMP(compFs, new File(projectId + File.separator + tmpModuleFile.getName()), Boolean.FALSE);       //copy all models in 'compFs' to temporary folder, so that origianl files can still be edited without causing problem with computation
            k.finish();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }finally{
            k.finish();
        }
    }

    //loads model from disk to align it to template, 
    /**
      * Align single mesh to a the main mesh. Nearest neighbor search is performed in parallel,
      * hence the Executor is necessary for the method to work. 
      * 
      * 
      * @param currentTMP - string containing address to current temporary folder of batch iteration
      * @param modelNumber - index of model that is to be aligned to the main mesh
      * @param i - number of batch iteration
      * @param m - undersampling method used
      * @param t - type of undersampling method (only valid if picked Method is Random or Number)
      * @param value - value of undersampling
      * @param executor - executor instance 
      * @param template - main model loaded in memory
      * @param templateTree - main model stored as KdTree structure
      * @param error - error rate parameter for ICP alignment
      * @param numberOfICPiteration - max. number of ICP iterations to be performed
      * @param scale - whether to use scale during ICP or not
      * @param tmpLocFile - tmp folder URL
      * @param metric - ICP metric to be used to align models
      * @param data - Batch Comparison data model
      * @return 
      */
    private List<Vector3f> batchAlign2Faces(String currentTMP, int modelNumber, int batchIteration, Methods m, Type t, float value, 
            /*ExecutorService executor,*/ Model template, KdTree templateTree, 
            float error, int numberOfICPiteration, boolean scale, File tmpLocFile, ICPmetric metric, BatchComparison data) {
        Model currentModel = ModelLoader.instance().loadModel(new File(currentTMP + modelNumber + File.separator + tmpModuleFile.getName() + "_" + batchIteration + "_" + modelNumber + ".obj"), Boolean.FALSE, false);
        List<Vector3f> samples = getUndersampledMesh(m, t, value, currentModel);
      
       List<Vector3f> future = new BatchProcessingCallable(currentModel, samples, template, templateTree,
                error, numberOfICPiteration, scale, tmpLocFile, modelNumber, batchIteration, Boolean.TRUE, metric, data).call();
       
        return future;
    }

    /**
     * Adds translation computed in 'list1' to sum of all translations in
     * 'trans'.
     *
     * @param trans list containing sum of all computed translations, size
     * equals to number of template vertices
     * @param list1 list with translations for each vertex of trans computed
     * from single compF
     */
    private void addTranslationToModel(List<Vector3f> trans, List<Vector3f> list1) {
        for (int j = 0; j < trans.size(); j++) {

            trans.get(j).x += list1.get(j).x;
            trans.get(j).y += list1.get(j).y;
            trans.get(j).z += list1.get(j).z;

        }
    }

    /**
     * Computes average translation, based on number of faces that are being
     * compared and applies translation to template creating average face.
     *
     * @param template - template from which new average face is created
     * @param trans - list containing sum of computed translations
     * @param templateSize - number of vertices of template
     * @param compFSize - number of models being compared
     */
    private void computeMeanTranslationToModel(Model template, List<Vector3f> trans, int templateSize, int compFSize) {
        for (int l = 0; l < templateSize; l++) {
            template.getVerts().get(l).x += trans.get(l).x / compFSize;
            template.getVerts().get(l).y += trans.get(l).y / compFSize;
            template.getVerts().get(l).z += trans.get(l).z / compFSize;
        }
    }

    /**
     * Computes raw visual results of comparison of each face in 'compFs' to
     * 'template'. Returns matrix of size template.size X compFs.size as it
     * computes a single value for each pair template-compFs.element, so that
     * each vertex of template has compFs.size values asigned to it. These
     * values are then used in computation of variation method and final result
     * (such as RMS, min, max etc.)
     *
     * @param template - average face to which all faces in 'compFs' will be
     * compared to
     * @param compFs - faces to be compared to template
     * @param useRelative - should the result be relative distance (takes into
     * consideration whether point is in front of behind template) or absolute
     * (classic Euclidean distance)
     * @param method - method used for comparison of models
     * @param metric - ICP metric used for comparison
     * @param computeAvg - choose whether to compute average mesh from the models or not
     * @return matrix of size template.size X compFs.size containing raw results
     * to be either analyzed on their own, or compute numerical results from
     * @throws FileManipulationException if method couldn't save temporary
     * results on disk
     */
    public ArrayList<ArrayList<Float>> compareFaces(Model template, List<File> compFs, boolean useRelative, ComparisonMethod method, ICPmetric metric, boolean computeAvg) throws FileManipulationException {
        ArrayList<ArrayList<Float>> results = new ArrayList<>(compFs.size());
        Curvature_jv templateCurv = null;
        Curvature_jv morphCurv = null;

        KdTree computeMorph;
        Model currentModel;

        //progress bar message
        ProgressHandle k = ProgressHandleFactory.createHandle("Computing average face.");
        k.start();

        //computes average face to compare all meshes to
        if (computeAvg) {
            computeAverage(template, compFs, metric);
        }
        if (method == ComparisonMethod.HAUSDORFF_CURV) {
            templateCurv = new Curvature_jv(template);
        }

        if (useRelative) {
            recomputeAndSetNormals(template);
        }

        k.setDisplayName("Comparing faces with default face.");

        //compute visual results and final matrix
        for (File f : compFs) {

            currentModel = ModelLoader.instance().loadModel(f, Boolean.FALSE, false);
            computeMorph = createModelKdTree(method, metric, currentModel);

            if (method == ComparisonMethod.HAUSDORFF_CURV) {
                morphCurv = new Curvature_jv(currentModel);
            }

            //finds NN to each vertex of template in curretn model  and computes distance to it (based on metric)
            ArrayList<Float> visResults = batchCompVisualResults(computeMorph, template, useRelative, morphCurv, templateCurv, method);
            results.add(visResults);
        }

        k.finish();

        return results;
    }
    
     /**
     * Computes visual results for surface batch processing
     * @return numerical results for single face
     */
    private ArrayList<Float> batchCompVisualResults(KdTree computeMorph, Model template, boolean useRelative, Curvature_jv morphCurv, Curvature_jv templateCurv, ComparisonMethod method){
        if(method == ComparisonMethod.HAUSDORFF_DIST){
            List<Vector3f> normalsUsed = template.getNormals();
        
            if(template.getVerts().size() > template.getNormals().size()){
                normalsUsed = SurfaceComparisonProcessing.instance().recomputeVertexNormals(template);
            }
        
            return (ArrayList<Float>) HausdorffDistance.instance().hDistance(computeMorph, template.getVerts(), normalsUsed, useRelative);
        }else{
           return (ArrayList<Float>) NearestCurvature.instance().nearestCurvature((KDTreeIndexed) computeMorph, template.getVerts(), morphCurv.getCurvature(CurvatureType.Gaussian), templateCurv.getCurvature(CurvatureType.Gaussian));
        }
    }

    /**
     * Create KdTree data structure from given model, based on ICPmetric that was be used
     * and ComparisonMethod that will be used.
     * 
     * @param method - ComparisonMethod that will be used in face comparison. If it is equal to Curvature, vertex-to-triangle 
     * metric can't be used
     * @param metric - ICPmetric which was used. This will decide whether to build KdTree that uses vertex-to-vertex metric, 
     * or vertex-to-triangle metric
     * @param currentModel - model to create KdTree data structure from
     * @return KdTree of appropriate type
     */
    private KdTree createModelKdTree(ComparisonMethod method, ICPmetric metric, Model currentModel) {
        KdTree computeMorph;
        //in case comparison 
        if (method == ComparisonMethod.HAUSDORFF_DIST && metric == ICPmetric.VERTEX_TO_MESH) {
            computeMorph = new KdTreeFaces(currentModel.getVerts(), currentModel.getFaces());
        } else {
            computeMorph = new KDTreeIndexed(currentModel.getVerts());
        }
        return computeMorph;
    }

    /**
     * Recomputes normals of given models and set it as new normals
     * 
     * @param template - model on which the normals need to be recomputed
     */
    private void recomputeAndSetNormals(Model template) {
        List<Vector3f> recomputedVertexNormals;
        //recompute normals of vertices in order to figure out whether part of compared mesh part is in front or behind template vertex
        recomputedVertexNormals = recomputeVertexNormals(template);
        template.setNormals((ArrayList<Vector3f>) recomputedVertexNormals);
    }

    /**
     * Computes numerical results for batch comparison based on 'varianceMethod'
     * picked, for each model in 'models'. Creates matrix of models.size X
     * models.size as such computes difference of each model in 'models' in
     * respect to each model in 'models'. It is possible to only use certain
     * amount of computed numerical result values by setting up appropriate
     * value of 'thresh', in range [0, 1].
     *
     * @param models models to be compared to each other
     * @param varianceMethod variance method to compute final results with
     * @param useRelative whether relative distance should be employed (with
     * sign), or whether classical Euclidean distance should be used instead
     * @param upperTreshold - allows to only use certain amount of computed numerical
     * result values, by setting parameter in range of [0, 1]
     * @param lowerTreshold - allow to bound used values of computed distances with lower bound
     * @param method - ComparisonMethod to be used to compute numerical results
     * @param auxiliaryResultsFile BatchComparison processing class to store
     * computed results to
     * @return matrix of size models.size X models.size containing numerical
     * result for each-to-each comparison
     */
    public ArrayList<ArrayList<Float>> batchCompareNumericalResults(List<File> models, int varianceMethod, boolean useRelative, Float upperTreshold, 
            Float lowerTreshold, ComparisonMethod method, BatchComparison auxiliaryResultsFile) {
        ArrayList<ArrayList<Float>> computedVariance;
        List<ArrayList<Float>> list;
        double[] mainCurv = null;
        KdTree mainFace;
        Model current;

        String projectId = "" + System.currentTimeMillis();
        String saveCSVTo = FileUtils.instance().getTempDirectoryPath() + File.separator + "batchTestResults" + File.separator + projectId;
        String saveTo = saveCSVTo + File.separator + "tmpCSV";

        computedVariance = ListUtils.instance().populateListList(models.size());
        
        //computing numerical results
        for (int i = 0; i < models.size(); i++) {
            ArrayList<ArrayList<Float>> uncomputedCollumn = new ArrayList<>(models.size());     //list to store raw comparison results

            current = ModelLoader.instance().loadModel(models.get(i), Boolean.FALSE, false);
            mainFace = createModelKdTree(method, auxiliaryResultsFile.getIcpMetric(), current);

            //in case curvature is used for comparison compute it
            if (method == ComparisonMethod.HAUSDORFF_CURV) {
                mainCurv = new Curvature_jv(current).getCurvature(CurvatureType.Gaussian);
            }
      
            uncomputedCollumn = (ArrayList<ArrayList<Float>>) batchRawResultsToSingle(models, i, mainFace, mainCurv, useRelative, upperTreshold, lowerTreshold, method);          
     
            batchVariance(uncomputedCollumn, computedVariance, varianceMethod, useRelative);
            //saves temporary results to disk to save up memory usage
            FileUtils.instance().saveCollumn(uncomputedCollumn, i, saveTo);
        }
        
        //save path where csvs are to use to recompute numerical results fasters
        if (auxiliaryResultsFile != null) {
            auxiliaryResultsFile.setHdCSVresults(new File(saveTo));
        }

        return computedVariance;
    }

    /**
     * Compute variance for all models compared to single model. 
     * Collects result from Executor (list of Future) and stores raw results (e.g. measured distances
     * to nearest neighbor) in "uncomputedColumn", while storing computed variance in "computedVariance"
     * 
     * @param uncomputedCollumn - list for raw results for given main model (this should be wiped clean every time new main model is loaded)
     * @param computedVariance - list for computed variance (this should be initialized only once and should not be wiped out)
     * @param varianceMethod - variance method to be used to compute numerical results
     * @param useRelative - whether to use 
     */
    private void batchVariance(List<ArrayList<Float>> uncomputedCollumn, ArrayList<ArrayList<Float>> computedVariance, int varianceMethod, boolean useRelative) {
        //compute final results based on raw comparison results and variance method
        //main face for comparison in first collumn to left
        for (int k = 0; k < uncomputedCollumn.size(); k++) {
             computedVariance.get(k).add(computeSingleVariation(uncomputedCollumn.get(k), varianceMethod, useRelative));  
        }
    }

    //give executor data to compute numerical results
    /**
     * Submits computation of distances from all models in "models" to main model stored in KdTree data structure to provided executor.
     * This method then returns all Future in order in which they were submited to Executor.
     * 
     * @param models - list of all models to be compared to the main face, stored on the disk
     * @param mainFaceIndex - index of the main face
     * @param executor - Executor instance to submit the tasks to
     * @param mainFace - main model in KdTree structure
     * @param mainCurv - main curvature for main face, this can be void if curvature is not used in comparison
     * @param useRelative - whether to use relative distances when computing results
     * @param upperTreshold - allows to bound measured distance with upper bound, interval [0,1]
     * @param lowerTreshold - allows to bound measured distance with lower bound, interval [0,1]
     * @param method - ComparisonMethod to be used for computation of numerical results
     * @return list of Future tasks submited to Executor
     */
    private List<ArrayList<Float>> batchRawResultsToSingle(List<File> models, int mainFaceIndex, KdTree mainFace, double[] mainCurv, boolean useRelative,
            Float upperTreshold, Float lowerTreshold, ComparisonMethod method) {
        List<ArrayList<Float>> list = new ArrayList<>();
        
        Model compF;
        double[] compCurvVals = null;
        //compute raw comparison results
        for (int j = 0; j < models.size(); j++) {
            if(p != null)
                p.setDisplayName("Computing numerical results for faces " + (mainFaceIndex + 1) + " and " + (j + 1) + ".");
            compF = ModelLoader.instance().loadModel(models.get(j), Boolean.FALSE, false);

            if (method == ComparisonMethod.HAUSDORFF_CURV) {
                compCurvVals = new Curvature_jv(compF).getCurvature(CurvatureType.Gaussian);
            }
            //Future<ArrayList<Float>> fut = executor.submit(new BatchComparisonNumericCallable(mainFace, compF, useRelative, upperTreshold, lowerTreshold, j, mainFaceIndex, mainCurv, compCurvVals, method));
            ArrayList<Float> singleRes = batchRawResultsTwoFaces(mainFace, compF, useRelative, upperTreshold, lowerTreshold, j, mainFaceIndex, mainCurv, compCurvVals, method);
                        
            list.add(singleRes);
        }
        
        return list;
    }
    
    private ArrayList<Float> batchRawResultsTwoFaces(KdTree mainF, Model compMesh, boolean useRelative, Float upperTreshold, Float lowerTreshold,  int mainFaceNum, int compareFaceNum,
            double[] mainCurv, double[] compCurv, ComparisonMethod method){
        List<Float> result;
        ProgressHandle p = ProgressHandleFactory.createHandle("Computing numerical results for faces " + (mainFaceNum + 1) + " and " + (compareFaceNum + 1) + ".");
        p.start();

        try {

            ArrayList<Float> tmp = new ArrayList<Float>((int) (compMesh.getVerts().size() * upperTreshold));

            if (method == ComparisonMethod.HAUSDORFF_DIST) {
                List<Vector3f> normalsUsed = compMesh.getNormals();

                if (compMesh.getVerts().size() > compMesh.getNormals().size()) {
                    normalsUsed = SurfaceComparisonProcessing.instance().recomputeVertexNormals(compMesh);
                }

                result = HausdorffDistance.instance().hDistance(mainF, compMesh.getVerts(), normalsUsed, useRelative);
            } else {
                result = NearestCurvature.instance().nearestCurvature((KDTreeIndexed) mainF, compMesh.getVerts(), mainCurv, compCurv);
            }
            result = ComparisonMetrics.instance().thresholdValues(result, upperTreshold, lowerTreshold, useRelative);
            tmp.addAll(result);

            p.finish();

            return tmp;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            p.finish();
        }

        return null;
    }

    /**
     * Returns string representation of computed numerical results so it can be
     * displayed in GUI table
     *
     * @param results computed numerical results to be displayed
     * @param varianceMethod variance method used, this parameter only serves to
     * inform user of the method they picked for final numerical results
     * @param originalModels list containing URLs to original models to be used to name columns in the table
     * @param upperTreshold upper threshold used to compute results 
     * @param lowerTreshold lower threshold used to compute results
     * @return string representation of computed results
     */
    public String batchCompareNumericalResultsTable(ArrayList<ArrayList<Float>> results, int varianceMethod, List<File> originalModels, float upperTreshold, float lowerTreshold) {
        StringBuilder strResults = new StringBuilder(getNameOfVarianceMethod(varianceMethod) + " Lower: " + (lowerTreshold * 100) + "% Upper: " + (upperTreshold * 100) + "% treshold;");

        for (int i = 0; i < results.size(); i++) {
            strResults.append(originalModels.get(i).getName()).append(';');
        }

        strResults.append("\n");

        for (int i = 0; i < results.size(); i++) {
            strResults.append(originalModels.get(i).getName()).append(';');

            for (int j = 0; j < results.size(); j++) {
                strResults.append(results.get(i).get(j)).append(';');
            }

            strResults.append("\n");
        }

        return strResults.toString();
    }

    /**
     * Returns string representation of method picked variance method.
     *
     * @param varianceMethod picked variance method represented by its index
     * @return string representation of picked variance method
     */
    public String getNameOfVarianceMethod(int varianceMethod) {
        String method = "";

        switch (varianceMethod) {
            case 0:
                method = "Root Mean Square";
                break;
            case 1:
                method = "Arithmetic Mean";
                break;
            case 2:
                method = "Geometric Mean";
                break;
            case 3:
                method = "Minimal Distance";
                break;
            case 4:
                method = "Maximal Distance";
                break;
            case 5:
                method = "Variance";
                break;
            case 6:
                method = "75 Percentile";
                break;
            default:
            //error   
        }

        return method;

    }

    /**
     * Computes variance for single comparison numerical results between 2
     * faces, resulting into one value, based on picked variance method.
     *
     * @param results raw results to compute variation from
     * @param varianceMethod method to be used to compute final variation
     * @param useRelative whether to use relative distance (with sign) or
     * classical, Euclidean, distance
     * @return single number representing final variance computed from 'results'
     * based on 'varianceMethod'
     */
    public static float computeSingleVariation(List<Float> results, int varianceMethod, boolean useRelative) {
        float variation = -1f;

        switch (varianceMethod) {
            case 0:
                variation = ComparisonMetrics.instance().rootMeanSqr(results, useRelative);
                break;
            case 1:
                variation = ComparisonMetrics.instance().aritmeticMean(results, useRelative);
                break;
            case 2:
                variation = ComparisonMetrics.instance().geometricMean(results, useRelative);
                break;
            case 3:
                variation = ComparisonMetrics.instance().findMinDistance(results, useRelative);
                break;
            case 4:
                variation = ComparisonMetrics.instance().findMaxDistance(results, useRelative);
                break;
            case 5:
                variation = ComparisonMetrics.instance().variance(results, useRelative);
                break;
            case 6:
                variation = ComparisonMetrics.instance().percentileSeventyFive(results, useRelative);
                break;
            default:
            //error                 

        }

        return variation;
    }

    /**
     * Creates list containing variation computed for every vertex of template
     * from tuple of pre-computed visual comparison values. Serves mainly for
     * computing visual results.
     *
     * @param resultMatrix matrix of size numberOfTemplateVertices X
     * numberOfComparedModels containing raw visual comparison data
     * @param varianceMethod method to be used to compute variance for each
     * vertex of average face
     * @param useRelative whether to use relative distance (with sign) or
     * classical, Euclidean, distance
     * @return list containing variation computed for every vertex of template
     * from tuple of pre-computed visual comparison values
     */
    public List<Float> computeVariation(ArrayList<ArrayList<Float>> resultMatrix, int varianceMethod, boolean useRelative) {
        List<Float> variance = new ArrayList<>(resultMatrix.size());
        List<Float> midResult = new ArrayList<>();
        Float midResNum;

        int sizeOfTemplate = resultMatrix.get(0).size();

        for (int i = 0; i < sizeOfTemplate; i++) {
            //memory?
            midResult.clear();

            for (int j = 0; j < resultMatrix.size(); j++) {
                midResNum = resultMatrix.get(j).get(i);

                if (!useRelative) {
                    midResNum = Math.abs(midResNum);
                }

                midResult.add(midResNum);
            }

            variance.add(computeSingleVariation(midResult, varianceMethod, useRelative));
        }

        return variance;
    }

    /**
     * Recomputes numerical results from precomputed values stored at this.
     *
     * @param precomputedResultsFile folder containing CSV files with
     * pre-computed data
     * @param varianceMethod variance method to be used for recomputing
     * numerical results
     * @param numOfModels number of models that were compared
     * @param upperTreshold allows to only use certain amount of pre-computed values,
     * thresh should be in range [0, 1]
     * @param useRelative whether to use relative distance (with sign) or
     * classical, Euclidean, distance
     * @return list containing recoputed numerical results, based on given
     * parameters
     */
    public List<ArrayList<Float>> recomputeNumericResults(File precomputedResultsFile, int varianceMethod, int numOfModels, float upperTreshold, float lowerTreshold, boolean useRelative) {
        List<ArrayList<Float>> finalMatrix = new ArrayList<>(numOfModels);
        List<Float> thresholdedValues;
        ArrayList<ArrayList<Float>> csv;

        for (int i = 0; i < numOfModels; i++) {
            csv = FileUtils.instance().readFolderWithCSV(precomputedResultsFile + File.separator + (i + 1), numOfModels, i, useRelative);   //read all precomputed CSV files stored in tmp folder
            List<Float> singleLine = new ArrayList<>(csv.size());

            //recompute data
            for (int j = 0; j < csv.size(); j++) {
                thresholdedValues = ComparisonMetrics.instance().thresholdValues(csv.get(j), upperTreshold, lowerTreshold, useRelative);
                singleLine.add(computeSingleVariation(thresholdedValues, varianceMethod, useRelative));
            }
            finalMatrix.add((ArrayList<Float>) singleLine);
        }

        return finalMatrix;
    }

    /**
     * Loads and returns uncomputed results of comparison between two faces, of
     * index numOfMainModel and numOfSecondaryModel.
     *
     * @param precomputedResultsFile - directory where aux results are stored
     * @param numOfModels - total number of models with which comparison
     * computes
     * @param numOfMainModel - index of main model
     * @param numOfSecondaryModel - index of secondary model
     * @param useRelative - whether to use signed distance or not
     * @return list of aux results for comparison of numOfSecondaryModel to
     * numOfMainModel
     */
    public List<Float> numRawResForModel(File precomputedResultsFile, int numOfModels, int numOfMainModel, int numOfSecondaryModel, boolean useRelative) {
        ArrayList<ArrayList<Float>> csv;

        csv = FileUtils.instance().readFolderWithCSV(precomputedResultsFile + File.separator + (numOfMainModel + 1), numOfModels, numOfMainModel, useRelative);

        return csv.get(numOfSecondaryModel);
    }


    /**
     * Compute average face based on 'tempalte' and aligned 'compF' faces
     *
     * @param template template mesh to be used to create new average face
     * @param compF faces to be used to compute parameters of new average face
     * from
     * @param metric - ICP metric used for registration
     */
    public void computeAverage(Model template, List<File> compF, ICPmetric metric) {
        List<Vector3f> trans;     //list that will contain sum of translation vectors for each vertex of tempalte
        Model comp;

        trans = ListUtils.instance().populateVectorList(template.getVerts().size());
        int templateSize = template.getVerts().size();
        
               

        //parallel computation nearest neighbor search (same as ICP but without alignment)
        for (File f : compF) {
            if(p != null)
                p.setDisplayName("Creating default face from models.");
            comp = ModelLoader.instance().loadModel(f, Boolean.FALSE, false);

            List<Vector3f> displacment = runAvgFaceComputation(comp, template, metric);
            addTranslationToModel(trans, displacment);
        }      

        //compute average translation for each vertex and apply it to given vertex
        computeMeanTranslationToModel(template, trans, templateSize, compF.size());
    }


    /**
     * Computes displacement vectors between template face and nearest neighbors in comp
     * 
     * @param comp - Model to be compared to the main model
     * @param template - Main model
     * @param metric - ICPmetric for alignment
     */
    private List<Vector3f> runAvgFaceComputation(Model comp, Model template, ICPmetric metric) {        
        List<Vector3f> displacments = new ArrayList<>(template.getVerts().size());
        List<Future<Vector3f>> nearestNeighbors = new ArrayList<>(template.getVerts().size());
        KdTree compTree;
        
        if (metric == ICPmetric.VERTEX_TO_VERTEX) {
            compTree = new KDTreeIndexed(comp.getVerts());
        } else {
            compTree = new KdTreeFaces(comp.getVerts(), comp.getFaces());
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); //creates thread pool of size of number of available processors 
        
        for(Vector3f v : template.getVerts()){
           nearestNeighbors.add(executor.submit(new NearestNeighborCallable(compTree, v)));
        }
        
        executor.shutdown();
        
        for(int i = 0; i < nearestNeighbors.size(); i++){
            try {
                displacments.add(MathUtils.instance().createVector(template.getVerts().get(i), nearestNeighbors.get(i).get()));
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        
        return displacments;
    }


    /**
     * Compute average face based on 'tempalte' and aligned 'compF' faces
     *
     * @param template template mesh to be used to create new average face
     * @param compF faces to be used to compute parameters of new average face
     * from
     * @param metric - ICP metric used for registration
     */
    public void computeAverage(Model template, Model[] compF, ICPmetric metric) {
        List<Vector3f> trans;     //list that will contain sum of translation vectors for each vertex of tempalte

        trans = ListUtils.instance().populateVectorList(template.getVerts().size());
        int templateSize = template.getVerts().size();
        
               

        //parallel computation nearest neighbor search (same as ICP but without alignment)
        for (Model m : compF) {
            if(p != null)
                p.setDisplayName("Creating default face from models.");
           
            List<Vector3f> displacment = runAvgFaceComputation(m, template, metric);
            addTranslationToModel(trans, displacment);
        }      

        //compute average translation for each vertex and apply it to given vertex
        computeMeanTranslationToModel(template, trans, templateSize, compF.length);
    }

    /**
     * Recomputes normals of given mesh. Useable when new average face is
     * created. Does not recompute normals if number of vertex normals in mesh
     * is larger than number of vertices (very likely this is an error in mesh)
     *
     * @param mesh mesh to recompute normals of
     * @return list of recomputed normals
     */
    public List<Vector3f> recomputeVertexNormals(Model mesh) {
        if (mesh.getVerts().size() < mesh.getNormals().size()) {
            return mesh.getNormals();
        }

        List<Vector3f> newVertexNormals = new ArrayList<>(mesh.getNormals().size());
        List<Vector3f> meshVertices = mesh.getVerts();
        Vector3f direction;
        int[] faceIndexes;
        int[] numAdded;
        int numberOfNormals = mesh.getVerts().size();

        numAdded = new int[numberOfNormals];

        for (int i = 0; i < numberOfNormals; i++) {
            newVertexNormals.add(new Vector3f());
            numAdded[i] = 0;
        }

        for (int i = 0; i < mesh.getFaces().getNumFaces(); i++) {
            faceIndexes = mesh.getFaces().getFaceVertIdxs(i);

            direction = MathUtils.instance().getNormalOfTriangle(meshVertices.get(faceIndexes[0] - 1), meshVertices.get(faceIndexes[1] - 1), meshVertices.get(faceIndexes[2] - 1));

            if (direction.length() != 0f) {
                direction = MathUtils.instance().divideVectorByNumber(direction, direction.length());       //does not save new normal of face
            }

            for (int j : faceIndexes) {
                newVertexNormals.get(j - 1).add(direction);               //does not change normal values in original model
                numAdded[j - 1]++;
            }
        }

        for (int i = 0; i < numberOfNormals; i++) {           //compute final normals and normalize
            Vector3f newVertexNormal = newVertexNormals.get(i);

            newVertexNormal.x /= numAdded[i];
            newVertexNormal.y /= numAdded[i];
            newVertexNormal.z /= numAdded[i];

            newVertexNormals.get(i).normalize();
        }

        return newVertexNormals;
    }

    private List<Vector3f> getUndersampledMesh(Methods m, Type t, float value, Model mesh) {
        List<Vector3f> undersampled;

        switch (m) {
            case Random:
                undersampled = Undersampling.instance().resolveRandom(t, (int) value, mesh.getVerts());
                break;
            case Curvature:
                undersampled = Undersampling.instance().resolveCurvature(t, (int) value, mesh);
                break;
            case Disc:
                undersampled = Undersampling.instance().resolveDisk(value, mesh);
                break;
            default:
                undersampled = mesh.getVerts();
                break;
        }

        return undersampled;
    }

    public Type getSelectedType(Methods m, ButtonGroup bg) {
        if (m != Methods.Random && m != Methods.Curvature) {
            return Type.NONE;
        }

        int i = 0;

        for (Enumeration<AbstractButton> buttons = bg.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return Type.values()[i];
            }

            i++;
        }

        return Type.NONE;
    }

    /**
     * Computes numerical results for 1:1 comparison
     *
     * @param hdDistance - thresholded values
     * @param useRelative - whether to use signed distance or not
     * @param threshold - used threshold
     * @return string representing results of 1:1 comparison
     */
    public String getNumericResults(List<Float> hdDistance, boolean useRelative) {
        return (  "Min;" + ComparisonMetrics.instance().findMinDistance(hdDistance, useRelative) + "\n"
                + "Max;" + ComparisonMetrics.instance().findMaxDistance(hdDistance, useRelative) + "\n"
                + "RMS;" + ComparisonMetrics.instance().rootMeanSqr(hdDistance, useRelative) + "\n"
                + "Arithmetic Mean;" + ComparisonMetrics.instance().aritmeticMean(hdDistance, useRelative) + "\n"
                + "Geomertic Mean;" + ComparisonMetrics.instance().geometricMean(hdDistance, useRelative)) + "\n"
                + "75 Percentile;" + ComparisonMetrics.instance().percentileSeventyFive(hdDistance, useRelative);
    }

    /**
     * Creates average from given model and its mirrored copy.
     *
     * @param m - model to create symmetrical copy of
     * @return symmetrical models
     */
    public Model createSymetricalModel(Model m) {
        Model copy = (Model) m.copy();
        
        createSymetricModelNoCopy(copy);
        
        return copy;
    }
    
    public void createSymetricModelNoCopy(Model m){        
        ProgressHandle p = ProgressHandleFactory.createHandle("Creating symmetrical model...");
        p.start(100);
        Icp.instance().setP(p);
        
        Model mirror = MeshUtils.instance().getMirroredModel(m); 
       
        Icp.instance().icp(new KDTreeIndexed(m.getVerts()), mirror.getVerts(), mirror.getVerts(), 0.05f, 20, false);
        //List<ICPTransformation> trans = FpProcessing.instance().faceRegistration(m);           
        
        p.finish();
           
        Model[] models = new Model[2];
        models[0] = m;
        models[1] = mirror;

        computeAverage(m, models, ICPmetric.VERTEX_TO_VERTEX);
        
        //Icp.instance().reverseAllTransformations(trans, m.getVerts(), true);
    }
    
    public List<File> createSymModelAndSave(List<File> models){
        List<File> savedTo = new LinkedList<>();
        String projectId = "" + System.currentTimeMillis();
        File saveFolder = new File(projectId + File.separator + tmpModuleFile.getName());
        
        try {
            FileUtils.instance().createTMPmoduleFolder(saveFolder);
            
            for(int i = 0; i < models.size(); i++){
            Model m = ModelLoader.instance().loadModel(models.get(i), false, Boolean.TRUE);
            createSymetricModelNoCopy(m);
            
            savedTo.add(ProcessingFileUtils.instance().saveModelToTMP(m, saveFolder, -2, i, false));
        }
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return savedTo;
    }

    public int findMostAvgFace(List<File> models) {
        
        ArrayList<ArrayList<Float>> res = new ArrayList<>(models.size());

        batchFindAvgFace(models, res);

        int mostAvg = -1;
        float leastDif = Float.MAX_VALUE;
        float sum;

        for (int i = 0; i < res.size(); i++) {
            sum = 0;
            for (Float entry : res.get(i)) {
                sum += entry;
            }

            if (sum < leastDif) {
                mostAvg = i;
                leastDif = sum;
            }
        }

        return mostAvg;
    }

    //does same as compute numerical batch results, but without saving the results to disk
    private void batchFindAvgFace(List<File> models, ArrayList<ArrayList<Float>> res) {
        ArrayList<ArrayList<Float>> uncomputedCollumn = new ArrayList<>(models.size());     //list to store raw comparison results
        
        for (File f : models) {
            res.add(new ArrayList<Float>(models.size()));
        }

        for (int i = 0; i < models.size(); i++) {
            uncomputedCollumn.clear();
            Model current = ModelLoader.instance().loadModel(models.get(i), Boolean.FALSE, false);

            KdTree mainFace = new KDTreeIndexed(current.getVerts());

            uncomputedCollumn.addAll(batchRawResultsToSingle(models, i, mainFace, null, false, 1.0f, 0.0f, ComparisonMethod.HAUSDORFF_DIST));
            batchVariance(uncomputedCollumn, res, 0, false);
        }

        p.setDisplayName("Registrating faces...");
    }

    public int findLeastAvgFace(List<File> models) {
  
        ArrayList<ArrayList<Float>> res = new ArrayList<>(models.size());

        batchFindAvgFace(models, res);
        int leastAvg = -1;
        float mostDif = Float.MIN_VALUE;
        float sum;

        for (int i = 0; i < res.size(); i++) {
            sum = 0;
            for (Float entry : res.get(i)) {
                sum += entry;
            }

            if (sum > mostDif) {
                leastAvg = i;
                mostDif = sum;
            }
        }

        return leastAvg;
    }

}
