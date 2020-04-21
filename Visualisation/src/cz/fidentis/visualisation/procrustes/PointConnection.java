/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation.procrustes;

import cz.fidentis.featurepoints.FacialPointType;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * Represents the connection of two facial points
 */
public class PointConnection {
    private Integer start;
    private Integer end;
    private Integer configuration;

    public PointConnection(Integer start) {
        this.start = start;
    }

    public PointConnection(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public PointConnection(Integer start, Integer end, Integer configuration) {
        this.start = start;
        this.end = end;
        this.configuration = configuration;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Integer configuration) {
        this.configuration = configuration;
    }
    
    public static Set<PointConnection> getDefaultPointConnections() {
        Set<PointConnection> connections = new HashSet();
        
        connections.add(new PointConnection(FacialPointType.PRN.ordinal(), FacialPointType.N.ordinal(),-1));
        connections.add(new PointConnection(FacialPointType.N.ordinal(), FacialPointType.G.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.PRN.ordinal(), FacialPointType.LS.ordinal(), -1));
 
        //brada
        connections.add(new PointConnection(FacialPointType.LI.ordinal(), FacialPointType.SL.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.SL.ordinal(), FacialPointType.PG.ordinal(), -1));

        //usta
        connections.add(new PointConnection(FacialPointType.CH_R.ordinal(), FacialPointType.STO.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.STO.ordinal(), FacialPointType.CH_L.ordinal(), -1));

        connections.add(new PointConnection(FacialPointType.CH_R.ordinal(), FacialPointType.LS.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.LS.ordinal(), FacialPointType.CH_L.ordinal(), -1));

        connections.add(new PointConnection(FacialPointType.CH_R.ordinal(), FacialPointType.LI.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.LI.ordinal(), FacialPointType.CH_L.ordinal(), -1));

        connections.add(new PointConnection(FacialPointType.LS.ordinal(), FacialPointType.STO.ordinal(), -1));
        connections.add(new PointConnection(FacialPointType.STO.ordinal(), FacialPointType.LI.ordinal(), -1));

        
        connections.add(new PointConnection(FacialPointType.EX_L.ordinal(), FacialPointType.EN_L.ordinal(), -1));
 
        connections.add(new PointConnection(FacialPointType.EX_R.ordinal(), FacialPointType.EN_R.ordinal(), -1));
        
        
        return connections;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.start) + Objects.hashCode(this.end) + Objects.hashCode(this.configuration);
        return hash;
    }
    
    public boolean isSameConfig(Integer config) {
        if (config == null) {
            if (this.configuration == null) {
                return true;
            }
            return false;
        }
        return this.configuration.equals(config);
        //return Objects.equals(this.configuration, config);
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
        final PointConnection other = (PointConnection) obj;
        if (!Objects.equals(this.configuration, other.configuration)) {
            return false;
        }
        if (Objects.equals(this.start, other.start) && Objects.equals(this.end, other.end)) {
            return true;
        }
        if (Objects.equals(this.start, other.end) && Objects.equals(this.end, other.start)) {
            return true;
        }
        return false;
    }
    
    
}
