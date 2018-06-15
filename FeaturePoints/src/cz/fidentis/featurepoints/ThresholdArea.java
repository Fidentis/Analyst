package cz.fidentis.featurepoints;

import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import jv.geom.PgElementSet;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;

/**
 *
 * @author Galvi
 */
public class ThresholdArea {
    
    private static int HIST_SIZE = 5000;
    
    private PgElementSet elementSet;
    private CornerTable cornerTable;
    private Set<Integer> visitedVertices;
    private Set<Integer> boundaryVertices;
    private HashSet<Integer> tmpThresholdBigestRegion;
    private HashSet<Integer> thresholdBigestRegion;
    
    private PdVector rightCheilion;
    private PdVector leftCheilion;
    private PdVector stomion;
    private PdVector pronasale;
    private PdVector rightEktokantion;
    private PdVector leftEktokantion;
    private PdVector rightEntokantion;
    private PdVector leftEntokantion;
    private PdVector subnasale;
    
    public enum EllipticalType {
        Convex,
        Concave
    }

    public ThresholdArea(PgElementSet elementSet, CornerTable cornerTable, Set<Integer> boundaryVertices) {
        this.elementSet = elementSet;
        this.cornerTable = cornerTable;
        this.boundaryVertices = boundaryVertices;
        tmpThresholdBigestRegion = new HashSet<>();
        visitedVertices = new HashSet<>();
        thresholdBigestRegion = new HashSet<>();

    }

    public ThresholdArea(Set<Integer> boundaryVertices) {
        this.boundaryVertices = boundaryVertices;
    }

    public ThresholdArea() {
    }

    public double calculateThreshold(double[] values, double thres) {
        double[] minMax = findMinAndMax(values);
        double min = minMax[0];
        double max = minMax[1];

        return (max - min) * (1 - thres) + min;
    }

    public double calculateAutoThreshold(double[] values, AutoThresholder.Method thresMetod) {
        double[] minMax = findMinAndMax(values);
        double min = minMax[0];
        double max = minMax[1];

        double iterValue = Math.abs(min - max) / HIST_SIZE;

        int[] histogram = new int[HIST_SIZE];

        for (int i = 0; i < values.length; i++) {
            if (!boundaryVertices.contains(i)) {
                for (int j = 0; j < HIST_SIZE; j++) {
                    if (values[i] <= (min + ((j + 1) * iterValue))) {
                        histogram[j]++;
                        break;
                    }
                }
            }
        }


        double threshold = new AutoThresholder().getThreshold(thresMetod, histogram);

        return min + iterValue * threshold;
    }
    
    public double calculateAutoThreshold(Set<Integer> thresholdVertices, double[] values, AutoThresholder.Method thresMetod) {
        double[] minMax = findMinAndMax(values, thresholdVertices);
        double min = minMax[0];
        double max = minMax[1];

        double iterValue = Math.abs(min - max) / HIST_SIZE;

        int[] histogram = new int[HIST_SIZE];

        for (int i = 0; i < values.length; i++) {
            if (!boundaryVertices.contains(i) && thresholdVertices.contains(i)) {
                for (int j = 0; j < HIST_SIZE; j++) {
                    if (values[i] <= (min + ((j + 1) * iterValue))) {
                        histogram[j]++;
                        break;
                    }
                }
            }
        }

        double threshold = new AutoThresholder().getThreshold(thresMetod, histogram);

        return min + iterValue * threshold;
    }

    public double[] findMinAndMax(double[] values) {
        double[] minMax = {values[0], values[0]};

        for (int i = 1; i < values.length; i++) {
            if (!boundaryVertices.contains(i)) {
                if (values[i] < minMax[0]) {
                    minMax[0] = values[i];
                }

                if (values[i] > minMax[1]) {
                    minMax[1] = values[i];
                }
            }
        }
        return minMax;
    }

    public double[] findMinAndMax(double[] values, Set<Integer> thresholdVertices) {
        double[] minMax = {values[0], values[0]};

        for (int i = 1; i < values.length; i++) {
            if (!boundaryVertices.contains(i) && thresholdVertices.contains(i)) {
                if (values[i] < minMax[0]) {
                    minMax[0] = values[i];
                }

                if (values[i] > minMax[1]) {
                    minMax[1] = values[i];
                }
            }
        }
        return minMax;
    }



