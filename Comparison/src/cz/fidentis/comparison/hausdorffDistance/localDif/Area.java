package cz.fidentis.comparison.hausdorffDistance.localDif;

import java.util.List;

/**
 *
 * @author Rasto
 */
public class Area {
    private int index;
    private List<Integer> vertices;
    private List<Float> csvValues;
    private List<Float> color;
    private float geoMean, ariMean, percentileSevFiv, min, max, rootMean, variance;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public void setVertices(List<Integer> vertices) {
        this.vertices = vertices;
    }

    public List<Float> getCsvValues() {
        return csvValues;
    }

    public void setCsvValues(List<Float> csvValues) {
        this.csvValues = csvValues;
    }

    public List<Float> getColor() {
        return color;
    }

    public void setColor(List<Float> color) {
        this.color = color;
    }

    public float getGeoMean() {
        return geoMean;
    }

    public void setGeoMean(float geoMean) {
        this.geoMean = geoMean;
    }

    public float getAriMean() {
        return ariMean;
    }

    public void setAriMean(float ariMean) {
        this.ariMean = ariMean;
    }

    public float getPercentileSevFiv() {
        return percentileSevFiv;
    }

    public void setPercentileSevFiv(float percentileSevFiv) {
        this.percentileSevFiv = percentileSevFiv;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getRootMean() {
        return rootMean;
    }

    public void setRootMean(float rootMean) {
        this.rootMean = rootMean;
    }

    public float getVariance() {
        return variance;
    }

    public void setVariance(float variance) {
        this.variance = variance;
    }
    
    
    
    @Override
    public String toString() {
        String str = "\nArea-> index: " + index + "\nvertices: " + vertices.toString()
                + "\ncsvValues: " + csvValues.toString() + "\nGeometric Mean: " + geoMean + "\nArithmetic Mean: " + ariMean + 
                "\nMaximal value: " + max + "\nMinimal value: " + min + "\n75 percentil: " + percentileSevFiv + "\nRoot Mean Square: " 
                + rootMean + "\nVariance: " + variance + "\n";

        return str;
    }
}
