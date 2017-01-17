/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.ageing;

import Jama.Matrix;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.comparison.procrustes.Procrustes1ToMany;
import cz.fidentis.comparison.procrustes.Procrustes2Models;
import cz.fidentis.controller.Gender;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.landmarkParser.FPparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;

/**
 *
 * @author Marek Zuzi
 */
public class AgeingComputation {
    private Model model;
    private List<FacialPoint> modelPoints;
    private double age;
    private Gender gender;
    
    private double target;
    private Model targetModel;
    private List<FacialPoint> targetPoints;
    
    
    
    private AgeingComputation() {}
    
    public AgeingComputation(Model model, List<FacialPoint> modelPoints, Gender gender, double age) {
        this.model = model;
        this.gender = gender;
        this.age = age;
        this.modelPoints = modelPoints;
    }
    
    public void computeToAge(double targetAge) {
        this.target = targetAge;
        
        FpModel alignToPoints = getMeanModel(gender, age);
        ArrayList<Vector3f> templateVerts = new ArrayList<>();
        for(FacialPoint p : alignToPoints.getFacialPoints()) {
            templateVerts.add(p.getPosition());
        }
        ArrayList<List<FacialPoint>> modelPointsList = new ArrayList<>(1);
        ArrayList<ArrayList<Vector3f>> models = new ArrayList<>(1);
        models.add(model.getVerts());
        modelPointsList.add(modelPoints);
        
        Procrustes1ToMany proc = new Procrustes1ToMany(alignToPoints.getFacialPoints(), templateVerts, modelPointsList, models, false);
        proc.align1withN();
        model.setVerts(proc.getPa2().get(0).getVertices());
        
        targetPoints = getMeanModel(gender, targetAge).getFacialPoints();
 
        targetModel = ModelLoader.instance().loadModel(model.getFile(), false, false);
        targetModel.setVerts(new ArrayList<>(model.getVerts()));
        transform(targetModel, modelPoints, targetPoints);
    }
    
    private FpModel getMeanModel(Gender gender, double age) {
        List<FpModel> allPoints = CSVparser.load(new File(".").getAbsolutePath() + File.separator + "models" + File.separator + "resources" + File.separator + "ages.csv");
        
        String basePath = "";
        if(gender == Gender.MALE) {
            basePath = "average_boy_";
        } else {
            basePath = "average_girl_";
        }
        
        if(age < 12) {
            basePath = basePath + "1";
        } else if(age < 20) {
            basePath = basePath + "2";
        } else {
            basePath = basePath + "3";
        }
        
        for(FpModel m : allPoints) {
            if (m.getModelName().equals(basePath)) {
                return m;
            }
        }
        return allPoints.get(0);
    }
    
    private void transform(Model model, List<FacialPoint> modelPts, List<FacialPoint> targetPts) {
        List<FacialPoint> modelPoints = new ArrayList<>();
        List<FacialPoint> targetPoints = new ArrayList<>();
        for (FacialPoint point : modelPts) {
            FacialPoint g = null;
            for (FacialPoint gemPoint : targetPts) {
                if (gemPoint.getType() == point.getType()) {
                    g = gemPoint;
                }
            }

            if (g != null) {
                modelPoints.add(point);
                targetPoints.add(g);
            }
        }
        
        Matrix CtrlPts = new Matrix(modelPoints.size()+4, 3, 0);
        Matrix L = new Matrix(modelPoints.size()+4, modelPoints.size()+4, 0);
        
        for(int i=0;i<modelPoints.size();i++) {
            for(int j=i;j<modelPoints.size();j++) {
                Vector3f x = new Vector3f(modelPoints.get(i).getPosition());
                x.sub(modelPoints.get(j).getPosition());
                L.set(i, j, x.length());
                L.set(j, i, L.get(i,j));
                L.set(i, modelPoints.size()+0, 1);
                L.set(i, modelPoints.size()+1, targetPoints.get(i).getPosition().x);
                L.set(i, modelPoints.size()+2, targetPoints.get(i).getPosition().y);
                L.set(i, modelPoints.size()+3, targetPoints.get(i).getPosition().z);
                L.set(modelPoints.size()+0, i, 1);
                L.set(modelPoints.size()+1, i, targetPoints.get(i).getPosition().x);
                L.set(modelPoints.size()+2, i, targetPoints.get(i).getPosition().y);
                L.set(modelPoints.size()+3, i, targetPoints.get(i).getPosition().z);
                CtrlPts.set(i, 0, targetPoints.get(i).getPosition().x);
                CtrlPts.set(i, 1, targetPoints.get(i).getPosition().y);
                CtrlPts.set(i, 2, targetPoints.get(i).getPosition().z);
            }
        }
        Matrix Param = L.inverse().times(CtrlPts);
        Matrix P = new Matrix(model.getVerts().size(), modelPoints.size() + 4);
        for(int i = 0;i<model.getVerts().size();i++) {
            for(int j=0;j<modelPoints.size();j++) {
                Vector3f x = modelPoints.get(j).getPosition();
                x.sub(model.getVerts().get(i));
                P.set(i,j, x.length());
            }
            P.set(i, modelPoints.size()+0, 1);
            P.set(i, modelPoints.size()+1, model.getVerts().get(i).x);
            P.set(i, modelPoints.size()+2, model.getVerts().get(i).y);
            P.set(i, modelPoints.size()+3, model.getVerts().get(i).z);
        }
        
        Matrix result = P.times(Param);
        for(int i = 0;i<model.getVerts().size();i++) {
            model.getVerts().set(i, new Vector3f((float)result.get(i, 0), (float)result.get(i, 1), (float)result.get(i, 2)));
        }
    }
    
    public Model getTargetModel() {
        return targetModel;
    }
    
    public List<FacialPoint> getTargetPoints() {
        return targetPoints;
    }
}
