package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class MtlFile
        extends AbstractPlainTextLineOneValue
        implements ObjLine {

    /**
     *
     * @param line
     */
    public MtlFile(final FileLine line) {
        super(line);
    }

    /**
     *
     * @param sgm
     */
    @Override
    public final void addToStagingGraphicMesh(final GraphicMeshBuilderFromObj sgm) {
        sgm.addMtlFile(getValue());
    }
}
