package cz.fidentis.merging.file_parsing.lines.obj;

import cz.fidentis.merging.file_parsing.GraphicMeshBuilderFromObj;
import cz.fidentis.merging.file_parsing.lines.AbstractPlainTextLine;
import cz.fidentis.merging.file_parsing.lines.FileLine;
import java.util.Iterator;

class Index extends AbstractPlainTextLine implements ObjLine {

    private final MeshFaceBuilder builder = new MeshFaceBuilder();

    public Index(FileLine line) {
        super(line);
        appendToList();
    }

    private void appendToList() {
        for (Iterator<String> it = getWords().iterator(); it.hasNext();) {
            String[] indexes = it.next().split("/");
            PointIndexing faceIndecies = GetIndecies(indexes);
            builder.addIndecies(faceIndecies);
        }
    }

    private PointIndexing GetIndecies(String[] indexes) throws NumberFormatException {
        Integer vertex = Integer.valueOf(indexes[0]) - 1;
        Integer normal = 0;
        Integer texture = 0;
        if (indexes.length > 1) {
            if (!indexes[1].isEmpty()) {
                texture = Integer.valueOf(indexes[1]) - 1;
            }
            normal = Integer.valueOf(indexes[2]) - 1;
        }
        PointIndexing faceIndecies;
        faceIndecies = new PointIndexing(vertex, normal, texture);
        return faceIndecies;
    }

    @Override
    public void addToStagingGraphicMesh(GraphicMeshBuilderFromObj sgm) {
        sgm.addVertexIndecies(builder);
    }
}
