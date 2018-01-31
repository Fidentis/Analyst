/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.pdm;

import cz.fidentis.featurepoints.FpModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rasto
 */
public class CSVparsers {

    public static String WHITE_SPACE_SEP = "\\s+";
    public static String SPACE = " ";
    public static String SEP = ";";

    /**
     * load .csv files and save them to the map data structer
     * @param filePath path to folder, which contains all .csv files
     * @return map of .csv files
     */
    public static Map load(String filePath) {
        Map trainingShapes = new HashMap();

        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();

        BufferedReader br = null;
        String line;
        int counter = 0;
        int name = 0;
        

        for (int l = 0; l < listOfFiles.length; l++) {
            if (listOfFiles[l].isFile()) {
                try {
                    br = new BufferedReader(new FileReader(listOfFiles[l].getPath()));
                    counter = 0;
                    
                    while ((line = br.readLine()) != null) {

                        if (counter == 1) {
                            String[] lineParts = line.split(SEP);
                            
                            double[][] valuesp = new double[lineParts.length/3 - 28][3];
                            int counter2 = 0;
                            
                            for (int i = 1; i < lineParts.length - 84; i += 3) {
                                
                                valuesp[counter2][0] = Double.parseDouble(lineParts[i]);
                                valuesp[counter2][1] = Double.parseDouble(lineParts[i+1]);
                                valuesp[counter2][2] = Double.parseDouble(lineParts[i+2]);
                                counter2++;
                            }

                            
                            trainingShapes.put(name, valuesp);
                            name++;
                        }
                        counter++;
                    }

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                        }
                    } else {
                    }
                }
            }
        }
        return trainingShapes;
    }
    
    /**
     * same as load, only difference in return List
     * @param filePath path to the folder
     * @return list of FpModels
     */
    public static List<FpModel> load2(String filePath) {
        List<FpModel> trainingShapes = new ArrayList<>();

        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();

        BufferedReader br = null;

        for (int l = 0; l < listOfFiles.length; l++) {
            if (listOfFiles[l].isFile()) {
                CSVparser pars = new CSVparser();
                trainingShapes.add(pars.load(listOfFiles[l].getPath()).get(0));
                
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
                } else {
                }
            }
        }
        return trainingShapes;
    }
    
    /**
     * same as load2
     * @param listOfFiles list of files
     * @return list of FpModels
     */
    public static List<FpModel> load3(File[] listOfFiles) {
        List<FpModel> trainingShapes = new ArrayList<>();

        BufferedReader br = null;

        for (int l = 0; l < listOfFiles.length; l++) {
            if (listOfFiles[l].isFile()) {
                CSVparser pars = new CSVparser();
                trainingShapes.add(pars.load(listOfFiles[l].getPath()).get(0));
                
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
                } else {
                }
            }
        }
        return trainingShapes;
    }
}
