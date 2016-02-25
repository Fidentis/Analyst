package cz.fidentis.merging.file_parsing.lines.mtl;

import cz.fidentis.merging.file_parsing.Materials;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLineOneValue;
import cz.fidentis.merging.file_parsing.lines.FileLine;

/**
 *
 * @author matej
 */
public class NewMaterialLine
        extends AbstractPlainTextLineOneValue
        implements MtlLine {

    protected NewMaterialLine(FileLine line) {
        super(line);
    }

    @Override
    public void appendTo(Materials sm) {
        sm.addNewMaterial(getValue());
    }

    public static final String PREFIX = "newmtl";

}
