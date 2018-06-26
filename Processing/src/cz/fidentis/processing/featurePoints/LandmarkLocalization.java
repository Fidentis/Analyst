/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;


import Jama.Matrix;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.corner_table.CornerTable;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import jv.geom.PgElementSet;
import jv.vecmath.PdVector;
import org.openide.util.Exceptions;

/**
 *
 * @author Rasto1
 */
public class LandmarkLocalization {

    private static File choosedTrainingModel = null;
    private static LandmarkLocalization instance = null;
          
    private LandmarkLocalization(){

    }
    
    public static LandmarkLocalization instance(){
        if(instance == null)
            instance = new LandmarkLocalization();
        
        return instance;
    }

    
    public List<FacialPoint> localizationOfLandmarks(Model model, PDM usedPdm) {
        
         FeaturePointsUniverse fpUni = new FeaturePointsUniverse(model);

        // simplifying the model
        fpUni.computeSimplify();
        
        AreasSearch search = new AreasSearch();
        // nose finding
        List<FacialPoint> noseVertex = search.finalNoseSearch(fpUni.getElementSet(), fpUni);

        // eye corner finding
        List<FacialPoint> eyeVertices = search.eyeCornerSerach(fpUni.getElementSet(), fpUni, noseVertex.get(0).getPosition(), model.getModelDims());
        
        // splitting eye corner on left and right
        Vector3f left = null;
        Vector3f right = null;

        //TODO: Fix based on dot product (?)
        if (eyeVertices.get(0).getPosition().x < eyeVertices.get(1).getPosition().x) {
            left = eyeVertices.get(1).getPosition();
            right = eyeVertices.get(0).getPosition();
        } else {
            left = eyeVertices.get(0).getPosition();
            right = eyeVertices.get(1).getPosition();
        }
        return findLandmarks(usedPdm, left, right, noseVertex.get(0).getPosition(), fpUni);
     }

