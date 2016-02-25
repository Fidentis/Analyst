package cz.fidentis.merging.file_parsing;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class ObjFileParsingException extends Exception {

    /**
     *
     * @param message
     */
    ObjFileParsingException(final String message) {
        super(message);
    }

    /**
     *
     */
    public ObjFileParsingException() {
        super();
    }
}
