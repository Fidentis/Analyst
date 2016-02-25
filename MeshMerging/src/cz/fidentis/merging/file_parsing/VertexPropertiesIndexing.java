package cz.fidentis.merging.file_parsing;

import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class VertexPropertiesIndexing {

    private final LinkedList<Integer> vertexIndexes = new LinkedList<>();
    private final LinkedList<Integer> normalIndexes = new LinkedList<>();
    private final LinkedList<Integer> textureIndexes = new LinkedList<>();

    public void addPositionIndex(Integer index) {
        vertexIndexes.addLast(index);
    }

    public void addNormalIndex(Integer index) {
        normalIndexes.addLast(index);
    }

    public void addTextureIndex(Integer index) {
        textureIndexes.addLast(index);
    }

}
