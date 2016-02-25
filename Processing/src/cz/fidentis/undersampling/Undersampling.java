/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.undersampling;

import cz.fidentis.model.Model;
import cz.fidentis.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class Undersampling {
    private static Undersampling instance;
    
    private Undersampling(){
        
    }
    
    public static Undersampling instance(){
        return instance != null ? instance : (instance = new Undersampling());
    }
    
    
    /**
     * Resolves random undersampling based on the type.
     * @param t - type of undersampling (Percentage, Number)
     * @param value - value of undersampling
     * @param mesh - mesh to be picked from
     * @return list of undersamples from given mesh based on parameters
     */
    public List<Vector3f> resolveRandom(Type t, int value, List<Vector3f> mesh){
        List<Integer> undersampling;
        
        if(t == Type.NUMBER){
            undersampling = RandomUndersampling.instance().randomNumberOfSamples(value, mesh);
        }else{
            undersampling = RandomUndersampling.instance().randomPercentage(value, mesh);
        }
        
        return ListUtils.instance().getVectorList(undersampling, mesh);
    }
    
    public List<Vector3f> resolveCurvature(Type t, int value, Model mesh){
        List<Integer> undersampling = null;
        CurvatureUndersampling cu = new CurvatureUndersampling(mesh);
        
        if(t == Type.NUMBER){
            undersampling = cu.curvatureSamples(value);
        }else{
            undersampling = cu.curvaturePercentage(value);
        }
        
        return ListUtils.instance().getVectorList(undersampling, mesh.getVerts());
    }
    
    public List<Vector3f> resolveDisk(float value, Model m){
        DiskUndersampling undersampling = new DiskUndersampling(m);
        List<Integer> indices = undersampling.diskUndersampling(value);   
        
        return ListUtils.instance().getVectorList(indices, m.getVerts());
    }
}
