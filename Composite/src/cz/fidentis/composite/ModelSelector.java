/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.composite;

import cz.fidentis.model.Model;
//import cz.fidentis.model.Vector3f;
import java.util.ArrayList;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class ModelSelector {

    private GLU glu;
//    private ArrayList<Model> models = new ArrayList<Model>();
    //   private int[] viewport = new int[4];
    //   private double[] modelViewMatrix = new double[16];
    //   private double[] projectionMatrix = new double[16];
    private double P0[] = new double[4];
    private double P1[] = new double[4];
    private Vector3f intersection;

    /**
     *
     * @param glu
     */
    public ModelSelector(GLU glu) {
        this.glu = glu;
    }

    /**
     *
     */
    public ModelSelector() {
    }
    /**
     * Test if casted ray intersects with bounding box of given model.
     * @param model model to test.
     * @return True if casted ray intersects with bounding box of given model.
     */
    public Boolean pickBoundingBox(Model model) {
        for (int j = 0; j < model.getModelDims().triangulateBoundingBox().size(); j++)//each face of model
        {
            Vector3f[] t = model.getModelDims().triangulateBoundingBox().get(j);
            Vector3f intersectionPoint = calculateIntersection(t,false);
            if (intersectionPoint != null) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param mouseReleasedX  x position of mouse
     * @param mouseReleasedY y position of mouse
     * @param models list of models
     * @param viewport viewport
     * @param modelViewMatrix modelViewMatrix
     * @param projectionMatrix projectionMatrix
     * @param camera position of camera
     * @return Return model whose intersection with bouding box is nearest to the camera.
     */
    public Model pickBoundingBoxModel(
            double mouseReleasedX, double mouseReleasedY,
            ArrayList<Model> models,
            int[] viewport,
            double[] modelViewMatrix,
            double[] projectionMatrix,
            Vector3f camera) {
        castRay(mouseReleasedX, mouseReleasedY, viewport, modelViewMatrix, projectionMatrix);

        ArrayList<Vector3f> intersectionPoints = new ArrayList<Vector3f>();
        ArrayList<Model> selectedModels = new ArrayList<Model>();

        for (int i = 0; i < models.size(); i++) //all models
        {
            Model model = models.get(i);
            for (int j = 0; j < model.getModelDims().triangulateBoundingBox().size(); j++)//each face of model
            {
                Vector3f[] t = model.getModelDims().triangulateBoundingBox().get(j);
 
                Vector3f intersectionPoint = calculateIntersection(t,false);

                if (intersectionPoint != null) {
                    intersectionPoints.add(intersectionPoint);
                    selectedModels.add(model);
                }


            }
        }
        return findNearestIntersection(intersectionPoints, selectedModels, camera);

    }

    /**
     *
     * @param mouseReleasedX  x position of mouse
     * @param mouseReleasedY y position of mouse
     * @param models list of models
     * @param viewport viewport
     * @param modelViewMatrix modelViewMatrix
     * @param projectionMatrix projectionMatrix
     * @param camera position of camera
     * @return Return model whose intersection is nearest to the camera.
     */
    public Model pickModel(
            double mouseReleasedX, double mouseReleasedY,
            ArrayList<Model> models,
            int[] viewport,
            double[] modelViewMatrix,
            double[] projectionMatrix,
            Vector3f camera) {
        //    this.models = models;
        //    this.viewport = viewport;
        //    this.modelViewMatrix = modelViewMatrix;
        //     this.projectionMatrix = projectionMatrix;
        castRay(mouseReleasedX, mouseReleasedY, viewport, modelViewMatrix, projectionMatrix);

        ArrayList<Vector3f> intersectionPoints = new ArrayList<Vector3f>();
        ArrayList<Model> selectedModels = new ArrayList<Model>();

        for (int i = 0; i < models.size(); i++) //all models
        {
            Model model = models.get(i);
            if (pickBoundingBox(model)) { 

                for (int j = 0; j < model.getFaces().getNumFaces(); j++)//each face of model
                {
                    int[] faceVertsIx = model.getFaces().getFaceVertIdxs(j);
 
                    if (faceVertsIx[0] < model.getVerts().size()) {
                        for (int k = 1; k <= (faceVertsIx.length - 2); k++) { //each vertex of face
                            if (faceVertsIx[k] < model.getVerts().size() && faceVertsIx[k + 1] < model.getVerts().size()) {
                                Vector3f p1 = model.getVerts().get(faceVertsIx[0]-1);
                                Vector3f p2 = model.getVerts().get(faceVertsIx[k]-1);
                                Vector3f p3 = model.getVerts().get(faceVertsIx[k + 1]-1);
                                                           
                                Vector3f[] t = {p1, p2, p3};
                                Vector3f intersectionPoint = calculateIntersection(t,false);
                                if (intersectionPoint != null) {
                                     intersectionPoints.add(intersectionPoint);
                                     selectedModels.add(model);
                                }
                            }
                        }
                    }
                }
            }
        }

        return findNearestIntersection(intersectionPoints, selectedModels, camera);

    }

    public Vector3f getRayStartPoint(){
        return new Vector3f((float)P0[0], (float)P0[1], (float)P0[2]);
    }
    
    public Vector3f getRayEndPoint(){
       return new Vector3f((float)P1[0], (float)P1[1], (float)P1[2]);
    }
    
    public void castRay(double mouseReleasedX, double mouseReleasedY, int[] viewport, double[] modelViewMatrix, double[] projectionMatrix) {
        int realY;// GL y coord pos
        realY = viewport[3] - (int) mouseReleasedY - 1;
     //   System.out.println("Coordinates at cursor are (" + mouseReleasedX + ", " + realY);
        glu.gluUnProject(mouseReleasedX, realY, 0.0, //
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                P0, 0);
     
        glu.gluUnProject(mouseReleasedX, realY, 0.9, //
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                P1, 0);
    
    }

    /**
     *
     * @param P0 first point of selection ray
     * @param P1 second point of selection ray
     */
    public void setRay(double P0[], double P1[]){
        this.P0 = P0;
        this.P1 = P1;
       
    }
    //if returnPlaneIntersection treu return intersection with plane given by triangle, else returns intersection with triangle
    /**
     *
     * @param triangle triangle for intersetion calculation.
     * @param returnPlaneIntersection set true if method shoud return intersection with plane given by triangle
     * @return If returnPlaneIntersection true return intersection with plane given by triangle, else returns intersection with triangle
     */
    public Vector3f calculateIntersection(Vector3f[] triangle, boolean returnPlaneIntersection) {
        if (triangle.length == 3) {
            //ray - points  P0, P1
            //triangle - points T0,T1,T2
            Vector3f u = new Vector3f(triangle[1].getX() - triangle[0].getX(), //T1-T0
                    triangle[1].getY() - triangle[0].getY(),
                    triangle[1].getZ() - triangle[0].getZ());
            Vector3f v = new Vector3f(triangle[2].getX() - triangle[0].getX(),//T2-T0
                    triangle[2].getY() - triangle[0].getY(),
                    triangle[2].getZ() - triangle[0].getZ());
            Vector3f n = new Vector3f();
            n.cross(u, v);
            Vector3f tp = new Vector3f(triangle[0].getX() - (float) P0[0], //T0-P0
                    triangle[0].getY() - (float) P0[1],
                    triangle[0].getZ() - (float) P0[2]);

            Vector3f pp = new Vector3f((float) P1[0] - (float) P0[0], //P1-P0
                    (float) P1[1] - (float) P0[1],
                    (float) P1[2] - (float) P0[2]);

            n.normalize();
            pp.normalize();

            Vector3f p0 = new Vector3f((float) P0[0], (float) P0[1], (float) P0[2]);

            float i;
            if (n.dot(pp) != 0) {
                //     i = tp.dot(n) / pp.dot(n);
            
                 float d = (-1) * (n.dot(triangle[0]));
                i = -(n.dot(p0) + d) / n.dot(pp);
            } else {
                return null;
            }
            
            if (i >= 0) {
                Vector3f intP = new Vector3f( p0.x + i * pp.x, // intersection with plane
                        p0.y + i * pp.y,
                        p0.z + i * pp.z);

                if (returnPlaneIntersection) {
                    return intP;
                }
                
                Vector3f w = new Vector3f(intP.getX() - triangle[0].getX(), //PI-T0
                    intP.getY() - triangle[0].getY(),
                    intP.getZ() - triangle[0].getZ());

            float uv = u.dot(v);
            float uu = u.dot(u);
            float vv = v.dot(v);
            float wu = w.dot(u);
            float wv =  w.dot( v);
            float s = (uv * wv - vv * wu) / (uv * uv - uu * vv);
            float t = (uv * wu - uu * wv) / (uv * uv - uu * vv);
            
            if (s >= 0 && t >= 0 && s + t <= 1) {
                  return intP;//intersectionPoint;
            }
            }
        }
        return null;
    }
    
     /**
     *
     * @param pl line point
     * @param u line vector
     * @param n plane normal
     * @param p point from plane
     * @return intersection of line and plane, if it doesn't exist returns null
     */
    public static Vector3f findLinePlaneIntersection(Vector3f pl, Vector3f u, Vector3f n, Vector3f p) {
        Vector3f w = new Vector3f(pl);
        w.sub(p);

        float D = n.dot(u);
        float N = -n.dot(w);

        if (Math.abs(D) == 0) {   // segment is parallel to plane
            if (N == 0) // segment lies in plane
            {
                return pl;
            } else {
                return null;                    // no intersection
            }
        }
        // they are not parallel
        // compute intersect param
        float sI = N / D;

        Vector3f intersection = new Vector3f(u);
        intersection.scale(sI);
        intersection.add(pl);

        return intersection;
    }
    
    
    private Model findNearestIntersection(ArrayList<Vector3f> intersectionPoints, ArrayList<Model> models, Vector3f camera) {
        if (intersectionPoints.size() > 0) {
            //  Vector3f intPoint = intersectionPoints.get(0);
            Model model = models.get(0);
            intersection = intersectionPoints.get(0);
            double dist = Math.sqrt(
                    (camera.x - intersectionPoints.get(0).getX()) * (camera.x - intersectionPoints.get(0).getX())
                    + (camera.y - intersectionPoints.get(0).getY()) * (camera.y - intersectionPoints.get(0).getY())
                    + (camera.z - intersectionPoints.get(0).getZ()) * (camera.z - intersectionPoints.get(0).getZ()));
            //  (P0[0] - intersectionPoints.get(0).getX()) * (P0[0] - intersectionPoints.get(0).getX())
            //  + (P0[1] - intersectionPoints.get(0).getY()) * (P0[1] - intersectionPoints.get(0).getY())
            //  + (P0[2] - intersectionPoints.get(0).getZ()) * (P0[2] - intersectionPoints.get(0).getZ()));
            for (int i = 1; i < intersectionPoints.size(); i++) {
                double newDist = Math.sqrt(
                        (camera.x - intersectionPoints.get(i).getX()) * (camera.x - intersectionPoints.get(i).getX())
                        + (camera.y - intersectionPoints.get(i).getY()) * (camera.y - intersectionPoints.get(i).getY())
                        + (camera.z - intersectionPoints.get(i).getZ()) * (camera.z - intersectionPoints.get(i).getZ()));

                //     (P0[0] - intersectionPoints.get(i).getX()) * (P0[0] - intersectionPoints.get(i).getX())
                //     + (P0[1] - intersectionPoints.get(i).getY()) * (P0[1] - intersectionPoints.get(i).getY())
                //     + (P0[2] - intersectionPoints.get(i).getZ()) * (P0[2] - intersectionPoints.get(i).getZ()));
                if (newDist < dist) {
                    dist = newDist;
                    intersection = intersectionPoints.get(i);
                    model = models.get(i);
                }

            }
            return model;

        }

        return null;
    }

    /**
     *
     * @return intersetion point.
     */
    public Vector3f getIntersection() {
        return intersection;
    }
}
