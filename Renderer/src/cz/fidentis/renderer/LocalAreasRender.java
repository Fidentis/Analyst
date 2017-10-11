package cz.fidentis.renderer;

import com.hackoeur.jglm.Mat3;
import cz.fidentis.comparison.localAreas.Area;
import cz.fidentis.comparison.localAreas.LocalAreas;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import com.jogamp.common.nio.Buffers;
import cz.fidentis.comparison.localAreas.PointsValues;
import cz.fidentis.model.Model;
import java.util.List;
import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DYNAMIC_DRAW;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.GL_VERTEX_ARRAY_BINDING;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author zanri
 */
public class LocalAreasRender{
    private Boolean isSetUp;
    private LocalAreas localAreas;

    // our OpenGL resources
    private int vertexBuffer;
    private int vertexArray;
    private int joglArray;
    
    private int colorBuffer;
    
    // our GLSL resources
    private int positionAttribLoc;
    private int colorAttribLoc;
    private int normalAttribLoc;
    private int vertMvpUniformLoc;
    private int normMvpUniformLoc;
    private Mat4 pointTranfMatrix;
    private boolean isDrawPoint;
    private boolean isClearSelection;
    private boolean isDrawPoints;
    private boolean isDrawSelectedArea;
    private float[] pointToDraw;
    private float[] colorForArea;
    private float[] pointsToDraw;
    private float[] colorPointsToDraw;

    
    public LocalAreasRender(){
        this.isSetUp = false;
        this.localAreas = new LocalAreas();
        isDrawPoint = false;
        isClearSelection = false;
    }
    // <editor-fold desc="Getters">
    public Boolean IsSetUp(){
        return isSetUp;
    }
    
    public Mat4 getMatrix(){
        return this.pointTranfMatrix;
    }
    
    public LocalAreas getLocalAreasBoundary(){
        return localAreas;
    }
    // </editor-fold>
    
    // <editor-fold desc="Setters">
    public void SetUp(int[] areasIndexes, List<Area> areas, Model model){
        localAreas.SetAreas(areasIndexes, areas, model);
        this.isSetUp = true;
        this.isClearSelection = false;
    }
    
    public void setPointToDraw(Vector4f pointToDraw){
        this.pointToDraw = new float[] {pointToDraw.x, pointToDraw.y, pointToDraw.z};
        isDrawPoint = true;
    }
    
    public void setPointsToDraw(List<Vector4f> points) {
        isDrawPoints = true;
        pointsToDraw = new float[points.size()*6];
        colorPointsToDraw = new float[points.size()*4];
        int index = 0;
        int colorIndex = 0;
        for (Vector4f point: points){
            pointsToDraw[index] = point.x;
            index++;
            pointsToDraw[index] = point.y;
            index++;
            pointsToDraw[index] = point.z;
            index++;
            pointsToDraw[index] = 1f;
            index++;
            pointsToDraw[index] = 1f;
            index++;
            pointsToDraw[index] = 1f;
            index++;

            colorPointsToDraw[colorIndex] = 0.0f;
            colorIndex++;
            colorPointsToDraw[colorIndex] = 0.0f;
            colorIndex++;
            colorPointsToDraw[colorIndex] = 1.0f;
            colorIndex++;
            colorPointsToDraw[colorIndex] = 1.0f;
            colorIndex++;
            
        }
    }
    
    public void setLocalArea(PointsValues points, Area area) {
        
        localAreas.setColorForChosenArea(points, area);
        isDrawSelectedArea = true;
    }
    
    public void clearSelection(){
        isClearSelection = true;
    }
    
    public void hideLocalArea() {
        isDrawSelectedArea = false;
    }
    
    public void hideSelectedPoints(){
        this.isDrawPoints = false;
    }

    public void HideLocalAreas(){
        this.isSetUp = false;
        isDrawPoint = false;
    }
    // </editor-fold>

