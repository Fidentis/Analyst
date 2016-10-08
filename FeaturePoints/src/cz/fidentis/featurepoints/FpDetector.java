package cz.fidentis.featurepoints;

import cz.fidentis.model.Material;
import cz.fidentis.model.Model;
import cz.fidentis.featurepoints.symmetryplane.Midline;
import cz.fidentis.featurepoints.texture.ImageAnalyzer;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import jv.object.PsDebug;
import org.opencv.core.Mat;
import org.opencv.LoadOpenCV;

/**
 *
 * @author Galvanizze
 */
public class FpDetector {

    private static final boolean CENTRALIZE = false;

    private Model model;
    private FeaturePointsUniverse fpUniverse;

    private List<FacialPoint> modelFacialPoints;
    private List<FacialPoint> textureFacialPoints;
    private List<FacialPoint> midlineFacialPoints;
    private List<FacialPoint> allFacialPoints;
    private FpModel modelFPs;
    private FpModel centralizedModelFPs;
    private FpModel midlineFPs;
    private FpModel textureFPs;

    private boolean isMaterial;
    private final String modelName;
    private boolean decentralize;
    private Mat analyzedImage;

    private static final float[] FP_COLOR_MODEL = new float[]{1f, 1f, 0f, 1.0f};
    private static final float[] FP_COLOR_MIDLINE = new float[]{1f, 0f, 1f, 1.0f};
    private static final float[] FP_COLOR_TEXTURE = new float[]{0f, 1f, 1f, 1.0f};

//    static {
//        try {
//            LoadOpenCV.loadOSXlib();
//            LoadOpenCV.loadWindowsLib();
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(FpDetector.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public FpDetector(String modelName) {
        this.modelName = modelName;
    }

    public FpDetector(Model model, String modelName) {
        this.model = model;
        this.modelName = modelName;

        if (CENTRALIZE) {
            model.centralize();
        }

        this.modelFacialPoints = new ArrayList<>();
        this.textureFacialPoints = new ArrayList<>();
        this.midlineFacialPoints = new ArrayList<>();
        this.allFacialPoints = new ArrayList<>();

        isMaterial();
    }

    public FpDetector(Model model) {
        this(model, "");
    }

    public FpDetector(Model model, String modelName, boolean decentralize) {
        this(model, modelName);
        this.decentralize = decentralize;
    }

    public List<FacialPoint> computeAllFPs(List<Vector3f> centerPoints) {
        computeModelFPs();
        computeMidlineFPs(centerPoints);
        computeTextureFPs();

        // niektore body mozu byt najdene viacnasobne, preto ich odfiltrujeme
        // podla uspesnosti spracovania algoritmov
        filterFPs();

        allFacialPoints.addAll(modelFacialPoints);
        allFacialPoints.addAll(midlineFacialPoints);
        allFacialPoints.addAll(textureFacialPoints);
        return allFacialPoints;
    }

    public void computeModelFPs() {
        this.fpUniverse = new FeaturePointsUniverse(model);

        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);

        fpUniverse.computeSimplify();

        fpUniverse.findNose();
        fpUniverse.findMouth();
        fpUniverse.findEyes();
        modelFacialPoints = fpUniverse.getFacialPoints();

        PsDebug.getConsole().setVisible(false);

//        setFPcolors(modelFacialPoints, FP_COLOR_MODEL);
        // Kvoli decentralizacii a davkovemu spracovaniu je potrebne pracovat
        // s centralizovanymi bodmi, je to hlavne z dovodu pouzitia bodov
        // v analyze textury
        centralizedModelFPs = new FpModel(modelName);
        centralizedModelFPs.setFacialpoints(modelFacialPoints);

        /*if (decentralize) {
            decentralizeFPs(modelFacialPoints);
        }*/

        modelFPs = new FpModel(modelName);
        modelFPs.setFacialpoints(modelFacialPoints);
    }

    public void computeMidlineFPs(List<Vector3f> centerPoints) {
        Midline midlineAnalyzer = new Midline(model, centerPoints);
//        midlineAnalyzer.setDoRegister(false);

        midlineAnalyzer.analyze();

        midlineFacialPoints = midlineAnalyzer.getFacialPoints();

//        setFPcolors(midlineFacialPoints, FP_COLOR_MIDLINE);
        if (decentralize) {
            decentralizeFPs(midlineFacialPoints);
        }

        midlineFPs = new FpModel(modelName);
        midlineFPs.setFacialpoints(midlineFacialPoints);
    }

