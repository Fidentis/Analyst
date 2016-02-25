package cz.fidentis.merging.bounding;

import cz.fidentis.merging.bounding.bvh.CoordinateStrategy;
import cz.fidentis.merging.bounding.bvh.FaceForBVH;
import cz.fidentis.merging.mesh_cutting.ParametricLine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class Boundary {

    private double max;
    private double min;
    private final CoordinateStrategy coordStrategy;
    private final ArrayList<Boundary> others = new ArrayList<>();

    public Boundary(CoordinateStrategy strategy) {
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        coordStrategy = strategy;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public void update(FaceForBVH face) {
        double maxValue = coordStrategy.getMaxValue(face);
        if (max < maxValue) {
            max = maxValue;
        }
        double minValue = coordStrategy.getMinValue(face);
        if (min > minValue) {
            min = minValue;
        }
    }

    public boolean contains(double value) {
        return max >= value && value >= min;
    }

    void pairTogether(Boundary other) {
        others.add(other);
        other.others.add(this);
    }

    Collection<Boundary> getBoundaries() {
        LinkedList<Boundary> result = new LinkedList(others);
        result.add(this);
        return result;
    }

    public boolean intersectsWith(ParametricLine line) {
        return intersectsAtCoordinate(line, max)
                || intersectsAtCoordinate(line, min);
    }

    private boolean intersectsAtCoordinate(ParametricLine line, double coord) {
        double param = line.getTimeIn(coord, coordStrategy);
        for (Boundary otherBoundery : others) {
            if (!otherBoundery.intersectsWithForParameter(line, param)) {
                return false;
            }
        }
        return true;
    }

    private boolean intersectsWithForParameter(ParametricLine line, double param) {
        double value = line.getValueIn(param, coordStrategy);
        return min <= value && value <= max;
    }

    void setMinMax(AABB aabb) {
        coordStrategy.setMinMax(aabb, min, max);
    }

}
