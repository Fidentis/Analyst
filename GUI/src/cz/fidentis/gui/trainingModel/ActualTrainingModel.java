/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.trainingModel;

import java.io.File;
import static java.io.File.separatorChar;
import java.io.IOException;

/**
 *
 * @author Rasto1
 */
public class ActualTrainingModel {
    private static File choosedTrainingModel = null;
    
    public static void setTrainingModel(String str) throws IOException{
        choosedTrainingModel = new File(new java.io.File(".").getCanonicalPath() + separatorChar + "models" + separatorChar + "resources" + separatorChar + "trainingModels" + str);
    }
    
    public File getTrainingModel(){
        return choosedTrainingModel;
    }
}