    public Mat computeTextureFPs() {
        if (!isMaterial || model.getTexCoords().size() <= 0 || !LoadOpenCV.openCVLoaded) {
            return null;
        }

        ImageAnalyzer analyzer = new ImageAnalyzer(model);
//        analyzer.setObjFPmodel(centralizedModelFPs);

        analyzedImage = new Mat();
        try {
            analyzer.setDoRotation(true);
            analyzedImage = analyzer.analyze();
        } catch (NullPointerException e) {
            System.out.println("Model: " + modelName + " failed processing.");
        }

        textureFacialPoints = analyzer.get3DfacialPoints();

//        setFPcolors(textureFacialPoints, FP_COLOR_TEXTURE);
        if (decentralize) {
            decentralizeFPs(textureFacialPoints);
        }

        textureFPs = new FpModel(modelName);
        textureFPs.setFacialpoints(textureFacialPoints);

        return analyzedImage;
    }

    public boolean isMaterial() {
        if (model.getMatrials() == null) {
            isMaterial = false;
        } else {
            ArrayList<Material> materials = model.getMatrials().getMatrials();

            if (materials.isEmpty()) {
                System.out.println("For model " + modelName + " exists no texture.");
                isMaterial = false;
            } else if (materials.size() != 1) {
                System.out.println("For model" + modelName + " exists more than 1 (" + materials.size() + ") texture.");
            } else {
                isMaterial = true;
            }
        }

        return isMaterial;
    }

//    private void setFPcolors(List<FacialPoint> facialPoints, float[] color) {
//        for (FacialPoint fp : facialPoints) {
//            fp.setColor(color);
//        }
//    }
    private void decentralizeFPs(List<FacialPoint> facialPoints) {
        Vector3f center = model.getModelDims().getOriginalCenter();
        for (FacialPoint facialPoint : facialPoints) {
            facialPoint.getPosition().x += center.x;
            facialPoint.getPosition().y += center.y;
            facialPoint.getPosition().z += center.z;
        }
    }

    public FpModel getModelFPs() {
        return modelFPs;
    }

    public FpModel getMidlineFPs() {
        return midlineFPs;
    }

    public FpModel getTextureFPs() {
        return textureFPs;
    }

    public void setModelFPs(FpModel modelFPs) {
        this.modelFPs = modelFPs;
    }

    public void setDecentralize(boolean decentralize) {
        this.decentralize = decentralize;
    }

    public Mat getAnalyzedImage() {
        return analyzedImage;
    }

    // filtrovat body, podla toho ci sa nasli v jednotlivych castiach algoritmu
    private void filterFPs() {
        // body Ektokantion a Ektokantion pouzit z textury, ak sa nasli
        if (textureFPs != null) {
            if (textureFPs.containsPoint(FacialPointType.EN_L)) {
                modelFacialPoints = deleteFPs(modelFacialPoints, FacialPointType.EN_L);
            }
            if (textureFPs.containsPoint(FacialPointType.EN_R)) {
                modelFacialPoints = deleteFPs(modelFacialPoints, FacialPointType.EN_R);
            }
            if (textureFPs.containsPoint(FacialPointType.EX_L)) {
                modelFacialPoints = deleteFPs(modelFacialPoints, FacialPointType.EX_L);
            }
            if (textureFPs.containsPoint(FacialPointType.EX_R)) {
                modelFacialPoints = deleteFPs(modelFacialPoints, FacialPointType.EX_R);
            }
        }

        // body Pronasale a Stomion pouzit zo zakrivenia, ak sa nasli
        if (modelFPs != null) {
            if (modelFPs.containsPoint(FacialPointType.PRN)) {
                midlineFacialPoints = deleteFPs(midlineFacialPoints, FacialPointType.PRN);
            }
            if (modelFPs.containsPoint(FacialPointType.STO)) {
                midlineFacialPoints = deleteFPs(midlineFacialPoints, FacialPointType.STO);
            }
        }

    }

    // vymaze FP specifikovane v parametri
    private List<FacialPoint> deleteFPs(List<FacialPoint> facialPoints, FacialPointType... types) {
        List<FacialPoint> newFPs = new ArrayList<>();
        for (FacialPoint fp : facialPoints) {
            boolean found = false;

            for (FacialPointType type : types) {
                if (fp.getType() == type) {
                    found = true;
                }
            }

            if (!found) {
                newFPs.add(fp);
            }
        }

        return newFPs;
    }
}
