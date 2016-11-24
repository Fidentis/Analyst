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
        removeDuplicateVertices(m.getFaces(), m.getVerts());
        
        return m;
    }

    private void removeDuplicateVertices(Faces faces, ArrayList<Vector3f> verts) {
        HashMap<Integer, Integer> dict = new HashMap<>();       //for each vertex index remember whether it was replaced or not, <index of point, index of point it was replaced by>
        HashMap<Vector3f, Integer> mainPoint = new HashMap<>(); //first occurance of given point <unique point, first index of occurance>

        for (int i = 0; i < verts.size(); i++) {
            Vector3f v = verts.get(i);
            if (mainPoint.containsKey(v)) {
                dict.put(i, mainPoint.get(v));
            } else {
                mainPoint.put(v, i);
                dict.put(i, i);
            }
        }

        //TODO remove duplicate vertices from the list
        for (int i = 0; i < faces.getNumFaces(); i++) {
            int[] indexes = faces.getFaceVertIdxs(i);
            for (int j = 0; j < indexes.length; j++) {
                indexes[j] = dict.get(indexes[j] - 1) + 1;
            }

            faces.setFacesVertIdxs(i, indexes);
        }

    }


    
}
