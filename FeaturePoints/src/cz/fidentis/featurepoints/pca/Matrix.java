package cz.fidentis.featurepoints.pca;

/**
 * Class for performing matrix calculations specific to PCA.
 * @author	Kushal Ranjan
 * @version	051413
 */
class Matrix {
	
	static int numMults = 0; //Keeps track of the number of multiplications performed
	
	/**
	 * Test code for SVD. Uses example from MIT video: http://www.youtube.com/watch?v=cOUTpqlX-Xs
	 */
	public static void main(String[] args) {
		System.out.println("Original matrix:");
		double[][] test = {{5, -1}, {5, 7}}; //C
		Matrix.print(test);
		double[][][] SVD = Matrix.singularValueDecomposition(test);
		double[][] U = SVD[0];
		double[][] S = SVD[1];
		double[][] V = SVD[2];
		System.out.println("U-matrix:");
		Matrix.print(U);
		System.out.println("Sigma-matrix:");
		Matrix.print(S);
		System.out.println("V-matrix:");
		Matrix.print(V);
		System.out.println("Decomposition product (C = US(V^T)):");
		Matrix.print(Matrix.multiply(U, Matrix.multiply(S, Matrix.transpose(V)))); //Should be C
	}
	
	
	/**
	 * Computes the singular value decomposition (SVD) of the input matrix.
	 * @param input		the input matrix
	 * @return			the SVD of input, {U,S,V}, such that input = US(V^T). U and S are
	 * 					orthogonal matrix, and the non-zero entries of the diagonal matrix S are
	 * 					the 
	 */
	static double[][][] singularValueDecomposition(double[][] input) {
		double[][] C = Matrix.copy(input);
		double[][] CTC = multiply(transpose(C), C); //(C^T)C = V(S^T)S(V^T)
		EigenSet eigenC = eigenDecomposition(CTC);
		double[][] S = new double[C.length][C.length]; //Diagonal matrix
		for(int i = 0; i < S.length; i++) {
			S[i][i] = Math.sqrt(eigenC.values[i]); //Squareroots of eigenvalues are entries of S
		}
		double[][] V = eigenC.vectors;
		double[][] CV = multiply(C, V); //CV = US
		double[][] invS = copy(S); //Inverse of S
		for(int i = 0; i < invS.length; i++) {
			invS[i][i] = 1.0/S[i][i];
		}
		double[][] U = multiply(CV, invS); //U = CV(S^-1)
		return new double[][][] {U, S, V};
	}
	
	/**
	 * Determines the eigenvalues and eigenvectors of a matrix by using the QR algorithm. Repeats
	 * until no eigenvalue changes by more than 1/100000.
	 * @param	input	input matrix; must be square
	 * @return			an EigenSet containing the eigenvalues and corresponding eigenvectors of
	 * 					input
	 */
	static EigenSet eigenDecomposition(double[][] input) {
		if(input.length != input[0].length) {
			throw new MatrixException("Eigendecomposition not defined on nonsquare matrices.");
		}
		double[][] copy = copy(input);
		double[][] Q = new double[copy.length][copy.length];
		for(int i = 0; i < Q.length; i++) {
			Q[i][i] = 1; //Q starts as an identity matrix
		}
		boolean done = false;
		while(!done) {
			double[][][] fact = Matrix.QRFactorize(copy);
			double[][] newMat = Matrix.multiply(fact[1], fact[0]); //[A_k+1] := [R_k][Q_k]
			Q = Matrix.multiply(fact[0], Q);
			//Stop the loop if no eigenvalue changes by more than 1/100000
			for(int i = 0; i < copy.length; i++) {
				if(Math.abs(newMat[i][i] - copy[i][i]) > 0.00001) {
					copy = newMat;
					break;
				} else if(i == copy.length - 1) { //End of copy table
					done = true;
				}
			}
		}
		EigenSet ret = new EigenSet();
		ret.values = Matrix.extractDiagonalEntries(copy); //Eigenvalues lie on diagonal
		ret.vectors = Q; //Columns of Q converge to the eigenvectors
		return ret;
	}
	
	/**
	 * Produces an array of the diagonal entries in the input matrix.
	 * @param input	input matrix
	 * @return		the entries on the diagonal of input
	 */
	static double[] extractDiagonalEntries(double[][] input) {
		double[] out = new double[input.length];
		for(int i = 0; i<input.length; i++) {
			out[i] = input[i][i];
		}
		return out;
	}
	
