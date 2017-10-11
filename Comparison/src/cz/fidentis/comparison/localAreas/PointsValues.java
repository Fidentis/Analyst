package cz.fidentis.comparison.localAreas;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class for points distribution in histogram
 * @author Richard
 */
public class PointsValues {
    public List<Integer> indexes;
    public List<Float> values;
    public List<Integer> clusterMark;

    public List<List<Integer>> distribution;
    public List<String> distributionBoundaries;
    public List<Color> distributionColor;
    public float clusterIndex;
    public int maxClusteredPoints;

    public float min;
    public float max;
    
    public Color colorMin;
    public Color colorMax;

    public PointsValues(){
        indexes = new ArrayList<>();
        values = new ArrayList<>();
        distribution = new ArrayList<>();
        distributionBoundaries = new ArrayList<>();
        clusterMark = new ArrayList<>();
        distributionColor = new ArrayList<>();
        
        colorMin = new Color(0, 0, 255);
        colorMax = new Color(0, 255, 0);
    }

    /**
     * Copy CSV values and indexes
     * @param values 
     */
    public void setValues(List<Float> values){
        this.values = deepCopy(values);
        indexes = new ArrayList<>();
        for (int i = 0; i < values.size(); i++ ){
            indexes.add(i);
        }
    }

    /**
     * order indexes and CSV values max first
     */
    public void selectionSort() {
        for (int i = 0; i < values.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < values.size(); j++) {
                if (values.get(j) > values.get(maxIndex)){
                    maxIndex = j;
                }
            }
            float tmp = values.get(i);
            values.set(i, values.get(maxIndex));
            values.set(maxIndex, tmp);

            int temp = indexes.get(i);
            indexes.set(i, indexes.get(maxIndex));
            indexes.set(maxIndex, temp);
        }
        max = values.get(0);
        min = values.get(values.size()-1);    
    }

    /**
     * set color of each CSV value
     */
    public void setColors(){
        distributionColor = new ArrayList<>();
        
        float step = 1.0f / (distribution.size());

        float steps = 0.0f;
        for (int i = 0; i < distribution.size(); i++){
            steps += step;
            
            distributionColor.add(LerpRGB(colorMax, colorMin, steps));
        }

    }

    /**
     * Recalculate distribution of CSV values
     * @param range number of columns
     * @param size width of Canvas
     */
    public void calculateDistribution(float range, int size){
        distribution = new ArrayList<>();
        distributionBoundaries = new ArrayList<>();
        clusterIndex = range / ((float)size/5.0f);

        int numberOfCollumns = (size/5);
        int index = 0;
        maxClusteredPoints = 0;

        for (int i = 0; i < numberOfCollumns; i++){
            float upValue = values.get(0)-(i)*clusterIndex;
            float downValue = values.get(0)-(i+1)*clusterIndex;
         
            List<Integer> Items = new ArrayList<>();
            
            while ((index < values.size() && upValue >= values.get(index) && downValue <= values.get(index))||(index < values.size() && i == numberOfCollumns-1)){
                Items.add(indexes.get(index));
                index++;
                
            }

            //maximal number of elements in one collumn
            if (maxClusteredPoints<=Items.size()){
                maxClusteredPoints = Items.size();
            }
            
            //adding to collumn
            distribution.add(Items);
            DecimalFormat df = new DecimalFormat("#.###");
            df.setRoundingMode(RoundingMode.CEILING);

            distributionBoundaries.add("<"+df.format(downValue)+";"+df.format(upValue)+")");
        }

        if (values.size() == 1){
            distribution.set(distribution.size()/2, distribution.get(0));
            distribution.set(0, new ArrayList<Integer>());
        }

        this.setColors();
    }
    
    /**
     * Deep copy for CSV values
     * @param values
     * @return 
     */
    private static List<Float> deepCopy(List<Float> values){
        List<Float> result = new ArrayList<>();
        
        for (int i = 0; i < values.size(); i++){
            result.add(values.get(i));
        }
        
        return result;
    }
    
    /**
     * Nice color distribution
     * @param a minimal color 
     * @param b maximal color 
     * @param t step <0, 1>
     * @return 
     */
    private static Color LerpRGB (Color a, Color b, float t){
        float r1 = (float)a.getRed()/255f;
        float r2 = (float)b.getRed()/255f;
        float g1 = (float)a.getGreen()/255f;
        float g2 = (float)b.getGreen()/255f;
        float b1 = (float)a.getBlue()/255f;
        float b2 = (float)b.getBlue()/255f;
        
	return new Color
	(
		r1 + (r2 - r1) * t,
		g1 + (g2 - g1) * t,
		b1 + (b2 - b1) * t,
		1.0f
	);
    }
}
