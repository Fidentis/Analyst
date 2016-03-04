/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java;

import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.utils.MathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Vector3f;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author xferkova
 */
public class KdTreeIndexedTest {
    private static String modelPath = "..\\resources\\test_face_01m.obj";
    private static Model loadedModel;
    
    public KdTreeIndexedTest(){
        ModelLoader ml = new ModelLoader();
        loadedModel = ml.loadModel(new File(modelPath), false, Boolean.TRUE);
    }
    
    
    @Test
    public void testPut(){
        Vector3f p = new Vector3f(0.1f, 0.5f, 0.7f);
        
        List<Vector3f> points = new LinkedList<>();
        points.add(p);
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        assertTrue(tree.containPoint(p));
    }
    
    @Test
    public void testPutNothing(){
        Vector3f p = new Vector3f(0.1f, 0.5f, 0.7f);
        List<Vector3f> points = new LinkedList<>();
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        assertTrue(!tree.containPoint(p));
    }
    
    private Vector3f findClosestSequential(List<Vector3f> points, Vector3f p){
        if(points.isEmpty()){
            return null;
        }
        
        Vector3f closest = points.get(0);
        float dis = (float) MathUtils.instance().distancePoints(closest, p);
        
        for(Vector3f cl : points){
            if((float) MathUtils.instance().distancePoints(cl, p) < dis){
                dis = (float) MathUtils.instance().distancePoints(cl, p);
                closest = cl;
            }
        }
        
        return closest;
    }
    
    @Test
    public void testFindClosestAlreadyIn(){       
        List<Vector3f> points = new LinkedList<>();
        
        for(int i = 0; i < 10; i++){
            points.add(new Vector3f(0.1f * i, 0.5f * i, 0.7f * i));
        }
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        Random r = new Random();
        
        Vector3f p = points.get(r.nextInt(points.size()));
        Vector3f found = tree.nearestNeighbour(p);
        
        assertTrue(found.equals(p));
        assertTrue(MathUtils.instance().distancePoints(found, p) == 0.0f); 
    }
    
    @Test
    public void testFindClosestNotInTree(){       
        List<Vector3f> points = new LinkedList<>();
        
        for(int i = 0; i < 10; i++){
            points.add(new Vector3f(0.1f * i, 0.5f * i, 0.7f * i));
        }
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Vector3f p = new Vector3f(2.5f, 0.3f, 1.2f);
        Vector3f found = tree.nearestNeighbour(p);
        
        Vector3f seq = findClosestSequential(points, p);
        
        assertTrue(found.equals(seq));
        assertTrue(MathUtils.instance().distancePoints(found, p) == MathUtils.instance().distancePoints(seq, p)); 
    }
    
    @Test
    public void testFindClosestModelAlreadyInTree(){
        List<Vector3f> points = loadedModel.getVerts();
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Random r = new Random();
        
        Vector3f p = points.get(r.nextInt(points.size()));
        Vector3f found = tree.nearestNeighbour(p);
        
        assertTrue(found.equals(p));
        assertTrue(MathUtils.instance().distancePoints(found, p) == 0.0f);         
    }
    
    @Test
    public void testFindClosestModelNotInTree(){
        List<Vector3f> points = loadedModel.getVerts();
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Vector3f p = new Vector3f(2.5f, 0.3f, 1.2f);
        Vector3f found = tree.nearestNeighbour(p);
        
        Vector3f seq = findClosestSequential(points, p);
        
        assertTrue(found.equals(seq));
        assertTrue(MathUtils.instance().distancePoints(found, p) == MathUtils.instance().distancePoints(seq, p)); 
    }
    
    @Test
    public void testIndexFound(){
        List<Vector3f> points = new ArrayList<>();
        
        for(int i = 0; i < 10; i++){
            points.add(new Vector3f(0.1f * i, 0.5f * i, 0.7f * i));
        }
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        Random r = new Random();
        
        Vector3f p = points.get(r.nextInt(points.size()));
        int found = tree.nearestIndex(p);
        
        assertTrue(points.get(found).equals(p));
        assertTrue(MathUtils.instance().distancePoints(points.get(found), p) == 0.0f); 
    }
    
    @Test
    public void testFindClosestIndexNotInTree(){       
        List<Vector3f> points = new ArrayList<>();
        
        for(int i = 0; i < 10; i++){
            points.add(new Vector3f(0.1f * i, 0.5f * i, 0.7f * i));
        }
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Vector3f p = new Vector3f(2.5f, 0.3f, 1.2f);
        int found = tree.nearestIndex(p);
        
        Vector3f seq = findClosestSequential(points, p);
        int seqIndex = points.indexOf(seq);
        
        assertTrue(found == seqIndex);
        assertTrue(MathUtils.instance().distancePoints(points.get(found), p) == MathUtils.instance().distancePoints(points.get(seqIndex), p)); 
    }
    
    @Test
    public void testFindClosestIndexModelAlreadyInTree(){
        List<Vector3f> points = loadedModel.getVerts();
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Random r = new Random();
        
        Vector3f p = points.get(r.nextInt(points.size()));
        int found = tree.nearestIndex(p);
        
        assertTrue(points.get(found).equals(p));
        assertTrue(MathUtils.instance().distancePoints(points.get(found), p) == 0.0f);       
    }
    
    @Test
    public void testFindClosestIndexModelNotInTree(){
        List<Vector3f> points = loadedModel.getVerts();
        
        KdTreeIndexed tree = new KdTreeIndexed(points);
        
        Vector3f p = new Vector3f(2.5f, 0.3f, 1.2f);
        int found = tree.nearestIndex(p);
        
        Vector3f seq = findClosestSequential(points, p);
        int seqIndex = points.indexOf(seq);
        
        assertTrue(found == seqIndex);
        assertTrue(MathUtils.instance().distancePoints(points.get(found), p) == MathUtils.instance().distancePoints(points.get(seqIndex), p)); 
    }
    
}
