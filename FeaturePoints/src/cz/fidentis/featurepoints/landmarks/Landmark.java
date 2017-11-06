/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.landmarks;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.FpModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import javax.vecmath.Vector3f;

/**
 *
 * @author Rasto1
 */
public class Landmark {
    public Landmark(){
        
    }
    
    // create mean model from training shapes on input
    public static FpModel trainigModel(List<FpModel> trainingShapes, JProgressBar progressBar) {

        FpModel meanShape;
        meanShape = trainingShapes.get(0);
        progressBar.setValue(0);
        
        int progress = 100/trainingShapes.size();
        int current = progress;
        for (int i = 1; i < trainingShapes.size(); i++) {
            current += progress;
            progressBar.setValue(current);
            
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
        progressBar.setValue(100);

        return meanShape;
    }

}
