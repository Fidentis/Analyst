
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.renderer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.procrustes.ProcrustesAnalysis;
import cz.fidentis.composite.ModelSelector;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FeaturePointsUniverse;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.model.Graph2;
import cz.fidentis.model.Model;
import cz.fidentis.model.VertexInfo;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import cz.fidentis.utils.IntersectionUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.visualisation.ComparisonListenerInfo;
import cz.fidentis.visualisation.procrustes.PApainting;
import cz.fidentis.visualisation.procrustes.PApaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.SelectionType;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
import static cz.fidentis.visualisation.surfaceComparison.VisualizationType.COLORMAP;
import static cz.fidentis.visualisation.surfaceComparison.VisualizationType.TRANSPARENCY;
import static cz.fidentis.visualisation.surfaceComparison.VisualizationType.VECTORS;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Float.NaN;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_ALWAYS;
import static javax.media.opengl.GL.GL_BACK;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_CULL_FACE;
import static javax.media.opengl.GL.GL_DEPTH_ATTACHMENT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_FALSE;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_COMPLETE;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_LINE_LOOP;
import static javax.media.opengl.GL.GL_LINE_STRIP;
import static javax.media.opengl.GL.GL_MIRRORED_REPEAT;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_NONE;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_STATIC_DRAW;
import static javax.media.opengl.GL.GL_TEXTURE;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE7;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL.GL_UNSIGNED_INT;
import static javax.media.opengl.GL.GL_WRITE_ONLY;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.GL_ALL_ATTRIB_BITS;
import static javax.media.opengl.GL2.GL_POLYGON;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_DEPTH_COMPONENT;
import static javax.media.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static javax.media.opengl.GL2ES2.GL_LINK_STATUS;
import static javax.media.opengl.GL2ES2.GL_VERTEX_SHADER;
import javax.media.opengl.GL2GL3;
import static javax.media.opengl.GL2GL3.GL_ALL_BARRIER_BITS;
import static javax.media.opengl.GL2GL3.GL_ATOMIC_COUNTER_BUFFER;
import static javax.media.opengl.GL2GL3.GL_DYNAMIC_COPY;
import static javax.media.opengl.GL2GL3.GL_MAP_READ_BIT;
import static javax.media.opengl.GL2GL3.GL_MAP_WRITE_BIT;
import static javax.media.opengl.GL2GL3.GL_PIXEL_UNPACK_BUFFER;
import static javax.media.opengl.GL2GL3.GL_R32UI;
import static javax.media.opengl.GL2GL3.GL_READ_WRITE;
import static javax.media.opengl.GL2GL3.GL_RED_INTEGER;
import static javax.media.opengl.GL2GL3.GL_RGBA32UI;
import static javax.media.opengl.GL2GL3.GL_TEXTURE_BUFFER;
import javax.media.opengl.GLAutoDrawable;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW_MATRIX;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION_MATRIX;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import jv.object.PsDebug;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Katka
 */
public class ComparisonGLEventListener extends GeneralGLEventListener {

    /*private HDpainting hdPaint;
    private HDpaintingInfo hdInfo;
    private boolean paintHD = false;*/
    private ComparisonListenerInfo info;

    private GLUT glut = new GLUT();
    private int[] viewport = new int[4];
    private double[] modelViewMatrix = new double[16];
    private double[] projectionMatrix = new double[16];
    /*private FeaturePointsUniverse fpUniverse;
    private List<FacialPoint> facialPoints = new ArrayList<FacialPoint>();
    private List<ICPTransformation> transformations;
    private ArrayList<Model> models = new ArrayList<>();*/
    private boolean showPlane = true;

    /* private int indexOfSelectedPoint = -1;
    private float facialPointRadius = 2;
    private float[] colorOfPoint = new float[]{1f, 0f, 0f, 1.0f};
    //
    private PApainting paPainting;
    private PApaintingInfo paInfo;

    private boolean procrustes = false;*/
    private Vector3f[] selectionCube = new Vector3f[5];
    private Point selectionStart = null;
    private Point selectionEnd = null;

    private float vetctorScale = 1;
    private int shadowMapShadersId;
    private int listCreationShadersId;
    private int OITShadersId;
    private int FinalShadersId;
    private int ColorMapShadersId;
    private int ColorMapReductionShadersId;

    /*private LinkedList<Vector3f> plane = new LinkedList<>();
    private ArrayList<LinkedList<LinkedList<Vector3f>>> lists = new ArrayList<>();
    private ArrayList<LinkedList<LinkedList<Vector2f>>> lists2 = new ArrayList<>();
    private ArrayList<Vector2f> samplePoints = new ArrayList<>();
    private ArrayList<Vector2f> sampleNormals = new ArrayList<>();
    private ArrayList<ArrayList<Vector2f>> distancePoints = new ArrayList<>();
    private ArrayList<Vector2f> averagedistancePoints = new ArrayList<>();*/
    private static final int MAX_FRAMEBUFFER_WIDTH = 2048;
    private static final int MAX_FRAMEBUFFER_HEIGHT = 2048;

    final int[] depthTexture = new int[1];
    final int[] depthFBO = new int[1];
    final int[] hpTexture = new int[1];
    final int[] hpInitializer = new int[1];
    final int[] acBuffer = new int[1];
    final int[] fsBuffer = new int[1];
    final int[] fsTexture = new int[1];
    final int[] quad_vao = new int[1];
    final int[] quad_vbo = new int[1];
    final Buffer zero;

    private int modelSelectionIndex = -1;
    private int arrowSelectionIndex = -1;
    private int modelNumUniform;
    private int innSurfSolidUniform;
    private int minZUniform;
    private int maxZUniform;
    private int fogUniform;
    private int fogColorUniform;
    private int fogVersion;
    private int colorSchemeUniform;
    private boolean secondaryListener = false;
    private int minColorUniform;
    private int maxColorUniform;
    private int minDistanceUniform;
    private int globalMaxDistanceUniform;
    private int globalMinDistanceUniform;
    private int maxDistanceUniform;
    private int curDistanceAttrib;
    private int selectionUniform;
    private int selectionCameraUbniform;
    private int selectionTypeUniform;
    private int startUniform;
    private int endUniform;

    private int shadowVertexListUniform;
    private int modelVertexListUniform;
    private int shadowNormalListUniform;
    private int modelNormalListUniform;
    private int shadowPrincipalListUniform;
    private int modelPrincipalListUniform;
    private int shadowSecondaryListUniform;
    private int modelSecondaryListUniform;

    /*private float[] primaryColor = {51f / 255f, 153f / 255f, 1f, 1.0f};
    private float[] secondaryColor = {1f, 1f, 0f, 1.0f};
    private float[] fogColor = {0f, 0f, 255f, 1.0f};*/
    private int innerSurfaceSolid = 1;
    private boolean useGlyphs = false;
    Texture cross;

    float[] modelWiew = new float[16];
    float[] projection = new float[16];

    private int currentHeight, currentWidth;
    private final int shadowRatio = 2;

    private ArrayList<Vector3f> boundingBox;
    private Model gizmo;
    private Model originalGizmo;
    private Vector3f gizmoIntersection;
    private boolean highlightCuts = true;

