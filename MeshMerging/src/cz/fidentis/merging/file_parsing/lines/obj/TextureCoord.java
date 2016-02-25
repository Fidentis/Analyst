/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineFloats;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class TextureCoord extends AbstractPlainTextLineFloats implements ObjLine {

    public TextureCoord(final FileLine line) {
        super(line);
    }

    @Override
    public final void addToStagingGraphicMesh(final GraphicMeshBuilderFromObj sgm) {
        sgm.addTextureCoord(points());
    }
}
