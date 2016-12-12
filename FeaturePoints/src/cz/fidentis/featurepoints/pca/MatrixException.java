/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints.pca;

/**
 * Exception class thrown when invalid matrix calculations are attempted
 * @author Marek Zuzi
 */
public class MatrixException extends RuntimeException {

    MatrixException(String string) {
        super(string);
    }
}
