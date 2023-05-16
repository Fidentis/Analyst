/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.decimation;

/**
 * Simple wrapper that identifies a valid pair for edge collapse.
 * @author Marek Zuzi
 */
public class Edge {
    private int from;
    private int to;
    
    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }
    
    public int getFrom() {
        return from;
    }
    
    public int getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        int addition = from + to;
        hash = 73 * hash + addition;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        return (this.from == other.from && this.to == other.to) || (this.from == other.to && this.to == other.from);
    }
    
    
}
