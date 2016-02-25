package cz.fidentis.merging.storing;

import cz.fidentis.merging.mesh.Material;
import java.io.IOException;

/**
 *
 * @author matej
 */
public class MtlWriter extends AbstractWriter {

    public MtlWriter(String file) throws IOException {
        super(file + ".mtl");
    }

    public void append(Material material) throws IOException {
        addLine("");
        addLine(format("newmtl", material.getMaterialName()));
        writeIfHasValue("map_Kd", material.getTextureName());
        addIndentedLine(format("Ns", material.getSpecularComponet()));
        addIndentedLine(format("Ni", material.getRefractionIndex()));
        addIndentedLine(format("d", material.getDisolveFactor()));
        writeIfHasValue("Tf", material.getTransmissionFilter());
        addIndentedLine(format("illum", material.getIllumination()));
        writeIfHasValue("Ka", material.getAmbient());
        writeIfHasValue("Kd", material.getDiffuse());
        writeIfHasValue("Ke", material.getEmmision());
    }

    private void writeIfHasValue(String name, double[] value) throws IOException {
        if (value != null) {
            addIndentedLine(format(name, value));
        }
    }

    private void writeIfHasValue(String name, String value) throws IOException {
        if (value != null) {
            addIndentedLine(format(name, value));
        }
    }

}
