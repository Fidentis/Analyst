package cz.fidentis.merging.file_parsing;

import cz.fidentis.merging.file_parsing.lines.obj.MeshFaceBuilder;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Material;
import cz.fidentis.merging.mesh.Vector3;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public abstract class AbstractGraphicMeshBuilder {

    private final ArrayList<Coordinates> vertices = new ArrayList<>();
    private final ArrayList<Vector3> normals = new ArrayList<>();
    private final ArrayList<Coordinates> textures = new ArrayList<>();
    private final HashMap<String, LinkedList<MeshFaceBuilder>> groups = new HashMap<>();
    private LinkedList<MeshFaceBuilder> currentGroup = new LinkedList<>();
    private final Deque<String> groupOrder = new LinkedList<>();
    private Material currentMaterial;
    private final HashMap<String, Material> materials = new HashMap<>();
    private final HashMap<Integer, Integer> positionIndexToMeshPointIndex = new HashMap<>();

    /**
     *
     * @param materialName
     */
    public void setCurrentMaterialMaterial(String materialName) {
        currentMaterial = materials.get(materialName);
    }

    public Material getCurrentMaterial() {
        return currentMaterial;
    }

    public void addMapping(Integer positionIndex, Integer meshPointIndex) {
        positionIndexToMeshPointIndex.put(positionIndex, meshPointIndex);
    }

    public boolean mappingExists(Integer positionIndex) {
        return positionIndexToMeshPointIndex.containsKey(positionIndex);
    }

    public Iterable<MeshFaceBuilder> getGroup(String group) {
        return groups.get(group);
    }

    public Deque<String> getGroupsByInsertionOrder() {
        return groupOrder;
    }

    public Integer getMapping(Integer positionIndex) {
        return positionIndexToMeshPointIndex.get(positionIndex);
    }

    /**
     *
     * @param coords
     */
    public void addVertex(final Coordinates coords) {
        vertices.add(coords);
    }

    /**
     *
     * @param coords
     */
    public void addNormal(final Vector3 coords) {
        normals.add(coords);
    }

    /**
     *
     * @param coords
     */
    public void addTextureCoord(final Coordinates coords) {
        textures.add(coords);
    }

    public Vector3 getNormal(Integer normalIndex) {
        return normals.get(normalIndex);
    }

    public Coordinates getPosition(Integer positionIndex) {
        return vertices.get(positionIndex);
    }

    public Coordinates getTextures(Integer textureIndex) {
        return textures.get(textureIndex);
    }

    /**
     *
     * @param group
     */
    public void addGroup(final String group) {
        if (!groups.containsKey(group)) {
            groups.put(group, new LinkedList<MeshFaceBuilder>());
            groupOrder.addLast(group);
        }
        currentGroup = groups.get(group);
    }

    /**
     *
     * @param builder
     */
    public void addVertexIndecies(final MeshFaceBuilder builder) {
        builder.setMaterial(currentMaterial);
        currentGroup.add(builder);
    }

    public final void addMaterial(String name, Material material) {
        materials.put(name, material);
    }

}
