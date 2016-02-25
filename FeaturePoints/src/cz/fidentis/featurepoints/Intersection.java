package cz.fidentis.featurepoints;

import cz.fidentis.model.Model;
import cz.fidentis.featurepoints.symmetryplane.PlaneEquation;
import cz.fidentis.featurepoints.symmetryplane.Triangle;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class Intersection {

    public static float THRESHOLD = 0.1f;
    public static float THRESHOLD2 = 0.00001f;

    public static Vector3f getIntersectionBetweenLineAndPlane(PlaneEquation eq, Vector3f one, Vector3f two) {

        float Xa = one.getX();
        float Ya = one.getY();
        float Za = one.getZ();
        float k;

        //A,B,C,D from Ax+By+Cz+d=0
        float A = eq.getA();
        float B = eq.getB();
        float C = eq.getC();
        float D = eq.getD();

        //case when plane and polygon have the common tangent
        if (isPointWithinPlane(eq, one) && isPointWithinPlane(eq, two)) {
            return null;
        }

        try {
            //for parallel it'will be an exception 
            k = -(A * Xa + B * Ya + C * Za + D) / (A * (two.getX() - one.getX())
                    + B * (two.getY() - one.getY())
                    + C * (two.getZ() - one.getZ()));
        } catch (Exception e) {
            System.out.println("Face je paralelný.");
            return null;
        }

        if (Math.abs(k) > 1 || k < 0) {
            return null;
        }

        float X0 = k * (two.getX() - one.getX()) + Xa;
        float Y0 = k * (two.getY() - one.getY()) + Ya;
        float Z0 = k * (two.getZ() - one.getZ()) + Za;

        Vector3f resPoint = new Vector3f(X0, Y0, Z0);

        return resPoint;
    }

    public static List<Vector3f> getIntersectionBetweenTriangleAndPlane(PlaneEquation eq, Triangle triangle) {

        Vector3f one = triangle.getA();
        Vector3f two = triangle.getB();
        Vector3f three = triangle.getC();

        ArrayList<Vector3f> intersectPoints = new ArrayList<>();

        intersectPoints = addNotNull(intersectPoints, getIntersectionBetweenLineAndPlane(eq, one, two));
        intersectPoints = addNotNull(intersectPoints, getIntersectionBetweenLineAndPlane(eq, two, three));
        intersectPoints = addNotNull(intersectPoints, getIntersectionBetweenLineAndPlane(eq, three, one));

        if (intersectPoints.size() == 2) {
//            if (equalsWithThreshold(intersectPoints.get(0), intersectPoints.get(1))) {
//                intersectPoints.remove(1);
//            }
        }

        if (intersectPoints.size() == 3) {
            System.out.println("tri body v rovine");
            if (equalsWithThreshold(intersectPoints.get(0), intersectPoints.get(1))) {
                intersectPoints.remove(1);
            } else if (equalsWithThreshold(intersectPoints.get(1), intersectPoints.get(2))) {
                intersectPoints.remove(2);
            } else if (equalsWithThreshold(intersectPoints.get(2), intersectPoints.get(0))) {
                intersectPoints.remove(2);
            }
        }

        return intersectPoints;
    }

    public static boolean isPointWithinPlane(PlaneEquation eq, Vector3f point) {
        double value = (eq.getA() * point.getX()
                + eq.getB() * point.getY()
                + eq.getC() * point.getZ()
                + eq.getD());

        return (Math.abs(value) / eq.getMAX() < THRESHOLD);
    }

    public static LinkedList<Point3f> computeSymmetryPlanePoints(Model model, Set<SimpleEdge> boundaryEdges,
            CornerTable cornerTable, ArrayList<Vector3f> centerPoints) {

//        for (int i = 0; i < model.getVerts().size(); i++) {
//            System.out.println("vertex: " + i + ", :" + model.getVerts().get(i));
//        }
        PlaneEquation planeEq = createPlaneEquation(centerPoints);
        Corner highestIntersectCorner = null;
        Vector3f highestIntersectPoint = null;
        Integer highestIntersectionFace = null;
        ArrayList<Integer> intersectionFaces = new ArrayList<>();
        ArrayList<TriangleIntersectionPair> intersectionPairs = new ArrayList<>();

        for (SimpleEdge edge : boundaryEdges) {
            Vector3f point = getIntersectionBetweenLineAndPlane(planeEq, model.getVerts().get(edge.getFirstPoint()), model.getVerts().get(edge.getSecondPoint()));

            if (point != null) {
                if (highestIntersectPoint == null || point.y > highestIntersectPoint.y) {
                    highestIntersectPoint = point;
                }
            }
        }

        for (int j = 0; j < model.getFaces().getNumFaces(); j++) {
            int[] faceVertsIx = model.getFaces().getFaceVertIdxs(j);
            Vector3f p1 = model.getVerts().get(faceVertsIx[0] - 1);
            Vector3f p2 = model.getVerts().get(faceVertsIx[1] - 1);
            Vector3f p3 = model.getVerts().get(faceVertsIx[2] - 1);

            Triangle triangle = new Triangle(p1, p2, p3);
            List intersectionPoints = getIntersectionBetweenTriangleAndPlane(planeEq, triangle);

            if (intersectionPoints.size() == 2) {
                intersectionPairs.add(new TriangleIntersectionPair(intersectionPoints));
            }
            else if (intersectionPoints.size() == 1) {
            }

            if (!intersectionPoints.isEmpty()) {
                intersectionFaces.add(j);
            }
        }
        
        LinkedList<Point3f> symmetryPlanePoints = new LinkedList<>();
        symmetryPlanePoints.add(new Point3f(highestIntersectPoint));
        
        symmetryPlanePoints = sortIntersectionPoints(intersectionPairs, symmetryPlanePoints);

//        System.out.println("intersekšn fejsis: " + intersectionFaces.size());

//        // Najdi najvyssi bod modelu, ktory pretina medianna (symetricka) rovina
//        for (Integer i : boundaryVertices) {
//            Corner corner = cornerTable.corner(i);
//
//            Vector3f point = getIntersectionBetweenLineAndPlane(planeEq, model.getVerts().get(corner.vertex), model.getVerts().get(corner.next.vertex));
//
//            if (point != null) {
//                if (highestIntersectCorner == null || point.y > highestIntersectPoint.y) {
//                    highestIntersectCorner = corner;
//                    highestIntersectPoint = point;
//                }
//            }
//        }
//        Set<Corner> visitedCorners = new HashSet<>();
//        Corner[] toVisitCorners;
//        Corner actualCorner = highestIntersectCorner;
        //ArrayList<Integer> visitedFaces = new ArrayList<>();
        //intersectionFaces.remove(highestIntersectionFace);

//        toVisitCorners = actualCorner.vertexNeighbors();
//        visitedCorners.add(actualCorner);
        
        
        
//        System.out.println("start intersekšn");
//        boolean noChange = false;
//        int lastSize = -1;
//        while (intersectionFaces.size() != lastSize) { //zazracna konstanta.. pravdepodobne nevyberam spravny zaciatocny bod
//            lastSize = intersectionFaces.size();
//            int iter = 0;
//            for (Integer intersectionFace : intersectionFaces) {
//
//                int[] faceVertsIx = model.getFaces().getFaceVertIdxs(intersectionFace);
//                Vector3f p1 = model.getVerts().get(faceVertsIx[0] - 1);
//                Vector3f p2 = model.getVerts().get(faceVertsIx[1] - 1);
//                Vector3f p3 = model.getVerts().get(faceVertsIx[2] - 1);
//
//                Triangle triangle = new Triangle(p1, p2, p3);
//                ArrayList<Vector3f> intersectionPoints = new ArrayList<>(getIntersectionBetweenTriangleAndPlane(planeEq, triangle));
//                // symmetryPlanePoints.addAll(intersectionPoints);
//
//                if (intersectionPoints.size() == 1) {
//                    // V prípade, ze je len jeden prienik, rovina sa dotyka roviny, prienik je v jednom bode,
//                    intersectionFaces.remove(intersectionFace);
//                    break;
////                    System.out.println("last sym point - > " + symmetryPlanePoints.get(symmetryPlanePoints.size() - 1).toString());
////                    System.out.println("intersct point - > " + intersectionPoints.get(0).toString());
////                    if (equalsWithThreshold(symmetryPlanePoints.get(symmetryPlanePoints.size() - 1), intersectionPoints.get(0))) {
////                        intersectionFaces.remove(intersectionFace);
////                        break;
////                    }
//                } else if (intersectionPoints.size() == 2) {
////                    System.out.println("iter: " + iter);
////                    iter++;
////                    System.out.println("last sym point - > " + symmetryPlanePoints.get(symmetryPlanePoints.size() - 1).toString());
////                    System.out.println("intersct point1 - > " + intersectionPoints.get(0).toString());
////                    System.out.println("intersct point1 - > " + intersectionPoints.get(1).toString());                    
//                    if (equalsWithThreshold(symmetryPlanePoints.get(symmetryPlanePoints.size() - 1), intersectionPoints.get(0))) {
//                        symmetryPlanePoints.add(intersectionPoints.get(1));
//                        intersectionFaces.remove(intersectionFace);
//                        break;
//                    }
//                    if (equalsWithThreshold(symmetryPlanePoints.get(symmetryPlanePoints.size() - 1), intersectionPoints.get(1))) {
//                        symmetryPlanePoints.add(intersectionPoints.get(0));
//                        intersectionFaces.remove(intersectionFace);
//                        break;
//                    }
//
//                }
//            }
            System.out.println("symetry points: " + symmetryPlanePoints.size());
////            noChange = true;
//        }
//
//        System.out.println("hotovo intersekšn");
//        System.out.println("symetry points: " + symmetryPlanePoints.size());
//
//        for (int i = 0; i < symmetryPlanePoints.size(); i++) {
//            System.out.println("Point " + i + " -> " + symmetryPlanePoints.get(i).toString());
//        }

//        for (int i = 0; i < toVisitCorners.length; i++) {
//            Vector3f p;
//            actualCorner = toVisitCorners[i];
//            if (!visitedCorners.contains(actualCorner)) {
//                visitedCorners.add(toVisitCorners[i]);
//                p = getIntersectionBetweenLineAndPlane(planeEq, verts.get(actualCorner.vertex + 1), verts.get(actualCorner.next.vertex + 1));
//            }
//            
//            if (p != null) {
//                symmetryPlanePoints.add(p);
//                
//            }
//        }
//        
//        for (Corner corner : cornerTable.corners()){
//            if (corner )
//        }
        return symmetryPlanePoints;
    }

    public static PlaneEquation createPlaneEquation(ArrayList<Vector3f> centerPoints) {

        Vector3f minYpoint = null;
        Vector3f maxYpoint = null;
        Vector3f maxZpoint = null;

        for (Vector3f vect : centerPoints) {
            if (minYpoint == null) {
                minYpoint = maxYpoint = maxZpoint = vect;
            } else {
                if (vect.y < minYpoint.y) {
                    minYpoint = vect;
                }
                if (vect.y > maxYpoint.y) {
                    maxYpoint = vect;
                }
                if (vect.z > maxZpoint.z) {
                    maxZpoint = vect;
                }
            }
        }

        maxZpoint = new Vector3f(maxZpoint.x, maxZpoint.y, maxZpoint.z + Math.abs(maxZpoint.z));
        maxYpoint = new Vector3f(maxYpoint.x, maxYpoint.y + Math.abs(maxYpoint.y), maxYpoint.z);
        minYpoint = new Vector3f(minYpoint.x, minYpoint.y - Math.abs(minYpoint.y), minYpoint.z);

        return new PlaneEquation(new Triangle(minYpoint, maxZpoint, maxYpoint));
    }

    // 
    public static ArrayList<Vector3f> createPlanePoints(ArrayList<Vector3f> centerPoints) {

        Vector3f minYpoint = null;
        Vector3f maxYpoint = null;
        Vector3f maxZpoint = null;

        ArrayList<Vector3f> planePoints = new ArrayList<>();

        for (Vector3f vect : centerPoints) {
            if (minYpoint == null) {
                minYpoint = maxYpoint = maxZpoint = vect;
            } else {
                if (vect.y < minYpoint.y) {
                    minYpoint = vect;
                }
                if (vect.y > maxYpoint.y) {
                    maxYpoint = vect;
                }
                if (vect.z > maxZpoint.z) {
                    maxZpoint = vect;
                }
            }
        }

        maxZpoint = new Vector3f(maxZpoint.x, maxZpoint.y, maxZpoint.z + 0.2f * Math.abs(maxZpoint.z));
        maxYpoint = new Vector3f(maxYpoint.x, maxYpoint.y + 0.2f * Math.abs(maxYpoint.y), maxYpoint.z);
        minYpoint = new Vector3f(minYpoint.x, minYpoint.y - 0.2f * Math.abs(minYpoint.y), minYpoint.z);

        System.out.println("max z: " + maxZpoint.toString());
        System.out.println("max y: " + maxYpoint.toString());
        System.out.println("min y: " + minYpoint.toString());

        return planePoints;
    }

    private static ArrayList<Vector3f> addNotNull(ArrayList<Vector3f> intersectPoints, Vector3f point) {
        if (point != null) {
            intersectPoints.add(point);
        }
        return intersectPoints;
    }

    private static boolean equalsWithThreshold(Vector3f p1, Vector3f p2) {

        if (Math.abs(Math.abs(p1.x) - Math.abs(p2.x)) > THRESHOLD2) {
            return false;
        } else if (p1 == null || p2 == null) {
            return false;
        } else if (Math.abs(Math.abs(p1.y) - Math.abs(p2.y)) > THRESHOLD2) {
            return false;
        } else if (Math.abs(Math.abs(p1.z) - Math.abs(p2.z)) > THRESHOLD2) {
            return false;
        } else {
            return true;
        }
    }

    public static List<Vector3f> computeFiniteDerivative(List<Vector3f> symmetryPlanePoints) {
        ArrayList<Vector3f> newList = new ArrayList<>();
//        for (int i = 1; i < symmetryPlanePoints.size(); i++) {
//            Vector3f p1 = symmetryPlanePoints.get(i);
//            Vector3f p2 = symmetryPlanePoints.get(i - 1);
//
//            float deriv = Math.abs(Math.abs(p1.z) - Math.abs(p2.z)) / Math.abs(Math.abs(p1.y) - Math.abs(p2.y));
//            System.out.println("'Derivácia' v bode " + i + "-> " + deriv);
//            if (deriv > 1) {
//                newList.add(p2);
//            }
//
//        }

        for (int i = 1; i < symmetryPlanePoints.size() - 1; i++) {
            Vector3f p1 = symmetryPlanePoints.get(i - 1);
            Vector3f p2 = symmetryPlanePoints.get(i);
            Vector3f p3 = symmetryPlanePoints.get(i + 1);

            Vector3f v1 = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
            Vector3f v2 = new Vector3f(p2.x - p3.x, p2.y - p3.y, p2.z - p3.z);

            float angle = v1.angle(v2);
            float angleInDeg = angle * (float) 57.2957795;
            System.out.println("Uhol v bode " + i + "-> " + angle + "rad. -> " + angleInDeg + "°");
            if (angleInDeg < 160.f) {
                newList.add(p2);
            }
        }
        return newList;
    }
    
    private static LinkedList<Point3f> sortIntersectionPoints(ArrayList<TriangleIntersectionPair> intersectionPairs, LinkedList<Point3f> symmetryPlanePoints){
        
        TriangleIntersectionPair nearestPair = null;
        Point3f nearestPoint = null;
        
        while (!intersectionPairs.isEmpty()) {
            Point3f symP = symmetryPlanePoints.getLast();
            boolean firstIter = true;
            
            for (TriangleIntersectionPair pair : intersectionPairs) {
                
                Point3f intersectP1 = pair.getFirstPoint();
                Point3f intersectP2 = pair.getSecondPoint();
                
                if (firstIter) {
                    nearestPoint = intersectP1;
                    nearestPair = pair;
                    firstIter = false;
                }
                
                if (symP.distance(intersectP1) <= symP.distance(nearestPoint) ){
                    nearestPoint = intersectP2;
                    nearestPair = pair;
                }
                
                if (symP.distance(intersectP2) <= symP.distance(nearestPoint) ){
                    nearestPoint = intersectP1;
                    nearestPair = pair;
                }   
            }           
            symmetryPlanePoints.add(nearestPoint);
            intersectionPairs.remove(nearestPair);
        }
        
        return symmetryPlanePoints;
    }

}

class TriangleIntersectionPair {
    
    Point3f[] intersectionPair;
    public TriangleIntersectionPair (List<Vector3f> intersectionPoints){
        
        intersectionPair = new Point3f[2];
        
        if (intersectionPoints.size() == 2) {
            intersectionPair[0] = new Point3f(intersectionPoints.get(0));
            intersectionPair[1] = new Point3f(intersectionPoints.get(1));
        }
    }
    
    public Point3f getFirstPoint(){
        return intersectionPair[0];
    }
    
    public Point3f getSecondPoint(){
        return intersectionPair[1];
    }
}
