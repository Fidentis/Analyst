package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
class UnsuportedLine extends AbstractPlainTextLineOneValue implements MtlLine {

    public UnsuportedLine(final FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        throw new UnsupportedOperationException(getValue());
    }

}
