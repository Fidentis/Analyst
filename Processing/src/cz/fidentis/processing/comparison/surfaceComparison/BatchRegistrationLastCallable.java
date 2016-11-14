/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.comparison.surfaceComparison;

import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.icp.KdTree;
import cz.fidentis.model.Model;
import cz.fidentis.processing.fileUtils.ProcessingFileUtils;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import javax.vecmath.Vector3f;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 * Class designed for multi-threading of last alignmed of faces to template
 * during batch registration.
 *
 * @author Zuzana Ferkova
 */
public class BatchRegistrationLastCallable implements Callable<File> {

    private final KdTree templateTree;
    private final Model compF;
    private final List<Vector3f> samples;
    private final float error;
    private final int iterations;
    private final boolean scale;
    private final File tmpLoc;
    private final int batchIteration;
    private final int currentModelNumber;

    /**
     * Data needed to perform last alignment in batch module
     *
     * @param templateTree - kdTree representation of template
     * @param compF - face to be aligned to template
     * @param error - error denoting when no more iterations of ICP are
     * necessary
     * @param iterations - number of iterations for ICP
     * @param scale - whether to use scale during ICP
     * @param tmpLoc - address to folder on disk where aligned face will be
     * stored
     * @param batchIteration - current number of iteration in batch
     * @param currentModelNumber - current number of model in list of all models
     * (to generate appropriate name when saving to disk)
     */
    public BatchRegistrationLastCallable(KdTree templateTree, Model compF, List<Vector3f> samples, float error, int iterations, boolean scale, File tmpLoc, int batchIteration, int currentModelNumber) {
        this.templateTree = templateTree;
        this.compF = compF;
        this.samples = samples;
        this.error = error;
        this.iterations = iterations;
        this.scale = scale;
        this.tmpLoc = tmpLoc;
        this.batchIteration = batchIteration;
        this.currentModelNumber = currentModelNumber;
    }

    /**
     * Aligns compF to template.
     *
     * @return 0 when compF was aligned
     * @throws Exception
     */
    @Override
    public File call() throws Exception {
        ProgressHandle p = ProgressHandleFactory.createHandle("Aligning face " + (currentModelNumber + 1) + " to last average face.");
        p.start();

        try {

            Icp.instance().icp(templateTree, compF.getVerts(), samples, error, iterations, scale);

            p.finish();
        } catch (Exception ex) {
            p.finish();
        }
        return ProcessingFileUtils.instance().saveModelToTMP(compF, tmpLoc, batchIteration, currentModelNumber, true);
    }

}