    public Set<Integer> getThresholdFaces(double[] values, double threshold) {

        Set<Integer> thresholdFaces = new HashSet<Integer>();
        PiVector face;
        for (int i = 0; i < elementSet.getNumElements(); i++) {
            face = elementSet.getElement(i);
            if (!(boundaryVertices.contains(face.getEntry(0))
                    || boundaryVertices.contains(face.getEntry(1))
                    || boundaryVertices.contains(face.getEntry(2)))
                    && (((values[face.getEntry(0)] > threshold)
                    || (values[face.getEntry(1)] > threshold)
                    || (values[face.getEntry(2)] > threshold)))) {

                thresholdFaces.add(i);
            }
        }
        return thresholdFaces;
    }
    
        public Set<Integer> getThresholdMinimalFaces(double[] values, double threshold) {

        Set<Integer> thresholdFaces = new HashSet<Integer>();
        PiVector face;
        for (int i = 0; i < elementSet.getNumElements(); i++) {
            face = elementSet.getElement(i);
            if (!(boundaryVertices.contains(face.getEntry(0))
                    || boundaryVertices.contains(face.getEntry(1))
                    || boundaryVertices.contains(face.getEntry(2)))
                    && (((values[face.getEntry(0)] < threshold)
                    || (values[face.getEntry(1)] < threshold)
                    || (values[face.getEntry(2)] < threshold)))) {

                thresholdFaces.add(i);
            }
        }
        return thresholdFaces;
    }

    public Set<Integer> getThresholdVertices(Set<Integer> thresholdFaces) {

        Set<Integer> thresholdVertices = new HashSet<Integer>();
        PiVector elem;
        for (Integer i : thresholdFaces) {
            elem = elementSet.getElement(i);
            for (int j = 0; j < 3; j++) {
                thresholdVertices.add(elem.getEntry(j));
            }
        }
        return thresholdVertices;
    }

    public Set<Integer> getThresholdVertices(Set<Integer> thresholdVertices, double threshold, double[] values) {
        Set<Integer> tmpThresholdVertices = new HashSet<Integer>();
        for (Integer i : thresholdVertices) {
            if (values[i] > threshold) {
                tmpThresholdVertices.add(i);
            }
        }
        return tmpThresholdVertices;
    }

    public Set<SimpleEdge> getThresholdEdges(Set<Integer> thresholdFaces) {

        Set<SimpleEdge> thresholdEdges = new HashSet<SimpleEdge>();
        PiVector elem;
        int k;
        for (Integer i : thresholdFaces) {
            elem = elementSet.getElement(i);
            SimpleEdge edge;
            for (int j = 0; j < 3; j++) {
                if (j < 2) {
                    k = j + 1;
                } else {
                    k = 0;
                }

                edge = new SimpleEdge(elem.getEntry(j), elem.getEntry(k));

                thresholdEdges.add(edge);
            }
        }
        return thresholdEdges;
    }

