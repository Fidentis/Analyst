package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.mesh.Material;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author matej
 */
public class MeshFaceBuilder implements Iterable<PointIndexing> {

    private final LinkedList<PointIndexing> indecies = new LinkedList<>();
    private Material material;

    public void setMaterial(Material material) {
        this.material = material;
    }

    public MeshFaceBuilder() {
    }

    public List<PointIndexing> getIndecies() {
        return Collections.unmodifiableList(indecies);
    }

    public void addIndecies(PointIndexing faceIndecies) {
        indecies.addLast(faceIndecies);
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public Iterator<PointIndexing> iterator() {
        return indecies.iterator();
    }

    public int size() {
        return indecies.size();
    }

}
