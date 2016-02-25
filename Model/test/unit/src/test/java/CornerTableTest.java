/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java;

import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Galvanizze
 */
public class CornerTableTest {

//    private static final String MODEL_PATH = "./Model/test/unit/src/test/resources/simple_polygon.obj";
    private static final String MODEL_PATH = "/Users/Galvanizze/Documents/FI MUNI/fidentis 2015/Model/test/unit/src/test/resources/simple_polygon.obj";
    private final Model loadedModel;
    private final CornerTable cornerTable;

    public CornerTableTest() {
        ModelLoader ml = new ModelLoader();
        loadedModel = ml.loadModel(new File(MODEL_PATH), false, true);
        cornerTable = loadedModel.getCornerTable();
    }

    @Test
    public void testAdjacentFaces() {
//        Set<Integer> result = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7, 9, 11, 13, 14, 15, 18, 19));
//        Set<Integer> adjacentTriangles = cornerTable.getAdjacentTriangles(10);
        Set<Integer> result = new HashSet<>(Arrays.asList(5, 6, 8, 9, 10, 12, 14, 16, 17, 18));
        Set<Integer> adjacentTriangles = cornerTable.getAdjacentTriangles(13);

        assertTrue(result.equals(adjacentTriangles));
    }

    @Test
    public void printCorners() {
        ArrayList<Corner> corners = cornerTable.corners();
        for (Corner corner : corners) {
            System.out.println(corner.toString());
        }
    }

}
