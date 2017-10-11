package cz.fidentis.comparison.localAreas;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * XML serialized class
 * @author Rasto & Richard 
 */
@XmlRootElement(name="Area")
@XmlAccessorType(XmlAccessType.FIELD)
public class Area {
    public int index;
    public List<Integer> vertices;
    public List<Float> csvValues;
    public List<Float> color;
    public float geoMean, ariMean, percentileSevFiv, min, max, rootMean, variance;
    
    //@XmlAttribute(required=true)
    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }
    
    //@XmlElement(required=true)
    public List<Integer> getVertices(){
        return vertices;
    }

    public void setVertices(List<Integer> vertices){
        this.vertices = vertices;
    }
    
    //@XmlElement(required=true)
    public List<Float> getCsvValues(){
        return csvValues;
    }

    public void setCsvValues(List<Float> csvValues){
        this.csvValues = csvValues;
    }
    
    //@XmlElement(required=true)
    public List<Float> getColor(){
        return color;
    }

    public void setColor(List<Float> color){
        this.color = color;
    }
    
    //@XmlElement(required=true)
    public float getGeoMean(){
        return geoMean;
    }

    public void setGeoMean(float geoMean){
        this.geoMean = geoMean;
    }
    
    //@XmlElement(required=true)
    public float getAriMean(){
        return ariMean;
    }

    public void setAriMean(float ariMean){
        this.ariMean = ariMean;
    }
    
    //@XmlElement(required=true)
    public float getPercentileSevFiv(){
        return percentileSevFiv;
    }

    public void setPercentileSevFiv(float percentileSevFiv){
        this.percentileSevFiv = percentileSevFiv;
    }
    
    //@XmlElement(required=true)
    public float getMin(){
        return min;
    }

    public void setMin(float min){
        this.min =  min;
    }
    
    //@XmlElement(required=true)
    public float getMax(){
        return max;
    }

    public void setMax(float max){
        this.max = max;
    }
    
    //@XmlElement(required=true)
    public float getRootMean(){
        return rootMean;
    }

    public void setRootMean(float rootMean){
        this.rootMean = rootMean;
    }
    
    //@XmlElement(required=true)
    public float getVariance(){
        return variance;
    }
    
    public void setVariance(float variance){
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
