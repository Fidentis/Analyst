/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import cz.fidentis.featurepoints.FacialPoint;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3f;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Marek Zuzi
 */
public class ImageFpCanvas extends JLabel {
    private List<FacialPoint> points;
    private double origWidth = 100;
    private double origHeight = 100;
    private int offsetY = 0;
    private int offsetX = 0;
    
    private int selectedPoint = -1;
    
    public ImageFpCanvas() {
        super();
        CanvasMouseListener lis = new CanvasMouseListener();
        this.addMouseListener(lis);
        this.addMouseMotionListener(lis);
    }
    
    public void setPoints(List<FacialPoint> pts) {
        this.points = pts;
        this.repaint();
    }

    public void setImage(File imgFile) {
        try {
            BufferedImage origI = ImageIO.read(imgFile);
            origWidth = origI.getWidth();
            origHeight = origI.getHeight();
            BufferedImage resized = resizeToFit(origI, this.getWidth(), this.getHeight());
            this.setIcon(new ImageIcon(resized));
            offsetY = (this.getHeight() - this.getIcon().getIconHeight()) / 2;
            offsetX = (this.getWidth() - this.getIcon().getIconWidth()) / 2;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not open image.");
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Icon icon = this.getIcon();
        if(points == null || icon == null) return;
        
        g.setColor(Color.red);
        for(FacialPoint p : points) {
            Vector3f coords = pointToCoords(p.getPosition());
            g.fillOval((int)coords.x-4, (int)coords.y-4, 8, 8);
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
    
    private Vector3f pointToCoords(Vector3f point) {
        if(this.getIcon() == null) {
            return new Vector3f();
        }
        double x = (point.x / origWidth) * this.getIcon().getIconWidth();
        double y = ((origHeight - point.y) / origHeight) * this.getIcon().getIconHeight();
        return new Vector3f((float)x + offsetX, (float)y + offsetY, 0);
    }
    
    private Vector3f coordsToPoint(int x, int y) {
        double px = ((double)(x - offsetX) / this.getIcon().getIconWidth()) * origWidth;
        double py = (1-((double)(y - offsetY) / this.getIcon().getIconHeight())) * origHeight;
        return new Vector3f((float)px, (float)py, 0);
    }
    
    private class CanvasMouseListener extends MouseAdapter
    {
        @Override
        public void mouseReleased(MouseEvent e) {
            selectedPoint = -1;
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(getIcon() == null || points == null) {
                return;
            }
            for(int i=0;i<points.size();i++) {
                Vector3f mousePoint = coordsToPoint(e.getX(), e.getY());
                mousePoint.sub(points.get(i).getPosition());
                if(Math.abs(mousePoint.x) < 16 && Math.abs(mousePoint.y) < 16) {
                    selectedPoint = i;
                    return;
                }
            }
            selectedPoint = -1;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(selectedPoint != -1) {
                points.get(selectedPoint).setCoords(coordsToPoint(e.getX(), e.getY()));
                repaint();
            }
        }
        
        
    }
}
