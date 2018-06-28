/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.comparison.procrustes.ProcrustesBatchProcessing;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.model.Model;
import cz.fidentis.utils.SortUtils;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Rasto1
 */
public class TrainingModel {

    private static TrainingModel instance = null;
    private static final Integer[] USED_LM = new Integer[]{FacialPointType.EX_R.ordinal(), FacialPointType.EX_L.ordinal(), FacialPointType.EN_R.ordinal(), FacialPointType.EN_L.ordinal(), 
        FacialPointType.PAS_R.ordinal(), FacialPointType.PAS_L.ordinal(), FacialPointType.PAI_R.ordinal(), FacialPointType.PAI_L.ordinal(), FacialPointType.G.ordinal(),
        FacialPointType.SN.ordinal(),FacialPointType.AL_L.ordinal(), FacialPointType.AL_R.ordinal(), FacialPointType.N.ordinal(), FacialPointType.PRN.ordinal()
     }; //USED LM HAVE TO BE SORTED BY INDEX RIGHT NOW!! (probably won't work if some index is skipped)

    private TrainingModel() {
    }

    public static TrainingModel instance(){
        if(instance == null){
            instance = new TrainingModel();
        }
        
        return instance;
    }
    
   
    /**
     * compute mean shape from .csv shapes
     * @param trainingShapes list of training shapes
     * @param m model
     * @return mean shape as FpModel
     */
    public PDM trainigModel(List<FpModel> trainingShapes, Model m, Integer[] fpTypes) {
        if(fpTypes == null)
            fpTypes = USED_LM;
        
        FpModel meanShape = createMeanModel((FpModel) trainingShapes.get(0), fpTypes);

        meanShape.centralizeToModel(m);
        
        // MEAN SHAPE
        for (int i = 1; i < trainingShapes.size(); i++) {

            FpModel values = trainingShapes.get(i);

            values.centralizeToModel(m);

            addToMeanModel( meanShape, values, fpTypes);
        }

        computeAverage(meanShape, trainingShapes.size());
        
        Matrix covarianceMatrix = covarianceMatrixFromData(meanShape, trainingShapes, fpTypes);

        // construction of eigen values and eigen vectors, sort by size, eigenValues[i] > eigenValues[i+1]
        int vals = (int) (meanShape.getPointsNumber() * 0.98);
        Matrix[] eigenValues = getEigenValues(vals, covarianceMatrix);
        Matrix eigenVectors = eigenValues[1];
        
        PDM pdm = new PDM(meanShape, eigenValues[0], eigenVectors);

        return pdm;
    }
    
    //TODO check for missing landmarks
    private FpModel createMeanModel(FpModel base, Integer[] fpTypes){
        FpModel mean = new FpModel();
        List<FacialPoint> fps = new ArrayList<>(fpTypes.length);
        
        for(Integer i : fpTypes){
            FacialPoint fp = new FacialPoint(i, base.getFacialPoint(i).getPosition());
            fps.add(fp);
        }
        
        mean.setFacialpoints(fps);
        return mean;
    }

    private Matrix covarianceMatrixFromData(FpModel meanShape, List<FpModel> trainingShapes, Integer[] fpTypes) {
        //EIGENVALUES AND EIGENVECTORS
        
        // creation of mean shape as matrix
        Matrix meanShapeMatrix = new Matrix(meanShape.getPointsNumber(), 3);
        for (int i = 0; i < meanShape.getPointsNumber(); i++) {
            meanShapeMatrix.set(i, 0, meanShape.getFacialPoints().get(i).getPosition().x);
            meanShapeMatrix.set(i, 1, meanShape.getFacialPoints().get(i).getPosition().y);
            meanShapeMatrix.set(i, 2, meanShape.getFacialPoints().get(i).getPosition().z);
        }
        // creation of covariance matrix 
        Matrix covarianceMatrix = covarianceMatrixCalculation(meanShapeMatrix, trainingShapes, fpTypes);
        return covarianceMatrix;
    }
    
    public PDM trainingModel(List<FpModel> trainingShapes, Integer[] fpTypes){
        if(fpTypes == null)
            fpTypes = USED_LM;
        
        FpModel meanShape = createMeanModel(trainingShapes.get(0), fpTypes);
        FpModel meanShapeStatic = new FpModel(meanShape.getModelName());
        meanShapeStatic.setFacialpoints(meanShape.createListFp());
        
        for(int i = 1; i < trainingShapes.size(); i++){
            FpModel trainingShape = trainingShapes.get(i);
            
            ProcrustesAnalysis main = new ProcrustesAnalysis(meanShapeStatic.createListFp());
            ProcrustesAnalysis comp = new ProcrustesAnalysis(trainingShape.getFacialPoints());
            
            comp.doProcrustesAnalysis(main, false);
            
            //TODO check if training model fp change after procrustes
            addToMeanModel(meanShape, trainingShape, fpTypes);
            
        }
        
        computeAverage(meanShape, trainingShapes.size());
        
        Matrix covarianceMatrix = covarianceMatrixFromData(meanShape, trainingShapes, fpTypes);

        // construction of eigen values and eigen vectors, sort by size, eigenValues[i] > eigenValues[i+1]
        int vals = (int) (meanShape.getPointsNumber() * 0.98);
        Matrix[] eigenValues = getEigenValues(vals, covarianceMatrix);
        Matrix eigenVectors = eigenValues[1];
        
        PDM pdm = new PDM(meanShape, eigenVectors, eigenValues[0]);

        return pdm;
    }
    
