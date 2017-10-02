/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions;

import com.jogamp.graph.math.Quaternion;
import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.RegistrationMethod;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.comparison.kdTree.KDTreeIndexed;
import cz.fidentis.comparison.procrustes.GPA;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.composite.ModelInfo;
import cz.fidentis.controller.BatchComparison;
import cz.fidentis.controller.Comparison2Faces;
import cz.fidentis.controller.Composite;
import cz.fidentis.controller.Controller;
import cz.fidentis.controller.OneToManyComparison;
import cz.fidentis.controller.Project;
import cz.fidentis.controller.data.ColormapConfig;
import cz.fidentis.controller.data.CrosscutConfig;
import cz.fidentis.controller.data.TransparencyConfig;
import cz.fidentis.controller.data.VectorsConfig;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.gui.ConfigurationTopComponent;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.gui.actions.newprojectwizard.ModelFileFilter;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.gui.guisetup.TwoFacesGUISetup;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.processing.exportProcessing.FPImportExport;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utilsException.FileManipulationException;
import cz.fidentis.visualisation.ColorScheme;
import cz.fidentis.visualisation.procrustes.PApainting;
import cz.fidentis.visualisation.procrustes.PApaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.HDpainting;
import cz.fidentis.visualisation.surfaceComparison.HDpaintingInfo;
import cz.fidentis.visualisation.surfaceComparison.SelectionType;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector3f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.OpenProject")
@ActionRegistration(
        displayName = "#CTL_OpenProject")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 75, separatorAfter = 100),
    @ActionReference(path = "Shortcuts", name = "D-O")
})
@Messages("CTL_OpenProject=Open Project...")
public final class OpenProject implements ActionListener {

    private final byte[] buffer = new byte[1024];
    private File tempFile;
    private List<FpModel> loadedFps;

    public void openProject(File file, final ProjectTopComponent ntc) throws ParserConfigurationException, SAXException, IOException {
        tempFile = new File(FileUtils.instance().getTempDirectoryPath() + File.separator + String.valueOf(System.currentTimeMillis()));
        if (tempFile.exists()) {
            try {
                FileUtils.instance().deleteFolder(tempFile);
            } catch (FileManipulationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        tempFile.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String fileName = entry.getName();
            File newFile = new File(tempFile.getAbsolutePath() + File.separator + fileName);

            new File(newFile.getParent()).mkdirs();
            FileOutputStream output = new FileOutputStream(newFile);
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            output.close();
            entry = zipIn.getNextEntry();
        }
        zipIn.closeEntry();
        zipIn.close();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();

        File projectFile = new File(tempFile.getAbsolutePath() + File.separator + "project.xml");
        Document doc = builder.parse(projectFile);
        doc.normalize();

        Element root = doc.getDocumentElement();
        final Project p = new Project(root.getAttribute("name"));
        p.setTempDirectory(tempFile);
        ntc.setProject(p);
        
        NodeList topNodes = root.getChildNodes();
        for (int i = 0; i < topNodes.getLength(); i++) {
            Node n = topNodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                switch (e.getTagName()) {
                    case "composite":
                        p.addComposite(NbBundle.getMessage(Controller.class, "tree.node.composite"));
                        createComposite(e, ntc);
                        break;
                    case "comparison-two-faces":
                        p.add2FacesComparison(NbBundle.getMessage(Controller.class, "tree.node.twoFacesComparison"));
                        createComparison2Faces(e, ntc);
                        break;
                    case "one-to-many-comparison":
                        p.addOneToManyComparison(NbBundle.getMessage(Controller.class, "tree.node.oneToMany"));
                        createOneToManyComparison(e, ntc);
                        break;
                    case "batch-comparison":
                        p.addBatchComparison(NbBundle.getMessage(Controller.class, "tree.node.batchComparison"));
                        createBatchComparison(e, ntc);
                        break;
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateGui(ntc, p);
            }
        });
    }
    
