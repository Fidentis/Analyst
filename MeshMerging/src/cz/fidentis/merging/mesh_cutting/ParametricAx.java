package cz.fidentis.merging.mesh_cutting;

/**
 *
 * @author matej
 */
public class ParametricAx {

    private final double start;
    private final double step;

    public ParametricAx(double start, double step) {
        this.start = start;
        this.step = step;
    }

    public double getTimeIn(double value) {
        return (value - start) / step;
    }

    public double getValueIn(double time) {
        return start + time * step;
    }

}
