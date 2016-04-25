/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.processing.exportProcessing;

import cz.fidentis.model.Model;
import cz.fidentis.model.ModelExporter;
import cz.fidentis.processing.comparison.surfaceComparison.SurfaceComparisonProcessing;
import cz.fidentis.undersampling.Undersampling;
import cz.fidentis.utils.DialogUtils;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utils.ListUtils;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.SortUtils;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author Zuzana Ferkova
 */
public class ResultExports {

    private static ResultExports instance;

    private static final String[] FILE_EXTENSIONS_MODEL = new String[]{"obj"};
    private static final String[] FILE_EXTENSIONS_RESULTS = new String[]{"csv"};
    private static final String[] FILE_EXTENSIONS_PIC = new String[]{"png"};

    private ResultExports() {
    }

    public static ResultExports instance() {
        if (instance == null) {
            instance = new ResultExports();
        }

        return instance;
    }

    /**
     * Exports aligned models from 1:1 comparison to the disk. Opens dialog
     * options to choose save location
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param mainF - main face of 1:1 comparison
     * @param compF - secondary face of 1:1 comparison
     */
    public void exportModels(Component tc, Model mainF, Model compF) {
        if (mainF == null || compF == null) {
            //error TODO
            return;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "OBJ Models", FILE_EXTENSIONS_MODEL, true);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        exportFace(mainF, filePath);
        exportFace(compF, filePath);
    }

    //exports single face to filePath. Name of the exported model is name of
    //the original model + '_twoFaces_reg.obj'
    private void exportFace(Model face, String filePath) {
        String modelName = getFileName(face.getName());

        ModelExporter me = new ModelExporter(face);
        File f = new File(filePath + File.separator + modelName + "_twoFaces_reg.obj");
        me.exportModelToObj(f, false);
    }

    /**
     * Exports numeric results of 1:1 comparison, given as string, to the disk.
     * Opens dialog option to choose save location.
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param results - string representation of 1:1 numerical results
     */
    public void exportCSVnumeric(Component tc, String results) {
        if (results == null) {
            //error TODO
            return;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, false);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        writeStringToFile(tc, filePath, results);
    }

    public void exportCSVnumericOrder(Component tc, List<Float> results, List<File> models) {
        if (results == null) {
            //error TODO
            return;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, false);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        List<Integer> order = ListUtils.instance().populateList(results.size());
        order = SortUtils.instance().sortIndices(results, order);
        List<Float> res = SortUtils.instance().sortListFromIndices(results, order);
        //res = ListUtils.instance().reverseList(res);
        String resStr = setValues(res, order, models);

        writeStringToFile(tc, filePath, resStr);
    }

    private String setValues(List<Float> hdDistance, List<Integer> indices, List<File> models) {
        StringBuilder strResults = new StringBuilder(";");

        for (int i = 0; i < indices.size(); i++) {
            strResults.append(models.get(indices.get(i)).getName()).append(';');
        }

        strResults.append("\nMain Face;");

        for (Float f : hdDistance) {
            strResults.append(f).append(';');
        }

        return strResults.toString();
    }

    /**
     * Computes and export symmetric result matrix from given precomputed
     * results.
     *
     * @param tc - GUI top component, usually window from which the export
     * button was called
     * @param precomputedRes - table format of computed results
     * @param varianceMethod - variance method to be used to get asymmetric
     * result table
     * @param originalModels - list of URLs containing models before alignment,
     * to use in table naming
     */
    public void exportSymetricRes(final Component tc, final String precomputedRes, final int varianceMethod, final List<File> originalModels, 
            final float upperTreshold, final float lowerTreshold) {
        if (precomputedRes == null) {
            //error
            return;
        }

        final String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, false);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ProgressHandle p = ProgressHandleFactory.createHandle("Computing symmetric nummerical results...");
                p.start();

