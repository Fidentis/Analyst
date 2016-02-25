package cz.fidentis.merging.file_parsing.lines;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author xlobodas
 */
public abstract class AbstractPlainTextLineFloats extends AbstractPlainTextLine {

    /**
     *
     */
    protected LinkedList<Float> points = new LinkedList<>();

    /**
     *
     * @param line
     */
    protected AbstractPlainTextLineFloats(FileLine line) {
        super(line);
        parseWords();
    }

    private void parseWords() {

        for (Iterator<String> it = getWords().iterator(); it.hasNext();) {
            points.add(Float.valueOf(it.next()));
        }

    }

    /**
     *
     * @return
     */
    protected double[] points() {
        double[] result = new double[points.size()];
        int i = 0;
        for (Iterator<Float> it = points.iterator(); it.hasNext();) {
            result[i] = it.next();
            i++;
        }
        return result;
    }
}
