package cz.fidentis.merging.file_parsing;

import cz.fidentis.merging.file_parsing.lines.FileLine;
import cz.fidentis.merging.scene.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matej Lobodáš
 */
public abstract class AbstractPlainTextFile {

    private BufferedReader fileStream;
    private boolean nextLineAvaileble = true;
    private String lastLine = "";
    private File file;

    /**
     *
     * @return
     */
    protected abstract String getExtension();

    /**
     *
     * @param openFile
     * @throws FileNotFoundException
     * @throws ObjFileParsingException
     */
    public AbstractPlainTextFile(final String openFile)
            throws FileNotFoundException, ObjFileParsingException {

        openFile(openFile);
        skipBlankLine();
    }

    /**
     *
     * @param fileName
     * @throws ObjFileParsingException
     */
    public final void openFile(final String fileName)
            throws ObjFileParsingException {

        if (!fileName.endsWith(getExtension())) {
            throw new ObjFileParsingException("Wrong or missing .extension");
        }
        try {
            file = new File(fileName);
            bufferFile();
        } catch (IOException ex) {
            Log.log(ex);
        }
    }

    private void bufferFile() throws IOException {
        fileStream = new BufferedReader(new FileReader(file));
    }

    /**
     *
     * @return
     */
    public final Boolean hasNextLine() {
        return nextLineAvaileble;
    }

    /**
     *
     * @return
     */
    public final FileLine getNextLine() {
        final FileLine line = new FileLine(lastLine);

        skipBlankLine();

        return line;

    }

    private void readLine() {
        try {
            lastLine = fileStream.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ObjFile.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        setHasNextLine();
    }

    private void skipBlankLine() {
        readLine();
        while (nextLineAvaileble && shouldSkipLastLine()) {
            readLine();
        }
    }

    private void setHasNextLine() {
        if (lastLine == null) {
            nextLineAvaileble = false;
            lastLine = "";
        }
    }

    /**
     *
     * @return
     */
    protected final boolean shouldSkipLastLine() {
        return lastLineIsEmpty() || lastLineStartsWith('#');
    }

    /**
     *
     * @return
     */
    protected final boolean lastLineIsEmpty() {
        return lastLine.isEmpty();
    }

    /**
     *
     * @param c
     * @return
     */
    protected final boolean lastLineStartsWith(final char c) {
        return lastLine.charAt(0) == c;
    }

    /**
     *
     * @param string
     * @return
     */
    protected final boolean lastLineStartsWith(final String string) {
        return lastLine.startsWith(string);
    }

    /**
     *
     * @return
     */
    public final String getDirectory() {
        return file.getParentFile().getAbsolutePath();
    }
}
