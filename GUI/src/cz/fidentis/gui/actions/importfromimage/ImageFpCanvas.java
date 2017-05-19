/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.importfromimage;

import cz.fidentis.gui.actions.importfromimage.ImportFromImage;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3f;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Marek Zuzi
 */
public class ImageFpCanvas extends JPanel {
    public static final String FP_PROPERTY_NAME = "featurePoints";
    private static final int LABEL_PADDING = 5;
    private static final int POINT_RADIUS = 5;
    
    private PropertyChangeListener _listener;
    
    private List<FacialPoint> points;
    private int offsetY = 0;
    private int offsetX = 0;
    
    private int selectedPoint = -1;
    
    private BufferedImage image;
    private int currentX = 0;
    private int currentY = 0;
    private int currentW;
    private int prevX;
    private int prevY;
    
    public ImageFpCanvas() {
        super();
        CanvasMouseListener lis = new CanvasMouseListener();
        this.addMouseListener(lis);
        this.addMouseMotionListener(lis);
        this.addMouseWheelListener(lis);
    }
    
    public void setPoints(List<FacialPoint> pts) {
        this.points = pts;
        for(FacialPoint p : points) {
            //p.getPosition().y = image.getHeight()-p.getPosition().y;
            constrainPoint(p.getPosition());
        }
        this.repaint();
        this.firePropertyChange("featurePoints", null, points);
    }

    public void setImage(File imgFile) {
        try {
            BufferedImage origI = ImageIO.read(imgFile);
            image = origI;
            currentX = 0; currentY = 0; currentW = image.getWidth();
            this.repaint();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not open image.");
        }
    }

    @Override
    public void paint(Graphics g) {
        if(image != null) {
            g.setColor(this.getBackground());
            g.clearRect(0, 0, this.getWidth(), this.getHeight());
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(),
                    currentX, currentY, currentX+currentW, currentY+computeHeight(), null);
        } else {
            super.paint(g);
        }
        
        if(selectedPoint != -1) {
            String str = points.get(selectedPoint).getName();
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D rect = fm.getStringBounds(str, g);
            g.setColor(Color.lightGray);
            g.fillRect(0, (int)(this.getHeight()-fm.getAscent()-rect.getHeight()), (int)rect.getWidth()+2*LABEL_PADDING, (int)rect.getHeight()+2*LABEL_PADDING);
            g.setColor(Color.black);
            g.drawString(str, LABEL_PADDING, (int)(this.getHeight()-rect.getHeight()+LABEL_PADDING));
        }
        
