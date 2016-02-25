package cz.fidentis.merging.mesh;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class MeshFace implements Iterable<Integer> {

    private final LinkedList<Integer> meshPointIndecies = new LinkedList<>();
    private final Material material;

    protected MeshFace(Material material) {
        this.material = material;
    }

    private MeshFace(Material material, Integer a, Integer b, Integer c) {
        this(material);
        meshPointIndecies.add(a);
        meshPointIndecies.add(b);
        meshPointIndecies.add(c);
    }

    protected MeshFace(Material material, Iterable<Integer> indecies) {
        this(material);
        for (Integer index : indecies) {
            addMeshpointIndex(index);
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return meshPointIndecies.iterator();
    }

    int size() {
        return meshPointIndecies.size();
    }

    Collection<? extends MeshFace> triangulateQuad(PointsOfMesh points) {
        Iterator<Integer> index = iterator();
        Integer aIndex = index.next();
        Integer bIndex = index.next();
        Integer cIndex = index.next();
        Integer dIndex = index.next();

        Coordinates a = points.getPositionOf(aIndex);
        Coordinates b = points.getPositionOf(bIndex);
        Coordinates c = points.getPositionOf(cIndex);
        Coordinates d = points.getPositionOf(dIndex);

        MeshFace meshFace1;
        MeshFace meshFace2;
        LinkedList<MeshFace> result = new LinkedList<>();

        if (a.getDistance(c) > b.getDistance(d)) {
            meshFace1 = new MeshFace(material, aIndex, bIndex, dIndex);
            meshFace2 = new MeshFace(material, bIndex, cIndex, dIndex);
        } else {
            meshFace1 = new MeshFace(material, aIndex, bIndex, cIndex);
            meshFace2 = new MeshFace(material, cIndex, dIndex, aIndex);
        }
        result.add(meshFace1);
        result.add(meshFace2);
        return result;
    }

    public Material getMaterial() {
        return material;
    }

    public final void addMeshpointIndex(Integer index) {
        meshPointIndecies.add(index);
    }

    public Integer getLastIndex() {
        return meshPointIndecies.getLast();
    }

    public Integer getFirstIndex() {
        return meshPointIndecies.getFirst();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer thi : this) {
            sb.append(thi);
            sb.append(',');
        }
        return sb.toString();
    }

}
