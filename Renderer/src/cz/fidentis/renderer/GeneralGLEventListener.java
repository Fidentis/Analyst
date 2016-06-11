/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.renderer;

import cz.fidentis.model.Model;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_FALSE;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static javax.media.opengl.GL2ES2.GL_LINK_STATUS;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarna Furmanová
 */
public class GeneralGLEventListener implements GLEventListener {
    protected static float[] backgroundColor = {0.2f, 0.2f, 0.2f, 0.0f};
    
    /**
     *
     */
    protected GLU glu;
    /**
     *
     */
    protected GL2 gl;
    /*   protected int[] viewport = new int[4];
     protected double[] modelViewMatrix = new double[16];
     protected double[] projectionMatrix = new double[16];
     protected double wcoord[] = new double[3];
     protected double wcoord1[] = new double[3];
     protected double wcoord2[] = new double[3];*/
    /**
     *
     */
    private ArrayList<Model> models = new ArrayList<>();
    /**
     *
     */
    protected Vector3f defaultPosition = new Vector3f(0, 0, 300);
    protected Vector3f currentPosition = new Vector3f(0, 0, 300);
    /**
     *
     */
    protected double zCenter = 0;
    /**
     *
     */
    protected double xCenter = 0;
    /**
     *
     */
    protected double yCenter = 0;

    protected double zCameraPosition;
    /**
     *
     */
    protected double xCameraPosition;
    /**
     *
     */
    protected double yCameraPosition;
    /**
     *
     */
    protected double zUpPosition = 0;
    /**
     *
     */
    protected double xUpPosition = 0;
    /**
     *
     */
    protected double yUpPosition = 1;
    /**
     *
     */
    protected int modelDL;
    /**
     *
     */
    protected int modelDLwithoutTxt;
    /**
     *
     */
    protected boolean drawTextures = true;

    /**
     *
     * @param glad
     */
    @Override
    public void init(GLAutoDrawable glad) {
        this.gl = (GL2) glad.getGL();
        glu = new GLU();
        gl.setSwapInterval(1);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(backgroundColor[0],backgroundColor[1],backgroundColor[2],1);     // background for GLCanvas
        gl.glShadeModel(GL2.GL_SMOOTH);    // use smooth shading

        gl.glDepthFunc(GL2.GL_LESS);
        gl.glDepthRange(0.0, 1.0);

        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glDisable(GL2.GL_CULL_FACE);
    }

