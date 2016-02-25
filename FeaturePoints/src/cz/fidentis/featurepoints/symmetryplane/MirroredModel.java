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
    
    private static Model registrationMirroredModel (Model model, Model mirroredModel){       
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

    private static Vector3f computeCentroid(List<Vector3f> verts) {
        float x = 0, y = 0, z = 0;

        for (Vector3f p : verts) {
            x = x + p.getX();
            y = y + p.getY();
            z = z + p.getZ();
        }

        x = x / verts.size();
        y = y / verts.size();
        z = z / verts.size();

        return new Vector3f(x, y, z);
    }
}
