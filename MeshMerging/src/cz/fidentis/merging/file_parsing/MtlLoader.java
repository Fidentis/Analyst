package cz.fidentis.merging.file_parsing;

import cz.fidentis.merging.file_parsing.lines.mtl.MtlLine;
import cz.fidentis.merging.file_parsing.lines.mtl.MtlLineFactory;
import java.io.FileNotFoundException;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class MtlLoader {

    private MtlLoader() {

    }

    /**
     *
     * @param fileName
     * @return
     * @throws ObjFileParsingException
     * @throws FileNotFoundException
     */
    public static Materials loadFrom(final String fileName)
            throws ObjFileParsingException, FileNotFoundException {

        final MtlFile file = new MtlFile(fileName);
        final Materials materials = new Materials();

        while (file.hasNextLine()) {
            final MtlLine line = MtlLineFactory.create(file.getNextLine());
            line.appendTo(materials);
        }

        return materials;
    }
}
