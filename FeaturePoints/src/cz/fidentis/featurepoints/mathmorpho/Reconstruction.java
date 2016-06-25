package cz.fidentis.featurepoints.mathmorpho;

import java.util.Arrays;

/**
 *
 * @author Galvanizze
 *
 * Reconstruction by dilation - R^δ_g(f)
 * Reconstruction by erosion  - R^ε_g(f)
 *
 * where f = marker (signal), g = mask
 *
 */
public class Reconstruction extends MorphoOperator {

    // Signal (alebo MASKA v 'nazvoslovi' morfologickej rekonstrukcie) je podedeny z triedy MorphoOperator
    private double[] inputMarker;

    // Nastavit masku (signal) a marker podla parametra, a normalizovat
    public Reconstruction(double[] mask, double[] marker) {
        super(mask);
        this.inputMarker = copy(marker);
    }

    // Nastavit masku (signal) a marker podla parametra, a normalizovat
    public Reconstruction(double[] mask, double[] marker, boolean normalize) {
        super(mask, normalize);
        this.inputMarker = copy(marker);
    }

    // Nastavit marker rovnaky ako je maska (signal)
    public Reconstruction(double[] mask, boolean normalize) {
        super(mask, normalize);
        this.inputMarker = copyMask();
    }

    // Nastavit marker rovnaky ako je maska (signal) a upravit podla parametra
    public Reconstruction(double[] signal, boolean normalize, double markerTransform) {
        this(signal, normalize);

        if (markerTransform == 0) {
            return;
        }

        for (int i = 0; i < signal.length; i++) {
            inputMarker[i] -= markerTransform;
        }
    }

    private double[] reconstruction(ReconstructionType type, int steps) {
        double[] mask = copyMask();
        double[] marker = copy(inputMarker);
        double[] markerTmp;
        boolean stability;

        do {
            steps--;
            markerTmp = copy(marker);

            // forward scan
            for (int i = 0; i < mask.length; i++) {
                if (type == ReconstructionType.byDilation) {
                    marker[i] = Math.min(mask[i], max(marker, i + 1, i));
                } else if (type == ReconstructionType.byErosion) {
                    marker[i] = Math.max(mask[i], min(marker, i - 1, i));
                }
            }

            // backward scan
            for (int i = mask.length - 1; i >= 0; i--) {
                if (type == ReconstructionType.byDilation) {
                    marker[i] = Math.min(mask[i], max(marker, i - 1, i));
                } else if (type == ReconstructionType.byErosion) {
                    marker[i] = Math.max(mask[i], min(marker, i + 1, i));
                }
            }

            stability = Arrays.equals(marker, markerTmp);

            if (steps == 0) {
                break;
            }
        } while (!stability);

        return marker;
    }

//    private void maskCorrection(double[] mask, double[] marker) {
//        for (int i = 0; i < mask.length; i++) {
//            if (marker[i] == 0) {
//                mask[i] = 0;
//            }
//        }
//    }
    
    public double[] byDilation(int steps) {
        return reconstruction(ReconstructionType.byDilation, steps);
    }

    public double[] byDilation() {
        return byDilation(Integer.MAX_VALUE);
    }

    public double[] byErosion(int steps) {
        return reconstruction(ReconstructionType.byErosion, steps);
    }

    public double[] byErosion() {
        return byErosion(Integer.MAX_VALUE);
    }

    private double max(double[] array, int i1, int i2) {
        i1 = i1 < 0 ? 0 : i1;
        i1 = i1 >= array.length ? array.length - 1 : i1;
        return Math.max(array[i1], array[i2]);
    }

    private double min(double[] array, int i1, int i2) {
        i1 = i1 < 0 ? 0 : i1;
        i1 = i1 >= array.length ? array.length - 1 : i1;
        return Math.min(array[i1], array[i2]);
    }

    private double arrayMin(double[] array, int startIdx, int endIdx) {
        startIdx = startIdx < 0 ? 0 : startIdx;
        endIdx = endIdx >= array.length ? array.length - 1 : endIdx;

        double arrayMin = Double.POSITIVE_INFINITY;
        for (int i = startIdx; i <= endIdx; i++) {
            arrayMin = Math.min(arrayMin, array[i]);
        }
        return arrayMin;
    }

    private double[] copyMask() {
        return copySignal();
    }

    // reconstruction by erosion computed using complement of reconstruction by dilation
    // R^ε_g(f) = [R^δ_g^c (f^c)]^c
    public double[] byErosionUsingDilationComplement(int h) {
        double[] mask = copyMask();
        double[] marker = copy(inputMarker);

        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < marker.length; i++) {
            maxVal = Math.max(maxVal, marker[i]);
        }

        for (int i = 0; i < marker.length; i++) {
            marker[i] = maxVal - marker[i];
        }

        for (int i = 0; i < mask.length; i++) {
            mask[i] = maxVal - mask[i];
        }

        byDilation(h);

        for (int i = 0; i < marker.length; i++) {
            marker[i] = maxVal - marker[i];
        }

        return marker;
    }

    // test
    public static void main(String args[]) {

        double[] marker = {7, 1, 1, 1, 1, 1, 1, 1, 3, 0, 0, 0};
        double[] mask = {8, 4, 4, 3, 3, 5, 2, 2, 3, 2, 2, 4};
        Reconstruction r = new Reconstruction(mask, marker);

        double[] dilation = r.byDilation();

        marker = new double[]{7, 7, 7, 4, 4, 4, 2, 2, 5, 5, 5, 9, 9, 9};
        mask = new double[]{0, 7, 2, 4, 4, 1, 0, 0, 1, 5, 2, 2, 9, 0};
        Reconstruction r2 = new Reconstruction(mask, marker);

        dilation = r2.byErosion();

        marker = new double[]{3, 3, 3, 3, 8, 4, 4, 4, 4, 4, 4, 2, 8, 8, 8, 8, 8, 8};
        mask = new double[]{5, 5, 7, 7, 7, 8, 9, 4, 6, 6, 3, 3, 2, 0, 0, 7, 7, 7};
        Reconstruction r3 = new Reconstruction(mask, marker);

    }

    private enum ReconstructionType {

        byDilation,
        byErosion;
    }

}
