///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package cz.fidentis.featurepoints;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import javax.vecmath.Vector3f;
//import javax.xml.stream.XMLEventFactory;
//import javax.xml.stream.XMLEventWriter;
//import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.events.Characters;
//import javax.xml.stream.events.EndElement;
//import javax.xml.stream.events.StartDocument;
//import javax.xml.stream.events.StartElement;
//import javax.xml.stream.events.XMLEvent;
//
///**
// *
// * @author Zully92
// */
//public class FPFilesWorker {
//    private List<FacialPoint> facialPoints;
//    public static int NUM_OF_FP = 8;
//
//    public FPFilesWorker(){
//        this.facialPoints = new ArrayList<FacialPoint>();
//    }
//    
//    public FPFilesWorker(List<FacialPoint> facialPoints){
//        this.facialPoints = facialPoints;
//    }
//    
//    public List<FacialPoint> getFeatPoints() {
//        return facialPoints;
//    }
//    
//    public List<FacialPoint> loadFeatPoints(File fPFile) throws IOException{
//        facialPoints = new ArrayList<FacialPoint>();
//        BufferedReader br;
//        FileReader fr;
//        
//        try{
//            fr = new FileReader(fPFile);
//            br = new BufferedReader(fr);
//            String line = br.readLine();
//            line = line.trim();
//            
//            while (line != null) {
//                if (line.length() > 0) {
//                    line = line.trim();
//                    
//                    System.out.println(line);
//                    
//                    if(line.startsWith("<EX_R>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.EX_R, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if(line.startsWith("<EX_L>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.EX_L, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if(line.startsWith("<EN_R>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.EN_R, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if(line.startsWith("<EN_L>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.EN_L, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if (line.startsWith("<PRN>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.PRN, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if (line.startsWith("<STO>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.STO, parsePosition(br.readLine(), br.readLine(), br.readLine()))); 
//                    } else if(line.startsWith("<CH_R>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.CH_R, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    } else if(line.startsWith("<CH_L>")){
//                        facialPoints.add(new FacialPoint(FacialPointType.CH_L, parsePosition(br.readLine(), br.readLine(), br.readLine())));
//                    }
//                     line = br.readLine();
//                     line = br.readLine();
//                }
//            }    
//        } catch (FileNotFoundException f) {
//            throw new IOException("File not found");
//        } catch (NumberFormatException n){
//            throw new IOException("Vertices are in wrong format");
//        }
//        
//        return facialPoints;
//    }
//    
//    public void exportFPToXml(List<FacialPoint> facialPoints, String file) throws XMLStreamException, FileNotFoundException, IOException{
//        FileOutputStream fos = null;
//        XMLEventWriter ew = null;
//        
//        try{
//            XMLOutputFactory outFact = XMLOutputFactory.newInstance();
//            
//            fos = new FileOutputStream(file);
//            ew = outFact.createXMLEventWriter(fos);
//            
//            XMLEventFactory eventFact = XMLEventFactory.newInstance();
//            
//            XMLEvent end = eventFact.createDTD("\n");
//            
//            StartDocument startDocument = eventFact.createStartDocument();
//            StartElement sElement = eventFact.createStartElement("", "", "featurePoints");
//            
//            ew.add(startDocument);
//            ew.add(sElement);
//            ew.add(end);
//
//            for(FacialPoint fp : facialPoints){
//                createNode(ew, fp.getType().toString(), fp.getCoords());
//            }
//
//            ew.add(eventFact.createEndElement("", "", "featurePoints"));
//            ew.add(end);
//            ew.add(eventFact.createEndDocument());
//            
//        }catch(FileNotFoundException f){
//                throw new IOException("File not found");
//        }finally{
//            ew.close();
//        }
//    }
//    
//    public void convertFPTxtToXml(File txt, String xml) throws IOException, XMLStreamException{
//        FPFilesWorker helpInstance = new FPFilesWorker();
//        facialPoints = helpInstance.loadFeatPoints(txt);
//        helpInstance.exportFPToXml(facialPoints, xml);
//    }
//    
//    public void createFPXmlFromTxt(File file) throws IOException, XMLStreamException{
//        FPFilesWorker fPs = null;
//        
//        BufferedReader br;
//        FileReader fr;
//        
//        try{
//            fr = new FileReader(file);
//            br = new BufferedReader(fr);
//            br.readLine();
//            String line = br.readLine();
//                    
//            while (line != null) {
//                if (line.length() > 0) {
//                    String[] arrayFP = new String[25];
//                    arrayFP = line.split(";",25);
//                    
//                    fPs = new FPFilesWorker(createFPs(arrayFP));
//                    fPs.exportFPToXml(facialPoints, arrayFP[0] + ".fp");
//                    
//                    line = br.readLine();
//                }
//            }
//        }catch(FileNotFoundException f){
//            throw new IOException("File not found");
//        }
//    }
//    
//    private void createNode(XMLEventWriter eventWriter, String fPtype, Vector3f coord) throws XMLStreamException {
//
//        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
//        XMLEvent end = eventFactory.createDTD("\n");
//        XMLEvent tab = eventFactory.createDTD("\t");
//        
//        StartElement sElement = eventFactory.createStartElement("", "", fPtype);
//        StartElement xElement = eventFactory.createStartElement("", "", "x");
//        StartElement yElement = eventFactory.createStartElement("", "", "y");
//        StartElement zElement = eventFactory.createStartElement("", "", "z");
//        EndElement eXElement = eventFactory.createEndElement("", "", "x");
//        EndElement eYElement = eventFactory.createEndElement("", "", "y");
//        EndElement eZElement = eventFactory.createEndElement("", "", "z");
//        
//        String x = Float.toString(coord.getX());
//        String y = Float.toString(coord.getY());
//        String z = Float.toString(coord.getZ());
//        
//        //FP
//        eventWriter.add(tab);
//        eventWriter.add(sElement);
//        eventWriter.add(end);
//          
//        //X
//        eventWriter.add(tab);
//        eventWriter.add(tab);
//        eventWriter.add(xElement);
//        
//        Characters xValue = eventFactory.createCharacters(x);
//        eventWriter.add(xValue);
//        
//        
//        eventWriter.add(eXElement);
//        eventWriter.add(end);
//        
//        //<Y>                
//        eventWriter.add(tab);
//        eventWriter.add(tab);
//        eventWriter.add(yElement);
//        
//        Characters yValue = eventFactory.createCharacters(y);
//        eventWriter.add(yValue);
//        
//        eventWriter.add(eYElement);
//        eventWriter.add(end);
//        
//        //<Z>  
//        eventWriter.add(tab);
//        eventWriter.add(tab);
//        eventWriter.add(zElement);
//        
//        Characters zValue = eventFactory.createCharacters(z);
//        eventWriter.add(zValue);
//
//        eventWriter.add(eZElement);
//        eventWriter.add(end);
//               
//        // FP end
//        EndElement eElement = eventFactory.createEndElement("", "", fPtype);
//        eventWriter.add(tab);
//        eventWriter.add(eElement);
//        eventWriter.add(end);
//      }
//    
//    private List<FacialPoint> createFPs(String[] arrayFP) throws NumberFormatException {
//        facialPoints = new ArrayList<FacialPoint>();
//        Vector3f[] arrayVertices = new Vector3f[NUM_OF_FP];
//        
//        short j = 1;
//        for(short i = 0; i < NUM_OF_FP; i++){
//            Vector3f vert = new Vector3f();
//            vert.setX(Float.parseFloat((arrayFP[j]).replace(',', '.')));
//            vert.setY(Float.parseFloat((arrayFP[j+1]).replace(',', '.')));
//            vert.setZ(Float.parseFloat((arrayFP[j+2]).replace(',', '.')));
//            arrayVertices[i] = vert;
//            j = +3;
//
//        }
//        
//        facialPoints.add(new FacialPoint(FacialPointType.EX_R, arrayVertices[0]));
//        facialPoints.add(new FacialPoint(FacialPointType.EX_L, arrayVertices[1]));
//        facialPoints.add(new FacialPoint(FacialPointType.EN_R, arrayVertices[2]));
//        facialPoints.add(new FacialPoint(FacialPointType.EN_L, arrayVertices[3]));
//        facialPoints.add(new FacialPoint(FacialPointType.PRN, arrayVertices[4]));
//        facialPoints.add(new FacialPoint(FacialPointType.STO, arrayVertices[5]));
//        facialPoints.add(new FacialPoint(FacialPointType.CH_R, arrayVertices[6]));
//        facialPoints.add(new FacialPoint(FacialPointType.CH_L, arrayVertices[7]));
//
//        return facialPoints;
//    }
//    
//    private Vector3f parsePosition(String x, String y, String z) throws NumberFormatException {
//        Vector3f vert = new Vector3f();
//        vert.setX(Float.parseFloat(x.substring(x.indexOf('>') + 1, x.lastIndexOf('<'))));
//        vert.setY(Float.parseFloat(y.substring(y.indexOf('>') + 1, y.lastIndexOf('<'))));
//        vert.setZ(Float.parseFloat(z.substring(z.indexOf('>') + 1, z.lastIndexOf('<'))));
//        return vert;
//    }
//}
