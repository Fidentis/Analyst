package cz.fidentis.merging.mesh;

import cz.fidentis.merging.file_parsing.AbstractGraphicMeshBuilder;
import cz.fidentis.merging.file_parsing.MaterialBuilder;
import cz.fidentis.merging.file_parsing.lines.obj.MeshFaceBuilder;
import cz.fidentis.merging.file_parsing.lines.obj.PointIndexing;
import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import javax.vecmath.Vector3f;

/**
 *
 * @author matej
 */
public class GraphicMeshBuilderFromModel extends AbstractGraphicMeshBuilder {

    public GraphicMeshBuilderFromModel(Model model) {
        addGroup("group");
        for (Vector3f vert : model.getVerts()) {
            addVertex(new Coordinates(vert));
        }
        for (Vector3f normal : model.getNormals()) {
            addNormal(new Vector3(normal));
        }
        for (Vector3f texCoord : model.getTexCoords()) {
            addTextureCoord(new Coordinates(texCoord));
        }

        for (cz.fidentis.model.Material material : model.getMatrials().getMatrials()) {
            MaterialBuilder materialBuilder = new MaterialBuilder(material);
            Material myMaterial = new Material(materialBuilder);
            addMaterial(myMaterial.getMaterialName(), myMaterial);
        }

        Faces faces = model.getFaces();
        for (int i = 0; i < faces.getNumFaces(); i++) {
            processFace(faces, i);
        }
    }

    private void processFace(Faces faces, int i) {
        int[] faceNormalIdxs = faces.getFaceNormalIdxs(i);
        int[] faceVertIdxs = faces.getFaceVertIdxs(i);
        int[] faceTexIdxs = faces.getFaceTexIdxs(i);
        String findMaterial = faces.findMaterial(i);
        setCurrentMaterialMaterial(findMaterial);
        MeshFaceBuilder meshFaceBuilder = new MeshFaceBuilder();
        for (int j = faceVertIdxs.length - 1; j >= 0; j--) {
            int vertexIndex = faceVertIdxs[j] - 1;
            int normalIndex = faceNormalIdxs[j] - 1;
            int textureIndex;
            if (faceTexIdxs != null) {
                textureIndex = faceTexIdxs[j] - 1;
            } else {
                textureIndex = 0;
            }
            PointIndexing pointIndexing;
            pointIndexing = new PointIndexing(vertexIndex, normalIndex, textureIndex);
            meshFaceBuilder.addIndecies(pointIndexing);
        }
        addVertexIndecies(meshFaceBuilder);
    }

}
