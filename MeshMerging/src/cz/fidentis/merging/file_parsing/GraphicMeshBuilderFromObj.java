package cz.fidentis.merging.file_parsing;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Material;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.scene.Log;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * Matej Lobodáš <lobodas.m at gmail.com>
 */
public class GraphicMeshBuilderFromObj extends AbstractGraphicMeshBuilder {

    private final String directory;

    /**
     *
     * @param sourceDirectory here .mtl should be stored here
     */
    public GraphicMeshBuilderFromObj(String sourceDirectory) {
        directory = sourceDirectory;

    }

    /**
     *
     * @param coords
     */
    public void addVertex(final double[] coords) {
        addVertex(Coordinates.fromArray(coords));
    }

    /**
     *
     * @param coords
     */
    public void addNormal(final double[] coords) {
        addNormal(Vector3.fromArrayV(coords));
    }

    /**
     *
     * @param coords
     */
    public void addTextureCoord(final double[] coords) {
        addTextureCoord(Coordinates.fromArray(coords));
    }

    /**
     *
     * @param shadingGroup
     */
    public void setShadingOfGroup(String shadingGroup) {
    }

    /**
     *
     * @param file
     */
    public void addMtlFile(String file) {
        try {
            Materials loaded = MtlLoader.loadFrom(directory + '/' + file);
            for (Map.Entry<String, MaterialBuilder> builder : loaded) {
                addMaterial(builder.getKey(), new Material(builder.getValue()));
            }
        } catch (ObjFileParsingException | FileNotFoundException ex) {
            Log.log(ex);
        }
    }

    public String getDirectory() {
        return directory;
    }

}
