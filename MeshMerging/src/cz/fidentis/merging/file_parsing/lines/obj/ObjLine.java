package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;

/**
 *
 * @author xlobodas
 */
public interface ObjLine {

    /**
     *
     * @param sgm
     */
    void addToStagingGraphicMesh(GraphicMeshBuilderFromObj sgm);
}