    private void updateGui(ProjectTopComponent ntc, Project p) {
        GUIController.getBlankProject(); // create panel for new project

        ntc.setProject(p);
        ntc.setName(String.valueOf(GUIController.getProjects().size()));
        ntc.setDisplayName(p.getName());
        p.setIndex(GUIController.getProjects().size());
        Controller.addProjcet(p);
        ntc.open();
        GUIController.setSelectedProjectTopComponent(ntc);

        ConfigurationTopComponent ctc = GUIController.getConfigurationTopComponent();
        
        boolean isMissingModel = false;
        List<File> modelsList = null;
        if(p.getSelectedOneToManyComparison() != null) {
            modelsList = p.getSelectedOneToManyComparison().getModels();
        }
        if(p.getSelectedBatchComparison() != null) {
            modelsList = p.getSelectedBatchComparison().getModels();
        }
        if(modelsList != null) {
            for(File modelFile : modelsList) {
                if(!modelFile.isFile()) {
                    isMissingModel = true;
                    break;
                }
            }
        }
        if(isMissingModel == true) {
            ResolveMissingModels dialog = new ResolveMissingModels(null, true);
            dialog.setLocationRelativeTo(ntc);
            dialog.setFiles(modelsList);
            dialog.setVisible(true);
            if(dialog.getResult() != null) {
                // remove models for the sake of keeping the right project tree
                int filesCount = modelsList.size();
                for (int i = filesCount - 1; i >= 0; i--) {
                    if (p.getSelectedOneToManyComparison() != null) {
                        p.getSelectedOneToManyComparison().removeModel(i);
                    } else {
                        p.getSelectedBatchComparison().removeModel(i);
                    }
                }

                for(File origFile : dialog.getResult().keySet()) {
                    if (p.getSelectedOneToManyComparison() != null) {
                        p.getSelectedOneToManyComparison().addModel(dialog.getResult().get(origFile));
                    } else {
                        p.getSelectedBatchComparison().addModel(dialog.getResult().get(origFile));
                    }
                }
            }
        }

        if (p.getSelectedComposite() != null) {
            ntc.showComposite();
            ButtonHelper.setCompositeEnabled(true);
            ButtonHelper.setViewerEnabled(true);
            ButtonHelper.setTexturesEnabled(true);
            ButtonHelper.getTexturesMenuItem().setSelected(true);
            ntc.getCompositePanel().setCompositeData(GUIController.getSelectedProjectTopComponent().getProject().getSelectedComposite());
            ntc.getCompositePanel().selectTemplates();
            ntc.getProject().setSelectedPart(1);

            ntc.setTextureRendering(ButtonHelper.getTexturesMenuItem().isSelected());
            GUIController.selectComposite();

        }
        if (p.getSelectedComparison2Faces() != null) {
            ntc.show2FacesViewer();
            Comparison2Faces comparison2f = p.getSelectedComparison2Faces();
            p.setSelectedPart(2);
            if (loadedFps != null) {
                for(FpModel m : loadedFps) {
                    if (m.getModelName().equals("main")) {
                        comparison2f.setMainFp(m.createListFp());
                        ntc.getViewerPanel_2Faces().getListener1().setFacialPoints(comparison2f.getMainFp());
                    }
                    if (m.getModelName().equals("secondary")) {
                        comparison2f.setSecondaryFp(m.createListFp());
                        ntc.getViewerPanel_2Faces().getListener2().setFacialPoints(comparison2f.getSecondaryFp());
                    }
                }
            }
            if (comparison2f.getModel1() != null) {
                ntc.getViewerPanel_2Faces().getCanvas1().setImportLabelVisible(false);
                ntc.getViewerPanel_2Faces().getListener1().setModels(comparison2f.getModel1());
            }
            if (comparison2f.getModel2() != null) {
                ntc.getViewerPanel_2Faces().getCanvas2().setImportLabelVisible(false);
                ntc.getViewerPanel_2Faces().getListener2().setModels(comparison2f.getModel2());
                if (comparison2f.getResultIcon() != null) {
                    ntc.getViewerPanel_2Faces().setResultButtonVisible(true, 0);
                    ntc.getViewerPanel_2Faces().getCanvas1().showResultIcon();
                }
            }
            if (comparison2f.getState() >= 3) {
                if (comparison2f.getResultIcon() != null) {
                    ntc.getViewerPanel_2Faces().setResultButtonVisible(false, 0);
                    ntc.getViewerPanel_2Faces().getCanvas1().showResultIcon();
                }

                if (comparison2f.getComparisonMethod() == ComparisonMethod.PROCRUSTES) {
                    ntc.getViewerPanel_2Faces().getListener1().setProcrustes(true);
                } else {
                    ntc.getViewerPanel_2Faces().getListener1().setModels(comparison2f.getHdPaintingInfo().getModel());
                    ntc.getViewerPanel_2Faces().getListener1().setHdInfo(comparison2f.getHdPaintingInfo());
                    ntc.getViewerPanel_2Faces().getListener1().setHdPaint(comparison2f.getHDP());
                    ntc.getViewerPanel_2Faces().getListener1().setPaintHD(true);
                    ntc.getViewerPanel_2Faces().getListener1().drawHD(true);
                }
            }
            ntc.show2FacesViewer();
        }
        if (p.getSelectedOneToManyComparison() != null) {
            ntc.show1toNViewer();
            OneToManyComparison comparison1N = p.getSelectedOneToManyComparison();
            p.setSelectedPart(3);
            if (comparison1N.getPrimaryModel() != null) {
                ntc.getOneToManyViewerPanel().getCanvas1().setImportLabelVisible(false);
                ntc.getOneToManyViewerPanel().getListener1().setModels(comparison1N.getPrimaryModel());
            }
            if (!comparison1N.getModels().isEmpty()) {
                ntc.getOneToManyViewerPanel().getCanvas2().setImportLabelVisible(false);
            }
            if (loadedFps != null) {
                List<File> allModelFiles = new ArrayList<>();
                allModelFiles.addAll(comparison1N.getModels());
                if (comparison1N.getPrimaryModel() != null) {
                    allModelFiles.add(comparison1N.getPrimaryModel().getFile());
                }
                FPImportExport.instance().alignPointsToModels(loadedFps, allModelFiles);
                for (FpModel model : loadedFps) {
                    if (comparison1N.getPrimaryModel() != null
                            && model.getModelName().equals(comparison1N.getPrimaryModel().getName())) {
                        ntc.getOneToManyViewerPanel().getListener1().setFacialPoints(model.getFacialPoints());
                        ntc.getOneToManyViewerPanel().getListener1().initFpUniverse(model.getFacialPoints());

                    }
                    comparison1N.addFacialPoints(model.getModelName(), model.getFacialPoints());
                }
            }
            if (comparison1N.getState() >= 3) {
                if (comparison1N.getComparisonMethod() == ComparisonMethod.PROCRUSTES) {
                    ntc.getOneToManyViewerPanel().getListener1().setProcrustes(true);
                } else {
                    ntc.getOneToManyViewerPanel().getListener1().setModels(comparison1N.getHdPaintingInfo().getModel());
                    ntc.getOneToManyViewerPanel().getListener1().setHdInfo(comparison1N.getHdPaintingInfo());
                    ntc.getOneToManyViewerPanel().getListener1().setHdPaint(comparison1N.getHDP());
                    ntc.getOneToManyViewerPanel().getListener1().setPaintHD(true);
                    ntc.getOneToManyViewerPanel().getListener1().drawHD(true);
                }
            }
            ntc.show1toNViewer();
        }
        if (p.getSelectedBatchComparison() != null) {
            ntc.showBatchViewer();
            BatchComparison comparison = p.getSelectedBatchComparison();
            p.setSelectedPart(4);
            if (!comparison.getModels().isEmpty()) {
                ntc.getViewerPanel_Batch().getCanvas1().setImportLabelVisible(false);
                //TODO nacitaj model do listenera
            }
            if (loadedFps != null) {
                FPImportExport.instance().alignPointsToModels(loadedFps, comparison.getModels());
                for (FpModel model : loadedFps) {
                    comparison.addFacialPoints(model.getModelName(), model.getFacialPoints());
                }
            }
            if (comparison.getState() >= 3) {
                if (comparison.getComparisonMethod() == ComparisonMethod.PROCRUSTES) {
                    ntc.getViewerPanel_Batch().getListener().setProcrustes(true);
                } else {
                    ntc.getViewerPanel_Batch().getListener().setModels(comparison.getHDinfo().getModel());
                    ntc.getViewerPanel_Batch().getListener().setHdInfo(comparison.getHDinfo());
                    ntc.getViewerPanel_Batch().getListener().setHdPaint(comparison.getHDP());
                    ntc.getViewerPanel_Batch().getListener().setPaintHD(true);
                    ntc.getViewerPanel_Batch().getListener().drawHD(true);
                }
            }
            ntc.showBatchViewer();
        }
        ntc.setTextureRendering(ButtonHelper.getTexturesMenuItem().isSelected());
        GUIController.updateNavigator();
        ntc.requestActive();
    }

