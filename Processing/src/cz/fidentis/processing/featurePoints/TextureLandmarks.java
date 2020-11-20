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
import cz.fidentis.landmarkParser.PPparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utilsException.FileManipulationException;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static String LANDMARK_DETECTOR_CNN;
    private static String SHAPE_PREDICTOR;
    private static String PYTHON_PATH;
    private static File SAVE_PATH;
    private static File SAVE_PATH_CNN;
    private static File PREDICTOR_FOLDER;

    private TextureLandmarks() {
        try {
            String resourcePath = new java.io.File(".").getCanonicalPath() + File.separator + "models" + File.separator + "resources" + File.separator;
            
            SAVE_PATH = new File(resourcePath + "trainingModels" + File.separator + "shape_predictor");
            SAVE_PATH_CNN = new File(resourcePath + "trainingModels" + File.separator + "CNN" + File.separator + "tmp");
            PREDICTOR_FOLDER = new File(resourcePath + "trainingModels" + File.separator + "CNN");
            LANDMARK_DETECTOR_CNN = resourcePath + "trainingModels" + File.separator + "CNN" + File.separator + "predict_landmarks.py";
            LANDMARK_DETECTOR =  SAVE_PATH + File.separator + "landmark_detection_forJava.py";
            SHAPE_PREDICTOR = SAVE_PATH + File.separator + "shape_predictor_68_face_landmarks.dat";
            PYTHON_PATH = new java.io.File(".").getCanonicalPath() + File.separator + "models" + File.separator + "python" + File.separator + "python.exe";            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static TextureLandmarks instance(){
        if(instance == null)
            instance = new TextureLandmarks();
        
        return instance;
    }
    
    public String detectTextureLandmarks(List<File> models){
        copyDataToTMP(models);
        
        try {            
            ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_PATH,
                    LANDMARK_DETECTOR_CNN,
                    SAVE_PATH_CNN + File.separator + "models",
                    SHAPE_PREDICTOR,
                    "false");

            pb.redirectErrorStream(true);
            pb.redirectOutput();
            pb.directory(PREDICTOR_FOLDER);

            Process p = pb.start();
           
            System.out.println(LANDMARK_DETECTOR_CNN);
            System.out.println(SHAPE_PREDICTOR);
            
            System.out.println(PYTHON_PATH);
            p.waitFor();
            
            
            return new File(SAVE_PATH_CNN + File.separator + "models").getAbsolutePath();
            
        } catch (InterruptedException | IOException ex) {
            // Throw error?
            //Exceptions.printStackTrace(ex);
            return null;
        }
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
     
    private void copyDataToTMP(List<File> models) {
        File savePath = new File(SAVE_PATH_CNN + File.separator + "models");
        if(SAVE_PATH_CNN.exists()) {
            try {
                FileUtils.instance().deleteFolder(savePath);
            } catch (FileManipulationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        SAVE_PATH_CNN.mkdir();
        
        if(!savePath.exists()){
            savePath.mkdirs();
        }
        
        for(File f : models) {
            Model m = ModelLoader.instance().loadeModelWithoutVertices(f);
            
            try {
                File txtPath = new File(m.getMatrials().getMatrials().get(0).getTextureFile());
                File mtlPath = new File(m.getMatrials().getDirectory() + File.separator + m.getMatrials().getMaterialFileName());
                
                
                Files.copy(m.getFile().toPath(), (new File(savePath + File.separator + m.getFile().getName())).toPath());
                Files.copy(txtPath.toPath(),
                        (new File(savePath + File.separator + txtPath.getName())).toPath());
                Files.copy(mtlPath.toPath(),
                        (new File(savePath + File.separator + mtlPath.getName())).toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
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
     
     public HashMap<String, List<FacialPoint>> CNNtoPP(File modelsPath) {
        String mouthRes = SAVE_PATH_CNN + File.separator + "res_mouth.txt";
        String lEyeRes = SAVE_PATH_CNN + File.separator + "res_lEye.txt";
        String rEyeRes = SAVE_PATH_CNN + File.separator + "res_rEye.txt";
        String noseRes = SAVE_PATH_CNN + File.separator + "res_nose.txt";
        String modelFolder = modelsPath.getAbsolutePath();        
        File[] testImages = new File(SAVE_PATH_CNN + File.separator + "mouth\\").listFiles();
        int modelIx = 0;

        
        // Currently, every 2 landmarks belong to one model, all in flattened array
        List<Vector2f> lmMouth = parseManyTxtLandmarks(new File(mouthRes));    
        List<Vector2f> lmLEye = parseManyTxtLandmarks(new File(lEyeRes));    
        List<Vector2f> lmREye = parseManyTxtLandmarks(new File(rEyeRes));    
        List<Vector2f> lmNose = parseManyTxtLandmarks(new File(noseRes));    
        
        HashMap<String,List<FacialPoint>> res = new HashMap<>();
        
        for (int k = 0; k < testImages.length; ++k/*File f : testImages*/) {  
            File f = testImages[k];
            if(f.getName().endsWith(".obj") || f.getName().endsWith(".mtl")
                    || f.getName().endsWith(".txt"))
                continue;

            
            String nameWithoutExtension = f.getName().substring(0, f.getName().length() - 4);
            String modelName = nameWithoutExtension + ".obj";
            Model m = ModelLoader.instance().loadModel(new File(modelFolder + File.separator + modelName), false, true);   
            
            KDTreeIndexed txtTree = new KDTreeIndexed(m.getTexCoords());
            HashMap<Integer, Integer> vertTxtCorrespondence = buildTextureVertexCorrespondence(m);

            List<FacialPoint> resRes = new ArrayList<>();
            List<Float> mouthInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "mouth" + File.separator + nameWithoutExtension + ".txt");
            List<Float> lEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "left_eye" + File.separator + nameWithoutExtension + ".txt");
            List<Float> rEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "right_eye" + File.separator + nameWithoutExtension + ".txt");
            List<Float> noseInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "nose" + File.separator + nameWithoutExtension + ".txt");

            resRes.add(new FacialPoint(FacialPointType.EX_R.ordinal(), find3dVertex(rEyeInfo, lmREye.get(modelIx * 4 ), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.EN_R.ordinal(), find3dVertex(rEyeInfo, lmREye.get(modelIx * 4 + 1), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PAS_R.ordinal(), find3dVertex(rEyeInfo, lmREye.get(modelIx * 4 + 2), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PAI_R.ordinal(), find3dVertex(rEyeInfo, lmREye.get(modelIx * 4 + 3), txtTree, vertTxtCorrespondence, m)));

            resRes.add(new FacialPoint(FacialPointType.EX_L.ordinal(), find3dVertex(lEyeInfo, lmLEye.get(modelIx * 4 ), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.EN_L.ordinal(), find3dVertex(lEyeInfo, lmLEye.get(modelIx * 4 + 1), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PAS_L.ordinal(), find3dVertex(lEyeInfo, lmLEye.get(modelIx * 4 + 2), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PAI_L.ordinal(), find3dVertex(lEyeInfo, lmLEye.get(modelIx * 4 + 3), txtTree, vertTxtCorrespondence, m)));

            resRes.add(new FacialPoint(FacialPointType.LS.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.STO.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 1), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.LI.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 2), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.CH_R.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 3), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.CH_L.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 4), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.CP_R.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 5), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.CP_L.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 6), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PG.ordinal(), find3dVertex(mouthInfo, lmMouth.get(modelIx * 8 + 7), txtTree, vertTxtCorrespondence, m)));
            
            resRes.add(new FacialPoint(FacialPointType.SN.ordinal(), find3dVertex(noseInfo, lmNose.get(modelIx * 5 ), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.AL_R.ordinal(), find3dVertex(noseInfo, lmNose.get(modelIx * 5 + 1), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.AL_L.ordinal(), find3dVertex(noseInfo, lmNose.get(modelIx * 5 + 2), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.N.ordinal(), find3dVertex(noseInfo, lmNose.get(modelIx * 5 + 3), txtTree, vertTxtCorrespondence, m)));
            resRes.add(new FacialPoint(FacialPointType.PRN.ordinal(), find3dVertex(noseInfo, lmNose.get(modelIx * 5 + 4), txtTree, vertTxtCorrespondence, m)));

            res.put(modelName, resRes);
            modelIx++;
        }
        
        try {
            FileUtils.instance().deleteFolder(SAVE_PATH_CNN);
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return res;
     }
     
     private List<Vector2f> parseManyTxtLandmarks(File txtLandmarks){
        List<Vector2f> landmarks = new LinkedList<>();
        String line;
        
        try(BufferedReader br = new BufferedReader(new FileReader(txtLandmarks));){
            while((line = br.readLine()) != null){
                line = line.replace('\n', ' ');
                String[] split = line.split(" ");
                
                for(int i = 0; i < split.length / 2; i++)
                {
                    landmarks.add(new Vector2f(Float.parseFloat(split[i*2]), Float.parseFloat(split[i*2+1])));
                }
            }
            
            
        } catch (IOException ex) {

        }
        
        return landmarks;
        
    }
        
      private List<Float> getModelCutoutData(String infoDataPath){
        List<Float> res = new LinkedList<>();
        String line;
        
        try(BufferedReader br = new BufferedReader(new FileReader(infoDataPath));){
            while((line = br.readLine()) != null){
                line = line.replace('\n', ' ');
                String[] split = line.split(" ");
                
                for(int i = 0; i < split.length; i++)
                {
                    res.add(Float.parseFloat(split[i]));
                }
            }
        } catch (IOException ex) {
            
        }
        
        return res;
    }
        
    private Vector3f find3dVertex(List<Float> modelInfo, Vector2f currentLm, KDTreeIndexed txtTree, HashMap<Integer, Integer> vertTxtCorrespondence,
            Model m)
    {
        Vector2f usedLM = currentLm;
        if(modelInfo != null)
        {
            usedLM.x = modelInfo.get(0) + modelInfo.get(2) * usedLM.x;
            usedLM.y = 1 - (modelInfo.get(1) + modelInfo.get(3) * usedLM.y);
        }else
        {
            usedLM.y = 1 - usedLM.y;
        }

        Vector3f currentlm = new Vector3f(usedLM.x, usedLM.y, 0.0f);
        int closestIndex = txtTree.nearestIndex(currentlm);
                
        if (vertTxtCorrespondence.get(closestIndex) != null) {
            int correspondIx = vertTxtCorrespondence.get(closestIndex);
             Vector3f pos = m.getVerts().get(correspondIx);
             return pos;
        }
        
        return null;
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
