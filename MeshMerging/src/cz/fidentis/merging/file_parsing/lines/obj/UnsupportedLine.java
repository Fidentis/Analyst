package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLine;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class UnsupportedLine extends AbstractPlainTextLine implements ObjLine {

    public UnsupportedLine(final FileLine line) {
        super(line);
    }

    @Override
    public void addToStagingGraphicMesh(final GraphicMeshBuilderFromObj sgm) {
        throw new UnsupportedOperationException("Unsuported" + this.toString());
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String word : getWords()) {
            sb.append(word);
        }
        return sb.toString();
    }
}
