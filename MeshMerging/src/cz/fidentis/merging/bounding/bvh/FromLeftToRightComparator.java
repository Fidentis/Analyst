package cz.fidentis.merging.bounding.bvh;

import java.util.Comparator;

/**
 *
 * @author matej
 */
public class FromLeftToRightComparator implements Comparator<FaceForBVH> {

    private final CoordinateStrategy spliter;

    public FromLeftToRightComparator(CoordinateStrategy spliter) {
        this.spliter = spliter;
    }

    @Override
    public int compare(FaceForBVH face1, FaceForBVH face2) {
        double rightMostValue1 = spliter.getMaxValue(face1);
        double rightMostValue2 = spliter.getMaxValue(face2);
        if (rightMostValue1 < rightMostValue2) {
            return -1;
        } else if (rightMostValue1 > rightMostValue2) {
            return 1;
        }
        return 0;
    }

}
