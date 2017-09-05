package cz.fidentis.gui.comparison_batch;

import cz.fidentis.comparison.localAreas.PointsValues;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author Richard
 */
public class HistogramJPanel extends javax.swing.JPanel {

    private PointsValues points;
    private int width, height;
    private Color color;
    private int selectedIndex;
    private int highlightedIndex;
    private LocalAreasSelectedAreaJPanel pointer;
    
    
    /**
     * Creates new form HistogramJPanel
     */
    public HistogramJPanel() {
        initComponents();
        this.setBackground(Color.WHITE);
        this.points = new PointsValues();
        this.color = Color.WHITE;
        this.selectedIndex = -1;
        this.highlightedIndex = -1;
    }
    
    public void setPointer(LocalAreasSelectedAreaJPanel pointer){
        this.pointer = pointer;
    }
    
    /**
     * Keeps track of actual size of canvas for drawing
     * @param width
     * @param height 
     */
    @Override
    public void setSize(int width, int height){
        this.width = width;
        this.height = height+10;
        this.setPreferredSize(new Dimension(width, height));

    }
    
    public void setColorMin(Color colorMin){
        this.points.colorMin = colorMin;
        this.repaint();
    }
    
    public void setColorMax(Color colorMax){
        this.points.colorMax = colorMax;
        this.repaint();
    }
            
    /**
     * Setting CSV values
     * @param values 
     */
    public void setValues(List<Float> values){
        this.points.setValues(values);
        this.points.selectionSort();

        this.repaint();
    }
    
    public PointsValues getPoints(){
        return points;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  
        
        if (points.values.size() > 0){
            int Ymax = height - 50;
            int Ymin = 35;
            int Xmax = width - 45;
            int Xmin = 45;

            //calculating distribution
            points.calculateDistribution(Math.abs(points.max - points.min), Xmax - Xmin);

            if(g instanceof Graphics2D){
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                //decimal formating
                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);
                
                // <editor-fold desc="Drawing graph">
                int Xhalf = ((width - 90)/2)+45;
                int XoneFourth = ((width - 90)/4)+45;
                int XthreeFourth = 3*((width - 90)/4)+45;
                
                float half = ((points.max - points.min)/2) + points.min;
                float oneFourth = ((points.max - points.min)/4) + points.min;
                float threeFourth = 3*((points.max - points.min)/4) + points.min;
                
                g2.drawString(""+df.format(points.max),Xmax-14,Ymax+14); 
                g2.drawString(""+df.format(half),Xhalf-14,Ymax+14);
                g2.drawString(""+df.format(oneFourth),XoneFourth-14,Ymax+14);
                g2.drawString(""+df.format(threeFourth),XthreeFourth-14,Ymax+14);
                g2.drawString(""+df.format(points.min),Xmin-14,Ymax+14); 
                
                g2.drawString("Difference of points in area",Xhalf - 50,Ymax+30);
                
                g.drawLine(Xmin, Ymax, Xmax, Ymax);
                
                g.drawLine(Xmin, Ymax, Xmin, Ymax+2);
                g.drawLine(XoneFourth, Ymax, XoneFourth, Ymax+2);
                g.drawLine(Xhalf, Ymax, Xhalf, Ymax+2);
                g.drawLine(XthreeFourth, Ymax, XthreeFourth, Ymax+2);
                g.drawLine(Xmax, Ymax, Xmax, Ymax+2);

                AffineTransform orig = g2.getTransform();
                g2.rotate(-Math.PI/2);
                g2.drawString("Number of points ",-Ymax+20,20); 
                
                g2.setTransform(orig);
                
                g2.drawString(""+points.maxClusteredPoints,Xmin-25,Ymin);
                g.drawLine(Xmin, Ymin, Xmin-5, Ymin);
                g.drawLine(Xmin, Ymin, Xmin, Ymax);   
                // </editor-fold>
                
                for (int i = 0; i < points.distribution.size(); i++){
                    // <editor-fold desc="Drawing column">
                    int value = points.distribution.get(points.distribution.size() - i -1).size();
                    int x = 46 + 5*i;
                    int y = 35 + ((height - 85)/points.maxClusteredPoints)*(points.maxClusteredPoints - value);
                    if (value == 0){
                        y = 35 + (height - 85);
                    }

                    int heightRec = height - 50 - y;
                    int widthRec = 5;

                    g.setColor(points.distributionColor.get(points.distribution.size() - i -1));
                    g.fillRect(x,y,widthRec,heightRec);
                    // </editor-fold>

                    // <editor-fold desc="Coloring selected column">
                    if (this.selectedIndex == i){
                        float thickness = 2;
                        Stroke oldStroke = g2.getStroke();
                        g2.setStroke(new BasicStroke(thickness));
                        g.setColor(Color.black);
                        g.drawRect(x,y,widthRec,heightRec);
                        g2.setStroke(oldStroke);
                    } 
                    
                    if (this.highlightedIndex == i){
                        float thickness = 3;
                        Stroke oldStroke = g2.getStroke();
                        g2.setStroke(new BasicStroke(thickness));
                        g.setColor(Color.red);
                        g.drawRect(x,y,widthRec,heightRec);
                        g2.setStroke(oldStroke);
                    } 
                    // </editor-fold>
                }

                // <editor-fold desc="Writing text for selected column">
                if (this.selectedIndex != -1){
                    g.setColor(Color.black);
                    g2.drawString("Count: "+points.distribution.get(points.distribution.size() - this.selectedIndex - 1).size(), 
                            30 + 5 * this.selectedIndex, Ymin - 22); 
                    
                    g2.drawString(points.distributionBoundaries.get(points.distribution.size() - this.selectedIndex - 1), 
                            10 + 5 * this.selectedIndex, Ymin - 5); 
                }
                
                if (this.highlightedIndex != -1){
                    g.setColor(Color.red);
                    g2.drawString("Count: "+points.distribution.get(points.distribution.size() - this.highlightedIndex - 1).size(), 
                            30 + 5 * this.highlightedIndex, Ymin - 22); 
                    
                    g2.drawString(points.distributionBoundaries.get(points.distribution.size() - this.highlightedIndex - 1), 
                            10 + 5 * this.highlightedIndex, Ymin - 5); 
                }
                // </editor-fold>
            }
            
        } 
    }

    /**
     * Find chosen point in area
     * @param mouseX
     * @param mouseY
     * @return number or -1 if not found
     */
    private int getChoosenIndex(int mouseX, int mouseY){
        int result = -1;
        if (points.distribution.isEmpty()){
            return result;
        }

        for (int i = 0; i < points.distribution.size(); i++){
            int x = 45 + 5*i;
            int y = 35;
            
            int heightRec = height - 50 - y;
            int widthRec = 5;
            
            if (mouseX >= x && mouseX <= x+widthRec && mouseY >= y && mouseY <= y+heightRec){
                result = i;
            }
        }

        return result;
    }
    
//    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (points != null){
            this.selectedIndex = getChoosenIndex(evt.getX(), evt.getY());
            this.repaint();
        }
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (points != null){
            this.highlightedIndex = getChoosenIndex(evt.getX(), evt.getY());
            if (this.highlightedIndex != -1){
                //sending indexes that should be highlighted on model
                List<Integer> pointsToSend = points.distribution.get(points.distribution.size() - this.selectedIndex - 1);
                
                if (this.pointer != null){
                    this.pointer.updateSelectedPoints(pointsToSend);
                }
            }
            this.repaint();
        }
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
