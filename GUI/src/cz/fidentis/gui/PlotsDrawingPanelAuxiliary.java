/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui;

import cz.fidentis.visualisation.ColorScheme;
import cz.fidentis.visualisation.ColorSelector;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.blue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

/**
 *
 * @author Katka
 */
public class PlotsDrawingPanelAuxiliary extends javax.swing.JPanel {

    private float unsortedValues[][] = new float[50][50];
    private float values[][] = new float[50][50];
    private float vl[][] = new float[50][50];
    private Vector4f activeArea = new Vector4f();
    private Point lastClickedPoint;
    private boolean select = false;
    private float cellHeight;
    private long lastMovedTime;
    private Point mousePosition;
    private Point slider1Tip = new Point(this.getWidth() - 50, 70);
    private int slider1P;
    private Point slider2Tip = new Point(this.getWidth() - 50, this.getHeight() - 70);
    private int slider2P;
    private int subselP;
    private boolean slider1Selected;
    private boolean slider2Selected;
    private boolean subselectionSelected;
    private Point subselectionTip = new Point();
    private String[] names;
    private int subselWidth = 0;
    private int selectedModelIndex = -1;
    private int selectedVertexIndex = -1;
    private boolean absolute = false;
    private ColorScheme scheme = ColorScheme.DIVERGING;

