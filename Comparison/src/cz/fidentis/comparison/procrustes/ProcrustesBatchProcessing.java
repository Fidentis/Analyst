/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.comparison.procrustes;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.featurepoints.FacialPoint;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana LÝOVÁ
 */
public class ProcrustesBatchProcessing {
    private GPA gpa;
    private boolean scaling = true;

    
    public ProcrustesBatchProcessing(List<List<FacialPoint>> fps, boolean scaling) {
         for(int j = 0; j < fps.size(); j++){
            if(fps.get(j) == null){
                throw new IllegalArgumentException("Facial points not set.");
            }
        }
        
        this.gpa = new GPA();
        this.gpa.setScaling(scaling);
        for(int i = 0; i < fps.size(); i++){
            this.gpa.addPA(new ProcrustesAnalysis(fps.get(i)));
        }
        this.scaling = scaling;
        System.out.println(this.scaling);
    }
    
    public ProcrustesBatchProcessing(List<List<FacialPoint>> fps, List <ArrayList<Vector3f>> modelsVerts, boolean scaling) {
         for(int j = 0; j < fps.size(); j++){
            if(fps.get(j) == null){
                throw new IllegalArgumentException("Facial points not set.");
            }
        }
        
        this.gpa = new GPA();
        for(int i = 0; i < fps.size(); i++){
            this.gpa.addPA(new ProcrustesAnalysis(fps.get(i),modelsVerts.get(i)));
        }
        this.scaling = scaling;
        System.out.println(this.scaling);
    }
    
    /**
     * This method does data analysis
     * 
     * @param treshold      treshold for ending GPA
     * @return              numerical result which is Procrustes distance between every pair of configurations
     */  
    public String doBatchProcessing(float treshold){
        String distances = " ;";
        double[] arrayOfDistances;
                
        gpa.setScaling(scaling);
        gpa.doGPA(treshold);
        
        arrayOfDistances = gpa.createArrayOfDistances();
        
        for(int i = 0; i < gpa.getConfigs().size(); i++){
            distances = distances.concat((i+1) + ";");
        }
        distances = distances.concat("\n");
        
        for(int i = 0; i < gpa.getConfigs().size(); i++){
            distances = distances.concat((i+1) + ";");
            for(int j = 0; j < gpa.getConfigs().size(); j++){
                distances = distances.concat(arrayOfDistances[i*gpa.getConfigs().size()+j] + ";");
            }
            distances = distances.concat("\n");
        }
        
        return distances;
    }
    
    /**
     * Performs GPA on all configurations passed to class in constructor.
     * 
     * @param treshold - to determine when to stop GPA alignment
     */
    public List<List<ICPTransformation>> alignBatch(float treshold){
        gpa.setScaling(scaling);
        return gpa.doGPA(treshold);
    }
    
    /**
     * 
     * 
     * @return 
     */
    public String compareBatch(List<File> models){
        String distances = " ;";
        double[] arrayOfDistances;
        
        arrayOfDistances = gpa.createArrayOfDistances();
        
        for(int i = 0; i < gpa.getConfigs().size(); i++){
            distances = distances.concat(models.get(i).getName() + ";");
        }
        distances = distances.concat("\n");
        
        for(int i = 0; i < gpa.getConfigs().size(); i++){
            distances = distances.concat((models.get(i).getName()) + ";");
            for(int j = 0; j < gpa.getConfigs().size(); j++){
                distances = distances.concat(arrayOfDistances[i*gpa.getConfigs().size()+j] + ";");
            }
            distances = distances.concat("\n");
        }
        
        return distances;
    }

    public GPA getGpa() {
        return gpa;
    }
    
    /**
     * Computes distance to mean configuration for each configuration and returns results as string
     * @return distance of each configuration to mean configuration
     */
    public String distanceToMean(){
        ProcrustesAnalysis meanConfig = gpa.countMeanConfig();
        StringBuilder sb = new StringBuilder("");
        
        for(int i = 0; i < gpa.getConfigs().size(); i++){
            sb.append(i+1).append(";");
        }
        
        sb.append(System.lineSeparator());
        
        for(ProcrustesAnalysis pa : gpa.getConfigs()){
            sb.append(pa.countDistance(meanConfig, scaling)).append(";");
        }
        
        return sb.toString();
    }
    
}
