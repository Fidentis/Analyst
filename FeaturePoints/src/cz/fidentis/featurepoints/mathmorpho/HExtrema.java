package cz.fidentis.featurepoints.mathmorpho;

/**
 *
 * @author Galvanizze
 */
public class HExtrema extends MorphoOperator {

    public HExtrema(double[] signal) {
        super(signal);
    }

    public HExtrema(double[] signal, boolean normalize) {
        super(signal, normalize);
    }

    public double[] hMaxima(double h) {
        double[] marker = new double[signal.length];
        Reconstruction rec;

        for (int i = 0; i < signal.length; i++) {
            marker[i] = signal[i] - h;
        }

        rec = new Reconstruction(signal, marker);

        return rec.byDilation();
    }

    public double[] hMinima(double h) {
        double[] marker = new double[signal.length];
        Reconstruction rec;

        for (int i = 0; i < signal.length; i++) {
            marker[i] = signal[i] + h;
        }

        rec = new Reconstruction(signal, marker);

        return rec.byErosion();
    }

    public double[] hConvex(double h) {
        return hConvex(h, false);
    }

    public double[] hConvexThreshold(double h) {
        return hConvex(h, true);
    }

    private double[] hConvex(double h, boolean threshold) {
        double[] hMaxima = hMaxima(h);
        double[] hConvex = new double[hMaxima.length];

        for (int i = 0; i < hMaxima.length; i++) {
            double value = signal[i] - hMaxima[i];
            hConvex[i] = threshold == true && value >= h
                    ? value
                    : 0;
        }

        return hConvex;
    }

    public double[] hConcave(double h) {
        return hConcave(h, false);
    }

    public double[] hConcaveThreshold(double h) {
        return hConcave(h, true);
    }

    private double[] hConcave(double h, boolean threshold) {
        double[] hMinima = hMinima(h);
        double[] hConcave = new double[hMinima.length];

        for (int i = 0; i < hMinima.length; i++) {
            double value = hMinima[i] - signal[i];
            hConcave[i] = threshold == true && value >= h
                    ? value
                    : 0;
        }

        return hConcave;
    }
}