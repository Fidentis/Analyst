/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utils;

import cz.fidentis.enums.FileExtensions;
import cz.fidentis.utilsException.FileManipulationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zuzana Ferkova
 */
public final class FileUtils {
    private static FileUtils instance;
    private File tmp;
    
    public static FileUtils instance(){
        if(instance == null){
            instance = new FileUtils();
        }
        
        return instance;
    }

    private FileUtils() {
        try {
            createTMPfolder(true);
        } catch (FileManipulationException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
     /*
    * Deletes given tmpFolder. File path is in format 'tmp/[tmpFolder.name]' where '[tmpFolder.name]' denotes
    * name of give tmpFodler.
    * 
    * @param - tmpFolder - folder from tmp directory to be deleted, if NULL whole tmp folder is deleted.
    * @throws - FileManipulationException - in case folder or it's children could not be deleted.
    */
    public void deleteTMPfolder(File tmpFolder) throws FileManipulationException{
         //exception/log on true/false 
        File completePath = tmp;
        
        if (tmpFolder != null) {
            completePath = new File(tmp.getAbsolutePath() + File.separator + tmpFolder.getName());
        }
        
        deleteFolder(completePath);
        
    }
    
    //deletes given folder
    public void deleteFolder(File folder) throws FileManipulationException{
         //exception/log on true/false 
        File completePath = folder;
        
        boolean success = true;
        
        if (completePath.exists()) {
            deleteDir(completePath.listFiles());

            success =  completePath.delete();
        }

        if(!success){
            throw new FileManipulationException(folder.getName() + " could not be deleted.");
        }
    }
    
    //deletes entire directory
    private void deleteDir(File[] files) throws FileManipulationException{
        boolean success;
        
        for (File f : files){

            if (f.isDirectory() && !(f.listFiles().length == 0)) {
                deleteDir(f.listFiles());
            }

           success =  f.delete();
           
           if(!success){
            throw new FileManipulationException(f.getName() + " could not be deleted.");
         }
        }
    }
    
    public void deleteTmpFolder() throws FileManipulationException{
        deleteFolder(tmp);
    }
    
     /**
     * Creates empty TMP folder in system default temporary-file storage to
     * store temporary data from application. This folder will be deleted on
     * application exit.
     * 
     *
     * @param deleteIfExists - deletes tmp directory if it already exists, for example if previous run of 
     * programme wasn't finished correctly
     * @throws FileManipulationException - if core tmp folder could not be
     * created
     */
    public void createTMPfolder(boolean deleteIfExists) throws FileManipulationException {        
        if (tmp == null || !tmp.exists()) {                  
                //tmp = Files.createTempDirectory("fidentis" + File.separator + "fidentis").toFile();
                tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "fidentis");
                tmp.mkdir();
                
                if(tmp.exists() && deleteIfExists){
                    deleteTmpFolder();
                    tmp.mkdir();
                }
                
                tmp.deleteOnExit();
        }
    }
     
    /**
     * Creates temporary folder for given module within tmp folder. Tmp folder does not need to exist. Can create hierarchical folders.
     * 
     * @param moduleTMPfolder - File representing temporary folder to be created
     * @return created folder
     * @throws FileManipulationException - when either module tmp file couldn't be created or there wasn't tmp folder created before 
     *                                      and program failed to create it.
     */
    public File createTMPmoduleFolder(File moduleTMPfolder) throws FileManipulationException{        
        boolean success = true;
        File completePath = new File(tmp.getAbsolutePath() + File.separator + moduleTMPfolder.getPath());

        if (!completePath.exists()) {
            success = completePath.mkdirs();
        }

        if (!success) {
            throw new FileManipulationException(moduleTMPfolder.getName() + " could not be created in TMP folder.");
        } else {
            return completePath;
        }
    }
    
    /**
     * Creates temporary folder with given name within tmp folder. Tmp folder does not need to exist. Can create hierarchical folders.
     * @param name name of directory to create
     * @return created directory
     * @throws FileManipulationException - when either module tmp file couldn't be created or there wasn't tmp folder created before and program failed to create it.
     */
    public File createTMPmoduleFolder(String name) throws FileManipulationException {
        return createTMPmoduleFolder(new File(name));
    }
    
    public String getTempDirectoryPath() {
        return tmp.getAbsolutePath();
    }
    
    /**
     * Saves result matrix to CSV. Compared to similar method in ProcessingFileUtils class, this method also saves names of the models into resulting CSV
     * @param results - result matrix
     * @param filePath - path to save CSV to
     * @param fileName - name of the file
     * @param mode - mode used (batch, 1:N etc.)
     * @param models - list mof models used to compute results
     * @param useRelative - whether to use signed distance or not
     */
    public void saveMatrixToCSV(ArrayList<ArrayList<Float>> results, String filePath, String fileName, String mode, List<File> models, boolean useRelative){
        String modelName;
        float res;
        
        File path = new File(filePath);
        path.mkdirs();
        path = (new File(path + File.separator + fileName + mode + ".csv"));
        
       try(FileWriter fstream = new FileWriter(path);
           BufferedWriter out = new BufferedWriter(fstream);){
           
           out.write(";");

           //first line
           for(int i = 0; i < results.size(); i++){
            modelName = models.get(i).getName().substring(0, models.get(i).getName().lastIndexOf(".")); 
            out.write(modelName + ";");
           }
           
           out.write("\n");
           
           //write results
           for(int i = 0; i < results.get(0).size(); i++){
               out.write((i+1) + ";");
               for(int j = 0; j < results.size(); j++){
                   res = results.get(j).get(i);
                   
                   if(!useRelative){
                       res = Math.abs(res);
                   }
                   
                   out.write(res + ";");
               }
               out.write("\n");
               out.flush();
           }
           
           out.flush();                   
       } catch (IOException ex) {
           Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
       } 
    }
    
    /**
     * Save a single column of results. This method is mainly used in batch processing, to store results on the disk, rather than keep them all in memory
     * @param collumn - single line of results to store (for single model compared to single model, similar to 1:1 comparison)
     * @param mainF - number of main face for the coparison
     * @param path - URL to save the results to
     */
    public void saveCollumn(ArrayList<ArrayList<Float>> collumn, int mainF, String path) {
        File pathTo;
        
        for (int j = 0; j < collumn.size(); j++ ){
             pathTo = new File(path + File.separator + (j+1));  
                
                if(!pathTo.exists()){
                    pathTo.mkdirs();
                }
                
                pathTo = new File(pathTo.getPath() + File.separator + (j+1) + "_" + (mainF + 1)+ ".txt");

            try(FileWriter fstream = new FileWriter(pathTo);
                BufferedWriter out = new BufferedWriter(fstream);) {
      
                for(Float f : collumn.get(j)){
                    out.write(f + "\n");
                }
                
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    /**
     * Reads folder with stored results into memory.
     * @param path - URL to stored results
     * @param numOfModels - number of models used
     * @param modelNumber - model currently loaded
     * @param useRelative - whether to use signed distance or not
     * @return results from disk lodaded into memory, this method reads data for single model and all models compared to it.
     */
    public ArrayList<ArrayList<Float>> readFolderWithCSV(String path, int numOfModels, int modelNumber, boolean useRelative) {
        ArrayList<ArrayList<Float>> results = new ArrayList<ArrayList<Float>>(numOfModels);
        List<Float> resultsLine = new LinkedList<Float>();
        
        String line;
        Float f;

        for (int i = 0; i < numOfModels; i++) {
            resultsLine.clear();

            try(BufferedReader br = new BufferedReader(new FileReader(new File(path + File.separator + (modelNumber + 1)+ "_" + (i + 1) + ".txt")));) {
 
                while ((line = br.readLine()) != null) {
                    f = new Float(line);
                    
                    if(!useRelative){
                        f = Math.abs(f);
                    }
                    
                    resultsLine.add(f);
                }

                results.add(new ArrayList(resultsLine));
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return results;
    }
    
    /**
     * Saves 1:1 comparison under name of main model.
     * @param path - URL to save results to.
     * @param results - results to save
     * @param useRelative - whether to use signed distance or not
     * @param mainModelName - name of the main model
     */
    public void savePairComparisonAuxResults(String path, List<Float> results, boolean useRelative, String mainModelName){
        File saveTo = new File(path);
        float k;
        
        try(FileWriter fstream = new FileWriter(saveTo);
            BufferedWriter out = new BufferedWriter(fstream);) {
            
            out.write(";" + mainModelName + ";\n");
            
            for(int i = 0; i < results.size(); i++){
                k = results.get(i);
                
                if(!useRelative){
                    k = Math.abs(k);
                }
                
                out.write((i + 1) + ";" + k + ";\n");
            }
            
            out.flush();
            
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    /**
     * Saves given object implementing serializable interface to specified file.
     *
     * @param saveFile file to save object
     * @param object object to save.
     */
    public void saveArbitraryObject(File saveFile, Serializable object) {
        saveFile.getParentFile().mkdirs();
        try (FileOutputStream fs = new FileOutputStream(saveFile);
                ObjectOutputStream output = new ObjectOutputStream(fs);) {
            output.writeObject(object);
        } catch (IOException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Could not save arbitrary object.", e);
        }
    }

    /**
     * Loads object serialized to given file.
     *
     * @param loadFile file to load from.
     * @return deserialized object or null if object could not be deserialized.
     */
    public Object loadArbitraryObject(File loadFile) {
        if (loadFile.isDirectory() || !loadFile.canRead()) {
            throw new IllegalArgumentException("Source file must be readable file.");
        }

        Object result = null;
        try (FileInputStream fs = new FileInputStream(loadFile);
                ObjectInputStream input = new ObjectInputStream(fs);) {

            result = input.readObject();
        } catch (IOException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Could not load arbitrary object.", e);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Cannot find class of loaded object.", e);
        }
        return result;
    }
    
    public FileExtensions getFileExtension(String fileName){
        String fileNameLower = fileName.toLowerCase();
        int extensionStart = fileNameLower.lastIndexOf(".");
        
        if(extensionStart < 0){
            return FileExtensions.NONE;
        }
        
        String extension = fileNameLower.substring(extensionStart + 1);
        
        switch(extension.toLowerCase()){
            case "obj":
                return FileExtensions.OBJ;
            case "stl":
                return FileExtensions.STL;
            case "ply":
                return FileExtensions.PLY;
            case "pp":
                return FileExtensions.PP;
            case "fp":
                return FileExtensions.FP;
            case "csv":
                return FileExtensions.CSV;
            case "png":
                return FileExtensions.PNG;
            case "dta":
                return FileExtensions.DTA;
            case "pts":
                return FileExtensions.PTS;
            case "fid":
                return FileExtensions.FID;
            default:
                return FileExtensions.NONE;
        }
    }
}
