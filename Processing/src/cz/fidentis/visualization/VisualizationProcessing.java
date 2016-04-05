/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualization;

import cz.fidentis.model.Graph2;
import cz.fidentis.model.Model;
import cz.fidentis.model.VertexInfo;
import cz.fidentis.utils.IntersectionUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.visualisation.ComparisonListenerInfo;
import java.util.ArrayList;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author xferkova
 */
public class VisualizationProcessing {
    private static VisualizationProcessing instance;
    
    private VisualizationProcessing(){};
    
    public static VisualizationProcessing instance(){
        if(instance == null){
            instance = new VisualizationProcessing();
        }
        
        return instance;
    }
    
    public void sampleModels(ComparisonListenerInfo ci) {
        ArrayList<Model> models = ci.getModels();
        ci.getSampleVetices().clear();
        ci.getVerticesInfo().clear();
        float crossSize = 15f;

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
                ci.getVerticesInfo().add(vertexInfo);

                ci.getSampleVetices().add(vertices);
            } else {
                ci.getVerticesInfo().add(null);
                ci.getSampleVetices().add(null);
            }
        }

    }
}
