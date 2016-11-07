package cz.fidentis.processing.registration;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.icp.KdTree;
import cz.fidentis.comparison.icp.KdTreeIndexed;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Registrator {

    private static final int DEFALT_ITERATIONS = 20;
    private static final boolean SCALE = true;
    private static final float ERROR = 0.f;
    // zatial sa nepouziva, ak sa bude, tak doplnit cestu pre priemernu tvar
    private static final String AVERAGE_FACE_PATH = ""; //"./models/resources/average_face.obj";

    private Model mainModel;
    private Model comparedModel;
    private List<ICPTransformation> transformations;

    public Registrator(Model mainModel, Model comparedModel) {
        this.mainModel = mainModel;
        this.comparedModel = comparedModel;
    }

    public Registrator(Model mainModel) {
        this.mainModel = mainModel;
    }

    public float register() {
        return register(mainModel, comparedModel, ERROR, DEFALT_ITERATIONS, SCALE);
    }

    public float registerToAverageFace() {
        return register(getAverageFace(), mainModel, ERROR, DEFALT_ITERATIONS, SCALE);
    }

    public float register(Model mainModel, Model comparedModel, float error, int iterations, boolean scale) {
        long start = System.nanoTime();
        System.out.println("Model registration...");

        KdTree mainF = new KdTreeIndexed(mainModel.getVerts());
//      Icp.instance().icp(mainF, comparedModel.getVerts(), comparedModel.getVerts().size(), 0.f);
        transformations = Icp.instance().icp(mainF, comparedModel.getVerts(), comparedModel.getVerts(), error, iterations, scale);

        System.out.println("Done.");
        long end = System.nanoTime();
        float seconds = TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS) / 1000.f;
        System.out.println("Registration time: " + seconds + "sec");

        return seconds;
    }

    public void reverse() {
        Icp.instance().reverseAllTransformations(transformations, mainModel.getVerts(), SCALE);
    }

    public static List<FacialPoint> reverse(List<FacialPoint> facialPoints, List<ICPTransformation> transformations) {
        List<Vector3f> reversedVerts = new ArrayList<>();
        for (FacialPoint facialPoint : facialPoints) {
            reversedVerts.add(new Vector3f(facialPoint.getCoords()));
        }

        Icp.instance().reverseAllTransformations(transformations, reversedVerts, SCALE);

        List<FacialPoint> transformedFacialPoints = new ArrayList<>();
        int index = 0;
        for (Vector3f vert : reversedVerts) {
            //FacialPointType originalType = facialPoints.get(index).getType();
            transformedFacialPoints.add(new FacialPoint(facialPoints.get(index).getType(), vert));
        }

        return transformedFacialPoints;
    }

    private Model getAverageFace() {
        try {
            return ModelLoader.instance().loadModel(new File(AVERAGE_FACE_PATH).getCanonicalFile(), false, true);
        } catch (IOException ex) {
            System.out.println("Can't load average face!");
            return null;
        }
    }

    public Model getMainModel() {
        return mainModel;
    }

    public void setMainModel(Model mainModel) {
        this.mainModel = mainModel;
    }

    public Model getComparedModel() {
        return comparedModel;
    }

    public void setComparedModel(Model comparedModel) {
        this.comparedModel = comparedModel;
    }

    public List<ICPTransformation> getTransformations() {
        return transformations;
    }

    public float getError() {
        return ERROR;
    }
}
