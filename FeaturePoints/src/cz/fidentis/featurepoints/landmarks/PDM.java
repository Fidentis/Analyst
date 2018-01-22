/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.landmarks;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FpModel;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author Rasto1
 */
public class PDM {
    public PDM(){
        
    }
    
    // create mean model from training shapes on input
    public static FpModel trainigModel(List<FpModel> trainingShapes) {

        FpModel meanShape;
        meanShape = trainingShapes.get(0);
        
        for (int i = 1; i < trainingShapes.size(); i++) {
            
            List<FacialPoint> values = trainingShapes.get(i).getFacialPoints();

            for (int j = 0; j < values.size(); j++) {
                Vector3f point = meanShape.getFacialPoints().get(j).getPosition();
                point.x += values.get(j).getPosition().x;
                point.y += values.get(j).getPosition().y;
                point.z += values.get(j).getPosition().z;

                meanShape.getFacialPoints().get(j).setCoords(point);
            }

        }

        for (int j = 0; j < meanShape.getPointsNumber(); j++) {
            Vector3f point = meanShape.getFacialPoints().get(j).getPosition();
            point.x /= trainingShapes.size();
            point.y /= trainingShapes.size();
            point.z /= trainingShapes.size();

            meanShape.getFacialPoints().get(j).setCoords(point);
        }

        return meanShape;
    }

}