    public void getThresholdBigestNeighbourhood(int vertIndex, Set<Integer> thresholdVertices) {

        Corner corner = cornerTable.getCorner(vertIndex);
        for (Corner neighbor : corner.vertexNeighbors()) {
            if (thresholdVertices.contains(neighbor.vertex) && visitedVertices.add(neighbor.vertex)
                    && tmpThresholdBigestRegion.add(neighbor.vertex)) {
                getThresholdBigestNeighbourhood(neighbor.vertex, thresholdVertices);
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public Set<Integer> findBigestRegion(Set<Integer> thresholdVertices) {

        thresholdBigestRegion.clear();
        tmpThresholdBigestRegion.clear();
        visitedVertices.clear();

        for (Integer i : thresholdVertices) {
            if (visitedVertices.add(i)) {
                getThresholdBigestNeighbourhood(i, thresholdVertices);

                if (tmpThresholdBigestRegion.size() > thresholdBigestRegion.size()) {
                    thresholdBigestRegion = (HashSet<Integer>) tmpThresholdBigestRegion.clone();
                }
                tmpThresholdBigestRegion.clear();
            }
        }
        return thresholdBigestRegion;
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> findNoseTip(Set<Integer> thresholdFaces) {

        thresholdBigestRegion.clear();
        tmpThresholdBigestRegion.clear();
        visitedVertices.clear();

        pronasale = new PdVector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        double maxArea = 0.0;

        Set<SimpleEdge> thresholdEdges = getThresholdEdges(thresholdFaces);
        Set<Integer> thresholdVertices = getThresholdVertices(thresholdFaces);
        Corner actualCorner;

        for (Corner corner : cornerTable.corners()) {
            if (visitedVertices.add(corner.vertex) && visitedVertices.contains(corner.vertex)) {

                actualCorner = corner;
                PdVector tmpPronasale = elementSet.getVertex(actualCorner.vertex);
                tmpThresholdBigestRegion.add(actualCorner.vertex);

// variables for find biggest square area                
//                double minX = Double.MAX_VALUE;
//                double maxX = Double.MIN_VALUE;
//                double minY = Double.MAX_VALUE;
//                double maxY = Double.MIN_VALUE;

                boolean continueFlag = true;
                HashSet<Corner> nextCorners = new HashSet<Corner>();

                while (continueFlag) {
                    for (Corner corner2 : actualCorner.vertexNeighbors()) {
                        if (thresholdVertices.contains(corner2.vertex) && tmpThresholdBigestRegion.add(corner2.vertex)
                                && thresholdEdges.contains(new SimpleEdge(actualCorner.vertex, corner2.vertex))
                                && visitedVertices.add(corner2.vertex)) {

                            if (elementSet.getVertex(corner2.vertex).getEntry(2) > tmpPronasale.getEntry(2)) {
                                tmpPronasale = elementSet.getVertex(corner2.vertex);
                            }

// find values for biggest square area                             
//                            if (elementSet.getVertex(corner2.vertex).getEntry(0) < minX) {
//                                minX = elementSet.getVertex(corner2.vertex).getEntry(0);
//                            }
//
//                            if (elementSet.getVertex(corner2.vertex).getEntry(0) > maxX) {
//                                maxX = elementSet.getVertex(corner2.vertex).getEntry(0);
//                            }
//
//                            if (elementSet.getVertex(corner2.vertex).getEntry(1) < minY) {
//                                minY = elementSet.getVertex(corner2.vertex).getEntry(1);
//                            }
//
//                            if (elementSet.getVertex(corner2.vertex).getEntry(1) > maxY) {
//                                maxY = elementSet.getVertex(corner2.vertex).getEntry(1);
//                            }

                            nextCorners.add(corner2);

                        }
                    }

                    if (!nextCorners.isEmpty()) {
                        actualCorner = nextCorners.iterator().next();
                        nextCorners.remove(actualCorner);
                    } else {
                        continueFlag = false;
                    }

                }

                if (tmpThresholdBigestRegion.size() > thresholdBigestRegion.size()) {
                    /*(Math.abs(minX - maxX) * Math.abs(minY - maxY)) > maxArea)
                     * (tmpPronasale.getEntry(2) > pronasale.getEntry(2))
                     * maxArea = Math.abs(minX - maxX) * Math.abs(minY - maxY); */

                    if (tmpPronasale.getEntry(2) > pronasale.getEntry(2)) {
                        pronasale = tmpPronasale;
                    }
                    thresholdBigestRegion = (HashSet<Integer>) tmpThresholdBigestRegion.clone();
                }

                tmpThresholdBigestRegion.clear();
            }
        }
        findPronasale(thresholdBigestRegion);
        return thresholdBigestRegion;
    }
    
    public void findPronasale(Set<Integer> thresholdVertices) {
        PdVector tmpPronasale = new PdVector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        for (Integer i : thresholdVertices) {
            if (elementSet.getVertex(i).getEntry(2) > tmpPronasale.getEntry(2)) {
                tmpPronasale = elementSet.getVertex(i);
            }
        }
        pronasale = tmpPronasale;
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> findMouth(Set<Integer> thresholdFaces, double threshold, double[] values, PdVector pronasale) {
        
        this.pronasale = pronasale;
        
        thresholdBigestRegion.clear();
        tmpThresholdBigestRegion.clear();
        visitedVertices.clear();

        double maxXdistance = 0;
        PdVector vertWithMaxCurvatureValue = new PdVector();
        

        Set<SimpleEdge> thresholdEdges = getThresholdEdges(thresholdFaces);
        Set<Integer> thresholdVertices = getThresholdVertices(thresholdFaces);
        Corner actualCorner;

        for (Corner corner : cornerTable.corners()) {
            if (visitedVertices.add(corner.vertex) && visitedVertices.contains(corner.vertex)) {

                actualCorner = corner;
                tmpThresholdBigestRegion.add(actualCorner.vertex);

                double minX = elementSet.getVertex(actualCorner.vertex).getEntry(0);
                double maxX = elementSet.getVertex(actualCorner.vertex).getEntry(0);
                
                int indOfVertWithMaxCurvatureValue = actualCorner.vertex;

                boolean continueFlag = true;
                HashSet<Corner> nextCorners = new HashSet<Corner>();

                while (continueFlag) {
                    for (Corner corner2 : actualCorner.vertexNeighbors()) {
                        if (thresholdVertices.contains(corner2.vertex) && tmpThresholdBigestRegion.add(corner2.vertex)
                                && thresholdEdges.contains(new SimpleEdge(actualCorner.vertex, corner2.vertex))
                                && visitedVertices.add(corner2.vertex)) {

                            if (elementSet.getVertex(corner2.vertex).getEntry(0) < minX) {
                                minX = elementSet.getVertex(corner2.vertex).getEntry(0);
                            }

                            if (elementSet.getVertex(corner2.vertex).getEntry(0) > maxX) {
                                maxX = elementSet.getVertex(corner2.vertex).getEntry(0);
                            }

                            if (values[corner2.vertex] > threshold && values[corner2.vertex] > values[indOfVertWithMaxCurvatureValue]) {
                                indOfVertWithMaxCurvatureValue = corner2.vertex;
                            }

                            nextCorners.add(corner2);

                        }
                    }

                    if (!nextCorners.isEmpty()) {
                        actualCorner = nextCorners.iterator().next();
                        nextCorners.remove(actualCorner);
                    } else {
                        continueFlag = false;
                    }

//                    if (i == actualCorner.vertex) {
//                        continueFlag = false;
//                    }
                }

                if (Math.abs(minX - maxX) > maxXdistance /*&& tmpThresholdBigestRegion.size() > thresholdBigestRegion.size()*/) {
                    maxXdistance = Math.abs(minX - maxX);
                    vertWithMaxCurvatureValue = elementSet.getVertex(indOfVertWithMaxCurvatureValue);
                    thresholdBigestRegion = (HashSet<Integer>) tmpThresholdBigestRegion.clone();
                }

                tmpThresholdBigestRegion.clear();
            }
        }
        findCheilions(thresholdBigestRegion, pronasale, threshold, values);
        findStomion(thresholdBigestRegion, pronasale, vertWithMaxCurvatureValue, values);
        return thresholdBigestRegion;
    }

    
    public void findCheilions(Set<Integer> mouthArea, PdVector maxCurvatureValue, double threshold, double[] values) {

        PdVector tmpRightCheilion = new PdVector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        PdVector tmpLeftCheilion = new PdVector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        
        Set<Integer> mouthAreaVertices = getThresholdVertices(mouthArea, threshold, values);
        for (Integer i : mouthAreaVertices) {

            if (elementSet.getVertex(i).getEntry(0) > tmpRightCheilion.getEntry(0)) {
                tmpRightCheilion = elementSet.getVertex(i);
            }
            if (elementSet.getVertex(i).getEntry(0) < tmpLeftCheilion.getEntry(0)) {
                tmpLeftCheilion = elementSet.getVertex(i);
            }
        }
        
        leftCheilion = tmpLeftCheilion;
        rightCheilion = tmpRightCheilion;
        
    }

    public void findStomion(Set<Integer> mouthArea, PdVector pronasale, PdVector maxCurvatureValue, double[] values) {
        double xDistance = Double.MAX_VALUE;
        double yDistance = Double.MAX_VALUE;
        PdVector tmpStomion = new PdVector(0, 0, 0);
        SortedSet<Integer> bigestThresholdValues = new TreeSet<Integer>();
        SortedSet<Double> distances = new TreeSet<Double>();
        
        int iter = 0;
        for (Integer i : mouthArea) {
            double tmpXdistance = axisDistance(elementSet.getVertex(i), pronasale, 0);          
            double tmpYdistance = axisDistance(elementSet.getVertex(i), maxCurvatureValue, 1);
            if (tmpXdistance < xDistance && tmpYdistance < yDistance) {
                xDistance = tmpXdistance;
                yDistance = tmpYdistance;
                tmpStomion = elementSet.getVertex(i);
            }
            
            iter++;
        }
        double tmpX = (leftCheilion.getEntry(0) + rightCheilion.getEntry(0)) / 2;
        double tmpZ = (rightCheilion.getEntry(2) + rightCheilion.getEntry(2)) / 2;
        stomion = new PdVector(tmpX, maxCurvatureValue.getEntry(1), tmpStomion.getEntry(2));
    }
    
    @SuppressWarnings("unchecked")
    public Set<Integer> findEyeArea(Set<Integer> thresholdFaces, double threshold, double[] values, PdVector pronasale, boolean isRightEye) {
        
        this.pronasale = pronasale;
        
        thresholdBigestRegion.clear();
        tmpThresholdBigestRegion.clear();
        visitedVertices.clear();
        
        Set<SimpleEdge> thresholdEdges = getThresholdEdges(thresholdFaces);
        Set<Integer> thresholdVertices = getThresholdVertices(thresholdFaces);
        Corner actualCorner;

        for (Corner corner : cornerTable.corners()) {
            if (visitedVertices.add(corner.vertex) && visitedVertices.contains(corner.vertex)) {

                actualCorner = corner;
                tmpThresholdBigestRegion.add(actualCorner.vertex);

                boolean continueFlag = true;
                HashSet<Corner> nextCorners = new HashSet<Corner>();

                while (continueFlag) {
                    for (Corner corner2 : actualCorner.vertexNeighbors()) {
                        if (thresholdVertices.contains(corner2.vertex) && elementSet.getVertex(corner2.vertex).getEntry(1) > pronasale.getEntry(1)
                                && thresholdEdges.contains(new SimpleEdge(actualCorner.vertex, corner2.vertex))
                                && visitedVertices.add(corner2.vertex)) {
                            
                            if (isRightEye) {
                                if (elementSet.getVertex(corner2.vertex).getEntry(0) > pronasale.getEntry(0)) {
                                    tmpThresholdBigestRegion.add(corner2.vertex);
                                    nextCorners.add(corner2);
                                }
                            } else {
                                if (elementSet.getVertex(corner2.vertex).getEntry(0) < pronasale.getEntry(0)) {
                                    tmpThresholdBigestRegion.add(corner2.vertex);
                                    nextCorners.add(corner2);
                                }
                            }
                        }
                    }

                    if (!nextCorners.isEmpty()) {
                        actualCorner = nextCorners.iterator().next();
                        nextCorners.remove(actualCorner);
                    } else {
                        continueFlag = false;
                    }

//                    if (i == actualCorner.vertex) {
//                        continueFlag = false;
//                    }
                }

                if (tmpThresholdBigestRegion.size() > thresholdBigestRegion.size()) {
                    thresholdBigestRegion = (HashSet<Integer>) tmpThresholdBigestRegion.clone();
                }

                tmpThresholdBigestRegion.clear();
            }
        }
        
        findEktoEntoKantions(thresholdBigestRegion, threshold, values, isRightEye);
        
        return thresholdBigestRegion;
    }
    
    public void findEktoEntoKantions(Set<Integer> eyeArea, double threshold, double[] values, boolean isRightEye) {
        PdVector firstEyePoint = new PdVector(-10000, -10000, -10000);
        PdVector secondEyePoint = new PdVector(10000, 10000, 10000);
        
        //double nextThreshold = calculateAutoThreshold(eyeArea, values, AutoThresholder.Method.Triangle);

        Set<Integer> eyeAreaVertices = getThresholdVertices(eyeArea, threshold/*nextThreshold*/, values);
        
        for (Integer i : eyeAreaVertices) {

            if (elementSet.getVertex(i).getEntry(0) > firstEyePoint.getEntry(0)) {
                firstEyePoint = elementSet.getVertex(i);
            }
            if (elementSet.getVertex(i).getEntry(0) < secondEyePoint.getEntry(0)) {
                secondEyePoint = elementSet.getVertex(i);
            }
        }
        
        if (isRightEye) {
            rightEktokantion = firstEyePoint;
            rightEntokantion = secondEyePoint;
        } else {
            leftEktokantion = secondEyePoint;
            leftEntokantion = firstEyePoint;
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public Set<Integer> findSubnasaleArea(Set<Integer> thresholdFaces, double threshold, double[] values, PdVector pronasale) {
        this.pronasale = pronasale;

        thresholdBigestRegion.clear();
        tmpThresholdBigestRegion.clear();
        visitedVertices.clear();

        Set<SimpleEdge> thresholdEdges = getThresholdEdges(thresholdFaces);
        Set<Integer> thresholdVertices = getThresholdVertices(thresholdFaces);

        PdVector nearestPoint;
        double minDistance = Double.MAX_VALUE;

        Corner actualCorner;

        for (Corner corner : cornerTable.corners()) {
            if (visitedVertices.add(corner.vertex) && visitedVertices.contains(corner.vertex)) {

                actualCorner = corner;
                tmpThresholdBigestRegion.add(actualCorner.vertex);
                nearestPoint = elementSet.getVertex(actualCorner.vertex);

                boolean continueFlag = true;
                HashSet<Corner> nextCorners = new HashSet<Corner>();

                while (continueFlag) {
                    for (Corner corner2 : actualCorner.vertexNeighbors()) {
                        if (thresholdVertices.contains(corner2.vertex) && elementSet.getVertex(corner2.vertex).getEntry(1) < pronasale.getEntry(1)
                                && thresholdEdges.contains(new SimpleEdge(actualCorner.vertex, corner2.vertex))
                                && visitedVertices.add(corner2.vertex)) {

                            PdVector actVert = elementSet.getVertex(corner2.vertex);
                            if (actVert.getEntry(1) < pronasale.getEntry(1)) {
                                tmpThresholdBigestRegion.add(corner2.vertex);

                                nearestPoint.set((nearestPoint.getEntry(0) + actVert.getEntry(0)) / 2,
                                        (nearestPoint.getEntry(1) + actVert.getEntry(1)) / 2,
                                        (nearestPoint.getEntry(2) + actVert.getEntry(2)) / 2);

                                nextCorners.add(corner2);
                            }

                        }
                    }

                    if (!nextCorners.isEmpty()) {
                        actualCorner = nextCorners.iterator().next();
                        nextCorners.remove(actualCorner);
                    } else {
                        continueFlag = false;
                    }

//                    if (i == actualCorner.vertex) {
//                        continueFlag = false;
//                    }
                }

                if /*(distance(pronasale, nearestPoint) < minDistance)*/
                    (tmpThresholdBigestRegion.size() > thresholdBigestRegion.size()) {
                    thresholdBigestRegion = (HashSet<Integer>) tmpThresholdBigestRegion.clone();
                }

                tmpThresholdBigestRegion.clear();
            }
        }
        
        findSubnasale(thresholdBigestRegion, threshold, values, pronasale);

        return thresholdBigestRegion;
    }
    
    public void findSubnasale(Set<Integer> subnasaleArea, double threshold, double[] values, PdVector pronasale) {
        PdVector tmpSubnasale = new PdVector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        Set<Integer> subnasaleAreaVertices = getThresholdVertices(subnasaleArea, threshold, values);
        for (Integer i : subnasaleAreaVertices) {
            if (elementSet.getVertex(i).getEntry(0) > tmpSubnasale.getEntry(0)) {
                tmpSubnasale = elementSet.getVertex(i);
            }
        }

        subnasale = new PdVector(pronasale.getEntry(0), tmpSubnasale.getEntry(1), tmpSubnasale.getEntry(2));
        
    }
    
    public double axisDistance(PdVector p1, PdVector p2, int axis) { //axis x - 0; y - 1; z - 2
        if (p1.getEntry(axis) < p2.getEntry(axis)) {
            return Math.abs(p1.getEntry(axis) - p2.getEntry(axis));
        } else {
            return Math.abs(p2.getEntry(axis) - p1.getEntry(axis));
        }
    }
    
    public double distance(PdVector p1, PdVector p2) {
        return Math.sqrt(Math.pow(p1.getEntry(0) - p2.getEntry(0), 2)
                + Math.pow(p1.getEntry(1) - p2.getEntry(1), 2)
                + Math.pow(p1.getEntry(2) - p2.getEntry(2), 2));
    }

    public Set<PdVector> getFacialPoints() {
        
        Set<PdVector> facialPointsSet = new HashSet<PdVector>();
        
        facialPointsSet.add(pronasale);
        facialPointsSet.add(rightCheilion);
        facialPointsSet.add(leftCheilion);
        facialPointsSet.add(stomion);
        
        return facialPointsSet;
    }
    
    public FacialPoint getFacialPoint(PdVector facialPoint, Integer type){
        return new FacialPoint(type, facialPoint);
    }

    public FacialPoint getPronasaleFP() {
        return getFacialPoint(pronasale, FacialPointType.PRN.ordinal());
    }
    
    public void setPronasale(PdVector pronasale) {
        this.pronasale = pronasale;
    }
    
    // POZOR BILATERALNE BODY SU PREHODENE, DOCASNE ZMENENE GETTERY
    public FacialPoint getRightCheilionFP() {
//        return getFacialPoint(rightCheilion, FacialPointType.CH_R);
        return getFacialPoint(rightCheilion, FacialPointType.CH_L.ordinal());
    }

    public void setRightCheilion(PdVector rightCheilion) {
        this.rightCheilion = rightCheilion;
    }

    public FacialPoint getLeftCheilionFP() {
//        return getFacialPoint(leftCheilion, FacialPointType.CH_L);
        return getFacialPoint(leftCheilion, FacialPointType.CH_R.ordinal());
    }

    public void setLeftCheilion(PdVector leftCheilion) {
        this.leftCheilion = leftCheilion;
    }

    public FacialPoint getStomionFP() {
        return getFacialPoint(stomion, FacialPointType.STO.ordinal());
    }

    public void setStomion(PdVector stomion) {
        this.stomion = stomion;
    }

    public FacialPoint getRightEktokantionFP() {
//        return getFacialPoint(rightEktokantion, FacialPointType.EX_R);
        return getFacialPoint(rightEktokantion, FacialPointType.EX_L.ordinal());
    }

    public void setRightEktokantion(PdVector rightEktokantion) {
        this.rightEktokantion = rightEktokantion;
    }

    public FacialPoint getLeftEktokantionFP() {
//        return getFacialPoint(leftEktokantion, FacialPointType.EX_L);
        return getFacialPoint(leftEktokantion, FacialPointType.EX_R.ordinal());
    }

    public void setLeftEktokantion(PdVector leftEktokantion) {
        this.leftEktokantion = leftEktokantion;
    }

    public FacialPoint getRightEntokantionFP() {
//        return getFacialPoint(rightEntokantion, FacialPointType.EN_R);
        return getFacialPoint(rightEntokantion, FacialPointType.EN_L.ordinal());
    }

    public void setRightEntokantion(PdVector rightEntokantion) {
        this.rightEntokantion = rightEntokantion;
    }

    public FacialPoint getLeftEntokantionFP() {
//        return getFacialPoint(leftEntokantion, FacialPointType.EN_L);
        return getFacialPoint(leftEntokantion, FacialPointType.EN_R.ordinal());
    }

    public void setLeftEntokantion(PdVector leftEntokantion) {
        this.leftEntokantion = leftEntokantion;
    }

    public PdVector getRightCheilion() {
        return rightCheilion;
    }

    public PdVector getLeftCheilion() {
        return leftCheilion;
    }

    public PdVector getStomion() {
        return stomion;
    }

    public PdVector getPronasale() {
        return pronasale;
    }

    public PdVector getRightEktokantion() {
        return rightEktokantion;
    }

    public PdVector getLeftEktokantion() {
        return leftEktokantion;
    }

    public PdVector getRightEntokantion() {
        return rightEntokantion;
    }

    public PdVector getLeftEntokantion() {
        return leftEntokantion;
    }
    
    
    public Set<Integer> dilation(int structElement, Set<Integer> area) {
        assert structElement >= 0;
        Set<Integer> currentArea = new HashSet<>(area);
        Set<Integer> newArea = new HashSet<>();

        for (int i = 0; i < structElement; i++) {
            for (Integer vert : currentArea) {
                Corner corner = getCornerVertex(vert);
                for (Corner neighbor : corner.vertexNeighbors()) {
                    newArea.add(neighbor.vertex);
                }
            }
            currentArea.addAll(newArea);
            //newArea.clear();
        }
        
        return currentArea;
    }
    
    public Set<Integer> erosion(int structElement, Set<Integer> area) {
        assert structElement >= 0;
        Set<Integer> currentArea = new HashSet<>(area);
        Set<Integer> newArea = new HashSet<>();

        for (int i = 0; i < structElement; i++) {
            for (Integer vert : currentArea) {
                if (containsAllNeighbours(vert, currentArea))
                    newArea.add(vert);
            }
            currentArea = new HashSet<>(newArea);
            newArea.clear();
        }
        return currentArea;
    }
    
    public Boolean containsAllNeighbours(int vert, Set<Integer> area){
        Corner corner = getCornerVertex(vert);
        for (Corner neighbor : corner.vertexNeighbors()) {
            if (!area.contains(neighbor.vertex))
                return false;
        }
        return true;
    }
    
    //erosion - dilation
    public Set<Integer> opening(int structElement1, int structElement2, Set<Integer> area) {
        return dilation(structElement2, erosion(structElement1, area));
    }
    
    //dilation - erosion
    public Set<Integer> closing(int structElement1, int structElement2, Set<Integer> area) {
        return erosion(structElement2, dilation(structElement1, area));
    }
   
    public Set<Integer> getKneighborhood(int k, int vert) {
        assert k <= 0;
        Set<Integer> area = new HashSet<>();
        Set<Integer> newArea = new HashSet<>();
        area.add(vert);
        for (int i = 0; i < k; i++) {
            for (Integer v : area) {
                Corner corner = getCornerVertex(v);
                for (Corner neighbor : corner.vertexNeighbors()) {
                    newArea.add(neighbor.vertex);
                }
            }
            area = new HashSet<>(newArea);
            //newArea.clear();
            
        }
        return area;
    }

    public Set<Integer> dilation2(int structElement, Set<Integer> area) {
        assert structElement >= 0;
        Set<Integer> currentArea = new HashSet<>(area);
        Set<Integer> newArea = new HashSet<>();
        for (Integer vert : currentArea) {
            newArea.addAll(getKneighborhood(structElement, vert));
        }
        return newArea;
    }
    
    public Corner getCornerVertex(int vertexInd) {
        for (Corner corner : cornerTable.corners()){
            if (corner.vertex == vertexInd)
                return corner;
        }
        return null;
    }
    
    public Set<Integer> thresholdVerticesToFaces(Set<Integer> thresholdVertices){
        Set<Integer> thresholdFaces = new HashSet<>();
        
            for (int i = 0; i < elementSet.getNumElements(); i++) {
            PiVector face = elementSet.getElement(i);
            Set<Integer> facePoints = new HashSet<>();
                for (int j = 0; j < face.getSize(); j++) {
                    facePoints.add(face.getEntry(j));
                }
            if (thresholdVertices.containsAll(facePoints))
                thresholdFaces.add(i);
            } //face.getEntry(1)
        
        return thresholdFaces;
    }    
    
    
    //PDM methods
    
    public Set<Set<Integer>> findAllNoseTipAreas(Set<Integer> thresholdFaces) {

        Set<Set<Integer>> allRegions = new HashSet<>();
        
        thresholdBigestRegion.clear();
        //tmpThresholdBigestRegion.clear();
        visitedVertices.clear();

        Set<SimpleEdge> thresholdEdges = getThresholdEdges(thresholdFaces);
        Set<Integer> thresholdVertices = getThresholdVertices(thresholdFaces);
        Corner actualCorner;

        for (Corner corner : cornerTable.corners()) {
            if (visitedVertices.add(corner.vertex) && visitedVertices.contains(corner.vertex)) {

                Set<Integer> tmpArea = new HashSet<>();
                
                actualCorner = corner;
                PdVector tmpPronasale = elementSet.getVertex(actualCorner.vertex);
                tmpArea.add(actualCorner.vertex);

                boolean continueFlag = true;
                HashSet<Corner> nextCorners = new HashSet<Corner>();

                while (continueFlag) {
                    for (Corner corner2 : actualCorner.vertexNeighbors()) {
                        if (thresholdVertices.contains(corner2.vertex) && tmpArea.add(corner2.vertex)
                                && thresholdEdges.contains(new SimpleEdge(actualCorner.vertex, corner2.vertex))
                                && visitedVertices.add(corner2.vertex)) {

                            if (elementSet.getVertex(corner2.vertex).getEntry(2) > tmpPronasale.getEntry(2)) {
                                tmpPronasale = elementSet.getVertex(corner2.vertex);
                            }

                            nextCorners.add(corner2);

                        }
                    }

                    if (!nextCorners.isEmpty()) {
                        actualCorner = nextCorners.iterator().next();
                        nextCorners.remove(actualCorner);
                    } else {
                        continueFlag = false;
                    }

                }
                
                if(tmpArea.size() > 10)
                    allRegions.add(tmpArea);

                //tmpThresholdBigestRegion.clear();
            }
        }
        return allRegions;
    }
}
