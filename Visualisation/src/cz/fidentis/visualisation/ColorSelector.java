/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.visualisation;

import java.awt.Color;

/**
 *
 * @author xfurman
 */
public class ColorSelector {

    public Color chooseColor(float min, float max, float value, ColorScheme cs) {
        switch (cs) {
            case RAINBOW:
                return chooseRainbowColor(min, max, value);
            case GREEN_BLUE:
                return chooseSequentialColor(min, max, value);
            case DIVERGING:
                return chooseDivergingColor(min, max, value);

        }
        return null;
    }

    private Color chooseRainbowColor(float min, float max, float value) {
        Color c1 = new Color(0, 0, 255);
        Color c2 = new Color(0, 255, 255);
        Color c3 = new Color(0, 255, 0);
        Color c4 = new Color(255, 255, 0);
        Color c5 = new Color(255,0, 0);
        float span = max - min;
        float pos = (value - min) / span;

        return chooseColor(c1, c2, c3, c4, c5, pos);
    }

    private Color chooseSequentialColor(float min, float max, float value) {
        Color c1 = new Color(255,255,204);
        Color c2 = new Color(161,218,180);
        Color c3 = new Color(65,182,196);
        Color c4 = new Color(44,127,184);
        Color c5 = new Color(37,52,148);
        float span = max - min;
        float pos = (value - min) / span;

        return chooseColor(c1, c2, c3, c4, c5, pos);
    }

    private Color chooseDivergingColor(float min, float max, float value) {
        Color c1 = new Color(44, 123, 182);
        Color c2 = new Color(171, 217, 233);
        Color c3 = new Color(255, 255, 191);
        Color c4 = new Color(253, 174, 97);
        Color c5 = new Color(215, 25, 28);
        float span = max - min;
        float pos = (value - min) / span;

        return chooseColor(c1, c2, c3, c4, c5, pos);

        /*  if (pos < 0.5f) {
         return interpolate(Color.red, Color.GRAY, pos / 0.5f);
         } else {
         return interpolate(Color.GRAY, Color.blue, (pos - 0.5f) / 0.5f);
         }*/
    }

    private Color chooseColor(Color c1, Color c2, Color c3, Color c4, Color c5, float pos) {
        if (pos < 1 / 4f) {
            return interpolate(c1, c2, pos / (1 / 4f));
        } else if (pos < 2 / 4f) {
            return interpolate(c2, c3, (pos - (1 / 4f)) / (1 / 4f));
        } else if (pos < 3 / 4f) {
            return interpolate(c3, c4, (pos - (2 / 4f)) / (1 / 4f));
        } else {
            return interpolate(c4, c5, (pos - (3 / 4f)) / (1 / 4f));
        }
    }

    private static Color interpolate(Color c1, Color c2, float ratio) {
        float r = Math.max(0, c2.getRed() * ratio + c1.getRed() * (1f - ratio)) / 255f;
        float g = Math.max(0, c2.getGreen() * ratio + c1.getGreen() * (1f - ratio)) / 255f;
        float b = Math.max(0, c2.getBlue() * ratio + c1.getBlue() * (1f - ratio)) / 255f;

        if (r < 0.0 || r > 1.0) {
            r = Math.round(r);
        }
        if (g < 0.0 || g > 1.0) {
            g = Math.round(g);
        }
        if (b < 0.0 || b > 1.0) {
            b = Math.round(b);
        }

        return new Color(r, g, b);
    }

}
