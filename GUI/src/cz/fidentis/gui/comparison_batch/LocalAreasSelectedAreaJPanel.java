
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
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;
import org.openide.util.Exceptions;

/**
 *
 * @author Richard
 */
public class LocalAreasSelectedAreaJPanel extends javax.swing.JPanel {

    private String text;
    private LocalAreasJPanel pointerLocalAreasJPanel;
    private Area area;
    private ComparisonMetrics metric;
    private ArrayList<ArrayList<Float>> HdVisualResults;
    private List<Integer> faceComparison;
    private List<String> modelsName;
    private int metricIndex;
    private List<String> metricName;
    private HistogramJPanel histogram;
    private Color areaColor;
    private VertexArea exportArea;
    private boolean relative;
    
    /**
     * Creates new form LocalAreasSelectedPointJPanel
     */
    public LocalAreasSelectedAreaJPanel() {
        initComponents();
        text = "Point(x,y,z): [-1, -1, -1]";
        metricName = new ArrayList<>();
        metricName.add("Root Mean Square");
        metricName.add("Arithmetic Mean");
        metricName.add("Geometric Mean");
        metricName.add("Minimal Distance");
        metricName.add("Maximal Distance");
        metricName.add("Variance");
        metricName.add("75 percentil");
        histogram = new HistogramJPanel();
        histogram.setPointer(this);
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
    public void SetArea(VertexArea exportArea, Area area, Boolean relative, ArrayList<ArrayList<Float>> HdVisualResults, List<File> models, int metricIndex){
        this.exportArea = exportArea;
        this.area = area;
        this.metric = ComparisonMetrics.instance();
        this.HdVisualResults = HdVisualResults;
        this.relative = relative;
        
        area.geoMean = metric.geometricMean(area.csvValues, relative);
        area.ariMean = metric.aritmeticMean(area.csvValues, relative);
        area.percentileSevFiv = metric.percentileSeventyFive(area.csvValues, relative);
        area.max = metric.findMaxDistance(area.csvValues, relative);
        area.min = metric.findMinDistance(area.csvValues, relative);
        area.rootMean = metric.rootMeanSqr(area.csvValues, relative);
        area.variance = metric.variance(area.csvValues, relative);
        
        faceComparison = calculateFaceComparison(HdVisualResults, area, metricIndex, relative);
        modelsName = filterModelName(models);
        this.metricIndex = metricIndex;
                
        this.labelAreaName.setText("Area "+ area.index+"");
        this.GeoMean.setText(""+ area.geoMean+"");
        this.AriMean.setText(""+ area.ariMean+"");
        this.RootMean.setText(""+ area.rootMean+"");
        this.Max.setText(""+ area.max+"");
        this.Min.setText(""+ area.min+"");
        this.SeventyFive.setText(""+ area.percentileSevFiv+"");
        this.Variance.setText(""+ area.variance+"");
        this.DifferentFace.setText(modelsName.get(faceComparison.get(0))+"");
        this.SimilarFace.setText(modelsName.get(faceComparison.get(faceComparison.size()-1))+"");
        this.MetricName.setText(metricName.get(metricIndex));
        
        this.areaColor = new Color(area.color.get(0), area.color.get(1), area.color.get(2));
        this.histogram.setSize(this.histogramHolder.getWidth(), this.histogramHolder.getHeight());
        this.histogram.setValues(area.csvValues);
        //this.histogram.setColor(areaColor);
        
        setJList();
    }
    
    public void setPointerLocalAreasJPanel(LocalAreasJPanel pointerLocalAreasJPanel){
        this.pointerLocalAreasJPanel = pointerLocalAreasJPanel;
    }
        
    public void updateSelectedPoints(List<Integer> get) {
        pointerLocalAreasJPanel.updateSelectedPoints(get);
    }

    // <editor-fold defaultstate="collapsed" desc="Private methods"> 
    private void setColors() {
        pointerLocalAreasJPanel.setAreaColors(histogram.getPoints(), area);
    }
    
    private void setJList(){
        
        DefaultListModel listModel = new DefaultListModel();
        
        for (String item : modelsName){
            listModel.addElement(item);
        }
            
        jListUserFaces.setModel(listModel);
    }
    
    private void updateFacesDifferences(int[] SelectedAreas) {
        ArrayList<ArrayList<Float>> customArrayList = new ArrayList<>();
        
        for (int i = 0; i < SelectedAreas.length; i++){
            customArrayList.add(HdVisualResults.get(SelectedAreas[i]));
        }
        
        faceComparison = calculateFaceComparison(customArrayList, area, metricIndex, relative);
        this.DifferentFace.setText(modelsName.get(SelectedAreas[faceComparison.get(0)])+"");
        this.SimilarFace.setText(modelsName.get(SelectedAreas[faceComparison.get(faceComparison.size()-1)])+"");
        this.MetricName.setText(metricName.get(metricIndex));
    }

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
    private static List<Integer> calculateFaceComparison(ArrayList<ArrayList<Float>> HdVisualResults, Area area, int metricIndex, boolean relative) {
        ComparisonMetrics metric = ComparisonMetrics.instance();
        List<Integer> result = new ArrayList<>();
        List<Float> averageDistance = new ArrayList<>();
        
        List<List<Float>> filteredArays = filterArrays(HdVisualResults, area);
        
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
    private static List<List<Float>> filterArrays(ArrayList<ArrayList<Float>> arrays, Area area){
        List<List<Float>> result = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++){
            ArrayList<Float> array = arrays.get(i);
            result.add(filterItems(array, area));
        }
        
        return result;
    }
    
    /**
     * Filtering area vertices
     * @param x list of CSV values
     * @param area given area
     * @return list of CSV values
     */
    private static List<Float> filterItems(ArrayList<Float> x, Area area){
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < area.vertices.size(); i++){
            result.add(x.get(area.vertices.get(i)));
            
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
    private static FileWriter csvGenerator(FileWriter writer, Area area) throws IOException{
        int totalLength = totalLength = area.csvValues.size();
        
        List<String> values = Arrays.asList(new String[1]);
        values.set(0, area.index+" Area");

        writer.write(writeRow(values));
        
        for (int j = 0; j < totalLength; j++){
            List<String> csvValues = Arrays.asList(new String[1]);
            csvValues.set(0, area.csvValues.get(j)+"");
            writer.write(writeRow(csvValues));
        }

        return writer;
    }
    
    /**
     * CSV
     * @param values
     * @return 
     */
    private static String writeRow(List<String> values){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.size(); i++){
            sb.append(values.get(i));
            sb.append(',');
        }
        sb.append('\n');

        return sb.toString();
        
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

        jPanel1 = new javax.swing.JPanel();
        MetricName = new javax.swing.JLabel();
        histogramHolder = new javax.swing.JPanel();
        labelAreaName1 = new javax.swing.JLabel();
        labelAreaName11 = new javax.swing.JLabel();
        labelAreaName2 = new javax.swing.JLabel();
        jButtonChangeColorMin = new javax.swing.JButton();
        labelAreaName3 = new javax.swing.JLabel();
        jButtonAreaColorChange = new javax.swing.JButton();
        labelAreaName4 = new javax.swing.JLabel();
        jButtonChangeColorMax = new javax.swing.JButton();
        labelAreaName5 = new javax.swing.JLabel();
        labelAreaName6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelAreaName7 = new javax.swing.JLabel();
        jRadButRelativeValuesYes = new javax.swing.JRadioButton();
        GeoMean = new javax.swing.JLabel();
        jRadButRelativeValuesNo = new javax.swing.JRadioButton();
        AriMean = new javax.swing.JLabel();
        RootMean = new javax.swing.JLabel();
        Max = new javax.swing.JLabel();
        Min = new javax.swing.JLabel();
        SeventyFive = new javax.swing.JLabel();
        Variance = new javax.swing.JLabel();
        jButtonExport = new javax.swing.JButton();
        labelAreaName8 = new javax.swing.JLabel();
        DifferentFace = new javax.swing.JLabel();
        labelAreaName9 = new javax.swing.JLabel();
        SimilarFace = new javax.swing.JLabel();
        labelAreaName10 = new javax.swing.JLabel();
        labelAreaName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListUserFaces = new javax.swing.JList<>();
        jButtonSelectAllFaces = new javax.swing.JButton();
        jButtonUserSelectFaces = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(600, 700));
        setPreferredSize(new java.awt.Dimension(600, 800));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(MetricName, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.MetricName.text")); // NOI18N

        histogramHolder.setPreferredSize(new java.awt.Dimension(600, 200));

        javax.swing.GroupLayout histogramHolderLayout = new javax.swing.GroupLayout(histogramHolder);
        histogramHolder.setLayout(histogramHolderLayout);
        histogramHolderLayout.setHorizontalGroup(
            histogramHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        histogramHolderLayout.setVerticalGroup(
            histogramHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 244, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName1, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName11, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName2, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonChangeColorMin, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonChangeColorMin.text")); // NOI18N
        jButtonChangeColorMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeColorMinActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName3, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAreaColorChange, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonAreaColorChange.text")); // NOI18N
        jButtonAreaColorChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAreaColorChangeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName4, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonChangeColorMax, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonChangeColorMax.text")); // NOI18N
        jButtonChangeColorMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeColorMaxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName5, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName6, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName7, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName7.text")); // NOI18N

        jRadButRelativeValuesYes.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadButRelativeValuesYes, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jRadButRelativeValuesYes.text")); // NOI18N
        jRadButRelativeValuesYes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadButRelativeValuesYesMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(GeoMean, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.GeoMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jRadButRelativeValuesNo, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jRadButRelativeValuesNo.text")); // NOI18N
        jRadButRelativeValuesNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadButRelativeValuesNoMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(AriMean, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.AriMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(RootMean, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.RootMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Max, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.Max.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Min, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.Min.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(SeventyFive, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.SeventyFive.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(Variance, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.Variance.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonExport, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonExport.text")); // NOI18N
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName8, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(DifferentFace, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.DifferentFace.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName9, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(SimilarFace, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.SimilarFace.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName10, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName10.text")); // NOI18N

        labelAreaName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelAreaName, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.labelAreaName.text")); // NOI18N

        jListUserFaces.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jListUserFaces);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSelectAllFaces, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonSelectAllFaces.text")); // NOI18N
        jButtonSelectAllFaces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectAllFacesActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUserSelectFaces, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jButtonUserSelectFaces.text")); // NOI18N
        jButtonUserSelectFaces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserSelectFacesActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Trajan Pro", 2, 13)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/fidentis/gui/comparison_batch/if_help_49832 (1).png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(LocalAreasSelectedAreaJPanel.class, "LocalAreasSelectedAreaJPanel.jLabel2.toolTipText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButtonChangeColorMin, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonChangeColorMax, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonUserSelectFaces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonSelectAllFaces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(labelAreaName10)
                                    .addGap(204, 204, 204))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelAreaName8)
                                        .addComponent(labelAreaName9))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(SimilarFace, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(DifferentFace, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(164, 164, 164)
                                .addComponent(MetricName, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(122, 122, 122)
                        .addComponent(jRadButRelativeValuesYes)
                        .addGap(18, 18, 18)
                        .addComponent(jRadButRelativeValuesNo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonExport, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(histogramHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(labelAreaName6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelAreaName5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelAreaName4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelAreaName3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelAreaName1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelAreaName2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelAreaName7))
                                .addComponent(labelAreaName11))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(GeoMean, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(Variance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                            .addComponent(SeventyFive, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(Min, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(Max, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(RootMean, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(AriMean, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGap(23, 23, 23))
                                .addComponent(jButtonAreaColorChange, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(labelAreaName, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGap(18, 18, 18)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(histogramHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonChangeColorMin)
                            .addComponent(jButtonChangeColorMax))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(MetricName)
                                    .addComponent(labelAreaName10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(DifferentFace)
                                    .addComponent(labelAreaName8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(SimilarFace)
                                    .addComponent(labelAreaName9)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(jButtonUserSelectFaces)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonSelectAllFaces))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadButRelativeValuesYes)
                    .addComponent(jRadButRelativeValuesNo)
                    .addComponent(jLabel4)
                    .addComponent(jButtonExport))
                .addGap(17, 17, 17))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(21, 21, 21)
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
                            .addComponent(labelAreaName7))
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
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonAreaColorChange)
                        .addComponent(labelAreaName11))
                    .addContainerGap(544, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (this.histogram != null && area != null){
            histogram.setSize(this.histogramHolder.getWidth(), this.histogramHolder.getHeight());
            this.histogram.setValues(area.csvValues);
            //this.setColors();
        }
        
    }//GEN-LAST:event_formComponentResized

    private void jButtonChangeColorMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeColorMinActionPerformed

        areaColor = JColorChooser.showDialog(null, "Change Area Color", areaColor);
        histogram.setColorMin(areaColor);
    }//GEN-LAST:event_jButtonChangeColorMinActionPerformed

    private void jButtonAreaColorChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAreaColorChangeActionPerformed
        setColors();
    }//GEN-LAST:event_jButtonAreaColorChangeActionPerformed

    private void jButtonChangeColorMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeColorMaxActionPerformed
        areaColor = JColorChooser.showDialog(null, "Change Area Color", areaColor);
        histogram.setColorMax(areaColor);
    }//GEN-LAST:event_jButtonChangeColorMaxActionPerformed

    private void jRadButRelativeValuesYesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadButRelativeValuesYesMouseClicked
        if (jRadButRelativeValuesYes.isSelected()){
            jRadButRelativeValuesNo.setSelected(false);
        }
    }//GEN-LAST:event_jRadButRelativeValuesYesMouseClicked

    private void jRadButRelativeValuesNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadButRelativeValuesNoMouseClicked
        if (jRadButRelativeValuesNo.isSelected()){
            jRadButRelativeValuesYes.setSelected(false);
        }
    }//GEN-LAST:event_jRadButRelativeValuesNoMouseClicked

    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed

        if(jRadButRelativeValuesYes.isSelected()) {
            exportArea.makeMatrics(true);
        }
        if(jRadButRelativeValuesNo.isSelected()){
            exportArea.makeMatrics(false);
        }

        if (jRadButRelativeValuesYes.isSelected() && jRadButRelativeValuesNo.isSelected()){
            return;
        }

        if (!jRadButRelativeValuesYes.isSelected() && !jRadButRelativeValuesNo.isSelected()){
            return;
        }

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(selectedFile.getAbsolutePath()+".csv");
                writer = csvGenerator(writer, area);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }//GEN-LAST:event_jButtonExportActionPerformed

    private void jButtonUserSelectFacesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserSelectFacesActionPerformed
        int[] SelectedAreas = new int[jListUserFaces.getSelectedIndices().length];
        SelectedAreas = jListUserFaces.getSelectedIndices();

        

        if (jListUserFaces.getSelectedIndices().length>0){
            updateFacesDifferences(SelectedAreas);
        }
    }//GEN-LAST:event_jButtonUserSelectFacesActionPerformed

    private void jButtonSelectAllFacesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectAllFacesActionPerformed
        
        int[] SelectedAreas = new int[modelsName.size()];
        for(int i=0; i<modelsName.size(); i++){
            SelectedAreas[i]=i;
        }

        jListUserFaces.setSelectedIndices(SelectedAreas);

        if (jListUserFaces.getSelectedIndices().length>0){
            updateFacesDifferences(SelectedAreas);
        }
    }//GEN-LAST:event_jButtonSelectAllFacesActionPerformed
    // </editor-fold>   

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
    private javax.swing.JButton jButtonAreaColorChange;
    private javax.swing.JButton jButtonChangeColorMax;
    private javax.swing.JButton jButtonChangeColorMin;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonSelectAllFaces;
    private javax.swing.JButton jButtonUserSelectFaces;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jListUserFaces;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadButRelativeValuesNo;
    private javax.swing.JRadioButton jRadButRelativeValuesYes;
    private javax.swing.JScrollPane jScrollPane1;
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
