/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.landmarkParser;


import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.featurepoints.FacialPoint;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openide.util.Exceptions;
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
public class FPparser {
    
    private static final String FILE_PROTOCOL = "file:" + File.separator;

    public static FpModel load(String filePath) {

        FpModel fpModel = new FpModel();

        try {
            if(!filePath.startsWith(FILE_PROTOCOL))
                filePath = FILE_PROTOCOL + filePath;
            
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(filePath);

            doc.getDocumentElement().normalize();

            // Nazov korenoveho elementu
            assert "facialPoints".equals(doc.getDocumentElement().getNodeName());
            
            fpModel.setModelName(doc.getDocumentElement().getAttribute("model"));
            

            NodeList nodeList = doc.getElementsByTagName("facialPoint");

            // Body
            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    FacialPoint facialPoint = new FacialPoint();
                    Element pointEl = (Element) node;
                    Integer value;
                    
                    try{
                        value = Integer.parseInt(pointEl.getAttribute("type"));
                    }catch(NumberFormatException ex){
                        value = -1;
                    }
                    
                    facialPoint.setType(value);

                    facialPoint.getPosition().setX(Float.parseFloat(pointEl.getElementsByTagName("x").item(0).getTextContent()));
                    facialPoint.getPosition().setY(Float.parseFloat(pointEl.getElementsByTagName("y").item(0).getTextContent()));
                    facialPoint.getPosition().setZ(Float.parseFloat(pointEl.getElementsByTagName("z").item(0).getTextContent()));

                    fpModel.addFacialPoint(facialPoint);

                }
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            Exceptions.printStackTrace(e);
        }
        return fpModel;
    }

    public static void save(FpModel fpModel, String path) throws ParserConfigurationException, TransformerConfigurationException, TransformerException  {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

        // facialPoints
        Element fpElement = doc.createElement("facialPoints");
        doc.appendChild(fpElement);
        fpElement.setAttribute("model", fpModel.getModelName());

        Comment comment = doc.createComment("Saved by software Fidentis Analyst");
        doc.insertBefore(comment, fpElement);

        // Body
        for (FacialPoint point : fpModel.getFacialPoints()) {

            Element pointElement = doc.createElement("facialPoint");
            fpElement.appendChild(pointElement);

            pointElement.setAttribute("type", point.getType().toString());

            Element coordElement = doc.createElement("x");
            coordElement.appendChild(doc.createTextNode(Float.toString(point.getPosition().getX())));
            pointElement.appendChild(coordElement);

            coordElement = doc.createElement("y");
            coordElement.appendChild(doc.createTextNode(Float.toString(point.getPosition().getY())));
            pointElement.appendChild(coordElement);

            coordElement = doc.createElement("z");
            coordElement.appendChild(doc.createTextNode(Float.toString(point.getPosition().getZ())));
            pointElement.appendChild(coordElement);
        }

        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);

//      // Print
//      tf.transform(source, new StreamResult(System.out));
     
        // Save
        
        int lastIndex = path.lastIndexOf('.');
        String filePath = path.substring(0, lastIndex) + setExtension(fpModel.getModelName(), "fp");
        File file = new File(filePath);
        tf.transform(source, new StreamResult(file));
    }
    

    public static String setExtension(String fileName, String newExtension) {
        String newFileName = fileName.substring(0, fileName.lastIndexOf('.') + 1);
        return newFileName + newExtension;
    }
}
