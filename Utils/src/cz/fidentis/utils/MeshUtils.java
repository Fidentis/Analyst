/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utils;


import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Zuzana Ferkova
 */
public class MeshUtils {
    private static MeshUtils instance;
    
    private MeshUtils(){};
    
    public static MeshUtils instance(){
        if(instance == null){
            instance = new MeshUtils();
        }
        
        return instance;
    }
    
    
    /**
     * Computes center of the mesh and returns it.
     *
     * @param mesh - point cloud representing mesh
     * @return array of float containing x, y and z coordinate of the center of mesh
     */
    public float[] computeCentroid(List<Vector3f> mesh) {
        float x = 0;
        float y = 0;
        float z = 0;
        for (Vector3f p : mesh) {
            x = x + p.getX();
            y = y + p.getY();
            z = z + p.getZ();
        }
        x = x / mesh.size();
        y = y / mesh.size();
        z = z / mesh.size();
        return new float[]{x, y, z};
    }
    
    /**
     * Creates mirrored model of given model
     * 
     * @param model - model to mirror
     * @return mirrored model
     */
    public Model getMirroredModel(Model model){
        Model mirroredModel = model.copy();
        float[] centroid = computeCentroid(mirroredModel.getVerts());
        
        for (Vector3f vert : mirroredModel.getVerts()) {
            vert.setX(centroid[0] - vert.x);
        }

        return mirroredModel;
    }
    
    public Model removeDuplicateVertices(Model m){
        HashMap<Integer, Integer> dict = new HashMap<>();       //for each vertex index remember whether it was replaced or not, <index of point, index of point it was replaced by>
        HashMap<Vector3f, Integer> mainPoint = new HashMap<>(); //first occurance of given point <unique point, first index of occurance>
        ArrayList<Vector3f> verts = m.getVerts();
        ArrayList<Vector3f> norms = m.getNormals();
        ArrayList<Vector3f> txts = m.getTexCoords();
        ArrayList<Vector3f> newVerts;
        ArrayList<Vector3f> newNorms;
        ArrayList<Vector3f> newTxts;
        Faces faces = m.getFaces();

        //fill out dict and mainPoint to see which triangles should be deleted
        for (int i = 0; i < verts.size(); i++) {
            Vector3f v = verts.get(i);
            if (mainPoint.containsKey(v)) {
                dict.put(i, mainPoint.get(v));
            } else {
                mainPoint.put(v, i);
                dict.put(i, i);
            }
        }

        //remove duplicate vertices from the list
        newVerts = new ArrayList<>(mainPoint.size());
        newNorms = new ArrayList<>(mainPoint.size());
        newTxts = new ArrayList<>(mainPoint.size());
        //get new indices of from new lists
        mainPoint = new HashMap<>();
        
        for(int i = 0; i < verts.size(); i++){
            if(dict.get(i) != i)        //skip duplicates
                continue;
            
            newVerts.add(verts.get(i));
            mainPoint.put(verts.get(i), newVerts.size() - 1);
            
            newNorms.add(norms.get(i));
            newTxts.add(txts.get(i));
            
        }
        
        m.setVerts(verts);
        m.setNormals(norms);
        m.setTextures(txts);
        
        
        for (int i = 0; i < faces.getNumFaces(); i++) {
            //triangle position
            int[] indexes = faces.getFaceVertIdxs(i);
            swapToNewIndices(indexes, mainPoint, verts);
            faces.setFacesVertIdxs(i, indexes);
            
            
            //triangle normals
            indexes = faces.getFaceNormalIdxs(i);
            swapToNewIndices(indexes, mainPoint, verts);  
            faces.setFacesNormIdxs(i, indexes);
            
            //TODO remove textures
            //triangle textures
            /*indexes = faces.getFaceTexIdxs(i);
            swapToNewIndices(indexes, mainPoint, verts);     
            faces.setFacesTexIdxs(i, indexes);*/
        }
        
        return m;
    }    

    private void swapToNewIndices(int[] indexes, HashMap<Vector3f, Integer> mainPoint, ArrayList<Vector3f> verts) {
        for (int j = 0; j < indexes.length; j++) {
            //get vertex from original vertex array and it's new index
            Vector3f v = verts.get(indexes[j] - 1);
            int index = mainPoint.get(v) + 1;
            indexes[j] = index;
        }
    }
}