    private void createComposite(Element projectE, ProjectTopComponent tc) {
        Composite composite = tc.getProject().getSelectedComposite();

        NodeList parts = projectE.getElementsByTagName("composite-model");
        for (int i = 0; i < parts.getLength(); i++) {
            Element partE = (Element) parts.item(i);
            File path = new File(partE.getAttribute("model-path"));
            ModelInfo info = tc.getCompositePanel().loadModel(path);
            FacePartType type = FacePartType.valueOf(partE.getAttribute("part-type"));
            Element e = (Element) partE.getElementsByTagName("translation").item(0);
            Vector3f translation = parseVector(e);
            e = (Element) partE.getElementsByTagName("initShift").item(0);
            Vector3f shift = parseVector(e);
            composite.addModel(path, type, translation, shift);
        }
    }

    private void createComparison2Faces(Element projectE, ProjectTopComponent tc) {
        Comparison2Faces comparison = tc.getProject().getSelectedComparison2Faces();
        
        
        TwoFacesGUISetup.setUpDefaultRegistrationData(comparison);
        TwoFacesGUISetup.setUpDefaultComparisonConfigurationData(comparison);
        TwoFacesGUISetup.setUpComparisonResultDefaultData(comparison);
        
        NodeList children = projectE.getChildNodes();
        Element primaryE = null;
        Element secondaryE = null;
        Element fpE = null;
        Element hdInfoE = null;
        Element paInfoE = null;
        Element transE = null;
        Element transpE = null;
        Element colorE = null;
        Element vectorsE = null;
        
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) n;
            switch (e.getTagName()) {
                case "primary-model":
                    primaryE = e;
                    break;
                case "secondary-model":
                    secondaryE = e;
                    break;
                case "facial-points":
                    fpE = e;
                    break;
                case "hdInfo":
                    hdInfoE = e;
                    break;
                case "paInfo":
                    paInfoE = e;
                    break;
                case "trans":
                    transE = e;
                    break;
                case "transparencyData":
                    transpE = e;
                    break;
                case "vectorsData":
                    vectorsE = e;
                    break;
                case "colormapData":
                    colorE = e;
                    break;
            }
        }

        comparison.setName(projectE.getAttribute("name"));

        String attr = projectE.getAttribute("hd");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setHd((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("sortedHdRelative");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setSortedHdValuesRelative((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("sortedHdAbs");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setSortedHdValuesAbs((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("showPointInfo");
        if (attr != null && !attr.isEmpty()) {
            comparison.setShowPointInfo(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("pointColor");
        if (attr != null && !attr.isEmpty()) {
            comparison.setPointColor(new Color(Integer.parseInt(attr)));
        }
       
        attr = projectE.getAttribute("fpScaling");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpScaling(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("useDatabase");
        if (attr != null && !attr.isEmpty()) {
            comparison.setUseDatabase(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("fpTreshold");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpTreshold(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("fpSize");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpSize(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("icpErrorRate");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPerrorRate(Float.parseFloat(attr));
        }
        attr = projectE.getAttribute("icpMaxIteration");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPmaxIteration(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("registrationMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setRegistrationMethod(RegistrationMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("comparisonMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setComparisonMethod(ComparisonMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("fpDistance");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpDistance(Integer.parseInt(attr));
        }
        
        attr = projectE.getAttribute("compareButtonEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setCompareButtonEnabled(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("numericalResults");
        if (attr != null && !attr.isEmpty()) {
            comparison.setNumericalResults(attr);
        }
        attr = projectE.getAttribute("scaleEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setScaleEnabled(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("modelIconFile");
        if (attr != null && !attr.isEmpty()) {
            File f = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setModelIcon((ImageIcon) FileUtils.instance().loadArbitraryObject(f));
        }
        attr = projectE.getAttribute("resultIconFile");
        if (attr != null && !attr.isEmpty()) {
            File f = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setResultIcon((ImageIcon) FileUtils.instance().loadArbitraryObject(f));
        }
        attr = projectE.getAttribute("valuesTypeIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setValuesTypeIndex(Integer.parseInt(attr));
        }
        
        attr = projectE.getAttribute("continueComparison");
        if(attr != null && !attr.isEmpty()){
            comparison.setContinueComparison(Boolean.parseBoolean(attr));
        }
        
        attr = projectE.getAttribute("visualization");
        if(attr != null && !attr.isEmpty()){
            comparison.setVisualization(VisualizationType.valueOf(attr));
        }
        

        if (primaryE != null) {
            Element modelE = (Element) primaryE.getElementsByTagName("model").item(0);
            File modelFile = new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name"));
            
            Model model = ModelLoader.instance().loadModel(modelFile, true, true);
            comparison.setModel1(model);
            comparison.setMainFace(new KDTreeIndexed(model.getVerts()));
        }

        if (secondaryE != null) {
            Element modelE = (Element) secondaryE.getElementsByTagName("model").item(0);
            File modelFile = new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name"));
            
            Model model = ModelLoader.instance().loadModel(modelFile, true, true);
            comparison.setModel2(model);
        }
        
        if (fpE != null) {
            String fileName = tempFile.getAbsolutePath() + File.separator + fpE.getAttribute("file");
            loadedFps = CSVparser.load(fileName);
        }

        if (transE != null) {
            List<List<ICPTransformation>> trans = parseICPTransformations(transE);
            if (trans.size() == 1) {
                comparison.setCompFTransformations(trans.get(0));
            }
        }

        if (hdInfoE != null) {
            boolean useRelative = Boolean.parseBoolean(hdInfoE.getAttribute("useRelative"));
            HDpaintingInfo info = parseHdInfo(hdInfoE, useRelative, comparison.getHd(), comparison.getModel1());

            comparison.setHdPaintingInfo(info);
            comparison.setHDP(new HDpainting(info));
        }

        if (paInfoE != null) {
            PApaintingInfo info = parsePaInfo(paInfoE);
            tc.getViewerPanel_Batch().getListener().setPaInfo(info);

            PApainting painting = new PApainting(info);
            tc.getViewerPanel_Batch().getListener().setPaPainting(painting);
        }
        
        if(transpE != null){
            TransparencyConfig data = parseTransparencyData(transpE);
            comparison.setTransparencyViz(data);
        }
        
        if(colorE != null){
            ColormapConfig data = parseColormapData(colorE);
            comparison.setColormapViz(data);
        }
        
        if(vectorsE != null){
            VectorsConfig data = parseVectorsData(vectorsE);
            comparison.setVectorsViz(data);
        }

        attr = projectE.getAttribute("state");
        if (attr != null && !attr.isEmpty()) {
            comparison.setState(Integer.parseInt(attr));
        }
    }

    private void createOneToManyComparison(Element projectE, ProjectTopComponent tc) {
        OneToManyComparison comparison = tc.getProject().getSelectedOneToManyComparison();
        NodeList children = projectE.getChildNodes();
        Element registeredE = null;
        Element modelsE = null;
        Element primaryE = null;
        Element fpE = null;
        Element icpTransE = null;
        Element hdInfoE = null;
        Element paInfoE = null;
        Element crossE = null;
        Element colorE = null;
        Element vectorsE = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) n;
            switch (e.getTagName()) {
                case "compared-models":
                    modelsE = e;
                    break;
                case "primary-model":
                    primaryE = e;
                    break;
                case "registered-models":
                    registeredE = e;
                    break;
                case "trans":
                    icpTransE = e;
                    break;
                case "facial-points":
                    fpE = e;
                    break;
                case "hdInfo":
                    hdInfoE = e;
                    break;
                case "paInfo":
                    paInfoE = e;
                    break;
                case "colormapData":
                    colorE = e;
                    break;
                case "vectorsData":
                    vectorsE = e;
                    break;
                case "crosscutData":
                    crossE = e;
                    break;
            }
        }

        comparison.setName(projectE.getAttribute("name"));


        String attr = projectE.getAttribute("hd");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setHd((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("sortedHdRelative");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setSortedHdRel((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("sortedHdAbs");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setSortedHdAbs((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("hdNumResults");
        if (attr != null && !attr.isEmpty()) {
            File numResultsFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setNumResults((ArrayList<ArrayList<Float>>) FileUtils.instance().loadArbitraryObject(numResultsFile));
        }
        attr = projectE.getAttribute("showPointInfo");
        if (attr != null && !attr.isEmpty()) {
            comparison.setShowPointInfo(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("pointColor");
        if (attr != null && !attr.isEmpty()) {
            comparison.setPointColor(new Color(Integer.parseInt(attr)));
        }

        
        attr = projectE.getAttribute("fpScaling");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpScaling(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("fpTreshold");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpTreshold(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("fpSize");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpSize(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("icpErrorRate");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPerrorRate(Float.parseFloat(attr));
        }
        attr = projectE.getAttribute("icpMaxIteration");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPmaxIteration(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("registrationMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setRegistrationMethod(RegistrationMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("comparisonMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setComparisonMethod(ComparisonMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("fpDistance");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpDistance(Integer.parseInt(attr));
        }
 
        attr = projectE.getAttribute("compareButtonEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setCompareButtonEnabled(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("numericalResults");
        if (attr != null && !attr.isEmpty()) {
            comparison.setNumericalResults(attr);
        }
        attr = projectE.getAttribute("scaleEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setScaleEnabled(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("valuesTypeIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setValuesTypeIndex(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("metricTypeIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setMetricTypeIndex(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("continueComparison");
        if (attr != null && !attr.isEmpty()) {
            comparison.setContinueComparison(Boolean.parseBoolean(attr));
        }
        
        attr = projectE.getAttribute("visualization");
        if (attr != null && !attr.isEmpty()) {
            comparison.setVisualization(VisualizationType.valueOf(attr));
        }
        

        if (primaryE != null) {
            Element modelE = (Element) primaryE.getElementsByTagName("model").item(0);
            
            File modelFile = new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name"));
            comparison.setPrimaryModel(ModelLoader.instance().loadModel(modelFile, true, true));
        }

        if (modelsE != null) {
            ArrayList<File> files = parseModelsList(modelsE);
            for (File f : files) {
                comparison.addModel(f);
            }
        }

        if (fpE != null) {
            String fileName = tempFile.getAbsolutePath() + File.separator + fpE.getAttribute("file");
            loadedFps = CSVparser.load(fileName);
        }

        if (icpTransE != null) {
            List<List<ICPTransformation>> trans = parseICPTransformations(icpTransE);
            comparison.setTrans(trans);
        }

        if (registeredE != null) {
            comparison.setRegisteredModels(parseModelsList(registeredE));
        }

        if (hdInfoE != null) {
            boolean isRelative = comparison.getValuesTypeIndex() == 0;
            /*ModelLoader modelLoader = new ModelLoader();
            File modelFile = null;
            if (comparison.getRegisteredModels() != null) {
                modelFile = comparison.getRegisteredModels().get(comparison.getTemplateIndex());
            } else {
                modelFile = comparison.getModel(comparison.getTemplateIndex());
            }*/
            HDpaintingInfo info = parseHdInfo(hdInfoE, isRelative, comparison.getHd(), comparison.getPrimaryModel());
            comparison.setHdPaintingInfo(info);
            comparison.setHDP(new HDpainting(info));
        }

        if (paInfoE != null) {
            PApaintingInfo info = parsePaInfo(paInfoE);
            tc.getOneToManyViewerPanel().getListener2().setPaInfo(info);

            PApainting paint = new PApainting(info);
            tc.getOneToManyViewerPanel().getListener2().setPaPainting(paint);
        }
        
        if(colorE != null){
            ColormapConfig data = parseColormapData(colorE);
            comparison.setColormapViz(data);
        }
        
        if(vectorsE != null){
            VectorsConfig data = parseVectorsData(vectorsE);
            comparison.setVectorsViz(data);
        }
        
        if(crossE != null){
            CrosscutConfig data = parseCrosscutData(crossE);
            comparison.setCrosscutViz(data);
        }

        attr = projectE.getAttribute("state");
        if (attr != null && !attr.isEmpty()) {
            comparison.setState(Integer.parseInt(attr));
        }
    }

    private void createBatchComparison(Element projectE, ProjectTopComponent tc) {
        BatchComparison comparison = tc.getProject().getSelectedBatchComparison();
        NodeList children = projectE.getChildNodes();
        Element registeredE = null;
        Element modelsE = null;
        Element averageE = null;
        Element fpE = null;
        Element icpTransE = null;
        Element hdInfoE = null;
        Element paInfoE = null;
        Element crossE = null;
        Element vectorsE = null;
        Element colorE = null;
        
        
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) n;
            switch (e.getTagName()) {
                case "compared-models":
                    modelsE = e;
                    break;
                case "registered-models":
                    registeredE = e;
                    break;
                case "average-face":
                    averageE = e;
                    break;
                case "trans":
                    icpTransE = e;
                    break;
                case "facial-points":
                    fpE = e;
                    break;
                case "hdInfo":
                    hdInfoE = e;
                    break;
                case "paInfo":
                    paInfoE = e;
                    break;
                case "crosscutData":
                    crossE = e;
                    break;
                case "vectorsData":
                    vectorsE = e;
                    break;
                case "colormapData":
                    colorE = e;
                    break;
            }
        }

        comparison.setName(projectE.getAttribute("name"));

        String attr = projectE.getAttribute("hd");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setHd((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("sortedHd");
        if (attr != null && !attr.isEmpty()) {
            File hdFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setSortedHd((ArrayList<Float>) FileUtils.instance().loadArbitraryObject(hdFile));
        }
        attr = projectE.getAttribute("hdNumResults");       //??
        if (attr != null && !attr.isEmpty()) {
            File numResultsFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
           // comparison.setHdNumResults((ArrayList<ArrayList<ArrayList<Float>>>) FileUtils.instance().loadArbitraryObject(numResultsFile));
        }
        attr = projectE.getAttribute("csvDirName");
        if (attr != null && !attr.isEmpty()) {
            comparison.setHdCSVresults(new File(tempFile.getAbsolutePath() + File.separator + attr));
        }
        attr = projectE.getAttribute("hdVisualResults");
        if (attr != null && !attr.isEmpty()) {
            File visualResultsFile = new File(tempFile.getAbsolutePath() + File.separator + attr);
            comparison.setHdVisualResults((ArrayList<ArrayList<Float>>) FileUtils.instance().loadArbitraryObject(visualResultsFile));
        }
        attr = projectE.getAttribute("showPointInfo");
        if (attr != null && !attr.isEmpty()) {
            comparison.setShowPointInfo(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("pointColor");
        if (attr != null && !attr.isEmpty()) {
            comparison.setPointColor(new Color(Integer.parseInt(attr)));
        }
        attr = projectE.getAttribute("valuesTypeIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setValuesTypeIndex(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("metricTypeIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setMetricTypeIndex(Integer.parseInt(attr));
        }
       
        attr = projectE.getAttribute("fpScaling");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpScaling(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("fpTreshold");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpTreshold(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("fpSize");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpSize(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("icpErrorRate");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPerrorRate(Float.parseFloat(attr));
        }
        attr = projectE.getAttribute("icpMaxIteration");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPmaxIteration(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("icpNumOfHeads");
        if (attr != null && !attr.isEmpty()) {
            comparison.setICPnumberOfHeads(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("templateIndex");
        if (attr != null && !attr.isEmpty()) {
            comparison.setTemplateIndex(Integer.parseInt(attr));
        }
        attr = projectE.getAttribute("registrationMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setRegistrationMethod(RegistrationMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("comparisonMethod");
        if (attr != null && !attr.isEmpty()) {
            comparison.setComparisonMethod(ComparisonMethod.valueOf(attr));
        }
        attr = projectE.getAttribute("fpDistance");
        if (attr != null && !attr.isEmpty()) {
            comparison.setFpDistance(Integer.parseInt(attr));
        }

        attr = projectE.getAttribute("compareButtonEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setCompareButtonEnabled(Boolean.parseBoolean(attr));
        }
        attr = projectE.getAttribute("registerButtonEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setRegisterButtonEnabled(Boolean.parseBoolean(attr));
        }
        
        attr = projectE.getAttribute("numericalResults");
        if (attr != null && !attr.isEmpty()) {
            comparison.setNumericalResults(attr);
        }
        attr = projectE.getAttribute("distanceToMean");
        if (attr != null && !attr.isEmpty()) {
            comparison.setDistanceToMeanConfiguration(attr);
        }
        attr = projectE.getAttribute("auxiliaryResultsFolder");     //??
        if (attr != null && !attr.isEmpty()) {
            //comparison.setAuxiliaryResultsFolder(new File(tempFile.getAbsolutePath() + File.separator + attr));
        }
        attr = projectE.getAttribute("scaleEnabled");
        if (attr != null && !attr.isEmpty()) {
            comparison.setScaleEnabled(Boolean.parseBoolean(attr));
        }
        
        attr = projectE.getAttribute("continueComparison");
        if(attr != null && !attr.isEmpty()){
            comparison.setContinueComparison(Boolean.parseBoolean(attr));
        }
        
        attr = projectE.getAttribute("visualization");
        if (attr != null && !attr.isEmpty()) {
            comparison.setVisualization(VisualizationType.valueOf(attr));
        }
             
               

        if (modelsE != null) {
            ArrayList<File> files = parseModelsList(modelsE);
            for (File f : files) {
                comparison.addModel(f);
            }
        }

        if (fpE != null) {
            String fileName = tempFile.getAbsolutePath() + File.separator + fpE.getAttribute("file");
            loadedFps = CSVparser.load(fileName);
        }

        if (icpTransE != null) {
            List<List<ICPTransformation>> transforms = parseICPTransformations(icpTransE);
            comparison.setTrans(transforms);
        }

        if (registeredE != null) {
            comparison.setRegistrationResults(parseModelsList(registeredE));
        }

        /*if (averageRegE != null) {
            Element modelE = (Element) averageRegE.getElementsByTagName("model").item(0);
            ModelLoader loader = new ModelLoader();
            File modelFile = new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name"));
            Model m = loader.loadModel(modelFile, true, true);
            comparison.setAverageFace(m);
        }*/

        if (averageE != null) {
            Element modelE = (Element) averageE.getElementsByTagName("model").item(0);
            
            File modelFile = new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name"));
            Model m = ModelLoader.instance().loadModel(modelFile, true, true);
            comparison.setAverageFace(m);
        }

        if (hdInfoE != null) {
            boolean useRelative = Boolean.parseBoolean(hdInfoE.getAttribute("useRelative"));
            HDpaintingInfo info = parseHdInfo(hdInfoE, useRelative, comparison.getHd(), comparison.getAverageFace());

            comparison.setHDinfo(info);
            comparison.setHDP(new HDpainting(info));
        }

        if (paInfoE != null) {
            PApaintingInfo info = parsePaInfo(paInfoE);
            tc.getViewerPanel_Batch().getListener().setPaInfo(info);

            PApainting painting = new PApainting(info);
            tc.getViewerPanel_Batch().getListener().setPaPainting(painting);
        }
        
        if(crossE != null){
            CrosscutConfig data = parseCrosscutData(crossE);
            comparison.setCrosscutViz(data);
        }
        
        if(colorE != null){
            ColormapConfig data = parseColormapData(colorE);
            comparison.setColormapViz(data);
        }
        
        if(vectorsE != null){
            VectorsConfig data = parseVectorsData(vectorsE);
            comparison.setVectorsViz(data);
        }

        attr = projectE.getAttribute("state");
        if (attr != null && !attr.isEmpty()) {
            comparison.setState(Integer.parseInt(attr));
        }
    }

    private ArrayList<File> parseModelsList(Element modelsE) {
        NodeList nodes = modelsE.getChildNodes();
        ArrayList<File> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element modelE = (Element) n;
            switch (modelE.getTagName()) {
                case "file":
                    list.add(new File(modelE.getAttribute("path")));
                    break;
                case "temp-file":
                    list.add(new File(tempFile.getAbsolutePath() + File.separator + modelE.getAttribute("name")));
                    break;
            }
        }
        return list;
    }

    private Vector3f parseVector(Element vecE) {
        float x = Float.parseFloat(vecE.getAttribute("x"));
        float y = Float.parseFloat(vecE.getAttribute("y"));
        float z = Float.parseFloat(vecE.getAttribute("z"));
        return new Vector3f(x, y, z);
    }

    private Object loadArbitraryObject(File loadFile) {
        if (loadFile.isDirectory() || !loadFile.canRead()) {
            throw new IllegalArgumentException("Source file must be readable file.");
        }

        Object result = null;
        try (FileInputStream fs = new FileInputStream(loadFile);
                ObjectInputStream input = new ObjectInputStream(fs);) {

            result = input.readObject();
        } catch (IOException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Could not load arbitrary object.", e);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Cannot find class of loaded object.", e);
        }
        return result;
    }

    private List<List<ICPTransformation>> parseICPTransformations(Element transE) {
        NodeList c = transE.getElementsByTagName("transformations-list");
        ArrayList<List<ICPTransformation>> transforms = new ArrayList<>(c.getLength());

        for (int i = 0; i < c.getLength(); i++) {
            Element transList = (Element) c.item(i);
            List<ICPTransformation> trans = parseICPTransformationsList(transList);
            transforms.add(trans);
        }

        return transforms;
    }

    private List<ICPTransformation> parseICPTransformationsList(Element transListE) {
        NodeList c = transListE.getElementsByTagName("icp-transformation");
        ArrayList<ICPTransformation> transforms = new ArrayList<>(c.getLength());

        for (int i = 0; i < c.getLength(); i++) {
            Element t = (Element) c.item(i);

            transforms.add(parseICPTransformation(t));
        }
        
        return transforms;
    }

    private ICPTransformation parseICPTransformation(Element t) {
        float scale = Float.parseFloat(t.getAttribute("scale"));
        float meanD = Float.parseFloat(t.getAttribute("meanD"));

        Vector3f translation = null;
        if (!t.hasAttribute("noTranslation")) {
            float tx = Float.parseFloat(t.getAttribute("tX"));
            float ty = Float.parseFloat(t.getAttribute("tY"));
            float tz = Float.parseFloat(t.getAttribute("tZ"));
            translation = new Vector3f(tx, ty, tz);
        }

        Quaternion q = null;
        if (!t.hasAttribute("noRotation")) {
            float qx = Float.parseFloat(t.getAttribute("qX"));
            float qy = Float.parseFloat(t.getAttribute("qY"));
            float qz = Float.parseFloat(t.getAttribute("qZ"));
            float qw = Float.parseFloat(t.getAttribute("qW"));
            q = new Quaternion(qw, qx, qy, qz);
        }

        return new ICPTransformation(translation, scale, q, meanD, null);
    }

    private HDpaintingInfo parseHdInfo(Element hdInfoE, boolean useRelative, List<Float> hd, Model model) {
        HDpaintingInfo info = new HDpaintingInfo(hd, model, useRelative);

        info.setMaxThreshValue(Float.parseFloat(hdInfoE.getAttribute("treshValue")));
        info.setMinSelection(Float.parseFloat(hdInfoE.getAttribute("minSelection")));
        info.setMaxSelection(Float.parseFloat(hdInfoE.getAttribute("maxSelection")));
        info.setIsSelection(Boolean.parseBoolean(hdInfoE.getAttribute("isSelection")));
        info.setIsRecomputed(Boolean.parseBoolean(hdInfoE.getAttribute("isRecomputed")));

        info.setvType(VisualizationType.valueOf(hdInfoE.getAttribute("viz-type")));
        info.setsType(SelectionType.valueOf(hdInfoE.getAttribute("selectionType")));

        info.setDensity(Float.parseFloat(hdInfoE.getAttribute("density")));
        info.setRecompute(Boolean.parseBoolean(hdInfoE.getAttribute("recompute")));

        return info;
    }

    private PApaintingInfo parsePaInfo(Element paInfoE) {
        File gpaFile = new File(tempFile + File.separator + paInfoE.getAttribute("gpaFile"));
        GPA gpa = (GPA) loadArbitraryObject(gpaFile);
        PApaintingInfo info = new PApaintingInfo(gpa, null, Integer.parseInt(paInfoE.getAttribute("type")));

        info.setEnhance(Float.parseFloat(paInfoE.getAttribute("enhance")));
        info.setPointSize(Float.parseFloat(paInfoE.getAttribute("pointSize")));
        info.setIndexOfSelectedPoint(Integer.parseInt(paInfoE.getAttribute("selectedPoint")));
        info.setIndexOfSelectedConfig(Integer.parseInt(paInfoE.getAttribute("selectedConfig")));
        info.setFacialPointRadius(Float.parseFloat(paInfoE.getAttribute("pointRadius")));

        return info;
    }
    
    private CrosscutConfig parseCrosscutData(Element crossE){
        CrosscutConfig crossViz = new CrosscutConfig();
        String attr;
        
        crossViz.setCrossCutPlaneIndex(Integer.parseInt(crossE.getAttribute("crossCutPlaneIndex")));
               
        Vector3f arbitraryPlane = new Vector3f();
        
        attr = crossE.getAttribute("arbitraryPlanePosX");
        if(attr != null && !attr.isEmpty())
            arbitraryPlane.x = Float.parseFloat(attr);
        attr = crossE.getAttribute("arbitraryPlanePosY");
        if(attr != null && !attr.isEmpty())
            arbitraryPlane.y = Float.parseFloat(attr);
        attr = crossE.getAttribute("arbitraryPlanePosZ");
        if(attr != null && !attr.isEmpty())
            arbitraryPlane.z = Float.parseFloat(attr);
        
        crossViz.setArbitraryPlanePos(arbitraryPlane.x, arbitraryPlane.y, arbitraryPlane.z);
        
        Vector3f planePos = new Vector3f();
        attr = crossE.getAttribute("planePosX");
        if(attr != null && !attr.isEmpty())
            planePos.x = Float.parseFloat(attr);
        attr = crossE.getAttribute("planePosY");
        if(attr != null && !attr.isEmpty())
            planePos.y = Float.parseFloat(attr);
        attr = crossE.getAttribute("planePosZ");
        if(attr != null && !attr.isEmpty())
            planePos.z = Float.parseFloat(attr);
        crossViz.setPlanePosition(planePos.x, planePos.y, planePos.z);
        
        crossViz.setCrosscutSize(Integer.parseInt(crossE.getAttribute("crosscutSize")));
        crossViz.setCrosscutThickness(Integer.parseInt(crossE.getAttribute("crosscutThickness")));
        
        attr = crossE.getAttribute("crosscutColor");
        if(attr != null && !attr.isEmpty())
            crossViz.setCrosscutColor(new Color(Integer.parseInt(attr)));
        crossViz.setHighlightCuts(Boolean.parseBoolean(crossE.getAttribute("highlightCuts")));
        crossViz.setShowVector(Boolean.parseBoolean(crossE.getAttribute("showVectors")));
        crossViz.setAllCuts(Boolean.parseBoolean(crossE.getAttribute("allCuts")));
        crossViz.setShowPlane(Boolean.parseBoolean(crossE.getAttribute("showPlane")));
        crossViz.setSamplingRays(Boolean.parseBoolean(crossE.getAttribute("samplingRays")));
        
        return crossViz;
    }
    
    private TransparencyConfig parseTransparencyData(Element transE){
        TransparencyConfig data = new TransparencyConfig();
        String attr;
        
        attr = transE.getAttribute("primaryColor");
        if(attr != null && !attr.isEmpty())
            data.setPrimaryColor(new Color(Integer.parseInt(attr)));
        attr = transE.getAttribute("secondaryColor");
        if(attr != null && !attr.isEmpty())
            data.setSecondaryColor(new Color(Integer.parseInt(attr)));
        data.setIsPrimarySolid(Boolean.parseBoolean(transE.getAttribute("primarySolid")));
        data.setIsSecondarySolid(Boolean.parseBoolean(transE.getAttribute("secondarySolid")));
        attr = transE.getAttribute("fogColor");
        if(attr != null && !attr.isEmpty())
            data.setFogColor(new Color(Integer.parseInt(attr)));
        data.setOverlayTransparency(Float.parseFloat(transE.getAttribute("overlayTransparency")));
        data.setInnerSurfaceSolid(Boolean.parseBoolean(transE.getAttribute("innerSurfaceSolid")));
        data.setUseGlyphs(Boolean.parseBoolean(transE.getAttribute("useGlyphs")));
        data.setUseContours(Boolean.parseBoolean(transE.getAttribute("useContours")));
        data.setFogVersion(Integer.parseInt(transE.getAttribute("fogVersion")));  
        
        return data;
    }
    
    private VectorsConfig parseVectorsData(Element vectorsE){
        VectorsConfig data = new VectorsConfig();
        
        data.setVectorDensity(Integer.parseInt(vectorsE.getAttribute("vectorDensity")));
        data.setVectorLength(Integer.parseInt(vectorsE.getAttribute("vectorLength")));
        data.setCylinderRadius(Integer.parseInt(vectorsE.getAttribute("cylinderRadius")));
        
        return data;      
    }
    
    private ColormapConfig parseColormapData(Element colorE){
        ColormapConfig data = new ColormapConfig();
        String attr;
        
        data.setHausdorfMaxTreshold(Integer.parseInt(colorE.getAttribute("haussdorfMaxTreshold")));
        data.setHausdorfMinTreshold(Integer.parseInt(colorE.getAttribute("haussdorfMinTreshold")));
        attr = colorE.getAttribute("colorScheme");
        if(attr != null && !attr.isEmpty())
            data.setUsedColorScheme(ColorScheme.valueOf(attr));
        
        return data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new ModelFileFilter(new String[]{"fid"}, "Fidentis project files"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            final File openFile = chooser.getSelectedFile();
            final ProjectTopComponent ntc = GUIController.getBlankProject();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ProgressHandle p = ProgressHandleFactory.createHandle("Opening project from file " + openFile.getName());
                    try {
                        p.start();
                        openProject(openFile, ntc);
                    } catch (IOException | ParserConfigurationException | SAXException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to open project");
                        ex.printStackTrace();
                    } finally {
                        p.finish();
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }
}