                try {

                    List<ArrayList<Float>> symRes = MathUtils.instance().symetricMatrix(precomputedRes);
                    String res = SurfaceComparisonProcessing.instance().batchCompareNumericalResultsTable((ArrayList<ArrayList<Float>>) symRes, varianceMethod, 
                            originalModels, upperTreshold, lowerTreshold);

                    writeStringToFile(tc, filePath, res);

                    p.finish();
                } catch (Exception ex) {
                    p.finish();
                }
            }

        };

        Thread t = new Thread(r);
        t.start();
    }

    //writes given string to path as csv
    private void writeStringToFile(Component tc, String path, String result) {
        if (!(path.contains(".csv"))) {
            path = path.concat(".csv");
        }
        File file = new File(path);
        BufferedWriter bw = null;
        FileWriter fw;

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(result);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(tc, "Saving failed.");
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException i) {
                JOptionPane.showMessageDialog(tc, "Saving failed.");
            }
        }
    }

    /**
     * Write auxiliary results to disk.
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param results - auxiliary results
     * @param modelName - name of the main model
     * @param useRelative - whether to use signed distance or not
     */
    public void writeAuxResults(Component tc, List<Float> results, String modelName, boolean useRelative) {
        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, false);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        writeListToFile(filePath, modelName, useRelative, results);
    }

    //writes list of floats to the disk, uses modelName to note which model was main in comparison
    private void writeListToFile(String path, String modelName, boolean useRelative, List<Float> results) {
        if (!path.contains(".csv")) {
            path += ".csv";
        }

        modelName = getFileName(modelName);

        FileUtils.instance().savePairComparisonAuxResults(path, results,
                useRelative, modelName);

    }

    //deletes file format from name of the model
    private String getFileName(String fileName) {
        int formatStartIndex = fileName.lastIndexOf(".");

        if (formatStartIndex > 0) {
            fileName = fileName.substring(0, formatStartIndex);
        }

        return fileName;

    }

    /**
     * Exports visual results to disk. Creates snapshot of given canvas
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param canvas - canvas containing results to be saved
     * @param width - max width of the picture
     * @param height - max height of the picture
     */
    public void exportVisualResults(Component tc, GLEventListener canvas, int width, int height) {
        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "PNG images", FILE_EXTENSIONS_PIC, false);

        if (filePath == null) {
            //selection of folder wasn't approved, nothing happens
            return;
        }

        savePicture(filePath, tc, canvas, width, height);

    }

    //saves picture as PNG file to given path, of given size
    private void savePicture(String path, Component tc, GLEventListener canvas, int width, int height) {
        if (path.contains(".png")) {
        } else {
            path = path + ".png";
        }

        BufferedImage bi;
        Graphics g;
        JFrame newFrame = new JFrame();
        GLJPanel picture = new GLJPanel();
        int[] size = new int[]{width, height};

        newFrame.setSize(size[0], size[1]);
        picture.addGLEventListener(canvas);
        newFrame.add(picture);
        newFrame.setVisible(true);
        picture.setSize(size[0], size[1]);
        picture.repaint();

        bi = new BufferedImage(size[0], size[1], BufferedImage.TYPE_3BYTE_BGR);
        g = bi.createGraphics();
        picture.paint(g);

        try {
            ImageIO.write(bi, "png", new File(path));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(tc, "Saving image failed.");
        }
    }

    /**
     * Save auxiliary result from 1:N comparison to disk.
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param results - auxiliary results for 1:N comparison
     * @param avgFaceResults - auxiliary results for 1:1 comparison between main
     * face and average face
     * @param models - list of N models
     * @param mainModelName - name of the main model
     * @param useRelative - whether to use signed distance or not
     */
    public void saveAuxOneToMany(Component tc, List<ArrayList<Float>> results, List<Float> avgFaceResults, List<File> models, String mainModelName, boolean useRelative) {
        if (results == null || avgFaceResults == null) {
            //error TODO
            return;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, false);

        if (filePath == null) {
            //choice of folder wasn't approved nothing happens
            return;
        }

        //save aux results for each face
        writeListListToDisk(results, models, mainModelName, useRelative, filePath, "_1n");

        String avgFacePath = filePath + File.separator + "Average Face Visual";

        //save aux results for average face
        writeListToFile(avgFacePath, "Average Face Visual", useRelative, avgFaceResults);
    }

    //write list of array list of floats to disk. Name of the resulting csv is mainModelName + mode
    private void writeListListToDisk(List<ArrayList<Float>> numRes, List<File> models, String modelName, boolean useRelative, String path, String mode) {
        modelName = getFileName(modelName);
        FileUtils.instance().saveMatrixToCSV((ArrayList<ArrayList<Float>>) numRes, path, modelName, mode, models, useRelative);
    }

    /**
     * Copies list of registered models from disk to another place in disk.
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param models - list of models to copy with their URL on disk
     * @param originalModels - preregistered models, used to name copied models
     * @param mainF - main face in 1:N comparison
     * @param mode
     */
    public void saveRegisteredModelsOneToMany(Component tc, List<File> models, List<File> originalModels, Model mainF, String mode) {
        String filePath = saveRegisteredModelsBatch(tc, models, originalModels, mode);

        if (filePath == null) {
            //save canceled nothing happenes
            return;
        }

        String mainModelName = getFileName(mainF.getName());

        //save single model
        saveSingleModel(filePath, mainF, mainModelName, mode);
    }

    //copy list of models from their location to new location. Name of copied model is in format originalModelName + mode + "_reg.obj"
    private void copyModelsToDisk(Component tc, List<File> models, List<File> originalModels, String mode, String folderOut) {
        boolean all = false;
        boolean cancel = false;

        for (int i = 0; i < models.size(); i++) {
            if (cancel) {
                break;
            }

            String orgModelName = getFileName(originalModels.get(i).getName());
            String outModelName = folderOut + File.separator + orgModelName + mode + "_reg.obj";
            Path currentModelPath = models.get(i).toPath();

            File fileOut = new File(outModelName);

            try {
                if (all || !fileOut.exists()) {
                    Files.copy(currentModelPath, fileOut.toPath(), REPLACE_EXISTING);
                } else {
                    int result = DialogUtils.instance().rewriteFile(outModelName);
                    switch (result) {       //resolve rewriting file
                        case 0: // Yes
                            Files.copy(currentModelPath, fileOut.toPath(), REPLACE_EXISTING);
                            break;
                        case 1: // Yes to all
                            Files.copy(currentModelPath, fileOut.toPath(), REPLACE_EXISTING);
                            all = true;
                            break;
                        case 2: //No
                            break;
                        case 3: //Cancel
                            cancel = true;
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(tc, "Saving models failed.");
            }

        }

    }

    //saves single model to disk as obj file
    private void saveSingleModel(String path, Model mainF, String modelName, String mode) {
        ModelExporter me = new ModelExporter(mainF);
        File f = new File(path + File.separator + modelName + mode);
        me.exportModelToObj(f, false);
    }

    /**
     * Reads auxiliary results from disk and saves them to user defined path
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param models - list of URLs to registered models
     * @param visualRes - listList of visual results
     * @param useRelative - whether to use signed distance or not
     * @param tmpDirPath - URL to tempory file
     */
    public void saveAuxBatch(Component tc, List<File> models, ArrayList<ArrayList<Float>> visualRes, boolean useRelative, String tmpDirPath) {
        if (models == null || visualRes == null || tmpDirPath == null) {
            //error TODO
            return;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "CSV files", FILE_EXTENSIONS_RESULTS, true);

        ProgressHandle p = ProgressHandleFactory.createHandle("Saving axuiliary results, please wait a moment...");
        p.start();

        try {

            if (filePath == null) {
                //choice of folder wasn't approved nothing happens
                return;
            }

            writeAuxBatch(models, visualRes, useRelative, tmpDirPath, filePath);

            p.finish();
        } catch (Exception ex) {
            p.finish();
        }
    }

    //reads tmp files with aux batch results and save them to user defined URL on disk
    private void writeAuxBatch(List<File> models, ArrayList<ArrayList<Float>> visualRes, boolean useRelative, String tmpDirPath, String savePath) {
        int numOfModels = models.size();
        String mode = "_batchResults";

        for (int i = 0; i < numOfModels; i++) {
            ArrayList<ArrayList<Float>> newOut = FileUtils.instance().readFolderWithCSV(tmpDirPath + File.separator + (i + 1),
                    numOfModels, i, useRelative);

            writeListListToDisk(newOut, models, models.get(i).getName(), useRelative, savePath, mode);
        }

        //save avg face aux results
        FileUtils.instance().saveMatrixToCSV(visualRes,
                savePath, "averageFace", mode, models, useRelative);
    }

    /**
     * Saves registered models to disk
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param models - list of URLs containing path to tmp folder with
     * registered models
     * @param originalModels - list of file with pre-registered models
     * @param mode - which mode was the computation made in (_1n, _batch, etc.)
     * @return file path chosen by user or null if no path was chosen
     */
    public String saveRegisteredModelsBatch(Component tc, List<File> models, List<File> originalModels, String mode) {
        if (models == null || originalModels == null) {
            //error TODO
            return null;
        }

        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "OBJ models", FILE_EXTENSIONS_MODEL, true);

        if (filePath == null) {
            //choice of folder wasn't approved nothing happens
            return null;
        }

        //copy N models
        copyModelsToDisk(tc, models, originalModels, mode, filePath);

        return filePath;
    }

    /**
     * Saves average face to disk
     *
     * @param tc - GUI component which opens the dialog (usually active project)
     * @param avgFace - average face
     * @param mode - which mode was the computation made in (_1n, _batch, etc.)
     */
    public void saveAvgFace(Component tc, Model avgFace, String mode) {
        if (avgFace == null) {
            return;
        }
        String filePath = DialogUtils.instance().openDialogueSaveFile(tc, "OBJ models", FILE_EXTENSIONS_MODEL, true);

        //save single model
        saveSingleModel(filePath, avgFace, "Average Face", mode);
    }
}
