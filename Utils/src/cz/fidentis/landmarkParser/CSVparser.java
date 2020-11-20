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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Galvanizze
 */
public class CSVparser {
public static String WHITE_SPACE_SEP = "\\s+";
    public static String SPACE = " ";
    public static String SEP = ";";

    public static List<FpModel> load(String filePath) {

        assert filePath.toLowerCase().endsWith(".csv");

        List<FpModel> fpModels = new ArrayList<>();

        BufferedReader br = null;
        String line;

        int counter = 0;

        try {
            br = new BufferedReader(new FileReader(filePath));
            List<Integer> fpTypes = new ArrayList<>();
            while ((line = br.readLine()) != null) {

                counter++;

                // Spracovat hlavicku
                if (counter == 1) {
                    fpTypes = parseHead(line);
                    continue;
                }

                // Pocet poli v riadku musi byt aspon 4 - nazov modelu a 1 bod (3 suradnice)
                String[] lineParts = line.split(SEP);
                if (lineParts.length < 4) {
                    continue;
                }

                FpModel fpModel = new FpModel(lineParts[0]);

                for (int i = 1; i < lineParts.length; i = i + 3) {
                    
                    // ak tento bod nie je vyplneny, tak pokracovat
                    if (lineParts[i].isEmpty() ||
                        lineParts[i+1].isEmpty() ||
                        lineParts[i+2].isEmpty()) {
                        continue;
                    }
                    
                    FacialPoint fp = new FacialPoint();
//                    fp.setType(FacialPointType.values()[(i + 1) / 3]);
                    fp.setType(fpTypes.get((i - 1) / 3));
                    fp.getPosition().setX(Float.parseFloat(lineParts[i]));
                    fp.getPosition().setY(Float.parseFloat(lineParts[i + 1]));
                    fp.getPosition().setZ(Float.parseFloat(lineParts[i + 2]));
                    fpModel.addFacialPoint(fp);
                }

                fpModels.add(fpModel);

                //FacialPoint facialPoint = new FacialPoint();
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
        return fpModels;
    }

    public static void save(List<FpModel> fpModels, String filePath) {

        assert !fpModels.isEmpty();
        assert !filePath.isEmpty();

        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {

            int numOfLandmarks = 0;
            for (FpModel fpModel : fpModels) {
                List<FacialPoint> tmpList = fpModel.createListFp();
                Collections.sort(tmpList, (fp1, fp2) -> {
                    return fp1.getType() - fp2.getType();
                });
                
                if(tmpList.get(tmpList.size() -1 ).getType() > numOfLandmarks) {
                    numOfLandmarks = tmpList.get(tmpList.size() -1 ).getType();
                }
            }
            
            // Hlavicka
            writer.println(buildHead(numOfLandmarks));      // +1 for undefined

            // Body
            // Do vystupu exportovat VSETKY body z FacialPointType
            // ak sa nejaky bod nenachaza v modely, tak exportovat prazdne hodnoty
            for (FpModel fpModel : fpModels) {
                writer.println(fpModel.toCSVstring(SEP));
            }

            writer.flush();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CSVparser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String buildHead(int numOfLandmarks) {
        String head = "Model name" + SEP;
        for (int i = 0; i < FacialPointType.values().length - 1; i++) {     //TODO: make sure it works with all the landmarks
            head = head + pointToHead(i);
        }
        
        if(FacialPointType.values().length - 1 < numOfLandmarks) {
            for(int i = FacialPointType.values().length - 1; i <= numOfLandmarks; i++) {
                head += i + " x" + SEP + i + " y" + SEP + i + " z" + SEP;
            }
        }
        
        // Vymaz poslednej ciarky
        return head.substring(0, head.length() - 1);
    }

    private static String pointToHead(int index) {
        String type = FacialPointType.values()[index].toString();
        // bod hlavicky bude vo formate 'PRN x, PRN y, PRN z,'
        return type + " x" + SEP + type + " y" + SEP + type + " z" + SEP;
    }

    private static List<Integer> parseHead(String line) {
        List<Integer> fpTypes = new ArrayList<>();
        String[] lineParts = line.split(SEP);
        for (int i = 1; i < lineParts.length; i = i + 3) {

            fpTypes.add(getPointType(lineParts[i]));
        }
        return fpTypes;
    }

    private static Integer getPointType(String headPart) {
//        TODO: dorobit aj kontrolu, ci ma bod spravne vyplnene suradnice x, y, z
        String[] part = headPart.trim().split(" ");
        Integer result = -1;
        try{
          result = Integer.parseInt(part[0]);
        }catch(NumberFormatException ex){
            result = FacialPointType.valueOf(part[0]).ordinal();        //in case it was one of the defined landmarks
        }
        
        return result;
    }

}
