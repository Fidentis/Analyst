package cz.fidentis.renderer;

import com.jogamp.opengl.util.gl2.GLUT;
import cz.fidentis.composite.ModelSelector;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.*;
import javax.vecmath.Vector3f;
import jv.object.PsDebug;

/**
 *
 * @author Katka
 */
public class FeaturePointsGLEventListener extends GeneralGLEventListener {

    //  private double zCamraDistance = 700.0;      // for the camera position
    private GLUT glut = new GLUT();
    //   private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
    private int[] viewport = new int[4];
    private double[] modelViewMatrix = new double[16];
    private double[] projectionMatrix = new double[16];
    private FeaturePointsUniverse fpUniverse;
    private List<FacialPoint> facialPoints = new ArrayList<FacialPoint>();
    private ArrayList<Model> models = new ArrayList<>();
    
    private int indexOfSelectedPoint = -1;
    private float facialPointRadius = 2;
    private float[] colorOfPoint = new float[]{1f, 0f, 0f, 1.0f};

    /**
     *
     * @param drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) // the model is rendered
    {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(xCameraPosition, yCameraPosition, zCameraPosition, 0, 0, 0, xUpPosition, yUpPosition, zUpPosition);
        
        gl.glPushMatrix();

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        gl.glShadeModel(GL2.GL_SMOOTH);
        /*
         gl.glPushMatrix();
         if (models.size() > 0) {
         models.get(0).draw(gl);
         }

         gl.glPopMatrix();
         */
         float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
            //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
            float[] colorKs = {0, 0, 0, 1.0f};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
        if (drawTextures) {
            
            models.get(0).draw(gl);

        } else {
            
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
            if(models.size()>0){
            models.get(0).drawWithoutTextures(gl, null);}
        }
        if (facialPoints != null) {
            drawFacialPoints(facialPoints);
        }

        gl.glPopMatrix();

