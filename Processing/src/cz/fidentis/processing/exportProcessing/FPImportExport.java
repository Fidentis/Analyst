/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.exportProcessing;

import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.comparison.icp.Icp;
import cz.fidentis.controller.BatchComparison;
import cz.fidentis.controller.OneToManyComparison;
import cz.fidentis.enums.FileExtensions;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.landmarkParser.CSVparser;
import cz.fidentis.landmarkParser.DTAparser;
import cz.fidentis.landmarkParser.FPparser;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.landmarkParser.PPparser;
import cz.fidentis.landmarkParser.PTSparser;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.processing.featurePoints.PDM;
import cz.fidentis.utils.DialogUtils;
import cz.fidentis.utils.FileUtils;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 * Class handles import and export of Feature Points, whether they are computed
 * or loaded.
 *
 * @author Zuzana Ferkova
 */
public class FPImportExport {

    private static FPImportExport instance;

    private static final String[] IMPORT_FP_EXTENSIONS = new String[]{"pp", "fp", "csv", "pts", "dta"};
    private static final String[] PDM_EXTENSION = new String[]{"pdm"};

    private FPImportExport() {
    }

    ;
    
    public static FPImportExport instance() {
        if (instance == null) {
            instance = new FPImportExport();
        }

        return instance;
    }

    /**
     * Imports points from disk, in one of supported formats. Right now it is
     * able to load .pp, .fp and .csv files.
     *
     * @param tc - GUI top component, from where load points was initialized.
     * @param allowMultiple - whether to allow picking multiple files or not
     * @return - list of loaded points as FPModel class, where each FPModel
     * corresponds to one model and its FP, or null if no files were picked to
     * load
     */
    public List<FpModel> importPoints(Component tc, boolean allowMultiple) {
        //pick files to load
        File[] loadedFiles = DialogUtils.instance().openDialogueLoadFiles(tc, "Feature Points file", IMPORT_FP_EXTENSIONS, allowMultiple);
        
        // import the selected files
        return importPoints(loadedFiles);
    }
    
    /**
     * See importPoints(Component tc, boolean allowMultiple) for more info.
     *
     * @param files - files that contain feature points to import.
     * @return - list of loaded points as FPModel class, where each FPModel
     * corresponds to one model and its FP, or null if no files were picked to
     * load
     */
    public List<FpModel> importPoints(File[] files) {
        if(files == null) {
            return null;
        }
        
        List<FpModel> points = new ArrayList<FpModel>(files.length);

        //parse files, based on file extension
        for (File loadedFile : files) {
            FileExtensions fe = FileUtils.instance().getFileExtension(loadedFile.getName());
            if (fe == FileExtensions.PP) {
                points.add(PPparser.load(loadedFile.getPath()));
            } else if (fe == FileExtensions.FP) {
                points.add(FPparser.load(loadedFile.getPath()));
            } else if (fe == FileExtensions.CSV) {
                points.addAll(CSVparser.load(loadedFile.getPath()));
            } else if (fe == FileExtensions.DTA) {
                points.add(DTAparser.load(loadedFile.getPath()));
            } else if (fe == FileExtensions.PTS) {
                points.add(PTSparser.load(loadedFile.getPath()));
            }
        }

        return points;
    }

    /**
     * When model is loaded, it is being centralized, so that the center of the
     * model is at coordinates [0,0,0]. Same transformations need to be applied
     * to loaded FP for them to display correctly on model. This method takes
     * list of all loaded FP for each model and list of all models and pair them
     * based on the name of model stated in FpModel class. Once the pair between
     * FpModel and Model is found, all FPs in FpModel are transformed based on
     * Model's centralization. If there is FpModel that has no model associated
     * loaded, no transformations are applied to FpModel.
     *
     * @param points - loaded FP for all parsed FP files
     * @param models - list of loaded models
     */
    public void alignPointsToModels(List<FpModel> points, List<File> models) {
        HashMap<String, Model> modelTree = new HashMap<>();     //to be able to pair model and FpModel faster
        
        for (File f : models) {
            Model m = ModelLoader.instance().loadModel(f, Boolean.FALSE, Boolean.TRUE);

            modelTree.put(m.getName(), m);
        }

        //find pair for each FpModel and transform its points
        for (FpModel p : points) {
            Model associatedModel = modelTree.get(p.getModelName());

            if (associatedModel == null) {
                continue;
            }

            p.centralizeToModel(associatedModel);
        }
    }

