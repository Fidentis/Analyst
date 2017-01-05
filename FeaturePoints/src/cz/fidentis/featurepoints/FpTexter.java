/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import static java.io.File.separatorChar;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Galvanizze
 */
public class FpTexter {

    private static String SEP = ";";
    private static String textName = "fp_text.csv";
    private static FpTexter instance  = new FpTexter();
    private static Map<Integer, List<String>> fpTexts = new HashMap<>();

    private FpTexter() {}

    public static FpTexter getInstance() {
        if (fpTexts.isEmpty()) {
            instance.loadTexts();
        }
        return instance;
    }

    private void loadTexts() {

        BufferedReader br = null;
        String line = "";
        String filePath = "";

        try {
            filePath = new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + textName;
            
            int counter = 0;
            
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {

                counter++;

                // Preskocit hlavicku
                if (counter == 1) {
                    continue;
                }

                String[] lineParts = line.split(SEP);
                if (lineParts.length != 3 ) {
                    continue;
                }

                List<String> values = new ArrayList<>();
                values.add(lineParts[1]);
                values.add(lineParts[2]);
                
                fpTexts.put(parseText(lineParts[0]), values);
                
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private Integer parseText(String text){
        int result;
        try{
            if(text.equals("unspecified")){
                result = -1;
            }else{
                result = Integer.parseInt(text);
            }
                      
        }catch(NumberFormatException ex){
            result = FacialPointType.valueOf(text).ordinal() + 1;
        }
        
        return result;        
    }
    
    public String getFPname(Integer type) {
        return getFPvalue(type, 0);
    }
    
    public String getFPinfo(Integer type) {
        return getFPvalue(type, 1);
    }
    
    private String getFPvalue(Integer type, int index){
        if (fpTexts.isEmpty()) {
            return type.toString();
        }
        
        List<String> values = fpTexts.get(type);
        if (values != null && !values.isEmpty()) {
            return values.get(index);
        } else {
            return type.toString();
        }       
    }
    
    /**
     * Removes landmark from the loaded list. Does NOT save the changes.
     * 
     * @param tc - component from which action was called
     * @param id - id of the landmark to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeLandmark(Component tc, int id){
        if(id < 0){
            return false;
        }
        
        if(id < FacialPointType.values().length){   //trying to delete some of the default values
            JOptionPane.showMessageDialog(tc,
                            "You are trying to delete default value. Landmark will not be deleted.", "Incorrect action",
                            JOptionPane.OK_OPTION);
            return false;
        }
        
        fpTexts.remove(id);
        return true;
    }
    
    /**
     * Attempts to add landmark with given ID. Does NOT save the changes.
     * 
     * @param tc - component from which action was called
     * @param id - id of the landmark to be added
     * @param desc - description (name, description) of the landmark to be added
     * @return true if action was successful, false otherwise
     */
    public boolean addLandmarks(Component tc, int id, List<String> desc){
        if(id < 0){
            return false;
        }
        
        if(id < FacialPointType.values().length){       //default values
            JOptionPane.showMessageDialog(tc,
                            "This ID is taken by default values. Landmark will not be added.", "Incorrect action",
                            JOptionPane.OK_OPTION);
            return false;
        }
        
        if(fpTexts.containsKey(id)){
            int result = JOptionPane.showConfirmDialog(tc,
                            "This ID already exists. Do you want to rewrite it?", "Landmark already exists",
                            JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.NO_OPTION){
                return false;
            }     
        }
        
        fpTexts.put(id, desc);
        return true;
    }
    
    /**
     * Attempt to edit landmark with given ID. Does NOT save the changes. Allows to change name a desc of build-in landmarks
     * 
     * @param tc - component from which the action was called
     * @param id - id of the landmark to be edited
     * @param desc - description (name, description) of the edited landmark
     * @return true if successful, false otherwise
     */
    public boolean editLandmark(Component tc, int id, int oldId, List<String> desc){
        int builtIn = FacialPointType.values().length;
        
        if(id < 0){
            return false;
        }
        
        if(oldId != id && id < builtIn){
            JOptionPane.showMessageDialog(tc, "You are trying to rewrite build-in landmark definition. Operation will not be completed.", "Incorrect action.",
                    JOptionPane.OK_OPTION);
            return false;
        }
        
        if(!fpTexts.containsKey(oldId)){
           JOptionPane.showMessageDialog(tc,
                            "The landmark wasn't found.", "Landmark not found",
                            JOptionPane.OK_OPTION);
           return false;
        }
        
        fpTexts.put(id, desc);
        fpTexts.remove(oldId);
        return true;
    }
    
    /**
     * Returns data about loaded landmarks so that they can be added to the table.
     * 
     * @return TableData class containing header for table and information about landmarks.
     */
    public TableData landmarkDescription(){     
     Set<Integer> ids = fpTexts.keySet();
     int landmarkNumber = fpTexts.size();       //acount for -1 which is unspecified, but not shown in GUI
     int counter = 0;
     int buildinLandmarksNum = FacialPointType.values().length;
     
     TableData td = new TableData();
     
     String[][] tableValues;
     
     if(landmarkNumber < buildinLandmarksNum){
         tableValues = new String[buildinLandmarksNum][3];
     }else{
         tableValues = new String[landmarkNumber][3];
     }
     
     String[] tableLine = new String[]{"ID", "Landmark Name", "Landmark Description"};
     td.setHeader(tableLine);   
     
     for(int i = 1; ;i++){      //to have ordered results
         if(ids.contains(i)){
             List<String> desc = fpTexts.get(i);
             tableLine = new String[]{Integer.toString(i), desc.get(0), desc.get(1)};             
             tableValues[counter] = tableLine;
             
             counter++;
         }else if(i < buildinLandmarksNum){     //desc of landmark wasn't loaded from file
             tableLine = new String[]{Integer.toString(i), FacialPointType.values()[i].toString(), "No information"};
             tableValues[counter] = tableLine;
             counter++;
             
             //add the value to texter
             List<String> info = Arrays.asList(new String[]{tableLine[1], tableLine[2]});
             fpTexts.put(i, info);
         }
         
         if(counter > landmarkNumber - 1 && counter > buildinLandmarksNum - 1){ //all landmarks have been added
             break;
         }
     }   
     
     td.setTableData(tableValues);
     
     return td;
    }

    public void saveLandmarks(){
        try {
            String filePath = new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + textName;
            StringBuilder sb = new StringBuilder("Type;Name;Info").append(System.lineSeparator());
        
            Set<Integer> ids = fpTexts.keySet();
            int landmarkNumber = fpTexts.size();
            int counter = 0;

            for (int i = 0;; i++) {      //to have ordered results
                if (ids.contains(i)) {
                    List<String> desc = fpTexts.get(i);
                    sb.append(i).append(SEP).append(desc.get(0)).append(SEP).append(desc.get(1)).append(System.lineSeparator());
                    
                    counter++;
                }

                if (counter >= landmarkNumber - 1) { //all landmarks have been added
                    break;
                }
            }

            try (FileWriter fstream = new FileWriter(filePath);
                    BufferedWriter out = new BufferedWriter(fstream);){
                
            out.write(sb.toString());
            out.flush();
        }
            
        } catch (IOException ex) {
            Logger.getLogger(FpTexter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
