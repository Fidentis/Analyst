package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineFloats;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class TransmissionFilterLine
        extends AbstractPlainTextLineFloats
        implements MtlLine {

    public TransmissionFilterLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setTransmisionFilter(points());
    }

    public static final String PREFIX = "Tf";
}
