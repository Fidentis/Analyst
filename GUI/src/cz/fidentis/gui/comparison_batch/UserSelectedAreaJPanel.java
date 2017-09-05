package cz.fidentis.gui.comparison_batch;

import cz.fidentis.comparison.hausdorffDistance.ComparisonMetrics;
import cz.fidentis.comparison.localAreas.Area;
import cz.fidentis.comparison.localAreas.VertexArea;
import static cz.fidentis.processing.comparison.surfaceComparison.SurfaceComparisonProcessing.computeSingleVariation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Arrays;
import javax.swing.JColorChooser;
import javax.swing.filechooser.FileSystemView;
import org.openide.util.Exceptions;

/**
 *
 * @author Richard
 */
public class UserSelectedAreaJPanel extends javax.swing.JPanel {
    private ComparisonMetrics metric;
    private ArrayList<ArrayList<Float>> HdVisualResults;
    private List<Integer> faceComparison;
    private List<String> modelsName;
    private int metricIndex;
    private List<String> metricName;
    private HistogramJPanel histogram;
    private List<Float> distances;
    private List<Integer> vertices;
    
    public UserSelectedAreaJPanel() {
        initComponents();
        metricName = new ArrayList<>();
        metricName.add("Root Mean Square");
        metricName.add("Arithmetic Mean");
        metricName.add("Geometric Mean");
        metricName.add("Minimal Distance");
        metricName.add("Maximal Distance");
        metricName.add("Variance");
        metricName.add("75 percentil");
        
        histogram = new HistogramJPanel();
        histogram.setSize(this.histogramHolder.getPreferredSize().width, this.histogramHolder.getPreferredSize().height);
        
        this.histogramHolder.setLayout(new BorderLayout());
        this.histogramHolder.add(histogram, BorderLayout.CENTER);
    }
    
    /**
     * Sets area data
     * @param exportArea
     * @param area
     * @param relative
     * @param HdVisualResults
     * @param models
     * @param metricIndex 
     */
    public void SetArea(List<Float> distances, List<Integer> vertices, Boolean relative, ArrayList<ArrayList<Float>> HdVisualResults, List<File> models, int metricIndex){
        this.distances = filterDistances(distances, vertices);
        this.vertices = vertices;
        this.metric = ComparisonMetrics.instance();
        this.HdVisualResults = HdVisualResults;

        faceComparison = calculateFaceComparison(HdVisualResults, vertices, metricIndex, relative);
        modelsName = filterModelName(models);
        this.metricIndex = metricIndex;
                
        this.labelAreaName.setText("User selected area");
        this.GeoMean.setText(""+ metric.geometricMean(this.distances, relative) +"");
        this.AriMean.setText(""+ metric.aritmeticMean(this.distances, relative) +"");
        this.RootMean.setText(""+ metric.rootMeanSqr(this.distances, relative) +"");
        this.Max.setText(""+ metric.findMaxDistance(this.distances, relative) +"");
        this.Min.setText(""+ metric.findMinDistance(this.distances, relative) +"");
        this.SeventyFive.setText(""+ metric.percentileSeventyFive(this.distances, relative) +"");
        this.Variance.setText(""+ metric.variance(this.distances, relative) +"");
        this.DifferentFace.setText(modelsName.get(faceComparison.get(0))+"");
        this.SimilarFace.setText(modelsName.get(faceComparison.get(faceComparison.size()-1))+"");
        this.MetricName.setText(metricName.get(metricIndex));

        this.histogram.setSize(this.histogramHolder.getWidth(), this.histogramHolder.getHeight());
        this.histogram.setValues(this.distances);
    }

    // <editor-fold defaultstate="collapsed" desc="Private methods"> 
    /**
     * Gets names of models
     * @param files
     * @return 
     */
    private static List<String> filterModelName(List<File> files){
        List<String> result = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++){
            int index = files.get(i).getPath().lastIndexOf("\\")+1;
            String text = files.get(i).getPath().substring(index, files.get(i).getPath().length());
            result.add(text);
        }
        
