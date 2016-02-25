/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.procrustes;

import Jama.Matrix;
import com.jogamp.opengl.util.gl2.GLUT;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * 
 */
public class PApainting {
    PApaintingInfo info;

    public PApainting(PApaintingInfo info) {
        this.info = info;
    }
    
    
    
    public void drawPointsAfterPA(GL2 gl, GLUT glut) {

        float[] color1 = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
        float[] color2 = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
        float[] color3 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};

        if (info.getType() < 2) {
            ProcrustesAnalysis pa1;
            ProcrustesAnalysis pa2;

            if (info.getType() == 0) {
                /*pa1 = info.getGpa().getPA(0);
                pa2 = info.getGpa().getPA(1);*/
                pa1 = info.getPa();
                pa2 = info.getPa2();
            } else {
                pa1 = info.getPa();
                pa2 = info.getGpa().countMeanConfig();
            }

            List<Vector3f> newFp1 = new ArrayList();
            List<Vector3f> newFp2 = new ArrayList();
            
            Matrix pa1VisMatrix = pa1.getVisualMatrix();
            Matrix pa2VisMatrix = pa2.getVisualMatrix();

            for (int i = 0; i < pa1VisMatrix.getRowDimension(); i++) {
                Vector3f vert1 = new Vector3f((float) pa1VisMatrix.get(i, 0),
                        (float) pa1VisMatrix.get(i, 1), (float) pa1VisMatrix.get(i, 2));
                Vector3f vert2 = new Vector3f((float) pa2VisMatrix.get(i, 0),
                        (float) pa2VisMatrix.get(i, 1), (float) pa2VisMatrix.get(i, 2));

                List<Vector3f> newVertices = this.enhanceVertices(vert1, vert2);

                if (info.getType() == 1) {
                    this.drawVertex(vert2, info.getPointSize(), color2, gl, glut);
                    this.drawArrow(vert2, newVertices.get(0), color1, gl, glut);
                    newFp1.add(vert2);
                } else {
                    newFp1.add(newVertices.get(0));
                    newFp2.add(newVertices.get(1));
                    this.drawVertex(newVertices.get(1), info.getPointSize(), color2, gl, glut);
                    this.drawVertex(newVertices.get(0), info.getPointSize(), color1, gl, glut);
                }
            }

            if (info.getType() == 0) {
                this.drawFaceShape(newFp1, color1, gl, glut);
                this.drawFaceShape(newFp2, color2, gl, glut);
            }
            if (info.getType() == 1) {
                this.drawFaceShape(newFp1, color2, gl, glut);

            }
        } else {
            Matrix mean = info.getGpa().countMeanConfig().getVisualMatrix();

            for (int j = 0; j < info.getGpa().getConfigs().size(); j++) {
                Matrix pa = info.getGpa().getPA(j).getVisualMatrix();

                for (int i = 0; i < mean.getRowDimension(); i++) {
                    Vector3f vert = new Vector3f((float) pa.get(i, 0),
                            (float) pa.get(i, 1), (float) pa.get(i, 2));

                    List<Vector3f> newVertices = this.enhanceVertices(vert, new Vector3f((float) mean.get(i, 0),
                            (float) mean.get(i, 1), (float) mean.get(i, 2)));
                    if (info.getIndexOfSelectedConfig() == j) {
                        this.drawVertex(newVertices.get(0), info.getPointSize() / 2f, color1, gl, glut);
                    } else {
                        this.drawVertex(newVertices.get(0), info.getPointSize() / 2f, color3, gl, glut);
                    }
                }
            }
            List<Vector3f> meanVertices = new ArrayList();
            for (int i = 0; i < mean.getRowDimension(); i++) {
                Vector3f meanVert = new Vector3f((float) mean.get(i, 0),
                        (float) mean.get(i, 1), (float) mean.get(i, 2));
                this.drawVertex(meanVert, info.getPointSize(), color2, gl, glut);
                meanVertices.add(meanVert);
            }
            this.drawFaceShape(meanVertices, color2, gl, glut);
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
        glut.glutSolidCylinder(info.getPointSize() / 4.5, length - info.getPointSize(), 20, 20);
        gl.glPopMatrix();

    }
    
    private void drawFaceShape(List<Vector3f> fps, float[] color, GL2 gl, GLUT glut) {

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

        //gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);

        
        //celo nos
        drawCylinder(fps.get(4), fps.get(8), gl, glut);
        drawCylinder(fps.get(8), fps.get(9), gl, glut);
        drawCylinder(fps.get(4), fps.get(10), gl, glut);

        //brada
        drawCylinder(fps.get(11), fps.get(12), gl, glut);
        drawCylinder(fps.get(12), fps.get(13), gl, glut);

        //usta
        drawCylinder(fps.get(6), fps.get(5), gl, glut);
        drawCylinder(fps.get(5), fps.get(7), gl, glut);

        drawCylinder(fps.get(6), fps.get(10), gl, glut);
        drawCylinder(fps.get(10), fps.get(7), gl, glut);

        drawCylinder(fps.get(6), fps.get(11), gl, glut);
        drawCylinder(fps.get(11), fps.get(7), gl, glut);

        drawCylinder(fps.get(10), fps.get(5), gl, glut);
        drawCylinder(fps.get(5), fps.get(11), gl, glut);
        //oci -- zanedbava nespecifikovane body v strede oka
        /*drawCylinder(fps.get(1), fps.get(14), gl, glut);
        drawCylinder(fps.get(14), fps.get(3), gl, glut);*/
        
        drawCylinder(fps.get(1), fps.get(3), gl, glut);
        //drawCylinder(fps.get(3), fps.get(8), gl, glut);

        /*drawCylinder(fps.get(0), fps.get(15), gl, glut);
        drawCylinder(fps.get(15), fps.get(2), gl, glut);*/
        drawCylinder(fps.get(0), fps.get(2), gl, glut);
        //drawCylinder(fps.get(2), fps.get(8), gl, glut);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
    }
}
