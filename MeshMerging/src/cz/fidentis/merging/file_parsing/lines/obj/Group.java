package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Group
        extends AbstractPlainTextLineOneValue
        implements ObjLine {

    /**
     *
     * @param line
     */
    public Group(final FileLine line) {
        super(line);
    }

    /**
     *
     * @param sgm
     */
    public final void addToStagingGraphicMesh(final GraphicMeshBuilderFromObj sgm) {
        sgm.addGroup(getValue());
    }
}