    public List<FpModel> trainigModel(List<List<FacialPoint>> trainingShapes, List<ArrayList<Vector3f>> trainingVerts) {

        ProcrustesBatchProcessing proc = new ProcrustesBatchProcessing(trainingShapes, trainingVerts, false);
        proc.alignBatch(0.05f);
        
        proc.getGpa().superimpose();
        List<FpModel> meanShape = new ArrayList<>();
        FpModel ms = new FpModel();
        ms.setFacialpoints(proc.getGpa().countMeanConfig().getFacialPoints());
        
        for(int i = 0 ; i < proc.getGpa().getConfigs().size(); i++){
            FpModel alignedConf = new FpModel();
            alignedConf.setFacialpoints(proc.getGpa().getPA(i).getFacialPoints());
            meanShape.add(alignedConf);
        }

        return meanShape;
    }
    
    private void addToMeanModel(FpModel meanShape, FpModel trainingShape, Integer[] fpTypes) {

        for (Integer j : fpTypes) {
            FacialPoint point = meanShape.getFacialPoint(j);

            point.getPosition().x += trainingShape.getFacialPoint(j).getPosition().x;
            point.getPosition().y += trainingShape.getFacialPoint(j).getPosition().y;
            point.getPosition().z += trainingShape.getFacialPoint(j).getPosition().z;
        }
    }
    
    private void computeAverage(FpModel meanShape, int numOfTrainingShapes){
        
        for (int j = 0; j < meanShape.getPointsNumber(); j++) {

            FacialPoint point = meanShape.getFacialPoints().get(j);

            point.getPosition().x /= numOfTrainingShapes;
            point.getPosition().y /= numOfTrainingShapes;
            point.getPosition().z /= numOfTrainingShapes;
        }
    }

    /**
     * construct covariance matrix from mean shape and training shapes
     * @param meanShape 
     * @param trainingShapes
     * @param m model
     * @return covariance matrix
     */
    public Matrix covarianceMatrixCalculation(Matrix meanShape, List trainingShapes, Integer[] fpTypes) {

        Matrix covariance = new Matrix(meanShape.getRowDimension(), meanShape.getRowDimension());

        for (int i = 0; i < trainingShapes.size(); i++) {
            FpModel values = (FpModel) trainingShapes.get(i);

            Matrix valuesMatrix = new Matrix(fpTypes.length, 3);

            for (int j = 0; j < fpTypes.length; j++) {
                valuesMatrix.set(j, 0, values.getFacialPoint(fpTypes[j]).getPosition().x);
                valuesMatrix.set(j, 1, values.getFacialPoint(fpTypes[j]).getPosition().y);
                valuesMatrix.set(j, 2, values.getFacialPoint(fpTypes[j]).getPosition().z);
            }

            Matrix shapeSubtract = valuesMatrix.minus(meanShape);

            covariance = covariance.plus(shapeSubtract.times(shapeSubtract.transpose()));
        }

        double[][] tmp = covariance.getArray();

        if(trainingShapes.size() != 1){
        
        for (int j = 0; j < tmp.length; j++) {
            for (int k = 0; k < tmp[j].length; k++) {
                tmp[j][k] /= (trainingShapes.size() - 1);
            }
        }

        }
        return new Matrix(tmp);
    }

    /**
     * return first i-th eigenVectors from covariance matrix based on eigenvalue, where value(i) >= value(i+1)
     * @param i number of eigenVectors
     * @param covariance
     * @return first i-th matrix of eigenVectors
     */
    public Matrix getEigenVectors(int i, Matrix covariance) {
        EigenvalueDecomposition eigen = covariance.eig();
        double[][] vectors = eigen.getV().getArray();

        return new Matrix(vectors);
    }

    /**
     * return first i-th eigenValues from covariance matrix, where value(i) >= value(i+1)
     * @param i number of eigen values
     * @param covariance covariance matrix
     * @return first i-th matrix of eigen values
     */
    public Matrix[] getEigenValues(int i, Matrix covariance) {
        EigenvalueDecomposition eigen = covariance.eig();
        Matrix[] matr = new Matrix[2];
        double[] values = eigen.getRealEigenvalues();
        double[][] values2 = eigen.getV().transpose().getArray();
        SortUtils.instance().quickSort(values, values2);

        double[][] eigenValues = new double[1][i];
        double[][] eigenVectors = new double[i][values2[0].length];
        int tmp = 0;

        for (int k = values.length - 1; k > values.length - 1 - i; k--) {

            eigenValues[0][tmp] = values[k];
            tmp++;
        }
        tmp = 0;
        for (int k = values2.length - 1; k > values2.length - 1 - i; k--) {
            for (int l = 0; l < values2[k].length; l++) {
                eigenVectors[tmp][l] = values2[k][l];
            }
            tmp++;
        }

        matr[0] = new Matrix(eigenValues);
        matr[1] = new Matrix(eigenVectors);

        return matr;
    }

}
