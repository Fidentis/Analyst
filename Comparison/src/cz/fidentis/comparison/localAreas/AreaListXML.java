/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.localAreas;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Container for Area class
 * @author Richard
 */
@XmlRootElement(name = "AreasList")
@XmlAccessorType(XmlAccessType.FIELD)
public class AreaListXML {
    private List<Area> areaList;
    
    //@XmlElement
    //@XmlElement(required=true)
    public void setAreaList(List<Area> areaList){
        this.areaList = areaList;
    }
    
//    @XmlAnyElement(lax=true)
    public List<Area> getAreaList(){
        return areaList;
    }
}
