package cz.fidentis.merging.mesh;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdgeId;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class FacesOfMesh implements Iterable<MeshFace> {

    private final HashSet<MeshFace> faces = new HashSet<>();

    public FacesOfMesh() {
    }

    /**
     *
     * @param toCopy
     */
    public FacesOfMesh(FacesOfMesh toCopy) {
        faces.addAll(toCopy.faces);
    }

    /**
     *
     * @param toAdd
     */
    public void add(MeshFace toAdd) {
        faces.add(toAdd);
    }

    /**
     *
     * @return
     */
    public int[] getVertexIndeciesArray() {

        int[] newArray = new int[faces.size() * 3];
        int i = 0;
        if (newArray.length == 0) {
            return newArray;
        }
        try {

            for (MeshFace face : faces) {
                for (Integer indecies : face) {
                    newArray[i] = indecies;
                    i++;
                }
            }

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println(e + " " + i);
        }

        return newArray;
    }

    /**
     *
     * @param points
     */
    public void triangulateFor(PointsOfMesh points) {
        HashSet<MeshFace> newFaces = new HashSet<>();
        for (MeshFace face : faces) {
            switch (face.size()) {
                case 3:
                    newFaces.add(face);
                    break;
                case 4:
                    newFaces.addAll(face.triangulateQuad(points));
                    break;
                default:
                    throw new UnsupportedOperationException("triangulation");
            }
        }
        faces.clear();
        faces.addAll(newFaces);
    }

    @Override
    public Iterator<MeshFace> iterator() {
        return faces.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MeshFace collection : this) {
            for (Integer integer : collection) {
                sb.append(integer);
                sb.append(',');
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public boolean haveOnlyTriangels() {
        for (MeshFace collection : faces) {
            if (collection.size() != 3) {
                return false;
            }
        }
        return true;
    }

    public MeshFace createMeshFace(Material material) {
        MeshFace meshFace = new MeshFace(material);
        add(meshFace);
        return meshFace;
    }

    protected Iterable<MeshFace> split(MeshFace toSplit, MeshPoint meshPoint) {

        if (faces.remove(toSplit)) {
            return doSplit(toSplit, meshPoint);
        } else {
            return new LinkedList<MeshFace>();
        }

    }

    Collection<MeshFace> replace(AbstractFace face, MeshFaceTwinSplit twinSplit) {
        if (faces.remove(face.getMeshFace())) {
            return doSplit(face, twinSplit);
        } else {
            return new LinkedList<MeshFace>();
        }
    }

    private Iterable<MeshFace> doSplit(MeshFace toSplit, MeshPoint meshPoint) {
        LinkedList<MeshFace> newFaces = new LinkedList<>();
        Integer previosIndex = toSplit.getLastIndex();
        for (Integer currentIndex : toSplit) {
            MeshFace meshFace = createMeshFace(toSplit.getMaterial());
            meshFace.addMeshpointIndex(previosIndex);
            meshFace.addMeshpointIndex(currentIndex);
            meshFace.addMeshpointIndex(meshPoint.getIndex());
            newFaces.add(meshFace);
            previosIndex = currentIndex;
        }
        return newFaces;
    }

    private Collection<MeshFace> doSplit(AbstractFace toSplit, MeshFaceTwinSplit value) {
        LinkedList<MeshFace> newFaces = new LinkedList<>();
        for (HalfEdgeId meshPointIndices : value.getNew(toSplit)) {
            MeshFace meshFace = createMeshFace(toSplit.getMeshFace().getMaterial());
            meshFace.addMeshpointIndex(meshPointIndices.getFromIndex());
            meshFace.addMeshpointIndex(meshPointIndices.getToIndex());
            meshFace.addMeshpointIndex(value.getSplitingMeshPoint());
            newFaces.add(meshFace);
        }
        return newFaces;
    }

    void remove(MeshFace meshFace) {
        faces.remove(meshFace);
    }

    HashMap<Material, LinkedList<MeshFace>> getFacesByMaterial() {
        HashMap<Material, LinkedList<MeshFace>> facesByMaterial = new HashMap<>();
        for (MeshFace meshFace : faces) {
            Material material = meshFace.getMaterial();
            if (!facesByMaterial.containsKey(material)) {
                facesByMaterial.put(material, new LinkedList<MeshFace>());
            }
            facesByMaterial.get(material).add(meshFace);
        }
        return facesByMaterial;
    }

}
