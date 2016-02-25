package cz.fidentis.merging.mesh;

import cz.fidentis.merging.mesh.buffers.AbstractDoubleCordinateBuffer;
import cz.fidentis.merging.mesh.buffers.ColorBuffer;
import cz.fidentis.merging.mesh.buffers.IndexBuffer;
import cz.fidentis.merging.mesh.buffers.NormalsBuffer;
import cz.fidentis.merging.mesh.buffers.VerticesBuffer;
import javax.media.opengl.GL2;

/**
 *
 * @author matej
 */
public class MeshBuffer {

    private IndexBuffer indicesBuf;
    private VerticesBuffer verticesBuf;
    private NormalsBuffer normalsBuf;
    private ColorBuffer lineColorBuf;
    private ColorBuffer faceColorBuf;
    private final GraphicMesh graphicMesh;

    public MeshBuffer(GraphicMesh mesh) {
        graphicMesh = mesh;
    }

    public final synchronized void draw(final GL2 openGl) {

        AbstractDoubleCordinateBuffer.bindIfExists(verticesBuf, openGl);
        AbstractDoubleCordinateBuffer.bindIfExists(normalsBuf, openGl);
        AbstractDoubleCordinateBuffer.bindIfExists(faceColorBuf, openGl);
        openGl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        openGl.glPolygonOffset(1.0f, 1.0f);
        indicesBuf.draw(openGl, GL2.GL_FILL, GL2.GL_FRONT);
        AbstractDoubleCordinateBuffer.unBindExists(faceColorBuf, openGl);
        AbstractDoubleCordinateBuffer.bindIfExists(lineColorBuf, openGl);
        indicesBuf.draw(openGl, GL2.GL_LINE, GL2.GL_FRONT_AND_BACK);
        indicesBuf.draw(openGl, GL2.GL_POINT, GL2.GL_FRONT);
        AbstractDoubleCordinateBuffer.unBindExists(lineColorBuf, openGl);

        AbstractDoubleCordinateBuffer.unBindExists(verticesBuf, openGl);
        AbstractDoubleCordinateBuffer.unBindExists(normalsBuf, openGl);

    }

    public synchronized final void reBuffer() {
        PointsOfMesh points = graphicMesh.getPoints();
        FacesOfMesh indices = graphicMesh.getMeshFaces();
        verticesBuf = new VerticesBuffer(points.getArrayOfPositions());
        normalsBuf = new NormalsBuffer(points.getArrayOfNormals());
        indicesBuf = IndexBuffer.GetInedeexBufferForTriangels(indices.getVertexIndeciesArray());
        lineColorBuf = new ColorBuffer(points.getArrayOfLineColors());
        faceColorBuf = new ColorBuffer(points.getArrayOfFaceColors());
    }

}
