package cz.fidentis.featurepoints.symmetryplane;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/** Analýza mediánnej (stredovej) roviny tváre a extrakcia bodov na nej
 *
 * @author Galvanizze
 */
public class SymmetryPlane {
    
    final static float SEARCH_THRESHOLD = 15.f;
    
    List<FacialPoint> facialPoints;
    LinkedList<Point3f> symmetryPlanePoints;
    
    FacialPoint glabelaFP;
    FacialPoint nasionFP;
    FacialPoint pronasaleFP;
    FacialPoint subnasaleFP;
    FacialPoint labraleSuperiusFP;
    FacialPoint stomionFP;
    FacialPoint labraleInferiusFP;
    FacialPoint sublabialeFP;
    FacialPoint pogonionFP;
    FacialPoint middleEyeLFP;
    FacialPoint middleEyeRFP;
    
    public SymmetryPlane(){
        facialPoints = new ArrayList<>();
        symmetryPlanePoints = new LinkedList<>();
    }

    public SymmetryPlane(List<FacialPoint> facialPoints, LinkedList<Point3f> symmetryPlanePoints) {
        this.facialPoints = facialPoints;
        this.symmetryPlanePoints = symmetryPlanePoints;
    }
    
    public List<FacialPoint> findAllSymmetryPlaneFPs(){
        if (facialPoints == null || symmetryPlanePoints == null) {
            return null;
        }
        
        findStomionFP();
        findNasionFP();
        findGlabelaFP();
        findLabralesFPs();
        findSublabialeFP();
        findPogonionFP();
        findMiddleEyeLFP();
        findMiddleEyeRFP();
        
        return facialPoints;
    }
     
    public void findMiddleEyeLFP(){
        Point3f middleEyeLcandidate = findMiddlePoint(FacialPointType.EX_L, FacialPointType.EN_L, false); // mimo mediannej roviny     
        middleEyeLcandidate.setZ(middleEyeLcandidate.z + 5.f);
        middleEyeLFP = new FacialPoint(FacialPointType.unspecified, middleEyeLcandidate);
        facialPoints.add(middleEyeLFP);
    }

    public void findMiddleEyeRFP(){
        Point3f middleEyeRcandidate = findMiddlePoint(FacialPointType.EX_R, FacialPointType.EN_R, false); // mimo mediannej roviny
        middleEyeRcandidate.setZ(middleEyeRcandidate.z + 5.f);
        middleEyeRFP = new FacialPoint(FacialPointType.unspecified, middleEyeRcandidate);
        facialPoints.add(middleEyeRFP);
    }
    
    public void findPronasaleFP(){
        Point3f pronasaleOld = getFPpoint(facialPoints, FacialPointType.PRN).getCoords();
        int newPronasaleIdx = findNearestPointOnSymmetryPlane(pronasaleOld, symmetryPlanePoints);
        facialPoints.remove(getFPpoint(facialPoints, FacialPointType.PRN));
        pronasaleFP = new FacialPoint(FacialPointType.PRN, symmetryPlanePoints.get(newPronasaleIdx));
        facialPoints.add(pronasaleFP);
    }
    
    public void findStomionFP(){
        Point3f stomionCandidate = findMiddlePoint(FacialPointType.CH_L, FacialPointType.CH_R, true); // na mediannej rovine
        facialPoints.remove(getFPpoint(facialPoints, FacialPointType.STO));
        stomionFP = new FacialPoint(FacialPointType.STO, stomionCandidate);
        facialPoints.add(stomionFP);
    }
    
    public void findNasionFP(){
        Point3f nasionCandidate = findMiddlePoint(FacialPointType.EN_L, FacialPointType.EN_R, true); // na mediannej rovine
        nasionFP = new FacialPoint(FacialPointType.N, nasionCandidate);
        facialPoints.add(nasionFP);
    }
    
    public void findGlabelaFP() {
        //for (int i = 0; i < symmetryPlanePoints.size(); i++) { // kvoli nespravnemu poradiu bodov sym roviny, prehladavam cely zoznam
        
        if (nasionFP == null) {
            return;
        }
        
        Point3f glabelaCandidate = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for(Point3f symPoint : symmetryPlanePoints) {
            if (symPoint.y > nasionFP.getPosition().y) {
                if (symPoint.z > glabelaCandidate.z)
                    glabelaCandidate = symPoint;
            }
        }
        
        glabelaFP = new FacialPoint(FacialPointType.G, glabelaCandidate);
        facialPoints.add(glabelaFP);
    }
    
    
    public void findLabralesFPs(){
        
        if (stomionFP == null) {
            return;
        }
        
        Point3f labraleSupCandidate = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        Point3f labraleInfCandidate = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
          
        for(Point3f symPoint : symmetryPlanePoints) {
            if (symPoint.y > stomionFP.getPosition().y && symPoint.y < stomionFP.getPosition().y + SEARCH_THRESHOLD) {
                if (symPoint.z > labraleSupCandidate.z)
                    labraleSupCandidate = symPoint;
            } else if (symPoint.y < stomionFP.getPosition().y && symPoint.y > stomionFP.getPosition().y - SEARCH_THRESHOLD) {
                if (symPoint.z > labraleInfCandidate.z)
                    labraleInfCandidate = symPoint;
            }
        }
        
        labraleSuperiusFP = new FacialPoint(FacialPointType.LS, labraleSupCandidate);
        labraleInferiusFP = new FacialPoint(FacialPointType.LI, labraleInfCandidate);
        
        facialPoints.add(labraleSuperiusFP);
        facialPoints.add(labraleInferiusFP);
    }
    
