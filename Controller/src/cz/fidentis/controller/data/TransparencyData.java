/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller.data;

import java.awt.Color;

/**
 *
 * @author xferkova
 */
public class TransparencyData {
    private Color primaryColor = new Color(51,153,255, 255);        //color for primary model when overlaid before comparison(blueish?)
    private Color secondaryColor = new Color(255,255,0,255);        //color for secondary model when overlaid before comparison(yellow)
    private boolean isPrimarySolid = false;
    private boolean isSecondarySolid = false;
    private Color fogColor;
    private float overlayTransparency;
    private boolean innerSurfaceSolid;
    private boolean useGlyphs;
    private boolean useContours;
    private int fogVersion;

    public TransparencyData() {
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(Color primaryColor) {
        this.primaryColor = primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public boolean isIsPrimarySolid() {
        return isPrimarySolid;
    }

    public void setIsPrimarySolid(boolean isPrimarySolid) {
        this.isPrimarySolid = isPrimarySolid;
    }

    public boolean isIsSecondarySolid() {
        return isSecondarySolid;
    }

    public void setIsSecondarySolid(boolean isSecondarySolid) {
        this.isSecondarySolid = isSecondarySolid;
    }

    public Color getFogColor() {
        return fogColor;
    }

    public void setFogColor(Color fogColor) {
        this.fogColor = fogColor;
    }

    public float getOverlayTransparency() {
        return overlayTransparency;
    }

    public void setOverlayTransparency(float overlayTransparency) {
        this.overlayTransparency = overlayTransparency;
    }

    public boolean isInnerSurfaceSolid() {
        return innerSurfaceSolid;
    }

    public void setInnerSurfaceSolid(boolean innerSurfaceSolid) {
        this.innerSurfaceSolid = innerSurfaceSolid;
    }

    public boolean isUseGlyphs() {
        return useGlyphs;
    }

    public void setUseGlyphs(boolean useGlyphs) {
        this.useGlyphs = useGlyphs;
    }

    public boolean isUseContours() {
        return useContours;
    }

    public void setUseContours(boolean useContours) {
        this.useContours = useContours;
    }

    public int getFogVersion() {
        return fogVersion;
    }

    public void setFogVersion(int fogVersion) {
        this.fogVersion = fogVersion;
    }
    
    
}