        gl.glFlush();
    } // end of display

    public boolean selectPoint(double x, double y) {
        if (facialPoints.isEmpty() || facialPoints == null) {
            return false;
        }
        ModelSelector picker = new ModelSelector(glu);
        picker.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
        Vector3f v = picker.getRayStartPoint();
        Vector3f v1 = picker.getRayEndPoint();
        Vector3f u = new Vector3f(v1.x - v.x, v1.y - v.y, v1.z - v.z);
        boolean selected = false;
        
        float minDist = Float.MAX_VALUE;
        //for (FacialPoint point : facialPoints) {
        for (int i = 0; i < facialPoints.size(); i++) {
            FacialPoint fp = facialPoints.get(i);
            double t = ((fp.getPosition().x - v.x) * (v1.x - v.x)
                    + (fp.getPosition().y - v.y) * (v1.y - v.y) + (fp.getPosition().z - v.z) * (v1.z - v.z))
                    / (float) (Math.pow(u.x, 2) + Math.pow(u.y, 2) + Math.pow(u.z, 2));
            Vector3f w = new Vector3f((float) (v.x + t * u.x), (float) (v.y + t * u.y), (float) (v.z + t * u.z));
            float dist = (float) Math.sqrt(Math.pow(w.x - fp.getPosition().x, 2) + Math.pow(w.y - fp.getPosition().y, 2) + Math.pow(w.z - fp.getPosition().z, 2));

            if (dist < facialPointRadius && dist < minDist) {
                indexOfSelectedPoint = i;
                minDist = dist;
                selected = true;
            }
            
            //System.out.println("distance of " + i + ". point: " + dist);
        }
        
        return selected;

    }
    
    public Vector3f checkPointInMesh(double x, double y) {

        ModelSelector picker = new ModelSelector(glu);
        picker.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
        ArrayList<Vector3f> intersectionPoints = new ArrayList<Vector3f>();
        
        Model model1 = models.get(0);
        for (int j = 0; j < model1.getFaces().getNumFaces(); j++)//each face of model
        {
            int[] faceVertsIx = model1.getFaces().getFaceVertIdxs(j);

            if (faceVertsIx[0] < model1.getVerts().size()) {
                for (int k = 1; k <= (faceVertsIx.length - 2); k++) { //each vertex of face
                    if (faceVertsIx[k] < model1.getVerts().size() && faceVertsIx[k + 1] < model1.getVerts().size()) {
                        Vector3f p1 = model1.getVerts().get(faceVertsIx[k - 1] - 1);
                        Vector3f p2 = model1.getVerts().get(faceVertsIx[k] - 1);
                        Vector3f p3 = model1.getVerts().get(faceVertsIx[k + 1] - 1);

                        Vector3f[] t = {p1, p2, p3};
                        Vector3f intersectionPoint = picker.calculateIntersection(t, false);
                        if (intersectionPoint != null) {
                            intersectionPoints.add(intersectionPoint);
                        }
                    }
                }
            }
        }
        if (intersectionPoints.isEmpty() )
            return null;
        else if (intersectionPoints.size() == 1)
            return intersectionPoints.get(0); 
        else 
            return findNearestPoint(intersectionPoints);

    }
    
    private Vector3f findNearestPoint(ArrayList<Vector3f> intersectionPoints) {
        float minDist = Float.MAX_VALUE;
        Vector3f intPoint = new Vector3f();
        for (int i = 0; i < intersectionPoints.size(); i++) {
            double dist = Math.sqrt(
                  Math.pow(xCameraPosition - intersectionPoints.get(i).getX(), 2)
                + Math.pow(yCameraPosition - intersectionPoints.get(i).getY(), 2)
                + Math.pow(zCameraPosition - intersectionPoints.get(i).getZ(), 2));

            if (dist < minDist) {
                minDist = (float)dist;
                intPoint = intersectionPoints.get(i);
            }
        }
        return intPoint;
    }
    
    public boolean editSelectedPoint(Vector3f coords){
        if (coords != null){
            facialPoints.get(indexOfSelectedPoint).setCoords(coords);
            return true;
        } else
            return false;
    }
        
    

    /**
     *
     * @param facialPoints
     */
    
    public void drawFacialPoints(List<FacialPoint> facialPoints) {

        gl.glPushMatrix();
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);
        
        float[] color;
        for (int i = 0; i < facialPoints.size(); i++) {
            if (indexOfSelectedPoint != -1 && i == indexOfSelectedPoint){
                color = new float[]{0f, 1f, 0f, 1.0f};
            } else 
                color = colorOfPoint;
                
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
        float[] colorKs = {0, 0, 0, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
        //for (FacialPoint point : facialPoints) {
            FacialPoint fp = facialPoints.get(i);
            gl.glTranslated(fp.getPosition().x, fp.getPosition().y, fp.getPosition().z);
            glut.glutSolidSphere((double)facialPointRadius, 16, 16);
            gl.glTranslated(-fp.getPosition().x, -fp.getPosition().y, -fp.getPosition().z);
        }

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();

    }

    /**
     *
     * @param drawable
     * @param modeChanged
     * @param deviceChanged
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    // Methods required for the implementation of MouseListener
    /**
     *
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    
        public void setModels(Model model) {
   //     models.clear();
        models.add(0,model);
    }

    /**
     *
     * @param models
     */
    public void setModels(ArrayList<Model> models) {
    //    models.clear();
        this.models = models;
    }
    
    public void initComputation() {
        fpUniverse = new FeaturePointsUniverse(models.get(0));
        facialPoints = new ArrayList<FacialPoint>();
    }
    
    public void /*List<FacialPoint>*/ getAllPoints(int minSize) {
        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);
        fpUniverse.findNose(minSize);
        facialPoints = fpUniverse.getFacialPoints();
        fpUniverse.findMouth();
        facialPoints = fpUniverse.getFacialPoints();
        fpUniverse.findEyes();
        facialPoints = fpUniverse.getFacialPoints();
        PsDebug.getConsole().setVisible(false);

        //return facialPoints;
    }

    public int getIndexOfSelectedPoint() {
        return indexOfSelectedPoint;
    }

    public void setIndexOfSelectedPoint(int indexOfSelectedPoint) {
        this.indexOfSelectedPoint = indexOfSelectedPoint;
    }

    public FacialPoint getFacialPoint(int index) {
        return facialPoints.get(index);
    }

    public void setColorOfPoint(float[] colorOfPoint) {
        this.colorOfPoint = colorOfPoint;
    }

    public void setFacialPointRadius(float facialPointRadius) {
        this.facialPointRadius = facialPointRadius;
    }

    public FeaturePointsUniverse getFpUniverse() {
        return fpUniverse;
    }
    
    
}
