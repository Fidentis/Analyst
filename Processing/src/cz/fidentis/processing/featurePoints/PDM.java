/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.featurePoints;

import Jama.Matrix;
import cz.fidentis.featurepoints.FpModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import org.openide.util.Exceptions;

/**
 *
 * @author Rasto1
 */
public class PDM implements Serializable{
    private FpModel meanShape;
    private Matrix eigenVectors;
    private Matrix eigenValues;
    private String modelName;

    public PDM(FpModel meanShape, Matrix eigenVectors, Matrix eigenValues) {
        this.meanShape = meanShape;
        this.eigenVectors = eigenVectors;
        this.eigenValues = eigenValues;
    }

    public PDM(FpModel meanShape, Matrix eigenVectors, Matrix eigenValues, String modelName) {
        this.meanShape = meanShape;
        this.eigenVectors = eigenVectors;
        this.eigenValues = eigenValues;
        this.modelName = modelName;
    }

    public FpModel getMeanShape() {
        return meanShape;
    }

    public Matrix getEigenVectors() {
        return eigenVectors;
    }

    public Matrix getEigenValues() {
        return eigenValues;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public void savePDM(String savePath) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(
                    new FileOutputStream(savePath + "_" + modelName + ".pdm")
            );
            
            out.writeObject(this);
            out.flush();
            out.close();
        } catch (FileNotFoundException ex ) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }
    
    public static PDM loadPDM(String filePath){
        ObjectInputStream in = null;
        PDM pdm = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filePath));
            pdm = (PDM) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return pdm;
    }
    
   
}
