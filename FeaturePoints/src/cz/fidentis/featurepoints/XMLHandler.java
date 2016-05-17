package cz.fidentis.featurepoints;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// obsolete
public class XMLHandler {
 
    // Export
    public static void marshal(List<FacialPoint> facialPoints, File selectedFile)
            throws IOException, JAXBException {
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(FacialPoints.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(new FacialPoints(facialPoints), writer);
        writer.close();
    }
 
    // Import
    public static List<FacialPoint> unmarshal(File importFile) throws JAXBException {
        FacialPoints facialPoints = new FacialPoints();
 
        JAXBContext context = JAXBContext.newInstance(FacialPoints.class);
        Unmarshaller um = context.createUnmarshaller();
        facialPoints = (FacialPoints) um.unmarshal(importFile);
 
        return facialPoints.getFacialPoints();
    }
}