package cz.fidentis.merging.mesh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mlobodas
 */
public final class MeshPoint {

    private static final Coordinates FACE_COLOR = new Coordinates(.8d, .8d, .8d);
    private static final Coordinates LINE_COLOR = new Coordinates(1d, .0d, .0d);

    private final Coordinates position;
    private Vector3 normal;
    private final HashMap<MeshFace, Coordinates> textures = new HashMap<>();
    private Coordinates faceColor;
    private Coordinates lineColor;
    private Coordinates pointColor;
    private final int index;

    /**
     *
     * @param index
     * @param at
     * @param normal
     * @param text
     */
    private MeshPoint(Integer index, final Coordinates at, final Vector3 normal,
            final HashMap<MeshFace, Coordinates> text) {
        this(index, at, normal);
        this.textures.putAll(text);
    }

    public MeshPoint(Integer index, final Coordinates at, final Vector3 normal) {
        this.position = at;
        this.normal = normal;
        this.index = index;
        pointColor = new Coordinates(1.d, 1.d, 0.d);
        lineColor = LINE_COLOR;
        faceColor = FACE_COLOR;
    }

    public MeshPoint(MeshPoint point, Coordinates newPosition) {
        this(point.index, newPosition, point.normal, point.textures);
    }

    /**
     *
     * @return
     */
    public Coordinates getPosition() {
        return position;
    }

    /**
     *
     * @return
     */
    public Vector3 getNormal() {
        return normal;
    }

    void setFaceColor(Coordinates newColor) {
        faceColor = newColor;
    }

    public void setLineColor(Coordinates newColor) {
        lineColor = newColor;
    }

    void setDefaultFaceColor() {
        faceColor = FACE_COLOR;
    }

    public void setDefaultLineColor() {
        lineColor = LINE_COLOR;
    }

    Coordinates getFaceColor() {
        return faceColor;
    }

    Coordinates getLineColor() {
        return lineColor;
    }

    Coordinates getPointColor() {
        return pointColor;
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public void setPointColor(Coordinates coordinates) {
        pointColor = coordinates;
    }

    public Coordinates getTextureCoordinates(MeshFace meshFace) {
        return textures.get(meshFace);
    }

    public void addTexture(Coordinates textureCooordinates, MeshFace meshFace) {
        if (textureCooordinates == null) {
            return;
        }
        textures.put(meshFace, textureCooordinates);
    }

    public int getIndex() {
        return index;
    }

    void updateNormal(Vector3 normal) {
        this.normal = normal;
    }

    void copyTexturesCoordinates(MeshFace original, MeshFace newFaces) {
        Coordinates toCopy = textures.get(original);
        if (toCopy == null) {
            return;
        }
        textures.put(newFaces, toCopy);
    }

    void removeTexturesOf(MeshFace toSplit) {
        textures.remove(toSplit);
    }

    public Map<MeshFace, Coordinates> getAllTextures() {
        return Collections.unmodifiableMap(textures);
    }

    public boolean haveTexturesFor(MeshFace meshFace) {
        return textures.containsKey(meshFace);
    }

    public void setNormal(Vector3 newNormal) {
        normal = newNormal;
    }

}
