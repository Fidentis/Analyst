/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.visualisation.surfaceComparison;

import cz.fidentis.model.Graph2;
import cz.fidentis.model.Model;
import cz.fidentis.visualisation.ColorScheme;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 * Class containing information for visalisation of Color Maps
 * 
 */
public class HDpaintingInfo {
    private List<Float> distance;
    private final Model model;
  //  private float threshPecent = 1f;
    private float maxThreshValue = Float.POSITIVE_INFINITY; 
    private float minThreshValue = Float.NEGATIVE_INFINITY; 
    private float minSelection;
    private float maxSelection;
    private boolean isSelection = false;
    private boolean isRecomputed = false;
    ArrayList<Integer> selectionVertices= new ArrayList<>();

    private float[] minColor = {0.5f, 0, 0};
    private float[] maxColor = {0, 1f, 0};
    private float[] vector = {maxColor[0] - minColor[0],
        maxColor[1] - minColor[1],
        maxColor[2] - minColor[2]};

    
    private boolean useRelative;

    private VisualizationType vType = VisualizationType.COLORMAP;
    private ColorScheme colorScheme = ColorScheme.GREEN_BLUE; 
    private SelectionType sType = SelectionType.RECTANGLE;

   // VECTORS VISUALIZATION VARIABLES
    public Graph2 graph;
    public Cylinder c = new Cylinder();
    
    private float density; 
    int[][] indicesForNormals;
    
    private float cylRadius;
    private float cylLengthFactor;

    private boolean recompute = true;
    //END
    
    public HDpaintingInfo(List<Float> distance, Model model, boolean useRelative) {
        this.distance = distance;
        this.model = model;
        this.useRelative = useRelative;
    }
    
    public float getCylRadius() {
        return cylRadius;
    }

    public void setCylRadius(float cylRadius) {
        this.cylRadius = cylRadius;
    }

    public float getCylLengthFactor() {
        return cylLengthFactor;
    }

    public void setCylLengthFactor(float cylLengthFactor) {
        this.cylLengthFactor = cylLengthFactor;
    }
    
    public boolean getRecompute() {
        return recompute;
    }

    public void setRecompute(boolean recompute) {
        this.recompute = recompute;
    }

    public Graph2 getGraph() {
        return graph;
    }

    public void setGraph(Graph2 graph) {
        this.graph = graph;
    }


    public void setSelectionCone(Vector3f[] selectionCone) {
        Vector3f c0 =   new Vector3f(selectionCone[0]); c0.sub(selectionCone[4]);
        Vector3f c1 =  new Vector3f(selectionCone[1]); c1.sub(selectionCone[4]);
        Vector3f c2 =  new Vector3f(selectionCone[2]); c2.sub(selectionCone[4]);
        Vector3f c3 =  new Vector3f(selectionCone[3]); c3.sub(selectionCone[4]);
        
        Vector3f n1  = new Vector3f(); n1.cross(c0, c3);
        Vector3f n2  = new Vector3f();n2.cross(c1, c2);
        Vector3f n3  = new Vector3f();n3.cross(c0, c1);
        Vector3f n4  = new Vector3f();n4.cross(c3, c2);
        
       selectionVertices= new ArrayList<>();
       for (int i = 0;i<model.getVerts().size(); i++){
            Vector3f p = new Vector3f(model.getVerts().get(i));
            p.sub(selectionCone[4]);
            float d1  = p.dot(n1);
            float d2  = p.dot(n2);
            float d3  = p.dot(n3);
            float d4  = p.dot(n4);           
            
           if((Math.signum(d1)!=Math.signum(d2))&&(Math.signum(d3)!=Math.signum(d4))){
              selectionVertices.add(i);
           }
       }    

    }

    public ArrayList<Integer> getSelectionVertices() {
        return selectionVertices;
    }

    public float getMaxThreshValue() {
        return maxThreshValue;
    }
     public float getMinThreshValue() {
        return minThreshValue;
    } 
   
    public float getMinDistance(){
        float min = Float.POSITIVE_INFINITY;
        for (Float f : distance) {
            if (f<min){
                min = f;
            }
        }
         if (minThreshValue == Float.NEGATIVE_INFINITY) {
               return min;
        }else{
            return minThreshValue;
        }
    }
    
     public float getMaxDistance(){
        float max = Float.NEGATIVE_INFINITY;
        for (Float f : distance) {
            if (f>max){
                max = f;
            }
        }
        if (maxThreshValue == Float.POSITIVE_INFINITY) {
               return max;
        }else{
            return maxThreshValue;
        }
    }
    

    public void setMaxThreshValue(float maxThreshValue) {
        this.maxThreshValue = maxThreshValue;
    }   
    
    public void setMinThreshValue(float minThreshValue) {
        this.minThreshValue = minThreshValue;
    } 
    
    public int[][] getIndicesForNormals() {
        return indicesForNormals;
    }

    public void setIndicesForNormals(int[][] indicesForNormals) {
        this.indicesForNormals = indicesForNormals;
    }

    public List<Float> getDistance() {
        return distance;
    }

    public void setDistance(List<Float> distance) {
        this.distance = distance;
    }

    public float getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(float minSelection) {
        this.minSelection = minSelection;
         minThreshValue = minSelection;
    }

    public float getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(float maxSelection) {
        this.maxSelection = maxSelection;
        maxThreshValue = maxSelection;
    }

    public boolean isIsSelection() {          
        return isSelection;
    }

    public void setIsSelection(boolean isSelection) {
        maxThreshValue = Float.POSITIVE_INFINITY;
        minThreshValue = Float.NEGATIVE_INFINITY;
        minSelection = getMinDistance();
        maxSelection = getMaxDistance();
        maxThreshValue = maxSelection;
        minThreshValue = minSelection;
        this.isSelection = isSelection;
    }


    public boolean isUseRelative() {
        return useRelative;
    }

    public void setUseRelative(boolean useRelative) {
        this.useRelative = useRelative;
    }

    public VisualizationType getvType() {
        return vType;
    }

    public void setvType(VisualizationType vType) {
        this.vType = vType;
    }

    public Model getModel() {
        return model;
    }   

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public boolean isRecomputed() {
        return isRecomputed;
    }

    public void setIsRecomputed(boolean isRecomputed) {
        this.isRecomputed = isRecomputed;
    }

    public SelectionType getsType() {
        return sType;
    }

    public void setsType(SelectionType sType) {
        this.sType = sType;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }
    
    public float[] getMinColor() {
        return minColor;
    }

    public void setMinColor(float[] minColor) {
        this.minColor = minColor;
        vector = new float[]{maxColor[0] - minColor[0],
            maxColor[1] - minColor[1],
            maxColor[2] - minColor[2]};
    }

    public float[] getMaxColor() {
        return maxColor;
    }

    public void setMaxColor(float[] maxColor) {
        this.maxColor = maxColor;
        vector = new float[]{maxColor[0] - minColor[0],
            maxColor[1] - minColor[1],
            maxColor[2] - minColor[2]};
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }    
}
