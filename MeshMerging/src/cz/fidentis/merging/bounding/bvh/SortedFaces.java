package cz.fidentis.merging.bounding.bvh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author matej
 */
public class SortedFaces {

    private final ArrayList<FaceForBVH> fromLeftToRight;
    private final int median;

    public SortedFaces(Collection<FaceForBVH> faces, CoordinateStrategy split) {

        fromLeftToRight = new ArrayList(faces);
        Collections.sort(fromLeftToRight, new FromLeftToRightComparator(split));

        median = (int) (fromLeftToRight.size() / 2);

    }

    Collection<FaceForBVH> getLeftFaces() {
        return fromLeftToRight.subList(0, median);
    }

    Collection<FaceForBVH> getRightFaces() {
        return fromLeftToRight.subList(median, fromLeftToRight.size());
    }

}
