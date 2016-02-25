package cz.fidentis.merging.storing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author matej
 */
public class AbstractWriter {

    private final BufferedWriter writer;

    public AbstractWriter(String file) throws IOException {
        writer = new BufferedWriter(new FileWriter(getUniqueName(file)));
    }

    protected void addLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    protected void addIndentedLine(String line) throws IOException {
        writer.write('\t');
        addLine(line);
    }

    protected String format(String name, String value) {
        return String.format(Locale.UK, "%s %s", name, value);
    }

    protected String format(String name, double value) {
        return String.format(Locale.UK, "%s %f", name, value);
    }

    protected String format(String name, double[] value) {
        return String.format(Locale.UK, "%s %f %f %f", name, value[0], value[1], value[2]);
    }

    protected String format(String name, int value) {
        return String.format(Locale.UK, "%s %d", name, value);
    }

    protected void close() throws IOException {
        writer.close();
    }

    protected static String getDirectoryPath() throws IOException {
        String thisDirectory;
        thisDirectory = new File(".").getCanonicalPath();
        thisDirectory += "/Composed/";
        return thisDirectory;
    }

    protected static String getUniqueName(String intededName) throws IOException {
        String directoryPath = getDirectoryPath();
        int counter = 0;
        String uniqueName = directoryPath + intededName;
        File f = new File(uniqueName);
        while (f.exists()) {
            counter++;
            uniqueName = directoryPath + String.valueOf(counter) + intededName;
            f = new File(uniqueName);
        }
        return uniqueName;
    }

}
