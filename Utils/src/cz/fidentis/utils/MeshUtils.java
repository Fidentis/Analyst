/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utils;


import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class MeshUtils {
    private static MeshUtils instance;
    
    private MeshUtils(){};
    
    public static MeshUtils instance(){
        if(instance == null){
            instance = new MeshUtils();
        }
        
        return instance;
    }
    
    
    /**
     * Computes center of the mesh and returns it.
     *
     * @param mesh - point cloud representing mesh
     * @return array of float containing x, y and z coordinate of the center of mesh
     */
    public float[] computeCentroid(List<Vector3f> mesh) {
        float x = 0;
        float y = 0;
        float z = 0;
        for (Vector3f p : mesh) {
            x = x + p.getX();
            y = y + p.getY();
            z = z + p.getZ();
        }
        x = x / mesh.size();
        y = y / mesh.size();
        z = z / mesh.size();
        return new float[]{x, y, z};
    }
    
    /**
     * Creates mirrored model of given model
     * 
     * @param model - model to mirror
     * @return mirrored model
     */
    public Model getMirroredModel(Model model){
        Model mirroredModel = model.copy();
        float[] centroid = computeCentroid(mirroredModel.getVerts());
        
        for (Vector3f vert : mirroredModel.getVerts()) {
            vert.setX(centroid[0] - vert.x);
        }

        return mirroredModel;
    }
    
    
    /**
     * Slight modification of 2D algorithm
     * @param verticesIndexes indexes
     * @param model model
     * @return 
     */
    public static List<Vector3f> giftWrapping(List<Vector3f> points){
        
        //getting pivot
        List<Vector3f> vertexList = new ArrayList<>();
        
        int pivot = smallestX(points);
        int currentIndex = pivot;
        int previousIndex = pivot;
        
        Vector3f a = points.get(currentIndex);
        vertexList.add(a);

        List<Integer> indexes = new ArrayList<>();
        
        //going around all points to make a wrapping
        do {
            int nextIndex = getMinimalAnglePoint(points, currentIndex, previousIndex, indexes);
            indexes.add(nextIndex);
            if (currentIndex != previousIndex){
                a = points.get(currentIndex);
                vertexList.add(a);
                
            }
            previousIndex = currentIndex;
            currentIndex = nextIndex;
            
        } while (pivot != currentIndex);
        
        
        return vertexList;
    }
    
    private static int smallestX(List<Vector3f> points){
        int index = -1;
        int pomIndex = -1;
        float smallestX = 5000.0f;
        
        for (Vector3f point : points){
            pomIndex++;
            if (point.getX()<smallestX){
                smallestX = point.x;
                index = pomIndex;
            }
        }
        return index;
    }
    
    /**
     * Finding the next point that have minimal angle to the two previous points
     * @param points
     * @param currentIndex
     * @param previousIndex
     * @param usedIndexes modification - prevents from cycling (3D is not 2D :D)
     * @return 
     */
    private static int getMinimalAnglePoint(List<Vector3f> points, int currentIndex, int previousIndex, List<Integer> usedIndexes){
        Vector3f currentPoint = points.get(currentIndex) ; 
        Vector3f previousPoint = points.get(previousIndex);
        
        //setting inicial line
        if (currentIndex == previousIndex){
            previousPoint = new Vector3f(previousPoint.getX(), previousPoint.getY()+100, previousPoint.getZ());
        }
        
        float[] line1 =   {previousPoint.getX(), 
                            previousPoint.getY(),
                            previousPoint.getZ(),
                            currentPoint.getX(),
                            currentPoint.getY(),
                            currentPoint.getZ()};

        //inicializing loop
        int index = -1;
        int tempIndex = -1;
        float minimalAngle = 5000.0f;
        
        //looking for point that have minimal angle to the two previous points
        for (Vector3f point : points){
            tempIndex++;
            
            if (tempIndex != currentIndex && tempIndex != previousIndex && !usedIndexes.contains(tempIndex)){
                
                float[] line2 =   {currentPoint.getX(),
                                    currentPoint.getY(),
                                    currentPoint.getZ(),
                                    point.getX(),
                                    point.getY(),
                                    point.getZ()};

                float pomMinimalAngle = angleBetween2Lines(line1, line2);

                if (pomMinimalAngle<minimalAngle){
                    minimalAngle = pomMinimalAngle;
                    index = tempIndex;
                }
            }
        }
        return index;
    }
    
    private static float angleBetween2Lines(float[] line1, float[] line2){
        
        Vector3f a = new Vector3f(line1[0] - line1[3], 
                                line1[1] - line1[4],
                                line1[2] - line1[5]);
        a.normalize();
        
        Vector3f b = new Vector3f(line2[0] - line2[3], 
                                line2[1] - line2[4],
                                line2[2] - line2[5]);
        b.normalize();

        return (float)Math.abs(Math.toDegrees(Math.acos(a.dot(b))));
    }
}
