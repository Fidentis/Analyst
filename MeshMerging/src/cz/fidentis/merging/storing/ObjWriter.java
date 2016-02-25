package cz.fidentis.merging.storing;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.Material;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.MeshPoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author matej
 */
public class ObjWriter extends AbstractWriter {

    private final MtlWriter mtlWriter;
    private final String name;
    private HashMap<Integer, Integer> newOrdering;
    private HashMap<PointIndexWithFace, Integer> indexInFile;

    public ObjWriter(String file) throws IOException {
        super(file + ".obj");
        mtlWriter = new MtlWriter(file);
        name = file;
    }

    public synchronized void write(GraphicMesh mesh) throws IOException {
        addLine(format("mtllib", name + ".mtl"));
        addLine("");
        addPositions(mesh);
        addNormals(mesh);
        addTextures(mesh);

        HashMap<Material, LinkedList<MeshFace>> facesByMaterial;
        facesByMaterial = mesh.getFacesByMaterials();
        for (Material material : facesByMaterial.keySet()) {
            addLine(format("usemtl", material.getMaterialName()));
            mtlWriter.append(material);
            for (MeshFace face : facesByMaterial.get(material)) {
                addFace(face);
            }
        }
        mtlWriter.close();
        close();
    }

    private void addFace(MeshFace face) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Integer pointIndex : face) {
            Integer newOrder = newOrdering.get(pointIndex);
            sb.append(newOrder);
            sb.append('/');
            PointIndexWithFace pointIndexWithFace;
            pointIndexWithFace = new PointIndexWithFace(pointIndex, face);

            Integer textureIndex = indexInFile.get(pointIndexWithFace);
            sb.append(textureIndex);
            sb.append('/');
            sb.append(newOrder);
            sb.append(' ');
        }
        addLine(format("f", sb.toString()));
    }

    private HashMap<PointIndexWithFace, Integer> addTextures(GraphicMesh mesh) throws IOException {
        Integer textureCounter = 0;
        indexInFile = new HashMap<>();
        for (MeshPoint point : mesh.getPoints()) {
            for (Map.Entry<MeshFace, Coordinates> entrySet : point.getAllTextures().entrySet()) {
                PointIndexWithFace key = new PointIndexWithFace(point.getIndex(), entrySet.getKey());
                textureCounter++;
                indexInFile.put(key, textureCounter);
                addLine(format("vt", entrySet.getValue().asArray()));
            }
        }
        return indexInFile;
    }

    private void addNormals(GraphicMesh mesh) throws IOException {
        addLine("");
        for (MeshPoint point : mesh.getPoints()) {
            addLine(format("vn", point.getNormal().asArray()));
        }
    }

    private HashMap<Integer, Integer> addPositions(GraphicMesh mesh) throws IOException {
        newOrdering = new HashMap<>();
        Integer counter = 0;
        for (MeshPoint point : mesh.getPoints()) {
            addLine(format("v", point.getPosition().asArray()));
            counter++;
            newOrdering.put(point.getIndex(), counter);
        }
        return newOrdering;
    }

}
