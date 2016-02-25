package cz.fidentis.merging.file_parsing.lines;

import java.util.LinkedList;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class AbstractPlainTextLine {

    private final LinkedList<String> words;

    /**
     *
     * @return
     */
    public final Iterable<String> getWords() {
        return words;
    }

    /**
     *
     * @return
     */
    protected final String firstWord() {
        return words.getFirst();
    }

    /**
     *
     * @param line
     */
    protected AbstractPlainTextLine(final FileLine line) {
        words = line.getValues();
    }

}
