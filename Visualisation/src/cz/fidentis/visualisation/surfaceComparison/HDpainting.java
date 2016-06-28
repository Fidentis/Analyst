package cz.fidentis.visualisation.surfaceComparison;

import cz.fidentis.model.Graph2;
import com.jogamp.opengl.util.gl2.GLUT;
import cz.fidentis.model.Faces;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.SortUtils;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.util.List;
import static javax.media.opengl.GL2.GL_ALL_ATTRIB_BITS;

/**
 * Created with IntelliJ IDEA. User: Zuzana Ferkova Date: 11.3.2013 Time: 16:52
 *
 * Class providing functions to visualize Hausdorff Distance in the application.
 */
public class HDpainting {

    private final HDpaintingInfo info;
    private float maxThresh = Float.NEGATIVE_INFINITY;
    private float minThresh = Float.POSITIVE_INFINITY;


    
    
    public HDpainting(HDpaintingInfo info) {
        this.info = info;
        info.graph = new Graph2(info.getModel());
        info.graph.createGraph();
        info.indicesForNormals = info.graph.indicesFordDensityNormals(info.getDensity());
    }

    public HDpaintingInfo getInfo() {
        return info;
    }

    /**
     * Computes the color for currently processed point. Based on HSV color
     * model.
     *
     * @param currentDistance - HD distance for the currently processed point
     * @param maxDistance - max HD distance in the mesh
     * @return float[] color - computed color
     */
    public float[] chooseColorHSVMapping(float currentDistance, float maxDistance, float minDistance) {

        float currentParameter = (currentDistance - minDistance) / (maxDistance - minDistance);
        Color minColor2int = new Color(info.getMinColor()[0], info.getMinColor()[1], info.getMinColor()[2]);
        Color maxColor2int = new Color(info.getMaxColor()[0], info.getMaxColor()[1], info.getMaxColor()[2]);

        float[] hsb1 = Color.RGBtoHSB(minColor2int.getRed(), minColor2int.getGreen(), minColor2int.getBlue(), null);
        float h1 = hsb1[0];
        float s1 = hsb1[1];
        float b1 = hsb1[2];

        float[] hsb2 = Color.RGBtoHSB(maxColor2int.getRed(), maxColor2int.getGreen(), maxColor2int.getBlue(), null);
        float h2 = hsb2[0];
        float s2 = hsb2[1];
        float b2 = hsb2[2];

        // determine clockwise and counter-clockwise distance between hues
        float distCCW;
        float distCW;

        if (h1 >= h2) {
            distCCW = h1 - h2;
            distCW = 1 + h2 - h1;
        } else {
            distCCW = 1 + h1 - h2;
            distCW = h2 - h1;
        }

        float hue;

        if (distCW >= distCCW) {
            hue = h1 + (distCW * currentParameter);
        } else {
            hue = h1 - (distCCW * currentParameter);
        }

        if (hue < 0) {
            hue = 1 + hue;
        }
        if (hue > 1) {
            hue = hue - 1;
        }

        float saturation = (1 - currentParameter) * s1 + currentParameter * s2;
        float brightness = (1 - currentParameter) * b1 + currentParameter * b2;

        Color hsb = Color.getHSBColor(hue, saturation, brightness);

        return hsb.getRGBColorComponents(info.getVector());
    }

    /**
     * A method to visualize Hausdorff Distance(HD) by painting the faces of the
     * mesh accordingly. Uses class attribute 'minColor' to determine the color
     * of the point with minimal HD. Uses class attribute 'maxColor' to
     * determine the color of the point with maximum HD. * Color is computed for
     * each point, based on the HD taken from the attribute 'distance',
     * interpolation of the face is then performed by OpenGL.
     *
     * This method chooses colors from HSV color space.
     *
     * @param gl - GL object
     */
    public void paintDistanceFace(GL2 gl) {
        Faces faces = info.getModel().getFaces();
        List<Vector3f> mesh = info.getModel().getVerts();
        List<Vector3f> normals = info.getModel().getNormals();

        List<Float> distanceCopy = new ArrayList<Float>(info.getDistance().size()); /*= new ArrayList<Float>(distance);*/

        for (Float f : info.getDistance()) {
            if (!info.isUseRelative()) {
                distanceCopy.add(Math.abs(f));
            } else {
                distanceCopy.add(f);
            }
        }
        List<Float> sorted = SortUtils.instance().sortValues(distanceCopy);

        if (info.getMaxThreshValue() == Float.POSITIVE_INFINITY) {
            maxThresh = sorted.get(sorted.size() - 1);
        } else {
            maxThresh = info.getMaxThreshValue();
        }
        
        if (info.getMinThreshValue() == Float.NEGATIVE_INFINITY) {
            minThresh = sorted.get(0);
        } else {
            minThresh = info.getMinThreshValue();
        }

        float[] color = new float[3];
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        Vector3f normal;
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);
        for (int i = 0; i < faces.getNumFaces(); i++) {

            int[] facesInd = faces.getFaceVertIdxs(i);
            int[] faceNormIndex = faces.getFaceNormalIdxs(i);

            gl.glBegin(GL.GL_TRIANGLES);
            for (int f = 0; f < facesInd.length; f++) {

                //computing color for the point
                if(info.getDistance().size() < facesInd[f] - 1){
                    color[0] = 0.5f;
                    color[1] = 0.5f;
                    color[2] = 0.5f;
                }
                else if (info.getDistance().get(facesInd[f] - 1) <= maxThresh && maxThresh != Float.NEGATIVE_INFINITY
                        && info.getDistance().get(facesInd[f] - 1) >= minThresh && minThresh != Float.POSITIVE_INFINITY) {
                    color = chooseColorHSVMapping(info.getDistance().get(facesInd[f] - 1), maxThresh, sorted.get(0));
                } else {
                    color[0] = 0.5f;
                    color[1] = 0.5f;
                    color[2] = 0.5f;
                }

                if (Float.isNaN(color[0])
                        || Float.isNaN(color[1])
                        || Float.isNaN(color[2])) {
                    color[0] = 0.5f;
                    color[1] = 0.5f;
                    color[2] = 0.5f;
                }

                if (faceNormIndex[f] != 0) {  // if there are normals, render them
                    normal = normals.get(faceNormIndex[f] - 1);
                    gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
                }

                float[] ambient = {0.1f, 0.1f, 0.1f, 0.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
                float[] colorKs = {0, 0, 0, 1.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                gl.glVertex3f(mesh.get(facesInd[f] - 1).getX(), mesh.get(facesInd[f] - 1).getY(), mesh.get(facesInd[f] - 1).getZ());
            }
            gl.glEnd();

        }

        gl.glPopAttrib();
    }

    /**
     * @author Jakub Palenik recycled paintDistanceFace for rendering model of
     * base color. Added normal visualization with length based on HDDistance
     * and density filtration.
     *
     * @param gl
     */
    
    private FloatBuffer vertices;
    private IntBuffer normals;
    private int[] VBO = new int[2];
    
    public void paintNormals(GL2 gl) {
        info.c.assignGl(gl);
        Faces faces = info.getModel().getFaces();
        
        List<Vector3f> mesh = info.getModel().getVerts();
        List<Vector3f> normals = info.getModel().getNormals();

        List<Float> distanceCopy = new ArrayList<>(info.getDistance().size());

        for (Float f : info.getDistance()) {
            if (!info.isUseRelative()) {
                distanceCopy.add(Math.abs(f));
            } else {
                distanceCopy.add(f);
            }
        }

        float[] color = new float[3];
        color[0] = 0.8667f;
        color[1] = 0.7176f;
        color[2] = 0.6275f;

        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);

        Vector3f normal;

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);
        
