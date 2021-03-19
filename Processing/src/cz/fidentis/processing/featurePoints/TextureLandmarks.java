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
import cz.fidentis.featurepoints.results.CNNDetectionResult;
import cz.fidentis.landmarkParser.PPparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utilsException.FileManipulationException;
import java.awt.image.BufferedImage;
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
import javax.imageio.ImageIO;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.netbeans.api.progress.ProgressHandle;
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
    
    public static int BATCH_SIZE = 20;

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
        return detectTextureLandmarks(models, false, false);
    }
    
    public String detectTextureLandmarks(List<File> models, Boolean detectFace, Boolean onlyTextures){
        if(onlyTextures){
            copyTextureToTMP(models);
        } else {
            copyDataToTMP(models);
        }
        
        try {
            int numOfDirs = (int) Math.ceil((float) models.size() / BATCH_SIZE);
            
            for(int i = 0; i < numOfDirs; i++) {
                 ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_PATH,
                    LANDMARK_DETECTOR_CNN,
                    SAVE_PATH_CNN + File.separator + "models" + File.separator + i,
                    SHAPE_PREDICTOR,
                    detectFace.toString(), Integer.toString(i));

                pb.redirectErrorStream(true);
                pb.redirectOutput();
                pb.directory(PREDICTOR_FOLDER);

                Process p = pb.start();
           
                System.out.println(LANDMARK_DETECTOR_CNN);
                System.out.println(SHAPE_PREDICTOR);
            
                System.out.println(PYTHON_PATH);
                p.waitFor();
            }
              
            return new File(SAVE_PATH_CNN + File.separator + "models").getAbsolutePath();
            
        } catch (InterruptedException | IOException ex) {
            // Throw error?
            //Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    public List<FpModel> get2DLandmarks(List<File> originalTextures, File modelsPath, int numModels) throws IOException {
        String mouthRes = SAVE_PATH_CNN + File.separator + "res_mouth_";
        String lEyeRes = SAVE_PATH_CNN + File.separator + "res_lEye_";
        String rEyeRes = SAVE_PATH_CNN + File.separator + "res_rEye_";
        String noseRes = SAVE_PATH_CNN + File.separator + "res_nose_";
        
        List<FpModel> res = new ArrayList<>();
        
        int numOfDirs = (int) Math.ceil((float) numModels / BATCH_SIZE);
        
        for(int i = 0; i < numOfDirs; i++) {
            int modelIx = 0;
            File[] testImages = new File(SAVE_PATH_CNN + File.separator + "mouth_" + i + "\\").listFiles();
            // Currently, every 2 landmarks belong to one model, all in flattened array
            List<Vector2f> lmMouth = parseManyTxtLandmarks(new File(mouthRes + i + ".txt"));    
            List<Vector2f> lmLEye = parseManyTxtLandmarks(new File(lEyeRes + i + ".txt"));    
            List<Vector2f> lmREye = parseManyTxtLandmarks(new File(rEyeRes + i + ".txt"));    
            List<Vector2f> lmNose = parseManyTxtLandmarks(new File(noseRes+ i + ".txt"));    

            
            for (int k = 0; k < testImages.length; ++k) {  
                File f = testImages[k];
                if(f.getName().endsWith(".obj") || f.getName().endsWith(".mtl")
                        || f.getName().endsWith(".txt"))
                    continue;


                String nameWithoutExtension = f.getName().substring(0, f.getName().length() - 4);
                
                BufferedImage bimg = ImageIO.read(originalTextures.get(modelIx + i * BATCH_SIZE));
                int width = bimg.getWidth();
                int height = bimg.getHeight();

                List<FacialPoint> resTexture = new ArrayList<>();
                List<Float> mouthInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "mouth_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> lEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "left_eye_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> rEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "right_eye_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> noseInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "nose_" + i + File.separator + nameWithoutExtension + ".txt");

                // 2D landmarks
                detecte2DLandmarks(resTexture, rEyeInfo, lEyeInfo, mouthInfo, noseInfo, lmREye, lmLEye, lmMouth, lmNose, modelIx, width, height);
                
                FpModel m = new FpModel(f.getName());
                m.setFacialpoints(resTexture);
                res.add(m);

                modelIx++;
            }
        }
              
        try {
            FileUtils.instance().deleteFolder(SAVE_PATH_CNN);
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return res;
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
        
        
        
        for(int i = 0; i < models.size(); i++) {
            File f = models.get(i);
            Model m = ModelLoader.instance().loadeModelWithoutVertices(f);
            
            // New batch, create new folder
            int dirNumber = i / BATCH_SIZE;
            if(i % BATCH_SIZE == 0) {
                new File(savePath + File.separator + dirNumber).mkdir();
            }
            
            try {
                File txtPath = new File(m.getMatrials().getMatrials().get(0).getTextureFile());
                File mtlPath = new File(m.getMatrials().getDirectory() + File.separator + m.getMatrials().getMaterialFileName());
                
                
                Files.copy(m.getFile().toPath(), (new File(savePath + File.separator + dirNumber + File.separator + m.getFile().getName())).toPath());
                Files.copy(txtPath.toPath(),
                        (new File(savePath + File.separator + dirNumber + File.separator + txtPath.getName())).toPath());
                Files.copy(mtlPath.toPath(),
                        (new File(savePath + File.separator + dirNumber + File.separator + mtlPath.getName())).toPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private void copyTextureToTMP(List<File> textures){
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
        
        
        
        for(int i = 0; i < textures.size(); i++) {
            File f = textures.get(i);
            // New batch, create new folder
            int dirNumber = i / BATCH_SIZE;
            if(i % BATCH_SIZE == 0) {
                new File(savePath + File.separator + dirNumber).mkdir();
            }
            
            try {
                Files.copy(f.toPath(),
                        (new File(savePath + File.separator + dirNumber + File.separator + f.getName())).toPath());
                
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
     
     public HashMap<String, CNNDetectionResult> CNNtoPP(File modelsPath, int numModels, ProgressHandle p) throws IOException {
        String mouthRes = SAVE_PATH_CNN + File.separator + "res_mouth_";
        String lEyeRes = SAVE_PATH_CNN + File.separator + "res_lEye_";
        String rEyeRes = SAVE_PATH_CNN + File.separator + "res_rEye_";
        String noseRes = SAVE_PATH_CNN + File.separator + "res_nose_";
        String modelFolder = modelsPath.getAbsolutePath();    
        
        HashMap<String, CNNDetectionResult> res = new HashMap<>();
        
        int numOfDirs = (int) Math.ceil((float) numModels / BATCH_SIZE);
        
        for(int i = 0; i < numOfDirs; i++) {
            int modelIx = 0;
            File[] testImages = new File(SAVE_PATH_CNN + File.separator + "mouth_" + i + "\\").listFiles();
            // Currently, every 2 landmarks belong to one model, all in flattened array
            List<Vector2f> lmMouth = parseManyTxtLandmarks(new File(mouthRes + i + ".txt"));    
            List<Vector2f> lmLEye = parseManyTxtLandmarks(new File(lEyeRes + i + ".txt"));    
            List<Vector2f> lmREye = parseManyTxtLandmarks(new File(rEyeRes + i + ".txt"));    
            List<Vector2f> lmNose = parseManyTxtLandmarks(new File(noseRes+ i + ".txt"));    

            
            for (int k = 0; k < testImages.length; ++k/*File f : testImages*/) {  
                File f = testImages[k];
                if(f.getName().endsWith(".obj") || f.getName().endsWith(".mtl")
                        || f.getName().endsWith(".txt"))
                    continue;


                String nameWithoutExtension = f.getName().substring(0, f.getName().length() - 4);
                String modelName = nameWithoutExtension + ".obj";
                p.progress("Acquiring 3D landmarks for model " + modelName + ".");
                Model m = ModelLoader.instance().loadModel(new File(modelFolder + File.separator + i + File.separator + modelName), false, true);   
                BufferedImage bimg = ImageIO.read(new File(m.getMatrials().getMatrials().get(0).getTextureFile()));
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                KDTreeIndexed txtTree = new KDTreeIndexed(m.getTexCoords());
                HashMap<Integer, Integer> vertTxtCorrespondence = buildTextureVertexCorrespondence(m);

                List<FacialPoint> resRes = new ArrayList<>();
                List<FacialPoint> resTexture = new ArrayList<>();
                List<Float> mouthInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "mouth_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> lEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "left_eye_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> rEyeInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "right_eye_" + i + File.separator + nameWithoutExtension + ".txt");
                List<Float> noseInfo = getModelCutoutData(SAVE_PATH_CNN + File.separator + "nose_" + i + File.separator + nameWithoutExtension + ".txt");

                // 2D landmarks
                detecte2DLandmarks(resTexture, rEyeInfo, lEyeInfo, mouthInfo, noseInfo, lmREye, lmLEye, lmMouth, lmNose, modelIx, width, height);
                
                
                // 3D landmarks
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

                res.put(modelName, new CNNDetectionResult(resRes, resTexture));
                modelIx++;
            }
        }
              
        try {
            FileUtils.instance().deleteFolder(SAVE_PATH_CNN);
        } catch (FileManipulationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return res;
     }
     
     private void detecte2DLandmarks(List<FacialPoint> resTexture, List<Float> rEyeInfo, List<Float> lEyeInfo, List<Float> mouthInfo,
             List<Float> noseInfo, List<Vector2f> lmREye, List<Vector2f> lmLEye, List<Vector2f> lmMouth, List<Vector2f> lmNose, int modelIx,
             int textureWidth, int textureHeight){
                resTexture.add(new FacialPoint(FacialPointType.EX_R.ordinal(), find2dTexturePosition(rEyeInfo, lmREye.get(modelIx * 4 ))));
                resTexture.add(new FacialPoint(FacialPointType.EN_R.ordinal(), find2dTexturePosition(rEyeInfo, lmREye.get(modelIx * 4 + 1))));
                resTexture.add(new FacialPoint(FacialPointType.PAS_R.ordinal(), find2dTexturePosition(rEyeInfo, lmREye.get(modelIx * 4 + 2))));
                resTexture.add(new FacialPoint(FacialPointType.PAI_R.ordinal(), find2dTexturePosition(rEyeInfo, lmREye.get(modelIx * 4 + 3))));
                
                resTexture.add(new FacialPoint(FacialPointType.EX_L.ordinal(), find2dTexturePosition(lEyeInfo, lmLEye.get(modelIx * 4 ))));
                resTexture.add(new FacialPoint(FacialPointType.EN_L.ordinal(), find2dTexturePosition(lEyeInfo, lmLEye.get(modelIx * 4 + 1))));
                resTexture.add(new FacialPoint(FacialPointType.PAS_L.ordinal(), find2dTexturePosition(lEyeInfo, lmLEye.get(modelIx * 4 + 2))));
                resTexture.add(new FacialPoint(FacialPointType.PAI_L.ordinal(), find2dTexturePosition(lEyeInfo, lmLEye.get(modelIx * 4 + 3))));
                
                resTexture.add(new FacialPoint(FacialPointType.LS.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 ))));
                resTexture.add(new FacialPoint(FacialPointType.STO.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 1))));
                resTexture.add(new FacialPoint(FacialPointType.LI.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 2))));
                resTexture.add(new FacialPoint(FacialPointType.CH_R.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 3))));
                resTexture.add(new FacialPoint(FacialPointType.CH_L.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 4))));
                resTexture.add(new FacialPoint(FacialPointType.CP_R.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 5))));
                resTexture.add(new FacialPoint(FacialPointType.CP_L.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 6))));
                resTexture.add(new FacialPoint(FacialPointType.PG.ordinal(), find2dTexturePosition(mouthInfo, lmMouth.get(modelIx * 8 + 7))));
                
                resTexture.add(new FacialPoint(FacialPointType.SN.ordinal(), find2dTexturePosition(noseInfo, lmNose.get(modelIx * 5 ))));
                resTexture.add(new FacialPoint(FacialPointType.AL_R.ordinal(), find2dTexturePosition(noseInfo, lmNose.get(modelIx * 5 + 1 ))));
                resTexture.add(new FacialPoint(FacialPointType.AL_L.ordinal(), find2dTexturePosition(noseInfo, lmNose.get(modelIx * 5 + 2 ))));
                resTexture.add(new FacialPoint(FacialPointType.N.ordinal(), find2dTexturePosition(noseInfo, lmNose.get(modelIx * 5 + 3 ))));
                resTexture.add(new FacialPoint(FacialPointType.PRN.ordinal(), find2dTexturePosition(noseInfo, lmNose.get(modelIx * 5 + 4 ))));
                
                // Data is normalized, get pixel value
                for(FacialPoint fp: resTexture){
                    fp.getPosition().x *= textureWidth;
                    fp.getPosition().y *= textureHeight;
                }
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
      
    private Vector3f find2dTexturePosition(List<Float> modelInfo, Vector2f currentLm){
        Vector3f usedLM = new Vector3f(currentLm.x, currentLm.y, 0.0f);
        if(modelInfo != null)
        {
            usedLM.x = modelInfo.get(0) + modelInfo.get(2) * usedLM.x;
            usedLM.y = 1 - (modelInfo.get(1) + modelInfo.get(3) * usedLM.y);
        }else
        {
            usedLM.y = 1 - usedLM.y;
        }
        
        return usedLM;
    }
        
    private Vector3f find3dVertex(List<Float> modelInfo, Vector2f currentLm, KDTreeIndexed txtTree, HashMap<Integer, Integer> vertTxtCorrespondence,
            Model m)
    {
        Vector3f currentlm = find2dTexturePosition(modelInfo, currentLm);
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