	/**
	 * Performs a QR factorization on the input matrix.
	 * @param input	input matrix
	 * @return		{Q, R}, the QR factorization of input.
	 */
	static double[][][] QRFactorize(double[][] input) {
		double[][][] out = new double[2][][];
		double[][] orthonorm = gramSchmidt(input);
		out[0] = orthonorm; //Q is the matrix of the orthonormal vectors formed by GS on input
		double[][] R = new double[orthonorm.length][orthonorm.length];
		for(int i = 0; i < R.length; i++) {
			for(int j = 0; j <= i; j++) {
				R[i][j] = dot(input[i], orthonorm[j]);
			}
		}
		out[1] = R;
		return out;
	}
	
	/**
	 * Converts the input list of vectors into an orthonormal list with the same span.
	 * @param input	list of vectors
	 * @return		orthonormal list with the same span as input
	 */
	static double[][] gramSchmidt(double[][] input) {
		double[][] out = new double[input.length][input[0].length];
		for(int outPos = 0; outPos < out.length; outPos++) {
			double[] v = input[outPos];
			for(int j = outPos - 1; j >= 0; j--) {
				double[] sub = proj(v, out[j]);
				v = subtract(v, sub); //Subtract off non-orthogonal components
			}
			out[outPos] = normalize(v); //return an orthonormal list
		}
		return out;
	}
	
	/**
	 * Returns the Givens rotation matrix with parameters (i, j, th).
	 * @param size	total number of rows/columns in the matrix
	 * @param i		the first axis of the plane of rotation; i > j
	 * @param j		the second axis of the plane of rotation; i > j
	 * @param th	the angle of the rotation
	 * @return		the Givens rotation matrix G(i,j,th)
	 */
	static double[][] GivensRotation(int size, int i, int j, double th) {
		double[][] out = new double[size][size];
		double sine = Math.sin(th);
		double cosine = Math.cos(th);
		for(int x = 0; x < size; x++) {
			if(x != i && x != j) {
				out[x][x] = cosine;
			} else {
				out[x][x] = 1;
			}
		}
		out[i][j] = -sine;//ith column, jth row
		out[j][i] = sine;
		return out;
	}
	