        for (int i = 0; i < faces.getNumFaces(); i++) {

            int[] facesInd = faces.getFaceVertIdxs(i);
            int[] faceNormIndex = faces.getFaceNormalIdxs(i);

            gl.glBegin(GL.GL_TRIANGLES);
            for (int f = 0; f < facesInd.length; f++) {
                if (faceNormIndex[f] != 0) {  // if there are normals, render them
                    normal = normals.get(faceNormIndex[f] - 1);
                    gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
                }

                float[] ambient = {0.1f, 0.1f, 0.1f, 0.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

                float[] colorKs = {0, 0, 0, 1.0f};
                gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                gl.glVertex3f(mesh.get(facesInd[f] - 1).getX(), mesh.get(facesInd[f] - 1).getY(), mesh.get(facesInd[f] - 1).getZ());
            }
            gl.glEnd();
        }

        if (info.getRecompute()) {
            recomputeNormals(gl, info, distanceCopy);
            info.setRecompute(false);
        }
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
            info.c.draw();
        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
 
        gl.glPopAttrib();
    }
    
     private void recomputeNormals(GL2 gl, HDpaintingInfo info, List<Float> distanceCopy){
        
        info.c.clear();

        List<Vector3f> mesh = info.getModel().getVerts();
        List<Vector3f> normals = info.getModel().getNormals();
        
        int numVerts = info.indicesForNormals[0].length;
          
    for (int k = 0; k < numVerts; k++) {

            Vector3f vecB = new Vector3f(mesh.get(info.indicesForNormals[0][k] - 1));
            Vector3f vecN = new Vector3f(normals.get(info.indicesForNormals[1][k] - 1));
            Vector3f vecE = new Vector3f(vecB);
            
            
            
            float sig = (distanceCopy.get(info.indicesForNormals[0][k] - 1 ));
            sig = Math.signum(sig);
            /*vecN.scale((info.getCylLengthFactor() * distanceCopy.get(info.indicesForNormals[0][k] - 1 )) + sig);
            vecE.add(vecN);*/

            vecN.normalize();
            vecN.scale(sig * info.getCylLengthFactor());
            vecE.add(vecN);
            
            //scale radius to 5th of previous size
            info.c.addCylider(vecB, vecE, info.getCylRadius() * 0.2f, 10, sig);
        }
            info.c.prepareBuffer();
            info.c.prepareVBO();
    }
     
     private void recomputeFace(GL2 gl){
         
        Faces faces = info.getModel().getFaces();
        List<Vector3f> mesh = info.getModel().getVerts();
        
        for (int i = 0; i < faces.getNumFaces(); i++) {

            int[] facesInd = faces.getFaceVertIdxs(i);
            
            
        }
     }


    private void drawCylinder(GLUT glut, GL2 gl, Vector3f from, Vector3f to) {

        Vector3f zAxis = new Vector3f(0, 0, 1);
        Vector3f vector1 = new Vector3f(to.x - from.x, to.y - from.y, to.z - from.z);
        float length = vector1.length();
        vector1.normalize();
        float angle = zAxis.angle(vector1);
        Vector3f axis = new Vector3f();
        axis.cross(zAxis, vector1);
        float convert = (float) (180f / Math.PI);

        gl.glPushMatrix();
        gl.glTranslatef(from.x, from.y, from.z);
        gl.glRotatef(angle * convert, axis.x, axis.y, axis.z);
        glut.glutSolidCylinder(0.5f, length - 0.5f, 20, 5);
        //glut.glutSolidCylinder(0.5f, 10, 20, 5);
        gl.glPopMatrix();

    }

 
}
