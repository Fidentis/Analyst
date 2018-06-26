/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.featurepoints.curvature.CurvatureType;
import cz.fidentis.model.Dimensions;
import cz.fidentis.model.corner_table.CornerTable;
import cz.fidentis.featurepoints.pdm.CurvatureMaps;
import cz.fidentis.utils.MathUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.vecmath.Vector3f;
import jv.geom.PgElementSet;

/**
 *
 * @author Rasto1
 */
public class AreasSearch {
    
    /**
     * Method which finds eye corners of model
     * @param set model set
     * @param fpUniverse model feature point universe
     * @param prn nose tip
     * @param dim model dimension
     * @return eye corners of model in list on 0 and 1 position
     */
    public List<FacialPoint> eyeCornerSerach(PgElementSet set, FeaturePointsUniverse fpUniverse, Vector3f prn, Dimensions dim) {
  
        List<FacialPoint> fps = new ArrayList<>();
        
        List<Vector3f> simplifiedModel = new ArrayList<>();
        
        //Get positions of vertices after simplification
        for(int i = 0; i < set.getVertices().length; i++){
            if(i == fpUniverse.getVerts().size()-1){
                break;
            }
            
            Vector3f p = new Vector3f((float) set.getVertex(i).getEntry(0), (float) set.getVertex(i).getEntry(1), (float) set.getVertex(i).getEntry(2));
            simplifiedModel.add(p);
        }
        
        //Min/max curvatre
        double[] minCurv = fpUniverse.calculateCurvature(CurvatureType.Minimum, true);
        double[] maxCurv = fpUniverse.calculateCurvature(CurvatureType.Maximum, true);  

        //Center of face, for centered mesh (0,0,0)
        Vector3f centerFace = new Vector3f();
 
        //Values measured from dataset, need to test this on different database, see if it works
        float meanSI = 0.61f;
        float meanSIstd = 0.18f;
        float meanSIvar = 0.03f * 2.5f;
        
        float meanPrnEyedist = 52.15f;
        float meanPrnEyedistStd = 3.84f * 2;
        float meanPrnEyedistVar = 14.76f;
        
        float eyeDist = 33.09f;
        float eyeDistStd = 3.21f;
        
        
        Set<Integer> possiblePoints = new HashSet<>();

        CurvatureMaps curvMaps = new CurvatureMaps();
        
        //Filter out points using distance between PRN and eye corners and Shape Index for eyes
        for (int i = 0; i < simplifiedModel.size(); i++) {
            float distToNose = (float) MathUtils.instance().distancePoints(prn, simplifiedModel.get(i));
            if (distToNose < meanPrnEyedist + meanPrnEyedistVar && distToNose > meanPrnEyedist - meanPrnEyedistVar) {
                float shapeIndex = curvMaps.shapeIndex((float) minCurv[i], (float) maxCurv[i]);
                float curvadness = curvMaps.curvedness((float) minCurv[i], (float) maxCurv[i]);


                if(shapeIndex > meanSI - meanSIvar && curvadness >= 0.03)
                    possiblePoints.add(i);
            }
        }
        
        //Continual areas
        Set<Set<Integer>> areas = findAreas(possiblePoints, fpUniverse.getCornerTable(), false, 20);
        Set<Integer> theArea = null;
        Vector3f centerArea = null;
        float minDist = Float.MAX_VALUE;
        
        //Find area, that has center closest to the center of the face
        for (Set<Integer> a : areas) {
                List<Vector3f> verts = new ArrayList<>();

                for (Integer i : a) {
                    verts.add(simplifiedModel.get(i));
                }

                Vector3f center = findCenterOfArea(verts);
                float dist = (float) MathUtils.instance().distancePoints(center, centerFace);
                
                if(dist < minDist){
                    theArea = a;
                    minDist = dist;
                    centerArea = center;
                }
            }
        
        //TO DO REFACTOR
        
        //This is same as for the nose, refactor, use same method
        //Sphere fitting -- kinda
        //Find which point in area contains the most other points
        //In area, if we place sphere to the center of it with sphereRadius
        int eye = 0;
        int numOfPoints = 0;
        float sphereRadius = 15.0f;

        for (Integer k : theArea) {
            int pointsInRadius = 0;

            for (Integer j : theArea) {
                if (k == j) {
                    continue;
                }

                if (MathUtils.instance().distancePoints(simplifiedModel.get(k), simplifiedModel.get(j)) <= sphereRadius) {
                    pointsInRadius++;
                }
            }

            if (numOfPoints < pointsInRadius) {
                eye = k;
                numOfPoints = pointsInRadius;
            }
        }


        Vector3f eyeCornerProbably = simplifiedModel.get(eye);
        fps.add(new FacialPoint(eye, eyeCornerProbably));

        //remove the area that you found, and do the rest one the rest of areas
        areas.remove(theArea);
        minDist = Float.MAX_VALUE;
        
        //Find area which center is closest to the measured distance between two eye corners
        for (Set<Integer> a : areas) {
                List<Vector3f> verts = new ArrayList<>();

                for (Integer i : a) {
                    verts.add(simplifiedModel.get(i));
                }

                Vector3f center = findCenterOfArea(verts);
                float dist = (float) Math.abs(eyeDist - MathUtils.instance().distancePoints(center, centerArea));
                
                if(dist < minDist){
                    theArea = a;
                    minDist = dist;
                }
            }
        
        eye = 0;        
        numOfPoints = 0;

        //Sphere fitting again
        for (Integer k : theArea) {
            int pointsInRadius = 0;

            for (Integer j : theArea) {
                if (k == j) {
                    continue;
                }

                if (MathUtils.instance().distancePoints(simplifiedModel.get(k), simplifiedModel.get(j)) <= sphereRadius) {
                    pointsInRadius++;
                }
            }

            if (numOfPoints < pointsInRadius) {
                eye = k;
                numOfPoints = pointsInRadius;
            }
        }


        eyeCornerProbably = simplifiedModel.get(eye);
        fps.add(new FacialPoint(eye, eyeCornerProbably));

        
        return fps;
    }
    