    public void findSublabialeFP(){
        
        if (labraleInferiusFP == null){
            return;
        }
        
        Point3f sublabialeCandidate = new Point3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
          
        // prva moznost, bez predpokladu, ze mnozina bodov symetrickej roviny je usporiadana
        for(Point3f symPoint : symmetryPlanePoints) {
            if (symPoint.y < labraleInferiusFP.getPosition().y && symPoint.y > labraleInferiusFP.getPosition().y - SEARCH_THRESHOLD) {
                if (symPoint.z < sublabialeCandidate.z)
                    sublabialeCandidate = symPoint;
            }
        }
        
        // druha moznost, s predpokladom usporiadania
//        Point3f previousPoint = symmetryPlanePoints.getFirst(); // sublabialeCandidate
//        for(int i = 1; i < symmetryPlanePoints.size(); i++){
//            Point3f symPoint = symmetryPlanePoints.get(i);
//            if (symPoint.y < labraleInferiusFP.y && symPoint.y < previousPoint.y && symPoint.y > labraleInferiusFP.y - SEARCH_THRESHOLD) {
//                if (symPoint.z > previousPoint.z) {
//                    sublabialeCandidate = previousPoint;
//                    break;
//                }          
//                previousPoint = symPoint;
//            }
//        }
        
        sublabialeFP = new FacialPoint(FacialPointType.SL, sublabialeCandidate);
        facialPoints.add(sublabialeFP);
    }
    
    public void findPogonionFP(){
        
        if (sublabialeFP == null){
            return;
        }
        
        Point3f pogonionCandidate = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
          
        // prva moznost, bez predpokladu, ze mnozina bodov symetrickej roviny je usporiadana
        for(Point3f symPoint : symmetryPlanePoints) {
            if (symPoint.y < sublabialeFP.getPosition().y && symPoint.y > sublabialeFP.getPosition().y - SEARCH_THRESHOLD * 2) {
                if (symPoint.z > pogonionCandidate.z)
                    pogonionCandidate = symPoint;
            }
        }
        
        // druha moznost, s predpokladom usporiadania
//        Point3f previousPoint = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);//symmetryPlanePoints.getFirst(); // pogonionCandidate
//        for(int i = 1; i < symmetryPlanePoints.size(); i++){
//            Point3f symPoint = symmetryPlanePoints.get(i);
//            if (symPoint.y < sublabialeFP.y && symPoint.y < previousPoint.y && symPoint.y > sublabialeFP.y - SEARCH_THRESHOLD) {
//                if (symPoint.z < previousPoint.z) {
//                    pogonionCandidate = previousPoint;
//                    break;
//                }          
//                previousPoint = symPoint;
//            }
//        }
        
        pogonionFP = new FacialPoint(FacialPointType.PG, pogonionCandidate);
        facialPoints.add(pogonionFP);
    }
    
    
    private Point3f findMiddlePoint(FacialPointType type1, FacialPointType type2, boolean onSymmetryPlane){
        FacialPoint fp1 = getFPpoint(facialPoints, type1);
        FacialPoint fp2 = getFPpoint(facialPoints, type2);
        
        if (fp1 == null || fp2 == null) {
            return null;
        }
        
        Point3f pointApprox = calculateAveragePoint(fp1, fp2);
        
        if (onSymmetryPlane){
            int newPointIdx = findNearestPointOnSymmetryPlane(pointApprox, symmetryPlanePoints);
            return symmetryPlanePoints.get(newPointIdx);
        } else {
            return pointApprox;
        }
    }
    
    public static FacialPoint getFPpoint(List<FacialPoint> facialPoints, FacialPointType type) {
        for (FacialPoint fp : facialPoints) {
            if (fp.getType() == type)
                return fp;
        }
        return null;
    }
    
    private Point3f calculateAveragePoint(FacialPoint p1, FacialPoint p2){
        Point3f newPoint = new Point3f();
        newPoint.setX((p1.getPosition().x + p2.getPosition(). x) / 2);
        newPoint.setY((p1.getPosition().y + p2.getPosition().y) / 2);
        newPoint.setZ((p1.getPosition().z + p2.getPosition().z) / 2);
        
        return newPoint;
    }
    
    private int findNearestPointOnSymmetryPlane(Point3f point, LinkedList<Point3f> symmetryPlanePoints){
        int nearestPointIdx = 0;
        
        for(int i = 1; i < symmetryPlanePoints.size(); i++){
            if (point.distance(symmetryPlanePoints.get(i)) < point.distance(symmetryPlanePoints.get(nearestPointIdx))) {
                nearestPointIdx = i;
            }
        }      
        return nearestPointIdx;
    }

    public List<FacialPoint> getFacialPoints() {
        return facialPoints;
    }
    
    public List<Vector3f> getFPcoords(){
        List<Vector3f> pos = new ArrayList<>(facialPoints.size());
        
        for(FacialPoint fp : facialPoints){
            pos.add(fp.getPosition());
        }
        
        return pos;
    }
}