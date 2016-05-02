/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utils;

import Jama.Matrix;
import com.jogamp.graph.math.Quaternion;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class MathUtils {
    private static MathUtils instance;
    
    public static MathUtils instance(){
        if(instance == null){
            instance = new MathUtils();
        }
        
        return instance;
    }

    private MathUtils() {
    }
    
     /**
     * Computes distance between point1 and point2 in space.
     *
     * @param point1
     * @param point2
     * @return distance between point1 and point2
     */
    public double distancePoints(Vector3f point1, Vector3f point2) {
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2)
                + Math.pow(point1.getY() - point2.getY(), 2)
                + Math.pow(point1.getZ() - point2.getZ(), 2));
    }

    /**
     * Computes distance between 2 coordinates. Can be negative.
     * 
     * @param p1 first coordinate
     * @param p2 second coordinate
     * @return distance between coordinates
     */
    public float distanceCoordinates(float p1, float p2) {
        return p2 - p1;
    }
    
    /**
     * Multiplies two quaternions and return new quaternion with the result of multiplication.
     * 
     * @param q1 first quaternion
     * @param q2 second quaternion
     * @return result of multiplication as new quaternion
     */
    public Quaternion multiply(Quaternion q1, Quaternion q2) {

        float x, y, z, w;

        w = q1.getW() * q2.getW() - q1.getX() * q2.getX() - q1.getY() * q2.getY() - q1.getZ() * q2.getZ();
        x = q1.getX() * q2.getW() + q1.getW() * q2.getX() + q1.getY() * q2.getZ() - q1.getZ() * q2.getY();
        y = q1.getY() * q2.getW() + q1.getW() * q2.getY() + q1.getZ() * q2.getX() - q1.getX() * q2.getZ();
        z = q1.getZ() * q2.getW() + q1.getW() * q2.getZ() + q1.getX() * q2.getY() - q1.getY() * q2.getX();
        return new Quaternion(x, y, z, w);
    }
    
    /**
     * Turns given quaternion into matrix and returns it.
     * 
     * @param q quaternion to be turned into matrix
     * @return matrix representing quaternion
     */
    public Matrix quaternionToMatrix(Quaternion q){
        Matrix m = new Matrix(4,4);
        float[] matrix = q.toMatrix();
        
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                m.set(i,j, matrix[i * 4 + j]);
            }
        }
        
        return m;
    }
    
    /**
     * Creates quaternion from matrix
     * 
     * @param m - 3x3 rotational matrix
     * @return quaternion created from given matrix
     */
    public Quaternion matrixToQuaternion(Matrix m){
        double[][] mat = m.getArray();
        float[] finalMat = new float[9];
        Quaternion q = new Quaternion();
        
        for(int i = 0; i < mat.length; i++){
            for(int j = 0; j < mat[i].length; j++){
                finalMat[i * 3 + j] = (float) mat[i][j];
            }
        }
        
        q.setFromMatrix(finalMat);
        
        return q;
    }
    
    /**
     * Returns matrix representation of given point. This will be 1x4 matrix.
     * 
     * @param p Vector to be turned into matrix
     * @return matrix representing the given point
     */
    public Matrix pointToMatrix(Vector3f p){
        Matrix m = new Matrix(1,4);
        
        m.set(0,0, p.x);
        m.set(0,1, p.y);
        m.set(0,2, p.z);
        m.set(0,3,1);
        
        return m;
    }
    
    public Matrix scaleMatrix(float scale){
        Matrix s = new Matrix(new double[] {scale, 0, 0 , 0,
                                            0, scale, 0, 0,
                                            0, 0, scale, 0,
                                            0, 0, 0, 1}, 4);
        
        return s;
    }
    
    public Matrix transMatrix(Vector3f t){
        Matrix m = new Matrix(new double[] {1, 0, 0 , t.x,
                                            0, 1, 0, t.y,
                                            0, 0, 1, t.z,
                                            0, 0, 0, 1}, 4);
        
        return m;
    }
    
    /**
     * Creates vector from two points. Does not normalize results
     * @param a - first point
     * @param b - second point
     * @return new vector created from two points
     */
    public Vector3f createVector(Vector3f a, Vector3f b){
        return new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);
    }
    
    /**
     * Divides vector by number. Returns new vector, does not modify original parameters.
     * @param a - vector to be divide
     * @param num - value to divide vector by
     * @return new vector, result of division
     */
    public Vector3f divideVectorByNumber(Vector3f a, float num){
        return new Vector3f(a.x / num, a.y / num, a.z / num);
    }
    
    public Vector3f multiplyVectorByNumber(Vector3f a, float num){
        return new Vector3f(a.x * num, a.y * num, a.z * num);
    }
    
    /**
     * Adds one vector to another, does not return new vector, but rather adds to vector in first parameter.
     * @param v - vector to be added to
     * @param toAdd - value of vector to add
     */
    public void addVector(Vector3f v, Vector3f toAdd){
        v.x += toAdd.x;
        v.y += toAdd.y;
        v.z += toAdd.z;
    }
    
    public Vector3f crossProduct(Vector3f p1, Vector3f p2){
        float x = p1.y * p2.z - p1.z * p2.y;
        float y = p1.z * p2.x - p1.x * p2.z;
        float z = p1.x * p2.y - p1.y * p2.x;
        
        return new Vector3f(x,y,z);
    }
    
    /**
     * Computes normal of triangle defined by three vertices.
     * 
     * @param p0 - point of triangle
     * @param p1 - point of triangle
     * @param p2 - point of triangle
     * @return  normal of triangle defined by three vertices
     */
    public Vector3f getNormalOfTriangle(Vector3f p0, Vector3f p1, Vector3f p2){
        Vector3f a = MathUtils.instance().createVector(p0, p1);
        Vector3f b = MathUtils.instance.createVector(p0, p2);
        
        return crossProduct(a, b);
    }
    
    public Matrix resultTableToMatrix(String results, int numOfModels){
        String[] lines = results.split("\n");
        Matrix mat = new Matrix(numOfModels, numOfModels);
        
        for(int i = 1; i < lines.length; i++){
            String[] lenRes = lines[i].split(";");
            
            for(int j = 1; j < lenRes.length; j++){
                mat.set(i - 1, j - 1, Double.parseDouble(lenRes[j]));
            }
        }
        
        return mat;
    }
    
    /**
     * Creates symmetric result matrix, by adding results for i-th and j-th model together (i.e. res[i,j] + res[j,i])
     * and dividing them by 2.
     * 
     * @param results - asymmetric result table
     * @return symmetric result table
     */
    public List<ArrayList<Float>> symetricMatrix(List<ArrayList<Float>> results){
        List<ArrayList<Float>> symRes = new ArrayList<>();
        
        for(int i = 0; i < results.size(); i++){
            ArrayList<Float> symLine = new ArrayList<Float>();
            
            for(int j = 0; j < results.size(); j++){
                float res = results.get(i).get(j) + results.get(j).get(i);
                res /= 2.0f;
                
                symLine.add(res);
            }
            
            symRes.add(symLine);
        }
        
        return symRes;        
    }
    
    /**
     * Creates symmetric result matrix, by adding results for i-th and j-th model together (i.e. res[i,j] + res[j,i])
     * and dividing them by 2.
     * 
     * @param tableResults - table format of results
     * @return symmetric result table
     */
    public List<ArrayList<Float>> symetricMatrix(String tableResults){
        List<ArrayList<Float>> numRes = parseTableResults(tableResults);        
        
        return symetricMatrix(numRes);        
    }
    
    //parse results from result table and returns values in it
    private List<ArrayList<Float>> parseTableResults(String tableResults){
        String[] lines = tableResults.split("\n");
        List<ArrayList<Float>> parsed = new ArrayList<>();
        
        for(int i = 1; i < lines.length; i++){
            ArrayList<Float> res = new ArrayList<>();
            String[] l = lines[i].split(";");
            
            for(int j  = 1; j < l.length; j++){
                res.add(Float.parseFloat(l[j]));
            }
            
            parsed.add(res);            
        }
        
        return parsed;
    }
    
    /**
     * Clamp val between values min and max.
     * 
     * @param val - value to clamp
     * @param min - min possible value
     * @param max - max possible value
     * @return val clamped between min and max
     */
    public float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
    
    /**
     * Computes distance of point p to triangle. If projection of point p is within triangle
     * this distance is equal to distance between projection and p. Otherwise algorithm projects
     * projection to triangle edges and finds closest projection. Distance is then equal
     * to distance between p and closest projection onto the edge.
     * 
     * @param p - original point to compute distance to triangle from
     * @param projection - projection of point p into plane defined by triangle
     * @param triangle - three vertices of triangle
     * @return distance from point p to triangle 
     */
    public float distanceToTriangle(Vector3f p, Vector3f projection, Vector3f[] triangle){
        if(IntersectionUtils.pointInTriangle(triangle, projection)){
            return (float) MathUtils.instance().distancePoints(p, projection);
        }
        
        return MathUtils.instance().distanceToTriangleEdges(p, projection, 
                triangle[0], triangle[1], triangle[2]);        
    }
    
    /**
     * Computes distance to single edge ab. Either as distance to point projected onto edge, or distance
     * to one of the ends of edge, if this distance is closer than distance to projection
     * 
     * @param p - point we compute distance to edge of
     * @param a - first end point of the edge
     * @param b - second end point of the edge
     * @return distance of the point p to the edge as described
     */
    private float distanceToEdge(Vector3f originalP, Vector3f p, Vector3f a, Vector3f b){        
        Vector3f ab = createVector(a, b);
        Vector3f ap = createVector(a, p);
        
        float t = ab.dot(ap) / ab.lengthSquared();
        
        if(t >= 0 && t <= 1){       //projection lies on edge AB
            Vector3f projection = new Vector3f(a.x + t * ab.x, a.y + t * ab.y, a.z + t * ab.z);
            return (float) distancePoints(originalP, projection);            
        }else if(t < 0){    //projection lies outside of edge AB and is closer to A
            return (float) distancePoints(originalP, a);
        }else{      //projection lies outside of edge AB and is closer to B
            return (float) distancePoints(originalP, b);
        }
    }
        
    /**
     * Computes distance to all edges of the triangle. Then returns the smallest distance as result.
     * 
     * @param originalP - original point to compute distance to the edges from
     * @param p - projection of originalP to plane defined by triangle ABC
     * @param a - point of triangle
     * @param b - point of triangle
     * @param c - point of triangle
     * @return shortest distance from originalP to one of edges of triangle ABC.
     */
    public float distanceToTriangleEdges(Vector3f originalP, Vector3f p, Vector3f a, Vector3f b, Vector3f c){
        float[] dist = new float[3];
        
        dist[0] = distanceToEdge(originalP, p, a, b);
        dist[1] = distanceToEdge(originalP, p, b, c);
        dist[2] = distanceToEdge(originalP, p, c, a);
        
        return minOfArray(dist);
    }
    
    //finds minimal value in array
    private float minOfArray(float[] values){
        if(values == null || values.length == 0){
            return Float.MAX_VALUE;
        }
        
        float minValue = values[0];
        
        for(int i = 1; i < values.length; i++){
            if(values[i] < minValue){
                minValue = values[i];
            }
        }
        
        return minValue;
    }
}