    public GL2 drawLocalAreas(GL2 gl, int vertexShaderID, double[] a, double[] b){
        
        if (isClearSelection){
            return gl;
        }

        float[] vertexesAreas = localAreas.getVertexAreas();
        float[] colorAreas = localAreas.getVertexAreasColors();
        
        // <editor-fold desc="Setting up matrixes">
        pointTranfMatrix = Mat4.MAT4_IDENTITY;

        float[] projectionArray = new float[16];
        for (int i = 0 ; i < 16; i++)
        {
            projectionArray [i] = (float) a[i];
        }
        Mat4 projection = new Mat4(projectionArray);
        float[] viewArray = new float[16];
        for (int i = 0 ; i < 16; i++)
        {
            viewArray[i] = (float) b[i];
        }
        Mat4 viewMat = new Mat4(viewArray);

        pointTranfMatrix = pointTranfMatrix.multiply(projection);
        pointTranfMatrix = pointTranfMatrix.multiply(viewMat);
        
        Mat3 n = invertMatrix(getMat3(pointTranfMatrix));
        n = n.transpose();
        // </editor-fold>

        // <editor-fold desc="Drawing Areas with triangles">
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable (GL_BLEND); 
        gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glLineWidth(2);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexesAreas.length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(vertexesAreas), GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, colorAreas.length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(colorAreas), GL_DYNAMIC_DRAW);

        gl.glUseProgram(vertexShaderID);

        gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
        gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

