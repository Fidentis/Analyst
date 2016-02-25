package cz.fidentis.merging.file_parsing.lines;

import java.util.LinkedList;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class FileLine {

    private final String prefix;
    private final LinkedList<String> words = new LinkedList<>();

    /**
     *
     * @param line
     */
    public FileLine(String line) {

        for (String split : line.split(" ")) {
            if (!split.isEmpty()) {
                words.add(split.replaceAll("\t", ""));
            }
        }

        prefix = words.removeFirst();
    }

    /**
     *
     * @return
     */
    public LinkedList<String> getValues() {
        return words;
    }

    /**
     *
     * @return
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @return
     */
    public char getPrefixStart() {
        return prefix.charAt(0);
    }

}
