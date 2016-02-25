package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class TextureNameLine
        extends AbstractPlainTextLineOneValue
        implements MtlLine {

    public TextureNameLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setTextureName(getValue());
    }

    public static final String PREFIX = "map_Kd";
}
