package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineFloats;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class ColorAmbientLine
        extends AbstractPlainTextLineFloats
        implements MtlLine {

    public ColorAmbientLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setAmbientColor(points());
    }

    public static final String PREFIX = "Ka";
}
