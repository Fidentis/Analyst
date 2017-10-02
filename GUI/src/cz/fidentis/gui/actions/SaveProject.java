/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import com.jogamp.graph.math.Quaternion;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.composite.CompositeModel;
import cz.fidentis.controller.Project;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import cz.fidentis.controller.Composite;
import cz.fidentis.controller.Comparison2Faces;
import cz.fidentis.controller.OneToManyComparison;
import cz.fidentis.controller.BatchComparison;
import cz.fidentis.controller.data.ColormapConfig;
import cz.fidentis.controller.data.CrosscutConfig;
import cz.fidentis.controller.data.TransparencyConfig;
import cz.fidentis.controller.data.VectorsConfig;
import cz.fidentis.enums.FileExtensions;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.newprojectwizard.ModelFileFilter;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelExporter;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import cz.fidentis.visualisation.procrustes.PApaintingInfo;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3f;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.SaveProject")
@ActionRegistration(
        displayName = "#CTL_SaveProject")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 150),
    @ActionReference(path = "Shortcuts", name = "D-S")
})
@Messages("CTL_SaveProject=Save Project")
public final class SaveProject implements ActionListener {

    private ZipOutputStream zipStream;
    private final byte[] buffer = new byte[1024];
    private File tempFile;

    // TODO: - escaping of text in xml
    //       - maybe deleting of temp file contents
    public void saveProject(ProjectTopComponent topComponent, File saveTo) throws ParserConfigurationException, TransformerException, IOException {
        Project context = topComponent.getProject();
        saveTo.createNewFile();
        FileOutputStream fos = new FileOutputStream(saveTo);
        zipStream = new ZipOutputStream(fos);
        tempFile = new File(FileUtils.instance().getTempDirectoryPath() + File.separator + System.currentTimeMillis());
        tempFile.mkdirs();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document doc = builder.newDocument();
        Element root = doc.createElement("fidentis-project");
        root.setAttribute("name", context.getName());
        doc.appendChild(root);

        if (context.getSelectedComposite() != null) {
            addComposite(root, topComponent);
        }
        if (context.getSelectedComparison2Faces() != null) {
            addComparison2Faces(root, topComponent);
        }
        if (context.getSelectedOneToManyComparison() != null) {
            addOneToManyComparison(root, topComponent);
        }
        if (context.getSelectedBatchComparison() != null) {
            addBatchComparison(root, topComponent);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(doc);
        File xmlFile = new File(tempFile.getAbsolutePath() + File.separator + "project.xml");
        StreamResult result = new StreamResult(xmlFile);

        xmlFile.createNewFile();
        transformer.transform(source, result);

        ZipEntry projectEntry = new ZipEntry(xmlFile.getName());
        zipStream.putNextEntry(projectEntry);
        try (FileInputStream input = new FileInputStream(xmlFile)) {
            int len;
            while ((len = input.read(buffer)) > 0) {
                zipStream.write(buffer, 0, len);
            }
        }
    }

    private Element addComposite(Element root, ProjectTopComponent tc) {
        Composite composite = tc.getProject().getSelectedComposite();
        Document doc = root.getOwnerDocument();
        Element compositeE = doc.createElement("composite");
        root.appendChild(compositeE);

        if (composite.getFaceParts().size() > 0) {
            Element partsE = doc.createElement("faceParts");
            compositeE.appendChild(partsE);

            for (CompositeModel cm : composite.getFaceParts()) {
                Element modelE = doc.createElement("composite-model");
                partsE.appendChild(modelE);

                modelE.setAttribute("part-type", cm.getPart().name());
                modelE.setAttribute("model-path", cm.getModel().getFile().getAbsolutePath());
                appendVector("translation", cm.getTranslation(), modelE);
                appendVector("initShift", cm.getInitialShift(), modelE);
            }
        }

        compositeE.setAttribute("name", composite.getName());
        return compositeE;
    }

    private Element addComparison2Faces(Element root, ProjectTopComponent tc) throws IOException {
        Comparison2Faces comparison = tc.getProject().getSelectedComparison2Faces();
        File auxFile = new File(tempFile.getAbsolutePath() + File.separator + "other");
        auxFile.mkdirs();

        Document doc = root.getOwnerDocument();
        Element comparisonE = doc.createElement("comparison-two-faces");
        root.appendChild(comparisonE);

        comparisonE.setAttribute("name", comparison.getName());

        if (comparison.getHdPaintingInfo() != null) {
            appendHdInfo(comparison.getHdPaintingInfo(), comparisonE);
        }

        if (tc.getViewerPanel_2Faces().getListener2().getPaInfo() != null) {
            appendPaInfo(tc.getViewerPanel_2Faces().getListener2().getPaInfo(), auxFile, comparisonE);
        }

        // mainFace - currently just made from primary model or whatever
        //comparisonE.setAttribute("description", comparison.getDecription());
        if (comparison.getHd() != null) {
            String path = File.separator + "hd";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getHd());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("hd", auxFile.getName() + path);
        }

        if (comparison.getSortedHdValuesRelative() != null) {
            String path = File.separator + "sortedHdRelative";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getSortedHdValuesRelative());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("sortedHdRelative", auxFile.getName() + path);
        }
        if (comparison.getSortedHdValuesAbs() != null) {
            String path = File.separator + "sortedHdAbs";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getSortedHdValuesAbs());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("sortedHdAbs", auxFile.getName() + path);
        }

        if (comparison.getModel1() != null) {
            Element primaryE = doc.createElement("primary-model");
            comparisonE.appendChild(primaryE);
            appendModelElement(comparison.getModel1(), primaryE, "primaryModel");
        }

        if (comparison.getModel2() != null) {
            Element secondaryE = doc.createElement("secondary-model");
            comparisonE.appendChild(secondaryE);
            appendModelElement(comparison.getModel2(), secondaryE, "secondaryModel");
        }

        if (comparison.getCompFTransformations() != null && comparison.getCompFTransformations().size() > 0) {
            Element transE = root.getOwnerDocument().createElement("trans");
            comparisonE.appendChild(transE);

            appendICPTranformationsList(comparison.getCompFTransformations(), transE);
        }
        
        // savig of feature points - a bit complicated but reusing the same format
        // as other project types
        HashMap<String, List<FacialPoint>> fps = new HashMap<>(2);
        HashMap<String, File> modelFiles = new HashMap<>(2);
        if (comparison.getMainFp() != null && comparison.getMainFp().size() > 0) {
            fps.put("main", comparison.getMainFp());
        }
        if (comparison.getSecondaryFp() != null && comparison.getSecondaryFp().size() > 0) {
            fps.put("secondary", comparison.getSecondaryFp());
            //modelFiles.put("secondary", comparison.getModel2().getFile());
        }
        if( fps.size() > 0) {
            appendFeaturePoints(fps, modelFiles, comparisonE, auxFile);
        }

        comparisonE.setAttribute("state", String.valueOf(comparison.getState()));

        comparisonE.setAttribute("showPointInfo", String.valueOf(comparison.isShowPointInfo()));

        comparisonE.setAttribute("pointColor", String.valueOf(comparison.getPointColor().getRGB()));

        comparisonE.setAttribute("fpScaling", String.valueOf(comparison.isFpScaling()));

        comparisonE.setAttribute("useDatabase", String.valueOf(comparison.getUseDatabase()));

        // database files
        comparisonE.setAttribute("fpTreshold", String.valueOf(comparison.getFpTreshold()));

        comparisonE.setAttribute("fpSize", String.valueOf(comparison.getFpSize()));

        comparisonE.setAttribute("icpErrorRate", String.valueOf(comparison.getICPerrorRate()));

        comparisonE.setAttribute("icpMaxIteration", String.valueOf(comparison.getICPmaxIteration()));

        if (comparison.getRegistrationMethod() != null) {
            comparisonE.setAttribute("registrationMethod", comparison.getRegistrationMethod().name());
        }

        if (comparison.getComparisonMethod() != null) {
            comparisonE.setAttribute("comparisonMethod", comparison.getComparisonMethod().name());
        }

        comparisonE.setAttribute("fpDistance", String.valueOf(comparison.getFpDistance()));

        //comparisonE.setAttribute("fpResultSize", String.valueOf(comparison.getFpResultSize()));
        comparisonE.setAttribute("compareButtonEnabled", String.valueOf(comparison.isCompareButtonEnabled()));

        comparisonE.setAttribute("numericalResults", comparison.getNumericalResults());

        comparisonE.setAttribute("scaleEnabled", String.valueOf(comparison.getScaleEnabled()));

        if (comparison.getModelIcon() != null) {
            String path = File.separator + "modelIcon";
            File modelIconFile = new File(auxFile.getAbsolutePath() + path);
            FileUtils.instance().saveArbitraryObject(modelIconFile, comparison.getModelIcon());
            comparisonE.setAttribute("modelIconFile", auxFile.getName() + path);
        }
        if (comparison.getResultIcon() != null) {
            String path = File.separator + "resultIcon";
            File resultIconFile = new File(auxFile.getAbsolutePath() + path);
            FileUtils.instance().saveArbitraryObject(resultIconFile, comparison.getResultIcon());
            comparisonE.setAttribute("resultIconFile", auxFile.getName() + path);
        }

        comparisonE.setAttribute("valuesTypeIndex", String.valueOf(comparison.getValuesTypeIndex()));
        comparisonE.setAttribute("continueComparison", String.valueOf(comparison.isContinueComparison()));

        if (comparison.getTransparencyViz() != null) {
            appendTransparencyData(comparison.getTransparencyViz(), comparisonE);
        }

        if (comparison.getVectorsViz() != null) {
            appendVectorsData(comparison.getVectorsViz(), comparisonE);
        }

        if (comparison.getColormapViz() != null) {
            appendColormapData(comparison.getColormapViz(), comparisonE);
        }

        //comparison result
        if (comparison.getVisualization() != null) {
            comparisonE.setAttribute("visualization", String.valueOf(comparison.getVisualization().name()));
        }

        zipDirectory(auxFile, null);

        return comparisonE;
    }

    private Element addOneToManyComparison(Element root, ProjectTopComponent tc) throws IOException {
        OneToManyComparison comparison = tc.getProject().getSelectedOneToManyComparison();
        File auxFile = new File(tempFile.getAbsolutePath() + File.separator + "other");
        auxFile.mkdirs();

        Document doc = root.getOwnerDocument();
        Element comparisonE = doc.createElement("one-to-many-comparison");
        root.appendChild(comparisonE);

        comparisonE.setAttribute("name", comparison.getName());

        if (comparison.getHdPaintingInfo() != null) {
            appendHdInfo(comparison.getHdPaintingInfo(), comparisonE);
        }

        if (tc.getOneToManyViewerPanel().getListener2().getPaInfo() != null) {
            appendPaInfo(tc.getOneToManyViewerPanel().getListener2().getPaInfo(), auxFile, comparisonE);
        }

        //comparisonE.setAttribute("description", comparison.getDecription());
        if (comparison.getHd() != null) {
            String path = File.separator + "hd";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getHd());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("hd", auxFile.getName() + path);
        }

        if (comparison.getSortedHdRel() != null) {
            String path = File.separator + "sortedHdRelative";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getSortedHdRel());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("sortedHdRelative", auxFile.getName() + path);
        }
        if (comparison.getSortedHdAbs() != null) {
            String path = File.separator + "sortedHdAbs";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getSortedHdAbs());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("sortedHdAbs", auxFile.getName() + path);
        }

        if (comparison.getNumResults() != null) {
            String path = File.separator + "hdNumResults";
            File numResultsFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<ArrayList<Float>> numResults = new ArrayList<>(comparison.getNumResults());
            FileUtils.instance().saveArbitraryObject(numResultsFile, numResults);
            comparisonE.setAttribute("hdNumResults", auxFile.getName() + path);
        }

        if (comparison.getModels().size() > 0) {
            Element modelsE = doc.createElement("compared-models");
            comparisonE.appendChild(modelsE);
            appendModelsList(comparison.getModels(), modelsE);
        }

        if (comparison.getRegisteredModels() != null) {
            Element registeredE = doc.createElement("registered-models");
            comparisonE.appendChild(registeredE);
            appendModelsList(comparison.getRegisteredModels(), registeredE);
        }

        if (comparison.getTrans() != null && comparison.getTrans().size() > 0) {
            appendICPTranformations(comparison.getTrans(), comparisonE);
        }

        if (comparison.getPrimaryModel() != null) {
            Element primaryE = doc.createElement("primary-model");
            comparisonE.appendChild(primaryE);
            appendModelElement(comparison.getPrimaryModel(), primaryE, "primaryModel");
        }


        if (comparison.getFacialPoints() != null && comparison.getFacialPoints().size() > 0) {
            HashMap<String, File> modelsMap = new HashMap<>(comparison.getFacialPoints().size());
            for (String name : comparison.getFacialPoints().keySet()) {
                modelsMap.put(name, comparison.getModel(name));
            }
            appendFeaturePoints(comparison.getFacialPoints(), modelsMap, comparisonE, auxFile);
        }

        comparisonE.setAttribute("state", String.valueOf(comparison.getState()));

        comparisonE.setAttribute("showPointInfo", String.valueOf(comparison.isShowPointInfo()));

        comparisonE.setAttribute("pointColor", String.valueOf(comparison.getPointColor().getRGB()));

        comparisonE.setAttribute("fpScaling", String.valueOf(comparison.isFpScaling()));

        comparisonE.setAttribute("fpTreshold", String.valueOf(comparison.getFpTreshold()));

        comparisonE.setAttribute("fpSize", String.valueOf(comparison.getFpSize()));

        comparisonE.setAttribute("icpErrorRate", String.valueOf(comparison.getICPerrorRate()));

        comparisonE.setAttribute("icpMaxIteration", String.valueOf(comparison.getICPmaxIteration()));

        if (comparison.getRegistrationMethod() != null) {
            comparisonE.setAttribute("registrationMethod", comparison.getRegistrationMethod().name());
        }

        if (comparison.getComparisonMethod() != null) {
            comparisonE.setAttribute("comparisonMethod", comparison.getComparisonMethod().name());
        }

        comparisonE.setAttribute("fpDistance", String.valueOf(comparison.getFpDistance()));

        //comparisonE.setAttribute("fpResultSize", String.valueOf(comparison.getFpResultSize()));
        comparisonE.setAttribute("compareButtonEnabled", String.valueOf(comparison.isCompareButtonEnabled()));

        comparisonE.setAttribute("numericalResults", comparison.getNumericalResults());

        comparisonE.setAttribute("scaleEnabled", String.valueOf(comparison.getScaleEnabled()));

        comparisonE.setAttribute("valuesTypeIndex", String.valueOf(comparison.getValuesTypeIndex()));

        comparisonE.setAttribute("metricTypeIndex", String.valueOf(comparison.getMetricTypeIndex()));

        comparisonE.setAttribute("continueComparison", String.valueOf(comparison.isContinueComparison()));


        if (comparison.getVisualization() != null) {
            comparisonE.setAttribute("visualization", String.valueOf(comparison.getVisualization().name()));
        }

        if (comparison.getCrosscutViz() != null) {
            appendCrosscutData(comparison.getCrosscutViz(), comparisonE);
        }

        if (comparison.getVectorsViz() != null) {
            appendVectorsData(comparison.getVectorsViz(), comparisonE);
        }

        if (comparison.getColormapViz() != null) {
            appendColormapData(comparison.getColormapViz(), comparisonE);
        }

        zipDirectory(auxFile, null);

        return comparisonE;
    }

    private Element addBatchComparison(Element root, ProjectTopComponent tc) throws IOException {
        BatchComparison comparison = tc.getProject().getSelectedBatchComparison();
        File auxFile = new File(tempFile.getAbsolutePath() + File.separator + "other");
        auxFile.mkdirs();

        Document doc = root.getOwnerDocument();
        Element comparisonE = doc.createElement("batch-comparison");
        root.appendChild(comparisonE);

        comparisonE.setAttribute("name", comparison.getName());

        if (comparison.getHDinfo() != null) {
            appendHdInfo(comparison.getHDinfo(), comparisonE);
        }

        if (tc.getViewerPanel_Batch().getListener().getPaInfo() != null) {
            appendPaInfo(tc.getViewerPanel_Batch().getListener().getPaInfo(), auxFile, comparisonE);
        }

        //comparisonE.setAttribute("description", comparison.getDecription());
        if (comparison.getHd() != null) {
            String path = File.separator + "hd";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getHd());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("hd", auxFile.getName() + path);
        }

        if (comparison.getSortedHd() != null) {
            String path = File.separator + "sortedHd";
            File hdFile = new File(auxFile.getAbsolutePath() + path);
            ArrayList<Float> hd = new ArrayList<>(comparison.getSortedHd());
            FileUtils.instance().saveArbitraryObject(hdFile, hd);
            comparisonE.setAttribute("sortedHd", auxFile.getName() + path);
        }

        if (comparison.getRegistrationResults() != null) {
            Element registeredE = doc.createElement("registered-models");
            comparisonE.appendChild(registeredE);
            appendModelsList(comparison.getRegistrationResults(), registeredE);
        }

        /* if (comparison.getHdNumResults() != null) {
         String path = File.separator + "hdNumResults";
         File hdNumResultsFile = new File(auxFile.getAbsolutePath() + path);
         FileUtils.instance().saveArbitraryObject(hdNumResultsFile, comparison.getHdNumResults());
         comparisonE.setAttribute("hdNumResults", auxFile.getName() + path);
         }*/
        if (comparison.getHdCSVresults() != null) {
            comparisonE.setAttribute("csvDirName", comparison.getHdCSVresults().getName());
            zipDirectory(comparison.getHdCSVresults(), null);
        }

        if (comparison.getHdVisualResults() != null) {
            String path = File.separator + "visualResults";
            File visResultsFile = new File(auxFile.getAbsolutePath() + path);
            FileUtils.instance().saveArbitraryObject(visResultsFile, comparison.getHdVisualResults());
            comparisonE.setAttribute("hdVisualResults", auxFile.getName() + path);
        }

        if (comparison.getModels().size() > 0) {
            Element modelsE = doc.createElement("compared-models");
            comparisonE.appendChild(modelsE);
            appendModelsList(comparison.getModels(), modelsE);
        }

        if (comparison.getTrans() != null && comparison.getTrans().size() > 0) {
            appendICPTranformations(comparison.getTrans(), comparisonE);
        }


        if (comparison.getFacialPoints() != null && comparison.getFacialPoints().size() > 0) {
            HashMap<String, File> modelsMap = new HashMap<>(comparison.getFacialPoints().size());
            for (String name : comparison.getFacialPoints().keySet()) {
                modelsMap.put(name, comparison.getModel(name));
            }
            appendFeaturePoints(comparison.getFacialPoints(), modelsMap, comparisonE, auxFile);
        }

        comparisonE.setAttribute("state", String.valueOf(comparison.getState()));

        comparisonE.setAttribute("showPointInfo", String.valueOf(comparison.isShowPointInfo()));

        comparisonE.setAttribute("pointColor", String.valueOf(comparison.getPointColor().getRGB()));

        comparisonE.setAttribute("valuesTypeIndex", String.valueOf(comparison.getValuesTypeIndex()));

        comparisonE.setAttribute("metricTypeIndex", String.valueOf(comparison.getMetricTypeIndex()));

        comparisonE.setAttribute("fpScaling", String.valueOf(comparison.isFpScaling()));

        comparisonE.setAttribute("fpTreshold", String.valueOf(comparison.getFpTreshold()));

        comparisonE.setAttribute("fpSize", String.valueOf(comparison.getFpSize()));

        comparisonE.setAttribute("icpErrorRate", String.valueOf(comparison.getICPerrorRate()));

        comparisonE.setAttribute("icpMaxIteration", String.valueOf(comparison.getICPmaxIteration()));

        comparisonE.setAttribute("icpNumOfHeads", String.valueOf(comparison.getICPnumberOfHeads()));

        comparisonE.setAttribute("templateIndex", String.valueOf(comparison.getTemplateIndex()));

        comparisonE.setAttribute("continueComparison", String.valueOf(comparison.isContinueComparison()));


        if (comparison.getVisualization() != null) {
            comparisonE.setAttribute("visualization", String.valueOf(comparison.getVisualization().name()));
        }

        if (comparison.getCrosscutViz() != null) {
            appendCrosscutData(comparison.getCrosscutViz(), comparisonE);
        }

        if (comparison.getVectorsViz() != null) {
            appendVectorsData(comparison.getVectorsViz(), comparisonE);
        }

        if (comparison.getColormapViz() != null) {
            appendColormapData(comparison.getColormapViz(), comparisonE);
        }

        if (comparison.getRegistrationMethod() != null) {
            comparisonE.setAttribute("registrationMethod", comparison.getRegistrationMethod().name());
        }

        if (comparison.getComparisonMethod() != null) {
            comparisonE.setAttribute("comparisonMethod", comparison.getComparisonMethod().name());
        }

        comparisonE.setAttribute("fpDistance", String.valueOf(comparison.getFpDistance()));

        //comparisonE.setAttribute("fpResultSize", String.valueOf(comparison.getFpResultSize()));
        comparisonE.setAttribute("compareButtonEnabled", String.valueOf(comparison.isCompareButtonEnabled()));

        comparisonE.setAttribute("registerButtonEnabled", String.valueOf(comparison.isRegisterButtonEnabled()));

        //comparisonE.setAttribute("variance", String.valueOf(comparison.getVariance()));
        comparisonE.setAttribute("numericalResults", comparison.getNumericalResults());

        comparisonE.setAttribute("distanceToMean", comparison.getDistanceToMeanConfiguration());

        /*if (comparison.getAuxiliaryResultsFolder() != null) {
         comparisonE.setAttribute("auxiliaryResultsFolder", comparison.getAuxiliaryResultsFolder().getName());
         zipDirectory(comparison.getAuxiliaryResultsFolder(), null);
         }*/
        if (comparison.getAverageFace() != null) {
            Element avgE = doc.createElement("average-face");
            comparisonE.appendChild(avgE);
            appendModelElement(comparison.getAverageFace(), avgE, "avgFace");
        }

        comparisonE.setAttribute("scaleEnabled", String.valueOf(comparison.getScaleEnabled()));

        zipDirectory(auxFile, null);

        return comparisonE;
    }

    /**
     * Appends single ICP transformation as a child of given parent XML element
     * as an XML element named "icp-transformation".
     *
     * @param t - ICP transformation to be appended to parent.
     * @param parent - Parent element of the added transformation element
     * containing the ICP transformation.
     */
    private void appendICPTransformation(ICPTransformation t, Element parent) {
        Element te = parent.getOwnerDocument().createElement("icp-transformation");
        te.setAttribute("scale", String.valueOf(t.getScaleFactor()));
        te.setAttribute("meanD", String.valueOf(t.getMeanD()));
        Quaternion rotation = t.getRotation();
        if (rotation != null) {
            te.setAttribute("qX", String.valueOf(rotation.getX()));
            te.setAttribute("qY", String.valueOf(rotation.getY()));
            te.setAttribute("qZ", String.valueOf(rotation.getZ()));
            te.setAttribute("qW", String.valueOf(rotation.getW()));
        } else {
            te.setAttribute("noRotation", "true");
        }
        Vector3f translation = t.getTranslation();
        if (translation != null) {
            te.setAttribute("tX", String.valueOf(t.getTranslation().getX()));
            te.setAttribute("tY", String.valueOf(t.getTranslation().getY()));
            te.setAttribute("tZ", String.valueOf(t.getTranslation().getZ()));
        } else {
            te.setAttribute("noTranslation", "true");
        }
    }
    
    /**
     * Appends given list of ICP transformations as "transformations-list" element
     * to the parent.
     * @param trans - list of ICP transformations to append to parent element
     * @param parent - parent element of the ICP transformations saved as XML
     */
    private void appendICPTranformationsList(List<ICPTransformation> trans, Element parent) {
        Element transE = parent.getOwnerDocument().createElement("transformations-list");
        parent.appendChild(transE);
        
        for(ICPTransformation t : trans) {
            appendICPTransformation(t, transE);
        }
    }
    
    /**
     * Adds "trans" element to the parent xml element. This element will contain
     * given TCP transformations.
     * @param trans - list of lists of ICP transformations to be saved as chind of given parent element
     * @param parent - parent element of the ICP transformations saved as XML
     */
    private void appendICPTranformations(List<List<ICPTransformation>> trans, Element parent) {
        Element transE = parent.getOwnerDocument().createElement("trans");
        parent.appendChild(transE);
        
        for(List<ICPTransformation> transList : trans) {
            appendICPTranformationsList(transList, transE);
        }
    }
    
    /**
     * Appends the given named facial points to given parent element as a "facial-points"
     * XML element. The XML element contains path to exported facial points that
     * gets zipped with the saved project.
     * @param fps - mapping of feature points to model names
     * @param models - mapping of model files to model names. If facial points have
     * corresponding model file, the model file is used to decentralise exported
     * facial points. Optional parameter.
     * @param parent - parent element of the created "facial-points" element.
     * @param auxFile - the temporary directory of the project to be zipped.
     * @throws IOException if there is a problem with writing feature points to file.
     */
    private void appendFeaturePoints(Map<String, List<FacialPoint>> fps, Map<String, File> models, Element parent, File auxFile) throws IOException {
        // create a node describing how facial points are saved
        Element fpE = parent.getOwnerDocument().createElement("facial-points");
        parent.appendChild(fpE);
        
        // construct a path to file with saved feature points
        String path = File.separator + "featurePoints.csv";
        
        // set the path to file as attribute of xml element
        fpE.setAttribute("file", auxFile.getName() + path);
        
        // prepare the feature points data to be saved
        File fpFile = new File(auxFile.getAbsolutePath() + path);
        ArrayList<FpModel> fpModels = new ArrayList<>(fps.size());
        for (String name : fps.keySet()) {
            FpModel fpmodel = FPImportExport.instance().getFpModelFromFP(fps.get(name), name);
            
            // decentralize to file if possible
            if (models != null && models.get(name) !=  null) {
                fpmodel.decentralizeToFile(models.get(name));
            }
            
            fpModels.add(fpmodel);
        }
        
        // check if the file exists, create it if not
        if (!fpFile.exists()) {
            fpFile.createNewFile();
        }
        
        // save the feature points to prepared file
        CSVparser.save(fpModels, fpFile.getAbsolutePath());
    }

    private void appendModelsList(List<File> models, Element element) throws IOException {
        for (File model : models) {
            appendFileElement(model, element);
        }
    }

    private void appendFileElement(File file, Element parent) throws IOException {
        Element e = null;
        if (file.getAbsolutePath().startsWith(FileUtils.instance().getTempDirectoryPath())) {
            e = parent.getOwnerDocument().createElement("temp-file");
            File modelDir = new File(file.getParent());
            e.setAttribute("name", modelDir.getName() + File.separator + file.getName());
            // save temporary model along with project
            zipDirectory(modelDir, null);
        } else {
            e = parent.getOwnerDocument().createElement("file");
            e.setAttribute("path", file.getAbsolutePath());
        }
        parent.appendChild(e);
    }

    private void appendModelElement(Model model, Element parent, String prefix) throws IOException {
        Element e = parent.getOwnerDocument().createElement("model");
        parent.appendChild(e);
        if (prefix == null || prefix.isEmpty()) {
            prefix = "";
        } else {
            prefix = prefix + File.separator;
        }
        File modelDir = new File(tempFile.getAbsolutePath() + File.separator + prefix + model.getName().substring(0, model.getName().length() - 4));
        e.setAttribute("name", prefix + modelDir.getName() + File.separator + modelDir.getName() + ".obj");

        modelDir.mkdirs();
        ModelExporter exporter = new ModelExporter(model);
        exporter.exportModelToObj(modelDir, true);
        zipDirectory(modelDir, prefix);
    }

    private void appendHdInfo(HDpaintingInfo info, Element parent) {
        Element hdE = parent.getOwnerDocument().createElement("hdInfo");
        parent.appendChild(hdE);
        hdE.setAttribute("treshValue", String.valueOf(info.getMaxThreshValue()));
        hdE.setAttribute("minSelection", String.valueOf(info.getMinSelection()));
        hdE.setAttribute("maxSelection", String.valueOf(info.getMaxSelection()));
        hdE.setAttribute("isSelection", String.valueOf(info.isIsSelection()));
        hdE.setAttribute("isRecomputed", String.valueOf(info.isRecomputed()));
        // selected verts
        hdE.setAttribute("useRelative", String.valueOf(info.isUseRelative()));
        // min/max color - from comparison, vector computed automatically from them
        hdE.setAttribute("viz-type", info.getvType().name());
        hdE.setAttribute("selectionType", info.getsType().name());
        hdE.setAttribute("density", String.valueOf(info.getDensity()));
        // indices for normals
        hdE.setAttribute("recompute", String.valueOf(info.getRecompute()));
    }

    private void appendPaInfo(PApaintingInfo info, File auxFile, Element parent) {
        Element infoE = parent.getOwnerDocument().createElement("paInfo");
        parent.appendChild(infoE);

        infoE.setAttribute("type", String.valueOf(info.getType()));

        File infoFile = new File(auxFile.getAbsolutePath() + File.separator + "paInfo");
        FileUtils.instance().saveArbitraryObject(infoFile, info.getGpa());
        infoE.setAttribute("gpaFile", auxFile.getName() + File.separator + "paInfo");

        infoE.setAttribute("enhance", String.valueOf(info.getEnhance()));
        infoE.setAttribute("pointSize", String.valueOf(info.getPointSize()));

        // pa + pa2
        infoE.setAttribute("selectedPoint", String.valueOf(info.getIndexOfSelectedPoint()));
        infoE.setAttribute("selectedConfig", String.valueOf(info.getIndexOfSelectedConfig()));
        infoE.setAttribute("pointRadius", String.valueOf(info.getFacialPointRadius()));
    }

    private void appendCrosscutData(CrosscutConfig data, Element parent) {
        Element crossE = parent.getOwnerDocument().createElement("crosscutData");
        parent.appendChild(crossE);

        crossE.setAttribute("crossCutPlaneIndex", String.valueOf(data.getCrossCutPlaneIndex()));

        if (data.getArbitraryPlanePos() != null) {
            crossE.setAttribute("arbitraryPlanePosX", String.valueOf(data.getArbitraryPlanePos().x));
            crossE.setAttribute("arbitraryPlanePosY", String.valueOf(data.getArbitraryPlanePos().y));
            crossE.setAttribute("arbitraryPlanePosZ", String.valueOf(data.getArbitraryPlanePos().z));
        }

        if (data.getPlanePosition() != null) {
            crossE.setAttribute("planePosX", String.valueOf(data.getPlanePosition().x));
            crossE.setAttribute("planePosY", String.valueOf(data.getPlanePosition().y));
            crossE.setAttribute("planePosZ", String.valueOf(data.getPlanePosition().z));
        }

        crossE.setAttribute("crosscutSize", String.valueOf(data.getCrosscutSize()));
        crossE.setAttribute("crosscutThickness", String.valueOf(data.getCrosscutThickness()));

        if (data.getCrosscutColor() != null) {
            crossE.setAttribute("crosscutColor", String.valueOf(data.getCrosscutColor().getRGB()));
        }

        crossE.setAttribute("highlightCuts", String.valueOf(data.isHighlightCuts()));
        crossE.setAttribute("showVectors", String.valueOf(data.isShowVector()));
        crossE.setAttribute("allCuts", String.valueOf(data.isAllCuts()));
        crossE.setAttribute("showPlane", String.valueOf(data.isShowPlane()));
        crossE.setAttribute("samplingRays", String.valueOf(data.isSamplingRays()));
    }

    private void appendTransparencyData(TransparencyConfig data, Element parent) {
        Element transE = parent.getOwnerDocument().createElement("transparencyData");
        parent.appendChild(transE);

        //comparison configuration
        if (data.getPrimaryColor() != null) {
            transE.setAttribute("primaryColor", String.valueOf(data.getPrimaryColor().getRGB()));
        }
        if (data.getSecondaryColor() != null) {
            transE.setAttribute("secondaryColor", String.valueOf(data.getSecondaryColor().getRGB()));
        }
        transE.setAttribute("primarySolid", String.valueOf(data.isIsPrimarySolid()));
        transE.setAttribute("secondarySolid", String.valueOf(data.isIsSecondarySolid()));
        if (data.getFogColor() != null) {
            transE.setAttribute("fogColor", String.valueOf(data.getFogColor().getRGB()));
        }
        transE.setAttribute("overlayTransparency", String.valueOf(data.getOverlayTransparency()));
        transE.setAttribute("innerSurfaceSolid", String.valueOf(data.isInnerSurfaceSolid()));
        transE.setAttribute("useGlyphs", String.valueOf(data.isUseGlyphs()));
        transE.setAttribute("useContours", String.valueOf(data.isUseContours()));
        transE.setAttribute("fogVersion", String.valueOf(data.getFogVersion()));
    }

    private void appendVectorsData(VectorsConfig data, Element parent) {
        Element vectorsE = parent.getOwnerDocument().createElement("vectorsData");
        parent.appendChild(vectorsE);

        vectorsE.setAttribute("vectorDensity", String.valueOf(data.getVectorDensity()));
        vectorsE.setAttribute("vectorLength", String.valueOf(data.getVectorLength()));
        vectorsE.setAttribute("cylinderRadius", String.valueOf(data.getCylinderRadius()));
    }

    private void appendColormapData(ColormapConfig data, Element parent) {
        Element colorE = parent.getOwnerDocument().createElement("colormapData");
        parent.appendChild(colorE);

        colorE.setAttribute("haussdorfMaxTreshold", String.valueOf(data.getHausdorfMaxTreshold()));
        colorE.setAttribute("haussdorfMinTreshold", String.valueOf(data.getHausdorfMinTreshold()));
        if (data.getUsedColorScheme() != null) {
            colorE.setAttribute("colorScheme", String.valueOf(data.getUsedColorScheme().name()));
        }
    }

    private Element appendVector(String elemName, Vector3f vector, Element parent) {
        Element el = parent.getOwnerDocument().createElement(elemName);
        parent.appendChild(el);
        el.setAttribute("x", String.valueOf(vector.x));
        el.setAttribute("y", String.valueOf(vector.y));
        el.setAttribute("z", String.valueOf(vector.z));
        return el;
    }

    private void zipDirectory(File directory, String prefix) {
        String pathPrefix = "";
        if (prefix != null && !prefix.isEmpty()) {
            pathPrefix = prefix + File.separator;
        }
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                zipDirectory(f, pathPrefix + directory.getName());
                continue;
            }

            FileInputStream input = null;
            try {
                ZipEntry entry = new ZipEntry(pathPrefix + directory.getName() + File.separator + f.getName());
                zipStream.putNextEntry(entry);
                input = new FileInputStream(f);
                int len;
                while ((len = input.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, len);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new ModelFileFilter(new String[]{"fid"}, "Fidentis project files"));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File selected = chooser.getSelectedFile();
            FileExtensions ext = FileUtils.instance().getFileExtension(selected.getName());
            if (ext == FileExtensions.NONE && !selected.exists()) {
                selected = new File(selected.getAbsolutePath() + ".fid");
            }
            if (selected.exists()) {
                int result = JOptionPane.showConfirmDialog(null, "File " + selected.getName() + " already exists. Overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            final File outFile = selected;
            final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();
            tc.setDisplayName(outFile.getName());
            tc.getProject().setName(outFile.getName());
            GUIController.updateNavigator();

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ProgressHandle p = ProgressHandleFactory.createHandle("Saving project " + tc.getProject().getName());
                    try {
                        p.start();
                        saveProject(tc, outFile);
                    } catch (ParserConfigurationException | TransformerException | IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to save project.");
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (zipStream != null) {
                                zipStream.closeEntry();
                                zipStream.close();
                                zipStream = null;
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        p.finish();
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }

    //debug
    public void closeZip() {
        try {
            if (zipStream != null) {
                zipStream.closeEntry();
                zipStream.close();
                zipStream = null;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
