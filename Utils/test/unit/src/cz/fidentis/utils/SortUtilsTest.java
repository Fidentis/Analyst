/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author xferkova
 */
public class SortUtilsTest {
    
    private static final File csvPath = new File("..\\resources\\testCsv.csv");
    private static List<Float> csvValues;
    
    public SortUtilsTest() {
       csvValues = parseCSV(csvPath);
    }

    @Test
    public void sortValuesSmall(){
        Random r = new Random();
        List<Float> values = new ArrayList<>();
        
        for(int i = 0; i < 10; i++){
            values.add(r.nextFloat());
        }
        
        values = SortUtils.instance().sortValues(values);
        
        for(int i = 0; i < values.size() - 1; i++){
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }
    
    private List<Float> parseCSV(File path){
        String line;
        List<Float> res = new ArrayList<>();
        int counter = 0;
        
        assert path.getPath().toLowerCase().endsWith(".csv");
        
         try(BufferedReader br = new BufferedReader(new FileReader(path));) {
             while((line = br.readLine()) != null){
                 if (counter == 0){
                     counter++;
                     continue;
                 }
                 
                 String[] lineParts = line.split(";");
                          
                 for(int i = 1; i < lineParts.length; i++){
                     res.add(Float.parseFloat(lineParts[i]));
                 }
             }
 
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ex.toString());
            } catch (IOException ex) {
                Logger.getLogger(ex.toString());
            }
         
         return res;
    }
    
    @Test
    public void sortValuesLarge(){
        List<Float> copy = new ArrayList<>();
        
        for(Float f : csvValues){
            copy.add(f);
        }
        
        copy = SortUtils.instance().sortValues(copy);
        
        for(int i = 0; i < copy.size() - 1; i++){
            assertTrue(copy.get(i) < copy.get(i + 1));
        }
    } 
    
    @Test
    public void sortValuesIndex(){
        Random r = new Random();
        List<Float> values = new ArrayList<>();
        List<Integer> indices = ListUtils.instance().populateList(10);
        
        for(int i = 0; i < 10; i++){
            values.add(r.nextFloat());
        }
        
        indices = SortUtils.instance().sortIndices(values, indices);
        
        for(int i = 0; i < indices.size() - 1; i++){
            assertTrue(values.get(indices.get(i)) < values.get(indices.get(i + 1)));
        }
    }
    
    @Test
    public void sortValuesIndexLarge(){
        List<Float> copy = new ArrayList<>();
        List<Integer> indices ;
        for(Float f : csvValues){
            copy.add(f);
        }
        
        indices = ListUtils.instance().populateList(copy.size());
        
        indices = SortUtils.instance().sortIndices(copy, indices);
        
        for(int i = 0; i < indices.size() - 1; i++){
             assertTrue(copy.get(indices.get(i)) < copy.get(indices.get(i + 1)));
        }
    }
    
    @Test
    public void sortListFromIndices(){
        Random r = new Random();
        List<Float> values = new ArrayList<>();
        List<Integer> indices = ListUtils.instance().populateList(10);
        
        for(int i = 0; i < 10; i++){
            values.add(r.nextFloat());
        }
        
        indices = SortUtils.instance().sortIndices(values, indices);
        values = SortUtils.instance().sortListFromIndices(values, indices);
        
        for(int i = 0; i < values.size() - 1; i++){
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }
    
    @Test
    public void sortListFromIndicesLarge(){
        List<Float> copy = new ArrayList<>();
        List<Integer> indices ;
        for(Float f : csvValues){
            copy.add(f);
        }
        
        indices = ListUtils.instance().populateList(copy.size());
        indices = SortUtils.instance().sortIndices(copy, indices);
        copy = SortUtils.instance().sortListFromIndices(copy, indices);
        
        for(int i = 0; i < copy.size() - 1; i++){
            assertTrue(copy.get(i) < copy.get(i + 1));
        }
    }    
}
