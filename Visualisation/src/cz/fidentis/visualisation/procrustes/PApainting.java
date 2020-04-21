/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.procrustes;

import Jama.Matrix;
import com.jogamp.opengl.util.gl2.GLUT;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * 
 */
public class PApainting {
    PApaintingInfo info;
    float[] color1 = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
    float[] color2 = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
    float[] color3 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};    
    float[] color4 = new float[]{1.0f, 1.0f, 1.0f, 0.4f};

    public PApainting(PApaintingInfo info) {
        this.info = info;
    }
    
    
    
    public void drawPointsAfterPA(GL2 gl, GLUT glut) {

//        float[] color1 = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
//        float[] color2 = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
//        float[] color3 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};

        if (info.getType() < 2) {
            ProcrustesAnalysis pa1;
            ProcrustesAnalysis pa2;
            
            //1:1 and 1:N
            if (info.getType() == 0) {
                /*pa1 = info.getGpa().getPA(0);
                pa2 = info.getGpa().getPA(1);*/
                pa1 = info.getPa();
                pa2 = info.getPa2();
            } else {
                pa1 = info.getPa();
                pa2 = info.getGpa().countMeanConfig();
            }

            Map<Integer, FacialPoint> pa1VisMatrix = pa1.getConfig();
            Map<Integer, FacialPoint> pa2VisMatrix = pa2.getConfig();
            List<Integer> correspondence = pa1.getFPtypeCorrespondence(pa2);
            
            Map<Integer, FacialPoint> pa1EnhancedMatrix = new HashMap<>();
            Map<Integer, FacialPoint> pa2EnhancedMatrix = new HashMap<>();

            for (Integer i: correspondence) {
                Vector3f vert1 = pa1VisMatrix.get(i).getPosition();
                Vector3f vert2 = pa2VisMatrix.get(i).getPosition();
                
                pa1EnhancedMatrix.put(i, pa1VisMatrix.get(i).deepCopyFp());
                pa2EnhancedMatrix.put(i, pa2VisMatrix.get(i).deepCopyFp());

                List<Vector3f> newVertices = this.enhanceVertices(vert1, vert2);
                
                pa1EnhancedMatrix.get(i).setPosition(newVertices.get(0));
                pa2EnhancedMatrix.get(i).setPosition(newVertices.get(1));

                Integer selectedPt = info.getIndexOfSelectedPoint();
                //Integer typeSelected = selectedPt >= 0 ? pa2.getFacialPoints().get(selectedPt).getType() : -1;
                double pointSize = i.equals(selectedPt) ? info.getPointSize() * 1.3 : info.getPointSize();
                
                //1toN
                if (info.getType() == 1) {
                    this.drawVertex(vert2, pointSize, color2, gl, glut, i.equals(selectedPt));
                    this.drawArrow(vert2, newVertices.get(0), color1, gl, glut);
                //1to1
                } else {
                    this.drawVertex(newVertices.get(1), pointSize, color2, gl, glut, i.equals(selectedPt));
                    this.drawVertex(newVertices.get(0), pointSize, color1, gl, glut, i.equals(selectedPt));
                }
            }

            if (info.getType() == 0) {
                this.drawFaceShape(correspondence, pa1EnhancedMatrix, color1, gl, glut);
                this.drawFaceShape(correspondence, pa2EnhancedMatrix, color2, gl, glut);
            }
            if (info.getType() == 1) {
                this.drawFaceShape(correspondence, pa2VisMatrix, color2, gl, glut); 
                
            }
        } else {
            //batch
            ProcrustesAnalysis meanAnalysis = info.getGpa().countMeanConfig();
            Map<Integer, FacialPoint> mean = meanAnalysis.getConfig();

            for (Integer j = 0; j < info.getGpa().getConfigs().size(); j++) {
                Map<Integer, FacialPoint> pa = info.getGpa().getPA(j).getConfig();
                List<Integer> correspondence = info.getGpa().getPA(j).getFPtypeCorrespondence(meanAnalysis);
                Map<Integer, FacialPoint> paEnhanced = new HashMap<>();

                for (Integer i : correspondence) {
                    Vector3f vert = pa.get(i).getPosition();
                    
                    paEnhanced.put(i, pa.get(i).deepCopyFp());

                    List<Vector3f> newVertices = this.enhanceVertices(vert, mean.get(i).getPosition());
                    paEnhanced.get(i).setPosition(newVertices.get(0));
                    
                    Integer selectedPt = info.getIndexOfSelectedPoint();
                    //Integer typeSelected = selectedPt >= 0 ? meanAnalysis.getFacialPoints().get(selectedPt).getType() : -1;
                    boolean isSelected = info.isHighlightSameTypePoints() ? i.equals(selectedPt) : j.equals(info.getIndexOfSelectedModel());
                    double pointSize = isSelected ? info.getPointSize() * 1.3 : info.getPointSize();
                    
                    if (info.getIndexOfSelectedConfig() == j) {
                        this.drawVertex(newVertices.get(0), pointSize / 2f, color1, gl, glut);
                    } else {
                        this.drawVertex(newVertices.get(0), pointSize / 2f, color3, gl, glut, isSelected);
                    }
                }
                if (info.isCustomConnections()) {
                    this.drawFaceShapeCustom(null, paEnhanced, color3, gl, glut, j);
                }
            }
            
            for (Integer i: mean.keySet()) {
                Integer selectedPt = info.getIndexOfSelectedPoint();
                //Integer typeSelected = selectedPt.compareTo(0) >= 0 ? meanAnalysis.getFacialPoints().get(selectedPt).getType() : -1;
                boolean isSelected = info.isHighlightSameTypePoints() ? i.equals(selectedPt) : info.getIndexOfSelectedModel() == -1;
                double pointSize = isSelected ? info.getPointSize() * 1.3 : info.getPointSize();
                Vector3f meanVert = mean.get(i).getPosition();
                this.drawVertex(meanVert, pointSize, color2, gl, glut, isSelected);
            }
            this.drawFaceShape(null, mean, color2, gl, glut, -1);
        }
    }
    
    public List<Vector3f> enhanceVertices(Vector3f vert1, Vector3f vert2) {
        List<Vector3f> list = new ArrayList<>();

        float x = (vert1.getX() - vert2.getX());
        float y = (vert1.getY() - vert2.getY());
        float z = (vert1.getZ() - vert2.getZ());

        list.add(new Vector3f(new Vector3f(vert1.getX() + x * info.getEnhance(),
                vert1.getY() + y * info.getEnhance(), vert1.getZ() + z * info.getEnhance())));
        list.add(new Vector3f(new Vector3f(vert2.getX() - x * info.getEnhance(),
                vert2.getY() - y * info.getEnhance(), vert2.getZ() - z * info.getEnhance())));

        return list;
    }
    
    private void drawArrow(Vector3f from, Vector3f to, float[] color, GL2 gl, GLUT glut) {
        Vector3f zAxis = new Vector3f(0, 0, 1);
        Vector3f vector = new Vector3f(to.x - from.x, to.y - from.y, to.z - from.z);
        float length = vector.length();
        vector.normalize();
        float angle = zAxis.angle(vector);
        Vector3f axis = new Vector3f();
        axis.cross(zAxis, vector);
        float convert = (float) (180f / Math.PI);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        gl.glPushMatrix();

        gl.glTranslatef(from.x, from.y, from.z);
        gl.glRotatef(angle * convert, axis.x, axis.y, axis.z);
        glut.glutSolidCylinder(info.getPointSize() / 3, length - info.getPointSize(), 20, 20);
        gl.glTranslatef(0, 0, length - info.getPointSize());
        glut.glutSolidCone(info.getPointSize(), info.getPointSize(), 20, 20);

        gl.glPopMatrix();

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
    }
    
    private Vector3f drawVertex(Vector3f vert, double size, float[] color, GL2 gl, GLUT glut) {
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glPushMatrix();
        gl.glTranslatef(vert.getX(), vert.getY(), vert.getZ());

        glut.glutSolidSphere(size, 20, 20);
        gl.glPopMatrix();

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();

        return vert;
    }
    
    private Vector3f drawVertex(Vector3f vert, double size, float[] color, GL2 gl, GLUT glut, Boolean selected) {
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glPushMatrix();
        gl.glTranslatef(vert.getX(), vert.getY(), vert.getZ());

        glut.glutSolidSphere(size, 20, 20);
        gl.glPopMatrix();
        
        if (selected) {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color4, 0);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color4, 0);
            gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
            
            gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_LIGHTING);
            
            gl.glPushMatrix();
            gl.glTranslatef(vert.getX(), vert.getY(), vert.getZ());

            glut.glutSolidSphere(size*1.2, 20, 20);
            gl.glPopMatrix();
            gl.glDisable(GL.GL_BLEND);
        }

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();

        return vert;
    }
    
    private void drawCylinder(Vector3f from, Vector3f to, GL2 gl, GLUT glut) {
        Vector3f zAxis = new Vector3f(0, 0, 1);
        Vector3f vector = new Vector3f(to.x - from.x, to.y - from.y, to.z - from.z);
        float length = vector.length();
        vector.normalize();
        float angle = zAxis.angle(vector);
        Vector3f axis = new Vector3f();
        axis.cross(zAxis, vector);
        float convert = (float) (180f / Math.PI);

        gl.glPushMatrix();

        gl.glTranslatef(from.x, from.y, from.z);
        gl.glRotatef(angle * convert, axis.x, axis.y, axis.z);
        glut.glutSolidCylinder(info.getPointSize() / 4.5, length /*- info.getPointSize()*/, 20, 20);
        gl.glPopMatrix();

    }
    
    
    private void drawFaceShapeCustom(List<Integer> correspondence, Map<Integer, FacialPoint> fps, float[] color, GL2 gl, GLUT glut, Integer configuration) {

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        //gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);

//        for (Map.Entry<Integer, Integer> pair : info.getCylinders().entrySet()) {
//            checkAndDrawCylinder(correspondence, fps, pair.getKey(), pair.getValue(), gl, glut);
//        }
        Set<PointConnection> connections = info.isCustomConnections() ? info.getPointConnections() : PointConnection.getDefaultPointConnections();
        
        for (PointConnection pc : connections) {
            if (pc.isSameConfig(configuration)) {
                checkAndDrawCylinder(correspondence, fps, pc.getStart(), pc.getEnd(), gl, glut);
            }
        }
        
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
    }
    
    private void drawFaceShape(List<Integer> correspondence, Map<Integer, FacialPoint> fps, float[] color, GL2 gl, GLUT glut) {
        drawFaceShape(correspondence, fps, color, gl, glut, -1);
    }

    private void drawFaceShape(List<Integer> correspondence, Map<Integer, FacialPoint> fps, float[] color, GL2 gl, GLUT glut, Integer configuration) {

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        //gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);
        
        drawFaceShapeCustom(correspondence, fps, color, gl, glut, configuration);

//        if (info.isCustomConnections()) {
//            drawFaceShapeCustom(correspondence, fps, color, gl, glut, configuration);
//            return;
//        }
//        
//        //celo nos
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.PRN.ordinal(), FacialPointType.N.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.N.ordinal(), FacialPointType.G.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.PRN.ordinal(), FacialPointType.LS.ordinal(), gl, glut);
// 
//        //brada
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.LI.ordinal(), FacialPointType.SL.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.SL.ordinal(), FacialPointType.PG.ordinal(), gl, glut);
//
//        //usta
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.CH_R.ordinal(), FacialPointType.STO.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.STO.ordinal(), FacialPointType.CH_L.ordinal(), gl, glut);
//
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.CH_R.ordinal(), FacialPointType.LS.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.LS.ordinal(), FacialPointType.CH_L.ordinal(), gl, glut);
//
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.CH_R.ordinal(), FacialPointType.LI.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.LI.ordinal(), FacialPointType.CH_L.ordinal(), gl, glut);
//
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.LS.ordinal(), FacialPointType.STO.ordinal(), gl, glut);
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.STO.ordinal(), FacialPointType.LI.ordinal(), gl, glut);
//
//        
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.EX_L.ordinal(), FacialPointType.EN_L.ordinal(), gl, glut);
// 
//        checkAndDrawCylinder(correspondence, fps, FacialPointType.EX_R.ordinal(), FacialPointType.EN_R.ordinal(), gl, glut);


        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
    }

    //check if both start and end point for cylinder are available and if so, draw cylinder
    private void checkAndDrawCylinder(List<Integer> correspondence, Map<Integer, FacialPoint> fps, int cylStart, int cylEnd, GL2 gl, GLUT glut) {
        
        
        if((correspondence != null && correspondence.contains(cylStart) && correspondence.contains(cylEnd)) ||
                (correspondence == null && fps.containsKey(cylStart) && fps.containsKey(cylEnd))){
            
            drawCylinder(fps.get(cylStart).getPosition(), fps.get(cylEnd).getPosition(), gl, glut);
        }
            
    }

    public void setColor1(float[] color1) {
        this.color1 = color1;
    }

    public void setColor2(float[] color2) {
        this.color2 = color2;
    }
    
    public void setColor3(float[] color3) {
        this.color3 = color3;
    }
    
}
