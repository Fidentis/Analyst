/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.pca;

    /**
 * Data holder class that contains a set of eigenvalues and their corresponding eigenvectors.
 * @author	Kushal Ranjan
 * @version 051413
 */
public class EigenSet {
	double[] values;
	double[][] vectors;
        
        public double[] getValues(){
            return values;
        }
        
        public double[][] getVectors(){
            return vectors;
        }
}

