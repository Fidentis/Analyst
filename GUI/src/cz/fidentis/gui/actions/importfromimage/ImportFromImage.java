/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.actions.importfromimage;

import cz.fidentis.comparison.icp.KdTreeIndexed;
import cz.fidentis.comparison.procrustes.Procrustes1ToMany;
import cz.fidentis.controller.Gender;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.gui.GUIController;
import cz.fidentis.gui.ProjectTopComponent;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import cz.fidentis.model.Faces;
import cz.fidentis.model.Material;
import cz.fidentis.model.Materials;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelExporter;
import cz.fidentis.model.ModelLoader;
import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import cz.fidentis.processing.comparison.surfaceComparison.SurfaceComparisonProcessing;
import cz.fidentis.utils.MathUtils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    
    private final float HEIGHT_TO_FIT = 200; // imported models will be scaled to this height
    
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
                Model imported = importFromImage(dialog.getImageFile(), dialog.getSelectedGender(), dialog.getSelectedAge(), points, dialog.getNumberOfVerts(), dialog.getNumOfClosestDepthMaps());             
                
                ModelExporter exp = new ModelExporter(imported);
                File modelSaveDir = new File(tc.getProject().getTempDirectory().getPath() + File.separator + imported.getName().substring(0, imported.getName().length()-4));
                File modelFile = new File(modelSaveDir.getPath() + File.separator + imported.getName());
                exp.exportModelToObj(modelSaveDir, true);
                
                Model model = ModelLoader.instance().loadModel(modelFile, true, true);
                
                //project landmarks to centralized model
                List<FacialPoint> landmarksIn3D = null;
                if(dialog.projectLandmarks())
                    landmarksIn3D = projectLandmarksTo3D(model, points, dialog.getImageFile());
                
                switch (tc.getProject().getSelectedPart()) {
                    case 2:
                        if (dialog.isPrimary()) {
                            tc.getViewerPanel_2Faces().getListener1().setModels(model);
                            tc.getViewerPanel_2Faces().getCanvas1().setImportLabelVisible(false);
                            tc.getProject().getSelectedComparison2Faces().setModel1(model);
                            
                            //set landmarks
                            if(landmarksIn3D != null){
                                tc.getViewerPanel_2Faces().getListener1().setFacialPoints(landmarksIn3D);
                                tc.getProject().getSelectedComparison2Faces().setMainFp(landmarksIn3D);
                                tc.getViewerPanel_2Faces().checkFpAvaibility();
                            }
                        } else {
                            tc.getViewerPanel_2Faces().getListener2().setModels(model);
                            tc.getViewerPanel_2Faces().getCanvas2().setImportLabelVisible(false);
                            tc.getProject().getSelectedComparison2Faces().setModel2(model);
                            
                             //set landmarks
                            if(landmarksIn3D != null){
                                tc.getViewerPanel_2Faces().getListener2().setFacialPoints(landmarksIn3D);
                                tc.getProject().getSelectedComparison2Faces().setSecondaryFp(landmarksIn3D);
                                tc.getViewerPanel_2Faces().checkFpAvaibility();
                            }
                        }
                        break;
                    case 3:
                        if (dialog.isPrimary()) {
                            tc.getOneToManyViewerPanel().getListener1().setModels(model);
                            tc.getOneToManyViewerPanel().getCanvas1().setImportLabelVisible(false);
                            tc.getProject().getSelectedOneToManyComparison().setPrimaryModel(model);
                            
                             //set landmarks
                            if(landmarksIn3D != null){
                                tc.getOneToManyViewerPanel().getListener1().setFacialPoints(landmarksIn3D);
                            }
                        } else {
                            tc.getOneToManyViewerPanel().getListener2().setModels(model);
                            tc.getOneToManyViewerPanel().getCanvas2().setImportLabelVisible(false);
                            tc.getProject().getSelectedOneToManyComparison().addModel(modelFile);
                            
                            //set landmarks
                            if(landmarksIn3D != null){
                                tc.getOneToManyViewerPanel().getListener2().setFacialPoints(landmarksIn3D);
                            }
                        }
                        
                        if(landmarksIn3D != null){
                            tc.getProject().getSelectedOneToManyComparison().addFacialPoints(imported.getName(),landmarksIn3D);
                            tc.getOneToManyViewerPanel().checkFpAvailable();
                        }
                            
                        
                        break;
                    case 4:
                        tc.getViewerPanel_Batch().getListener().setModels(model);
                        tc.getViewerPanel_Batch().getCanvas1().setImportLabelVisible(false);
                        tc.getProject().getSelectedBatchComparison().addModel(modelFile);
                        
                        if(landmarksIn3D != null){
                            tc.getViewerPanel_Batch().getListener().setFacialPoints(landmarksIn3D);
                            tc.getProject().getSelectedBatchComparison().addFacialPoints(imported.getName(), landmarksIn3D);
                            tc.getViewerPanel_Batch().checkFpAvaialable();
                        }
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
    
    /**
     * 
     * @param imageFile the image of a face to be imported
     * @param targetGender the gender of a person whose face is to be imported
     * @param targetAge the age category of a person whose face is to be imported
     * @param points facial landmarks defined on the image in the XY plane
     * @param numOfVerts number of vertices the imported face should consist of
     * @return a mesh reconstructed from the given image
     */
    public Model importFromImage(File imageFile, Gender targetGender, AgeCategories targetAge, List<FacialPoint> points, int numOfVerts, int numOfClosestDepthMaps) {
        // 1. load suitable gem models
        ArrayList<Model> gems = getSuitableGems(targetGender, targetAge, points, numOfClosestDepthMaps);
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
        adjustProportions(model);
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
            for(int i=0;i<3;i++) {
                indexList.add(t.getVertex(i).index+1);
            }
            result.add(indexList);
        }
        return result;
    }
    
    /**
     * Performs a Loop Subdivision algorithm on given model by splitting each
     * triangle of the model to four triangles by adding a new vertex on each
     * edge.
     * @param model model to subdivide by one level.
     */
    private void loopSubdiv(Model model) {
        model.setCornerTable(new CornerTable(model));
        HashMap<HalfEdgeId, Integer> splitInfo = new HashMap<>();
        ArrayList<Vector3f> newEvenPositions = new ArrayList<>(model.getVerts().size());
        ArrayList<Integer> midTriangle = null;
        ArrayList<Integer> currentTriangle = null;
        ArrayList<ArrayList<Integer>> newFaces = new ArrayList<>(model.getFaces().getNumFaces()*4);
        
        // get updated positions of initial vertices but dont apply them yet
        for(int i=0;i<model.getVerts().size(); i++){
            newEvenPositions.add(getUpdatedPosition(i, model));
        }
        
        // for each face of the model
        for(int i=0;i<model.getFaces().getNumFaces();i++) {
            int[] face = model.getFaces().getFaceVertIdxs(i);
            // for each edge of the face
            for(int j=0;j<face.length;j++) {
                // find out whether the edge was split already
                HalfEdgeId id = new HalfEdgeId(face[j], face[(j+1)%face.length]);
                if(!splitInfo.containsKey(id)) {
                    // if the edge was not split yet, split it by adding a new vertex
                    int idx = model.getVerts().size();
                    Vector3f splitPoint = getSplitPosition(id.getFromIndex()-1, id.getToIndex()-1, i, model);
                    // add the new vertex to the model
                    model.getVerts().add(splitPoint);
                    model.getNormals().add(splitPoint);
                    model.getTexCoords().add(splitPoint);
                    // mark that this edge was already split
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
        
        // after new vertices and faces are added, update positions of existing
        // vertices determined earlier and then correct model's list of faces
        for(int i=0;i<newEvenPositions.size();i++) {
            model.getVerts().set(i, newEvenPositions.get(i));
        }
        model.getFaces().clearFaces();
        for(ArrayList<Integer> face : newFaces) {
            model.getFaces().addFace(face, face);
        }
    }
    
    /**
     * Gets the position of a new vertex created by splitting an edge in Loop
     * subdivision.
     * @param index1 index of the first vertex of the edge to split
     * @param index2 index of the second vertex of the edge to split
     * @param triangleIdx index of the triangle that the split edge belongs to
     * @param model model that is being subdivided
     * @return a position of the new vertex on the edge that was split.
     */
    private Vector3f getSplitPosition(int index1, int index2, int triangleIdx, Model model) {
        Vector3f v1 = model.getVerts().get(index1);
        Vector3f v2 = model.getVerts().get(index2);
        Vector3f v3;
        Vector3f v4 = null;
        
        // find the triangle in the corner table
        CornerTable ct = model.getCornerTable();
        Corner triangleCorner = ct.getCorner(triangleIdx);
        // find the corner opposite to the edge in the triangle
        Corner oppositeToEdge = null;
        Corner[] n = triangleCorner.triangleCorners();
        for(Corner c : n) {
            if(c.vertex != index1 && c.vertex != index2) {
                oppositeToEdge = c;
            }
        }
        v3 = model.getVerts().get(oppositeToEdge.vertex);
        // find the vertex opposite to the edge on the other side of the edge if possible
        if(oppositeToEdge.opposite != null) {
            v4 = model.getVerts().get(oppositeToEdge.opposite.vertex);
        }

        Vector3f result;
        if (v4 == null) {
            // the edge is on boundary, just get the middle
            result = new Vector3f(v1);
            result.add(v2);
            result.scale(0.5f);
            return result;
        } else {
            // edge has two opposite vertices, use the formula to get position
            Vector3f ab = new Vector3f(v1); ab.add(v2); ab.scale(3.0f/8.0f);
            Vector3f cd = new Vector3f(v3); cd.add(v4); cd.scale(1.0f/8.0f);
            ab.add(cd);
            return ab;
        }
    }
    
    /**
     * Gets the updated position of an existing vertex of a model according to
     * smoothing of vertices in Loop Subdivision.
     * @param vertexIdx index of the vertex.
     * @param model model being subdivided.
     * @return position where to move the vertex to make subdivision smooth.
     */
    private Vector3f getUpdatedPosition(int vertexIdx, Model model) {
        CornerTable ct = model.getCornerTable();
        Corner vertexCorner = ct.getCornerByVertexId(vertexIdx);
        TreeSet<Integer> neighbors = new TreeSet<>(); // neighboring vertices
        ArrayList<Vector3f> boundNeighbors = new ArrayList<>(); // mesh bound edges
        
        // search neighborhood for neighboring vertices
        for(Integer i : vertexCorner.adjacentTriangles()) {
            Corner triangleCorner = ct.getCorner(i);
            for(Corner c : triangleCorner.triangleCorners()) {
                if(c.next.vertex == vertexIdx) {
                    neighbors.add(c.vertex);
                    if(c.opposite == null) {
                        // if the neighbor has no opposite, it means the vertex is on boundary
                        boundNeighbors.add(model.getVerts().get(c.next.next.vertex));
                    }
                } else if(c.prev.vertex == vertexIdx) {
                    neighbors.add(c.vertex);
                    if(c.opposite == null) {
                        // if the neighbor has no opposite, it means the vertex is on boundary
                        boundNeighbors.add(model.getVerts().get(c.prev.prev.vertex));
                    }
                }
            }
        }
        
        Vector3f result = new Vector3f(model.getVerts().get(vertexIdx));
        if(boundNeighbors.size() > 0) {
            // if vertex is on mesh boundary
            Vector3f v1 = boundNeighbors.get(0);
            Vector3f v2 = boundNeighbors.get(1);
            
            result.scale(6);
            result.add(v1);
            result.add(v2);
            result.scale(1.0f/8.0f);
        } else {
            // if the vertex is not on boundary, perform weight averaging of neighbors
            float beta;
            if (neighbors.size() <= 3) {
                beta = 3.0f / 16.0f;
            } else {
                beta = 3.0f / (neighbors.size() * 8.0f);
            }
            
            Vector3f sum = new Vector3f(0, 0, 0);
            for(Integer neighborIdx : neighbors) {
                sum.add(model.getVerts().get(neighborIdx));
            }
            sum.scale(beta);
            
            result.scale(1 - (neighbors.size() * beta));
            result.add(sum);
        }
        
        return result;
    }
    
    /**
     * Finds and loads available depth models that match given parameters of the
     * face. Models to be used will have the same gender and age category as the
     * input parameters.
     * @param gender gender of a face to be imported
     * @param age age of a face to be imported
     * @param points feature points of the face to be imported
     * @return list of depth models that match given parameters. Each model will
     * have all vertices that correspond to given facial points
     */
    private ArrayList<Model> getSuitableGems(Gender gender, AgeCategories age, List<FacialPoint> points, int numOfClosestGems) {
        ArrayList<Model> gems = new ArrayList<>();
        ArrayList<List<FacialPoint>> fps = new ArrayList<>();

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
                        List<FacialPoint> pointsCopy = new ArrayList<>(gemPoints.size());
                        for (FacialPoint p : gemPoints) {
                            pointsCopy.add(new FacialPoint(p));
                        }
                        fps.add(pointsCopy);

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

        // now from the depth maps selected by age and gender select at most N
        // most similar faces to the input
        // if there is not enoug GEMs selected just use all
        if (numOfClosestGems >= gems.size()) {
            return gems;
        }
        // otherwise limit the selected depth maps by procrustes distance:
        Procrustes1ToMany procrustes = new Procrustes1ToMany(points, fps, true);
        // register the considered depth maps to the input feature points
        procrustes.align1withN();
        // compute the procrustes distances of depth models to the input face
        List<Double> distances = procrustes.compare1WithN(0.05f);
        // sort the result to be able to find out the max distance to be accepted
        List<Double> sorted = new ArrayList<>(distances);
        Collections.sort(sorted);
        double max = sorted.get(numOfClosestGems);
        // make a list of GEMs closer than the max distance, which should get N models
        ArrayList<Model> result = new ArrayList<>();
        for (int i = 0; i < gems.size(); i++) {
            if (distances.get(i) < max) {
                result.add(gems.get(i));
            }
            // when we have N models already selected we can finish. Also, this
            // check may handle the case when a lot of models have the same distance
            if (result.size() == numOfClosestGems) {
                break;
            }
        }

        return result;
    }

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
            Exceptions.printStackTrace(e);
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
    
    /**
     * Returns a set of facial points that are available on depth maps that are
     * used to reconstruct face from image. Any subset of these points may be used
     * as input to reconstruction.
     * @return set of points that may be used as input to reconstruction of face
     * from image
     */
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

    /**
     * The function will initialize meshes of given model and a list of depth models
     * used in reconstruction. First, a delaunay triangulation is done on the vertices
     * of model mesh. Then, the same triangulation is made on each depth model to
     * make correspondence of triangles on all models. The models must have vertices
     * already initialized to match the feature points of the face to be reconstructed.
     * @param model model with vertices being the feature points of reconstructed face
     * @param gems list of depth models to be used in reconstruction.
     */
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
            loopSubdiv(model);
            for(Model gem : gems) {
                loopSubdiv(gem);
            }
        }
    }

    private void assignDepth(Model model, ArrayList<Model> gems, File imageFile, List<FacialPoint> points) {
        // read depth map imagess for each depth model
        ArrayList<BufferedImage> gemImgs = new ArrayList<>(gems.size());
        for(Model gem : gems) {
            File gemImgFile = new File(gem.getMatrials().getMatrials().get(0).getTextureFile());
            BufferedImage gemImg;
            try {
                gemImg = ImageIO.read(gemImgFile);
                gemImgs.add(gemImg);
            } catch (IOException ex) {
                System.err.println("Could not load depth image from " + gemImgFile.getAbsolutePath() + ": " + ex.getMessage());
            }
        }
        
        // load the input image that will be used as a texture to imported model
        BufferedImage texImg;
        try {
            texImg = ImageIO.read(imageFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        
        // compute the width of the face to be able to adjust depth to it
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        for(FacialPoint p : points) {
            if (p.isActive()) {
                maxX = Math.max(maxX, p.getPosition().x);
                minX = Math.min(minX, p.getPosition().x);
            }
        }
        float factor = (maxX - minX);
        model.getTexCoords().clear();
        for (int i = 0; i < model.getVerts().size(); i++) {
            Vector3f currentVertex = model.getVerts().get(i);
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
            currentVertex.setZ((averageDepth / 255) * factor);
            
            // compute and assign texture coordinate of point
            Vector3f texCoord = new Vector3f();
            texCoord.x = model.getVerts().get(i).x / texImg.getWidth();
            texCoord.y = 1 - (model.getVerts().get(i).y / texImg.getHeight());
            texCoord.z = 0;
            model.getTexCoords().add(texCoord);
            
            // finally flip Y axis because of different origin of coordinates
            currentVertex.y = texImg.getHeight() - currentVertex.y;
        }
    }

    /**
     * Adjusts proportions of the model to make it have some normalised size.
     * This is needed mainly to transfer the model from the XY coordinates of
     * input image to the 3D coordinates of the 3D face (and the coordinates
     * which our application uses).
     * @param model model of face reconstructed from image.
     */
    private void adjustProportions(Model model) {
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        
        // compute bounds of all vertices of model
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
        
        // compute centroid of the model to be able to translate center to zero
        double n = model.getVerts().size();
        Vector3f centroid = new Vector3f((float)(sumX/n), (float)(sumY/n), (float)(sumZ/n));

        // translate the model's center to zero coordinates and scale model to
        // some reasonable size
        float scale = HEIGHT_TO_FIT/(maxY-minY);
        for(Vector3f v : model.getVerts()) {
            v.sub(centroid);
            v.scale(scale);
        }
    }

    /**
     * Does some final adjustments needed to be done on model after it is created
     * in order to make it usable in the whole program. (e.g. compute normals,
     * set up some default materials etc).
     * @param model model of face reconstructed from image.
     */
    private void postprocessModel(Model model) {
        // use material appropriately
        for(int i=0;i<model.getFaces().getNumFaces();i++) {
            model.getFaces().addMaterialUse(i, model.getMatrials().getMatrials().get(0).getName());
        }
        
        // recompute normals of the whole model
        model.setNormals((ArrayList<Vector3f>) SurfaceComparisonProcessing.instance().recomputeVertexNormals(model));
    }
    
    private List<FacialPoint> projectLandmarksTo3D(Model m, List<FacialPoint> landmarksIn2D, File imageFile) {
        List<FacialPoint> projectedLandmarks = new ArrayList<>(landmarksIn2D.size());
        HashMap<Integer, Integer> textureVertexCorrespondence = textureVertexCorrespondence(m.getFaces());
        KdTreeIndexed faceTexTree = new KdTreeIndexed(m.getTexCoords());

        try {
            BufferedImage img = ImageIO.read(imageFile);
 
            for (FacialPoint fp : landmarksIn2D) {
                Vector3f pos = findClosest3Dvertex(faceTexTree, m.getVerts(), textureVertexCorrespondence, fp.getPosition(), img.getWidth(), img.getHeight());
                projectedLandmarks.add(new FacialPoint(fp.getType(), pos));
            }
        } catch (IOException ex) {
            System.err.println("Could not load depth image from " + imageFile.getAbsolutePath() + ": " + ex.getMessage());
        }

        return projectedLandmarks;
    }
    
    private HashMap<Integer, Integer> textureVertexCorrespondence(Faces faces){
        HashMap<Integer, Integer> corr = new HashMap<>();
        int numOfFaces = faces.getNumFaces();
        
        for(int i = 0; i < numOfFaces; i++){
            int[] textureIndices = faces.getFaceTexIdxs(i);
            int[] vertexIndices = faces.getFaceVertIdxs(i);
            
            for(int j = 0; j < textureIndices.length; j++){
                if(corr.containsKey(textureIndices[j] - 1))
                    continue;
                
                corr.put(textureIndices[j] - 1, vertexIndices[j] - 1);
            }
        }
        
        return corr;
    }
    
    private Vector3f findClosest3Dvertex(KdTreeIndexed faceTxtTree, List<Vector3f> vertices, HashMap<Integer, Integer> corr, Vector3f landmark, int imageWidth, int imageHeight){        
        // needs to flip Y axis due to opengl texture mapping process
        Vector3f mapped = new Vector3f(landmark.x / imageWidth, (imageHeight - landmark.y) / imageHeight, landmark.z);
        
        int closestIndex = faceTxtTree.nearestIndex(mapped);
        
        return vertices.get(corr.get(closestIndex));
    }
    
}
