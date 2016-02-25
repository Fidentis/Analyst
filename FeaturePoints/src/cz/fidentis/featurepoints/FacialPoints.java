package cz.fidentis.featurepoints;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "facialPoints")
public class FacialPoints {
 
    @XmlElement(name = "facialPoint", type = FacialPoint.class)
    private List<FacialPoint> facialPoints = new ArrayList<FacialPoint>();
 
    public FacialPoints() {}
    
    public FacialPoints(List<FacialPoint> facialPoints) {
        this.facialPoints = facialPoints;
    }
 
    public List<FacialPoint> getFacialPoints() {
        return facialPoints;
    }
 
    public void setFacialPoints(List<FacialPoint> facialPoints) {
        this.facialPoints = facialPoints;
    }
}
