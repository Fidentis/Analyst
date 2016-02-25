package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class IlluminationLine
        extends AbstractPlainTextLineOneValue
        implements MtlLine {

    public IlluminationLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setIllumination(getValueAsDouble());
    }

    public static final String PREFIX = "illum";
}
