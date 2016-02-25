package cz.fidentis.merging.file_parsing;

import cz.fidentis.merging.file_parsing.lines.FileLine;
import cz.fidentis.merging.file_parsing.lines.obj.ObjLine;
import cz.fidentis.merging.file_parsing.lines.obj.ObjLineFactory;
import cz.fidentis.merging.mesh.GraphicMesh;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class ObjLoader {

    public final Deque<GraphicMesh> loadMeshesFromObjFile(final String fileName)
            throws FileNotFoundException, ObjFileParsingException {

        final ObjFile file = new ObjFile(fileName);

        final GraphicMeshBuilderFromObj sgm;
        sgm = new GraphicMeshBuilderFromObj(file.getDirectory());

        while (file.hasNextLine()) {
            final FileLine line = file.getNextLine();

            final ObjLine parsedLine = ObjLineFactory.getObjLine(line);

            parsedLine.addToStagingGraphicMesh(sgm);
        }
        LinkedList<GraphicMesh> results = new LinkedList<>();
        for (String groupName : sgm.getGroupsByInsertionOrder()) {
            GraphicMesh newGraphicMesh = new GraphicMesh(sgm, groupName);
            results.addLast(newGraphicMesh);
        }
        return results;

    }

    /**
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws ObjFileParsingException
     */
    public final GraphicMesh loadMeshFromObjFile(final String fileName)
            throws FileNotFoundException, ObjFileParsingException {

        return loadMeshesFromObjFile(fileName).getFirst();

    }
}