        if(points != null) {
            for (FacialPoint p : points) {
                if(p.isActive()) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.GRAY);
                }
                Vector3f coords = pointToCoords(p.getPosition());
                g.fillOval((int) coords.x - POINT_RADIUS, (int) coords.y - POINT_RADIUS, 2*POINT_RADIUS, 2*POINT_RADIUS);
            }
        }
    }
    
    private BufferedImage resizeToFit(BufferedImage img, int maxWidth, int maxHeight) {
        double w = img.getWidth();
        double h = img.getHeight();
        if(w < maxWidth && h < maxHeight) return img;
        
        double ratio = Math.min(maxWidth / w, maxHeight / h);
        int newW = (int)Math.floor(w * ratio);
        int newH = (int)Math.floor(h * ratio);
        Image temp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        
        BufferedImage result = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = result.createGraphics();
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        
        return result;
    }
    
    private int computeHeight() {
        if(image != null) {
            float aspect = this.getHeight() / (float) this.getWidth();
            return (int)(aspect*currentW);
        } else {
            return this.getHeight();
        }
    }
    
    private int hitPoint(int x, int y) {
        if(points == null || image == null) {
            return -1;
        }
        Vector3f mousePoint = new Vector3f(x,y,0);
        int d = POINT_RADIUS+2;
        for (int i = 0; i < points.size(); i++) {
            Vector3f point = pointToCoords(points.get(i).getPosition());
            point.sub(mousePoint);
            if (Math.abs(point.x) < d && Math.abs(point.y) < d) {
                return i;
            }
        }
        return -1;
    }
    
    private Vector3f pointToCoords(Vector3f point) {
        float x = (point.getX() - currentX)/currentW;
        float y = (point.getY() - currentY)/computeHeight();
        return new Vector3f(x*this.getWidth(), y*this.getHeight(), 0);
    }
    
    private Vector3f coordsToPoint(int x, int y) {
        float xr = ((float)x) / this.getWidth();
        float yr = ((float)y) / this.getHeight();
        return new Vector3f(currentX + currentW*xr, currentY + computeHeight()*yr, 0);
    }
    
    private void constrainView() {
        if(image == null) return;
        
        currentW = Math.max(currentW, 50);
        currentW = Math.min(currentW, image.getWidth());
        
        currentX = Math.max(currentX, 0);
        currentX = Math.min(currentX, image.getWidth()-currentW);
        currentY = Math.max(currentY, 0);
        currentY = Math.min(currentY, image.getHeight()-computeHeight());
    }
    
    private void constrainPoint(Vector3f position) {
        if(image == null) return;
        
        position.x = Math.min(Math.max(position.x, 0), image.getWidth());
        position.y = Math.min(Math.max(position.y, 0), image.getHeight());
    }
    
    private void addPoint(FacialPoint point) {
        points.add(point);
        repaint();
        this.firePropertyChange("featurePoints", null, points);
    }
    
    private void removePoint(int index) {
        points.remove(index);
        repaint();
        this.firePropertyChange("featurePoints", null, points);
    }
    
    private void togglePointActive(int index) {
        points.get(index).setActive(!points.get(index).isActive());
        repaint();
    }
    
    private class CanvasMouseListener extends MouseAdapter
    {
        @Override
        public void mouseReleased(MouseEvent me) {
            selectedPoint = -1;
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me)) {
                Vector3f pointCoords = coordsToPoint(me.getX(), me.getY());
                JPopupMenu popup = new JPopupMenu();

                final int pointIdx = hitPoint(me.getX(), me.getY());
                if (pointIdx >= 0) {
                    // remove action
                    JMenuItem remove = new JMenuItem("Remove point");
                    remove.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            removePoint(pointIdx);
                        }
                    });
                    popup.add(remove);
                    
                    // is active action
                    JMenuItem item = new JCheckBoxMenuItem("Active");
                    item.setSelected(points.get(pointIdx).isActive());
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            togglePointActive(pointIdx);
                        }
                    });
                    popup.add(item);
                } else {

                    JMenuItem title = new JMenuItem("Add point...");
                    title.setEnabled(false);
                    popup.add(title);
                    popup.addSeparator();

                    TreeSet<FacialPointType> required = ImportFromImage.getUsedPoints();
                    FacialPointType[] allTypes = FacialPointType.values();
                    for (FacialPoint p : points) {
                        required.remove(allTypes[p.getType()]);
                    }
                    for (FacialPointType typ : required) {
                        final FacialPoint menuPoint = new FacialPoint(typ.ordinal(), pointCoords);
                        JMenuItem item = new JMenuItem(menuPoint.getName());
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addPoint(menuPoint);
                            }
                        });
                        popup.add(item);
                    }
                }

                popup.show(ImageFpCanvas.this, me.getX(), me.getY());
            } else {
                prevX = me.getX();
                prevY = me.getY();
                selectedPoint = hitPoint(me.getX(), me.getY());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(selectedPoint != -1) {
                FacialPoint p = points.get(selectedPoint);
                p.setCoords(coordsToPoint(e.getX(), e.getY()));
                constrainPoint(p.getPosition());
            } else {
                Vector3f prev = coordsToPoint(prevX, prevY);
                Vector3f next = coordsToPoint(e.getX(), e.getY());
                prev.sub(next);
                currentX = currentX + (int)prev.getX();
                currentY = currentY + (int)prev.getY();
                prevX = e.getX();
                prevY = e.getY();
            }
            constrainView();
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                int count = e.getWheelRotation();
                int d = (count*currentW/10);
                currentW = currentW+(count*currentW/10);
                currentX = currentX - d/2;
                currentY = currentY - d/2;
            }
            constrainView();
            repaint();
        }
    }
}
