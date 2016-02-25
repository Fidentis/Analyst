package cz.fidentis.featurepoints.pca;

import java.util.ArrayList;
import javax.vecmath.Vector3f;


/**
 * Holds the information of a data set. Each row contains a single data point. Primary computations
 * of PCA are performed by the Data object.
 * @author	Kushal Ranjan
 * @version	051313
 */
class PCA {
	private double[][] matrix; //matrix[i] is the ith row; matrix[i][j] is the ith row, jth column
	/**
	 * Constructs a new data matrix.
	 * @param vals	data for new Data object; dimensions as columns, data points as rows.
	 */
	public PCA(double[][] vals) {
            matrix = Matrix.copy(vals);
	}
        
        public PCA (ArrayList<Vector3f> verts){
            matrix = new double[3][verts.size()];
            int i = 0;
            for(Vector3f vert : verts){
                matrix[0][i] = vert.getX();
                matrix[1][i] = vert.getY();
                matrix[2][i] = vert.getZ();
                i++;
            }
        }
	
	/**
	 * PCA implemented using the NIPALS algorithm. The return value is a double[][], where each
	 * double[] j is an array of the scores of the jth data point corresponding to the desired
	 * number of principal components.
	 * @param numComponents	desired number of PCs
	 * @return				the scores of the data array against the PCS
	 */
	public double[][] PCANIPALS(int numComponents) {
		center();
		double[][][] PCA = NIPALSAlg(numComponents);
		double[][] scores = new double[numComponents][matrix[0].length];
		for(int point = 0; point < scores[0].length; point++) {
			for(int comp = 0; comp < PCA.length; comp++) {
				scores[comp][point] = PCA[comp][0][point];
			}
		}
		return scores;
	}
	
	/**
	 * Implementation of the non-linear iterative partial least squares algorithm on the data
	 * matrix for this Data object. The number of PCs returned is specified by the user.
	 * @param numComponents	number of principal components desired
	 * @return				a double[][][] where the ith double[][] contains ti and pi, the scores
	 * 						and loadings, respectively, of the ith principal component.
	 */
	public double[][][] NIPALSAlg(int numComponents) {
		final double THRESHOLD = 0.00001;
		double[][][] out = new double[numComponents][][];
		double[][] E = Matrix.copy(matrix);
		for(int i = 0; i < out.length; i++) {
			double eigenOld = 0;
			double eigenNew = 0;
			double[] p = new double[matrix[0].length];
			double[] t = new double[matrix[0].length];
			double[][] tMatrix = {t};
			double[][] pMatrix = {p};
			for(int j = 0; j < t.length; j++) {
				t[j] = matrix[i][j];
			}
			do {
				eigenOld = eigenNew;
				double tMult = 1/Matrix.dot(t, t);
				tMatrix[0] = t;
				p = Matrix.scale(Matrix.multiply(Matrix.transpose(E), tMatrix), tMult)[0];
				p = Matrix.normalize(p);
				double pMult = 1/Matrix.dot(p, p);
				pMatrix[0] = p;
				t = Matrix.scale(Matrix.multiply(E, pMatrix), pMult)[0];
				eigenNew = Matrix.dot(t, t);
			} while(Math.abs(eigenOld - eigenNew) > THRESHOLD);
			tMatrix[0] = t;
			pMatrix[0] = p;
			double[][] PC = {t, p}; //{scores, loadings}
			E = Matrix.subtract(E, Matrix.multiply(tMatrix, Matrix.transpose(pMatrix)));
			out[i] = PC;
		}
		return out;
	}
	
	/**
	 * Previous algorithms for performing PCA
	 */
	
	/**
	 * Performs principal component analysis with a specified number of principal components.
	 * @param input			input data; each double[] in input is an array of values of a single
	 * 						variable for each data point
	 * @param numComponents	number of components desired
	 * @return				the transformed data set
	 */
	public double[][] principalComponentAnalysis( int numComponents) {
		center();
		EigenSet eigen = getCovarianceEigenSet();
		double[][] featureVector = buildPrincipalComponents(numComponents, eigen);
		double[][] PC = Matrix.transpose(featureVector);
		double[][] inputTranspose = Matrix.transpose(matrix);
		return Matrix.transpose(Matrix.multiply(PC, inputTranspose));
	}
	
	/**
	 * Returns a list containing the principal components of this data set with the number of
	 * loadings specified.
	 * @param numComponents	the number of principal components desired
	 * @param eigen			EigenSet containing the eigenvalues and eigenvectors
	 * @return				the numComponents most significant eigenvectors
	 */
	public double[][] buildPrincipalComponents(int numComponents, EigenSet eigen) {
		double[] vals = eigen.values;
		if(numComponents > vals.length) {
			throw new RuntimeException("Cannot produce more principal components than those provided.");
		}
		boolean[] chosen = new boolean[vals.length];
		double[][] vecs = eigen.vectors;
		double[][] PC = new double[numComponents][];
		for(int i = 0; i < PC.length; i++) {
			int max = 0;
			while(chosen[max]) {
				max++;
			}
			for(int j = 0; j < vals.length; j++) {
				if(Math.abs(vals[j]) > Math.abs(vals[max]) && !chosen[j]) {
					max = j;
				}
			}
			chosen[max] = true;
			PC[i] = vecs[max];
		}
		return PC;
	}
	
	/**
	 * Uses the QR algorithm to determine the eigenvalues and eigenvectors of the covariance 
	 * matrix for this data set. Iteration continues until no eigenvalue changes by more than 
	 * 1/10000.
	 * @return	an EigenSet containing the eigenvalues and eigenvectors of the covariance matrix
	 */
	public EigenSet getCovarianceEigenSet() {
		double[][] data = covarianceMatrix();
		return Matrix.eigenDecomposition(data);
	}
	
	/**
	 * Constructs the covariance matrix for this data set.
	 * @return	the covariance matrix of this data set
	 */
	public double[][] covarianceMatrix() {
		double[][] out = new double[matrix.length][matrix.length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out.length; j++) {
				double[] dataA = matrix[i];
				double[] dataB = matrix[j];
				out[i][j] = covariance(dataA, dataB);
			}
		}
		return out;
	}
	
	/**
	 * Returns the covariance of two data vectors.
	 * @param a	double[] of data
	 * @param b	double[] of data
	 * @return	the covariance of a and b, cov(a,b)
	 */
	public double covariance(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new MatrixException("Cannot take covariance of different dimension vectors.");
		}
		double divisor = a.length - 1;
		double sum = 0;
		double aMean = mean(a);
		double bMean = mean(b);
		for(int i = 0; i < a.length; i++) {
			sum += (a[i] - aMean) * (b[i] - bMean);
		}
		return sum/divisor;
	}
	
	/**
	 * Centers each column of the data matrix at its mean.
	 */
	public void center() {
		matrix = normalize(matrix);
	}
	
	
	/**
	 * Normalizes the input matrix so that each column is centered at 0.
	 */
	public double[][] normalize(double[][] input) {
		double[][] out = new double[input.length][input[0].length];
		for(int i = 0; i < input.length; i++) {
			double mean = mean(input[i]);
			for(int j = 0; j < input[i].length; j++) {
				out[i][j] = input[i][j] - mean;
			}
		}
		return out;
	}
	
	/**
	 * Calculates the mean of an array of doubles.
	 * @param entries	input array of doubles
	 */
	public double mean(double[] entries) {
		double out = 0;
		for(double d: entries) {
			out += d/entries.length;
		}
		return out;
	}
}
