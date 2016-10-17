package cz.fidentis.featurepoints;

import cz.fidentis.*;
import java.io.Serializable;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import jv.vecmath.PdVector;

/**
 *
 * @author Galvi
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "facialPoint")
public class FacialPoint implements Serializable {
    
    private Integer type;
    private boolean active = true;

    private Vector3f pos;

    public FacialPoint() {
        this.pos = new Vector3f();
    }
    
    public FacialPoint(Integer type, PdVector coords) {        
        this.pos = new Vector3f((float)coords.getEntry(0), (float)coords.getEntry(1), (float)coords.getEntry(2));
        
        this.type = type;
    }
    
    public FacialPoint(Integer type) {
        this.type = type;
    }

    
    public FacialPoint(Integer type, Tuple3f coords) {        
        this.pos = new Vector3f(coords);
        
        this.type = type;
    }
    
    public FacialPoint(Integer type, Tuple3d coords) {        
        this.pos = new Vector3f(coords);
        
        this.type = type;
    }    

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return FpTexter.getInstance().getFPname(type);
    }
    
    public String getInfo() {
        return FpTexter.getInstance().getFPinfo(type);
    }

    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public void setCoords(Tuple3f coords) {        
        this.pos = new Vector3f(coords);
    }
    
    public Point3f getCoords() {
        return new Point3f(pos.x, pos.y, pos.z);
    }
    
    public Vector3f getPosition(){
        return pos;
    }
    
    public String toCSVstring(String separator){
        return this.pos.x + separator + this.pos.y + separator + this.pos.z;
    }
     
    @Override
    public String toString(){
        return type + ", " + getName() + ", " + getInfo();
    }


}
