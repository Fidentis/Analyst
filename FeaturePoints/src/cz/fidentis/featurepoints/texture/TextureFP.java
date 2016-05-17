package cz.fidentis.featurepoints.texture;

import cz.fidentis.featurepoints.FacialPointType;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import org.opencv.core.Point;

/**
 *
 * @author Galvanizze
 */
public class TextureFP {

    private FacialPointType type;
    public float x;
    public float y;

    public TextureFP(FacialPointType type, Point point) {
        this.type = type;
        this.x = (float) point.x;
        this.y = (float) point.y;
    }

    public TextureFP(FacialPointType type, Tuple2d point) {
        this.type = type;
        this.x = (float) point.x;
        this.y = (float) point.y;
    }

    public TextureFP(FacialPointType type, Tuple2f point) {
        this.type = type;
        this.x = point.x;
        this.y = point.y;
    }

    public FacialPointType getType() {
        return type;
    }

    public void setType(FacialPointType type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    public void setPoint(Point p) {
        this.x = (float)p.x;
        this.y = (float)p.y;
    }
    
    public Point getPoint() {
        return new Point(this.x, this.y);
    }

}
