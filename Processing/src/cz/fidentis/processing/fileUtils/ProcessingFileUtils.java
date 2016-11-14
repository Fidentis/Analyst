/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.processing.fileUtils;


import cz.fidentis.model.Model;
import cz.fidentis.model.ModelExporter;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utilsException.FileManipulationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class to manipulate with Files during processing.
 * TODO: input checks (for NPE)
 * 
 * @author Zuzana Ferkova
 */
public class ProcessingFileUtils {
    
   private static ProcessingFileUtils instance;
   
   private ProcessingFileUtils(){}
   
   public static ProcessingFileUtils instance(){
       if(instance == null){
           instance = new ProcessingFileUtils();
       }
       
       return instance;
   }

   /**
    * Creates local copy of models given by their File object (containing path to models) to tempory folder 
    * (path tmp/[moduleTMPfile.name], where '[tmpFolder.name]' denotes
    * name of give tmpFolder). TMP folder does not need to exist and neither does moduleTMPfile.
    * 
    * @param models - list containing paths to models to be copied
    * @param moduleTMPfile - path to module tempory folder, where local copies are to be created
    * @param texture - denotes whether to copy textures as well or not
    * @throws FileManipulationException - throws in case tmp folder or moduleTMPfile could not be created.
    */
   
    public void copyModelsToTMP(List<File> models, File moduleTMPfile, boolean texture) throws FileManipulationException {
        FileUtils.instance().createTMPmoduleFolder(moduleTMPfile);

        
        Model currentModel;

        for (int i = 0; i < models.size(); i++) {
            currentModel = ModelLoader.instance().loadModel(models.get(i), texture,true);

            saveModelToTMP(currentModel, new File(/*FileUtils.instance().getTempDirectoryPath() + File.separator +*/ moduleTMPfile.getPath()), 0, i, texture);
        }
    }
    
    /**
     * Creates copy of given models and saves them to tmp folder.
     * @param models - models to be copied to tmp folder
     * @param moduleTMPfile - URL to tmp folder
     * @param texture - wheather to use textures or not
     * @return URLs to copies models
     * @throws FileManipulationException 
     */
     public List<File> saveModelsToTMP(List<Model> models, File moduleTMPfile, boolean texture) throws FileManipulationException {
        FileUtils.instance().createTMPmoduleFolder(moduleTMPfile);

        Model currentModel;
        
        List<File> files = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            currentModel = models.get(i);

            files.add(saveModelToTMP(currentModel, new File(/*FileUtils.instance().getTempDirectoryPath() + File.separator + */moduleTMPfile.getName()), 0, i, texture));
        }
        return files;
    }
    
    /**
     * Copies single model to tmp folder
     * @param model - model to save to tmp folder
     * @param moduleTMPfolder - folder to copy model to
     * @param i - number of iteration of models (0 for first copy, 1 for first avg face etc.)
     * @param j - model's number
     * @param textures - whether to use textures or not
     * @return 
     */
    public File saveModelToTMP(Model model, File moduleTMPfolder, int i, int j, boolean textures) {
        ModelExporter me;   
        
        String fPath = FileUtils.instance().getTempDirectoryPath() + File.separator + moduleTMPfolder.getPath() + File.separator + moduleTMPfolder.getName() + "_" + i + "_" + j;
        //String fPath = moduleTMPfolder.getPath() + File.separator + moduleTMPfolder.getName() + "_" + i + "_" + j;
        File f = new File(fPath + ".ftmp");
        
        

        me = new ModelExporter(model);
        me.exportModelToObj(f, textures);
        
        return new File(fPath + File.separator + moduleTMPfolder.getName() + "_" + i + "_" + j + ".obj");
    }

    /**
     * Takes matrix of results and saves it into CSV. 
     * @param results
     * @param filePath
     * @param k 
     */
    public void saveMatrixToCSV(List<ArrayList<Float>> results, String filePath, int k) {
           File path = new File(filePath);
           path.mkdirs();
           path = (new File(path + File.separator + (k+1) + ".csv"));
           
       try (FileWriter fstream = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fstream) ;){
       
           out.write(";");

           //first line
           for(int i = 0; i < results.size(); i++){
             out.write((i+1) + ";");
           }
           
           out.write("\n");
           
           //write results
           for(int i = 0; i < results.get(0).size(); i++){
               out.write((i+1) + ";");
               for(int j = 0; j < results.size(); j++){
                   out.write(results.get(j).get(i) + ";");
               }
               out.write("\n");
           }
           
           out.flush();
           //file automatically closed by Java
       } catch (IOException ex) {
           Logger.getLogger(cz.fidentis.utils.FileUtils.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
}
