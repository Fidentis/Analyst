/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.landmarkParser;


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
 */
public class PTSparser {

    public static String VERSION = "Version 1.0";
    public static String WHITE_SPACE_SEP = "\\s+";
    public static String SEP = "  ";

    public static FpModel load(String filePath) {

        assert filePath.toLowerCase().endsWith(".pts");

        FpModel fpModel = new FpModel();

        BufferedReader br = null;
        String line;

        int counter = 0;
        int numOfPoints = 0;

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {

                counter++;

                // Spracovat hlavicku
                if (counter < 3) {
                    if (counter == 2) {
                        numOfPoints = Integer.parseInt(line);
                    }
                    continue;
                }

                // Pocet poli v riadku musi byt presne 4 - nazov bodu a 3 suradnice
                String[] lineParts = line.split(WHITE_SPACE_SEP);
                if (lineParts.length != 4) {
                    System.out.println("pts loading: point error");
                    continue;
                }

                FacialPoint fp = new FacialPoint();
                fp.setType(getPointType(lineParts[0]));
                fp.getPosition().setX(Float.parseFloat(lineParts[1]));
                fp.getPosition().setY(Float.parseFloat(lineParts[2]));
                fp.getPosition().setZ(Float.parseFloat(lineParts[3]));
                fpModel.addFacialPoint(fp);

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

        String filePath = path + setExtension(fpModel.getModelName(), "pts");

        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {

            // Hlavicka
            writer.println(VERSION); // verzia softveru Landmark
            writer.println(fpModel.getPointsNumber()); // pocet bodov

            // Body
            for (FacialPoint fp : fpModel.getFacialPoints()) {

                String point = getStringName(fp.getType()) + SEP + fp.toCSVstring(SEP);
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
        if (index > 42) {
            index = 42;
        }
        return FacialPointType.values()[index];
    }
}
