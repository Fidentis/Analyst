/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.bounding.bvh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import java.util.Comparator;

/**
 *
 * @author matej
 */
public class FromRightToLeftComparator implements Comparator<AbstractFace> {

    private final CoordinateStrategy spliter;

    public FromRightToLeftComparator(CoordinateStrategy spliter) {
        this.spliter = spliter;
    }

    @Override
    public int compare(AbstractFace face1, AbstractFace face2) {
        double leftMostValue1 = spliter.getLeftMostValue(face2);
        double leftMostValue2 = spliter.getLeftMostValue(face2);
        if (leftMostValue1 <= leftMostValue2) {
            return -1;
        }
        return 1;
    }

}
