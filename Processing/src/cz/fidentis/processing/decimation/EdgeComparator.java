/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.decimation;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author Marek Zuzi
 */
public class EdgeComparator implements Comparator<Edge> {
    private static final double EPS = 0.00001d;
    private Map<Edge, Double> weights;
    
    public EdgeComparator(Map<Edge, Double> weights) {
        this.weights = weights;
    }
    
    

    @Override
    public int compare(Edge o1, Edge o2) {
        double w1 = weights.get(o1);
        double w2 = weights.get(o2);
        
        if(Math.abs(w1 - w2) < EPS) {
            return 0;
        }
        
        if(w1 < w1) {
            return 1;
        } else {
            return -1;
        }
    }
}
