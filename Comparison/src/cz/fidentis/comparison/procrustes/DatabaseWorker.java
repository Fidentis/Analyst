package cz.fidentis.comparison.procrustes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class works with database of Procrustes distances
 *
 * @author Zuzana LÝOVÁ
 * @version 2014
 */
public class DatabaseWorker {

    private int numberOfConfigs = -1;
    private double[] database;
    private boolean empty = true;


    public DatabaseWorker() throws FileNotFoundException {
          //TODO: Check for basic values or make non-parametric constructor private (aka delete it)
    }
    
    public DatabaseWorker(boolean scaling) throws FileNotFoundException {
        String path="";
        try {
            path = new java.io.File(".").getCanonicalPath();
        } catch (IOException ex) {
           //TODO: Manage exception
        }
        
        if(scaling){
            this.database = this.readFromDatabaseFile(new File(path+ separatorChar + "models" + separatorChar + "resources" + separatorChar + "databaseScaled.txt"));
        }else{
            this.database = this.readFromDatabaseFile(new File(path+ separatorChar + "models" + separatorChar + "resources" + separatorChar + "databaseNonScaled.txt"));            
        }
        
        if(this.numberOfConfigs < 0){
            throw new IllegalArgumentException("reading database failed");
        }
        this.empty = false;
    }

    public DatabaseWorker(String databaseName) throws FileNotFoundException {
        this.database = this.readFromDatabaseFile(new File(databaseName));
        
        if((this.numberOfConfigs < 0)||(this.empty)){
            throw new IllegalArgumentException("reading database failed");
        }
    }

    
    public DatabaseWorker(double[] arrayOfDistances, int numberOfConfigs){
        if(arrayOfDistances == null){
            throw new NullPointerException();
        }if(numberOfConfigs < 0){
            throw new IllegalArgumentException("number of configurations is less than 0");
        }
        
        database = new double[arrayOfDistances.length];
        System.arraycopy(arrayOfDistances, 0, database, 0, arrayOfDistances.length);
        
        this.empty = false;
        this.numberOfConfigs = numberOfConfigs;
    }
    
    /**
     * This method creates database of Procrustes distances
     *
     * @param gpa               list of configurations
     * @param treshold          accuracy
     * @param databaseFile      file where to create database
     * @throws FileNotFoundException
     */
    public void createDatabaseFile(GPA gpa, float treshold, File databaseFile) throws FileNotFoundException {
        gpa.doGPA(treshold);

        this.database = gpa.createArrayOfDistances();
        this.numberOfConfigs = gpa.getConfigs().size();
        this.empty = false;
        
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(databaseFile);
            bw = new BufferedWriter(fw);
            
            //writing number od configurations
            bw.write(gpa.getConfigs().size());
            bw.newLine();
            
            //writing Procrustes distances 
            for(int i = 0; i < database.length; i++){
                bw.write(Double.toString(database[i]));
                    bw.newLine();
            }
            
        } catch (IOException i) {
            throw new FileNotFoundException("file not written");
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                throw new FileNotFoundException("file not closed");
            }
        }

        this.sortDatabase(databaseFile);
    }

    /**
     * This method reads database of Procrustes distances and saves all values
     * into an array
     *
     * @return array of Procrustes distances
     * @throws FileNotFoundException
     */
    private double[] readFromDatabaseFile(File databaseFile) throws FileNotFoundException {
        if (!databaseFile.exists()) {
            throw new FileNotFoundException("database not found");
        }

        BufferedReader br;
        FileReader fr;

        try {
            fr = new FileReader(databaseFile);
            br = new BufferedReader(fr);
            String line = br.readLine();
            
            this.numberOfConfigs = Integer.parseInt(line);
            line = br.readLine();
            
            this.database = new double[numberOfConfigs * numberOfConfigs];
            
            int i = 0;

            while ((line != null) && (i < (numberOfConfigs * numberOfConfigs))) {
                if (line.length() > 0) {

                    database[i] = Float.valueOf(line);

                    i++;
                    line = br.readLine();
                }
            }
        } catch (IOException i) {
            throw new FileNotFoundException();
        }
        
        this.empty = false;
        
        return database;
    }

    /**
     * This method applies reverse bubble sort on database
     *
     */
    public void bubbleSortReverse() {
        double help;
        for (int i = 0; i < database.length; i++) {
            for (int j = 0; j < (database.length - 1); j++) {
                if (database[j] < database[j + 1]) {
                    help = database[j];
                    database[j] = database[j + 1];
                    database[j + 1] = help;
                }
            }
        }
    }

    /**
     * This method converts String representing table with numerical results 
     * into String which can be exported as a database
     * @param table     String representing table of numerical results
     */
    public void convertTableToDatabase(String table){
        String[] text = table.split("\n");
        String[] line = text[0].split(";");
        this.numberOfConfigs = line.length - 1;        
        this.database = new double[this.numberOfConfigs * this.numberOfConfigs];
        
        for(int i = 1; i < text.length; i++){
            line = text[i].split(";"); 
            
            for(int j = 1; j < line.length; j++){
                database[(i-1)*(line.length - 1)+ j - 1] = Float.valueOf(line[j]);
            }
        }
        this.empty = false;
    }
    
    /**
     * This method sorts the database and return all values as an array
     *
     * @param databaseFile  file with database
     * @return              sorted array of distances
     * @throws              FileNotFoundException
     */
    public double[] sortDatabase(File databaseFile) throws FileNotFoundException {
        this.bubbleSortReverse();

        BufferedWriter bw = null;
        FileWriter fw;

        try {

            fw = new FileWriter(databaseFile);
            bw = new BufferedWriter(fw);
            
            bw.write(Integer.toString(this.numberOfConfigs));
            bw.newLine();
            
            for (int i = 0; i < database.length; i++) {
                bw.write(Double.toString(database[i]));
                bw.newLine();
            }
        } catch (IOException i) {
            throw new FileNotFoundException("file not written");
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                throw new FileNotFoundException("file not closed");
            }
        }

        return database;
    }

    /**
     * This method counts percentile of given distance considering the database
     *
     * @param distance given distance
     * @return percentile of given distance considering the database
     */
    public float countPercentile(double distance){
        float percentile;
        int i = 0;

        while ((distance < database[i])) {
            i++;
            if(i == database.length){
                break;
            }
        }

        percentile = i / (database.length / 100f);
        return percentile;
    }

    /**
     * This method count minimal distance in the database
     *
     * @return minimal distance in the database
     */
    public double getMinDistance(){
        this.bubbleSortReverse();
        return database[database.length - this.numberOfConfigs - 1];
    }

    /*
     * This method count maximal distance in the database
     * @return          maximal distance in the database
     */
    public double getMaxDistance(){
        this.bubbleSortReverse();
        return database[0];
    }

    /**
     * This method count mean distance in the database
     *
     * @return          mean distance in the database
     */
    public double getMeanDistance(){
        this.bubbleSortReverse();
        double meanDistance = 0;

        for (int i = 0; i < database.length; i++) {
            meanDistance = meanDistance + database[i];
        }

        return meanDistance / database.length;
    }
    
        public double[] getDatabase() {
        if(empty){
            throw new NullPointerException();
        }
        return database;
    }
   
}