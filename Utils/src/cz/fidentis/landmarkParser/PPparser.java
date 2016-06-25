/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.landmarkParser;

import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Galvanizze
 */
public class PPparser {

    public static FpModel load(String filePath) {

        FpModel fpModel = new FpModel();
        int startNameIdx = 0;

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(filePath);

            doc.getDocumentElement().normalize();

            // Nazov modelu
            NodeList nodeList = doc.getElementsByTagName("DataFileName");
            Node node = nodeList.item(0);
            Element nameEl = (Element) node;
            fpModel.setModelName(nameEl.getAttribute("name"));

            nodeList = doc.getElementsByTagName("point");

            // Body
            for (int i = 0; i < nodeList.getLength(); i++) {

                node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    FacialPoint facialPoint = new FacialPoint();
                    Element pointEl = (Element) node;

                    if (Integer.parseInt(pointEl.getAttribute("active")) == 0) {
                        continue;
                    }

                    int intName = Integer.parseInt(pointEl.getAttribute("name"));
                    
                    // nekontrolovat - prvy bod s indexom 0 nemusi byt vobec ulozeny
//                    // v prvom cykle skontolovat ci sa typy bodov indexuju od 0 alebo od 1
//                    if (i == 0) {
//                        if (intName == 1) {
//                            startNameIdx = 1;
//                            intName--;
//                        } else if (intName > 1) {
//                            // rovnako ako ked je zaciatocny index 0
//                        }
//                    } else if (startNameIdx == 1) {
//                        intName--;
//                    }

                    // zatial, ak ma nejaky bod index vacsi ako 42, tak nastavit na 42 - viac typov nemame definovanych
                    if (intName > 42) {
                        intName = 42;
                    }

                    facialPoint.setType(FacialPointType.values()[intName]);

                    facialPoint.getPosition().setX(Float.parseFloat(pointEl.getAttribute("x")));
                    facialPoint.getPosition().setY(Float.parseFloat(pointEl.getAttribute("y")));
                    facialPoint.getPosition().setZ(Float.parseFloat(pointEl.getAttribute("z")));

                    fpModel.addFacialPoint(facialPoint);

//                    int j = i + 1;
//                    System.out.println("Point " + j + ": " + facialPoint.getType().toString() + "; x = " + facialPoint.x + ", y = " + facialPoint.y + ", z = " + facialPoint.z);
                }
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
        }

        return fpModel;
    }

    public static void save(FpModel fpModel, String path) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

//        // Doctype - tag s vykricnikom sa neda pridat
//        Element docTypeElement = doc.createElement("!DOCTYPE PickedPoints");
        
        // PickedPoints
        
        Element ppElement = doc.createElement("PickedPoints");
        doc.appendChild(ppElement);

        Comment comment = doc.createComment("Saved by software Fidentis Analyst");
        doc.insertBefore(comment, ppElement);

        // DocumentData
        Element docDataElement = doc.createElement("DocumentData");
        ppElement.appendChild(docDataElement);

        Element dateTimeElement = doc.createElement("DateTime");
        docDataElement.appendChild(dateTimeElement);
        dateTimeElement.setAttribute("time", getTime());
        dateTimeElement.setAttribute("date", getDate());

        Element unameElement = doc.createElement("User");
        docDataElement.appendChild(unameElement);
        unameElement.setAttribute("name", "Fidentis");

        Element fnameElement = doc.createElement("DataFileName");
        docDataElement.appendChild(fnameElement);
        fnameElement.setAttribute("name", fpModel.getModelName());

        Element tnameElement = doc.createElement("templateName");
        docDataElement.appendChild(tnameElement);
        tnameElement.setAttribute("name", ".pickPointsTemplate.pptpl");

        // Points
        int iter = 0;
        for (FacialPoint point : fpModel.getFacialPoints()) {
            iter++;

            Element pointElement = doc.createElement("point");
            ppElement.appendChild(pointElement);

            pointElement.setAttribute("x", Float.toString(point.getPosition().getX()));
            pointElement.setAttribute("y", Float.toString(point.getPosition().getY()));
            pointElement.setAttribute("z", Float.toString(point.getPosition().getZ()));
            pointElement.setAttribute("active", "1");
            pointElement.setAttribute("name", Integer.toString(point.getType().ordinal()));
        }

        
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);

//      // Print
//      tf.transform(source, new StreamResult(System.out));
      
        // Save
        String filePath = path + setExtension(fpModel.getModelName(), "pp");
        File file = new File(filePath);
        tf.transform(source, new StreamResult(file));
    }
    

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
    
    public static String setExtension(String fileName, String newExtension ) {
        String newFileName = fileName.substring(0, fileName.lastIndexOf('.') + 1);
        return newFileName + newExtension;
    }

}
