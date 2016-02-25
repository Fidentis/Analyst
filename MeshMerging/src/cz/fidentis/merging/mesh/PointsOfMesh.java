package cz.fidentis.merging.mesh;

import cz.fidentis.merging.scene.MeshDisplacment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class PointsOfMesh implements Iterable<MeshPoint> {

    private final ArrayList<MeshPoint> meshPoints = new ArrayList<>();
    private int nextIndex = 0;

    /**
     *
     */
    public PointsOfMesh() {
    }

    /**
     *
     * @param toCopy
     */
    public PointsOfMesh(final PointsOfMesh toCopy) {
        Objects.requireNonNull(toCopy);
        meshPoints.addAll(toCopy.meshPoints);
        nextIndex = toCopy.nextIndex;
        defaultColors();
    }

    PointsOfMesh(PointsOfMesh toDisplace, MeshDisplacment dis) {
        Objects.requireNonNull(toDisplace);
        Objects.requireNonNull(dis);
        for (MeshPoint point : toDisplace.meshPoints) {
            Coordinates newPosition = point.getPosition().getDisplacment(dis);
            MeshPoint meshPoint = new MeshPoint(point, newPosition);
            meshPoints.add(meshPoint.getIndex(), meshPoint);
        }
        nextIndex = toDisplace.nextIndex;
    }

    /**
     *
     * @return
     */
    public int getCoordCount() {
        int size = meshPoints.size();
        if (size <= 0) {
            return size;
        }
        return size * Coordinates.DIMENSION;
    }

    /**
     *
     * @return
     */
    public int size() {
        return meshPoints.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public MeshPoint getMeshPoint(int index) {
        return meshPoints.get(index);
    }

    /**
     *
     * @param index
     * @return
     */
    public Coordinates getPositionOf(int index) {
        return meshPoints.get(index).getPosition();
    }

    public MeshPoint createMeshPoint(Coordinates position, Vector3 normal) {
        MeshPoint meshPoint = new MeshPoint(nextIndex, position, normal);
        meshPoints.add(nextIndex, meshPoint);
        nextIndex++;
        return meshPoint;
    }

    MeshPoint createMeshPoint(Coordinates position) {
        return createMeshPoint(position, Vector3.ZERO_VECTOR);
    }

    public Collection<Coordinates> getPositions() {
        ArrayList<Coordinates> listOfPositions = new ArrayList<>(meshPoints.size());
        for (MeshPoint point : meshPoints) {
            listOfPositions.add(point.getIndex(), point.getPosition());
        }
        return listOfPositions;
    }

    protected Collection<Vector3> getNormals() {
        ArrayList<Vector3> listOfNormals = new ArrayList<>(meshPoints.size());
        for (MeshPoint point : meshPoints) {
            listOfNormals.add(point.getIndex(), point.getNormal());
        }
        return listOfNormals;
    }

    private Collection<Coordinates> getFaceColors() {
        ArrayList<Coordinates> listOfFaceColors = new ArrayList<>(meshPoints.size());
        for (MeshPoint point : meshPoints) {
            listOfFaceColors.add(point.getIndex(), point.getFaceColor());
        }
        return listOfFaceColors;
    }

    private Collection<Coordinates> getLineColors() {
        ArrayList<Coordinates> listOfLineColors = new ArrayList<>(meshPoints.size());
        for (MeshPoint point : meshPoints) {
            listOfLineColors.add(point.getIndex(), point.getLineColor());
        }
        return listOfLineColors;
    }

    private Collection<? extends ComposedValue> getPointColors() {
        ArrayList<Coordinates> listOfPointColors = new ArrayList<>(meshPoints.size());
        for (MeshPoint point : meshPoints) {
            listOfPointColors.add(point.getIndex(), point.getPointColor());
        }
        return listOfPointColors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < meshPoints.size(); i++) {
            sb.append(i);
            sb.append(' ');
            sb.append(meshPoints.get(i).getPosition());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public Coordinates getCenter() {
        Coordinates sum = Coordinates.sum(getPositions());
        return new Coordinates(sum.scaled(1.0d / (double) meshPoints.size()));
    }

    @Override
    public int hashCode() {
        return meshPoints.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointsOfMesh)) {
            return false;
        }
        final PointsOfMesh other = (PointsOfMesh) obj;
        return meshPoints.equals(other.meshPoints);
    }

    @Override
    public Iterator<MeshPoint> iterator() {
        return meshPoints.iterator();
    }

    void moveOnIndex(int index, Coordinates movement) {
        MeshPoint old = meshPoints.get(index);
        Coordinates newPossition = old.getPosition().add(movement);
        MeshPoint newMeshPoint = new MeshPoint(old, newPossition);
        meshPoints.set(index, newMeshPoint);
    }

    private void defaultColors() {
        for (MeshPoint point : this) {
            point.setDefaultFaceColor();
            point.setDefaultLineColor();
        }
    }

    /**
     *
     * @param source
     * @return
     */
    public double[] getAsArray(Collection<? extends ComposedValue> source) {
        double[] newArray = new double[getCoordCount()];

        if (newArray.length == 0) {
            return newArray;
        }
        int i = 0;
        for (ComposedValue pointf : source) {
            for (double d : pointf.asArray()) {
                newArray[i] = d;
                i++;
            }
        }
        return newArray;
    }

    double[] getArrayOfPositions() {
        return getAsArray(getPositions());
    }

    double[] getArrayOfNormals() {
        return getAsArray(getNormals());
    }

    double[] getArrayOfLineColors() {
        return getAsArray(getLineColors());
    }

    double[] getArrayOfFaceColors() {
        return getAsArray(getFaceColors());
    }

    double[] getArrayOfPointColors() {
        return getAsArray(getPointColors());
    }

    void defaultFaceColor() {

    }

    void defaultFaceColor(int index) {
        meshPoints.get(index).setDefaultFaceColor();
    }

    public boolean contains(Integer index) {
        return meshPoints.size() < index;
    }

}
