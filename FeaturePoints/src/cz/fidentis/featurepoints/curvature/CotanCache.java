package cz.fidentis.featurepoints.curvature;
/*
 Copyright 2011 Milian Wolff <mail@milianw.de>
	
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of 
 the License, or (at your option) any later version.
	
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
	
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;

/**
 * Lookup table to cache repeated computations of tan/cotan on the same angle
 * values.
 */
public class CotanCache {
    
    private HashMap<Double, Double> map;

    public CotanCache(int size) {
        map = new HashMap<Double, Double>(size);
    }

    double cotan(double degree) {
        assert degree > 0 : degree;
        if (degree == 90) {
            return 0;
        }
        Double val = map.get(degree);
        if (val == null) {
            assert Math.tan(Math.toRadians(degree)) != 0 : degree;
            val = 1.0d / Math.tan(Math.toRadians(degree));
            assert !val.isNaN() : degree;
            assert !val.isInfinite() : degree;
            map.put(degree, val);
        }
        return val;
    }

    double tan(double degree) {
        if (degree == 0) {
            return 0;
        }
        assert degree > 0 : degree;
        return 1.0d / cotan(degree);
    }
}
