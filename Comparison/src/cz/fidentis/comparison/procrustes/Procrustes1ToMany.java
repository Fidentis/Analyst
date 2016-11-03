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
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana LÝOVÁ
 */
public class Procrustes1ToMany {
    private ProcrustesAnalysis pa;
    private List<ProcrustesAnalysis> pa2;
    private GPA gpa;
    private boolean scaling = true;

     public Procrustes1ToMany(List<FacialPoint> fps1, List<Vector3f> verts, List<List<FacialPoint>> fps2, List <ArrayList<Vector3f>> modelsVerts,boolean scaling){
         if(fps1 == null){
            throw new IllegalArgumentException("Facial points not set.");
        }
         if(verts==null){
                throw new IllegalArgumentException("Model vertices not set.");
            }
        
        for(int j = 0; j < fps2.size(); j++){
            if(fps2.get(j) == null){
                throw new IllegalArgumentException("Facial points not set.");
            }
            if(modelsVerts.get(j)==null){
                throw new IllegalArgumentException("Model vertices not set.");
            }
        }
        
        this.pa2 = new ArrayList();
        for(int i = 0; i < fps2.size(); i++){
            this.pa2.add(new ProcrustesAnalysis(fps2.get(i),modelsVerts.get(i)));
        }
        
        this.pa = new ProcrustesAnalysis(fps1,verts);
        this.scaling = scaling;
     }
    
    public Procrustes1ToMany(List<FacialPoint> fps1, List<List<FacialPoint>> fps2, boolean scaling){
        if(fps1 == null){
            throw new IllegalArgumentException("Facial points not set.");
        }
        
        for(int j = 0; j < fps2.size(); j++){
            if(fps2.get(j) == null){
                throw new IllegalArgumentException("Facial points not set.");
            }
        }
        
        this.pa2 = new ArrayList();
        for(int i = 0; i < fps2.size(); i++){
            this.pa2.add(new ProcrustesAnalysis(fps2.get(i)));
        }
        
        this.pa = new ProcrustesAnalysis(fps1);
        this.scaling = scaling;
    }
    
    /**
     * This method does comparison 1:N
     * 
     * @return      numerical result which is Procrustes distance between reference and each from N conf.
     */    
    public String compare1WithN(float treshold){
        String distances = " ;1;\n";
        List<ProcrustesAnalysis> list = new ArrayList();
        
        pa.normalize(scaling);
        
        for(int i = 0; i < pa2.size(); i++){
            gpa = new GPA();
            gpa.addPA(pa);
            gpa.addPA(pa2.get(i));
            gpa.setScaling(scaling);
            gpa.doGPA(treshold);            //align to pa?
            distances = distances.concat((i+1) + ";" + gpa.getPA(0).countDistance(gpa.getPA(1), scaling) + ";\n");
            
            list.add(gpa.getPA(1));
        }
        
        gpa = new GPA();
        for(int j = 0; j < pa2.size(); j++){
            gpa.addPA(list.get(j));
        }
        
        return distances;
    }
    
    public List<List<ICPTransformation>> align1withN(){
        List<List<ICPTransformation>> trans = new LinkedList<>();
        
        pa.normalize(scaling);
        
        for (ProcrustesAnalysis currentPA : pa2) {
            trans.add(pa.doProcrustesAnalysis(currentPA, scaling));        //slightly moves pa too, in each iteration due to superimposition method
        }
        
        return trans;
    }
    
    public String compare1toN(float threshold, String mainFace, List<File> models){
        String distances = " ;" + mainFace + ";\n";
        //List<ProcrustesAnalysis> list = new ArrayList();
        gpa = new GPA();
        
        for(int i = 0; i < pa2.size(); i++){
            distances = distances.concat(models.get(i).getName() + ";" + pa2.get(i).countDistance(pa, scaling) + ";\n");
            gpa.addPA(pa2.get(i));
        }
        
        return distances;
    }

    public List<ProcrustesAnalysis> getPa2() {
        return pa2;
    }
    
    public ProcrustesAnalysis getPa2(int i) {
        return pa2.get(i);
    }
    
    public ProcrustesAnalysis getPa() {
        return pa;
    }

    public GPA getGpa() {
        return gpa;
    }
    
}
