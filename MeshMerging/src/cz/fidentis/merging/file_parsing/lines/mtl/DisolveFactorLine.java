package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class DisolveFactorLine
        extends AbstractPlainTextLineOneValue
        implements MtlLine {

    public DisolveFactorLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setDisolveFactor(getValueAsDouble());
    }

    public static final String PREFIX = "d";
    public static final String PREFIX_ALIAS = "Tr";

}
