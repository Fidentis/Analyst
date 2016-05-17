/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.landmarkParser;


import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Galvanizze
 * 
 * .dta format podporovany softverom Landmark
 * http://web.cs.ucdavis.edu/~amenta/LandmarkDoc_v3_b6.pdf - str 38
 */
public class DTAparser {

    public static String WHITE_SPACE_SEP = "\\s+";
    public static String SEP = "  ";

    public static FpModel load(String filePath) {

        assert filePath.toLowerCase().endsWith(".dta");
        
        

        FpModel fpModel = new FpModel();

        BufferedReader br = null;
        String line;

        int counter = 0;
        int index = 0;
        int numOfPoints = 0;

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {

                counter++;

                // Spracovat hlavicku
                if (counter < 7) {
                    if (counter == 5) {
                        fpModel.setModelName(line);
                    }
                    continue;
                }

                // Pocet poli v riadku musi byt presne 3 - 3 suradnice
                line = line.trim();
                String[] lineParts = line.split(WHITE_SPACE_SEP);
                if (lineParts.length != 3) {
                    System.out.println("dta loading: point error");
                    continue;
                }
                
                FacialPoint fp = new FacialPoint();
                fp.setType(FacialPointType.values()[index]);
                fp.getPosition().setX(Float.parseFloat(lineParts[0]));
                fp.getPosition().setY(Float.parseFloat(lineParts[1]));
                fp.getPosition().setZ(Float.parseFloat(lineParts[2]));
                fpModel.addFacialPoint(fp);
                
                index++;

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
        return fpModel;
    }

    public static void save(FpModel fpModel, String path) {

        assert fpModel.containsPoints();
        assert !path.isEmpty();
        
        String fileName = setExtension(fpModel.getModelName(), "dta");
        String filePath = path + fileName;
        
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {

            // Hlavicka
            writer.println("'" + fileName); // nazov suboru
            writer.println("'Saved by software Fidentis Analyst"); // komentar
            writer.println("1 1L " + fpModel.getPointsNumber() * 3 + " 0 9999 Dim=3"); // nastavenia
            writer.println();
            writer.println(fpModel.getModelName()); // nazov modelu
            writer.println();

            // Body
            for (FacialPoint fp : fpModel.getFacialPoints()) {

                String point = SEP + fp.toCSVstring(SEP);
                writer.println(point);
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CSVparser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String setExtension(String fileName, String newExtension) {
        String newFileName = fileName.substring(0, fileName.lastIndexOf('.') + 1);
        return newFileName + newExtension;
    }

    private static String getStringName(FacialPointType type) {
        return "S" + String.format("%03d", type.ordinal());
    }

    private static FacialPointType getPointType(String stringName) {
        int index = Integer.parseInt(stringName.replaceAll("\\D+", ""));
        return FacialPointType.values()[index];
    }
}