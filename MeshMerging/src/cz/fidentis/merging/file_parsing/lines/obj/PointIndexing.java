package cz.fidentis.merging.file_parsing.lines.obj;

/**
 *
 * @author matej
 */
public class PointIndexing {

    final Integer vertexIndex;
    final Integer normalIndex;
    final Integer textureIndex;

    public PointIndexing(Integer vertex, Integer normal, Integer texture) {
        vertexIndex = vertex;
        normalIndex = normal;
        textureIndex = texture;
    }

    public Integer getPositionIndex() {
        return vertexIndex;
    }

    public Integer getNormalIndex() {
        return normalIndex;
    }

    public Integer getTextureIndex() {
        return textureIndex;
    }

}
