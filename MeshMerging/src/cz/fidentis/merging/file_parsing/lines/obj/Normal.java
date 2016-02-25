package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineFloats;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class Normal extends AbstractPlainTextLineFloats implements ObjLine {

    public Normal(final FileLine line) {
        super(line);
    }

    @Override
    public final void addToStagingGraphicMesh(final GraphicMeshBuilderFromObj sgm) {
        sgm.addNormal(points());
    }
}