        return result;
    }
    
    /**
     * Calculate most and least similar face
     * @param HdVisualResults 
     * @param area
     * @param metricIndex minimal size of area
     * @param relative 
     * @return sorted array of similar faces
     */
    private static List<Integer> calculateFaceComparison(ArrayList<ArrayList<Float>> HdVisualResults, List<Integer> vertices, int metricIndex, boolean relative) {
        ComparisonMetrics metric = ComparisonMetrics.instance();
        List<Integer> result = new ArrayList<>();
        List<Float> averageDistance = new ArrayList<>();
        
        List<List<Float>> filteredArays = filterArrays(HdVisualResults, vertices);
        
        //calculate average distance
        for (int i = 0; i < filteredArays.size(); i++){
            float average = computeSingleVariation(filteredArays.get(i), metricIndex, relative);
  
            averageDistance.add(average);
        }
        
        for (int i = 0; i < filteredArays.size(); i++){
            result.add(i);
        }
        
        //selection sort
        for (int i = 0; i < averageDistance.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < averageDistance.size(); j++) {
                if (averageDistance.get(j) > averageDistance.get(maxIndex)){
                    maxIndex = j;
                } 
            }
            float tmp = averageDistance.get(i);
            averageDistance.set(i, averageDistance.get(maxIndex));
            averageDistance.set(maxIndex, tmp);
            
            int temp = result.get(i);
            result.set(i, result.get(maxIndex));
            result.set(maxIndex, temp);
        } 
        
        
        return result;
    }

    /**
     * Get CSV values from areas
     * @param arrays
     * @param area
     * @return CSV values
     */
    private static List<List<Float>> filterArrays(ArrayList<ArrayList<Float>> arrays, List<Integer> vertices){
        List<List<Float>> result = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++){
            ArrayList<Float> array = arrays.get(i);
            result.add(filterItems(array, vertices));
        }
        
        return result;
    }
    
    /**
     * Filtering area vertices
     * @param x list of CSV values
     * @param vertices given area
     * @return list of CSV values
     */
    private static List<Float> filterItems(ArrayList<Float> x, List<Integer> vertices){
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++){
            result.add(x.get(vertices.get(i)));
            
        }
        return result;
    }
    
    /**
     * CSV generator
     * @param writer
     * @param area
     * @return
     * @throws IOException 
     */
    private static FileWriter csvGenerator(FileWriter writer, List<Float> distances) throws IOException{
        int totalLength = distances.size();

        writer.write(writeRow("1 Area"));
        
        for (int j = 0; j < totalLength; j++){
            writer.write(writeRow(distances.get(j)+""));
        }

        return writer;
    }
    
    /**
     * CSV
     * @param values
     * @return 
     */
    private static String writeRow(String value){
        StringBuilder sb = new StringBuilder();

        
        sb.append(value);
        sb.append(',');
        
        sb.append('\n');

        return sb.toString();
        
    }
    
    private static List<Float> filterDistances(List<Float> distances, List<Integer> vertices) {
        List<Float> result = new ArrayList<>();
        
        for (int i = 0; i < vertices.size(); i++){
            result.add(distances.get(vertices.get(i)));
        }
        
        return result;
    }
    // </editor-fold> 
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonExport = new javax.swing.JButton();
        histogramHolder = new javax.swing.JPanel();
        labelAreaName3 = new javax.swing.JLabel();
        labelAreaName4 = new javax.swing.JLabel();
        labelAreaName5 = new javax.swing.JLabel();
        RootMean = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Max = new javax.swing.JLabel();
        labelAreaName6 = new javax.swing.JLabel();
        Min = new javax.swing.JLabel();
        labelAreaName7 = new javax.swing.JLabel();
        SeventyFive = new javax.swing.JLabel();
        GeoMean = new javax.swing.JLabel();
        Variance = new javax.swing.JLabel();
        AriMean = new javax.swing.JLabel();
        labelAreaName = new javax.swing.JLabel();
        labelAreaName1 = new javax.swing.JLabel();
        labelAreaName11 = new javax.swing.JLabel();
        labelAreaName2 = new javax.swing.JLabel();
        jButtonChangeColorMax = new javax.swing.JButton();
        labelAreaName8 = new javax.swing.JLabel();
        DifferentFace = new javax.swing.JLabel();
        labelAreaName9 = new javax.swing.JLabel();
        SimilarFace = new javax.swing.JLabel();
        labelAreaName10 = new javax.swing.JLabel();
        MetricName = new javax.swing.JLabel();
        jButtonChangeColorMin = new javax.swing.JButton();
        jButtonExport1 = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jButtonExport, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.jButtonExport.text")); // NOI18N
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        histogramHolder.setPreferredSize(new java.awt.Dimension(600, 200));

        javax.swing.GroupLayout histogramHolderLayout = new javax.swing.GroupLayout(histogramHolder);
        histogramHolder.setLayout(histogramHolderLayout);
        histogramHolderLayout.setHorizontalGroup(
            histogramHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        histogramHolderLayout.setVerticalGroup(
            histogramHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName3, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName4, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName5, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(RootMean, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.RootMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Max, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.Max.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName6, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Min, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.Min.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName7, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(SeventyFive, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.SeventyFive.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(GeoMean, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.GeoMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Variance, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.Variance.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(AriMean, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.AriMean.text")); // NOI18N

        labelAreaName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName1, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName11, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName2, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonChangeColorMax, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.jButtonChangeColorMax.text")); // NOI18N
        jButtonChangeColorMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeColorMaxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName8, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(DifferentFace, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.DifferentFace.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName9, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(SimilarFace, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.SimilarFace.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName10, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.labelAreaName10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(MetricName, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.MetricName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonChangeColorMin, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.jButtonChangeColorMin.text")); // NOI18N
        jButtonChangeColorMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeColorMinActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonExport1, org.openide.util.NbBundle.getMessage(UserSelectedAreaJPanel.class, "UserSelectedAreaJPanel.jButtonExport1.text")); // NOI18N
        jButtonExport1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExport1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(histogramHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonChangeColorMin, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelAreaName10)
                                    .addComponent(labelAreaName8)
                                    .addComponent(labelAreaName9))
                                .addGap(157, 157, 157)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(DifferentFace, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(MetricName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SimilarFace, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonChangeColorMax, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExport1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelAreaName6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelAreaName5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelAreaName4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelAreaName3)
                            .addComponent(labelAreaName1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelAreaName2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelAreaName7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(GeoMean, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(Variance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SeventyFive, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Min, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Max, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(RootMean, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(AriMean, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelAreaName, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelAreaName11, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelAreaName)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelAreaName2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAreaName7)
                        .addGap(27, 27, 27)
                        .addComponent(labelAreaName11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(histogramHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonChangeColorMin)
                                .addGap(18, 18, 18)
                                .addComponent(labelAreaName10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelAreaName8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelAreaName9))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonChangeColorMax)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(MetricName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DifferentFace)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SimilarFace)
                                .addGap(12, 12, 12)
                                .addComponent(jButtonExport1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GeoMean)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AriMean)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RootMean)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Max)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Min)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SeventyFive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Variance)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChangeColorMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeColorMaxActionPerformed
        Color color = JColorChooser.showDialog(null, "Change Area Color", null);
        histogram.setColorMax(color);
    }//GEN-LAST:event_jButtonChangeColorMaxActionPerformed

    private void jButtonChangeColorMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeColorMinActionPerformed

        Color color = JColorChooser.showDialog(null, "Change Area Color", null);
        histogram.setColorMin(color);
    }//GEN-LAST:event_jButtonChangeColorMinActionPerformed

    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(selectedFile.getAbsolutePath()+".csv");
                writer = csvGenerator(writer, distances);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }//GEN-LAST:event_jButtonExportActionPerformed

    private void jButtonExport1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExport1ActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(selectedFile.getAbsolutePath()+".csv");
                writer = csvGenerator(writer, distances);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }//GEN-LAST:event_jButtonExport1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AriMean;
    private javax.swing.JLabel DifferentFace;
    private javax.swing.JLabel GeoMean;
    private javax.swing.JLabel Max;
    private javax.swing.JLabel MetricName;
    private javax.swing.JLabel Min;
    private javax.swing.JLabel RootMean;
    private javax.swing.JLabel SeventyFive;
    private javax.swing.JLabel SimilarFace;
    private javax.swing.JLabel Variance;
    private javax.swing.JPanel histogramHolder;
    private javax.swing.JButton jButtonChangeColorMax;
    private javax.swing.JButton jButtonChangeColorMin;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonExport1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelAreaName;
    private javax.swing.JLabel labelAreaName1;
    private javax.swing.JLabel labelAreaName10;
    private javax.swing.JLabel labelAreaName11;
    private javax.swing.JLabel labelAreaName2;
    private javax.swing.JLabel labelAreaName3;
    private javax.swing.JLabel labelAreaName4;
    private javax.swing.JLabel labelAreaName5;
    private javax.swing.JLabel labelAreaName6;
    private javax.swing.JLabel labelAreaName7;
    private javax.swing.JLabel labelAreaName8;
    private javax.swing.JLabel labelAreaName9;
    // End of variables declaration//GEN-END:variables
}
