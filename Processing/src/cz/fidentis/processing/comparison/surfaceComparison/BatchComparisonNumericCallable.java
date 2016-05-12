/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.comparison.surfaceComparison;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.hausdorffDistance.ComparisonMetrics;
import cz.fidentis.comparison.hausdorffDistance.HausdorffDistance;
import cz.fidentis.comparison.hausdorffDistance.NearestCurvature;
import cz.fidentis.comparison.icp.KdTree;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.featurepoints.curvature.Curvature_jv;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author Zuzana Ferkova
 */
public class BatchComparisonNumericCallable implements Callable<ArrayList<Float>> {

    private KdTree mainF;
    private Model compMesh;

    public BatchComparisonNumericCallable() {
    }

    private boolean useRelative;
    private Float upperTreshold;
    private Float lowerTreshold;
    private int mainFaceNum;
    private int compareFaceNum;
    private double[] mainCurv;
    private double[] compCurv;
    private ComparisonMethod method;

    public BatchComparisonNumericCallable(KdTree mainF, Model compMesh, boolean useRelative, Float upperTreshold, Float lowerTreshold,  int mainFaceNum, int compareFaceNum,
            double[] mainCurv, double[] compCurv, ComparisonMethod method) {
        this.mainF = mainF;
        this.compMesh = compMesh;
        this.useRelative = useRelative;
        this.upperTreshold = upperTreshold;
        this.lowerTreshold = lowerTreshold;
        this.mainFaceNum = mainFaceNum;
        this.compareFaceNum = compareFaceNum;
        this.mainCurv = mainCurv;
        this.compCurv = compCurv;
        this.method = method;
    }

    /**
     * Computes numerical results for surface batch processing
     *
     * @return numerical results for a single face
     * @throws Exception
     */
    @Override
    public ArrayList<Float> call() throws Exception {
        List<Float> result;
        ProgressHandle p = ProgressHandleFactory.createHandle("Computing numerical results for faces " + (mainFaceNum + 1) + " and " + (compareFaceNum + 1) + ".");
        p.start();

        try {

            ArrayList<Float> tmp = new ArrayList<Float>((int) (compMesh.getVerts().size() * upperTreshold));

            if (method == ComparisonMethod.HAUSDORFF_DIST) {
                List<Vector3f> normalsUsed = compMesh.getNormals();

                if (compMesh.getVerts().size() > compMesh.getNormals().size()) {
                    normalsUsed = SurfaceComparisonProcessing.instance().recomputeVertexNormals(compMesh);
                }

                result = HausdorffDistance.instance().hDistance(mainF, compMesh.getVerts(), normalsUsed, useRelative);
            } else {
                result = NearestCurvature.instance().nearestCurvature((KdTreeIndexed) mainF, compMesh.getVerts(), mainCurv, compCurv);
            }
            result = ComparisonMetrics.instance().thresholdValues(result, upperTreshold, lowerTreshold, useRelative);
            tmp.addAll(result);

            p.finish();

            return tmp;
        } catch (Exception ex) {
            p.finish();
        }

        return null;
    }

}