    /**
     * Creates new form plotsPanel
     */
    public PlotsDrawingPanelAuxiliary() {
        initComponents();

        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                Random r = new Random();
                float v = r.nextFloat() * 200;
                values[i][j] = v;
            }
        }

    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public float[][] getValues() {
        return values;
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public void setScheme(ColorScheme scheme) {
        this.scheme = scheme;
    }

    public void setValues(float[][] values) {
        unsortedValues = values;
        values = sortValues(values);
        this.vl = values;
        this.values = values;
        select = false;
        this.repaint();
        this.repaint();
    }

    public void resetValues() {
        values = sortValues(unsortedValues);
        this.vl = values;
        this.values = values;
        select = false;
        this.repaint();
        this.repaint();
    }

    private float[][] sortValues(float[][] vl) {
        float[][] val = new float[vl.length][vl[0].length];
        for (int i = 0; i < vl.length; i++) {
            val[i] = Arrays.copyOf(vl[i], vl[i].length);
        }

        float[] s = new float[val[0].length];
        for (int j = 0; j < val[0].length; j++) {
            for (int i = 0; i < val.length; i++) {
                if (absolute) {
                    s[j] += Math.abs(val[i][j]);
                } else {
                    s[j] += val[i][j];
                }
            }
        }
        for (int j = s.length - 1; j >= 0; j--) {
            for (int i = 0; i < j; i++) {
                if (s[i] < s[j]) {
                    for (int k = 0; k < val.length; k++) {
                        float tmp = val[k][i];
                        val[k][i] = val[k][j];
                        val[k][j] = tmp;
                    }
                    float tmp = s[i];
                    s[i] = s[j];
                    s[j] = tmp;
                }
            }
        }

        s = new float[val.length];
        for (int i = 0; i < val.length; i++) {
            for (int j = 0; j < val[0].length; j++) {
                s[i] += Math.abs(val[i][j]);
            }
        }
        for (int j = s.length - 1; j >= 0; j--) {
            for (int i = 0; i < j; i++) {
                if (s[i] > s[j]) {

                    float[] tmp = val[i];
                    val[i] = val[j];
                    val[j] = tmp;

                    float tmpf = s[i];
                    s[i] = s[j];
                    s[j] = tmpf;

                    if (names != null && names[i] != null) {
                        String nm = names[i];
                        names[i] = names[j];
                        names[j] = nm;
                    }
                }
            }
        }

        return val;
    }

    /*   public void setP(PlotsPanel p) {
     this.p = p;
     }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(400, 180));
        setName(""); // NOI18N
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
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
            .addGap(0, 180, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (pointInActiveArea(evt.getPoint())) {
            lastClickedPoint = evt.getPoint();
            select = true;
            float y = lastClickedPoint.y;
            y = (float) Math.floor((y - 70) / (cellHeight + 1));
            //          p.getHistogramPanel1().setRestriction((int) y);

            if (evt.getX() - subselWidth / 2 > activeArea.z) {
                subselectionTip = new Point((int) activeArea.z - subselWidth, (int) (67 + (y * cellHeight) + y - 2));
            } else if (evt.getX() - subselWidth / 2 < activeArea.x) {
                subselectionTip = new Point((int) activeArea.x, (int) (67 + (y * cellHeight) + y - 2));
            } else {
                subselectionTip = new Point(evt.getX() - subselWidth / 2, (int) (67 + (y * cellHeight) + y - 2));
            }
            selectedModelIndex = (int) y;

            this.repaint();

        } else if (evt.getX() > 67 && evt.getX() < this.getWidth() - 158 && evt.getY() > (this.getHeight() - 63) && evt.getY() < this.getHeight() - 13) {
            // 67, this.getHeight() - 63, this.getWidth() - 225, 50
            float width = this.getWidth() - 230;

            int ratio = (int) Math.ceil(vl[0].length / (int) Math.floor(width));
            int x = subselectionTip.x - 70;
            int first = x * ratio;
            int y = lastClickedPoint.y;
            y = (int) Math.floor((y - 70) / (cellHeight + 1));
            int i = x + first - 70;
            System.out.println(i);

        } else {
            //  p.getHistogramPanel1().setRestriction(-1);
            select = false;
            selectedModelIndex = -1;
        }
        this.repaint();
    }

    public int getSelectedModelIndex() {
        return selectedModelIndex;
    }

    public void setSelectedModelIndex(int selectedModelIndex) {
        this.selectedModelIndex = selectedModelIndex;
    }

    public int getSelectedVertexIndex() {
        return selectedVertexIndex;
    }

    public void setSelectedVertexIndex(int selectedVertexIndex) {
        this.selectedVertexIndex = selectedVertexIndex;
    }

    public void adjustValues() {
        float width = this.getWidth() - 230;
        if (vl[0].length > width) {
            int ratio = (int) Math.ceil(vl[0].length / (int) Math.floor(width));
            subselWidth = (int) (width / ratio);
            if (vl.length > 0 && (Math.ceil(vl[0].length / ratio) + 1) > 0) {
                float v[][] = new float[vl.length][(int) Math.ceil(vl[0].length / ratio) + 1];
                int counter;
                for (int i = 0; i < vl.length; i++) {
                    counter = 0;
                    while ((counter * ratio) < vl[0].length - 1) {
                        float val = 0;
                        for (int j = 0; j < 2 * ratio; j++) {
                            if (j + (counter * ratio) < vl[0].length - 1 && j - ratio + (counter * ratio) >= 0) {
                                val = vl[i][j - ratio + (counter * ratio)];
                            }
                        }
                        val = val / 10f;
                        v[i][counter] = val;
                        counter++;

                    }
                }

                values = v;
            }
        }
        this.repaint();
    }


    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        slider1Tip = new Point(this.getWidth() - 50, 70);
        slider2Tip = new Point(this.getWidth() - 50, this.getHeight() - 70);
        select = false;
        adjustValues();


    }//GEN-LAST:event_formComponentResized

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        /*       lastMovedTime = System.currentTimeMillis();
         mousePosition = new Point(evt.getX(), evt.getY());
         final PlotsDrawingPanelAuxiliary pdp = this;
         Timer t = new Timer();
         t.schedule(new TimerTask() {

         @Override
         public void run() {
         pdp.repaint();
         }
         }, 2000);
         pdp.repaint();*/

    }//GEN-LAST:event_formMouseMoved

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        int x = evt.getX() - slider1Tip.x;
        int y = evt.getY() - slider1Tip.y;
        if ((x > 0 && x < 20) && ((y > -(1 / 2f) * x && y <= 0) || (y < (1 / 2f) * x && y >= 0))) {

            slider1Selected = true;
            lastClickedPoint = evt.getPoint();
            slider1P = slider1Tip.y;
        }

        int x2 = evt.getX() - slider2Tip.x;
        int y2 = evt.getY() - slider2Tip.y;

        if ((x2 > 0 && x2 < 20) && ((y2 > -(1 / 2f) * x2 && y2 <= 0) || (y2 < (1 / 2f) * x2 && y2 >= 0))) {
            slider2Selected = true;
            lastClickedPoint = evt.getPoint();
            slider2P = slider2Tip.y;
        }

        int x3 = evt.getX() - subselectionTip.x;
        int y3 = evt.getY() - subselectionTip.y;

        if ((x3 > 0 && x3 < subselWidth) && (y3 > 0 && y3 < cellHeight + 9)) {
            subselectionSelected = true;
            lastClickedPoint = evt.getPoint();
            subselP = subselectionTip.x;
        }


    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        slider1Selected = false;
        slider2Selected = false;
        subselectionSelected = false;
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (slider1Selected) {
            int y = evt.getY() - lastClickedPoint.y;
            if (slider1P + y < 70) {
                slider1Tip.y = 70;
            } else if ((slider1P + y) > slider2Tip.y - 20) {
                slider1Tip.y = slider2Tip.y - 20;
            } else {
                slider1Tip.y = slider1P + y;
            }
            this.repaint();
        }
        if (slider2Selected) {
            int y = evt.getY() - lastClickedPoint.y;
            if (slider2P + y < slider1Tip.y + 20) {
                slider2Tip.y = slider1Tip.y + 20;
            } else if (slider2P + y > this.getHeight() - 70) {
                slider2Tip.y = this.getHeight() - 70;
            } else {
                slider2Tip.y = slider2P + y;
            }
            this.repaint();
        }
        if (subselectionSelected) {
            int x = evt.getX() - lastClickedPoint.x;
            if (subselP + x + subselWidth > activeArea.z) {
                subselectionTip.x = (int) (activeArea.z - subselWidth);
            } else if (subselP + x < activeArea.x) {
                subselectionTip.x = (int) activeArea.x;
            } else {
                subselectionTip.x = subselP + x;
            }
            this.repaint();
        }
    }//GEN-LAST:event_formMouseDragged

    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.WHITE);
        g2.fill(new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight()));
        int height = this.getHeight() - 140;

        paintGrdient(g2, this.getWidth() - 70, 70, height, 20);
        int width = this.getWidth() - 230;
        if (values[0].length > width + 1) {
            adjustValues();
        }

        int numValuesX = values.length;
        int numValuesY = values[0].length;

        //   cellWidth = width / (float) numValuesX - 1;
        cellHeight = height / (float) numValuesX - 1;

        float maxValue = Float.MIN_VALUE;
        float minValue = Float.MAX_VALUE;
        for (int i = 0; i < numValuesX; i++) {
            for (int j = 0; j < numValuesY; j++) {
                if (absolute) {
                    if (Math.abs(values[i][j]) > maxValue) {
                        maxValue = Math.abs(values[i][j]);
                    }
                    if (Math.abs(values[i][j]) < minValue) {
                        minValue = Math.abs(values[i][j]);
                    }
                } else {
                    if (values[i][j] > maxValue) {
                        maxValue = values[i][j];
                    }
                    if (values[i][j] < minValue) {
                        minValue = values[i][j];
                    }
                }
            }
        }
        if (minValue < 0) {
            if (Math.abs(minValue) > maxValue) {
                maxValue = Math.abs(minValue);
            } else {
                minValue = -maxValue;
            }
        }

        activeArea = new Vector4f(70, 70, this.getWidth() - 160, this.getHeight() - 70);

        FontMetrics fm = getFontMetrics(getFont());
        int h = fm.getHeight();
        /*  if (cellHeight < h) {
         Font yFont = g2.getFont();
         g2.setFont(yFont.deriveFont(cellHeight));
         }*/
        Font f = getFont();
        f = f.deriveFont(10f);
        g2.setFont(f);

        if (numValuesY > width) {
            numValuesY = width;
        }
        float s1 = (slider1Tip.y - 70) * ((2f / 3f) / (this.getHeight() - 140));
        float s2 = (slider2Tip.y - 70) * ((2f / 3f) / (this.getHeight() - 140));
        float distance = (maxValue - minValue) / (2f / 3f);

        for (int i = 0; i < numValuesX; i++) {
            for (int j = 0; j < numValuesY; j++) {
                g2.setPaint(Color.BLACK);
                if (j == 0) {
                    fm = getFontMetrics(getFont());
                    h = fm.getHeight();
                    if (h > cellHeight) {
                        h = (int) cellHeight;
                    }
                    int w = fm.stringWidth(names[i]);
                    if (w > 65) {

                    }

                    g2.drawString(names[i], 75 - w, 70 + (i + 1) * cellHeight + i - (cellHeight - h));
                }

                float v = values[i][j];
                if (absolute) {
                    v = Math.abs(v);
                }
                v = (v - minValue) / distance;
                ColorSelector s = new ColorSelector();

                if (v >= s1 && v <= s2) {
                    if (absolute) {
                        Color c = s.chooseColor(minValue, maxValue, Math.abs(values[i][j]), scheme);
                        g2.setPaint(c);
                    } else {
                        Color c = s.chooseColor(minValue, maxValue, values[i][j], scheme);
                        g2.setPaint(c);

                    }
                } else {
                    g2.setPaint(Color.getHSBColor(1, 0, 0.5f));
                }

                /* if (v >= s1 && v <= s2) {
                 if (v < 1 / (float) 3) {
                 g2.setPaint(Color.getHSBColor(1, 1 - 2 * v, 1 - v));
                 } else {
                 g2.setPaint(Color.getHSBColor(2 / (float) 3, v * 2 - (1 / 3f), 1 - (2 / (float) 3 - v)));
                 }
                 } else {
                 g2.setPaint(Color.getHSBColor(v, 0, 0.5f));
                 }*/
                g2.draw(new Line2D.Double(70 + j, 70 + (i * cellHeight) + i, 70 + j, 70 + (i * cellHeight) + i + cellHeight - 1));

            }
        }

        paintSlider(g2, slider1Tip);

        paintSlider(g2, slider2Tip);

        paintScale(g2, maxValue, minValue,
                10);
        if (select) {
            paintSelection(g2, numValuesX, numValuesY);
            paintSubselection(g2);
            paintZoom(g2);
        }
        //     Font f = g2.getFont();
        Font fn = g2.getFont();

        g2.setPaint(Color.BLACK);

        g2.fill(
                new Rectangle2D.Double(70, 35, 165, 5));

        int xPoints[] = {235, 250, 235};
        int yPoints[] = {30, 38, 45};

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.fill(
                new Polygon(xPoints, yPoints, 3));

        fn.deriveFont(Font.BOLD,
                20f);
        g2.setFont(fn);

        g2.drawString(
                "Vertices", 70, 30);

        g2.setFont(f);

    }

    private void paintScale(Graphics2D g2, float max, float min, int steps) {
        g2.draw(new Line2D.Float(this.getWidth() - 70, 70, this.getWidth() - 70, this.getHeight() - 70));
        float stepHeight = (this.getHeight() - 140) / (float) steps;
        float step = (max - min) / (float) steps;
        for (int i = 0; i <= steps; i++) {
            g2.draw(new Line2D.Float(this.getWidth() - 80, 70 + (i * stepHeight), this.getWidth() - 70, 70 + (i * stepHeight)));
            float v = min + i * step;
            FontMetrics fm = getFontMetrics(getFont());
            float h = fm.getHeight();
            float w = fm.stringWidth(Float.toString(v));
            g2.drawString(Float.toString(v), this.getWidth() - 80 - w, 70 + (i * stepHeight) + (h / 3f));
        }

    }

    private void paintZoom(Graphics2D g2) {
        float width = this.getWidth() - 230;

        if (vl[0].length > width) {
            float maxValue = Float.MIN_VALUE;
            float minValue = Float.MAX_VALUE;
            for (int i = 0; i < vl.length - 1; i++) {
                for (int j = 0; j < vl[0].length - 1; j++) {
                    if (absolute) {
                        if (Math.abs(vl[i][j]) > maxValue) {
                            maxValue = Math.abs(vl[i][j]);
                        }
                        if (Math.abs(vl[i][j]) < minValue) {
                            minValue = Math.abs(vl[i][j]);
                        }
                    } else {
                        if (vl[i][j] > maxValue) {
                            maxValue = vl[i][j];
                        }
                        if (vl[i][j] < minValue) {
                            minValue = vl[i][j];
                        }
                    }
                }
            }
            if (minValue < 0) {
                if (Math.abs(minValue) > maxValue) {
                    maxValue = Math.abs(minValue);
                } else {
                    minValue = -maxValue;
                }
            }

            float s1 = (slider1Tip.y - 70) * ((2f / 3f) / (this.getHeight() - 140));
            float s2 = (slider2Tip.y - 70) * ((2f / 3f) / (this.getHeight() - 140));
            float distance = (maxValue - minValue) / (2f / 3f);

            int ratio = (int) Math.ceil(vl[0].length / (int) Math.floor(width));
            int x = subselectionTip.x - 70;
            int first = x * ratio;
            int y = lastClickedPoint.y;
            y = (int) Math.floor((y - 70) / (cellHeight + 1));
            for (int i = first; i < (first + width); i++) {
                if (i >= 0 && y >= 0) {
                    float v = vl[y][i];
                    if (absolute) {
                        v = Math.abs(v);
                    }
                    v = (v - minValue) / distance;
                    ColorSelector s = new ColorSelector();

                    if (v >= s1 && v <= s2) {
                        if (absolute) {
                            Color c = s.chooseColor(minValue, maxValue, Math.abs(vl[y][i]), scheme);
                            g2.setPaint(c);
                        } else {
                            Color c = s.chooseColor(minValue, maxValue, vl[y][i], scheme);
                            g2.setPaint(c);

                        }

                    } else {
                        g2.setPaint(Color.getHSBColor(1, 0, 0.5f));
                    }

                    /*  if (v >= s1 && v <= s2) {
                     // g2.setPaint(Color.getHSBColor(v, 1, 1));
                     if(v<1/(float)3){
                     g2.setPaint(Color.getHSBColor(1, 1-2*v, 1-v));
                     }
                     else{
                     g2.setPaint(Color.getHSBColor(2/(float)3,v*2-(1/3f), 1-(2/(float)3-v)));
                     }
                     } else {
                     g2.setPaint(Color.getHSBColor(v, 0, 1));
                     }*/
                    g2.draw(new Line2D.Double(70 + i - first, this.getHeight() - 60, 70 + i - first, this.getHeight() - 60 + 45));
                }
            }

        }
    }

    private void paintSubselection(Graphics2D g2) {
        float x = lastClickedPoint.x;
        float y = lastClickedPoint.y;
        y = (float) Math.floor((y - 70) / (cellHeight + 1));
        Color c = Color.getHSBColor(5f / 6f, 1, 1);
        g2.setPaint(c);
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Rectangle2D.Double(subselectionTip.x, subselectionTip.y, subselWidth, cellHeight + 9));

        g2.draw(new Rectangle2D.Double(67, this.getHeight() - 63, this.getWidth() - 225, 50));

    }

    private void paintSelection(Graphics2D g2, int numValuesX, int numValuesY) {
        float x = lastClickedPoint.x;
        float y = lastClickedPoint.y;
        y = (float) Math.floor((y - 70) / (cellHeight + 1));
        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Rectangle2D.Double(68, 67 + (y * cellHeight) + y, this.getWidth() - 228, cellHeight + 5));
        g2.setStroke(new BasicStroke(1));

    }

    private void paintSlider(Graphics2D g2, Point sliderTip) {
        g2.setPaint(Color.BLACK);
        int xPoints[] = {(int) sliderTip.x, (int) sliderTip.x + 20, (int) sliderTip.x + 20};
        int yPoints[] = {(int) sliderTip.y, (int) sliderTip.y + -10, (int) sliderTip.y + 10};

        g2.draw(new Line2D.Float(new Point(sliderTip.x - 20, sliderTip.y), sliderTip));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.fill(new Polygon(xPoints, yPoints, 3));
    }

    private boolean pointInActiveArea(Point p) {
        return p.x >= activeArea.x && p.y > activeArea.y && p.x <= activeArea.z && p.y < activeArea.w;
    }

    private void paintGrdient(Graphics2D g2, float x, float y, float height, float width) {
        int parts = 50;
        float fraction = height / (float) parts;
        /*   for (int i = 0; i < parts; i++) {
         Color hsb1 = Color.getHSBColor(((2 / 3f) / (float) parts) * i, 1, 1f);
         Color hsb2 = Color.getHSBColor(((2 / 3f) / (float) parts) * (i + 1), 1, 1f);
         float h = 70 + ((i + 1) * fraction);

         GradientPaint gp = new GradientPaint(0, y + (i * fraction), hsb1, 0, y + ((i + 1) * fraction), hsb2);
         g2.setPaint(gp);
         g2.fill(new Rectangle2D.Double(x, y + (i * fraction), width, fraction));
         }*/
        ColorSelector s = new ColorSelector();
        for (int i = 0; i < parts; i++) {

            Color hsb1 = s.chooseColor(0, (2 / 3f), ((2 / 3f) / (float) parts) * i, scheme);
            Color hsb2 = s.chooseColor(0, (2 / 3f), ((2 / 3f) / (float) parts) * (i + 1), scheme);

            //    Color hsb1 = Color.getHSBColor(1, 1-(1 / (float) (parts/2)) * i, 1-(1 /(3* (float) (parts/2))) * i);
            //Color hsb2 = Color.getHSBColor(1, 1-(1 / (float) (parts/2)) * (i + 1), 1-(1 /(3*(float) (parts/2))) * (i+1));
            float h = 70 + ((i + 1) * fraction);

            GradientPaint gp = new GradientPaint(0, y + (i * fraction), hsb1, 0, y + ((i + 1) * fraction), hsb2);
            g2.setPaint(gp);
            g2.fill(new Rectangle2D.Double(x, y + (i * fraction), width, fraction));
        }
        /*  for (int i = parts / 2; i < parts; i++) {
         Color hsb1 = Color.getHSBColor(2 / (float) 3, (1 / (float) (parts / 2) * (i - (parts / 2))), 2 / (float) 3 + (1 / (3 * (float) (parts / 2))) * (i - (parts / 2))); //(1 / (3*(float) (parts-(parts/2))) * (i-(parts-(parts/2)))));
         Color hsb2 = Color.getHSBColor(2 / (float) 3, (1 / (float) (parts / 2) * (i + 1 - (parts / 2))), 2 / (float) 3 + (1 / (3 * (float) (parts / 2))) * (i + 1 - (parts / 2)));// (1 / (3*(float) (parts-(parts/2))) * (i + 1-(parts-(parts/2)))));
         float h = 70 + ((i + 1) * fraction);

         GradientPaint gp = new GradientPaint(0, y + (i * fraction), hsb1, 0, y + ((i + 1) * fraction), hsb2);
         g2.setPaint(gp);
         g2.fill(new Rectangle2D.Double(x, y + (i * fraction), width, fraction));
         }*/
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