    private List<FacialPoint> findLandmarks(PDM usedPdm, Vector3f enl, Vector3f enr, Vector3f prn, FeaturePointsUniverse fpUni) {
        List<FacialPoint> landmarks = new ArrayList<FacialPoint>();

        // static mean shape
        FpModel meanShapeStatic = new FpModel();
        FpModel meanShape = new FpModel();
        meanShape.setFacialpoints(usedPdm.getMeanShape().createListFp());
        meanShapeStatic.setFacialpoints(usedPdm.getMeanShape().createListFp());

        Matrix eigenVectors = usedPdm.getEigenVectors();

       // creation of mean shape as matrix
        Matrix meanShapeMatrix = new Matrix(meanShape.getPointsNumber(), 3);

        for (int i = 0; i < meanShape.getPointsNumber(); i++) {
            meanShapeMatrix.set(i, 0, meanShape.getFacialPoints().get(i).getPosition().x);
            meanShapeMatrix.set(i, 1, meanShape.getFacialPoints().get(i).getPosition().y);
            meanShapeMatrix.set(i, 2, meanShape.getFacialPoints().get(i).getPosition().z);
        }

        List<Vector3f> simplifiedModel = new ArrayList<>();
        PgElementSet set = fpUni.getElementSet();

        for (int j = 0; j < set.getVertices().length; j++) {

            Vector3f v = new Vector3f((float) set.getVertex(j).getEntry(0), (float) set.getVertex(j).getEntry(1), (float) set.getVertex(j).getEntry(2));
            simplifiedModel.add(v);
        }



        KdTreeIndexed kdCko = new KdTreeIndexed(simplifiedModel);

        // find candidate areas
        List<Vector3f> nose = findAreaCandidates(prn, kdCko, fpUni, simplifiedModel);
        List<Vector3f> eyeL = findAreaCandidates(enl, kdCko, fpUni, simplifiedModel);
        List<Vector3f> eyeR = findAreaCandidates(enr, kdCko, fpUni, simplifiedModel);

        List<FacialPoint> listOfFacials;
        FacialPoint rCanFacial = new FacialPoint(FacialPointType.EN_R.ordinal(), new PdVector());
        FacialPoint lCanFacial = new FacialPoint(FacialPointType.EN_L.ordinal(), new PdVector());
        FacialPoint nCanFacial = new FacialPoint(FacialPointType.PRN.ordinal(), new PdVector());

        List<Vector3f> newVertices;
        List<Vector3f> newVerticesTmp;

        // kd tree
        float bMinimum = 9999;

        Matrix finalBVector = null;

        List<ICPTransformation> transMatrix;
        List<ICPTransformation> finalTransMatrix = new ArrayList<>();

        // procrustes analysis on mean shape
        ProcrustesAnalysis proc = new ProcrustesAnalysis(meanShape.getFacialPoints());

        for (int z = 0; z < eyeR.size(); z++) {
            Vector3f rCan = eyeR.get(z);

            for (int x = 0; x < eyeL.size(); x++) {
                Vector3f lCan = eyeL.get(x);

                for (int c = 0; c < nose.size(); c++) {
                    Vector3f nCan = nose.get(c);

                    newVertices = new ArrayList<>();
                    newVerticesTmp = new ArrayList<>();
                    listOfFacials = new ArrayList<>();

                    // candidates conversion to facial points
                    rCanFacial.setCoords(rCan);
                    lCanFacial.setCoords(lCan);
                    nCanFacial.setCoords(nCan);

                    listOfFacials.add(rCanFacial);
                    listOfFacials.add(lCanFacial);
                    listOfFacials.add(nCanFacial);

                    // procustes analysis on set of candidates
                    ProcrustesAnalysis proc2 = new ProcrustesAnalysis(listOfFacials);

                    // application of transforms on models
                    transMatrix = proc2.superimposePDM(proc, false);

                    // change mean shape to default
                    meanShape.getFacialPoints().clear();
                    meanShape.setFacialpoints(meanShapeStatic.createListFp());

                    // find closest neighbours to new transformed model
                    for (int i = 0; i < proc.getFacialPoints().size(); i++) {
                        newVertices.add(kdCko.nearestNeighbour(new Vector3f(proc.getFacialPoints().get(i).getPosition().x + (transMatrix.get(0).getTranslation().x * -1), proc.getFacialPoints().get(i).getPosition().y + (transMatrix.get(0).getTranslation().y * -1), proc.getFacialPoints().get(i).getPosition().z + (transMatrix.get(0).getTranslation().z * -1))));
                    }

                    proc.updateFacialPoints(meanShape.getFacialPoints());

                    // application of inverse transformation on new points
                    for (int i = 0; i < newVertices.size(); i++) {

                        Matrix newTmp = new Matrix(new double[][]{{newVertices.get(i).x + transMatrix.get(0).getTranslation().x}, {newVertices.get(i).y + transMatrix.get(0).getTranslation().y}, {newVertices.get(i).z + transMatrix.get(0).getTranslation().z}});

                        Matrix newVertex = newTmp.transpose().times(transMatrix.get(2).getTransMatrix().inverse()).transpose();

                        newVerticesTmp.add(new Vector3f((float) newVertex.get(0, 0) - transMatrix.get(1).getTranslation().x, (float) newVertex.get(1, 0) - transMatrix.get(1).getTranslation().y, (float) newVertex.get(2, 0) - transMatrix.get(1).getTranslation().z));
                    }

                    // b vector computation
                    Matrix newVerticesMatrix = new Matrix(newVerticesTmp.size(), 3);

                    for (int i = 0; i < newVerticesTmp.size(); i++) {
                        newVerticesMatrix.set(i, 0, newVerticesTmp.get(i).x - meanShapeStatic.getFacialPoints().get(i).getPosition().x);
                        newVerticesMatrix.set(i, 1, newVerticesTmp.get(i).y - meanShapeStatic.getFacialPoints().get(i).getPosition().y);
                        newVerticesMatrix.set(i, 2, newVerticesTmp.get(i).z - meanShapeStatic.getFacialPoints().get(i).getPosition().z);
                    }

                    Matrix bVectorMatrix = eigenVectors.times(newVerticesMatrix);

                    // finding b vector with smallest sum
                    float[] bMinValue = new float[bVectorMatrix.getRowDimension()];
                    boolean notIn = false;

                    for (int i = 0; i < bVectorMatrix.getRowDimension(); i++) {
                        for (int j = 0; j < bVectorMatrix.getColumnDimension(); j++) {

                            bMinValue[i] += Math.sqrt(bVectorMatrix.get(i, j) * bVectorMatrix.get(i, j));

                        }

                    }

                    float sum = 0;

                    for (int i = 0; i < bMinValue.length; i++) {
                        sum += bMinValue[i];
                    }

                    if (sum < bMinimum && !notIn) {
                        finalTransMatrix = transMatrix;
                        finalBVector = bVectorMatrix;
                        bMinimum = sum;

                    }

                }

            }

        }

        // final vertices computation
        Matrix finalVertices = meanShapeMatrix.plus(eigenVectors.transpose().times(finalBVector));

        for (int i = 0; i < finalVertices.getRowDimension(); i++) {
            finalVertices.set(i, 0, finalVertices.get(i, 0) + finalTransMatrix.get(1).getTranslation().x);
            finalVertices.set(i, 1, finalVertices.get(i, 1) + finalTransMatrix.get(1).getTranslation().y);
            finalVertices.set(i, 2, finalVertices.get(i, 2) + finalTransMatrix.get(1).getTranslation().z);

            Matrix lol = finalVertices.getMatrix(i, i, 0, 2).times(finalTransMatrix.get(2).getTransMatrix());

            lol.set(0, 0, lol.get(0, 0) - finalTransMatrix.get(0).getTranslation().x);
            lol.set(0, 1, lol.get(0, 1) - finalTransMatrix.get(0).getTranslation().y);
            lol.set(0, 2, lol.get(0, 2) - finalTransMatrix.get(0).getTranslation().z);

            finalVertices.setMatrix(i, i, 0, 2, lol);
        }

        List<FacialPoint> usedLandmarks = usedPdm.getMeanShape().getFacialPoints();

        // set final vertices
        for (int i = 0; i < finalVertices.getRowDimension(); i++) {
            landmarks.add(new FacialPoint(usedLandmarks.get(i).getType(), kdCko.nearestNeighbour(new Vector3f((float) finalVertices.get(i, 0), (float) finalVertices.get(i, 1), (float) finalVertices.get(i, 2)))));
        }

        return landmarks;
    }
        
