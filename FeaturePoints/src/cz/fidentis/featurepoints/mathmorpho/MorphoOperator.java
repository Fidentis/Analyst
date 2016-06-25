package cz.fidentis.featurepoints.mathmorpho;

import java.util.Arrays;

/**
 *
 * @author Galvanizze
 */
public abstract class MorphoOperator {

    protected double[] signal;
    protected double min, max;

    public MorphoOperator(double[] signal, boolean normalize) {
        setSignal(signal, normalize);
    }

    public MorphoOperator(double[] signal) {
        this(signal, false);
    }

    public final void setSignal(double[] signal, boolean normalize) {
        this.signal = copy(signal);
        setMinMax();
        if (normalize) {
            normalize();
        }
    }

    public final void setSignal(double[] signal) {
        setSignal(signal, false);
    }

    private void normalize() {
        for (int i = 0; i < signal.length; i++) {
            signal[i] += -min;
        }
//        min += -min;
//        max += -min;
        setMinMax();
    }

    private void setMinMax() {
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < signal.length; i++) {
            min = Math.min(signal[i], min);
            max = Math.max(signal[i], max);
        }
    }

    public double[] getSignal() {
        return copySignal();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double[] getOriginal() {
        double[] original = copySignal();

        for (int i = 0; i < signal.length; i++) {
            original[i] += min;
        }
        return original;
    }

    protected final double[] copySignal() {
        return copy(signal);
    }

    protected final double[] copy(double[] signal) {
        return Arrays.copyOf(signal, signal.length);
    }

}
