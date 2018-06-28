/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.model.Model;
import cz.fidentis.utils.MathUtils;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.openide.util.Exceptions;

/**
 *
 * @author xferkova
 */
public class TextureLandmarks {
    
    private static TextureLandmarks instance;
    private static String LANDMARK_DETECTOR;
    private static String SHAPE_PREDICTOR;
    private static String PYTHON_PATH;
    private static File SAVE_PATH;

    private TextureLandmarks() {
        try {
            String resourcePath = new java.io.File(".").getCanonicalPath() + File.separator + "models" + File.separator + "resources" + File.separator;
            
            SAVE_PATH = new File(resourcePath + "trainingModels" + File.separator + "shape_predictor");
            LANDMARK_DETECTOR =  SAVE_PATH + File.separator + "landmark_detection_forJava.py";
            SHAPE_PREDICTOR = SAVE_PATH + File.separator + "shape_predictor_68_face_landmarks.dat";
            PYTHON_PATH = resourcePath + "python-3.6.5" + File.separator + "python.exe";            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static TextureLandmarks instance(){
        if(instance == null)
            instance = new TextureLandmarks();
        
        return instance;
    }
    
    public String detectTextureLandmarks(File texturePath){
        String savedFile = null;
        
        try {
            savedFile = SAVE_PATH.getAbsolutePath() + File.separator + texturePath.getName() + ".txt";
            ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_PATH,
                    LANDMARK_DETECTOR,
                    SHAPE_PREDICTOR,
                    texturePath.getAbsolutePath());

            pb.redirectErrorStream(true);
            pb.directory(SAVE_PATH);

            Process p = pb.start();
           
            System.out.println(LANDMARK_DETECTOR);
            System.out.println(SHAPE_PREDICTOR);
            
            System.out.println(PYTHON_PATH);
            p.waitFor();
            
            
        } catch (InterruptedException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return savedFile;
    }
    
    public FpModel convert2Dto3D(String landmarkPath, Model m) throws ParserConfigurationException, TransformerException{
         if (!landmarkPath.endsWith(".txt")) {
             return null;
         }
         
         KDTreeIndexed txtTree = new KDTreeIndexed(m.getTexCoords());
         HashMap<Integer, Integer> vertTxtCorrespondence = buildTextureVertexCorrespondence(m);

         List<Vector2f> currentLandmarks = parseTxtLandmarks(new File(landmarkPath));
         List<FacialPoint> projectedLandmarks = new LinkedList<>();

         for (int j = 0; j < currentLandmarks.size(); j++) {
             FacialPointType type = getValidFidoFPType(j);
             Vector3f currentlm = new Vector3f(currentLandmarks.get(j).x, currentLandmarks.get(j).y, -5.0f);

             if (type == FacialPointType.undefined) {
                 continue;
             }
             
             int closestIndex = txtTree.nearestIndex(currentlm);
             int correspondIx = vertTxtCorrespondence.get(closestIndex);
             Vector3f pos = m.getVerts().get(correspondIx);
             projectedLandmarks.add(new FacialPoint(type.ordinal(), pos));

         }

         FpModel fpModel = getFpModelFromFP(projectedLandmarks, m.getName());


         return fpModel;
    }
     
     private List<Vector2f> parseTxtLandmarks(File txtLandmarks){
        List<Vector2f> landmarks = new LinkedList<>();
        String line;
        
        try(BufferedReader br = new BufferedReader(new FileReader(txtLandmarks));){
            while((line = br.readLine()) != null){
                line = line.replace('\n', ' ');
                String[] split = line.split(" ");
            
                landmarks.add(new Vector2f(Float.parseFloat(split[0]), Float.parseFloat(split[1])));
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(TextureLandmarks.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return landmarks;
        
    }

     
     private FacialPointType getValidFidoFPType(int index){
        switch(index){
            case 39:
                return FacialPointType.EN_R;
            case 36:
                return FacialPointType.EX_R;
            case 42:
                return FacialPointType.EN_L;
            case 45:
                return FacialPointType.EX_L;
            default: 
                return FacialPointType.undefined;
        }
        
               
    }
     
      private FpModel getFpModelFromFP(List<FacialPoint> points, String modelName) {
        if (points == null || modelName == null) {
            return null;
        }

        FpModel model = new FpModel(modelName);
        model.setFacialpoints(points);

        return model;
    }
      
    //Texture to Vertex Correspondence index
    private HashMap<Integer, Integer> buildTextureVertexCorrespondence(Model m){
        List<Vector3f> verts = m.getVerts();
        
        ArrayList<int[]> faceVerts = m.getFaces().getFacesVertIdxs();
        ArrayList<int[]> faceTxt = m.getFaces().getFacesTexIdxs();
        
        HashMap<Integer, Integer> correspondence = new HashMap<>(verts.size());
        
        for(int i = 0; i < faceTxt.size(); i++){
            for(int j = 0; j < faceTxt.get(i).length; j++){
                int vertIx = faceVerts.get(i)[j];
                int txtIx = faceTxt.get(i)[j];
                
                if(!correspondence.containsKey(txtIx))
                    correspondence.put(txtIx - 1, vertIx - 1);
            }
        }
        
        return correspondence;
    } 
}
