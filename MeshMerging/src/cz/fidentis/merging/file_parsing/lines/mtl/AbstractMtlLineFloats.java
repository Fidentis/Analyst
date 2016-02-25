package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineFloats;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author xlobodas
 */
public abstract class AbstractMtlLineFloats
        extends AbstractPlainTextLineFloats
        implements MtlLine {

    /**
     *
     * @param line
     */
    public AbstractMtlLineFloats(final FileLine line) {
        super(line);
    }

}
