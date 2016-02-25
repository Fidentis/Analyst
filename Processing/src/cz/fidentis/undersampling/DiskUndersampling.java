/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.undersampling;

import cz.fidentis.model.Graph2;
import cz.fidentis.model.Model;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author xferkova
 */
public class DiskUndersampling {
    private Graph2 graph;

    public DiskUndersampling(Model m) {
        this.graph = new Graph2(m);
        graph.createGraph();
    }
    
    /**
     * Creates list of integers representing indices of vertices of mesh
     * to be used for undersampling
     *  
     * @param value - density value for undersampling
     * @return list of indices representing vertices to use
     */
    public List<Integer> diskUndersampling(float value){
        int[][] indices = graph.indicesFordDensityNormals(value);
        
        List<Integer> resInd = new LinkedList<>();
        for(int i = 0; i < indices[0].length; i++){
            resInd.add(indices[0][i]);
        }
        
        return resInd;
    }  
    
}
