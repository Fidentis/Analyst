package cz.fidentis.merging.bounding;

import cz.fidentis.merging.bounding.bvh.CoordinateStrategy;
import java.util.Collection;

/**
 *
 * @author matej
 */
public class BoundaryFactory {

    public static Collection<Boundary> boundariesOfAABB() {
        Boundary xBoundary = new Boundary(CoordinateStrategy.createXstrategy());
        Boundary yBoundary = new Boundary(CoordinateStrategy.createYstrategy());
        Boundary zBoundary = new Boundary(CoordinateStrategy.createZstrategy());
        xBoundary.pairTogether(yBoundary);
        zBoundary.pairTogether(xBoundary);
        zBoundary.pairTogether(yBoundary);
        return xBoundary.getBoundaries();
    }
}