    public List<FacialPoint> landmarkDetectionTexture(Model m, PDM usedPdm){
        try {
            //detect landmarks in 2D
            String saved = TextureLandmarks.instance().detectTextureLandmarks(new File(m.getMatrials().getMatrials().get(0).getTextureFile()));
            //convert 2D landmarks to 3D
            FpModel landmarksIn3D = TextureLandmarks.instance().convert2Dto3D(saved, m);
            Vector3f enl = landmarksIn3D.getFacialPoint(FacialPointType.EN_L.ordinal()).getPosition();
            Vector3f enr = landmarksIn3D.getFacialPoint(FacialPointType.EN_R.ordinal()).getPosition();
            
            //delete tmp file
            new File(saved).delete();
            
            return localizationOfLandmarks(m, usedPdm, enl, enr);
        } catch (ParserConfigurationException | TransformerException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return null;
    }
    
    private List<FacialPoint> localizationOfLandmarks(Model model, PDM usedPdm, Vector3f enl, Vector3f enr) {
                
         FeaturePointsUniverse fpUni = new FeaturePointsUniverse(model);

        // simplifying the model
        fpUni.computeSimplify();
        
        AreasSearch search = new AreasSearch();
        // nose finding
        List<FacialPoint> noseVertex = search.finalNoseSearch(fpUni.getElementSet(), fpUni);

        return findLandmarks(usedPdm, enl, enr, noseVertex.get(0).getPosition(), fpUni);
    }
    
        /**
     * method finds second neighbour vertices
     *
     * @param in main vector for finding
     * @return list of neighbour vertices
     */
    private List<Vector3f> findAreaCandidates(Vector3f in, KdTreeIndexed kdCko, Model model) {
        int targetVector = 0;

        targetVector = kdCko.nearestIndex(in);

        CornerTable neighbors = new CornerTable(model);
        int[] mainNeighbours = neighbors.getIndexNeighbors(targetVector, model);

        List<Vector3f> areaCandidates = new ArrayList<>();
        areaCandidates.add(model.getVerts().get(targetVector));

        for (int b = 0; b < mainNeighbours.length; b++) {
            int[] secondNeighbours = neighbors.getIndexNeighbors(mainNeighbours[b], model);

            areaCandidates.add(model.getVerts().get(mainNeighbours[b]));

            for (int m = 0; m < secondNeighbours.length; m++) {
                if (!areaCandidates.contains(model.getVerts().get(secondNeighbours[m]))) {
                    areaCandidates.add(model.getVerts().get(secondNeighbours[m]));
                }
            }

        }

        return areaCandidates;
    }
    
     private List<Vector3f> findAreaCandidates(Vector3f in, KdTreeIndexed kdCko, FeaturePointsUniverse set, List<Vector3f> simplifiedModel) {
        int targetVector = 0;
        PgElementSet s = set.getElementSet();

        targetVector = kdCko.nearestIndex(in);

        CornerTable neighbors = set.getCornerTable();
        int[] mainNeighbours = neighbors.getIndexNeighbors(targetVector);

        List<Vector3f> areaCandidates = new ArrayList<>();
        
        
        
        areaCandidates.add(simplifiedModel.get(targetVector));

        for (int b = 0; b < mainNeighbours.length; b++) {
            int[] secondNeighbours = neighbors.getIndexNeighbors(mainNeighbours[b]);

            areaCandidates.add(simplifiedModel.get(mainNeighbours[b]));

            for (int m = 0; m < secondNeighbours.length; m++) {
                if (!areaCandidates.contains(simplifiedModel.get(secondNeighbours[m]))) {
                    areaCandidates.add(simplifiedModel.get(secondNeighbours[m]));
                }
            }

        }

        return areaCandidates;
    }
    
    // calculate laplacian smoothing
    public ArrayList<Vector3f> laplacianSmoothing(Model model) {
        CornerTable neighbors = new CornerTable(model);
        ArrayList<Vector3f> newVerts = new ArrayList<>();

        for (int i = 0; i < model.getVerts().size(); i++) {

            int[] currentNeighbors = neighbors.getIndexNeighbors(i, model);
            Vector3f sumVec = new Vector3f(0, 0, 0);
            Vector3f current = model.getVerts().get(i);

            for (int j = 0; j < currentNeighbors.length; j++) {
                sumVec.x += ((model.getVerts().get(currentNeighbors[j]).x - current.x) / currentNeighbors.length);
                sumVec.y += ((model.getVerts().get(currentNeighbors[j]).y - current.y) / currentNeighbors.length);
                sumVec.z += ((model.getVerts().get(currentNeighbors[j]).z - current.z) / currentNeighbors.length);
            }

            newVerts.add(new Vector3f(current.x + sumVec.x, current.y + sumVec.y, current.z + sumVec.z));
        }

        return newVerts;
    }
    
    //method for loading sets of models for training purposes
    public static List<FpModel> loadTrainingSets(File[] listOfFiles, int[] fpTypes) {
        List<FpModel> trainingShapes = new ArrayList<>();
       
        CSVparser pars = new CSVparser();
                
        List<FpModel> models = pars.load(listOfFiles[0].getPath());
                
        for(int i = 0; i < models.size(); i++){
            FpModel tmp = new FpModel();
                
            for(int j = 0; j < fpTypes.length; j++){
                    tmp.addFacialPoint(models.get(i).getFacialPoint(fpTypes[j]));
            }
            
            trainingShapes.add(tmp);
        }
               
        return trainingShapes;
    }
    
    public static void setTrainingModel(String str) throws IOException{
        choosedTrainingModel = new File(new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + "trainingModels" + separatorChar + str);
    }
    
    public File getTrainingModel(){
        return choosedTrainingModel;
    }
}