	/**
	 * Returns the transpose of the input matrix.
	 * @param matrix	double[][] matrix of values
	 * @return			the matrix transpose of matrix
	 */
	static double[][] transpose(double[][] matrix) {
		double[][] out = new double[matrix[0].length][matrix.length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out[0].length; j++) {
				out[i][j] = matrix[j][i];
			}
		}
		return out;
	}
	
	/**
	 * Returns the sum of a and b.
	 * @param a	double[][] matrix of values
	 * @param b	double[][] matrix of values
	 * @return	the matrix sum a + b
	 */
	static double[][] add(double[][] a, double[][] b) {
		if(a.length != b.length || a[0].length != b[0].length) {
			throw new MatrixException("Matrices not same size.");
		}
		double[][] out = new double[a.length][a[0].length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out[0].length; j++) {
				out[i][j] = a[i][j] + b[i][j];
			}
		}
		return out;
	}
	
	/**
	 * Returns the difference of a and b.
	 * @param a	double[][] matrix of values
	 * @param b	double[][] matrix of values
	 * @return	the matrix difference a - b
	 */
	static double[][] subtract(double[][] a, double[][] b) {
		if(a.length != b.length || a[0].length != b[0].length) {
			throw new MatrixException("Matrices not same size.");
		}
		double[][] out = new double[a.length][a[0].length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out[0].length; j++) {
				out[i][j] = a[i][j] - b[i][j];
			}
		}
		return out;
	}
	
	/**
	 * Returns the sum of a and b.
	 * @param a	double[] vector of values
	 * @param b	double[] vector of values
	 * @return	the vector sum a + b
	 */
	static double[] add(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new MatrixException("Vectors are not same length.");
		}
		double[] out = new double[a.length];
		for(int i = 0; i < out.length; i++) {
			out[i] = a[i] + b[i];
		}
		return out;
	}
	
	/**
	 * Returns the difference of a and b.
	 * @param a	double[] vector of values
	 * @param b	double[] vector of values
	 * @return	the vector difference a - b
	 */
	static double[] subtract(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new MatrixException("Vectors are not same length.");
		}
		double[] out = new double[a.length];
		for(int i = 0; i < out.length; i++) {
			out[i] = a[i] - b[i];
		}
		return out;
	}
	
	/**
	 * Returns the matrix product of a and b; if the horizontal length of a is not equal to the
	 * vertical length of b, throws an exception.
	 * @param a	double[][] matrix of values
	 * @param b	double[][] matrix of values
	 * @return	the matrix product ab
	 */
	static double[][] multiply(double[][] a, double[][] b) {
		if(a.length != b[0].length) {
			throw new MatrixException("Matrices not compatible for multiplication.");
		}
		double[][] out = new double[b.length][a[0].length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out[0].length; j++) {
				double[] row = getRow(a, j);
				double[] column = getColumn(b, i);
				out[i][j] = dot(row, column);
			}
		}
		return out;
	}
	
	/**
	 * Returns a version of mat scaled by a constant.
	 * @param mat	input matrix
	 * @param coeff	constant by which to scale
	 * @return		mat scaled by coeff
	 */
	static double[][] scale(double[][] mat, double coeff) {
		double[][] out = new double[mat.length][mat[0].length];
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < out[0].length; j++) {
				out[i][j] = mat[i][j] * coeff;
			}
		}
		return out;
	}
	
	/**
	 * Takes the dot product of two vectors, {a[0]b[0], ..., a[n]b[n]}.
	 * @param a	double[] of values
	 * @param b	double[] of values
	 * @return	the dot product of a with b
	 */
	static double dot(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new MatrixException("Vector lengths not equal: " + a.length + "=/=" + b.length);
		}
		double sum = 0;
		for(int i = 0; i < a.length; i++) {
			numMults++;
			sum += a[i] * b[i];
		}
		return sum;
	}
	
	/**
	 * Returns a copy of the input matrix.
	 * @param input	double[][] to be copied
	 */
	static double[][] copy(double[][] input) {
		double[][] copy = new double[input.length][input[0].length];
		for(int i = 0; i < copy.length; i++) {
			for(int j = 0; j < copy[i].length; j++) {
				copy[i][j] = input[i][j];
			}
		}
		return copy;
	}
	
	/**
	 * Returns the ith column of the input matrix.
	 */
	static double[] getColumn(double[][] matrix, int i) {
		return matrix[i];
	}
	
	/**
	 * Returns the ith row of the input matrix.
	 */
	static double[] getRow(double[][] matrix, int i) {
		double[] vals = new double[matrix.length];
		for(int j = 0; j < vals.length; j++) {
			vals[j] = matrix[j][i];
		}
		return vals;
	}
	
	/**
	 * Returns the projection of vec onto the subspace spanned by proj
	 * @param vec	vector to be projected
	 * @param proj	spanning vector of the target subspace
	 * @return		proj_proj(vec)
	 */
	static double[] proj(double[] vec, double[] proj) {
		double constant = dot(proj, vec)/dot(proj, proj);
		double[] projection = new double[vec.length];
		for(int i = 0; i < proj.length; i++) {
			projection[i] = proj[i]*constant;
		}
		return projection;
	}
	
	/**
	 * Returns a normalized version of the input vector, i.e. vec scaled such that ||vec|| = 1.
	 * @return	vec/||vec||
	 */
	static double[] normalize(double[] vec) {
		double[] newVec = new double[vec.length];
		double norm = norm(vec);
		for(int i = 0; i < vec.length; i++) {
			newVec[i] = vec[i]/norm;
		}
		return newVec;
	}
	
	/**
	 * Computes the norm of the input vector
	 * @return ||vec||
	 */
	static double norm(double[] vec) {
		return Math.sqrt(dot(vec,vec));
	}
	
	/**
	 * Prints the input matrix with each value rounded to 4 significant figures
	 */
	static void print(double[][] matrix) {
		for(int j = 0; j < matrix[0].length; j++) {
			for(int i = 0; i < matrix.length; i++) {
				double formattedValue = Double.parseDouble(String.format("%.4g%n", matrix[i][j]));
				if(Math.abs(formattedValue) < 0.00001) { //Hide negligible values
					formattedValue = 0;
				}
				System.out.print(formattedValue + "\t");
			}
			System.out.print("\n");
		}
		System.out.println("");
	}
}

/**
 * Exception class thrown when invalid matrix calculations are attempted
 */
class MatrixException extends RuntimeException {
	MatrixException(String string) {
		super(string);
	}
}