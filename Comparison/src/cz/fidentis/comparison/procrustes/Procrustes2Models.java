/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.procrustes;

import cz.fidentis.featurepoints.FacialPoint;
import java.io.FileNotFoundException;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana LÝOVÁ
 */
public class Procrustes2Models {
    private final ProcrustesAnalysis pa;
    private final ProcrustesAnalysis pa2;
    private GPA gpa;
    private GPA gpa4Database = null;
    private DatabaseWorker dfw;
    
    private boolean scaling = true;
    
     public Procrustes2Models(List<FacialPoint> fps, List<Vector3f> verts, List<FacialPoint> fps2, List<Vector3f> verts2, boolean scaling) throws FileNotFoundException{
        pa = new ProcrustesAnalysis(fps,verts);
        pa2 = new ProcrustesAnalysis(fps2,verts2);
        this.scaling = scaling;
        //dfw = new DatabaseWorker(this.scaling);
    }
    
    
    public Procrustes2Models(List<FacialPoint> fps, List<FacialPoint> fps2, boolean scaling) throws FileNotFoundException{
        pa = new ProcrustesAnalysis(fps);
        pa2 = new ProcrustesAnalysis(fps2);
        this.scaling = scaling;
        dfw = new DatabaseWorker(this.scaling);
    }
    
    public Procrustes2Models(List<FacialPoint> fps,List<FacialPoint> fps2, String file, boolean scaling) throws FileNotFoundException{
        pa = new ProcrustesAnalysis(fps);
        pa2 = new ProcrustesAnalysis(fps2);
        this.scaling = scaling;
        dfw = new DatabaseWorker(file);
    }   
    
    public Procrustes2Models(List<FacialPoint> fps, List<FacialPoint> fps2, List<List<FacialPoint>> list, boolean scaling) throws FileNotFoundException{
        pa = new ProcrustesAnalysis(fps);
        pa2 = new ProcrustesAnalysis(fps2);
        this.scaling = scaling;
        
        gpa4Database = new GPA();
        gpa4Database.setScaling(this.scaling);

        for (List<FacialPoint> list1 : list) {
            gpa4Database.addPA(new ProcrustesAnalysis(list1));
        }
    }
    
    /**
     * This method does 1:1 comparison without using database
     * 
     * @param treshold      treshold for ending GPA
     * @return              numerical result which is only Procrustes distance
     */
    public String compare2Models(float treshold){
        double distance;
        
        /*gpa = new GPA();
        gpa.setScaling(scaling);
        gpa.addPA(pa);
        gpa.addPA(pa2);
        gpa.doGPA(treshold);            //will align to mean configuration
        distance = gpa.getPA(0).countDistance(gpa.getPA(1));*/
        
        distance = pa2.countDistance(pa, scaling);
        
        String result;
        result = "Procrustes distance is ;" + distance;
        return result;
    }
    
    /**
     * This method does 1:1 comparison with database
     * 
     * @param treshold      treshold for ending GPA
     * @return              numerical result which is Procrustes distance, percentile of similarity and other values from database
     * @throws FileNotFoundException 
     */
    public String compare2ModelsWithDatabase(float treshold) throws FileNotFoundException{
        double distance;
        float percentile;
        double min;
        double max;
        double mean;
        
        gpa = new GPA();
        gpa.setScaling(scaling);
        gpa.addPA(pa);
        gpa.addPA(pa2);
        gpa.doGPA(treshold);
        distance = gpa.getPA(0).countDistance(gpa.getPA(1), scaling);
        
        if(gpa4Database != null){
            gpa4Database.doGPA(treshold);
            dfw = new DatabaseWorker(gpa4Database.createArrayOfDistances(), 
                        gpa4Database.getConfigs().size());
            dfw.bubbleSortReverse();
        }
        
        percentile = dfw.countPercentile(distance);
        min = dfw.getMinDistance();
        max = dfw.getMaxDistance();
        mean = dfw.getMeanDistance();
        
        String result;
        result = "Results of comparision: \nMinimal distance in database is " + min + 
                "\nMaximal distance in database is " + max +
                "\nAverage distance in database is " + mean +
                "\nProcrustes distance is " + distance +
                "\nPercentile of the distance considering the database is " + percentile;
        return result;
    }   
    
    public GPA getGpa() {
        return gpa;
    }

    public ProcrustesAnalysis getPa() {
        return pa;
    }

    public ProcrustesAnalysis getPa2() {
        return pa2;
    }
    
    
}
