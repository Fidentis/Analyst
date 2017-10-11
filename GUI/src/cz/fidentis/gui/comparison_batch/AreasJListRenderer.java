/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.comparison_batch;

import cz.fidentis.comparison.localAreas.Area;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Richard
 */
public class AreasJListRenderer extends DefaultListCellRenderer  {
    
    private List<Area> areasList;
    
    public void setAreas(List<Area> value){
        areasList = value;
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
              boolean isSelected, boolean cellHasFocus) {
           Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
           if (!this.getText().equals("No Area was found!")){
               for (Area area : areasList){
                   if(this.getText().equals(area.index+" Area")){
                       setBackground(new Color(area.color.get(0), area.color.get(1), area.color.get(2)));
                   }
               }

           }

           if (isSelected) {
               setBackground(getBackground().white);
           }
         return c;
    }
    
}