    /**
     * Saves given points to one of supported formats. So far .pp, .fp and .csv
     * are supported. In order for points to be saved in specific format, user
     * has to give name of file in save window with specific file extension
     * (e.g. if you want to save file as .csv you have to type <file_name>.csv
     * to dialog. If no extension was given by user while picking save path,
     * file is saved as .fp
     *
     * @param tc - GUI top component, usually window from which save points
     * option was called
     * @param points - list of points to save, one FpModel per model
     */
    public void exportPoints(Component tc, List<FpModel> points) {
        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "Feature Points Files", IMPORT_FP_EXTENSIONS, false);

        if (filePath == null) {
            //save folder wasn't picked
            return;
        }

        FileExtensions fe = FileUtils.instance().getFileExtension(filePath);
        
        if(fe == FileExtensions.NONE){      //if no extension was given, save as .fp
            fe = FileExtensions.FP;
            filePath += ".fp";
        }

        if (fe == FileExtensions.CSV) {
            CSVparser.save(points, filePath);
        } else if (fe == FileExtensions.PP) {
            for (FpModel model : points) {
                try {
                    PPparser.save(model, filePath);
                } catch (Exception ex) {
                    Logger.getLogger(FPImportExport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (fe == FileExtensions.DTA) {
            for (FpModel model : points) {
                DTAparser.save(model, filePath);
            }
        } else if (fe == FileExtensions.PTS) {
            for (FpModel model : points) {
                PTSparser.save(model, filePath);
            }
        } else if (fe == FileExtensions.FP) {
            for (FpModel model : points) {
                try {
                    FPparser.save(model, filePath);
                } catch (ParserConfigurationException | TransformerException ex) {
                    Logger.getLogger(FPImportExport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Turn list of FacialPoints to FpModel. If points, or modelName are null,
     * computation is stopped. Does not create copy of Facial Points.
     *
     * @param points - list of Facial Points
     * @param modelName - name of the model for which the FpModel is being
     * created
     * @return new FpModel with given points and model name, or null if either
     * of parameters is null
     */
    public FpModel getFpModelFromFP(List<FacialPoint> points, String modelName) {
        if (points == null || modelName == null) {
            return null;
        }

        FpModel model = new FpModel(modelName);
        model.setFacialpoints(points);

        return model;
    }

    /**
     * Export points for 1:1 mode. If points were computed in FIDENTIS, points
     * are reverted back to position before models were aligned to model in
     * Frankfurt position. In all cases, points are decentralized based on
     * centralization transformations of model they are associated with.
     *
     * @param tc - GUI top component, usually window from which point exporting
     * was initialized
     * @param mainFP - Feature points for main face
     * @param mainModel - main model
     * @param compFP - Feature points for secondary model
     * @param secondaryModel - secondary model
     */
    public void exportTwoFaces(Component tc, List<FacialPoint> mainFP, Model mainModel,
            List<FacialPoint> compFP, Model secondaryModel) {
        List<FpModel> points = new ArrayList<>(2);

        FpModel model = prepareFPforExport(mainFP, mainModel);
        if (model != null) {
            points.add(model);
        }

        model = prepareFPforExport(compFP, secondaryModel);
        if (model != null) {
            points.add(model);
        }

        FPImportExport.instance().exportPoints(tc, points);
    }

    //creates FpModel and decentralize FPs to model they were computed on
    private FpModel prepareFPforExport(List<FacialPoint> fp, Model m) {
        if(fp == null || fp.isEmpty() || m == null){
            return null;        //don't export fps if there are none
        }
        
        FpModel model = FPImportExport.instance().getFpModelFromFP(fp, m.getName());

        if (model != null) {
            model.decentralizeToModel(m);
        }

        return model;
    }

    //creates FpModel and decentralize FPs to model they were computed on
    private FpModel prepareFPforExport(List<FacialPoint> fp, File f) {
        FpModel model = FPImportExport.instance().getFpModelFromFP(fp, f.getName());

        if (model != null) {
            model.decentralizeToFile(f);
        }

        return model;
    }

    /**
     * Export points for 1:N mode.Points are decentralized based on
     * centralization transformations of model they are associated with.
     *
     * @param tc - GUI top component, usually window from which point exporting
     * was initialized
     * @param data - OneToManyComparison class storing all computed data from
     * 1:N mode
     * @param mainFP - Feature points for main face
     * @param mainModel - main model
     */
    public void exportOneToMany(final Component tc, final OneToManyComparison data,
            final List<FacialPoint> mainFP, final Model mainModel) {
        final List<FpModel> points = new ArrayList<>();
        final List<File> models = data.getModels();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ProgressHandle p = ProgressHandleFactory.createHandle("Exporting Feature Points...");
                p.start();

                try {

                    //transform and store FpModel for N models
                    for (int i = 0; i < models.size(); i++) {
                        File f = models.get(i);
                        String modelName = f.getName();
                        List<FacialPoint> fp = data.getFacialPoints(modelName);
                        
                        if(fp == null || fp.isEmpty())
                            continue;       //don't export fps if there are none

                        FpModel model = prepareFPforExport(fp, f);

                        if (model != null) {
                            points.add(model);
                        }
                    }

                    //transform and store FpModel for main face        
                    FpModel model = prepareFPforExport(mainFP, mainModel);

                    if (model != null) {
                        points.add(model);
                    }

                    FPImportExport.instance().exportPoints(tc, points);

                    p.finish();
                } catch (Exception ex) {
                    p.finish();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    /**
     * Export points for N:N mode. Points are decentralized based on
     * centralization transformations of model they are associated with.
     *
     * @param tc - GUI top component, usually window from which point exporting
     * was initialized
     * @param data - BatchComparison class storing all computed data from N:N
     * mode
     */
    public void exportBatch(final Component tc, final BatchComparison data) {
        final List<FpModel> points = new ArrayList<>();
        final List<File> models = data.getModels();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ProgressHandle p = ProgressHandleFactory.createHandle("Exporting Feature Points...");
                p.start();

                try {

                    //transform and store FpModel for N faces
                    for (int i = 0; i < models.size(); i++) {
                        File f = models.get(i);
                        String modelName = f.getName();
                        List<FacialPoint> fp = data.getFacialPoints(modelName);
                        
                        if(fp == null || fp.isEmpty())
                            continue;       //don't export fps if they are not there

                        FpModel model = prepareFPforExport(fp, f);

                        if (model != null) {
                            points.add(model);
                        }
                    }

                    FPImportExport.instance().exportPoints(tc, points);
                    p.finish();
                } catch (Exception ex) {
                    p.finish();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
    
    public void exportPDM(final Component tc, PDM pdm){
        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "Point Distribution Model (.pdm)", PDM_EXTENSION, false);

        if (filePath == null) {
            //save folder wasn't picked
            return;
        }
        
        pdm.savePDM(filePath);
    }
    
    public PDM importPDM(final Component tc){
        File[] loadedFiles = DialogUtils.instance().openDialogueLoadFiles(tc, "Point Distribution Model (.pdm)", PDM_EXTENSION, false);
        
        PDM pdm = PDM.loadPDM(loadedFiles[0].getAbsolutePath());
        
        return pdm;
    }
}
