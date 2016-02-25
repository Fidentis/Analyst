package cz.fidentis.merging.file_parsing;

import java.io.FileNotFoundException;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class ObjFile extends AbstractPlainTextFile {

    private static final String OBJ = ".obj";

    /**
     *
     * @param openFile
     * @throws FileNotFoundException
     * @throws ObjFileParsingException
     */
    public ObjFile(final String openFile)
            throws FileNotFoundException, ObjFileParsingException {

        super(openFile);
    }

    /**
     *
     * @return
     */
    @Override
    protected final String getExtension() {
        return OBJ;
    }
}
