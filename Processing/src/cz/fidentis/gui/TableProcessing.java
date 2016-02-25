/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui;

import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.undersampling.Methods;
import cz.fidentis.undersampling.Type;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author xferkova
 */
public class TableProcessing {
    private static TableProcessing instance;
    
    private TableProcessing(){}
    
    public static TableProcessing instance(){
        if(instance == null){
            instance = new TableProcessing();
        }
        
        return instance;
    }
    
    public void setUpTable(JTable table, JFrame frame, Component tc, String frameName){
      table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0 || row == 0) {
                    c.setBackground(Color.ORANGE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                }
                return c;
            }
        });

        table.setTableHeader(null);
        table.setRowHeight(30);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setMinWidth(80);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() - 40);
        int height = (int) (screenSize.getHeight() - 40);

        int sizeH = (table.getRowCount() * 40 > height ? height : table.getRowCount() * 40);
        int sizeW = (table.getColumnCount() * 80 + 30 > width ? width : table.getColumnCount() * 80 + 40);

        frame.setTitle(frameName);
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        frame.setIconImage(icon);
        frame.setVisible(true);
        frame.setSize(sizeW, sizeH);
        frame.setLocationRelativeTo(tc);  
    }
    
    public String[][] parseTable(String result){
            String[] lines = result.split("\n");
            String[][] values = new String[lines.length][];
            
            for (int i = 0; i < lines.length; i++) {
                values[i] = lines[i].split(";");
            } 
            
            return values;
    } 
    
    public String[][] parseTableAddHeader(String result, String[] header){
        String[][] values = parseTable(result);
        
        String[][] res = new String[values.length + 1][];
        res[0] = header;
        
        System.arraycopy(values, 0, res, 1, values.length);
        
        return res;       
    }
    
    public String[][] alignmentInfoTable(ICPmetric metric, boolean scale, float error, int maxIteration, int avgMeshes, String templateName, Methods m, Type t, float value){        
        StringBuilder sb = new StringBuilder("Parameters;Values\n");
        
        sb.append("ICP metric;").append(metric).append("\n");
        sb.append("Use scale;").append(scale).append("\n");
        sb.append("Error rate;").append(error).append("\n");
        sb.append("Max iteration;").append(maxIteration).append("\n");
        
        if(avgMeshes > 0){
            sb.append("Average meshes;").append(avgMeshes).append("\n");
            sb.append("Average face template;").append(templateName).append("\n");
        }
        
        sb.append("Undersampling;").append(m).append("\n");
        if(m != Methods.None){  
            if(m != Methods.Disc){
               sb.append("Undersamplying Type;").append(t).append("\n"); 
            }
            sb.append("Undersampling value;").append(value).append("\n");
        }

        String[][] res = parseTable(sb.toString());
        
        return res;        
    }
}
