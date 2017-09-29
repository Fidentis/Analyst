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
    public static List<Point3D> giftWrapping(List<Point3D> points){
        
        //getting pivot
        List<Point3D> vertexList = new ArrayList<>();
        
        int pivot = smallestX(points);
        int currentIndex = pivot;
        int previousIndex = pivot;
        
        Point3D a = points.get(currentIndex);
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
    
    private static int smallestX(List<Point3D> points){
        int index = -1;
        int pomIndex = -1;
        Double smallestX = 5000.0;
        
        for (Point3D point : points){
            pomIndex++;
            if (point.getX()<smallestX){
                smallestX = point.getX();
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
    private static int getMinimalAnglePoint(List<Point3D> points, int currentIndex, int previousIndex, List<Integer> usedIndexes){
        Point3D currentPoint = points.get(currentIndex) ; 
        Point3D previousPoint = points.get(previousIndex);
        
        //setting inicial line
        if (currentIndex == previousIndex){
            previousPoint = new Point3D(previousPoint.getX(), previousPoint.getY()+100, previousPoint.getZ());
        }
        
        double[] line1 =   {previousPoint.getX(), 
                            previousPoint.getY(),
                            previousPoint.getZ(),
                            currentPoint.getX(),
                            currentPoint.getY(),
                            currentPoint.getZ()};

        //inicializing loop
        int index = -1;
        int tempIndex = -1;
        Double minimalAngle = 5000.0;
        
        //looking for point that have minimal angle to the two previous points
        for (Point3D point : points){
            tempIndex++;
            
            if (tempIndex != currentIndex && tempIndex != previousIndex && !usedIndexes.contains(tempIndex)){
                
                double[] line2 =   {currentPoint.getX(),
                                    currentPoint.getY(),
                                    currentPoint.getZ(),
                                    point.getX(),
                                    point.getY(),
                                    point.getZ()};

                Double  pomMinimalAngle = angleBetween2Lines(line1, line2);

                if (pomMinimalAngle<minimalAngle){
                    minimalAngle = pomMinimalAngle;
                    index = tempIndex;
                }
            }
        }
        return index;
    }
    
    private static double angleBetween2Lines(double[] line1, double[] line2){
        
        Point3D a = new Point3D(line1[0] - line1[3], 
                                line1[1] - line1[4],
                                line1[2] - line1[5]);
        Double aNorm =  Math.sqrt(a.getX()*a.getX()+a.getY()*a.getY()+a.getZ()*a.getZ());
        a = new Point3D(a.getX()/aNorm, a.getY()/aNorm, a.getZ()/aNorm);
        
        Point3D b = new Point3D(line2[0] - line2[3], 
                                line2[1] - line2[4],
                                line2[2] - line2[5]);
        Double bNorm =  Math.sqrt(b.getX()*b.getX()+b.getY()*b.getY()+b.getZ()*b.getZ());
        b = new Point3D(b.getX()/bNorm, b.getY()/bNorm, b.getZ()/bNorm);
        
        Double dot = (a.getX()*b.getX()) + (a.getY()*b.getY()) + (a.getZ()*b.getZ());
        return Math.abs(Math.toDegrees(Math.acos(dot)));
    }
}
