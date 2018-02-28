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
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.pdm.TrainingModel;
import cz.fidentis.featurepoints.pdm.CSVparsers;
import cz.fidentis.featurepoints.pdm.AreasSearch;
import cz.fidentis.model.Model;
import cz.fidentis.model.corner_table.CornerTable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import jv.vecmath.PdVector;

/**
 *
 * @author Rasto1
 */
public class LandmarkLocalization {
    
    private static final int[] FPTYPES = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
    
    /**
     * Landmark localization method, compute landmarks
     * @param model model for localization
     * @param trainingShapesFiles .csv shapes for training model
     * @return List of 14 landmarks
     */
    public List<FacialPoint> makeArea(Model model, File[] trainingShapesFiles) {

        TrainingModel train = new TrainingModel();

        CSVparsers parser = new CSVparsers();
        List trainingShapes = parser.load3(trainingShapesFiles);

        List<FacialPoint> landmarks = new ArrayList<FacialPoint>();

        // creation of mean shape
        FpModel meanShape = train.trainigModel(trainingShapes, model);
        
        // static mean shape
        FpModel meanShapeStatic = new FpModel();

        meanShapeStatic.setFacialpoints(meanShape.createListFp());

        // creation of mean shape as matrix
        Matrix meanShapeMatrix = new Matrix(meanShape.getPointsNumber(), 3);

        for (int i = 0; i < meanShape.getPointsNumber(); i++) {
            meanShapeMatrix.set(i, 0, meanShape.getFacialPoints().get(i).getPosition().x);
            meanShapeMatrix.set(i, 1, meanShape.getFacialPoints().get(i).getPosition().y);
            meanShapeMatrix.set(i, 2, meanShape.getFacialPoints().get(i).getPosition().z);
        }

        // creation of covariance matrix 
        Matrix covarianceMatrix = train.covarianceMatrixCalculation(meanShapeMatrix, trainingShapes, model);

        // construction of eigen values and eigen vectors, sort by size, eigenValues[i] > eigenValues[i+1]
        Matrix[] eigenValues = train.getEigenValues(9, covarianceMatrix);
        Matrix eigenVectors = eigenValues[1];

        FeaturePointsUniverse fpUni = new FeaturePointsUniverse(model);

        System.out.print(fpUni.getElementSet().getVertices().length + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +"\n");
        
        // simplifying the model
        fpUni.computeSimplify();
        
        AreasSearch search = new AreasSearch();
        
        // nose finding 
        List<FacialPoint> noseVertex = search.finalNoseSearch(fpUni.getElementSet(), fpUni);

        // eye corner finding
        List<FacialPoint> eyeVertices = search.eyeCornerSerach(fpUni.getElementSet(), fpUni, noseVertex.get(0).getPosition(), model.getModelDims());
        
        // splitting eye corner on left and right
        Vector3f left = new Vector3f();
        Vector3f right = new Vector3f();

        if (eyeVertices.get(0).getPosition().x < eyeVertices.get(1).getPosition().x) {
            left = eyeVertices.get(1).getPosition();
            right = eyeVertices.get(0).getPosition();
        } else {
            left = eyeVertices.get(0).getPosition();
            right = eyeVertices.get(1).getPosition();
        }
        
        KdTreeIndexed kdCko = new KdTreeIndexed(model.getVerts());

        // find candidate areas
        List<Vector3f> nose = findAreaCandidates(noseVertex.get(0).getPosition(), kdCko, model);
        List<Vector3f> eyeL = findAreaCandidates(left, kdCko, model);
        List<Vector3f> eyeR = findAreaCandidates(right, kdCko, model);
        
        List<FacialPoint> listOfFacials = new ArrayList<FacialPoint>();
        FacialPoint rCanFacial = new FacialPoint(3, new PdVector());
        FacialPoint lCanFacial = new FacialPoint(4, new PdVector());
        FacialPoint nCanFacial = new FacialPoint(14, new PdVector());

        List<Vector3f> newVertices = new ArrayList<>();
        List<Vector3f> newVerticesTmp = new ArrayList<>();

        // kd tree
        float bMinimum = 9999;

        Matrix finalBVector = null;

        List<ICPTransformation> transMatrix = new ArrayList<ICPTransformation>();
        List<ICPTransformation> finalTransMatrix = new ArrayList<ICPTransformation>();

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
                    listOfFacials = new ArrayList<FacialPoint>();

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
                    transMatrix = proc2.doProcrustesAnalysis(proc, false);

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

                        Matrix newVertex = transMatrix.get(2).getTransMatrix().inverse().times(newTmp);

                        newVerticesTmp.add(new Vector3f((float) newVertex.get(0, 0) + (transMatrix.get(1).getTranslation().x * -1), (float) newVertex.get(1, 0) + (transMatrix.get(1).getTranslation().y * -1), (float) newVertex.get(2, 0) + (transMatrix.get(1).getTranslation().z * -1)));
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

            lol.set(0, 0, lol.get(0, 0) + (finalTransMatrix.get(0).getTranslation().x * -1));
            lol.set(0, 1, lol.get(0, 1) + (finalTransMatrix.get(0).getTranslation().y * -1));
            lol.set(0, 2, lol.get(0, 2) + (finalTransMatrix.get(0).getTranslation().z * -1));

            finalVertices.setMatrix(i, i, 0, 2, lol);
        }

        int currentType = 0;
        
        // set final vertices
        for (int i = 0; i < finalVertices.getRowDimension(); i++) {
            landmarks.add(new FacialPoint(FPTYPES[currentType], kdCko.nearestNeighbour(new Vector3f((float) finalVertices.get(i, 0), (float) finalVertices.get(i, 1), (float) finalVertices.get(i, 2)))));
            currentType++;
        }

        return landmarks;
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
}
