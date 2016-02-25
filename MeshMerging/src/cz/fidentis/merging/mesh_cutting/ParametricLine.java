package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.bounding.bvh.CoordinateStrategy;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public class ParametricLine {

    private final Vector3 step;
    private final Coordinates origin;

    public ParametricLine(Vector3 vector, Coordinates coords) {
        step = vector;
        origin = coords;
    }

    ParametricLine(Coordinates from, Coordinates to) {
        this(new Vector3(from, to), from);
    }

    public ParametricLine(HalfEdge halfEdge) {
        this(halfEdge.getBeginingPosition(), halfEdge.getEndPosition());
    }

    public double getTimeIn(double value, CoordinateStrategy strategy) {
        return (value - strategy.getValue(origin)) / strategy.getValue(step);
    }

    public double getValueIn(double time, CoordinateStrategy strategy) {
        return strategy.getValue(origin) + time * strategy.getValue(step);
    }

    boolean areCollinear(ParametricLine other) {
        return step.cosineOfAngel(other.step) == 1.d;
    }

}
