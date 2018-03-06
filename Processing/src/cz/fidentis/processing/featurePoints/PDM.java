/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.landmarkParser.CSVparser;
import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.ArrayList;
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
    public static FpModel trainigModel(List<FpModel> trainingShapes, String name) throws IOException {

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
        
        CSVparser pars = new CSVparser();
        
        List<FpModel> fpModels = new ArrayList<>();
        
        fpModels.add(meanShape);
        
        for(int i = 0; i < trainingShapes.size(); i++){
            fpModels.add(trainingShapes.get(i));
        }
        
        pars.save(fpModels,new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + "trainingModels" + separatorChar + name + ".csv");

        return meanShape;
    }

}
