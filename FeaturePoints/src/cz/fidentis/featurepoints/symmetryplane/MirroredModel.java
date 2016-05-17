/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.symmetryplane;

import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Galvanizze
 */
public class MirroredModel {

    //mirrored model method moved to MeshUtils
    
    /**
     * Computes center of the mesh and returns it.
     *
     * @param mesh - point cloud representing mesh
     * @return array of float containing x, y and z coordinate of the center of mesh
     */
    private static float[] computeCentroid(List<Vector3f> mesh) {
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
    public static Model getMirroredModel(Model model){
        Model mirroredModel = model.copy();
        float[] centroid = computeCentroid(mirroredModel.getVerts());
        
        for (Vector3f vert : mirroredModel.getVerts()) {
            vert.setX(centroid[0] - vert.x);
        }

        return mirroredModel;
    }
    
    public static ArrayList<Vector3f> getCenterPoints(Model model, Model mirroredModel){
        ArrayList<Vector3f> centerPoints = new ArrayList<>();
        ArrayList<Vector3f> verts = model.getVerts();
        ArrayList<Vector3f> mirroredVerts = mirroredModel.getVerts();
        
        for (int i = 0; i < model.getVerts().size(); i++) {
            Vector3f centerPoint = new Vector3f( (verts.get(i).x + mirroredVerts.get(i).x ) / 2,
                                                 (verts.get(i).y + mirroredVerts.get(i).y ) / 2,
                                                 (verts.get(i).z + mirroredVerts.get(i).z ) / 2 );
            centerPoints.add(centerPoint);
        }
        
        return centerPoints;
    }
}