    /*private ArrayList<ArrayList<Float>> distances = new ArrayList<>();
    private ArrayList<ArrayList<Vector2f>> textureCoordinates = new ArrayList<>();
    private ArrayList<Set<Integer>> usedFaces = new ArrayList<>();
    private ArrayList<float[]> sampleVetices = new ArrayList<>();
    private ArrayList<ArrayList<VertexInfo>> verticesInfo = new ArrayList<>();*/
    //private boolean distanceface = true;
    /*private boolean contours = true;
    private boolean showAllCuts;
    private boolean showSamplingRays;
    private boolean showVectors = true;
    private boolean render;
    private Vector3f planePoint = new Vector3f(0, 0, 0);
    private Vector3f planeNormal = new Vector3f(1, 0, 0);*/
    //  private boolean primaryModelSet;
    public ComparisonGLEventListener() {
        info = new ComparisonListenerInfo();
        int[] z = {0};
        this.zero = Buffers.newDirectIntBuffer(z);
        /*models.add(null);
        models.add(null);*/
        info.getModels().add(null);
        info.getModels().add(null);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        this.gl = (GL2) glad.getGL();
        glu = new GLU();
        gl.setSwapInterval(1);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        float[] lightpos = {0, 0, 1000, 0};
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightpos, 0);
        initShaders();
        initBuffers();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 1);     // background for GLCanvas
        gl.glShadeModel(GL2.GL_SMOOTH);    // use smooth shading

        gl.glDepthFunc(GL2.GL_LESS);
        gl.glDepthRange(0.0, 1.0);

        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL.GL_BLEND);

    }

    @Override
    public void display(GLAutoDrawable glad) {
        gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 1);     // background for GLCanvas

        gl.glUseProgram(0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glDepthMask(true);
        gl.glDepthFunc(GL_LESS);
        glu.gluLookAt(xCameraPosition, yCameraPosition, zCameraPosition, xCenter, yCenter, zCenter, xUpPosition, yUpPosition, zUpPosition);

        gl.glPushMatrix();

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        gl.glShadeModel(GL2.GL_SMOOTH);

        if (info.isProcrustes()) {
            gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
            info.getPaPainting().drawPointsAfterPA(gl, glut);
            gl.glPopAttrib();
        } else if (info.isPaintHD()) {
            /**
             * edited - 11.03.2015 Jakub Palenik multiple visualizations of
             * HDpaint based on hdPaint attribute vType of ENUM
             * VisualizationType
             */
            switch (info.getHdInfo().getvType()) {
                case COLORMAP:
                    paintHD();
                    if (selectionCube[3] != null && !info.getHdInfo().isIsSelection()) {
                        if (info.getHdInfo().getsType() == SelectionType.ELLIPSE) {
                            paintSelectionEllipse();
                        }
                        if (info.getHdInfo().getsType() == SelectionType.RECTANGLE) {
                            paintSelectionRectangle();
                        }

                    }
                    break;
                case TRANSPARENCY:
                    trasparencyRender();
                    break;
                case VECTORS:
                    info.getHdPaint().paintNormals(gl);
                    break;
                case CROSSSECTION:
                    if (secondaryListener) {
                        slicesAllRender();
                    } else {
                        sliceMainRender();
                    }
                    break;
                default:
                    break;
            }
        } else if (info.getModels().size() == 2) {
            trasparencyRender();
        } else {
            for (int i = 0; i < info.getModels().size(); i++) {
                if (info.getModels().get(i) != null) {
                    gl.glPushMatrix();
                    float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
                    float[] colorKs = {0, 0, 0, 1f};

                    //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
                    gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);

                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
                    if (drawTextures) {
                        info.getModels().get(i).draw(gl);
                    } else {

                        info.getModels().get(i).drawWithoutTextures(gl);

                    }
                    if (info.getFacialPoints() != null) {
                        drawFacialPoints(info.getFacialPoints());
                    }
                    gl.glDisable(GL.GL_BLEND);
                    gl.glPopMatrix();
                }
            }

        }

        gl.glPopMatrix();
        gl.glFlush();

    }

    public void setShowPlane(boolean showPlane) {
        this.showPlane = showPlane;
    }

    private void paintHD() {
        // gl.glDisable(GL.GL_BLEND);
        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
        unsetTextureMatrix();
        gl.glDepthMask(true);
        gl.glDepthFunc(GL_ALWAYS);
        gl.glEnable(GL_LIGHTING);
        gl.glViewport(0, 0, currentWidth, currentHeight);
        gl.glColorMask(true, true, true, true);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(ColorMapShadersId);

        //reset atomic counter
        gl.glBindBufferBase(GL_ATOMIC_COUNTER_BUFFER, 0, acBuffer[0]);
        gl.glBufferSubData(GL_ATOMIC_COUNTER_BUFFER, 0, zero.capacity() * 4, zero);

        //reset headPointer
        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, hpInitializer[0]);
        gl.glBindTexture(GL_TEXTURE_2D, hpTexture[0]);
        gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, MAX_FRAMEBUFFER_WIDTH, MAX_FRAMEBUFFER_HEIGHT, GL_RED_INTEGER, GL_UNSIGNED_INT, 0);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        setupMatrices((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition, (float) xCenter, (float) yCenter, (float) zCenter, (float) xUpPosition, (float) yUpPosition, (float) zUpPosition);
        gl.glDisable(GL_CULL_FACE);

        gl.glBindImageTexture(1, hpTexture[0], 0, false, 0, GL_READ_WRITE, GL_R32UI);
        gl.glBindImageTexture(2, fsTexture[0], 0, false, 0, GL_WRITE_ONLY, GL_RGBA32UI);

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glEnable(GL_TEXTURE_2D);
        renderModels(false, true);

        gl.glMemoryBarrier(GL_ALL_BARRIER_BITS);

        if (!info.getHdInfo().isRecomputed() && selectionStart != null && selectionEnd != null && info.getHdInfo().isIsSelection()) {
            gl.glBindBufferBase(GL_ATOMIC_COUNTER_BUFFER, 0, acBuffer[0]);
            ByteBuffer bf = gl.glMapBufferRange(GL_ATOMIC_COUNTER_BUFFER, 0, zero.capacity() * 4, GL_MAP_READ_BIT);
            int count = bf.getInt();
            gl.glUnmapBuffer(GL_ATOMIC_COUNTER_BUFFER);
            FloatBuffer data = Buffers.newDirectFloatBuffer(count);

            gl.glBindBuffer(GL_TEXTURE_BUFFER, fsBuffer[0]);
            gl.glGetBufferSubData(GL_TEXTURE_BUFFER, 0, count * 4, data);
            float[] d = new float[count];
            float minimum = Float.POSITIVE_INFINITY;
            float maximum = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < count; i++) {
                if (i % 4 == 1 && i > 3) {
                    if (data.get(i) < minimum) {
                        minimum = data.get(i);
                    }
                    if (data.get(i) > maximum && data.get(i) < Float.POSITIVE_INFINITY) {
                        maximum = data.get(i);
                    }
                }
                d[i] = data.get(i);
            }
            info.getHdInfo().setMinSelection(minimum);
            info.getHdInfo().setMaxSelection(maximum);
            gl.glBindBuffer(GL_TEXTURE_BUFFER, 0);
            info.getHdInfo().setIsRecomputed(true);
        }

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(ColorMapReductionShadersId);
        if (selectionStart != null && selectionEnd != null && info.getHdInfo().isIsSelection()) {
            gl.glUniform1f(minDistanceUniform, info.getHdInfo().getMinSelection());
            gl.glUniform1f(maxDistanceUniform, info.getHdInfo().getMaxSelection());
        } else {
            gl.glUniform1f(minDistanceUniform, info.getHdInfo().getMinDistance());
            gl.glUniform1f(maxDistanceUniform, info.getHdInfo().getMaxDistance());
        }
        gl.glUniform1i(colorSchemeUniform, info.getHdInfo().getColorScheme().ordinal());
        gl.glUniform1f(globalMaxDistanceUniform, info.getHdInfo().getMaxDistance());
        gl.glUniform1f(globalMinDistanceUniform, info.getHdInfo().getMinDistance());
        gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        gl.glUseProgram(0);
        gl.glPopAttrib();

    }

    private void sliceMainRender() {
        paintHD();
        gl.glDepthFunc(GL_LESS);

        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
        float[] color = {0.8667f, 0.7176f, 0.6275f, 1f};
        setMaterialColor(color);

        //models.get(0).drawWithoutTextures(gl);
        gl.glColor3f(0, 0, 0);
        drawBoundingBox();

        float[] color2 = {1, 0, 0, 0.5f};
        setMaterialColor(color2);

        if (!info.getModels().isEmpty() && showPlane) {
            gl.glEnable(GL.GL_BLEND); // Enable the OpenGL Blending functionality  
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glBegin(GL_POLYGON);
            for (Vector3f p : info.getPlane()) {
                gl.glVertex3f(p.x, p.y, p.z);
            }
            gl.glEnd();

            gl.glDisable(GL.GL_BLEND);

            gl.glBegin(GL_LINE_LOOP);
            for (Vector3f p : info.getPlane()) {
                gl.glVertex3f(p.x, p.y, p.z);
            }
            gl.glEnd();
        }

        if (highlightCuts && !info.getLists().isEmpty()) {

            for (LinkedList<Vector3f> l : info.getLists().get(0)) {
                for (int i = 0; i < l.size() - 1; i++) {
                    drawCylinder(l.get(i), l.get(i + 1), 1);
                }
            }

        }
        if (gizmo != null) {
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gizmo.drawWithoutTextures(gl);
        }
        gl.glPopAttrib();

    }

    public void setSecondaryListener(boolean s) {
        this.secondaryListener = s;
    }    
    
    public boolean isSecondaryListener() {
        return secondaryListener;
    }

    private void slicesAllRender() {
        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
        gl.glDisable(GL_LIGHTING);

        gl.glLineWidth(info.getCutThickness());

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        if (info.isShowSamplingRays()) {
            gl.glColor4fv(info.getColorOfCut(), 0);
            gl.glLineWidth(info.getCutThickness());
            gl.glBegin(GL_LINES);
            for (int i = 1; i < info.getSampleNormals().size(); i++) {
                 if (info.getPointDistances()!= null && info.getPointDistances().get(i).size() > 1) {
                    Vector2f n = new Vector2f(info.getSampleNormals().get(i));
                    n.normalize();
                    Vector2f a = new Vector2f(n);               
                    a.scale(info.getPointDistances().get(i).get(0));
                    a.add(info.getSamplePoints().get(i));
                    Vector2f b = new Vector2f(n);
                    b.scale(info.getPointDistances().get(i).get(info.getPointDistances().get(i).size() - 1));
                    b.add(info.getSamplePoints().get(i));                    
                    gl.glVertex2d(a.x, a.y);
                    gl.glVertex2d(b.x, b.y);                    
                }
            }
            gl.glEnd();
        }

        if (info.isShowBoxplot() || info.isShowBoxplotFunction()) {
            gl.glColor4fv(info.getColorOfCut(), 0);
            //intersections/normals
            //  for (ArrayList<Vector2f> l : info.getDistancePoints()) {
            ArrayList<Vector2f> midline = new ArrayList<>();
            ArrayList<Vector2f> left = new ArrayList<>();
            ArrayList<Vector2f> right = new ArrayList<>();
            for (int i = 1; i < info.getSamplePoints().size(); i++) {
                if (info.getSamplePoints().get(i) != null) {
                    gl.glLineWidth(info.getCutThickness());
                    Vector2f n = new Vector2f(info.getSampleNormals().get(i));
                    n.normalize();
                    Vector2f a = new Vector2f(n);
                    Vector2f c = new Vector2f(n);
                    Vector2f n2 = new Vector2f(-n.y, n.x);
                    n2.normalize();
                    n2.scale(info.getCutThickness());
                    Vector2f n3 = new Vector2f(n2);
                    n3.scale(-1);
                    int size = info.getPointDistances().get(i).size();
                    if (size > 0) {
                        a.scale(info.getPointDistances().get(i).get(size / 4));
                        a.add(info.getSamplePoints().get(i));
                        Vector2f b = new Vector2f(a);

                        c.scale(info.getPointDistances().get(i).get(3 * size / 4));
                        c.add(info.getSamplePoints().get(i));
                        Vector2f d = new Vector2f(c);

                        if (midline.size() >= 2 && IntersectionUtils.findSegmentSegmentIntersection(midline.get(midline.size() - 2), a, midline.get(midline.size() - 1), c) != null) {
                            midline.add(new Vector2f(c));
                            midline.add(new Vector2f(a));
                        } else {
                            midline.add(new Vector2f(a));
                            midline.add(new Vector2f(c));
                        }

                        a.add(n2);
                        b.add(n3);
                        c.add(n3);
                        d.add(n2);

                        if (info.isShowBoxplot()) {
                            gl.glBegin(GL_TRIANGLES);
                            gl.glVertex2d(a.x, a.y);
                            gl.glVertex2d(b.x, b.y);
                            //     gl.glVertex2d(b.x, b.y);
                            gl.glVertex2d(c.x, c.y);
                            gl.glVertex2d(c.x, c.y);
                            gl.glVertex2d(d.x, d.y);
                            //     gl.glVertex2d(d.x, d.y);
                            gl.glVertex2d(a.x, a.y);
                            gl.glEnd();
                        }
                        float iqr = info.getPointDistances().get(i).get(3 * size / 4) - info.getPointDistances().get(i).get(size / 4);
                        float median = info.getPointDistances().get(i).get(size / 2);
                        float bottomWhisker = Float.POSITIVE_INFINITY;
                        float topWhisker = Float.NEGATIVE_INFINITY;
                        for (int k = 0; k < info.getPointDistances().get(i).size(); k++) {
                            if (info.getPointDistances().get(i).get(k) > (median - (iqr * 1.5)) && bottomWhisker == Float.POSITIVE_INFINITY) {
                                bottomWhisker = info.getPointDistances().get(i).get(k);
                            }
                            if (info.getPointDistances().get(i).get(k) < (median + (iqr * 1.5))) {
                                topWhisker = info.getPointDistances().get(i).get(k);
                            }
                        }
                        Vector2f aWhisk = new Vector2f(n);
                        aWhisk.scale(topWhisker);
                        aWhisk.add(info.getSamplePoints().get(i));
                        Vector2f bWhisk = new Vector2f(n);
                        bWhisk.scale(bottomWhisker);
                        bWhisk.add(info.getSamplePoints().get(i));

                        left.add(new Vector2f(aWhisk));
                        right.add(new Vector2f(bWhisk));

                        if (info.isShowBoxplot()) {
                            gl.glBegin(GL_LINES);
                            gl.glVertex2d(aWhisk.x, aWhisk.y);
                            gl.glVertex2d(bWhisk.x, bWhisk.y);

                            gl.glVertex2d(aWhisk.x + n2.x, aWhisk.y + n2.y);
                            gl.glVertex2d(aWhisk.x + n3.x, aWhisk.y + n3.y);

                            gl.glVertex2d(bWhisk.x + n2.x, bWhisk.y + n2.y);
                            gl.glVertex2d(bWhisk.x + n3.x, bWhisk.y + n3.y);
                            gl.glEnd();
                        }
                        gl.glPointSize(info.getCutThickness() + 1);
                        gl.glBegin(GL_POINTS);
                        for (int k = 0; k < info.getPointDistances().get(i).size(); k++) {
                            if (info.getPointDistances().get(i).get(k) < bottomWhisker || info.getPointDistances().get(i).get(k) > topWhisker) {
                                Vector2f p = new Vector2f(n);
                                p.scale(info.getPointDistances().get(i).get(k));
                                p.add(info.getSamplePoints().get(i));
                                gl.glVertex2d(p.x, p.y);
                            }
                        }
                        gl.glEnd();
                    }
                }
                //  }
            }
            if (info.isShowBoxplotFunction()) {
                gl.glBegin(GL_TRIANGLE_STRIP);
                for (Vector2f m : midline) {
                    gl.glVertex2d(m.x, m.y);
                }
                gl.glEnd();

                gl.glBegin(GL_LINE_STRIP);
                for (Vector2f m : left) {
                    gl.glVertex2d(m.x, m.y);
                }
                gl.glEnd();

                gl.glBegin(GL_LINE_STRIP);
                for (Vector2f m : right) {
                    gl.glVertex2d(m.x, m.y);
                }
                gl.glEnd();
            }
        }
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glLineWidth(info.getCutThickness() + 1);
        gl.glColor3f(1, 0, 0);

        if (!info.getLists2().isEmpty()) {
            for (LinkedList<Vector2f> l : info.getLists2().get(0)) {
                gl.glBegin(GL_LINE_STRIP);
                for (int i = 0; i < l.size() - 1; i++) {
                    gl.glVertex2d(l.get(i).x, l.get(i).y);
                }
                gl.glEnd();
            }
        }

        if (info.isShowAllCuts()) {
            gl.glLineWidth(info.getCutThickness());
            gl.glColor4fv(info.getColorOfCut(), 0);
            for (LinkedList<LinkedList<Vector2f>> list : info.getLists2()) {

                for (LinkedList<Vector2f> l : list) {
                    gl.glBegin(GL_LINE_STRIP);
                    for (int i = 0; i < l.size() - 1; i++) {
                        gl.glVertex2d(l.get(i).x, l.get(i).y);

                    }
                    gl.glEnd();
                }

            }

        }

        gl.glLineWidth(info.getCutThickness() + 1);
        if (info.isShowVectors()) {
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            //average
            gl.glColor3f(1, 0, 0);
            for (int i = 0; i < info.getAveragedistancePoints().size(); i++) {
                if (info.getAveragedistancePoints().get(i) != null) {

                    if (arrowSelectionIndex == i) {
                        gl.glColor3f(1, 1, 0);
                    } else {
                        gl.glColor3f(1, 0, 0);
                    }
                    drawArrow(info.getSamplePoints().get(i), info.getAveragedistancePoints().get(i));
                }
            }
        }
        //testArrowClicked();
        gl.glPopAttrib();
    }

    public Vector3f testArrowClicked(float x, float y) {

        ModelSelector s = new ModelSelector(glu);
        arrowSelectionIndex = -1;
        for (int i = 0; i < info.getAveragedistancePoints().size(); i++) {
            if (info.getAveragedistancePoints().get(i) != null) {
                Vector2f p1 = new Vector2f(info.getSamplePoints().get(i));
                Vector2f p2 = new Vector2f(info.getAveragedistancePoints().get(i));
                Vector2f u = new Vector2f(p2);
                u.sub(p1);
                Vector2f n = new Vector2f(u);
                n.normalize();
                n = new Vector2f(-n.y, n.x);
                n.scale((float) Math.abs(2 * (zCameraPosition / 90f)));
                u.scale(vetctorScale);
                p2 = new Vector2f(p1);
                p2.add(u);

                if (u.length() < (float) Math.abs(2 * (zCameraPosition / 90f))) {
                    float size = (float) Math.abs(2 * (zCameraPosition / 90f)) - u.length();
                    Vector2f v = new Vector2f(u);
                    v.normalize();
                    v.scale(size);
                    p1.sub(v);
                }

                Vector3f a = new Vector3f(p1.x - n.x / 2f, p1.y - n.y / 2f, 0);
                Vector3f b = new Vector3f(p1.x + n.x / 2f, p1.y + n.y / 2f, 0);
                Vector3f c = new Vector3f(p2.x + n.x / 2f, p2.y + n.y / 2f, 0);
                Vector3f d = new Vector3f(p2.x - n.x / 2f, p2.y - n.y / 2f, 0);

                s.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
                Vector3f[] t1 = {a, b, c};
                Vector3f[] t2 = {a, c, d};
                if (s.calculateIntersection(t1, false) != null || s.calculateIntersection(t2, false) != null) {
                    arrowSelectionIndex = i;
                }

            }
        }
        return arrowSelectionIndex > -1 ? rotateToXYZ(new Vector3f(info.getSamplePoints().get(arrowSelectionIndex).x, info.getSamplePoints().get(arrowSelectionIndex).y, 0)) : null;
    }

    public void setPlaneNormal(Vector3f planeNormal) {
        info.setPlaneNormal(planeNormal);
        if (info.getModels().size() == 1) {
            info.setPlane(computePlanePolygon(boundingBox));
            rotateGizmo();
        }
    }

    private void rotateGizmo() {
        Vector3f xAxis = new Vector3f(1, 0, 0);
        Vector3f vector = new Vector3f(info.getPlaneNormal());
        vector.normalize();
        if (info.getPlaneNormal().x < 0) {
            vector.setX(-vector.x);
        }
        float angle = xAxis.angle(vector);
        Vector3f axis = new Vector3f();
        axis.cross(xAxis, vector);
        Vector3f p = IntersectionUtils.findLinePlaneIntersection(new Vector3f(), info.getPlaneNormal(), info.getPlaneNormal(), info.getPlanePoint());

        for (int i = 0; i < originalGizmo.getVerts().size(); i++) {
            Vector3f v = new Vector3f(originalGizmo.getVerts().get(i));
            v = rotateAroundAxe(v, axis, angle);
            if (info.getPlaneNormal().x < 0) {
                v.setX(-v.x);
            }
            v.add(p);
            gizmo.getVerts().set(i, v);
        }
        // ArrayList<Vector3f> bb = new ArrayList<Vector3f>();
        for (int i = 0; i < gizmo.getModelDims().getOriginalBoundingBox().size(); i++) {
            Vector3f v = new Vector3f(gizmo.getModelDims().getOriginalBoundingBox().get(i));
            v = rotateAroundAxe(v, axis, angle);
            if (info.getPlaneNormal().x < 0) {
                v.setX(-v.x);
            }
            v.add(p);
            gizmo.getModelDims().getBoundingBox().set(i, v);
        }

    }

    public boolean isShowVectors() {
        return info.isShowVectors();
    }

    public ComparisonListenerInfo getInfo() {
        return info;
    }

    public void setShowVectors(boolean showVectors) {
        info.setShowVectors(showVectors);
    }

    public boolean isShowAllCuts() {
        return info.isShowAllCuts();
    }

    public void setShowAllCuts(boolean showAllCuts) {
        info.setShowAllCuts(showAllCuts);
    }

    public void setHighlightCuts(boolean highlightCuts) {
        this.highlightCuts = highlightCuts;
    }

    public void setVectroScale(float scale) {
        vetctorScale = scale;
    }

    public boolean isShowSamplingRays() {
        return info.isShowSamplingRays();
    }

    public void setShowSamplingRays(boolean showSamplingRays) {
        info.setShowSamplingRays(showSamplingRays);
    }

    private Vector2f lineSegmentIntersection(Vector2f point1, Vector2f normal, Vector2f a, Vector2f b) {
        Vector2f intersection;

        Vector2f u = new Vector2f(b);
        u.sub(a);

        float det = u.y * normal.x - u.x * normal.y;

        if (det == 0) {
            return null;//parallel lines
        } else {
            float s = (u.y * (a.x - point1.x) + u.x * (point1.y - a.y)) / det;
            float t = (point1.y - a.y + s * normal.y) / u.y;

            float x = point1.x + s * normal.x;
            float y = point1.y + s * normal.y;

            intersection = new Vector2f(x, y);
            if (t >= 0 && t <= 1) {
                return intersection;
            }

        }

        return null;

    }

    private Vector2f findPointOnCurve(Vector2f pt, Vector2f n, LinkedList<LinkedList<Vector2f>> lists) {
        Vector2f intersection = null;
        float dist = Float.MAX_VALUE;
        for (LinkedList<Vector2f> list : lists) {
            for (int i = 1; i < list.size(); i++) {
                Vector2f tmp = lineSegmentIntersection(pt, n, list.get(i - 1), list.get(i));
                if (tmp != null) {
                    Vector2f tmp2 = new Vector2f(tmp);
                    tmp2.sub(pt);
                    if (tmp2.length() < dist) {
                        intersection = tmp;
                        dist = tmp2.length();
                    }
                }
            }
        }
        //threshold
        if (dist < 20) {
            return intersection;
        } else {
            return null;
        }
    }

    private void sampleList(LinkedList<LinkedList<Vector2f>> lists) {
        ArrayList<Vector2f> points = new ArrayList<>();
        ArrayList<Vector2f> normals = new ArrayList<>();

        float step = 3;

        if (lists != null && lists.size() > 0) {

            if (lists.getFirst().size() > 1) {
                points.add(lists.getFirst().getFirst());
                Vector2f x = new Vector2f(lists.getFirst().get(1));
                x.sub(lists.getFirst().get(0));
                normals.add(new Vector2f(x.y, -x.x));
                normals.get(0).normalize();
            }

            for (LinkedList<Vector2f> list : lists) {
                float distance = 0;
                for (int i = 1; i < list.size(); i++) {
                    Vector2f v = new Vector2f(list.get(i));
                    v.sub(list.get(i - 1));
                    if (v.length() > 0) {
                        if ((distance + v.length()) >= step) {
                            Vector2f u = new Vector2f(list.get(i - 1));
                            float length = v.length();
                            float tmpDistance = length - (step - distance);
                            v.normalize();
                            Vector2f n = new Vector2f(-v.y, v.x);
                            normals.add(n);
                            v.scale(step - distance);
                            u.add(v);
                            points.add(u);
                            distance = tmpDistance;
                            while (distance > step) {
                                v.normalize();
                                v.scale(step);
                                u.add(v);
                                points.add(u);
                                normals.add(n);
                                distance = distance - step;
                            }

                        } else {
                            distance += v.length();
                        }
                    }
                }
            }
        }

        info.setSamplePoints(points);
        info.setSampleNormals(normals);
    }

    private void rotateToXY(ArrayList<LinkedList<LinkedList<Vector3f>>> lists) {
        Vector3f axe = new Vector3f();
        Vector3f n = new Vector3f(info.getPlaneNormal());
        n.normalize();
        Vector3f XYnormal = new Vector3f(0, 0, 1);
        axe.cross(n, XYnormal);
        axe.normalize();
        double angle = Math.acos(n.dot(XYnormal));

        ArrayList<LinkedList<LinkedList<Vector2f>>> finalLists = new ArrayList<>();

        for (LinkedList<LinkedList<Vector3f>> list : lists) {
            LinkedList<LinkedList<Vector2f>> tmpLists = new LinkedList<>();
            for (LinkedList<Vector3f> l : list) {
                LinkedList<Vector2f> tmp = new LinkedList<>();
                for (Vector3f l1 : l) {
                    if (Float.isNaN(axe.x)) {
                        tmp.add(new Vector2f(l1.x, l1.y));
                    } else {
                        Vector3f v = rotateAroundAxe(l1, axe, angle);
                        tmp.add(new Vector2f(v.x, v.y));
                        //  tmp.getLast().setZ(0);
                    }

                }
                tmpLists.add(tmp);
            }
            finalLists.add(tmpLists);
        }

        info.setLists2(finalLists);
    }

    private Vector3f rotateToXYZ(Vector3f p) {
        Vector3f axe = new Vector3f();
        Vector3f n = new Vector3f(info.getPlaneNormal());
        n.normalize();
        Vector3f XYnormal = new Vector3f(0, 0, 1);
        axe.cross(n, XYnormal);
        axe.normalize();
        double angle = Math.acos(n.dot(XYnormal));
        Vector3f tmp = rotateAroundAxe(p, axe, -angle);
        tmp.add(info.getPlanePoint());
        return tmp;
    }

    public void setLists(ArrayList<LinkedList<LinkedList<Vector3f>>> lists) {
        info.setLists(lists);
    }

    public void setLists(ArrayList<LinkedList<LinkedList<Vector3f>>> lists, boolean b) {
        rotateToXY(lists);
        sampleList(info.getLists2().get(0));

        ArrayList<ArrayList<Vector2f>> points = new ArrayList<>();
        for (int j = 1; j < info.getLists2().size(); j++) {
            ArrayList<Vector2f> pts = new ArrayList<>();
            for (int i = 0; i < info.getSamplePoints().size(); i++) {
                //    pts.add(findNearestPoint(samplePoints.get(i), this.lists.get(j)));
                pts.add(findPointOnCurve(info.getSamplePoints().get(i), info.getSampleNormals().get(i), info.getLists2().get(j)));
            }
            points.add(pts);
        }

        info.setDistancePoints(points);

        ArrayList<ArrayList<Float>> distances = new ArrayList<>();
        for (int i = 0; i < info.getSamplePoints().size(); i++) {
            ArrayList<Float> ptDistances = new ArrayList<>();
            for (int j = 1; j < points.size(); j++) {
                if (points.get(j).get(i) != null) {
                    Vector2f pb = points.get(j).get(i);
                    Vector2f pa = info.getSamplePoints().get(i);
                    Vector2f n = info.getSampleNormals().get(i);
                    Vector2f ab = new Vector2f(pb);
                    ab.sub(pa);
                    float dot = n.dot(ab);
                    float pt = Math.signum(dot) * ab.length();
                    if (pt != NaN) {
                        ptDistances.add(Math.signum(dot) * ab.length());
                    }
                } else {
                    //  ptDistances.add(null);
                }
            }
            Collections.sort(ptDistances);
            distances.add(ptDistances);
        }
        info.setPointDistances(distances);

        //average
        List<Vector2f> averagedistancePoints = new ArrayList<>();
        ArrayList<Integer> counters = new ArrayList<>();
        for (Vector2f samplePoint : info.getSamplePoints()) {
            averagedistancePoints.add(null);
            counters.add(0);
        }

        for (ArrayList<Vector2f> pts : info.getDistancePoints()) {
            for (int i = 0; i < pts.size(); i++) {
                if (averagedistancePoints.get(i) == null && pts.get(i) != null) {
                    averagedistancePoints.set(i, new Vector2f(pts.get(i)));
                    counters.set(i, counters.get(i) + 1);
                } else if (pts.get(i) != null) {
                    averagedistancePoints.get(i).add(new Vector2f(pts.get(i)));
                    counters.set(i, counters.get(i) + 1);
                }

            }
        }

        for (int i = 0; i < averagedistancePoints.size(); i++) {
            if (averagedistancePoints.get(i) != null) {
                averagedistancePoints.set(i, new Vector2f(averagedistancePoints.get(i).x / counters.get(i), averagedistancePoints.get(i).y / counters.get(i)));
            }
        }

        info.setAveragedistancePoints((ArrayList<Vector2f>) averagedistancePoints);

    }

    /**
     * Select gizmo on which user clicked
     *
     * @param x x mouse position
     * @param y y mouse position
     * @return true if gizmo was selected
     */
    public Boolean pickManipulator(int x, int y) {
        if (info.getHdInfo() != null && info.getHdInfo().getvType() == VisualizationType.CROSSSECTION) {
            ModelSelector picker = new ModelSelector(glu);
            ArrayList<Model> m = new ArrayList<>();
            m.add(gizmo);
            Vector3f cameraPosition = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);

            Model model = picker.pickModel(x, y, m, viewport, modelViewMatrix, projectionMatrix, cameraPosition);
            gizmoIntersection = picker.getIntersection();

            return model != null;
        }
        return false;
    }

    public Vector3f getGizmoIntersection() {
        return gizmoIntersection;
    }

    public Vector3f getPlaneCenter() {
        Vector3f p = IntersectionUtils.findLinePlaneIntersection(new Vector3f(), info.getPlaneNormal(), info.getPlaneNormal(), info.getPlanePoint());
        return p;
    }

    public Point getPlaneCenter2D() {
        Vector3f p = IntersectionUtils.findLinePlaneIntersection(new Vector3f(), info.getPlaneNormal(), info.getPlaneNormal(), info.getPlanePoint());
        return get2DPoint(p);
    }

    private Point get2DPoint(Vector3f coords) {
        double coordsTransformed[] = new double[3];
        glu.gluProject(
                coords.getX(),
                coords.getY(),
                coords.getZ(),
                modelViewMatrix, 0,
                projectionMatrix, 0,
                viewport, 0,
                coordsTransformed, 0);
        coordsTransformed[1] = viewport[3] - coordsTransformed[1] - 1;
        return new Point((int) coordsTransformed[0], (int) coordsTransformed[1]);
    }

    public void setPlanePoint(Vector3f planePoint) {
        info.setPlanePoint(planePoint);
        if (gizmo != null) {
            rotateGizmo();
        }
        if (boundingBox != null) {
            info.setPlane(computePlanePolygon(boundingBox));
        }
    }

    public Vector3f getPlaneNormal() {
        return info.getPlaneNormal();

    }

    private LinkedList<Vector3f> computePlanePolygon(ArrayList<Vector3f> bbox) {
        ArrayList<Vector3f> bb = new ArrayList<>();
        for (Vector3f bb1 : bbox) {
            Vector3f p = new Vector3f(bb1);
            p.scale(1.3f);
            bb.add(p);
        }
        LinkedList<Vector3f> plane = new LinkedList<>();
        Vector3f in = new Vector3f();
        for (int i = 0; i < 7; i++) {
            in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(i), bb.get(i + 1), info.getPlaneNormal(), info.getPlanePoint());
            if (in != null) {
                plane.add(in);
            }
        }
        in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(0), bb.get(3), info.getPlaneNormal(), info.getPlanePoint());
        if (in != null) {
            plane.add(in);
        }
        in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(0), bb.get(5), info.getPlaneNormal(), info.getPlanePoint());
        if (in != null) {
            plane.add(in);
        }
        in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(1), bb.get(6), info.getPlaneNormal(), info.getPlanePoint());
        if (in != null) {
            plane.add(in);
        }
        in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(2), bb.get(7), info.getPlaneNormal(), info.getPlanePoint());
        if (in != null) {
            plane.add(in);
        }
        in = IntersectionUtils.findSegmentPlaneIntersection(bb.get(4), bb.get(7), info.getPlaneNormal(), info.getPlanePoint());
        if (in != null) {
            plane.add(in);
        }

        //find mean
        in = new Vector3f(0, 0, 0);
        for (Vector3f p : plane) {
            in.add(p);
        }
        in = new Vector3f(in.x / (float) plane.size(), in.y / (float) plane.size(), in.z / (float) plane.size());

        //order clockwise
        if (plane.size() > 0) {
            Vector3f p1 = new Vector3f(plane.get(0));
            p1.sub(in);
            LinkedList<Double> angles = new LinkedList<>();
            angles.add(0.);

            Vector3f n = new Vector3f(info.getPlaneNormal());
            n.normalize();

            for (int i = 1; i < plane.size(); i++) {
                Vector3f p2 = new Vector3f(plane.get(i));
                p2.sub(in);

                float dot = p1.dot(p2);
                Vector3f cross = new Vector3f();
                cross.cross(p1, p2);
                float det = n.dot(cross);
                double angle = Math.atan2(det, dot);
                if (angle < 0) {
                    angle = angle + 2 * Math.PI;
                }
                angles.add(angle);

            }

            for (int i = 1; i < plane.size(); i++) {
                for (int j = plane.size() - 1; j > i; j--) {
                    if (angles.get(j) > angles.get(i)) {
                        double angle = angles.get(i);
                        angles.set(i, angles.get(j));
                        angles.set(j, angle);

                        Vector3f p = plane.get(i);
                        plane.set(i, plane.get(j));
                        plane.set(j, p);

                    }
                }
            }

        }

        return plane;
    }

    public Vector3f getPlanePoint() {
        return info.getPlanePoint();
    }

    private void drawArrow(Vector2f from, Vector2f t) {
        Vector2f u = new Vector2f(t);
        u.sub(from);
        u.scale(vetctorScale);
        Vector2f to = new Vector2f(from);
        to.add(u);

        double angle = Math.toRadians(45);
        Vector2f v = new Vector2f(from);
        v.sub(to);
        v.normalize();
        if (!info.isRender()) {
            v.scale((float) Math.abs(2 * (zCameraPosition / 80f)));
        } else {
            v.scale(2);
        }

        v.add(to);
        double newX1 = to.x + (Math.cos(angle) * (v.x - to.x) + Math.sin(angle) * (v.y - to.y));
        double newY1 = to.y + (-Math.sin(angle) * (v.x - to.x) + Math.cos(angle) * (v.y - to.y));
        double newX2 = to.x + (Math.cos(-angle) * (v.x - to.x) + Math.sin(-angle) * (v.y - to.y));
        double newY2 = to.y + (-Math.sin(-angle) * (v.x - to.x) + Math.cos(-angle) * (v.y - to.y));

        gl.glBegin(GL_LINES);

        gl.glVertex2d(from.x, from.y);

        gl.glVertex2d(to.x, to.y);

        gl.glVertex2d(newX1, newY1);

        gl.glVertex2d(to.x, to.y);

        gl.glVertex2d(newX2, newY2);

        gl.glVertex2d(to.x, to.y);

        gl.glEnd();
    }

    private ArrayList<Vector3f> calculateBoundingBox() {
        float leftPt = 0.0f;
        float rightPt = 0.0f;
        float topPt = 0.0f;
        float bottomPt = 0.0f;
        float farPt = 0.0f;
        float nearPt = 0.0f;
        for (Model model : info.getModels()) {
            ArrayList<Vector3f> bb = model.getModelDims().getBoundingBox();
            for (int j = 0; j < 8; j++) {
                if (bb.get(j).x < leftPt) {
                    leftPt = bb.get(j).x;
                }
                if (bb.get(j).x > rightPt) {
                    rightPt = bb.get(j).x;
                }
                if (bb.get(j).y > topPt) {
                    topPt = bb.get(j).y;
                }
                if (bb.get(j).y < bottomPt) {
                    bottomPt = bb.get(j).y;
                }
                if (bb.get(j).z < farPt) {
                    farPt = bb.get(j).z;
                }
                if (bb.get(j).z > nearPt) {
                    nearPt = bb.get(j).z;
                }
            }
        }
        ArrayList<Vector3f> bb = new ArrayList<>();
        bb.add(new Vector3f(rightPt, topPt, farPt));
        bb.add(new Vector3f(rightPt, topPt, nearPt));
        bb.add(new Vector3f(rightPt, bottomPt, nearPt));
        bb.add(new Vector3f(rightPt, bottomPt, farPt));
        bb.add(new Vector3f(leftPt, bottomPt, farPt));
        bb.add(new Vector3f(leftPt, topPt, farPt));
        bb.add(new Vector3f(leftPt, topPt, nearPt));
        bb.add(new Vector3f(leftPt, bottomPt, nearPt));
        return bb;
    }

    public void setPrimaryModel() {
        boundingBox = calculateBoundingBox();
        info.setPlane(computePlanePolygon(boundingBox));
        //lists = new ArrayList<>();
        info.getLists().add(0, IntersectionUtils.findModelPlaneIntersection(info.getModels().get(0), info.getPlaneNormal(), info.getPlanePoint()));
//        primaryModelSet = true;

    }

    private void drawBoundingBox() {
        gl.glDisable(GL_LIGHTING);
        // gl.glPointSize(3);

        gl.glLineWidth(1);
        gl.glBegin(GL.GL_LINES);

        ArrayList<Vector3f> bb = new ArrayList<>();
        for (Vector3f bb1 : boundingBox) {          // selectionCube) {//  
            Vector3f p = new Vector3f(bb1);
            p.scale(1.3f);
            bb.add(p);
        }

        for (int i = 0; i < 7; i++) {
            gl.glVertex3f(bb.get(i).getX(), bb.get(i).getY(), bb.get(i).getZ());
            gl.glVertex3f(bb.get(i + 1).getX(), bb.get(i + 1).getY(), bb.get(i + 1).getZ());
            //  drawCylinder(bb.get(i), bb.get(i + 1),0.3f);
        }

        gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
        gl.glVertex3f(bb.get(3).getX(), bb.get(3).getY(), bb.get(3).getZ());

        gl.glVertex3f(bb.get(0).getX(), bb.get(0).getY(), bb.get(0).getZ());
        gl.glVertex3f(bb.get(5).getX(), bb.get(5).getY(), bb.get(5).getZ());

        gl.glVertex3f(bb.get(1).getX(), bb.get(1).getY(), bb.get(1).getZ());
        gl.glVertex3f(bb.get(6).getX(), bb.get(6).getY(), bb.get(6).getZ());

        gl.glVertex3f(bb.get(2).getX(), bb.get(2).getY(), bb.get(2).getZ());
        gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());

        gl.glVertex3f(bb.get(4).getX(), bb.get(4).getY(), bb.get(4).getZ());
        gl.glVertex3f(bb.get(7).getX(), bb.get(7).getY(), bb.get(7).getZ());
        /*     
         drawCylinder(bb.get(0), bb.get(3),0.1f);
         drawCylinder(bb.get(0), bb.get(5),0.3f);
         drawCylinder(bb.get(1), bb.get(6),0.3f);
         drawCylinder(bb.get(2), bb.get(7),0.3f);
         drawCylinder(bb.get(4), bb.get(7),0.3f);*/

        gl.glEnd();
        gl.glEnable(GL_LIGHTING);
    }

    private void drawCylinder(Vector3f from, Vector3f to, float size) {
        GLUT glut = new GLUT();
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
        glut.glutSolidCylinder(size, length, 10, 1);
        gl.glPopMatrix();

    }

    private void trasparencyRender() {
        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
        //Shadow map rendering
        if (useGlyphs) {
            gl.glBindFramebuffer(GL_FRAMEBUFFER, depthFBO[0]);
            gl.glDepthFunc(GL_LESS);
            gl.glUseProgram(shadowMapShadersId);
            gl.glViewport(0, 0, shadowRatio * MAX_FRAMEBUFFER_HEIGHT, shadowRatio * MAX_FRAMEBUFFER_WIDTH);
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glColorMask(false, false, false, false);//or false??
            setupMatrices(defaultPosition.x, defaultPosition.y, defaultPosition.z, 0, 0, 0, 0, 1, 0);

            // setupMatrices((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition, (float) xCenter, (float) yCenter, (float) zCenter, (float) xUpPosition, (float) yUpPosition, (float) zUpPosition);
            gl.glCullFace(GL_FRONT);
            renderModels(true, false);
            setTextureMatrix();
            gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
            gl.glCullFace(GL_BACK);
        } else {
            unsetTextureMatrix();
        }

        //************************************************
        //Linked List creation 
        gl.glDepthMask(true);
        gl.glDepthFunc(GL_ALWAYS);
        gl.glEnable(GL_LIGHTING);
        gl.glViewport(0, 0, currentWidth, currentHeight);
        gl.glColorMask(true, true, true, true);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(listCreationShadersId);

        //reset atomic counter
        gl.glBindBufferBase(GL_ATOMIC_COUNTER_BUFFER, 0, acBuffer[0]);
        gl.glBufferSubData(GL_ATOMIC_COUNTER_BUFFER, 0, zero.capacity() * 4, zero);
        //reset headPointer
        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, hpInitializer[0]);
        gl.glBindTexture(GL_TEXTURE_2D, hpTexture[0]);
        gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, MAX_FRAMEBUFFER_WIDTH, MAX_FRAMEBUFFER_HEIGHT, GL_RED_INTEGER, GL_UNSIGNED_INT, 0);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

        gl.glActiveTexture(GL_TEXTURE0);

        if (useGlyphs) {
            gl.glBindTexture(GL_TEXTURE_2D, depthTexture[0]);
        } else {
            gl.glBindTexture(GL_TEXTURE_2D, 0);
        }
        setupMatrices((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition, (float) xCenter, (float) yCenter, (float) zCenter, (float) xUpPosition, (float) yUpPosition, (float) zUpPosition);
        gl.glDisable(GL_CULL_FACE);

        gl.glEnable(GL.GL_BLEND); // Enable the OpenGL Blending functionality  
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindImageTexture(1, hpTexture[0], 0, false, 0, GL_READ_WRITE, GL_R32UI);
        gl.glBindImageTexture(2, fsTexture[0], 0, false, 0, GL_WRITE_ONLY, GL_RGBA32UI);

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glEnable(GL_TEXTURE_2D);
        renderModels(false, false);
        gl.glDisable(GL_LIGHTING);
        gl.glMemoryBarrier(GL_ALL_BARRIER_BITS);

        // * ***************************************************************
        //calculate actual z-buffer values
        FloatBuffer depth = Buffers.newDirectFloatBuffer(currentHeight * currentWidth);
        gl.glReadPixels(0, 0, currentWidth, currentHeight, GL_DEPTH_COMPONENT, GL_FLOAT, depth);
        float min = 1;
        float max = 0;
        int i = 0;
        while (i < currentHeight * currentWidth) {
            if (depth.get(i) == 0 || depth.get(i) == 1) {
                depth.put(i, -1);
            }
            if (depth.get(i) < min && depth.get(i) != -1) {
                min = depth.get(i);
            }
            if (depth.get(i) > max && depth.get(i) != -1) {
                max = depth.get(i);
            }
            i++;
        }

        float[] fogColor = info.getFogColor();

        //rendering of final image
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(OITShadersId);
        gl.glUniform1f(minZUniform, min);
        gl.glUniform1f(maxZUniform, max);
        gl.glUniform1i(fogUniform, fogVersion);
        gl.glUniform4f(fogColorUniform, fogColor[0], fogColor[1], fogColor[2], 1);
        gl.glUniform1i(innSurfSolidUniform, innerSurfaceSolid);
        gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (info.isContours()) //  rendering of contours
        {
            gl.glUseProgram(FinalShadersId);
            gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            gl.glDisable(GL.GL_BLEND);
        }

        gl.glDisable(GL.GL_BLEND);
        gl.glPopAttrib();
    }

    private void unsetTextureMatrix() {
        gl.glMatrixMode(GL_TEXTURE);
        gl.glActiveTexture(GL_TEXTURE7);
        float[] bias = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gl.glLoadIdentity();
        gl.glLoadMatrixf(bias, 0);
        gl.glMatrixMode(GL_MODELVIEW);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        reloadTextures();
        currentHeight = height;
        currentWidth = width;

        if (height == 0) {
            height = 1;    // to avoid division by 0 in aspect ratio below
        }
        gl.glViewport(x, y, width, height);  // size of drawing area

        float h = (float) height / (float) width;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(60, width / (float) height, 5.0f, 1500.0f);
        //  gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 1500.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        //  gl.glTranslatef(0.0f, 0.0f, -40.0f);
    }

    private void setTextureMatrix() {
        // This is matrix transform every coordinate x,y,z
        // x = x* 0.5 + 0.5 
        // y = y* 0.5 + 0.5 
        // z = z* 0.5 + 0.5 
        // Moving from unit cube [-1,1] to [0,1]  
        float[] bias = {0.5f, 0, 0, 0, 0, 0.5f, 0, 0, 0, 0, 0.5f, 0, 0.5f, 0.5f, 0.5f, 1};

        // Grab modelview and transformation matrices
        gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelWiew, 0);
        gl.glGetFloatv(GL_PROJECTION_MATRIX, projection, 0);

        gl.glMatrixMode(GL_TEXTURE);
        gl.glActiveTexture(GL_TEXTURE7);

        gl.glLoadIdentity();
        gl.glLoadMatrixf(bias, 0);

        // concatating all matrice into one.
        gl.glMultMatrixf(projection, 0);
        gl.glMultMatrixf(modelWiew, 0);

        // Go back to normal matrix mode
        gl.glMatrixMode(GL_MODELVIEW);
    }

    void setupMatrices(float position_x, float position_y, float position_z, float lookAt_x, float lookAt_y, float lookAt_z, float up_x, float up_y, float up_z) {
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60, currentWidth / (float) currentHeight, 5.0f, 5000.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(position_x, position_y, position_z, lookAt_x, lookAt_y, lookAt_z, up_x, up_y, up_z);
    }

    private void renderModels(boolean shadowMap, boolean colormap) {
        if (colormap && info.getModels().size() > 0) {
            float[] color = {1, 1, 1, 1};
            setMaterialColor(color);

            if (info.getHdInfo().isIsSelection() && selectionCube[4] != null) {
                float vertices[] = new float[12];
                for (int f = 0; f < 4; f++) {
                    Vector3f v = selectionCube[f];
                    vertices[f * 3] = v.x;
                    vertices[f * 3 + 1] = v.y;
                    vertices[f * 3 + 2] = v.z;

                }
                FloatBuffer a = FloatBuffer.wrap(vertices);
                gl.glUniform1i(selectionTypeUniform, info.getHdInfo().isIsSelection() ? info.getHdInfo().getsType().ordinal() : -1);
                gl.glUniform3fv(selectionUniform, 12, a);
                gl.glUniform3f(selectionCameraUbniform, selectionCube[4].x, selectionCube[4].y, selectionCube[4].z);
            } else {
                float vertices[] = new float[12];
                for (int f = 0; f < 4; f++) {
                    vertices[f * 3] = 0;
                    vertices[f * 3 + 1] = 0;
                    vertices[f * 3 + 2] = 0;

                }
                FloatBuffer a = FloatBuffer.wrap(vertices);
                gl.glUniform1i(selectionTypeUniform, info.getHdInfo().isIsSelection() ? info.getHdInfo().getsType().ordinal() : -1);
                gl.glUniform3fv(selectionUniform, 12, a);
                gl.glUniform3f(selectionCameraUbniform, 0, 0, 0);
            }

            info.getModels().get(0).drawForShaders(gl, curDistanceAttrib, info.getHdInfo().getDistance());
        } else if (info.getModels().size() == 2) {
            reloadTextures();
            float[] color = {0.8667f, 0.7176f, 0.6275f, 0.5f};

            for (int i = 0; i < 2; i++) {
                if (info.getModels().get(i) != null) {
                    gl.glUniform1i(modelNumUniform, i);
                    if (i == 0) {
                        setMaterialColor(info.getPrimaryColor());
                    } else if (i == 1) {
                        setMaterialColor(info.getSecondaryColor());
                    } else {
                        setMaterialColor(color);
                    }

                    if (info.getSampleVetices().size() == info.getModels().size() && useGlyphs) {
                        if (shadowMap) {
                            info.getModels().get(i).drawForShaders(gl, info.getVerticesInfo().get(i), shadowVertexListUniform, shadowNormalListUniform, shadowPrincipalListUniform, shadowSecondaryListUniform);
                        } else {
                            info.getModels().get(i).drawForShaders(gl, info.getVerticesInfo().get(i), modelVertexListUniform, modelNormalListUniform, modelPrincipalListUniform, modelSecondaryListUniform);
                        }

                    } else {
                        info.getModels().get(i).drawWithoutTextures(gl);
                    }
                }
                // }
            }

        }
    }

    private void setMaterialColor(float[] color) {
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 50);
        float[] colorKs = {1, 1, 1, 0f};
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
    }

    @Override
    public void setModels(Model model) {
        info.setModel(model);
        if (info.getModels().size() == 2) {
            sampleModels(info.getModels());
        }

    }

    public void addModel(Model model) {
        info.addModel(model);
        if (info.getModels().size() == 2) {
            sampleModels(info.getModels());
        }
    }

    @Override
    public void removeModel() {
        info.removesModel();
    }

    public HDpaintingInfo getHdInfo() {
        return info.getHdInfo();
    }

    public void setHdInfo(HDpaintingInfo hdInfo) {
        info.setHdInfo(hdInfo);
    }

    public void setHdPaint(HDpainting hdPaint) {
        info.setHdPaint(hdPaint);
    }

    public void drawHD(boolean paintHD) {
        info.setPaintHD(paintHD);
    }

    public boolean selectPoint(double x, double y) {
        if (!info.isProcrustes() && (info.getFacialPoints() == null || info.getFacialPoints().isEmpty())) {
            return false;
        }
        ModelSelector picker = new ModelSelector(glu);
        picker.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
        Vector3f v = picker.getRayStartPoint();
        Vector3f v1 = picker.getRayEndPoint();
        Vector3f u = new Vector3f(v1.x - v.x, v1.y - v.y, v1.z - v.z);
        boolean selected = false;

        float minDist = Float.MAX_VALUE;

        if (info.isProcrustes() && info.getPaInfo().getType() == 2) {
            ProcrustesAnalysis mean = info.getPaInfo().getGpa().countMeanConfig();
            List<ArrayList<Vector3f>> points = new ArrayList<>();

            for (int j = 0; j < info.getPaInfo().getGpa().getConfigs().size(); j++) {
                ProcrustesAnalysis lpa = info.getPaInfo().getGpa().getPA(j);
                ArrayList<Vector3f> p = new ArrayList<>();
                /*for (int i = 0; i < mean.getConfig().getRowDimension(); i++) {
                 Vector3f vert = new Vector3f((float) lpa.getConfig().get(i, 0),
                 (float) lpa.getConfig().get(i, 1), (float) lpa.getConfig().get(i, 2));

                 List<Vector3f> newVertices = paPainting.enhanceVertices(vert, new Vector3f((float) mean.getConfig().get(i, 0),
                 (float) mean.getConfig().get(i, 1), (float) mean.getConfig().get(i, 2)));

                 p.add(newVertices.get(0));
                 }*/

                for (Integer fpt : mean.getConfig().keySet()) {
                    FacialPoint lpaFP = lpa.getConfig().get(fpt);

                    if (lpaFP == null) {
                        continue;
                    }

                    Vector3f pos = lpaFP.getPosition();
                    Vector3f vert = new Vector3f(pos.x, pos.y, pos.z);

                    Vector3f meanPos = mean.getConfig().get(fpt).getPosition();
                    List<Vector3f> newVertices = info.getPaPainting().enhanceVertices(vert, new Vector3f(meanPos.x, meanPos.y, meanPos.z));

                    p.add(newVertices.get(0));
                }

                points.add(p);
            }
            for (int i = 0; i < points.size(); i++) {
                for (int j = 0; j < points.get(i).size(); j++) {
                    Vector3f p = points.get(i).get(j);
                    double t = ((p.x - v.x) * (v1.x - v.x)
                            + (p.y - v.y) * (v1.y - v.y) + (p.z - v.z) * (v1.z - v.z))
                            / (float) (Math.pow(u.x, 2) + Math.pow(u.y, 2) + Math.pow(u.z, 2));
                    Vector3f w = new Vector3f((float) (v.x + t * u.x), (float) (v.y + t * u.y), (float) (v.z + t * u.z));
                    float dist = (float) Math.sqrt(Math.pow(w.x - p.x, 2) + Math.pow(w.y - p.y, 2) + Math.pow(w.z - p.z, 2));

                    if (dist < info.getPaInfo().getPointSize() / 2f && dist < minDist) {
                        StatusDisplayer.getDefault().setStatusText("Selected configuration:" + i);
                        minDist = dist;
                    }
                }
            }

        }

        //for (FacialPoint point : facialPoints) {
        for (int i = 0; i < info.getFacialPoints().size(); i++) {
            FacialPoint fp = info.getFacialPoints().get(i);
            double t = ((fp.getPosition().x - v.x) * (v1.x - v.x)
                    + (fp.getPosition().y - v.y) * (v1.y - v.y) + (fp.getPosition().z - v.z) * (v1.z - v.z))
                    / (float) (Math.pow(u.x, 2) + Math.pow(u.y, 2) + Math.pow(u.z, 2));
            Vector3f w = new Vector3f((float) (v.x + t * u.x), (float) (v.y + t * u.y), (float) (v.z + t * u.z));
            float dist = (float) Math.sqrt(Math.pow(w.x - fp.getPosition().x, 2) + Math.pow(w.y - fp.getPosition().y, 2) + Math.pow(w.z - fp.getPosition().z, 2));

            if (dist < info.getFacialPointRadius() && dist < minDist) {
                info.setIndexOfSelectedPoint(i);
                //System.out.println("selected point" + indexOfSelectedPoint);
                minDist = dist;
                selected = true;
            }

        }

        return selected;

    }

    //returns point in mesh at given x,y position
    public Vector3f checkPointInMesh(double x, double y) {

        ModelSelector picker = new ModelSelector(glu);
        picker.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
        ArrayList<Vector3f> intersectionPoints = new ArrayList<Vector3f>();

        if (info.getModels().size() > 0) {
            Model model1 = info.getModels().get(0);
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
        }
        if (intersectionPoints.isEmpty()) {
            return null;
        } else if (intersectionPoints.size() == 1) {
            return intersectionPoints.get(0);
        } else {
            return findNearestPoint(intersectionPoints);
        }

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
                minDist = (float) dist;
                intPoint = intersectionPoints.get(i);
            }
        }
        return intPoint;
    }

    public boolean editSelectedPoint(Vector3f coords) {
        if (coords != null) {
            info.getFacialPoints().get(info.getIndexOfSelectedPoint()).setCoords(coords);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setModels(ArrayList<Model> models) {
        info.setModels(models);
        if (info.getModels().size() == 2) {
            sampleModels(info.getModels());
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Model getModel() {
        if (info.getModels().size() > 0) {
            return info.getModels().get(0);
        } else {
            return null;
        }
    }

    public List<Model> getModels() {
        return info.getModels();
    }

    public int getNumberOfModels() {
        return info.getModels().size();
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
            if (info.getIndexOfSelectedPoint() != -1 && i == info.getIndexOfSelectedPoint()) {
                color = new float[]{0f, 1f, 0f, 1.0f};
            } else if (facialPoints.get(i).isActive()) {
                color = info.getColorOfPoint();
            } else {
                color = info.getColorOfInactivePoint();
            }
            gl.glDisable(i);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
            float[] colorKs = {0, 0, 0, 1.0f};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
            //for (FacialPoint point : facialPoints) {
            FacialPoint fp = facialPoints.get(i);
            gl.glTranslated(fp.getPosition().x, fp.getPosition().y, fp.getPosition().z);
            glut.glutSolidSphere((double) info.getFacialPointRadius(), 16, 16);
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

    public void initComputation() {
        info.initFpUniverse();
    }

    public void initFpUniverse(List<FacialPoint> points) {
        info.initFpUniverse(points);
    }

    public void /*List<FacialPoint>*/ computeAllFacialPoints(ArrayList<Vector3f> centerPoints) {
        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);
        FeaturePointsUniverse fpu = info.getFpUniverse();
        fpu.findNose();
        //facialPoints = fpUniverse.getFacialPoints();
        fpu.findMouth();
        //facialPoints = fpUniverse.getFacialPoints();
        fpu.findEyes();
        //facialPoints = fpUniverse.getFacialPoints();
        PsDebug.getConsole().setVisible(false);

        if (centerPoints != null) {
            info.setFacialPoints(fpu.getSymmetryPlaneFPs(centerPoints));
        }

        //revert transformations computed during computing FP
        FpModel model = FPImportExport.instance().getFpModelFromFP(info.getFacialPoints(),
                info.getModels().get(0).getName());

        if (model != null) {          //if there was no problem with creating FP mode

            //apply reverse ICP transformation to computed FP
            List<Vector3f> modelFP = model.listOfFP();
            Icp.instance().reverseAllTransformations(info.getTransformations(), modelFP, true);
            Icp.instance().reverseAllTransformations(info.getTransformations(), info.getModels().get(0).getVerts(), true);

        }

        //return facialPoints;
    }

    public int getIndexOfSelectedPoint() {
        return info.getIndexOfSelectedPoint();
    }

    public void setIndexOfSelectedPoint(int indexOfSelectedPoint) {
        info.setIndexOfSelectedPoint(indexOfSelectedPoint);
    }

    public FacialPoint getFacialPoint(int index) {
        return info.getFacialPoints().get(index);
    }

    public void setModelIndex(int index, Model m) {
        info.getModels().set(index, m);
    }

    public void setColorOfPoint(float[] colorOfPoint) {
        info.setColorOfPoint(colorOfPoint);
    }

    public void setFacialPointRadius(float facialPointRadius) {
        info.setFacialPointRadius(facialPointRadius);
    }

    public FeaturePointsUniverse getFpUniverse() {
        return info.getFpUniverse();
    }

    public boolean isPaintHD() {
        return info.isPaintHD();
    }

    public void setPaintHD(boolean paintHD) {
        info.setPaintHD(paintHD);
    }

    public List<FacialPoint> getFacialPoints() {
        return info.getFacialPoints();
    }

    public void setFacialPoints(List<FacialPoint> facialPoints) {
        info.setFacialPoints(facialPoints);
    }

    public PApainting getPaPainting() {
        return info.getPaPainting();
    }

    public void setPaPainting(PApainting paPainting) {
        info.setPaPainting(paPainting);
    }

    public PApaintingInfo getPaInfo() {
        return info.getPaInfo();
    }

    public void setPaInfo(PApaintingInfo paInfo) {
        info.setPaInfo(paInfo);
    }

    public boolean isProcrustes() {
        return info.isProcrustes();
    }

    public void setProcrustes(boolean procrustes) {
        info.setProcrustes(procrustes);
    }

    public void setPrimaryColor(float[] color) {
        info.setPrimaryColor(color);
    }

    public void setSecondaryColor(float[] color2) {
        info.setSecondaryColor(color2);
    }

    public void setInnerSurfaceVisible(boolean selected) {
        innerSurfaceSolid = (selected == true) ? 1 : 0;
    }

    public void setUseGlyphs(boolean selected) {
        useGlyphs = selected;
    }

    public void setFogColor(float[] color3) {
        info.setFogColor(color3);
    }

    @Override
    public void reloadTextures() {
        List<Model> models = info.getModels();
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

    public void setFogVersion(int fogVersion) {
        this.fogVersion = fogVersion;
    }

    public void sampleModels(ArrayList<Model> models) {
        info.getSampleVetices().clear();
        info.getVerticesInfo().clear();
        float crossSize = 15f;
        //   Vector3f curvature = new Vector3f(0, 1, 1);

        for (Model m : models) {
            if (m != null) {
                Graph2 g = new Graph2(m);
                g.createGraph();
                int ind[][] = g.indicesFordDensityNormals(1.5f * crossSize);
                float vertices[] = new float[3 * ind[0].length];

                ArrayList<Vector3f> pricipalCurvatures = new ArrayList<>();
                for (int j = 0; j < ind[0].length; j++) {

                    Vector3f v = new Vector3f(m.getVerts().get(ind[0][j] - 1));
                    Vector3f e3 = new Vector3f(m.getVertexNormal(ind[0][j] - 1));
                    e3.normalize();
                    Vector3f e2 = IntersectionUtils.findLinePlaneIntersection(new Vector3f(0, 0, 0), e3, e3, v);
                    e2.sub(v);
                    e2.normalize();
                    if (e2.dot(e3) == 0) {
                        e2 = IntersectionUtils.findLinePlaneIntersection(new Vector3f(1, 0, 0), e3, e3, v);
                        e2.sub(v);
                        e2.normalize();
                    }
                    Vector3f e1 = new Vector3f();
                    e1.cross(e2, e3);
                    e1.normalize();

                    Vector4f A = new Vector4f();
                    int counter = 0;

                    float sizeThreshold = crossSize;
                    for (int i = 0; i < m.getFaces().getNumFaces(); i++) {
                        // for (int i = 0; i < m.getVerts().size(); i++) {
                        //     if(IntersectionUtils.getDistance(m.getVerts().get(i),v)<sizeThreshold){
                        if (MathUtils.instance().distancePoints(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[0] - 1), v) < sizeThreshold
                                || MathUtils.instance().distancePoints(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[1] - 1), v) < sizeThreshold
                                || MathUtils.instance().distancePoints(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[2] - 1), v) < sizeThreshold) {
                            Vector3f normal = new Vector3f();
                            normal.add(m.getNormals().get(m.getFaces().getFaceNormalIdxs(i)[0] - 1));
                            normal.add(m.getNormals().get(m.getFaces().getFaceNormalIdxs(i)[1] - 1));
                            normal.add(m.getNormals().get(m.getFaces().getFaceNormalIdxs(i)[2] - 1));
                            normal.normalize();

                            Vector3f middlePoint = new Vector3f();
                            middlePoint.add(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[0] - 1));
                            middlePoint.add(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[1] - 1));
                            middlePoint.add(m.getVerts().get(m.getFaces().getFaceVertIdxs(i)[2] - 1));
                            middlePoint.scale(1 / (3.0f));
                            /*       Vector3f normal = new Vector3f(m.getVertexNormal(i));
                             normal.normalize();
                             Vector3f middlePoint =new Vector3f(m.getVerts().get(i));*/

                            Vector3f p = new Vector3f();
                            p.sub(middlePoint, v);

                            float omega11 = e1.dot(normal) / e1.dot(p);
                            float omega21 = e2.dot(normal) / e1.dot(p);
                            float omega12 = e1.dot(normal) / e2.dot(p);
                            float omega22 = e2.dot(normal) / e2.dot(p);

                            counter++;
                            A.add(new Vector4f(omega11, omega21, omega12, omega22));
                        }
                    }
                    A.scale(1 / (float) counter);
                    // float determinant = A.x*A.w - A.y*A.z;
                    float b = -A.w - A.x;
                    float disc = b * b + 4 * (A.y * A.z);
                    float l1 = 0;
                    float l2 = 0;

                    if (disc >= 0) {
                        l1 = (float) (-b + Math.sqrt(disc) / 2);
                        l2 = (float) (-b - Math.sqrt(disc) / 2);
                        if (Math.abs(l1) < Math.abs(l2)) {
                            float t = l2;
                            l2 = l1;
                            l1 = t;
                        }
                    }
                    Vector4f E = new Vector4f(A.x - l1, A.y, A.z, A.w - l1);
                    float y = (E.x + E.z) / -(E.y + E.w);
                    Vector2f v1 = new Vector2f(1, y);
                    v1.normalize();

                    E = new Vector4f(A.x - l2, A.y, A.z, A.w - l2);
                    y = (E.x + E.z) / -(E.y + E.w);
                    Vector2f v2 = new Vector2f(1, y);
                    v2.normalize();

                    Vector3f e1Temp = new Vector3f(e1);
                    Vector3f e2Temp = new Vector3f(e2);
                    e1Temp.scale(v1.x);
                    e2Temp.scale(v1.y);
                    e1Temp.add(e2Temp);
                    e1Temp.normalize();

                    Vector3f pricipalCurvature = new Vector3f(e1Temp);

                    pricipalCurvatures.add(pricipalCurvature);
                }

                ArrayList<VertexInfo> vertexInfo = new ArrayList<>();

                for (int i = 0; i < m.getVerts().size(); i++) {
                    VertexInfo info = new VertexInfo();
                    float dist = Float.MAX_VALUE;
                    int closest = 0;
                    int t = 0;
                    for (int j = 0; j < ind[0].length; j++) {//
                        float d = (float) MathUtils.instance().distancePoints(m.getVerts().get(i), m.getVerts().get(ind[0][j] - 1));
                        if (d < dist) {
                            dist = d;
                            closest = ind[0][j] - 1;
                            t = j;
                        }
                    }
                    info.setSampleVertex(m.getVerts().get(closest));
                    info.setSampleNormal(m.getVertexNormal(closest));

                    Vector3f pricipalCurvature = pricipalCurvatures.get(t);//new Vector3f(0, 1, 0);
                    pricipalCurvature.normalize();
                    Vector3f s = new Vector3f();
                    s.cross(m.getVertexNormal(closest), pricipalCurvature);
                    s.normalize();

                    info.setSamplePrincipalCurvature(pricipalCurvature);
                    info.setSampleSecondaryCurvature(s);
                    vertexInfo.add(info);
                }
                info.getVerticesInfo().add(vertexInfo);

                info.getSampleVetices().add(vertices);
            } else {
                info.getVerticesInfo().add(null);
                info.getSampleVetices().add(null);

            }
        }

    }

    public void setContours(boolean contours) {
        info.setContours(contours);
    }

    private void initBuffers() {
        gl.glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
        int total_pixels = MAX_FRAMEBUFFER_WIDTH * MAX_FRAMEBUFFER_HEIGHT;

        gl.glGenTextures(1, depthTexture, 0);
        gl.glBindTexture(GL_TEXTURE_2D, depthTexture[0]);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowRatio * MAX_FRAMEBUFFER_WIDTH, shadowRatio * MAX_FRAMEBUFFER_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        gl.glGenFramebuffers(1, depthFBO, 0);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthFBO[0]);
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);

        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture[0], 0);

        if (gl.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");
        }
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        gl.glGenTextures(1, hpTexture, 0);
        gl.glBindTexture(GL_TEXTURE_2D, hpTexture[0]);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_R32UI, MAX_FRAMEBUFFER_WIDTH, MAX_FRAMEBUFFER_HEIGHT, 0, GL_RED_INTEGER, GL_UNSIGNED_INT, null);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        gl.glBindImageTexture(1, hpTexture[0], 0, true, 0, GL_READ_WRITE, GL_R32UI);

        gl.glGenBuffers(1, hpInitializer, 0);
        gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, hpInitializer[0]);
        gl.glBufferData(GL_PIXEL_UNPACK_BUFFER, total_pixels * 4, null, GL_STATIC_DRAW);

        ByteBuffer data = gl.glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY);
        byte aData[] = new byte[data.capacity()];
        Arrays.fill(aData, (byte) 0x00);
        data.rewind();
        data.put(aData);
        gl.glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);

        gl.glGenBuffers(1, acBuffer, 0);
        gl.glBindBuffer(GL_ATOMIC_COUNTER_BUFFER, acBuffer[0]);
        gl.glBufferData(GL_ATOMIC_COUNTER_BUFFER, 4, null, GL_DYNAMIC_COPY);

        gl.glGenBuffers(1, fsBuffer, 0);
        gl.glBindBuffer(GL_TEXTURE_BUFFER, fsBuffer[0]);
        gl.glBufferData(GL_TEXTURE_BUFFER, 2 * total_pixels * 16, null, GL_DYNAMIC_COPY); // Updated often by GPU

        gl.glGenTextures(1, fsTexture, 0);
        gl.glBindTexture(GL_TEXTURE_BUFFER, fsTexture[0]);
        gl.glTexParameteri(GL_TEXTURE_BUFFER, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_BUFFER, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA32UI, fsBuffer[0]);
        gl.glBindTexture(GL_TEXTURE_BUFFER, 0);

        gl.glBindImageTexture(2, fsTexture[0], 0, false, 0, GL_READ_WRITE, GL_RGBA32UI);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private void initShaders() {
        String SMvertexShaderList = null;
        String SMfragmentShaderList = null;
        String vertexShaderList = null;
        String fragmentShaderList = null;
        String vertexShaderOIT = null;
        String fragmentShaderOIT = null;
        String fragmentShaderFinal = null;
        String colorMapvertexShader = null;
        String colorMapfragmentShader = null;
        String colorMapShadeFragmentShader = null;

        try {
            SMvertexShaderList = readFile(ComparisonGLEventListener.class
                    .getResourceAsStream("shaders/ShadowMapVS.glsl"));
            SMfragmentShaderList
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/ShadowMapFS.glsl"));

            vertexShaderList
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/FirstPassVS.glsl"));
            fragmentShaderList
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/FirstPassFS.glsl"));

            vertexShaderOIT
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/OITresultVS.glsl"));
            fragmentShaderOIT
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/OITresultFS.glsl"));

            fragmentShaderFinal
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/FinalPassFS.glsl"));

            colorMapvertexShader
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/ColormapVS.glsl"));
            colorMapfragmentShader
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/ColormapFS.glsl"));

            colorMapShadeFragmentShader
                    = readFile(ComparisonGLEventListener.class
                            .getResourceAsStream("shaders/ColormapShadeFS.glsl"));

        } catch (IOException ex) {
            Logger.getLogger(ComparisonGLEventListener.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        int vertexShaderSMId = initShader(gl, GL_VERTEX_SHADER, SMvertexShaderList);
        int fragmentShaderSMId = initShader(gl, GL_FRAGMENT_SHADER, SMfragmentShaderList);

        int vertexShaderId = initShader(gl, GL_VERTEX_SHADER, vertexShaderList);
        int fragmentShaderId = initShader(gl, GL_FRAGMENT_SHADER, fragmentShaderList);

        int vertexShaderOITId = initShader(gl, GL_VERTEX_SHADER, vertexShaderOIT);
        int fragmentShaderOITId = initShader(gl, GL_FRAGMENT_SHADER, fragmentShaderOIT);

        int fragmentShaderFinalId = initShader(gl, GL_FRAGMENT_SHADER, fragmentShaderFinal);

        int colorMapvertexShaderID = initShader(gl, GL_VERTEX_SHADER, colorMapvertexShader);
        int colorMapfragmentShaderID = initShader(gl, GL_FRAGMENT_SHADER, colorMapfragmentShader);

        int colorMapShadeFragmentShaderID = initShader(gl, GL_FRAGMENT_SHADER, colorMapShadeFragmentShader);

        shadowMapShadersId = gl.glCreateProgram();
        listCreationShadersId = gl.glCreateProgram();
        OITShadersId = gl.glCreateProgram();
        FinalShadersId = gl.glCreateProgram();
        ColorMapShadersId = gl.glCreateProgram();
        ColorMapReductionShadersId = gl.glCreateProgram();

        gl.glAttachShader(shadowMapShadersId, vertexShaderSMId);
        gl.glAttachShader(shadowMapShadersId, fragmentShaderSMId);
        gl.glLinkProgram(shadowMapShadersId);

        checkProgramStatus(gl, shadowMapShadersId, 3);
        shadowVertexListUniform = gl.glGetUniformLocation(shadowMapShadersId, "sampleVertices");
        shadowNormalListUniform = gl.glGetUniformLocation(shadowMapShadersId, "sampleNormals");
        shadowPrincipalListUniform = gl.glGetUniformLocation(shadowMapShadersId, "samplePrincipalCurvature");
        shadowSecondaryListUniform = gl.glGetUniformLocation(shadowMapShadersId, "sampleSecondaryCurvature");

        gl.glAttachShader(listCreationShadersId, vertexShaderId);
        gl.glAttachShader(listCreationShadersId, fragmentShaderId);
        gl.glLinkProgram(listCreationShadersId);

        checkProgramStatus(gl, listCreationShadersId, 3);
        modelNumUniform = gl.glGetUniformLocation(listCreationShadersId, "modelNumber");
        modelVertexListUniform = gl.glGetUniformLocation(listCreationShadersId, "sampleVertices");
        modelNormalListUniform = gl.glGetUniformLocation(listCreationShadersId, "sampleNormals");
        modelPrincipalListUniform = gl.glGetUniformLocation(listCreationShadersId, "samplePrincipalCurvature");
        modelSecondaryListUniform = gl.glGetUniformLocation(listCreationShadersId, "sampleSecondaryCurvature");

        gl.glAttachShader(OITShadersId, vertexShaderOITId);
        gl.glAttachShader(OITShadersId, fragmentShaderOITId);
        gl.glLinkProgram(OITShadersId);

        checkProgramStatus(gl, OITShadersId, 3);

        innSurfSolidUniform = gl.glGetUniformLocation(OITShadersId, "innerSurfaceSolid");
        minZUniform = gl.glGetUniformLocation(OITShadersId, "minZ");
        maxZUniform = gl.glGetUniformLocation(OITShadersId, "maxZ");
        fogUniform = gl.glGetUniformLocation(OITShadersId, "fogVersion");
        fogColorUniform = gl.glGetUniformLocation(OITShadersId, "fogColor");

        gl.glAttachShader(FinalShadersId, vertexShaderOITId);
        gl.glAttachShader(FinalShadersId, fragmentShaderFinalId);
        gl.glLinkProgram(FinalShadersId);

        checkProgramStatus(gl, FinalShadersId, 3);

        gl.glAttachShader(ColorMapShadersId, colorMapvertexShaderID);
        gl.glAttachShader(ColorMapShadersId, colorMapfragmentShaderID);
        gl.glLinkProgram(ColorMapShadersId);

        curDistanceAttrib = gl.glGetAttribLocation(ColorMapShadersId, "cDistance");
        selectionUniform = gl.glGetUniformLocation(ColorMapShadersId, "selectionRectangle");
        selectionCameraUbniform = gl.glGetUniformLocation(ColorMapShadersId, "selectionCameraPosition");
        selectionTypeUniform = gl.glGetUniformLocation(ColorMapShadersId, "selectionType");

        checkProgramStatus(gl, ColorMapShadersId, 3);

        gl.glAttachShader(ColorMapReductionShadersId, vertexShaderOITId);
        gl.glAttachShader(ColorMapReductionShadersId, colorMapShadeFragmentShaderID);
        gl.glLinkProgram(ColorMapReductionShadersId);

        colorSchemeUniform = gl.glGetUniformLocation(ColorMapReductionShadersId, "colorScheme");
        minDistanceUniform = gl.glGetUniformLocation(ColorMapReductionShadersId, "minDistance");
        maxDistanceUniform = gl.glGetUniformLocation(ColorMapReductionShadersId, "maxDistance");
        globalMaxDistanceUniform = gl.glGetUniformLocation(ColorMapReductionShadersId, "maxThreshDistance");
        globalMinDistanceUniform = gl.glGetUniformLocation(ColorMapReductionShadersId, "minThreshDistance");

        checkProgramStatus(gl, ColorMapReductionShadersId, 3);

    }

    /**
     * Nacita obsah shaderu do jedneho retezca.
     *
     * @param stream Input stream
     * @return obsah shaderu ulozeny v jednom retezci
     */
    protected String readFile(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();

        String ls = System.getProperty("line.separator");
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

    /**
     * Inicializuje Vertex/Fragment shader.
     *
     * @param gl graficky kontext
     * @param shaderType typ shaderu
     * @param shaderString obsah shaderu
     * @return ID shaderu
     */
    protected int initShader(GL2 gl, int shaderType, String shaderString) {
        int shaderId = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderId, 1, new String[]{shaderString}, new int[]{shaderString.length()}, 0);
        gl.glCompileShader(shaderId);
        checkShaderStatus(gl, shaderId, 2);

        return shaderId;

    }

    /**
     * Skontroluje status kompilacie shaderu.
     *
     * @param gl graficky kontext
     * @param shaderId ID shaderu
     * @param status pola typu int obsahujici jediny prvek, ktory predstavuje
     * status operace
     * @param errorCode navratova hodnota pouzita pri ukonceni programu
     */
    private void checkShaderStatus(GL2 gl, int shaderId, int errorCode) {

        int[] compileStatus = new int[1];
        gl.glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);

        if (GL_FALSE == compileStatus[0]) {
            int[] infoLogLength = new int[1];
            gl.glGetShaderiv(shaderId, GL_INFO_LOG_LENGTH, infoLogLength, 0);

            byte[] infoLogBytes = new byte[infoLogLength[0]];
            gl.glGetShaderInfoLog(shaderId, infoLogLength[0], infoLogLength, 0, infoLogBytes, 0);

            String error = new String(infoLogBytes, 0, infoLogLength[0]);

            System.err.println(error);
        }
    }

    /**
     * Skontroluje status linkovania programu.
     *
     * @param gl graficky kontext
     * @param programId ID programu
     * @param status pole typu int obsahujici jediny prvek, ktery predstavuje
     * status operacie
     * @param errorCode navratova hodnota pouzita pri ukonceni programu
     */
    protected void checkProgramStatus(GL2 gl, int programId, int errorCode) {

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);

        if (GL_FALSE == linkStatus[0]) {
            int[] infoLogLength = new int[1];
            gl.glGetProgramiv(programId, GL_INFO_LOG_LENGTH, infoLogLength, 0);

            byte[] infoLogBytes = new byte[infoLogLength[0]];
            gl.glGetProgramInfoLog(programId, infoLogLength[0], infoLogLength, 0, infoLogBytes, 0);

            String error = new String(infoLogBytes, 0, infoLogLength[0]);

            System.out.println(error);
            System.err.println(error);
        }
    }

    public Vector3f getXaxis() {
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

    public Vector3f getYaxis() {
        Vector3f yAxis = new Vector3f((float) xUpPosition, (float) yUpPosition, (float) zUpPosition);
        float length = (float) Math.sqrt(yAxis.getX() * yAxis.getX() + yAxis.getY() * yAxis.getY() + yAxis.getZ() * yAxis.getZ());
        yAxis.setX(yAxis.getX() / length);
        yAxis.setY(yAxis.getY() / length);
        yAxis.setZ(yAxis.getZ() / length);

        return yAxis;
    }

    public void setGizmo(Model model) {
        gizmo = model;
        originalGizmo = model.copy();
        rotateGizmo();
    }

    public void setSelectionStart(Point point) {
        if (point != null) {
            selectionStart = point;
            selectionCube[0] = setSelection3Dpoint(point.x, point.y);
            selectionCube[1] = setSelection3Dpoint(point.x, point.y);
            selectionCube[2] = setSelection3Dpoint(point.x, point.y);
            selectionCube[3] = setSelection3Dpoint(point.x, point.y);
            selectionCube[4] = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);
            info.getHdInfo().setIsSelection(false);
        }
    }

    public void setSelectionEnd(Point point, int width, int height) {
        if (point != null && selectionStart != null) {
            selectionCube[1] = setSelection3Dpoint(point.x, selectionStart.y);
            selectionCube[2] = setSelection3Dpoint(point.x, point.y);
            selectionCube[3] = setSelection3Dpoint(selectionStart.x, point.y);
            selectionEnd = point;
            info.getHdInfo().setIsSelection(false);
            selectionCube[4] = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);
        }

    }

    private Vector3f setSelection3Dpoint(int x, int y) {
        Vector3f p;// = checkPointInMesh(x, y);
        //  if (p == null) {
        ModelSelector m = new ModelSelector(glu);
        m.castRay(x, y, viewport, modelViewMatrix, projectionMatrix);
        Vector3f ln = new Vector3f(m.getRayEndPoint());
        ln.sub(m.getRayStartPoint());
        Vector3f cn = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);
        Vector3f ct = new Vector3f((float) xCenter, (float) yCenter, (float) zCenter);
        cn.sub(ct);

        p = m.findLinePlaneIntersection(m.getRayEndPoint(), ln, cn, ct);
        //p = m.getRayEndPoint();

        //}
        return p;
    }

    public void setSelectionBox(Vector3f[] selectionCube) {
        this.selectionCube = selectionCube;
        selectionStart = new Point();
        selectionEnd = new Point();
        setSelectionFinished(true);
        // selectionCube[4] = new Vector3f((float) xCameraPosition, (float) yCameraPosition, (float) zCameraPosition);

    }

    public void setSelectionFinished(boolean selectionFinished) {
        info.getHdInfo().setIsSelection(selectionFinished);
        info.getHdInfo().setSelectionCone(selectionCube);
        info.getHdInfo().setIsRecomputed(false);

    }

    public void clearSelection() {
        if (info.getHdInfo() != null) {
            info.getHdInfo().setIsSelection(false);
            info.getHdInfo().setIsRecomputed(false);
        }

        selectionStart = null;
        selectionEnd = null;
        selectionCube = new Vector3f[5];

    }

    public int getModelSelectionIndex() {
        return modelSelectionIndex;
    }

    public void setModelSelectionIndex(int modelSelectionIndex) {
        this.modelSelectionIndex = modelSelectionIndex;
    }

    private void paintSelectionEllipse() {
        Vector3f axe = new Vector3f(selectionCube[4]);
        axe.sub(new Vector3f((float) xCenter, (float) yCenter, (float) zCenter));
        axe.normalize();

        double angle = Math.acos(axe.dot(new Vector3f(0, 0, 1)));
        axe.cross(axe, new Vector3f(0, 0, 1));
        axe.normalize();

        Vector3f p1;
        Vector3f p2;
        if (angle != 0) {
            p1 = rotateAroundAxe(selectionCube[0], axe, angle);
            p2 = rotateAroundAxe(selectionCube[2], axe, angle);
        } else {
            p1 = new Vector3f(selectionCube[0]);
            p2 = new Vector3f(selectionCube[2]);
        }

        float xradius = (p1.x - p2.x) / 2;
        float yradius = (p1.y - p2.y) / 2;

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL_LIGHTING);
        gl.glLineWidth(1);
        gl.glBegin(GL.GL_LINE_LOOP);

        for (int i = 0; i < 360; i++) {
            //convert degrees into radians
            double degInRad = Math.toRadians(i);
            Vector3f v = new Vector3f((float) (Math.cos(degInRad) * Math.abs(xradius) + p1.x - xradius), (float) (Math.sin(degInRad) * Math.abs(yradius) + p1.y - yradius), 0);

            if (angle != 0) {
                Vector3f w = rotateAroundAxe(v, axe, -angle);
                gl.glVertex3d(w.x, w.y, w.z);
            } else {
                gl.glVertex3d(v.x, v.y, v.z);
            }

            // Math.cos(degInRad) * Math.abs(zradius) + selectionCube[0].z - zradius
        }
        gl.glEnd();

        /*      gl.glBegin(GL.GL_LINE_LOOP);

         for (int i = 0; i < 3; i++) {
         gl.glVertex3f(selectionCube[i].getX(), selectionCube[i].getY(), selectionCube[i].getZ());
         gl.glVertex3f(selectionCube[i + 1].getX(), selectionCube[i + 1].getY(), selectionCube[i + 1].getZ());

         }

         gl.glEnd();*/
        gl.glEnable(GL_LIGHTING);
    }

    private void paintSelectionRectangle() {
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL_LIGHTING);
        gl.glLineWidth(1);
        gl.glBegin(GL.GL_LINE_LOOP);

        for (int i = 0; i < 3; i++) {
            gl.glVertex3f(selectionCube[i].getX(), selectionCube[i].getY(), selectionCube[i].getZ());
            gl.glVertex3f(selectionCube[i + 1].getX(), selectionCube[i + 1].getY(), selectionCube[i + 1].getZ());

        }

        gl.glEnd();
        gl.glEnable(GL_LIGHTING);
    }

    public void setTransformations(List<ICPTransformation> trans) {
        info.setTransformations(trans);
    }

    public GL2 getGLContext() {
        return gl;
    }

}
