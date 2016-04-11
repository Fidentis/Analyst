/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation.surfaceComparison;

import cz.fidentis.utils.MathUtils;
import java.nio.FloatBuffer;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import javax.media.opengl.GL2;

/**
 *
 * @author Kubo Palenik
 */
public class Cylinder {

    ArrayList<Vector3f> trianglesF = new ArrayList<>();
    ArrayList<Vector3f> trianglesB = new ArrayList<>();
    
    private MathUtils mu = MathUtils.instance();
    private Vector3f d;
    
    private FloatBuffer verticesF;
    private FloatBuffer verticesB;
    private int[] vbo = new int[2];
    private int numVertsF;
    private int numVertsB;
    GL2 gl;

    public Cylinder() {

    }

    public void assignGl(GL2 gl) {
        this.gl = gl;
    }

    public Cylinder(GL2 gl) {
        this.gl = gl;
    }

    public void clear() {
        trianglesF.clear();
        trianglesB.clear();
        verticesF = null;
        verticesB = null;
        gl.glDeleteBuffers(2, vbo, 0);
        vbo[0] = 0;
        vbo[1] = 0;
    }

    public void prepareBuffer() {
        numVertsF = trianglesF.size();
        numVertsB = trianglesB.size();
        
        verticesF = FloatBuffer.allocate(numVertsF * 3);
        verticesB = FloatBuffer.allocate(numVertsB * 3);
        
        for (Vector3f v : trianglesF) {
            verticesF.put(v.x).put(v.y).put(v.z);
        }
        
        for (Vector3f v : trianglesB) {
            verticesB.put(v.x).put(v.y).put(v.z);
        }
        verticesF.rewind();
        verticesB.rewind();
    }

    public void prepareVBO() {
 
        gl.glGenBuffers(2, vbo, 0);
        if(numVertsF > 0){
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, numVertsF * 3 * Float.SIZE, verticesF, gl.GL_STATIC_DRAW);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, 0);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        }
        if(numVertsB >0) {
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[1]);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, numVertsB * 3 * Float.SIZE, verticesB, gl.GL_STATIC_DRAW);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, 0);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        }
    }

    public void draw() {
        gl.glDisable(gl.GL_CULL_FACE);
        float[] color = new float[4];
        color[0] = 0.5f; color[1] = 0.0f; color[2] = 0.0f; color[3] = 1.0f;
        float colorDiff[] = new float[4];
        colorDiff[0] = 1f; colorDiff[1] = 1f; colorDiff[2] = 1f; colorDiff[3] = 1f;
        if(numVertsF >0) {
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, colorDiff, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
        
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, numVertsF);
        }
        color[0] = 0.0f; color[1] = 0.0f; color[2] = 0.5f; color[3] = 1.0f;
        
        if(numVertsB >0) {
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, colorDiff, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
        
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[1]);
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, numVertsB);
        }
        //gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        gl.glEnable(gl.GL_CULL_FACE);
    }

    /**
     * adds cylinder to vbo array
     */
    public void addCylider(Vector3f from, Vector3f to, float radius, int segments, float orientation) {
        ArrayList<Vector3f> fromCircPointsBase = compFromCircPointsBase(from, to, radius, segments);

        Vector3f dir = mu.createVector(from, to);
        Vector3f dirn = new Vector3f(dir);
        d = dirn; dirn.normalize();
        
        float angleY = angleFrom010(dirn);
        float angleX = angleFrom100(dirn);

        ArrayList<Vector3f> fromCircPointsTran = transformList(fromCircPointsBase, angleX, angleY, from);
        ArrayList<Vector3f> toCircPointsTran = translate(fromCircPointsTran, dir);
        
        if(orientation > 0){
            addGeometryToBuffer(trianglesF, from, to, fromCircPointsTran, toCircPointsTran, orientation);
        }else{
            addGeometryToBuffer(trianglesF, from, to, fromCircPointsTran, toCircPointsTran, orientation);
        }
    }

    private void addGeometryToBuffer(ArrayList<Vector3f> storage, Vector3f fromCent, Vector3f toCent, ArrayList<Vector3f> fromCirc, ArrayList<Vector3f> toCirc, float orientation) {
 
        for (int i = 0; i < fromCirc.size(); i++) {
            
            storage.add(fromCent);
            storage.add(fromCirc.get(i));
            storage.add(fromCirc.get((i + 1) % fromCirc.size()));

            storage.add(toCent);
            storage.add(toCirc.get(i));
            storage.add(toCirc.get((i + 1) % toCirc.size()));

            storage.add(fromCirc.get(i));
            storage.add(fromCirc.get((i + 1) % fromCirc.size()));
            storage.add(toCirc.get(i));

            storage.add(toCirc.get(i));
            storage.add(toCirc.get((i + 1) % toCirc.size()));
            storage.add(fromCirc.get((i + 1) % fromCirc.size()));
 
        }
    }

    private ArrayList<Vector3f> compFromCircPointsBase(Vector3f from, Vector3f to, float radius, int segments) {
        ArrayList<Vector3f> fromCircPoints = new ArrayList<>();

        fromCircPoints.add(new Vector3f(radius, 0f, 0f));

        float angle = (float) ( 2*Math.PI / segments);

        for (int i = 0; i < segments - 1; i++) {
            fromCircPoints.add(rotateY(getLast(fromCircPoints), angle));
        }

        return fromCircPoints;
    }

    private <T> T getLast(ArrayList<T> list) {
        return list.get(list.size() - 1);
    }

    private Vector3f rotateY(Vector3f v, float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        float x = c * v.x + -s * v.z;
        float z = s * v.x + c * v.z;

        return new Vector3f(x, v.y, z);

    }

    private Vector3f rotateZ(Vector3f v, float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        float x = c * v.x + -s * v.y;
        float y = s * v.x + c * v.y;

        return new Vector3f(x, y, v.z);

    }

    private float angleFrom100(Vector3f vec) {
        return (float) Math.acos((vec.x/(Math.sqrt((double) (vec.x*vec.x + vec.z*vec.z)))));
    }

    private float angleFrom010(Vector3f vec) {
        return (float) Math.acos(vec.y);
    }

    private Vector3f transform(Vector3f vec, float angleX, float angleY, Vector3f dir) {
        Vector3f temp = rotateZ(vec, -angleY);
        temp = rotateY(temp, Math.signum(d.z)*angleX);
        temp.add(dir);
        return temp;
    }

    private ArrayList<Vector3f> transformList(ArrayList<Vector3f> vecL, float angleX, float angleY, Vector3f dir) {
        ArrayList<Vector3f> ret = new ArrayList<>();
        for (Vector3f vec : vecL) {
            ret.add(transform(vec, angleX, angleY, dir));
        }
        return ret;
    }
    
    private ArrayList<Vector3f> translate(ArrayList<Vector3f> vecL, Vector3f dir) {
        ArrayList<Vector3f> ret = new ArrayList<>();
        for (Vector3f vec : vecL) {
            Vector3f temp = new Vector3f(vec);
            temp.add(dir);
            ret.add(temp);
        }
        return ret;
    }
}
