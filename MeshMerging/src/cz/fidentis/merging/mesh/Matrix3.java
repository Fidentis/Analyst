package cz.fidentis.merging.mesh;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class Matrix3 {

    private double[][] mat = new double[SIZE][SIZE];

    /**
     *
     */
    protected static final int SIZE = 3;

    /**
     *
     * @param columnOne
     * @param columnTwo
     * @param columnTree
     */
    public Matrix3(final double[] columnOne, final double[] columnTwo,
            final double[] columnTree) {
        System.arraycopy(columnOne, 0, mat[0], 0, columnOne.length);
        System.arraycopy(columnTwo, 0, mat[1], 0, columnTwo.length);
        System.arraycopy(columnTree, 0, mat[2], 0, columnTree.length);
    }

    /**
     *
     * @param other
     */
    public void multiply(final Matrix3 other) {
        double[][] newMat = new double[SIZE][SIZE];
        double sum = 0;
        for (int c = 0; c < SIZE; c++) {
            for (int d = 0; d < SIZE; d++) {
                for (int k = 0; k < SIZE; k++) {
                    sum = sum + mat[c][k] * other.mat[k][d];
                }

                newMat[c][d] = sum;
                sum = 0;
            }
        }
        mat = newMat;
    }

    /**
     *
     * @param other
     * @return
     */
    public Vector3 multiply(final Vector3 other) {
        double[] asArray = other.asArray();
        return new Vector3(multiply(asArray));
    }

    /**
     *
     * @param other
     * @return
     */
    public Coordinates multiply(final Coordinates other) {
        double[] asArray = other.asArray();
        return new Coordinates(multiply(asArray));
    }

    private double[] multiply(final double[] other) {
        double[] newArray = new double[SIZE];
        double sum = 0;
        for (int c = 0; c < SIZE; c++) {
            for (int k = 0; k < SIZE; k++) {
                sum = sum + mat[c][k] * other[k];
            }
            newArray[c] = sum;
            sum = 0;
        }
        return newArray;
    }

}