    /**
     * Method which finds nose tip of model
     * @param set model set
     * @param fpUniverse model feature point universe
     * @return  list which contains on 0 position nose tip
     */
    public List<FacialPoint> finalNoseSearch(PgElementSet set, FeaturePointsUniverse fpUniverse) {

        List<FacialPoint> fps = new ArrayList<>();

        //Get vertices of simplifies model
        List<Vector3f> simpModelVertices = new ArrayList<>();
        
        for (int j = 0; j < set.getVertices().length; j++) {
            if(j == fpUniverse.getVerts().size()-1){
                break;
            }
            
            Vector3f v = new Vector3f((float) set.getVertex(j).getEntry(0), (float) set.getVertex(j).getEntry(1), (float) set.getVertex(j).getEntry(2));
            simpModelVertices.add(v);
        }

        Set<Set<Integer>> allRegions = fpUniverse.findAllNoses();

        //Get min and max curvature for shape and curvedness computation
        double[] minCurv = fpUniverse.calculateCurvature(CurvatureType.Minimum, true);
        double[] maxCurv = fpUniverse.calculateCurvature(CurvatureType.Maximum, true);
        Set<Integer> filteredRegions = new HashSet<>();

        CurvatureMaps curvMaps = new CurvatureMaps();
        
        //Filter detected regions using shape index and curvedness values
        for (Set<Integer> i : allRegions) {

            for (Integer j : i) {

                float shapeIndex = curvMaps.shapeIndex((float) minCurv[j], (float) maxCurv[j]);
                float curvadness = curvMaps.curvedness((float) minCurv[j], (float) maxCurv[j]);

                //First 40 faces show all shape indieces for nose > 0.7 and curvedness > 0.049
                if (shapeIndex >= 0.7f && curvadness > 0.05f) {
                    filteredRegions.add(j);
                }
            }
        }

        //Find continual areas in filtered regions
        Set<Set<Integer>> areas = findAreas(filteredRegions, fpUniverse.getCornerTable(), false, 10);
        Set<Integer> area = null;

        //Pick the largest continual filtered area
        int largestRegionSize = 0;
        for (Set<Integer> r : areas) {
            if (r.size() > largestRegionSize) {
                area = r;
                largestRegionSize = r.size();
            }
        }

        //Sphere fitting -- kinda
        //Find which point in area contains the most other points
        //In area, if we place sphere to the center of it with sphereRadius
        int nose = 0;
        int numOfPoints = 0;
        float sphereRadius = 10.0f;

        for (Integer k : area) {
            int pointsInRadius = 0;

            for (Integer j : area) {
                if (k == j) {
                    continue;
                }

                if (MathUtils.instance().distancePoints(simpModelVertices.get(k), simpModelVertices.get(j)) <= sphereRadius) {
                    pointsInRadius++;
                }
            }

            if (numOfPoints < pointsInRadius) {
                nose = k;
                numOfPoints = pointsInRadius;
            }
        }

        Vector3f noseTipProbably = simpModelVertices.get(nose);
        fps.clear();
        fps.add(new FacialPoint(FacialPointType.PRN.ordinal(), noseTipProbably));

        return fps;
    }
    
    /////// Help methods for search
    
    //Find continual region from given indices
    private Set<Set<Integer>> findAreas(Set<Integer> vertices, CornerTable ct, boolean discardBoundries, int minAreaSize) {

        Set<Set<Integer>> areas = new HashSet<>();
        Set<Integer> usedIndices = new HashSet<>();
        List<Integer> vv = new ArrayList<Integer>();
        vv.addAll(vertices);

        for (Integer i : vertices) {
            if (discardBoundries && ct.getVertexCorner(i, ct).isBoundary()) {
                continue;
            }

            if (usedIndices.contains(i)) {
                continue;
            }

            Set<Integer> currentArea = new HashSet<>();

            currentArea = addToArea(ct, i, currentArea, usedIndices, vertices);

            if (currentArea.size() < minAreaSize) {
                continue;
            }

            areas.add(currentArea);
        }

        return areas;
    }

    private Set<Integer> addToArea(CornerTable ct, int neighbor, Set<Integer> area, Set<Integer> usedIndices, Set<Integer> vertices) {
        int added = 0;

        if (vertices.contains(neighbor) && !usedIndices.contains(neighbor)) {
            usedIndices.add(neighbor);
            area.add(neighbor);
            added++;
        }

        if (added != 0) {
            int[] neighbors = ct.getIndexNeighbors(neighbor);

            for (int i : neighbors) {
                addToArea(ct, i, area, usedIndices, vertices);
            }
        }

        return area;
    }
    
    private Vector3f findCenterOfArea(List<Vector3f> verts){
        Vector3f center = new Vector3f(0,0,0);
        
        for(int i = 0; i < verts.size(); i++){
            center.x += verts.get(i).x;
            center.y += verts.get(i).y;
            center.z += verts.get(i).z;
        }
        
        return new Vector3f(center.x/verts.size(),center.y/verts.size(),center.z/verts.size());
    }
    
}