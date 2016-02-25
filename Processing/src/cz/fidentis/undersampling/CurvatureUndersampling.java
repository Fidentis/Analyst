/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.undersampling;

import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.featurepoints.curvature.Curvature_jv;
import cz.fidentis.model.Model;
import cz.fidentis.utils.ListUtils;
import cz.fidentis.utils.SortUtils;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class CurvatureUndersampling {
    private final List<Vector3f> mesh;
    private final Curvature_jv curvature;

    public CurvatureUndersampling(Model mesh) {
        this.mesh = mesh.getVerts();
        curvature = new Curvature_jv(mesh);
    }
    
    public List<Integer> curvaturePercentage(int percentage){
        float scale = percentage / 100f;
        
        int size = (int) (mesh.size() * scale);
        
        List<Integer> undersamples = curvatureSamples(size);
        
        return undersamples;
    }
    
    public List<Integer> curvatureSamples(int samples){
        List<Integer> curvePick = ListUtils.instance().populateList(mesh.size());
        double[] vc = curvature.getCurvature(CurvatureType.Maximum);
        List<Float> computedCurv = new ArrayList<>(vc.length);
        
        for(double d : vc){
           computedCurv.add((float)d);
        }
        
        curvePick = SortUtils.instance().sortIndices(computedCurv, curvePick);
        curvePick = curvePick.subList(mesh.size() - samples, mesh.size());
        
        return curvePick;        
    }
}
