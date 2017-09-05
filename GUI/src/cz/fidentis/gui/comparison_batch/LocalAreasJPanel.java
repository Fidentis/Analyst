package cz.fidentis.gui.comparison_batch;

import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.comparison.localAreas.Area;
import cz.fidentis.comparison.localAreas.AreaListXML;
import cz.fidentis.comparison.localAreas.BinTree;
import cz.fidentis.comparison.localAreas.LocalAreaLibrary;
import cz.fidentis.comparison.localAreas.LocalAreas;
import cz.fidentis.comparison.localAreas.PointsValues;
import cz.fidentis.comparison.localAreas.VertexArea;
import cz.fidentis.model.Model;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.geometry.Point3D;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openide.util.Exceptions;

/**
 * 
 * @author Richard
 */
public class LocalAreasJPanel extends javax.swing.JPanel {

    // <editor-fold desc="Workers">
    /**
     * Calculate areas
     */
    private class FindAreasWorker extends SwingWorker<String, Object>{
        @Override
        protected String doInBackground() {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);

            calculateVertexArea();

            setAreasJList(vertexArea.getAreas());

            return "Done.";
        }

        @Override
        protected void done() {
            progressBar.setVisible(false);
        }
    }
    
    /**
     * Finding intersection with points on face model
     */
    private class SelectPointWorker extends SwingWorker<String, Object>{
        @Override
        protected String doInBackground() {
            
            while (isMouseOnCanvas){
                int difference = differenceInMiliseconds(timeOfMouseMovement, Calendar.getInstance());
                
                if (difference >= 300){
                    try {
                        drawHooveredPoint();
                    }
                    catch(Exception e){
                        System.out.println(e.toString());
                    }
                    
                    
                }
                
            }
            
            return "Done.";
        }

        @Override
        protected void done() {
        
        }
    }
    // </editor-fold>
    
    private BatchComparisonResults pointerBatchComparisonResult;
    private Double SizeOfArea;
    private Double BottomTresh;
    private Double TopTresh;
    private Double min;
    private Double max;
    private List<Area> AreasList;
    private List<Area> OriginalAreasList;
    private int SelectedAreas[];
    private Boolean RelativeValues;
    private ArrayList<Float> areaList;
    private VertexArea vertexArea;
    private Model model;
    private Model initialModel;
    private boolean isInicialized;
    private boolean isAnyAreaDrawn;
    private boolean isAreaSelected;
    private JFrame LocalAreaFrame;
    private LocalAreasSelectedAreaJPanel LocalAreaJPanel;
    private boolean isMouseOnCanvas;
    private Calendar timeOfMouseMovement;
    private Vector2d mousePosition; 
    private boolean isWorkerRunning;
    private boolean isPointSelected;
    private boolean isLocalAreasSet;
    private boolean isVisible;
    
    
    
    /**
     * Creates new form LocalAreasJPanel
     */
    public LocalAreasJPanel() {
        initComponents();
        
        SizeOfArea = 0.0;
        BottomTresh = 0.0;
        TopTresh = 0.0;
        AreasList = new ArrayList<>();
        OriginalAreasList = new ArrayList<>();
        SelectedAreas = new int[10];
        RelativeValues = true;
        areaList = null;
        model = null;
        isInicialized = false;
        isAnyAreaDrawn = false;
        isWorkerRunning = false;
        isAreaSelected = false;
        isPointSelected = false;
        
        AreasJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        progressBar.setVisible(false);
        
        enableComponents(false);
        
    }
    
    // <editor-fold defaultstate="collapsed" desc="Getters">
    public boolean isInitialized(){
        return this.isInicialized;
    }
    
    public void isVisible(boolean value){
        this.isVisible = value;
        if (!value){
            LocalAreaFrame.setVisible(false);
            hideSelectedAreaInfo();
        }
        
    }
    
    public boolean isLocalAreasSet(){
        return isLocalAreasSet;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Public">
    public void closeSelectedArea(){
        if (LocalAreaFrame != null){
            LocalAreaFrame.dispatchEvent(new WindowEvent(LocalAreaFrame, WindowEvent.WINDOW_CLOSING));
        }
    }
    
    public void setAreaColors(PointsValues points, Area area) {
        pointerBatchComparisonResult.getRenderer().drawSelectedArea(points, area);
    }

    public void updateSelectedPoints(List<Integer> indexes) {

        List<Vector4f> points = new ArrayList<>();
        List<Vector4f> allPoints = pointerBatchComparisonResult.getRenderer().getLocalAreas().getAllPointsFromOneArea();
        for (Integer index : indexes){
            points.add(allPoints.get(index));
        }

        pointerBatchComparisonResult.getRenderer().setPointsToDraw(points);
        
    }
    
    public void hideSelectedAreaInfo() {
        pointerBatchComparisonResult.getCanvas().showPointValue(false, "", 1, 1);
        pointerBatchComparisonResult.getRenderer().hideSelectedPoints();
        pointerBatchComparisonResult.getRenderer().hideSelectedArea();
    }

    /**
     * When model is changed, the data are send to renderer
     * @param model 
     */
    public void updateModel(Model model){
        if (!this.isVisible()){
            return;
        }
        
        KdTreeIndexed indexer = new KdTreeIndexed(model.getVerts());
        List<Area> tempAreaList = new ArrayList<>();
        
        for (int i = 0; i < OriginalAreasList.size(); i++){
            Area tempArea = OriginalAreasList.get(i);
            List<Integer> tempVertices = new ArrayList<>();
            
            for (int j = 0; j < tempArea.vertices.size(); j++){
                int areaVertexIndex = tempArea.vertices.get(j);
                int index = indexer.nearestIndex(this.initialModel.getVerts().get(areaVertexIndex));
                tempVertices.add(index);
            }
            
            Area deepCopyArea = deepCopyArea(tempArea);
            deepCopyArea.vertices = tempVertices;
            tempAreaList.add(deepCopyArea);
        }  
        this.model = model;
        this.AreasList = tempAreaList;
        
        renderSelectedAreas();
    }
    
    /**
     * Set range for local areas
     * @param min
     * @param max 
     */
    public void loadRangeValues(float min, float max) {
        areaList = new ArrayList(pointerBatchComparisonResult.GetAuxiliaryAverageResults());
        pointerBatchComparisonResult.GetAuxiliaryResults();
        BottomTresh = (double)min;
        TopTresh = (double)max;
        this.max = (double)max;
        this.min = (double)min;
        TopTextField.setText(TopTresh.toString());
        BottomTextField.setText(BottomTresh.toString());
        isInicialized = true;
        this.jLabelInitialModel.setText(pointerBatchComparisonResult.GetAverageModel().getName());
    }
    
    public void setPointerBatchComparisonResults(BatchComparisonResults pointer){
        this.pointerBatchComparisonResult = pointer;   
    }

    /**
     * Find a selected area
     * @param x X mouse position on screen
     * @param y Y mouse position on screen
     */
    public void setMousePositionToSelectArea(double x, double y){
        if (LocalAreaFrame == null){
            return;
        }
        
        if (!isInicialized){
            return;
        }
        
        if (!isAnyAreaDrawn){
            return;
        }
        
        if (!isVisible){
            return;
        }
        
        LocalAreas localAreas = pointerBatchComparisonResult.getRenderer().getLocalAreas();
        double[] modelViewMatrix = pointerBatchComparisonResult.getRenderer().getModelViewMatrix();
        double[] projectionMatrix = pointerBatchComparisonResult.getRenderer().getProjectionMatrix();
        int[] viewPort = pointerBatchComparisonResult.getRenderer().getViewPort();
        
        int i = 0;
        //looking through all areas
        for (List<Point3D> points : localAreas.getBoundariesAreasPoints()){
            Vector3f point = LocalAreaLibrary.intersectionWithArea(x, y, viewPort, modelViewMatrix, projectionMatrix, points);

            //if there is an intersection
            if (point != null){
                if (!LocalAreaFrame.isVisible()){
                    LocalAreaFrame.setVisible(true);
                    LocalAreaFrame.setAlwaysOnTop(true);
                }
                
                LocalAreaJPanel.SetArea(vertexArea,
                        AreasList.get(localAreas.getIndexes()[i]), 
                        RelativeValues, 
                        pointerBatchComparisonResult.getContext().getHdVisualResults(), 
                        pointerBatchComparisonResult.getContext().getModels(),
                        pointerBatchComparisonResult.getContext().getMetricTypeIndex());
                
                selectArea(localAreas.getIndexes()[i]);
                isAreaSelected = true;
                startMousePositionDetectionOnCanvas(true);
                AreasJList.setSelectedIndex(localAreas.getIndexes()[0]);
                return;
            }
            i++;
        }

    }

    /**
     * Starts Worker if mouse is on canvas
     * @param value 
     */
    public void startMousePositionDetectionOnCanvas(boolean value){
        this.isMouseOnCanvas = value;
        if (value && (!isWorkerRunning) && isAreaSelected){
            new SelectPointWorker().execute();
            this.isWorkerRunning = true;
        } else {
            this.isWorkerRunning = false;
        }
    }
    
    /**
     * Used only for worker
     * @param x
     * @param y
     * @param time 
     */
    public void setMousePosition(double x, double y, Calendar time){
        this.mousePosition = new Vector2d(x, y);
        this.timeOfMouseMovement = time;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Set JList for areas
     * @param areasList 
     */
    private void setAreasJList(List<Area> areasList){
        AreasList = areasList;
        OriginalAreasList = areasList;
        DefaultListModel listModel = new DefaultListModel();
        if (areasList.size()>0){
            for (Area item : areasList){
                listModel.addElement(item.index+" Area");
            }
            enableComponents(true);
        } else {
            enableComponents(false);
            listModel.addElement("No Area was found!");
        }
        AreasJList.setModel(listModel);
        AreasJListRenderer renderer = new AreasJListRenderer();
        renderer.setAreas(areasList);
        AreasJList.setCellRenderer(renderer);
        this.isLocalAreasSet = true;
    }

    private void enableComponents(Boolean value){
        AreasJList.setEnabled(value);
        SelectButton.setEnabled(value);
        AllButton.setEnabled(value);
        jRadButRelativeValuesYes.setEnabled(value);
        jRadButRelativeValuesNo.setEnabled(value);
        ExportButton.setEnabled(value);
    }
    
    /**
     * Calculate intersection with points
     */
    private void drawHooveredPoint(){
        if (!this.isVisible()){
            pointerBatchComparisonResult.getCanvas().showPointValue(false, "", 1, 1);
            return;
        }
        
        List<Vector4f> points = pointerBatchComparisonResult.getRenderer().getLocalAreas().getAllPointsFromOneArea();
        double[] modelViewMatrix = pointerBatchComparisonResult.getRenderer().getModelViewMatrix();
        double[] projectionMatrix = pointerBatchComparisonResult.getRenderer().getProjectionMatrix();
        int[] viewPort = pointerBatchComparisonResult.getRenderer().getViewPort();
        
        Vector4f point = LocalAreaLibrary.intersectionWithPoint(mousePosition.x, mousePosition.y, viewPort, modelViewMatrix, projectionMatrix, points);
        
        //check for intersection
        if (point != null){

            DecimalFormat df = new DecimalFormat("#.###");
            df.setRoundingMode(RoundingMode.CEILING);
      
            float pointCsvValue = getCSVvalue(AreasList.get(SelectedAreas[0]), point.w);
            pointerBatchComparisonResult.getRenderer().setPointToDraw(point);
            String message = "["+df.format(point.x)+", "+df.format(point.y)+", "+df.format(point.z)+"]: CSV: "+df.format(pointCsvValue);
            pointerBatchComparisonResult.getCanvas().showPointValue(true, message, (int)mousePosition.x, (int)mousePosition.y+10);
            isPointSelected = true;
        }
    }
    
    /**
     * Gets models, calculate areas, sets LocalAreaJPanel
     */
    private void calculateVertexArea(){
        isLocalAreasSet = false;
        
        model = pointerBatchComparisonResult.getCurrentModel(); 
        initialModel = pointerBatchComparisonResult.getCurrentModel(); 
        areaList = new ArrayList(pointerBatchComparisonResult.GetAuxiliaryAverageResults());
        BinTree thres = new BinTree(areaList);
        vertexArea = new VertexArea(model, thres);
        vertexArea.createAreas(SizeOfArea.intValue(), BottomTresh.floatValue(), TopTresh.floatValue());
        
        //set LocalAreaFrame
        LocalAreaFrame = new JFrame("Area");
        LocalAreaFrame.setVisible(false);
        LocalAreaFrame.setMinimumSize(new Dimension(630, 730));

        LocalAreaJPanel = new LocalAreasSelectedAreaJPanel();
        LocalAreaJPanel.setPointerLocalAreasJPanel(this);
        
        LocalAreaFrame.add(LocalAreaJPanel);
        LocalAreaFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                hideSelectedAreaInfo();
            }
        });
        
        LocalAreaFrame.pack();
    }

    /**
     * Select specific area
     * @param index index of area
     */
    private void selectArea(int index){
        SelectedAreas = new int[1];
        
        SelectedAreas[0]=index;
       
        AreasJList.clearSelection();
        AreasJList.setSelectedIndices(new int[] {index});
        renderSelectedAreas();
        
        if (AreasJList.getSelectedIndices().length>0){
             isAnyAreaDrawn = true;
        }
    }
    
    /**
     * send areas indexes to render
     */
    private void renderSelectedAreas(){
        List<Area> tempList = new ArrayList<>();
        for (int i = 0; i < SelectedAreas.length; i++){
            Area selectedArea = AreasList.get(SelectedAreas[i]);
            tempList.add(selectedArea);
        }
        
        pointerBatchComparisonResult.getRenderer().SetUpLocalAreaRender(SelectedAreas, tempList, model);
    }
    
    private static Area deepCopyArea(Area area){
        Area result = new Area();
        
        result.ariMean = area.ariMean;
        result.color = area.color;
        result.csvValues = area.csvValues;
        result.geoMean = area.geoMean;
        result.index = area.index;
        result.max = area.max;
        result.min = area.min;
        result.percentileSevFiv = area.percentileSevFiv;
        result.rootMean = area.rootMean;
        result.variance = area.variance;
        result.vertices = area.vertices;

        return result;
    }
    
    private static float getCSVvalue(Area area, float w){
        
        int index = area.vertices.indexOf((int)w);
        if (index != -1){
            return area.csvValues.get(index);
        }
        

        return 0.0f;
    }
    
    private static int differenceInMiliseconds(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        
        return (int)TimeUnit.MILLISECONDS.toMillis(Math.abs(end - start));
    }
    
    private static FileWriter csvGenerator(FileWriter writer, VertexArea vertexArea) throws IOException{
        int totalLength = -1;
        int numberOfAreas = vertexArea.getAreas().size();
        
        List<String> values = Arrays.asList(new String[numberOfAreas]);
        
        for (int i = 0; i < numberOfAreas; i++){
            values.set(i, i+" Area");
            if (totalLength < vertexArea.getAreas().get(i).csvValues.size()){
                totalLength = vertexArea.getAreas().get(i).csvValues.size();
            }
        }
        
        writer.write(writeRow(values));
        
        for (int j = 0; j < totalLength; j++){
            List<String> csvValues = Arrays.asList(new String[numberOfAreas]);
            for (int i = 0; i < numberOfAreas; i++){
                if (vertexArea.getAreas().get(i).csvValues.size() > j){
                    csvValues.set(i, vertexArea.getAreas().get(i).csvValues.get(j)+"");
                } else {
                    csvValues.set(i, "");
                }
                
            }
            writer.write(writeRow(csvValues));
        }

        return writer;
    }
    
    private static String writeRow(List<String> values){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.size(); i++){
            sb.append(values.get(i));
            sb.append(',');
        }
        sb.append('\n');

        return sb.toString();
        
    }
    
    private static List<Area> extendAreaList(List<Area> areas, AreaListXML areasXML){
        
        for (int i = 0; i < areasXML.getAreaList().size(); i++){
            Area tempArea = areasXML.getAreaList().get(i);
            tempArea.index = areas.size();
            areas.add(tempArea);
        }

        return areas;
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

        progressBar = new javax.swing.JProgressBar();
        BottomTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        TopTextField = new javax.swing.JTextField();
        jLabelInitialModel = new javax.swing.JLabel();
        AreaTextField = new javax.swing.JTextField();
        jRadButRelativeValuesYes = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        AreasJList = new javax.swing.JList<>();
        jRadButRelativeValuesNo = new javax.swing.JRadioButton();
        jButtonImportAreas = new javax.swing.JButton();
        ApplyButton = new javax.swing.JButton();
        jButtonExportAreas = new javax.swing.JButton();
        SelectButton = new javax.swing.JButton();
        AllButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ExportButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(313, 700));

        BottomTextField.setText(org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.BottomTextField.text")); // NOI18N
        BottomTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                BottomTextFieldFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabel5.text")); // NOI18N

        TopTextField.setText(org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.TopTextField.text")); // NOI18N
        TopTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TopTextFieldFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabelInitialModel, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabelInitialModel.text")); // NOI18N

        AreaTextField.setText(org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.AreaTextField.text")); // NOI18N
        AreaTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                AreaTextFieldFocusLost(evt);
            }
        });

        jRadButRelativeValuesYes.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadButRelativeValuesYes, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jRadButRelativeValuesYes.text")); // NOI18N
        jRadButRelativeValuesYes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadButRelativeValuesYesMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(AreasJList);

        org.openide.awt.Mnemonics.setLocalizedText(jRadButRelativeValuesNo, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jRadButRelativeValuesNo.text")); // NOI18N
        jRadButRelativeValuesNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadButRelativeValuesNoMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonImportAreas, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jButtonImportAreas.text")); // NOI18N
        jButtonImportAreas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportAreasActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(ApplyButton, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.ApplyButton.text")); // NOI18N
        ApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ApplyButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonExportAreas, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jButtonExportAreas.text")); // NOI18N
        jButtonExportAreas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportAreasActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(SelectButton, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.SelectButton.text")); // NOI18N
        SelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(AllButton, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.AllButton.text")); // NOI18N
        AllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AllButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ExportButton, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.ExportButton.text")); // NOI18N
        ExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(LocalAreasJPanel.class, "LocalAreasJPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ExportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ApplyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelInitialModel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AreaTextField)
                            .addComponent(TopTextField)
                            .addComponent(BottomTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(42, 42, 42)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SelectButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadButRelativeValuesYes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadButRelativeValuesNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonImportAreas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonExportAreas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabelInitialModel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(AreaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TopTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(BottomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(ApplyButton)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonImportAreas)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonExportAreas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SelectButton)
                        .addGap(18, 18, 18)
                        .addComponent(AllButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jRadButRelativeValuesYes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadButRelativeValuesNo)))
                .addGap(36, 36, 36)
                .addComponent(ExportButton)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BottomTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BottomTextFieldFocusLost
        try{
            Double BottomTreshTemp = Double.valueOf(this.BottomTextField.getText());
            if (BottomTreshTemp < min || BottomTreshTemp >= TopTresh){
                BottomTextField.setText(min.toString());
            } else {
                BottomTresh = BottomTreshTemp;
            }
        }catch(Exception e){
            BottomTextField.setText(BottomTresh.toString());
        }
    }//GEN-LAST:event_BottomTextFieldFocusLost

    private void TopTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TopTextFieldFocusLost
        try{
            Double TopTreshTemp = Double.valueOf(this.TopTextField.getText());
            if (TopTreshTemp> max || TopTreshTemp <= BottomTresh){
                TopTextField.setText(max.toString());
            } else {
                TopTresh = TopTreshTemp;
            }
        }catch(Exception e){
            TopTextField.setText(TopTresh.toString());
        }
    }//GEN-LAST:event_TopTextFieldFocusLost

    private void AreaTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AreaTextFieldFocusLost
        try{
            SizeOfArea = Double.valueOf(this.AreaTextField.getText());
        }catch(Exception e){
            AreaTextField.setText(SizeOfArea.toString());
        }
    }//GEN-LAST:event_AreaTextFieldFocusLost

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

    private void jButtonImportAreasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportAreasActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = new File(jfc.getSelectedFile().getAbsolutePath());
                JAXBContext jaxbContext = JAXBContext.newInstance(AreaListXML.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                AreaListXML areaListXML = (AreaListXML) jaxbUnmarshaller.unmarshal(file);

                AreasList = extendAreaList(AreasList, areaListXML);
                OriginalAreasList = AreasList;

                setAreasJList(AreasList);

            } catch (JAXBException e) {
                Exceptions.printStackTrace(e);
            }

        }
    }//GEN-LAST:event_jButtonImportAreasActionPerformed

    private void ApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ApplyButtonActionPerformed
        new FindAreasWorker().execute();
    }//GEN-LAST:event_ApplyButtonActionPerformed

    private void jButtonExportAreasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportAreasActionPerformed

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                AreaListXML areaListXML = new AreaListXML();
                areaListXML.setAreaList(AreasList);

                File file = new File(jfc.getSelectedFile().getAbsolutePath()+".xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(AreaListXML.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(areaListXML, file);
                //jaxbMarshaller.marshal(areaListXML, System.out);
            } catch (JAXBException e) {
                Exceptions.printStackTrace(e);
            }

        }

    }//GEN-LAST:event_jButtonExportAreasActionPerformed

    private void SelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectButtonActionPerformed
        SelectedAreas = new int[AreasJList.getSelectedIndices().length];
        SelectedAreas = AreasJList.getSelectedIndices();

        renderSelectedAreas();

        if (AreasJList.getSelectedIndices().length>0){
            isAnyAreaDrawn = true;
        }

        //        if (!pointerBatchComparisonResult.getCurrentModel().getName().equals(this.initialModel.getName()))
        //        {
            //            this.updateModel(pointerBatchComparisonResult.getCurrentModel());
            //        }
    }//GEN-LAST:event_SelectButtonActionPerformed

    private void AllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AllButtonActionPerformed
        SelectedAreas = new int[AreasList.size()];
        for(int i=0; i<AreasList.size(); i++){
            SelectedAreas[i]=i;
        }

        AreasJList.setSelectedIndices(SelectedAreas);
        renderSelectedAreas();

        if (AreasJList.getSelectedIndices().length>0){
            isAnyAreaDrawn = true;
        }

        //        if (!pointerBatchComparisonResult.getCurrentModel().getName().equals(this.initialModel.getName()))
        //        {
            //            this.updateModel(pointerBatchComparisonResult.getCurrentModel());
            //        }

    }//GEN-LAST:event_AllButtonActionPerformed

    private void ExportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportButtonActionPerformed
        if(jRadButRelativeValuesYes.isSelected()) {
            vertexArea.makeMatrics(true);
        }
        if(jRadButRelativeValuesNo.isSelected()){
            vertexArea.makeMatrics(false);
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
                writer = csvGenerator(writer, vertexArea);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }//GEN-LAST:event_ExportButtonActionPerformed
    // </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AllButton;
    private javax.swing.JButton ApplyButton;
    private javax.swing.JTextField AreaTextField;
    private javax.swing.JList<String> AreasJList;
    private javax.swing.JTextField BottomTextField;
    private javax.swing.JButton ExportButton;
    private javax.swing.JButton SelectButton;
    private javax.swing.JTextField TopTextField;
    private javax.swing.JButton jButtonExportAreas;
    private javax.swing.JButton jButtonImportAreas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelInitialModel;
    private javax.swing.JRadioButton jRadButRelativeValuesNo;
    private javax.swing.JRadioButton jRadButRelativeValuesYes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
