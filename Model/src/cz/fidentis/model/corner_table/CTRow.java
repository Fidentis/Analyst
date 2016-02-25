/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.model.corner_table;

/**
 * Private struct that resembles a row in the initial corner-table creation
 * routine
 */
public class CTRow implements Comparable<CTRow> {

    public Corner c;
    public int min;
    public int max;

    public CTRow(Corner corner) {
        c = corner;
        min = Math.min(c.prev.vertex, c.next.vertex);
        max = Math.max(c.prev.vertex, c.next.vertex);
    }

    /**
     * Sort first by min, then by max in ascending order
     */
    @Override
    public int compareTo(CTRow o) {
        if (min < o.min) {
            return -1;
        } else if (min == o.min) {
            if (max < o.max) {
                return -1;
            } else if (max == o.max) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

}
