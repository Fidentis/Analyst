package cz.fidentis.featurepoints.symmetryplane;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.Intersection;
import cz.fidentis.featurepoints.mathmorpho.Dynamic;
import cz.fidentis.model.Model;
//import cz.fidentis.processing.registration.Registrator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Midline {

    /**
     * Kazdemu hladanemu bodu vieme priradit poradie
     *
     */
    private final int DEFAULT_DYNAMICS_NUM = 10;

    private static final int MIN_EXTREMA_NEIGHBOR = 5;
    private static final double STEP = 0.01;

//    private Registrator averageRegistrator;
//    private Registrator mirrorRegistrator;
    private List<Vector3f> centerPoints;

    private Spline2D spline;
    private List<Point2d> splinePoints;
    private Model model;
    private LinkedList<Point3d> midlinePoints;
    private boolean doRegister;
    private Integer tmpIndex;

    private Dynamic dynamic;
    private double[] firstDerivative;
    private double[] maxDynamics;
    private double[] minDynamics;
    private List<Integer> sortedMaxDynamics;
    private List<Integer> sortedMinDynamics;

    private List<FacialPoint> midlineFacialPoints;
    private FpModel modelFPs;

    private List<Integer> maximaFPorderList;
    private List<Integer> minimaFPorderList;
    private List<Integer> midlineFPorderList;

    public Midline(Model model, List<Vector3f> centerPoints) {
        this.model = model;
        this.doRegister = false;

        this.centerPoints = centerPoints;
        this.midlineFacialPoints = new ArrayList<>();

        initFPorderList();
    }

    public Midline(Model model) {
        this(model, new ArrayList<Vector3f>());
    }

    public void analyze() {

        // Na tomto mieste neregistrovat, vykonava sa vo volajucej metode
//        if (doRegister) {
//            averageRegistrator = new Registrator(model);
//            averageRegistrator.registerToAverageFace();
//        }
//
//        Model mirroredModel = MirroredModel.getMirroredModel(model);
//
//        mirrorRegistrator = new Registrator(model, mirroredModel);
//        mirrorRegistrator.register();
//        // body v mediannej rovine - len priemet bodov modelu a zrkadloveho modelu do stredu tvare
//        ArrayList<Vector3f> centerPoints = MirroredModel.getCenterPoints(model, mirroredModel);
//
        // vypocet prieniku stredovej roviny a modelu
        midlinePoints = Intersection.computeSymmetryPlanePoints(model, model.getCornerTable(), centerPoints);

        // interpolacia bodov do splinu
        spline = new Spline2D(midlinePoints);
        splinePoints = spline.getAllpoints(STEP);

        // Vypocet krivosti podla dotykovych kruznic - nie je take presne ako
        // podla derivacii
        // double[] curvatures = OsculatingCircle.getCurvaturesFrom2D(splinePoints);        
        // Vypocet krivosti podla derivacii
        double[] curvatures = spline.getCurvatures(STEP);
        firstDerivative = spline.getFirstDerivativeY(STEP);
//        double[] fstDerivativeCopy = firstDerivative;

        // zmensenie rozsahu
        for (int i = 0; i < firstDerivative.length; i++) {
            firstDerivative[i] = firstDerivative[i] / 5;
        }

        for (int i = 0; i < curvatures.length; i++) {
            curvatures[i] = curvatures[i] * 50;
        }
        // vypocet dynamiky
        // nahradili sme vypoctom derivacie
        {

            dynamic = new Dynamic(firstDerivative, true);
            maxDynamics = dynamic.dynMax(); //(minimaFPorderList.size());
            sortedMaxDynamics = dynamic.getSortedDynamics();

            double[] minDynamics = dynamic.dynMin(); //(maximaFPorderList.size());
            sortedMinDynamics = dynamic.getSortedDynamics();

//            pointDetection();
            // previest najdene body podla dynamik spat na symetrivky profil
            // interpolateFPs(splinePoints, maxDynamics, maximaFPorderList);
            // interpolateFPs(splinePoints, minDynamics, minimaFPorderList);
        }

        List<Integer> extremasIndices = findFPsFromExtremas();

//        if (doRegister) {
//            averageRegistrator.reverse();
//            Registrator.reverse(midlineFacialPoints, averageRegistrator.getTransformations());
//        }
    }

    public List<Point3d> getMidlelinePoints() {
        return midlinePoints;
    }

    public void setDoRegister(boolean doRegister) {
        this.doRegister = doRegister;
    }

    private void initFPorderList() {

        // Vytvorime aj celkovy zoznam poradia, nezalezi na maxime, alebo minime
        midlineFPorderList = new ArrayList<>();
        midlineFPorderList.add(FacialPointType.G.ordinal()); // Glabela
        midlineFPorderList.add(FacialPointType.N.ordinal()); // Nasion
        midlineFPorderList.add(FacialPointType.PRN.ordinal()); // Pronasale  
        midlineFPorderList.add(FacialPointType.SN.ordinal()); // Subnasale (?) - nie som si isty, ci je to minimum
        midlineFPorderList.add(FacialPointType.LS.ordinal()); // Labrale superius
        midlineFPorderList.add(FacialPointType.STO.ordinal()); // Stomion
        midlineFPorderList.add(FacialPointType.LI.ordinal()); // Labrale inferius
        midlineFPorderList.add(FacialPointType.SL.ordinal()); // Sublabiale
        midlineFPorderList.add(FacialPointType.PG.ordinal()); // Pogonion
        // midlineFPorderList.add(FacialPointType.GN); // Gnathion (?) - nie som si isty, ci je to minimum

        // Lokalne maxima, od hora na dol, dolezite je poradie bodov
        maximaFPorderList = new ArrayList<>();
        maximaFPorderList.add(FacialPointType.G.ordinal()); // Glabela
        maximaFPorderList.add(FacialPointType.PRN.ordinal()); // Pronasale
        maximaFPorderList.add(FacialPointType.LS.ordinal()); // Labrale superius
        maximaFPorderList.add(FacialPointType.LI.ordinal()); // Labrale inferius
        maximaFPorderList.add(FacialPointType.PG.ordinal()); // Pogonion

        // Lokalne Minima, od hora na dol, dolezite je poradie bodov
        minimaFPorderList = new ArrayList<>();
        minimaFPorderList.add(FacialPointType.N.ordinal()); // Nasion
        minimaFPorderList.add(FacialPointType.SN.ordinal()); // Subnasale (?) - nie som si isty, ci je to minimum
        minimaFPorderList.add(FacialPointType.STO.ordinal()); // Stomion
        minimaFPorderList.add(FacialPointType.SL.ordinal()); // Sublabiale
        minimaFPorderList.add(FacialPointType.GN.ordinal()); // Gnathion (?) - nie som si isty, ci je to minimum

    }

    private List<Integer> findFPsFromExtremas() {
        // najblizsi sused musi byt v minimalnej vzdialenosti podla konstanty
        // incialne nastavit na posledny extrem na lubovolnu vysoku hodnotu
        int lastExtrema = 1000;
        // maximalny index, ktory chceme vysetrovat - z oboch stran
        int boundaryIndex = 10;

        List<Integer> extremaIndexes = new ArrayList<>();

        // extremy - hladame body, v ktorom funkcia nadobuda 0 hodnoty
        for (int i = boundaryIndex; i < firstDerivative.length - boundaryIndex; i++) {
            double idx1 = firstDerivative[i];
            double idx2 = firstDerivative[i + 1];
            // bod je extremom vtedy, ak znamienka dvoch susednych bodov je rozdielny
            // alebo ak sa bod == 0
            boolean isExtreme = (idx1 == 0 || (Math.signum(idx1) != Math.signum(idx2)));

            if (isExtreme && lastExtrema >= MIN_EXTREMA_NEIGHBOR) {
                // pridat az dalsi bod
                extremaIndexes.add(i + 1);
                lastExtrema = 0;

                // Ak je lokalizovany maximalny pocet bodov, tak koniec hladania
                if (extremaIndexes.size() == midlineFPorderList.size()) {
                    break;
                }
            }
            lastExtrema++;
        }
        interpolateFPs(extremaIndexes);

        validatePoints();

        return extremaIndexes;
    }

    private void validatePoints() {

    }

    private void pointDetection() {
        // bod pronasale urcime, ako najblizsi bod k uz najdenemu bodu pronasale
        // TODO nevytvarat novy, ale nahradit existujuci bod
        Point3d modelPronasale = new Point3d(modelFPs.getFacialPoint(FacialPointType.PRN.ordinal()).getCoords());
        Point3d pronasale = findNearestPoint(modelPronasale, sortedMinDynamics);
        Integer pronasaleIdx = tmpIndex;
        FacialPoint pronasaleFP = new FacialPoint(FacialPointType.PRN.ordinal(), pronasale);
        midlineFacialPoints.add(pronasaleFP);

        // bod stomion urcime, ako najblizsi bod k uz najdenemu bodu stomion
        // TODO nevytvarat novy, ale nahradit existujuci bod
        Point3d modelStomion = new Point3d(modelFPs.getFacialPoint(FacialPointType.STO.ordinal()).getCoords());
        Point3d stomion = findNearestPoint(modelStomion, sortedMinDynamics);
        Integer stomionIdx = tmpIndex;
        FacialPoint stomionFP = new FacialPoint(FacialPointType.STO.ordinal(), stomion);
        midlineFacialPoints.add(stomionFP);

        // bod nasion nebol vysetrovany, preto len priblizna interpolacia - 
        // - zaujima nas len y-ova suradnica
        double modelNasionY = (modelFPs.getFacialPoint(FacialPointType.EN_L.ordinal()).getPosition().y
                + modelFPs.getFacialPoint(FacialPointType.EN_R.ordinal()).getPosition().y) / 2;
        // bod nasion urcime, ako najblizsi bod vzhladom na vnutorne kutiky oci
        Point3d nasion = findNearestPoint(modelNasionY, sortedMaxDynamics);
        Integer nasionIdx = tmpIndex;
        FacialPoint nasionFP = new FacialPoint(FacialPointType.N.ordinal(), nasion);
        midlineFacialPoints.add(nasionFP);

        // bod glabela urcime ako prvy bod s vysokou dynamikou, ktory
        // je vyssie ako bod nasion
        Point3d glabela = findHigherPoint(nasionIdx, sortedMinDynamics);
        Integer glabelaIdx = tmpIndex;
        FacialPoint glabelaFP = new FacialPoint(FacialPointType.G.ordinal(), glabela);
        midlineFacialPoints.add(glabelaFP);

        // bod labrale superius urcime ako prvy bod s vysokou dynamikou, ktory
        // je vyssie ako bod stomion
        Point3d labraleSuperius = findHigherPoint(stomionIdx, sortedMinDynamics);
        Integer labraleSuperiusIdx = tmpIndex;
        FacialPoint labraleSuperiusFP = new FacialPoint(FacialPointType.LS.ordinal(), labraleSuperius);
        midlineFacialPoints.add(labraleSuperiusFP);

        // bod labrale inferius urcime ako prvy bod s vysokou dynamikou, ktory
        // je nizsie ako bod stomion
        Point3d labraleInferius = findLowerPoint(stomionIdx, sortedMinDynamics);
        Integer labraleInferiusIdx = tmpIndex;
        FacialPoint labraleInferiusFP = new FacialPoint(FacialPointType.LI.ordinal(), labraleInferius);
        midlineFacialPoints.add(labraleInferiusFP);

        // bod sublabiale urcime ako prvy bod s vysokou dynamikou, ktory
        // je nizsie ako bod labrale inferius
        Point3d sublabiale = findLowerPoint(labraleInferiusIdx, sortedMaxDynamics);
        Integer sublabialeIdx = tmpIndex;
        FacialPoint sublabialeFP = new FacialPoint(FacialPointType.SL.ordinal(), sublabiale);
        midlineFacialPoints.add(sublabialeFP);

        // bod pogonion urcime ako prvy bod s vysokou dynamikou, ktory
        // je nizsie ako bod sublabiale
        Point3d pogonion = findLowerPoint(sublabialeIdx, sortedMinDynamics);
        Integer pogonionIdx = tmpIndex;
        FacialPoint pogonionFP = new FacialPoint(FacialPointType.PG.ordinal(), pogonion);
        midlineFacialPoints.add(pogonionFP);

        // bod subnasale urcimee ako prvy bod s vysokou dynamikou, ktory
        // sa nachadza medzi bodmi pronasale a labrale superius
        Point3d subnasale = findPointBetween(pronasaleIdx, labraleSuperiusIdx, sortedMaxDynamics);
        Integer subnasaleIdx = tmpIndex;
        FacialPoint subnasaleFP = new FacialPoint(FacialPointType.SN.ordinal(), subnasale);
        midlineFacialPoints.add(subnasaleFP);

        deleteNullFPs();
    }

    private void interpolateFPs(List<Integer> pointIndexes) {
        interpolateFPs(pointIndexes, midlineFPorderList);
    }

    private void interpolateFPs(List<Integer> pointIndexes, List<Integer> orderList) {
        // Pre kazdu hodnotu dynamiky najdeme bod na symetrickom profile
        int index = 0;
        for (Integer idx : pointIndexes) {
            Point2d point = splinePoints.get(idx);

            Point3d interpolatedPoint = interpolateFP(point);

            // Zo zoznamu vyberieme typ bodu a pridame FP do zoznamu
            midlineFacialPoints.add(new FacialPoint(orderList.get(index), interpolatedPoint));
            index++;
        }
    }

    private Point3d interpolateFP(Point2d point) {
        double minDistance = Double.POSITIVE_INFINITY;
        Point3d nearestPoint = new Point3d();
        for (Point3d midlinePoint : midlinePoints) {

            double distance = point.distance(new Point2d(midlinePoint.y, midlinePoint.z));
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = midlinePoint;
                if (distance == 0.d) {
                    break;
                }
            }
        }
        return nearestPoint;
    }

    private Point3d findNearestPoint(double y, List<Integer> dynamics) {
        double minDistance = Double.POSITIVE_INFINITY;
        Point2d point = null;
        for (Integer dynIdx : dynamics) {
            Point2d dynPoint = splinePoints.get(dynIdx);
            double distance = Math.abs(y - dynPoint.y);

            if (distance < minDistance) {
                minDistance = distance;
                point = dynPoint;
            }
        }
        return interpolateFP(point);
    }

    private Point3d findNearestPoint(Point2d p, List<Integer> dynamics) {
        double minDistance = Double.POSITIVE_INFINITY;
        Point2d point = null;
        for (Integer dynIdx : dynamics) {
            Point2d dynPoint = splinePoints.get(dynIdx);
            double distance = dynPoint.distance(p);

            if (distance < minDistance) {
                minDistance = distance;
                point = dynPoint;
                tmpIndex = dynIdx;
            }
        }
        return interpolateFP(point);
    }

    private Point3d findNearestPoint(Point3d p, List<Integer> dynamics) {
        return findNearestPoint(new Point2d(p.y, p.z), dynamics);
    }

    // najde nizsi bod na splajne, podla indexu
    private Point3d findHigherPoint(Integer inIdx, List<Integer> dynamics) {
        if (inIdx == null) {
            tmpIndex = null;
            return null;
        }

        for (int i = 0; i < dynamics.size(); i++) {
            if (dynamics.get(i) > inIdx) {
                tmpIndex = dynamics.get(i);
                break;
            }
        }
        return interpolateFP(splinePoints.get(tmpIndex));
    }

    private Point3d findLowerPoint(Integer inIdx, List<Integer> dynamics) {
        if (inIdx == null) {
            tmpIndex = null;
            return null;
        }

        for (int i = dynamics.size() - 1; i > -1; i--) {

            if (dynamics.get(i) < inIdx) {
                tmpIndex = dynamics.get(i);
                break;
            }
        }
        return interpolateFP(splinePoints.get(tmpIndex));
    }

    // Najde bod medzi dvomi indexmi, prehladavame zhora
    private Point3d findPointBetween(Integer lowIdx, Integer highIdx, List<Integer> dynamics) {
        if (lowIdx == null || highIdx == null) {
            tmpIndex = null;
            return null;
        }

        for (int i = dynamics.size() - 1; i > -1; i--) {
            if (dynamics.get(i) > lowIdx && dynamics.get(i) < highIdx) {
                tmpIndex = dynamics.get(i);
                break;
            }
        }
        return interpolateFP(splinePoints.get(tmpIndex));
    }

    private void deleteNullFPs() {
        for (FacialPoint midlineFacialPoint : midlineFacialPoints) {
            if (midlineFacialPoint.getCoords() == null) {
                midlineFacialPoints.remove(midlineFacialPoint);
            }
        }
    }

    public List<FacialPoint> getFacialPoints() {
        return midlineFacialPoints;
    }

    public void setModelFPs(FpModel modelFPs) {
        this.modelFPs = modelFPs;
    }

}
