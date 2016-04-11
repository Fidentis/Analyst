package cz.fidentis.comparison.procrustes;

import Jama.Matrix;
import com.jogamp.graph.math.Quaternion;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector3f;


/**
 * This class implements Generalized Procrustes Analysis on list of configuration of Feature points
 * to create database of Procrustes distances
 * 
 * @author Zuzana LÝOVÁ
 * @version 2014
 */
public class GPA implements Serializable {
    private final List<ProcrustesAnalysis> configs;
    private boolean scaling = true;
    
    public GPA(){
        configs = new ArrayList();
    }
    
    public GPA(List<ProcrustesAnalysis> list){
        configs = new ArrayList();
        configs.addAll(list);
    }

    public void setScaling(boolean scaling) {
        this.scaling = scaling;
    }
    
    public List<ProcrustesAnalysis> getConfigs() {
        return configs;
    }
    
    /**
     * This method adds configuration to the list of configurations
     * @param pa        configuration to add
     */
    
    public void addPA(ProcrustesAnalysis pa){
        configs.add(pa);
    }
    
    /**
     * This metod returns i-th confirugation in this set of configurations
     * @param i
     * @return          i-th configuration in the set
     */
    public ProcrustesAnalysis getPA(int i){
        return this.configs.get(i);
    }
    
    /**
     * This method moves this all configurations to the center and normalize theirs size
     */
    
    public void normalizeAll(){
        for(int i = 0; i < configs.size(); i++){
            configs.get(i).normalize(scaling);
        }  
    }
    
    /**
     * This method counts mean configuration from all
     * @return          mean configuration
     */
    
    public ProcrustesAnalysis countMeanConfig(){
        /*Matrix mean = new Matrix(configs.get(0).getConfig().getRowDimension(), 
                configs.get(0).getConfig().getColumnDimension());
        float x;
        float y;
        float z;
        
        for(int i = 0; i < configs.size(); i++){
            for(int j = 0; j < configs.get(i).getConfig().getRowDimension(); j++){
                x = (float) configs.get(i).getConfig().get(j, 0);
                y = (float) configs.get(i).getConfig().get(j, 1);
                z = (float) configs.get(i).getConfig().get(j, 2);
                
                mean.set(j, 0, (mean.get(j, 0) + x));
                mean.set(j, 1, (mean.get(j, 1) + y));
                mean.set(j, 2, (mean.get(j, 2) + z));
            }
        }
        
        for(int k = 0; k < mean.getRowDimension(); k++){
            
            mean.set(k, 0, mean.get(k, 0)/configs.size());
            mean.set(k, 1, mean.get(k, 1)/configs.size());
            mean.set(k, 2, mean.get(k, 2)/configs.size());
            
        }*/
        
        Map<FacialPointType, FacialPoint> mean = new HashMap<>();
        Map<FacialPointType, Integer> timesAdded = new HashMap<>();
        
        for(ProcrustesAnalysis pa : configs){
            for(FacialPointType ft : pa.getConfig().keySet()){
                if(ft == FacialPointType.unspecified){
                    continue;
                }
                
                if(!mean.containsKey(ft)){
                    mean.put(ft, new FacialPoint(ft, new Vector3f()));
                    timesAdded.put(ft, 0);
                }
                
                mean.get(ft).getPosition().add(pa.getFPposition(ft));         //make sure it actually adds things in
                timesAdded.put(ft, timesAdded.get(ft) + 1);
            }
        }
        
        for(FacialPointType ft : mean.keySet()){
            Vector3f meanV = mean.get(ft).getPosition();
            int tAdded = timesAdded.get(ft);
            
            mean.put(ft, new FacialPoint(ft, new Vector3f(meanV.x / tAdded, meanV.y / tAdded, meanV.z / tAdded)));
        }
        
        
        return new ProcrustesAnalysis(mean);
    }

    
    /**
     * This method approximate all configurations to the mean configuration
     */
    
    public void superimpose(){
        this.normalizeAll();        
        rotateAll();
    }
    
    private List<Quaternion> rotateAll(){
        List<Quaternion> rotations = new ArrayList<>(configs.size());
        ProcrustesAnalysis mean = this.countMeanConfig();
        
        for(int i = 0; i < this.getConfigs().size(); i++){
           rotations.add(configs.get(i).rotate(mean));                    
        }
        
        return rotations;
    }
    
    
    /**
     * This method implements GPA algorithm
     * @param treshold      accuracy
     */
    
    public List<ICPTransformation> doGPA(float treshold){                  //registration step, return mean config?
        List<ProcrustesAnalysis> helpList = new ArrayList();
        List<ICPTransformation> trans = new ArrayList<>();
        
        this.normalizeAll();
        
        ProcrustesAnalysis oldMean = configs.get(0);            //doesn't create copy 
        ProcrustesAnalysis newMean;
        double oldDistance = Float.MAX_VALUE;
        double newDistance = 0;
        
        for(int i = 1; i < configs.size(); i++){
            Quaternion q = configs.get(i).rotate(oldMean);  
            trans.add(new ICPTransformation(new Vector3f(), 1.0f, q, 0.0f));
        }
        
        newMean = this.countMeanConfig();
        newDistance = newMean.countDistance(oldMean, scaling);
        int i = 0;
        
        while ((newDistance > treshold)){
            helpList.clear();
            helpList.addAll(0, configs);
            oldDistance = newDistance;
            oldMean = newMean;
            //this.superimpose();
            List<Quaternion> q = rotateAll();
            
            trans = createNewTransformations(q, trans);
            
            newMean = this.countMeanConfig();
            newDistance = newMean.countDistance(oldMean, scaling);
            if(oldDistance < newDistance){
                configs.clear();
                configs.addAll(0, helpList);
                break;
            }
        }
        
        return trans;
    }
    
    private List<ICPTransformation> createNewTransformations(List<Quaternion> q, List<ICPTransformation> trans){
        if(q.size() != trans.size()){
            return null;
        }
        
        List<ICPTransformation> newTrans = new LinkedList<>();
        
        for(int i = 0; i <  q.size(); i++){
            q.get(i).mult(trans.get(i).getRotation());
            newTrans.add(new ICPTransformation(new Vector3f(), 1.0f, q.get(i), 0.0f));
        }
        
        return newTrans;
    }
   
    
    /**
     * This method creates array of Procruestes distances 
     * between each 2 configurations in the list
     * 
     * @return      array of Procrustes distances
     */
    public double[] createArrayOfDistances(){
        double[] distances = new double[configs.size()*configs.size()];
        int k = 0;
        
        for (int i = 0; i < configs.size(); i++) {
            for (int j = 0; j < configs.size(); j++) {
                    distances[k] = configs.get(i).countDistance(configs.get(j), scaling);
                    k++;
            }
        }

        return distances;
    }
    
    /**
     * This method creates file with all configurations
     * @param file
     * @throws FileNotFoundException 
     */
    
    public void writeConfigs(String file) throws FileNotFoundException{
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        try{
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            
            for(int i = 0; i < configs.size(); i++){
                bw.write(configs.get(i).toString()); 
                bw.newLine();
            }
        }catch(IOException i){
            throw new FileNotFoundException("file not written"); 
        }finally{
            try{
                bw.close();
            }catch(IOException e){
                throw new FileNotFoundException("file not closed");
            }
        }
    }
}
