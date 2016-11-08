/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.importfromimage;

import cz.fidentis.controller.Gender;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangleFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangularDCEL;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.GraphicMeshBuilderFromModel;
import cz.fidentis.model.Material;
import cz.fidentis.model.Materials;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelExporter;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.processing.comparison.surfaceComparison.SurfaceComparisonProcessing;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3f;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Mode",
        id = "cz.fidentis.gui.actions.ImportFromImage"
)
@ActionRegistration(
        displayName = "#CTL_ImportFromImage"
)
@ActionReference(path = "Menu/File", position = -100)
@Messages("CTL_ImportFromImage=Import face from image...")
public final class ImportFromImage implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final ProjectTopComponent tc = GUIController.getSelectedProjectTopComponent();
        if(tc.getProject() == null) {
            JOptionPane.showMessageDialog(tc, "Cannot import model to empty project.");
            return;
        }
        final ImportFromImageDialog dialog = new ImportFromImageDialog(null, true);
        dialog.setLocationRelativeTo(tc);

        dialog.setVisible(true);
        if(dialog.isCanceled()) {
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ProgressHandle p = ProgressHandle.createHandle("Creating face...");
                p.start();
                List<FacialPoint> points = dialog.getFeaturePoints();
                Model imported = importFromImage(dialog.getImageFile(), dialog.getSelectedGender(), dialog.getSelectedAge(), points, dialog.getNumberOfVerts());
                
                ModelExporter exp = new ModelExporter(imported);
                File modelSaveDir = new File(tc.getProject().getTempDirectory().getPath() + File.separator + imported.getName().substring(0, imported.getName().length()-4));
                File modelFile = new File(modelSaveDir.getPath() + File.separator + imported.getName());
                exp.exportModelToObj(modelSaveDir, true);
                
                
                Model model = ModelLoader.instance().loadModel(modelFile, true, true);
                
                switch (tc.getProject().getSelectedPart()) {
                    case 2:
                        if (dialog.isPrimary()) {
                            tc.getViewerPanel_2Faces().getListener1().setModels(model);
                            tc.getViewerPanel_2Faces().getCanvas1().setImportLabelVisible(false);
                            tc.getProject().getSelectedComparison2Faces().setModel1(model);
                        } else {
                            tc.getViewerPanel_2Faces().getListener2().setModels(model);
                            tc.getViewerPanel_2Faces().getCanvas2().setImportLabelVisible(false);
                            tc.getProject().getSelectedComparison2Faces().setModel2(model);
                        }
                        break;
                    case 3:
                        if (dialog.isPrimary()) {
                            tc.getOneToManyViewerPanel().getListener1().setModels(model);
                            tc.getOneToManyViewerPanel().getCanvas1().setImportLabelVisible(false);
                            tc.getProject().getSelectedOneToManyComparison().setPrimaryModel(model);
                        } else {
                            tc.getOneToManyViewerPanel().getListener2().setModels(model);
                            tc.getOneToManyViewerPanel().getCanvas2().setImportLabelVisible(false);
                            tc.getProject().getSelectedOneToManyComparison().addModel(modelFile);
                        }
                        break;
                    case 4:
                        tc.getViewerPanel_Batch().getListener().setModels(model);
                        tc.getViewerPanel_Batch().getCanvas1().setImportLabelVisible(false);
                        tc.getProject().getSelectedBatchComparison().addModel(modelFile);
                        break;
                    /*case 5:
                        tc.getAgeingViewerPanel().getListenerOrigin().setModels(model);
                        tc.getAgeingViewerPanel().getOriginCanvas().setImportLabelVisible(false);
                        tc.getProject().getSelectedAgeing().setOriginModel(model);
                        break;*/
                }
                p.finish();
                GUIController.updateNavigator();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public Model importFromImage(File imageFile, Gender trgetGender, AgeCategories targetAge, List<FacialPoint> points, int numOfVerts) {
        // 1. load suitable gem models
        ArrayList<Model> gems = getSuitableGems(trgetGender, targetAge, points);
        // 2. initialize models for computation
        Model model = new Model();
        initializeModels(model, gems, imageFile, points);
        // 3. prepare delaunay meshes
        initializeDelaunayMeshes(model, gems);
        // 4. subdivide to reach desired detail
        subdivideUntilSatisfied(model, gems, numOfVerts);
        // 5. finally, assign depth to model using gems
        assignDepth(model, gems, imageFile, points);
        // 6. adjust model to some reasonable proportions
        adjustProportions(model, points);
        // 7. postprocess model to be ready to use in application
        postprocessModel(model);

        return model;
    }

    private ArrayList<ArrayList<Integer>> delaunay(List<Vector3f> verts) {
        Delaunay d = new Delaunay(verts);
        ArrayList<Delaunay.Triangle> triangles = d.delaunay();
        ArrayList<ArrayList<Integer>> result = new ArrayList<>(triangles.size());
        for(Delaunay.Triangle t : triangles) {
            ArrayList<Integer> indexList = new ArrayList<>(3);
            //for(int i=2;i>=0;i--) {
            for(int i=0;i<3;i++) {
                indexList.add(t.getVertex(i).index+1);
            }
            result.add(indexList);
        }
        return result;
        /*ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        int[][] indices = new int[][]{
            new int[]{1, 2, 26},
            new int[]{23, 22, 26},
            new int[]{23, 27, 22},
            new int[]{2, 23, 26},
            new int[]{15, 1, 26},
            new int[]{23, 5, 27},
            new int[]{5, 6, 27},
            new int[]{6, 16, 27},
            new int[]{20, 1, 15},
            new int[]{20, 8, 1},
            new int[]{1, 8, 2},
            new int[]{2, 8, 3},
            new int[]{2, 3, 23},
            new int[]{3, 24, 23},
            new int[]{24, 25, 23},
            new int[]{25, 4, 23},
            new int[]{4, 5, 23},
            new int[]{5, 4, 9},
            new int[]{6, 5, 9},
            new int[]{6, 9, 21},
            new int[]{21, 16, 6},
            new int[]{24, 3, 8},
            new int[]{8, 7, 24},
            new int[]{7, 25, 24},
            new int[]{7, 9, 25},
            new int[]{9, 4, 25},
            new int[]{18, 8, 20},
            new int[]{18, 13, 8},
            new int[]{28, 13, 18},
            new int[]{28, 17, 13},
            new int[]{17, 12, 13},
            new int[]{19, 21, 9},
            new int[]{14, 19, 9},
            new int[]{14, 29, 19},
            new int[]{17, 14, 12},
            new int[]{17, 29, 14},
            new int[]{13, 12, 11},
            new int[]{12, 14, 11},
            new int[]{11, 14, 10},
            new int[]{13, 11, 10},
            new int[]{13, 10, 8},
            new int[]{10, 7, 8},
            new int[]{10, 14, 9},
            new int[]{10, 9, 7}
        };
        for (int[] indice : indices) {
            ArrayList<Integer> t = new ArrayList<>(3);
            for (int j = 0; j < indice.length; j++) {
                t.add(indice[j]);
            }
            result.add(t);
        }
        return result;*/
    }
    
    private void loop2(Model model) {
        HashMap<HalfEdgeId, Integer> splitInfo = new HashMap<>();
        ArrayList<Integer> midTriangle = null;
        ArrayList<Integer> currentTriangle = null;
        ArrayList<ArrayList<Integer>> newFaces = new ArrayList<>(model.getFaces().getNumFaces()*4);
        
        for(int i=0;i<model.getFaces().getNumFaces();i++) {
            int[] face = model.getFaces().getFaceVertIdxs(i);
            for(int j=0;j<face.length;j++) {
                HalfEdgeId id = new HalfEdgeId(face[j], face[(j+1)%face.length]);
                if(!splitInfo.containsKey(id)) {
                    int idx = model.getVerts().size();
                    Vector3f splitPoint = new Vector3f(model.getVerts().get(id.getFromIndex()-1));
                    splitPoint.add(model.getVerts().get(id.getToIndex()-1));
                    splitPoint.scale(0.5f);
                    model.getVerts().add(splitPoint);
                    model.getNormals().add(splitPoint);
                    model.getTexCoords().add(splitPoint);
                    splitInfo.put(id, idx + 1);
                    splitInfo.put(id.getIdOfTwin(), idx + 1);
                }
            }
            
            midTriangle = new ArrayList<>(3);
            for (int j=0;j<face.length;j++) {
                HalfEdgeId id = new HalfEdgeId(face[j], face[(j+1)%face.length]);
                midTriangle.add(splitInfo.get(id));
                currentTriangle = new ArrayList<>(3);
                currentTriangle.add(splitInfo.get(id));
                currentTriangle.add(id.getToIndex());
                HalfEdgeId nextId = new HalfEdgeId(face[(j+1)%face.length], face[(j+2)%face.length]);
                currentTriangle.add(splitInfo.get(nextId));
                newFaces.add(currentTriangle);
            }
            newFaces.add(midTriangle);
        }
        
        model.getFaces().clearFaces();
        for(ArrayList<Integer> face : newFaces) {
            model.getFaces().addFace(face, face);
        }
    }

    private void loopSubdivision(Model model) {
        GraphicMeshBuilderFromModel builder = new GraphicMeshBuilderFromModel(model);
        GraphicMesh graphicMesh = new GraphicMesh(builder);
        TriangularDCEL dcel = TriangularDCEL.fromMesh(graphicMesh);

        // update vertex positions for original vertices first
        int i = 0;
        for (Vertex v : dcel.getVertecies()) {
            Vector3f newPos = updatePosition(v);
            model.getVerts().set(i, newPos);
            i++;
        }
        
        model.getFaces().clearFaces();

        HashMap<HalfEdgeId, Integer> splitInfo = new HashMap<>();
        ArrayList<Integer> midTriangle = new ArrayList<>(3);
        ArrayList<Integer> currentTriangle = new ArrayList<>(3);

        for (TriangleFace face : dcel.getFaces()) {
            for (HalfEdge edge : face.getIncidentHalfEdge().getLoop()) {
                if (!splitInfo.containsKey(edge.getId())) {
                    int idx = model.getVerts().size();
                    Vector3f splitPoint = updatePosition(edge);
                    model.getVerts().add(splitPoint);
                    model.getNormals().add(splitPoint);
                    model.getTexCoords().add(splitPoint);
                    splitInfo.put(edge.getId(), idx);
                    splitInfo.put(edge.getTwin().getId(), idx);
                }
            }

            midTriangle.clear();
            for (HalfEdge edge : face.getIncidentHalfEdge().getLoop()) {
                midTriangle.add(splitInfo.get(edge.getId()) + 1);
                currentTriangle.clear();
                currentTriangle.add(splitInfo.get(edge.getId()) + 1);
                currentTriangle.add(edge.getEnd().getIndex() + 1);
                currentTriangle.add(splitInfo.get(edge.getNext().getId()) + 1);
                model.getFaces().addFace(currentTriangle, currentTriangle);
            }
            model.getFaces().addFace(midTriangle, midTriangle);
        }
    }

    private Vector3f updatePosition(Vertex v) {
        Coordinates origin = v.position();
        Vector3f result = new Vector3f();
        ArrayList<Vertex> betas = new ArrayList<>(); // neighboring vertices
        ArrayList<HalfEdge> boundEdges = new ArrayList<>(); // mesh bound edges
        IncidentEdgeIterator it = new IncidentEdgeIterator(v);

        // search neighborhood
        while (it.hasNext()) {
            HalfEdge e = it.next();
            betas.add(e.getBegining());
            if (e.isOuter() || e.getTwin().isOuter()) {
                boundEdges.add(e);
            }
        }

        if (boundEdges.size() > 0) {
            // if vertex is on mesh boundary
            Coordinates v1 = boundEdges.get(0).getEndPosition();
            Coordinates v2 = boundEdges.get(1).getEndPosition();

            result.x = (float) ((origin.getX() * 6 + v1.getX() + v2.getX()) / 8);
            result.y = (float) ((origin.getY() * 6 + v1.getY() + v2.getY()) / 8);
            result.z = (float) ((origin.getZ() * 6 + v1.getZ() + v2.getZ()) / 8);
        } else {
            double beta;
            if (betas.size() <= 3) {
                beta = 3 / (double) 16;
            } else {
                beta = 3 / ((double) betas.size() * 8);
            }

            result.x = 0;
            result.y = 0;
            result.z = 0;
            for (int i = 0; i < betas.size(); i++) {
                Coordinates b = betas.get(i).position();
                result.x += beta * b.getX();
                result.y += beta * b.getY();
                result.z += beta * b.getZ();
            }

            result.x += (1 - betas.size() * beta) * origin.getX();
            result.y += (1 - betas.size() * beta) * origin.getY();
            result.z += (1 - betas.size() * beta) * origin.getZ();
        }

        return result;
    }

    private Vector3f updatePosition(HalfEdge edge) {
        Vertex v1 = edge.getBegining();
        Vertex v2 = edge.getEnd();
        Vertex v3 = null;
        Vertex v4 = null;

        if (!edge.getIncidentFace().isOuterFace()) {
            v3 = edge.getNext().getEnd();
        } else if (!edge.getTwin().getIncidentFace().isOuterFace()) {
            v3 = edge.getTwin().getNext().getEnd();
        } else {
            v3 = edge.getNext().getEnd();
            v4 = edge.getTwin().getNext().getEnd();
        }

        if (v4 == null) {
            Coordinates mid = v1.position().getMiddlePoint(v2.position());
            return new Vector3f(mid.asFloatArray());
        } else {
            Coordinates x = new Coordinates();
            x = x.add(v1.position().scaled(3)).add(v2.position().scaled(3)).add(v3.position()).add(v4.position()).scaled(0.125d);
            return new Vector3f(x.asFloatArray());
        }
    }
    
    private ArrayList<Model> getSuitableGems(Gender gender, AgeCategories age, List<FacialPoint> points) {
        ArrayList<Model> gems = new ArrayList<>();

        String pathBase = GUIController.getPath() + File.separator + "models" + File.separator + "resources" + File.separator + "depth_models" + File.separator;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathBase + "index.csv"));) {
            // skip the header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Gender currentGender = Gender.valueOf(parts[0]);
                    AgeCategories currentAge = AgeCategories.valueOf(parts[1]);
                    if (currentGender == gender && (age == AgeCategories.ALL || currentAge == age)) {
                        // include this model to GEM computation
                        System.out.println("Gender " + currentGender + " Age " + currentAge);
                        Model gem = new Model();
                        String gemDir = pathBase + parts[2];
                        gem.setDirectoryPath(gemDir);

                        // load its points
                        List<FacialPoint> gemPoints = loadFidoCsv(new File(gemDir + File.separator + "pts.csv"));

                        for (FacialPoint point : points) {
                            if (point.isActive()) {
                                FacialPoint g = null;
                                for (FacialPoint gemPoint : gemPoints) {
                                    if (gemPoint.getType().intValue() == point.getType().intValue()) {
                                        g = gemPoint;
                                    }
                                }

                                if (g != null) {
                                    gem.getVerts().add(g.getPosition());
                                }
                            }
                        }
                        gem.getNormals().addAll(gem.getVerts());
                        gem.getTexCoords().addAll(gem.getVerts());
                        gems.add(gem);
                    }
                }
            }
        } catch (IOException ex) {
            return null;
        }

        return gems;
    }

    /*private void assignFeaturePoints(List<FacialPoint> points, Model model, Model gemModel) {
        String path = GUIController.getPath() + File.separator + "models" + File.separator + "resources" + File.separator + "depth_models" + File.separator;
        File csvFile = new File(path + "pts.csv");
        gemModel.setDirectoryPath(path);

        List<FacialPoint> gemPoints = loadFidoCsv(csvFile);
        
        for (FacialPoint point : points) {
            FacialPoint g = null;
            for (FacialPoint gemPoint : gemPoints) {
                if (gemPoint.getType() == point.getType()) {
                    g = gemPoint;
                }
            }

            if (g != null) {
                model.getVerts().add(point.getPosition());
                gemModel.getVerts().add(g.getPosition());
            }
        }
        
        model.getNormals().addAll(model.getVerts());
        model.getTexCoords().addAll(model.getVerts());
        gemModel.getNormals().addAll(gemModel.getVerts());
        gemModel.getTexCoords().addAll(gemModel.getVerts());
    }*/

    public static ArrayList<FacialPoint> loadFidoCsv(File csvFile) {
        ArrayList<FacialPoint> pts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            float x = 0;
            float y = 0;

            String wholeLine = reader.readLine();
            String[] numbers = wholeLine.split(";");
            for (int i = 0; i < 58; i++) { // only take 58 numbers (29 pairs)
                float val = Float.parseFloat(numbers[i].replace(',', '.'));
                if (i % 2 == 0) {
                    x = val;
                } else {
                    y = val;
                    FacialPoint p = new FacialPoint(getFpTypeFromFido(i / 2), new Vector3f(x, y, 0));
                    //if (p.getType() != FacialPointType.unspecified) {
                        pts.add(p);
                    //}
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return pts;
    }

    public static Integer getFpTypeFromFido(int i) {
        switch (i) {
            case 0:
                return FacialPointType.EX_R.ordinal();
            case 1:
                return FacialPointType.PUP_R.ordinal();//pupila r
            case 2:
                return FacialPointType.EN_R.ordinal();
            case 3:
                return FacialPointType.EN_L.ordinal();
            case 4:
                return FacialPointType.PUP_L.ordinal();//pupila l
            case 5:
                return FacialPointType.EX_L.ordinal();
            case 6:
                return FacialPointType.N.ordinal();
            case 7:
                return FacialPointType.AL_R.ordinal();
            case 8:
                return FacialPointType.AL_L.ordinal();
            case 9:
                return FacialPointType.LS.ordinal();
            case 10:
                return FacialPointType.STO.ordinal();
            case 11:
                return FacialPointType.LI.ordinal();
            case 12:
                return FacialPointType.CH_R.ordinal();
            case 13:
                return FacialPointType.CH_L.ordinal();
            case 14:
                return FacialPointType.ZY_R.ordinal();
            case 15:
                return FacialPointType.ZY_L.ordinal();
            case 16:
                return FacialPointType.GNA.ordinal();//gnathion
            case 17:
                return FacialPointType.GO2_R.ordinal();//gonion2 r
            case 18:
                return FacialPointType.GO2_L.ordinal();//goinon2 l
            case 19:
                return FacialPointType.T_R.ordinal();
            case 20:
                return FacialPointType.T_L.ordinal();
            case 21:
                return FacialPointType.V.ordinal();//vertex
            case 22:
                return FacialPointType.O.ordinal();//Ophryon
            case 23:
                return FacialPointType.RN_R.ordinal();//radix nasi r
            case 24:
                return FacialPointType.RN_L.ordinal();//radix nasi l
            case 25:
                return FacialPointType.EU2_R.ordinal();//Euryon II r
            case 26:
                return FacialPointType.EU2_L.ordinal();//Euryon II l
            case 27:
                return FacialPointType.RM_R.ordinal();//Ramus mandibulae r
            case 28:
                return FacialPointType.RM_L.ordinal();//Ramus mandibulae l
            case 29:
                return -1;//Lobulus auriculae r
            case 30:
                return -1;//Lobulus auriculae l
            default:
                return -1;
        }
    }
    
    public static TreeSet<FacialPointType> getUsedPoints() {
        TreeSet<FacialPointType> set = new TreeSet<>();
        set.add(FacialPointType.EX_R);
        set.add(FacialPointType.PUP_R);//pupila r
        set.add(FacialPointType.EN_R);
        set.add(FacialPointType.EN_L);
        set.add(FacialPointType.PUP_L);//pupila l
        set.add(FacialPointType.EX_L);
        set.add(FacialPointType.N);
        set.add(FacialPointType.AL_R);
        set.add(FacialPointType.AL_L);
        set.add(FacialPointType.LS);
        set.add(FacialPointType.STO);
        set.add(FacialPointType.LI);
        set.add(FacialPointType.CH_R);
        set.add(FacialPointType.CH_L);
        set.add(FacialPointType.ZY_R);
        set.add(FacialPointType.ZY_L);
        set.add(FacialPointType.GNA);//gnathion
        set.add(FacialPointType.GO2_R);//gonion2 r
        set.add(FacialPointType.GO2_L);//goinon2 l
        set.add(FacialPointType.T_R);
        set.add(FacialPointType.T_L);
        set.add(FacialPointType.V);//vertex
        set.add(FacialPointType.O);//Ophryon
        set.add(FacialPointType.RN_R);//radix nasi r
        set.add(FacialPointType.RN_L);//radix nasi l
        set.add(FacialPointType.EU2_R);//Euryon II r
        set.add(FacialPointType.EU2_L);//Euryon II l
        set.add(FacialPointType.RM_R);//Ramus mandibulae r
        set.add(FacialPointType.RM_L);//Ramus mandibulae l
        return set;
    }

    private void initializeModels(Model model, ArrayList<Model> gems, File imageFile, List<FacialPoint> points) {
        model.setName(imageFile.getName().substring(0, imageFile.getName().lastIndexOf(".")) + ".obj");
        for (FacialPoint p : points) {
            if (p.isActive()) {
                model.getVerts().add(p.getPosition());
                model.getNormals().add(p.getPosition());
                model.getTexCoords().add(p.getPosition());
            }
        }
        
        // assign materials because of DCEL construction...
        model.setMaterials(new Materials("materials.mtl", imageFile));
        Material mat = new Material("material0");
        mat.setTextureFile(imageFile.getAbsolutePath());
        model.getMatrials().getMatrials().add(mat);
        for(Model gem : gems) {
            Material gemMat = new Material("material0");
            gemMat.setTextureFile(gem.getDirectoryPath() + File.separator + "depth.png");
            gem.setMaterials(new Materials("materials.mtl", imageFile));
            gem.getMatrials().getMatrials().add(gemMat);
        }
    }

    private void initializeDelaunayMeshes(Model model, ArrayList<Model> gems) {
        // triangulate face model
        ArrayList<ArrayList<Integer>> triangulation = delaunay(model.getVerts());
        for (ArrayList<Integer> triangle : triangulation) {
            // add face to model
            model.getFaces().addFace(triangle, triangle);
            
            // add face to gem models
            for(Model gem : gems) {
                gem.getFaces().addFace(triangle, triangle);
            }
        }
    }

    private void subdivideUntilSatisfied(Model model, ArrayList<Model> gems, int numOfVerts) {
        // subdivide both models the same way until we have enough verts
        while(model.getVerts().size() < numOfVerts) {
            //loopSubdivision(model);
            //loopSubdivision(gem);
            loop2(model);
            for(Model gem : gems) {
                loop2(gem);
            }
        }
    }

    private void assignDepth(Model model, ArrayList<Model> gems, File imageFile, List<FacialPoint> points) {
        ArrayList<BufferedImage> gemImgs = new ArrayList<>(gems.size());
        for(Model gem : gems) {
            File gemImgFile = new File(gem.getMatrials().getMatrials().get(0).getTextureFile());
            BufferedImage gemImg = null;
            try {
                gemImg = ImageIO.read(gemImgFile);
                gemImgs.add(gemImg);
            } catch (IOException ex) {
                System.err.println("Could not load depth image from " + gemImgFile.getAbsolutePath());
            }
        }
        
        BufferedImage texImg = null;
        try {
            texImg = ImageIO.read(imageFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        for(FacialPoint p : points) {
            if (p.isActive()) {
                maxX = Math.max(maxX, p.getPosition().x);
                minX = Math.min(minX, p.getPosition().x);
            }
        }
        float factor = 1.5f * (maxX - minX);
        model.getTexCoords().clear();
        for (int i = 0; i < model.getVerts().size(); i++) {
            // compute and assign depth of point
            int summedDepth = 0;
            for(int j=0;j<gems.size();j++) {
                BufferedImage gemImg = gemImgs.get(j);
                Model gem = gems.get(j);
                
                Vector3f position = gem.getVerts().get(i);
                Color color = new Color(gemImg.getRGB((int) position.getX(), (int) (gemImg.getHeight() - position.getY())));
                summedDepth += color.getRed();
            }
            float averageDepth = ((float)summedDepth)/gems.size();
            model.getVerts().get(i).setZ((averageDepth / 255) * factor);
            
            // compute and assign texture coordinate of point
            Vector3f texCoord = new Vector3f();
            texCoord.x = model.getVerts().get(i).x / texImg.getWidth();
            texCoord.y = 1 - (model.getVerts().get(i).y / texImg.getHeight());
            texCoord.z = 0;
            model.getTexCoords().add(texCoord);
            
            // get min and max
            Vector3f v = model.getVerts().get(i);
            v.y = texImg.getHeight() - v.y;
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
            maxY = Math.max(maxY, v.y);
            minY = Math.min(minY, v.y);
        }
        
        // translate all points and scale
        double n = model.getVerts().size();
        Vector3f centroid = new Vector3f((float)(sumX/n), (float)(sumY/n), (float)(sumZ/n));
        float scale = 200/(maxY-minY);
        for(Vector3f v : model.getVerts()) {
            v.sub(centroid);
            v.scale(scale);
        }
    }

    private void adjustProportions(Model model, List<FacialPoint> points) {
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        
        for (FacialPoint p : points) {
            if (p.isActive()) {
                maxX = Math.max(maxX, p.getPosition().x);
                minX = Math.min(minX, p.getPosition().x);
            }
        }
        float factor = 1.5f * (maxX - minX);
        
        for(int i=0;i<model.getVerts().size();i++) {
            Vector3f v = model.getVerts().get(i);
            maxX = Math.max(maxX, v.x);
            minX = Math.min(minX, v.x);
            maxY = Math.max(maxY, v.y);
            minY = Math.min(minY, v.y);
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }
        
        // translate all points and scale
        double n = model.getVerts().size();
        Vector3f centroid = new Vector3f((float)(sumX/n), (float)(sumY/n), (float)(sumZ/n));
        float scale = 200/(maxY-minY);
        for(Vector3f v : model.getVerts()) {
            v.sub(centroid);
            v.scale(scale);
        }
    }

    private void postprocessModel(Model model) {
        // use material appropriately
        for(int i=0;i<model.getFaces().getNumFaces();i++) {
            model.getFaces().addMaterialUse(i, model.getMatrials().getMatrials().get(0).getName());
        }
        
        // recompute normals of the whole model
        model.setNormals((ArrayList<Vector3f>) SurfaceComparisonProcessing.instance().recomputeVertexNormals(model));
    }

    private class IncidentEdgeIterator implements Iterator<HalfEdge> {

        private final HalfEdge start;
        private HalfEdge next;

        public IncidentEdgeIterator(Vertex vert) {
            start = vert.getIncidentHalfEdge();
            next = start;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public HalfEdge next() {
            HalfEdge n = next;

            next = n.getTwin().getNext();

            if (next == start) {
                next = null;
            }

            return n;
        }
    }
}
