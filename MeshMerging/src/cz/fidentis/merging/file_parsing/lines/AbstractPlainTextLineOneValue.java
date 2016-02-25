package cz.fidentis.merging.file_parsing.lines;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public abstract class AbstractPlainTextLineOneValue
        extends AbstractPlainTextLine {

    private final String value;

    /**
     *
     * @return
     */
    protected final String getValue() {
        return value;
    }

    /**
     *
     * @return
     */
    protected final double getValueAsDouble() {
        return Double.valueOf(value);
    }

    /**
     *
     * @return
     */
    protected final int getValueAsInt() {
        return Integer.valueOf(value);
    }

    /**
     *
     * @param line
     */
    protected AbstractPlainTextLineOneValue(final FileLine line) {
        super(line);
        value = firstWord();
    }
}