    /**
     *
     * @param glad
     */
    @Override
    public void dispose(GLAutoDrawable glad) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param glad
     */
    @Override
    public void display(GLAutoDrawable glad) {
        gl.glClearColor(backgroundColor[0],backgroundColor[1],backgroundColor[2],1);     // background for GLCanvas       
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(xCameraPosition, yCameraPosition, zCameraPosition, xCenter, yCenter, zCenter, xUpPosition, yUpPosition, zUpPosition);

        /*
         Vector3f xAxis = new Vector3f((float) (yCameraPosition * zUpPosition - zCameraPosition * yUpPosition),
         (float) (zCameraPosition * xUpPosition - xCameraPosition * zUpPosition),
         (float) (xCameraPosition * yUpPosition - xUpPosition * yCameraPosition));
         gl.glRotatef(view_rotx, xAxis.x, xAxis.y, xAxis.z);
         gl.glRotatef(view_roty, (float)xUpPosition, (float)yUpPosition, (float)zUpPosition);
         gl.glRotatef(view_rotz, 0f - (float)xCameraPosition, 0.0f - (float)yCameraPosition, 0f - (float)zCameraPosition);
         */
        gl.glPushMatrix();

        gl.glShadeModel(GL2.GL_SMOOTH);
        /*
         for (int i = 0; i < models.size(); i++) {
         if (models.get(i) != null) {
         gl.glPushMatrix();
         if (drawTextures) {
         models.get(i).draw(gl);
         } else {
         float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
         //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
         gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
         gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
         gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
         float[] colorKs = {0, 0, 0, 1.0f};
         gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
         models.get(i).drawWithoutTextures(gl);
         }

         gl.glPopMatrix();
         }
         }*/


        /*if (drawTextures) {
         gl.glCallList(modelDL);
         } else {
         gl.glCallList(modelDLwithoutTxt);
         }*/
        if (models.size() > 0) {

            reloadTextures();

            float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
            float[] colorKs = {0, 0, 0, 1.0f};

            if (drawTextures) {     //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                for (int i = 0; i < models.size(); i++) {
                    models.get(i).draw(gl);
                }
            } else {
                for (int i = 0; i < models.size(); i++) {

                    //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                    gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                    models.get(i).drawWithoutTextures(gl, null);
                }
            }

            gl.glPopMatrix();
            gl.glFlush();
        }
    }

    /**
     *
     */
    public void reloadTextures() {
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i) != null) {
                if (models.get(i).getMatrials() != null) {
                    for (int j = 0; j < models.get(i).getMatrials().getMatrials().size(); j++) {
                        models.get(i).getMatrials().reloadTextures(gl);
                    }
                }
            }
        }

    }

    /**
     *
     * @param glad
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        reloadTextures();

        if (height == 0) {
            height = 1;    // to avoid division by 0 in aspect ratio below
        }
        gl.glViewport(x, y, width, height);  // size of drawing area

        float h = (float) height / (float) width;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(65, width / (float) height, 5.0f, 1500.0f);
        //  gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 1500.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        //  gl.glTranslatef(0.0f, 0.0f, -40.0f);
    }

    /**
     *
     * @param model
     */
    public void setModels(Model model) {
        //     models.clear();
        models.add(0, model);
    }

    /**
     *
     * @param models
     */
    public void setModels(ArrayList<Model> models) {
        //    models.clear();
        this.models = models;
    }

    /**
     *
     * @return
     */
    public Model getModel() {
        return models.get(0);
    }

    public void removeModel() {
        models.clear();
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setCameraPosition(float x, float y, float z) {
        currentPosition.setX(x);
        currentPosition.setY(y);
        currentPosition.setZ(z);

        setNewCameraPosition(currentPosition);
        //  zCameraPosition = defaultPosition.getZ();
        //  xCameraPosition = defaultPosition.getX();
        //   yCameraPosition = defaultPosition.getY();

    }
    

    /**
     *
     * @param position
     */
    public void setNewCameraPosition(Vector3f position) {
        xCameraPosition = position.getX();
        yCameraPosition = position.getY();
        zCameraPosition = position.getZ();

    }

    /**
     *
     * @param degree degree of rotation
     */
    public void rotateUp(double degree) {

        rotate(-degree, 0);

    }

    /**
     *
     * @param degree degree of rotation
     */
    public void rotateDown(double degree) {
        rotate(degree, 0);
    }

    /**
     *
     * @param degree degree of rotation
     */
    public void rotateLeft(double degree) {

        rotate(0, degree);

    }

    /**
     *
     * @param degree degree of rotation
     */
    public void rotateRight(double degree) {
        rotate(0, -degree);

    }

    private Vector3f getXaxis() {
        Vector3f xAxis = new Vector3f((float) ((yCameraPosition - yCenter) * zUpPosition - (zCameraPosition - zCenter) * yUpPosition),
                (float) ((zCameraPosition - zCenter) * xUpPosition - (xCameraPosition - xCenter) * zUpPosition),
                (float) ((xCameraPosition - xCenter) * yUpPosition - xUpPosition * (yCameraPosition - yCenter)));

        /* Vector3f xAxis = new Vector3f((float) (yCameraPosition * zUpPosition - zCameraPosition * yUpPosition),
         (float) (zCameraPosition * xUpPosition - xCameraPosition * zUpPosition),
         (float) (xCameraPosition * yUpPosition - xUpPosition * yCameraPosition));
         */
        float length = (float) Math.sqrt(xAxis.getX() * xAxis.getX() + xAxis.getY() * xAxis.getY() + xAxis.getZ() * xAxis.getZ());
        xAxis.setX(xAxis.getX() / length);
        xAxis.setY(xAxis.getY() / length);
        xAxis.setZ(xAxis.getZ() / length);
        return xAxis;
    }

    private Vector3f getYaxis() {
        Vector3f yAxis = new Vector3f((float) xUpPosition, (float) yUpPosition, (float) zUpPosition);
        float length = (float) Math.sqrt(yAxis.getX() * yAxis.getX() + yAxis.getY() * yAxis.getY() + yAxis.getZ() * yAxis.getZ());
        yAxis.setX(yAxis.getX() / length);
        yAxis.setY(yAxis.getY() / length);
        yAxis.setZ(yAxis.getZ() / length);

        return yAxis;
    }

    /**
     * Rotates object around axes that apear as horizontal and vertical axe on
     * screen (paralel to the sceen edges), intersecting at the center of
     * screen( i.e head center).
     *
     * @param xAngle angle around vertical axe on screen
     * @param yAngle angle around horizontal axe on screen
     */
    public void rotate(double xAngle, double yAngle) {
        Vector3f xAxis = getXaxis();
        Vector3f yAxis = getYaxis();

        Vector3f point = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);

        Vector3f camera = rotateAroundAxe(point, xAxis, Math.toRadians(xAngle));
        camera = rotateAroundAxe(camera, yAxis, Math.toRadians(yAngle));

        point = new Vector3f((float) xUpPosition, (float) yUpPosition, (float) zUpPosition);

        Vector3f up = rotateAroundAxe(point, xAxis, Math.toRadians(xAngle));
        up = rotateAroundAxe(up, yAxis, Math.toRadians(yAngle));

        xUpPosition = up.getX();
        yUpPosition = up.getY();
        zUpPosition = up.getZ();

        setNewCameraPosition(camera);
    }

    public void move(double xShift, double yShift) {
        Vector3f xAxis = getXaxis();
        Vector3f yAxis = getYaxis();

        Vector3f shift = new Vector3f((float) (xAxis.x * xShift + yAxis.x * yShift), (float) (xAxis.y * xShift + yAxis.y * yShift), (float) (xAxis.z * xShift + yAxis.z * yShift));
        Vector3f camera = new Vector3f((float) xCameraPosition + shift.x, (float) yCameraPosition + shift.y, (float) zCameraPosition + shift.z);
        xCenter += shift.x;
        yCenter += shift.y;
        zCenter += shift.z;

        setNewCameraPosition(camera);

    }

    /**
     * Calculate the new position f point from given angle and rotation axe.
     *
     * @param point original position
     * @param u vector of rotation axe
     * @param angle angle of rotation
     * @return new position
     */
    public Vector3f rotateAroundAxe(Vector3f point, Vector3f u, double angle) {
        Vector3f p;
        float x = (float) ((Math.cos(angle) + u.getX() * u.getX() * (1 - Math.cos(angle))) * point.getX()
                + (u.getX() * u.getY() * (1 - Math.cos(angle)) - u.getZ() * Math.sin(angle)) * point.getY()
                + (u.getX() * u.getZ() * (1 - Math.cos(angle)) + u.getY() * Math.sin(angle)) * point.getZ());
        float y = (float) ((u.getX() * u.getY() * (1 - Math.cos(angle)) + u.getZ() * Math.sin(angle)) * point.getX()
                + (Math.cos(angle) + u.getY() * u.getY() * (1 - Math.cos(angle))) * point.getY()
                + (u.getY() * u.getZ() * (1 - Math.cos(angle)) - u.getX() * Math.sin(angle)) * point.getZ());
        float z = (float) ((u.getX() * u.getZ() * (1 - Math.cos(angle)) - u.getY() * Math.sin(angle)) * point.getX()
                + (u.getY() * u.getZ() * (1 - Math.cos(angle)) + u.getX() * Math.sin(angle)) * point.getY()
                + (Math.cos(angle) + u.getZ() * u.getZ() * (1 - Math.cos(angle))) * point.getZ());
        p = new Vector3f(x, y, z);

        return p;

    }

    /**
     *
     */
    public void rotationAndSizeRestart() {

        xUpPosition = 0;
        yUpPosition = 1;
        zUpPosition = 0;

        setNewCameraPosition(defaultPosition);
        xCenter = 0;
        yCenter = 0;
        zCenter = 0;

    }

    /**
     *
     * @param distance
     */
    public void zoomIn(double distance) {
        double x = xCameraPosition - xCenter;
        double y = yCameraPosition - yCenter;
        double z = zCameraPosition - zCenter;
        double sqrt = Math.sqrt(x * x + y * y + z * z);
        // double sqrt = Math.sqrt(xCameraPosition * xCameraPosition + yCameraPosition * yCameraPosition + zCameraPosition * zCameraPosition);

        if (sqrt > 0) {
            // xCameraPosition = (sqrt - distance) * xCameraPosition / sqrt;
            // yCameraPosition = (sqrt - distance) * yCameraPosition / sqrt;
            // zCameraPosition = (sqrt - distance) * zCameraPosition / sqrt;
            xCameraPosition = xCenter + ((sqrt - distance) * x / sqrt);
            yCameraPosition = yCenter + ((sqrt - distance) * y / sqrt);
            zCameraPosition = zCenter + ((sqrt - distance) * z / sqrt);
        }

    }

    /**
     *
     * @param distance
     */
    public void zoomOut(double distance) {
        double x = xCameraPosition - xCenter;
        double y = yCameraPosition - yCenter;
        double z = zCameraPosition - zCenter;
        double sqrt = Math.sqrt(x * x + y * y + z * z);

        //double sqrt = Math.sqrt(xCameraPosition * xCameraPosition + yCameraPosition * yCameraPosition + zCameraPosition * zCameraPosition);
        if (sqrt == 0) {
            sqrt = 1;
        }

        xCameraPosition = xCenter + ((sqrt + distance) * x / sqrt);
        yCameraPosition = yCenter + ((sqrt + distance) * y / sqrt);
        zCameraPosition = zCenter + ((sqrt + distance) * z / sqrt);

        // xCameraPosition = (sqrt + distance) * xCameraPosition / sqrt;
        // yCameraPosition = (sqrt + distance) * yCameraPosition / sqrt;
        // zCameraPosition = (sqrt + distance) * zCameraPosition / sqrt;
    }

    public static float[] getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(float[] backgroundColor) {
        GeneralGLEventListener.backgroundColor = backgroundColor;
    }

    
    
    /**
     *
     * @param drawTextures
     */
    public void setDrawTextures(boolean drawTextures) {
        this.drawTextures = drawTextures;
    }
    
    
    public GL2 getContext(){
        return gl;
    }
    
    public BufferedImage screenShot(int width, int height){
         int i = gl.glGetError();
        
        gl.glFinish();
        gl.glReadBuffer(gl.GL_BACK); // or GL.GL_BACK

        i = gl.glGetError();
        
        ByteBuffer glBB = ByteBuffer.allocate(3 * width * height);
        gl.glReadPixels(0, 0, width, height, gl.GL_BGR, gl.GL_BYTE, glBB);
        
        i = gl.glGetError();
        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] bd = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = 2 * glBB.get();
                int g = 2 * glBB.get();
                int r = 2 * glBB.get();

                bd[(height - y - 1) * width + x] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }
        return bi;
    }
}
