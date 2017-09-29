/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.localAreas;

import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;


/**
 * Library for advance functionality
 * 
 * @author Richard
 */
public class LocalAreaLibrary {
    /**
     * Intersection between screen and model
     * @param mouseReleasedX mouse position on screen
     * @param mouseReleasedY mouse position on screen
     * @param viewport viewport matrix
     * @param modelViewMatrix model matrix
     * @param projectionMatrix projection matrix
     * @param areaPoints points of a model
     * @return 
     */
    public static Vector4f intersectionWithPoint(double mouseReleasedX, 
                                               double mouseReleasedY, 
                                               int[] viewport, 
                                               double[] modelViewMatrix, 
                                               double[] projectionMatrix,
                                               List<Vector4f> areaPoints) {

        double v0[] = new double[4];
        double v1[] = new double[4];
        int realY = viewport[3] - (int) mouseReleasedY - 1;

        GLU glu = new GLU();

        //first point of intersection, close to the camera postion - v0 is an output
        glu.gluUnProject(mouseReleasedX, realY, 0.0, 
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                v0, 0);

        //second point of intersection, close to the back of the projection - v1 is an output
        glu.gluUnProject(mouseReleasedX, realY, 1.0, 
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                v1, 0);

        double treshold = 100000.0;
        Vector4f point = null;
        
        for (int i = 0; i < areaPoints.size(); i++){

            Vector4f a = areaPoints.get(i);

            //vector of these two points
            Vector3f pp = new Vector3f((float) v1[0] - (float) v0[0], //P1-P0
                (float) v1[1] - (float) v0[1],
                (float) v1[2] - (float) v0[2]);
            
            //finding the closest point
            double distance = calculateDistanceBetweenPoints(pp, v0, v1, a);
            if (treshold > distance){
                point = a;
                treshold = distance;
            }
        }

        if (point == null){
            return null;
        }
        
        return new Vector4f((float)point.x, (float)point.y, (float)point.z, (float)point.w);
    }
    
    /**
     * Calculate distance between two points
     * 
     * @param pp intersection vector
     * @param P0 first point from gluUnProject
     * @param P1 second point from gluUnProject
     * @param point calculating distance to this point
     * @return 
     */
    private static double calculateDistanceBetweenPoints(Vector3f pp, double[] P0, double[] P1, Vector4f point) {

        Vector3f result = new Vector3f();
        result.cross(pp, new Vector3f((float) point.getX() - (float) P0[0], (float) point.getY() - (float) P0[1], (float) point.getZ() - (float) P0[2]));
        
        return result.length();   
    }
    
    /**
     * Intersection with Area
     * @param mouseReleasedX X mouse position on screen
     * @param mouseReleasedY Y mouse position on screen
     * @param viewport  matrix
     * @param modelViewMatrix matrix
     * @param projectionMatrix matrix
     * @param areaBoundary boundary of a area - closed deformed circle 
     * @return 
     */
    public static Vector3f intersectionWithArea(double mouseReleasedX, 
                                               double mouseReleasedY, 
                                               int[] viewport, 
                                               double[] modelViewMatrix, 
                                               double[] projectionMatrix,
                                               List<Point3D> areaBoundary) {
        
        if (areaBoundary.size()<3){
            return null;
        }
    
        double v0[] = new double[4];
        double v1[] = new double[4];
        int realY = viewport[3] - (int) mouseReleasedY - 1;

        GLU glu = new GLU();

        //v0 output - closer to the camera position
        glu.gluUnProject(mouseReleasedX, realY, 0.0, 
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                v0, 0);

        //v1 output - closer to the back of projection
        glu.gluUnProject(mouseReleasedX, realY, 1.0, 
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                v1, 0);

        Point3D a = areaBoundary.get(0);

        for (int i = 1; i < areaBoundary.size()-1; i++){
            
            Point3D b = areaBoundary.get(i);
            Point3D c = areaBoundary.get(i+1);

            Point3D[] triangle = {a, b, c};
            
            //calculate if the given ray is inside this triangle
            Vector3f point = calculateIntersectionWithTriangle(v0, v1, triangle);
            if (point != null){
                return point;
            }
        }

        return null;
    }
    

    /**
     * Calculate intersection with triangle
     * @param P0 beginning of a ray
     * @param P1 ending of a ray
     * @param triangle array of three points
     * @return 
     */
    private static Vector3f calculateIntersectionWithTriangle(double[] P0, double[] P1, Point3D[] triangle) {
        if (triangle.length == 3) {
            //ray - points  P0, P1
            //triangle - points T0,T1,T2
            //vector u
            Vector3f u = new Vector3f((float)(triangle[1].getX() - triangle[0].getX()), //T1-T0
                                      (float)(triangle[1].getY() - triangle[0].getY()),
                                      (float)(triangle[1].getZ() - triangle[0].getZ()));
            //vector v
            Vector3f v = new Vector3f((float)(triangle[2].getX() - triangle[0].getX()),//T2-T0
                                      (float)(triangle[2].getY() - triangle[0].getY()),
                                      (float)(triangle[2].getZ() - triangle[0].getZ()));
            //vertical to vectors u and v
            Vector3f n = new Vector3f();
            n.cross(u, v);
            
            //ray vector
            Vector3f pp = new Vector3f((float) P1[0] - (float) P0[0], //P1-P0
                    (float) P1[1] - (float) P0[1],
                    (float) P1[2] - (float) P0[2]);

            n.normalize();
            pp.normalize();

            Vector3f p0 = new Vector3f((float) P0[0], (float) P0[1], (float) P0[2]);

            //check if they are not in right angle
            float i;
            if (n.dot(pp) != 0) {
                 float d = (-1) * (n.dot(new Vector3f ((float )triangle[0].getX(),
                                                       (float )triangle[0].getY(),
                                                       (float )triangle[0].getZ())));
                i = -(n.dot(p0) + d) / n.dot(pp);
            } else {
                return null;
            }
            
            if (i >= 0) {
                // intersection with plane
                Vector3f intP = new Vector3f( p0.x + i * pp.x, 
                        p0.y + i * pp.y,
                        p0.z + i * pp.z);

                //PI-T0
                Vector3f w = new Vector3f(intP.getX() - (float )triangle[0].getX(), 
                    intP.getY() - (float )triangle[0].getY(),
                    intP.getZ() - (float )triangle[0].getZ());

                float uv = u.dot(v);
                float uu = u.dot(u);
                float vv = v.dot(v);
                float wu = w.dot(u);
                float wv =  w.dot(v);
                float s = (uv * wv - vv * wu) / (uv * uv - uu * vv);
                float t = (uv * wu - uu * wv) / (uv * uv - uu * vv);

                if (s >= 0 && t >= 0 && s + t <= 1) {
                      return intP;
                }
            }
        }
        return null;
    }
}