        gl.glBindVertexArray(vertexArray);

        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, vertexesAreas.length/6);

        gl.glBindVertexArray(joglArray);
        // </editor-fold>

        // <editor-fold desc="Drawing Areas points">
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        gl.glPointSize(3);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, localAreas.getVertexes().length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(localAreas.getVertexes()), GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, localAreas.getVertexColors().length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(localAreas.getVertexColors()), GL_DYNAMIC_DRAW);

        gl.glUseProgram(vertexShaderID);

        gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
        gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

        gl.glBindVertexArray(vertexArray);

        gl.glDrawArrays(GL2.GL_POINTS, 0, localAreas.getVertexes().length/6);

        gl.glBindVertexArray(joglArray);
        // </editor-fold>

        // <editor-fold desc="Drawing Areas boundaries">
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glLineWidth(1f);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, localAreas.getBorder().length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(localAreas.getBorder()), GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, localAreas.getBorderColor().length * Buffers.SIZEOF_FLOAT,
                Buffers.newDirectFloatBuffer(localAreas.getBorderColor()), GL_DYNAMIC_DRAW);

        gl.glUseProgram(vertexShaderID);

        gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
        gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

        gl.glBindVertexArray(vertexArray);


        gl.glDrawArrays(GL2.GL_LINES, 0, localAreas.getBorder().length/6);

        gl.glBindVertexArray(joglArray);
        // </editor-fold>

        //selected are
        if (this.isDrawSelectedArea){
            // <editor-fold desc="Draw selected area">
            gl.glClear(GL_DEPTH_BUFFER_BIT);
  
            gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER,localAreas.getSelectedAreaPoints().length * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(localAreas.getSelectedAreaPoints()), GL_DYNAMIC_DRAW);

            gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, localAreas.getSelectedAreaPointsColor().length * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(localAreas.getSelectedAreaPointsColor()), GL_DYNAMIC_DRAW);

            gl.glUseProgram(vertexShaderID);

            gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
            gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

            gl.glBindVertexArray(vertexArray);

            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, localAreas.getSelectedAreaPoints().length/6);

            gl.glBindVertexArray(joglArray);
            // </editor-fold>
        }

        if (isDrawPoints && pointsToDraw.length>0){
            // <editor-fold desc="Draw group of points from selected area">
            
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glPointSize(5);
            
            gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, pointsToDraw.length * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(pointsToDraw), GL_DYNAMIC_DRAW);

            gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, colorPointsToDraw.length * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(colorPointsToDraw), GL_DYNAMIC_DRAW);

            gl.glUseProgram(vertexShaderID);

            gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
            gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

            gl.glBindVertexArray(vertexArray);

            gl.glDrawArrays(GL2.GL_POINTS, 0, pointsToDraw.length/6);
            
            
            gl.glBindVertexArray(joglArray);
            // </editor-fold>
        }
        
        if (isDrawPoint){
            // <editor-fold desc="Draw single selected point from mouse hoovering">
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glPointSize(5);
            
            gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, 6 * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(pointToDraw), GL_DYNAMIC_DRAW);

            gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, 4 * Buffers.SIZEOF_FLOAT,
                    Buffers.newDirectFloatBuffer(new float[]  {1.0f, 0.0f, 0.0f, 1.0f}), GL_DYNAMIC_DRAW);

            gl.glUseProgram(vertexShaderID);

            gl.glUniformMatrix4fv(vertMvpUniformLoc, 1, false, pointTranfMatrix.getBuffer());
            gl.glUniformMatrix3fv(normMvpUniformLoc, 1, false, n.getBuffer());

            gl.glBindVertexArray(vertexArray);

            gl.glDrawArrays(GL_POINTS, 0, 1);
            
            gl.glBindVertexArray(joglArray);
            // </editor-fold>
        }
        
        return gl;
    }

    public GL2 init(GL2 gl, int vertexShaderID) {

        float[] vert = new float[3];
        float[] color = new float[3];

        
        // create buffers with geometry
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        vertexBuffer = buffers[0];
        colorBuffer = buffers[1];
        
        // create a vertex array object for the geometry
        int[] arrays = new int[1];
        gl.glGenVertexArrays(1, arrays, 0);
        vertexArray = arrays[0];

        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vert.length * Buffers.SIZEOF_FLOAT,
                null, GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, color.length * Buffers.SIZEOF_FLOAT,
                null, GL_DYNAMIC_DRAW);

        positionAttribLoc = gl.glGetAttribLocation(vertexShaderID, "position");
        colorAttribLoc = gl.glGetAttribLocation(vertexShaderID, "color");
        normalAttribLoc = gl.glGetAttribLocation(vertexShaderID, "normal");
        vertMvpUniformLoc = gl.glGetUniformLocation(vertexShaderID, "MVP");
        normMvpUniformLoc = gl.glGetUniformLocation(vertexShaderID, "N");
        
        // set the attributes of the geometry
        int binding[] = new int[1];
        gl.glGetIntegerv(GL_VERTEX_ARRAY_BINDING, binding, 0);
        joglArray = binding[0];
        
        gl.glBindVertexArray(vertexArray);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        gl.glEnableVertexAttribArray(positionAttribLoc);
        gl.glVertexAttribPointer(positionAttribLoc, 3, GL_FLOAT, false, 6 * Buffers.SIZEOF_FLOAT, 0);
         gl.glEnableVertexAttribArray(normalAttribLoc);
        gl.glVertexAttribPointer(normalAttribLoc, 3, GL_FLOAT, false, 6 * Buffers.SIZEOF_FLOAT, 3 * Buffers.SIZEOF_FLOAT);
        gl.glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        gl.glEnableVertexAttribArray(colorAttribLoc);
        gl.glVertexAttribPointer(colorAttribLoc, 4, GL_FLOAT, false, 0, 0);

        gl.glBindVertexArray(joglArray);
        return gl;
    }
    
    // <editor-fold desc="Private methods">
    private static Mat3 getMat3(Mat4 m) {
        Vec4 col0 = m.getColumn(0);
        Vec4 col1 = m.getColumn(1);
        Vec4 col2 = m.getColumn(2);
        Mat3 ma = new Mat3(
                col0.getX(), col0.getY(), col0.getZ(),
                col1.getX(), col1.getY(), col1.getZ(),
                col2.getX(), col2.getY(), col2.getZ());
        return ma;
    }
    
    private static Mat3 invertMatrix(Mat3 m) {
        Vec3 col0 = m.getColumn(0);
        Vec3 col1 = m.getColumn(1);
        Vec3 col2 = m.getColumn(2);

        Matrix3f mt = new Matrix3f(
                col0.getX(), col0.getY(), col0.getZ(),
                col1.getX(), col1.getY(), col1.getZ(),
                col2.getX(), col2.getY(), col2.getZ());

        mt.invert();

        Mat3 matrix = new Mat3(mt.m00, mt.m01, mt.m02, mt.m10, mt.m11, mt.m12, mt.m20, mt.m21, mt.m22);

        return matrix;
    }
    // </editor-fold>
    

}
