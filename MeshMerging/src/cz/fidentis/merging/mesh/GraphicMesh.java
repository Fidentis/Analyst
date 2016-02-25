package cz.fidentis.merging.mesh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.file_parsing.AbstractGraphicMeshBuilder;
import cz.fidentis.merging.file_parsing.lines.obj.MeshFaceBuilder;
import cz.fidentis.merging.file_parsing.lines.obj.PointIndexing;
import cz.fidentis.merging.storing.PointIndexWithFace;
import cz.fidentis.model.Faces;
import cz.fidentis.model.Materials;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class GraphicMesh {

    private final MeshBuffer buffer;
    private final PointsOfMesh points;
    private final FacesOfMesh meshFaces;
    private boolean firstTime = true;

    public GraphicMesh(AbstractGraphicMeshBuilder builder) {
        this(builder, builder.getGroupsByInsertionOrder().getFirst());
    }

    public GraphicMesh(AbstractGraphicMeshBuilder builder, String groupName) {
        this.points = new PointsOfMesh();
        meshFaces = new FacesOfMesh();
        buffer = new MeshBuffer(this);
        populateFrom(builder, groupName);
    }

    private void populateFrom(AbstractGraphicMeshBuilder builder, String groupName) {
        for (MeshFaceBuilder faceIndexing : builder.getGroup(groupName)) {
            pupulateForFace(faceIndexing, builder);
        }
    }

    private void pupulateForFace(MeshFaceBuilder faceIndexing, AbstractGraphicMeshBuilder builder) {
        MeshFace newMeshFace = createMeshFace(faceIndexing.getMaterial());
        for (PointIndexing pointIndexing : faceIndexing) {
            Integer positionIndex = pointIndexing.getPositionIndex();
            Integer meshPointIndex;
            meshPointIndex = getMeshPoint(builder, positionIndex);
            newMeshFace.addMeshpointIndex(meshPointIndex);
            MeshPoint meshPoint = points.getMeshPoint(meshPointIndex);
            Integer normalIndex = pointIndexing.getNormalIndex();
            Vector3 normal = builder.getNormal(normalIndex);
            meshPoint.updateNormal(normal);

            Integer textureIndex = pointIndexing.getTextureIndex();
            if (textureIndex >= 0) {
                meshPoint.addTexture(builder.getTextures(textureIndex), newMeshFace);
            }

        }
    }

    private Integer getMeshPoint(AbstractGraphicMeshBuilder builder, Integer positionIndex) {
        Integer meshPointIndex;
        if (!builder.mappingExists(positionIndex)) {
            Coordinates position = builder.getPosition(positionIndex);
            meshPointIndex = createMeshPoint(position);
            builder.addMapping(positionIndex, meshPointIndex);
        } else {
            meshPointIndex = builder.getMapping(positionIndex);
        }
        return meshPointIndex;
    }

    /**
     *
     * @param openGl
     */
    public final synchronized void draw(final GL2 openGl) {
        if (firstTime) {
            firstTime = false;
            reBuffer();
        }
        buffer.draw(openGl);
    }

    public void triangulate() {
        meshFaces.triangulateFor(points);
    }

    /**
     *
     * @return
     */
    public final FacesOfMesh getMeshFaces() {
        return meshFaces;
    }

    /**
     *
     * @return
     */
    public final PointsOfMesh getPoints() {
        return points;
    }

    /**
     *
     * @return
     */
    public final int getVerticesCount() {
        return points.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public final Vector3 getNormal(final int index) {
        return points.getMeshPoint(index).getNormal();
    }

    /**
     *
     * @param index
     * @return
     */
    public final Coordinates getPosition(final int index) {
        return points.getMeshPoint(index).getPosition();
    }

    /**
     *
     */
    public synchronized final void reBuffer() {
        buffer.reBuffer();
    }

    /**
     *
     * @param index
     * @return
     */
    public MeshPoint getMeshPoint(final int index) {
        return points.getMeshPoint(index);
    }

    /**
     *
     * @param index
     * @param movement
     */
    public void moveVertex(int index, Coordinates movement) {
        points.moveOnIndex(index, movement);
    }

    /**
     *
     * @return
     */
    public Coordinates getWeight() {
        return points.getCenter();
    }

    public void updateVertexColor(int index) {
        points.defaultFaceColor(index);
    }

    public void addTextures(int index, Coordinates texture, MeshFace meshFace) {
        points.getMeshPoint(index).addTexture(texture, meshFace);
    }

    public Integer createMeshPoint(Coordinates coordinates, Vector3 normal) {
        MeshPoint meshpoint = points.createMeshPoint(coordinates, normal);
        return meshpoint.getIndex();
    }

    private Integer createMeshPoint(Coordinates position) {
        MeshPoint meshpoint;
        meshpoint = points.createMeshPoint(position);
        return meshpoint.getIndex();
    }

    public MeshFace createMeshFace(Material material) {
        return meshFaces.createMeshFace(material);
    }

    public boolean contains(Integer vertexIndex) {
        return points.contains(vertexIndex);
    }

    public Iterable<MeshFace> split(MeshFace toSplit, Integer splitIndex) {
        MeshPoint splitPoint = points.getMeshPoint(splitIndex);
        Iterable<MeshFace> inserted;
        inserted = meshFaces.split(toSplit, splitPoint);
        newTexturing(inserted, toSplit);
        removeOldTexturing(toSplit, splitPoint);
        return inserted;
    }

    private void removeOldTexturing(MeshFace toSplit, MeshPoint splitPoint) {
        removeOldTexturing(toSplit);
        splitPoint.removeTexturesOf(toSplit);
    }

    private void removeOldTexturing(MeshFace toSplit) {
        if (toSplit == null) {
            return;
        }
        for (Integer meshPointIndex : toSplit) {
            MeshPoint meshPoint = points.getMeshPoint(meshPointIndex);
            meshPoint.removeTexturesOf(toSplit);
        }
    }

    private void newTexturing(Iterable<MeshFace> inserted, MeshFace toSplit) {
        for (MeshFace created : inserted) {
            for (Integer meshPointIndex : created) {
                MeshPoint meshPoint = points.getMeshPoint(meshPointIndex);
                meshPoint.copyTexturesCoordinates(toSplit, created);
            }
        }
    }

    public Iterable<MeshFace> split(MeshFaceTwinSplit twinSplit) {
        LinkedList<MeshFace> newFaces = new LinkedList<>();
        MeshPoint spliter = points.getMeshPoint(twinSplit.getSplitingMeshPoint());
        for (AbstractFace face : twinSplit) {
            MeshFace toSplit = face.getMeshFace();
            Collection<MeshFace> inserted = meshFaces.replace(face, twinSplit);
            newTexturing(inserted, toSplit);
            removeOldTexturing(toSplit, spliter);
            newFaces.addAll(inserted);
        }
        return newFaces;
    }

    public void remove(MeshFace meshFace) {

        removeOldTexturing(meshFace);

        meshFaces.remove(meshFace);
    }

    public HashMap<Material, LinkedList<MeshFace>> getFacesByMaterials() {
        return meshFaces.getFacesByMaterial();
    }

    public Model toModel(Materials materials) {
        Model model = new Model();        
        HashMap<Integer, Integer> newOrdering = addPoints(model);
        model.getModelDims().setBoundingBox(model.getModelDims().getCentralizedBoundingBox());     //create bounding box
        
        HashMap<PointIndexWithFace, Integer> textureIndex;
        textureIndex = addTextureCoordinates(model);
        model.setMaterials(materials);
        addFaces(newOrdering, textureIndex, model);
        return model;
    }

    private void addFaces(HashMap<Integer, Integer> newOrdering, HashMap<PointIndexWithFace, Integer> textureIndex, Model model) {
        HashMap<Material, LinkedList<MeshFace>> facesByMaterial;
        facesByMaterial = getFacesByMaterials();
        int faceCounter = 0;
        for (Material material : facesByMaterial.keySet()) {

            for (MeshFace face : facesByMaterial.get(material)) {
                ArrayList<Integer> vertInsdecies = new ArrayList<>();
                ArrayList<Integer> texInsdecies = new ArrayList<>();
                for (Integer pointIndex : face) {
                    vertInsdecies.add(newOrdering.get(pointIndex) + 1);
                    PointIndexWithFace pointIndexWithFace;
                    pointIndexWithFace = new PointIndexWithFace(pointIndex, face);
                    Integer textCoord = textureIndex.get(pointIndexWithFace);
                    if (textCoord == null) {
                        texInsdecies.add(0);
                    } else {
                        texInsdecies.add(textCoord + 1);
                    }
                }
                Faces faces = model.getFaces();
                faces.addFace(vertInsdecies, texInsdecies);
                faces.addMaterialUse(faceCounter, material.getMaterialName());
                faceCounter++;
            }
        }
    }

    private HashMap<PointIndexWithFace, Integer> addTextureCoordinates(Model model) {
        ArrayList<Vector3f> textures = new ArrayList<>();
        Integer textureCounter = 0;
        HashMap<PointIndexWithFace, Integer> textureIndex = new HashMap<>();
        for (MeshPoint point : getPoints()) {
            for (Map.Entry<MeshFace, Coordinates> entrySet : point.getAllTextures().entrySet()) {
                float[] tex = entrySet.getValue().asFloatArray();
                textures.add(new Vector3f(tex));

                PointIndexWithFace key;
                key = new PointIndexWithFace(point.getIndex(), entrySet.getKey());

                textureIndex.put(key, textureCounter);
                textureCounter++;

            }
        }
        model.setTextures(textures);
        return textureIndex;
    }

    private HashMap<Integer, Integer> addPoints(Model model) {
        HashMap<Integer, Integer> newOrdering = new HashMap<>();
        Integer counter = 0;
        ArrayList<Vector3f> vertecies = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        
        boolean isFirst = true;
        
        for (MeshPoint point : getPoints()) {
            float[] possition = point.getPosition().asFloatArray();
            vertecies.add(new Vector3f(possition));
            
            //to create bounding box 
            if(isFirst){
                isFirst = false;
                model.getModelDims().set(vertecies.get(vertecies.size() - 1));
            } else {
                model.getModelDims().update(vertecies.get(vertecies.size() - 1));
            }
            
            float[] normal = point.getNormal().asFloatArray();
            normals.add(new Vector3f(normal));
            newOrdering.put(point.getIndex(), counter);
            counter++;
        }
        model.setVerts(vertecies);
        model.setNormals(normals);
        return newOrdering;
    }

}
