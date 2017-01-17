/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.processing.comparison.surfaceComparison;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.hausdorffDistance.HausdorffDistance;
import cz.fidentis.comparison.hausdorffDistance.NearestCurvature;
import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.featurepoints.curvature.Curvature_jv;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class BatchComparisonVisualCallable implements Callable<ArrayList<Float>>{

    private KdTree computeMorph;
    private Model template;
    private boolean useRelative;
    private ComparisonMethod method;
    private Curvature_jv morphCurv;
    private Curvature_jv templateCurv;
    
    public BatchComparisonVisualCallable(KdTree computeMorph, Model template, boolean useRelative, Curvature_jv morphCurv, Curvature_jv templateCurv, ComparisonMethod method) {
        this.computeMorph = computeMorph;
        this.template = template;
        this.useRelative = useRelative;
        this.method = method;
        this.morphCurv = morphCurv;
        this.templateCurv = templateCurv;
    }
    
    /**
     * Computes visual results for surface batch processing
     * @return numerical results for single face
     * @throws Exception 
     */
    @Override
    public ArrayList<Float> call() throws Exception {
        
        if(method == ComparisonMethod.HAUSDORFF_DIST){
            List<Vector3f> normalsUsed = template.getNormals();
        
            if(template.getVerts().size() > template.getNormals().size()){
                normalsUsed = SurfaceComparisonProcessing.instance().recomputeVertexNormals(template);
            }
        
            return (ArrayList<Float>) HausdorffDistance.instance().hDistance(computeMorph, template.getVerts(), normalsUsed, useRelative);
        }else{
           return (ArrayList<Float>) NearestCurvature.instance().nearestCurvature((KDTreeIndexed) computeMorph, template.getVerts(), morphCurv.getCurvature(CurvatureType.Gaussian), templateCurv.getCurvature(CurvatureType.Gaussian));
        }
    }
    
}
