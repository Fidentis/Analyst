/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller.data;

import cz.fidentis.visualisation.ColorScheme;

/**
 * Class holding configuration for Colormap visualization
 * 
 * @author xferkova
 */
public class ColormapConfig {
    private int hausdorfMaxTreshold = 100;     //max threshold value in % (HDPainting info contains actual computed distance threshold)
    private int hausdorfMinTreshold = 00;     //min threshold value in % (HDPainting info contains actual computed distance threshold)
    private ColorScheme usedColorScheme;

    public ColormapConfig() {
    }

    public int getHausdorfMaxTreshold() {
        return hausdorfMaxTreshold;
    }

    public void setHausdorfMaxTreshold(int hausdorfMaxTreshold) {
        this.hausdorfMaxTreshold = hausdorfMaxTreshold;
    }

    public int getHausdorfMinTreshold() {
        return hausdorfMinTreshold;
    }

    public void setHausdorfMinTreshold(int hausdorfMinTreshold) {
        this.hausdorfMinTreshold = hausdorfMinTreshold;
    }

    public ColorScheme getUsedColorScheme() {
        return usedColorScheme;
    }

    public void setUsedColorScheme(ColorScheme usedColorScheme) {
        this.usedColorScheme = usedColorScheme;
    }
    
    
}
