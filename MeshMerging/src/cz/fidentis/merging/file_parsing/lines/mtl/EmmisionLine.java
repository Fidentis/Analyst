package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author matej
 */
public class EmmisionLine extends AbstractMtlLineFloats implements MtlLine {

    public EmmisionLine(FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(final Materials sm) {
        sm.setEmmision(points());
    }

    public static final String PREFIX = "Ke";

}
