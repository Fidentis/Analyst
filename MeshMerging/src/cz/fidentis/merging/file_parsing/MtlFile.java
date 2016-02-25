package cz.fidentis.merging.file_parsing;

import java.io.FileNotFoundException;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class MtlFile extends AbstractPlainTextFile {

    /**
     *
     * @param openFile
     * @throws FileNotFoundException
     * @throws ObjFileParsingException
     */
    public MtlFile(final String openFile)
            throws FileNotFoundException, ObjFileParsingException {
        super(openFile);
    }

    /**
     *
     * @return
     */
    @Override
    protected final String getExtension() {
        return MTL;
    }
    private static final String MTL = ".mtl";

}
