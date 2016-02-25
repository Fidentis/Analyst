package cz.fidentis.merging.mesh;

import cz.fidentis.merging.mesh.buffers.AbstractDoubleCordinateBuffer;
import cz.fidentis.merging.mesh.buffers.ColorBuffer;
import cz.fidentis.merging.mesh.buffers.IndexBuffer;
import cz.fidentis.merging.mesh.buffers.VerticesBuffer;
import cz.fidentis.merging.mesh_cutting.snake.AbstractSnake;
import cz.fidentis.merging.mesh_cutting.snake.AbstractSnaxel;
import javax.media.opengl.GL2;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class LineLoop {

    private IndexBuffer indicesBuf;
    private VerticesBuffer verticesBuf;
    private ColorBuffer lineColorBuf;
    private ColorBuffer pointColorBuf;
    private PointsOfMesh points;

    /**
     *
     * @param openGl
     */
    public final synchronized void draw(final GL2 openGl) {
        if (points == null || points.size() == 0) {
            return;
        }
        AbstractDoubleCordinateBuffer.bindIfExists(verticesBuf, openGl);
        openGl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        openGl.glPolygonOffset(1.0f, 1.0f);
        AbstractDoubleCordinateBuffer.bindIfExists(lineColorBuf, openGl);
        indicesBuf.drawLine(openGl, GL2.GL_LINE_LOOP, GL2.GL_FRONT_AND_BACK);
        AbstractDoubleCordinateBuffer.unBindExists(lineColorBuf, openGl);
        AbstractDoubleCordinateBuffer.bindIfExists(pointColorBuf, openGl);

        float[] f = new float[1];
        openGl.glGetFloatv(GL2.GL_POINT_SIZE, f, 0);
        openGl.glPointSize(3.0f);
        indicesBuf.drawLine(openGl, GL2.GL_POINTS, GL2.GL_FRONT);
        openGl.glPointSize(f[0]);

        AbstractDoubleCordinateBuffer.bindIfExists(pointColorBuf, openGl);
        AbstractDoubleCordinateBuffer.unBindExists(verticesBuf, openGl);

    }

    /**
     *
     * @return
     */
    public final int getVerticesCount() {
        return points.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public final Vector3 getNormal(final int index) {
        return points.getMeshPoint(index).getNormal();
    }

    /**
     *
     * @param index
     * @return
     */
    public final Coordinates getPosition(final int index) {
        return points.getMeshPoint(index).getPosition();
    }

    private int[] createLinearIndex(int size) {
        int[] indecis = new int[size];
        for (int i = 0; i < indecis.length; i++) {
            indecis[i] = i;
        }
        return indecis;
    }

    /**
     *
     */
    private synchronized final void reBuffer() {

        verticesBuf = new VerticesBuffer(points.getArrayOfPositions());
        indicesBuf = IndexBuffer.GetInedeexBufferForLines(createLinearIndex(points.size()));
        lineColorBuf = new ColorBuffer(points.getArrayOfLineColors());
        pointColorBuf = new ColorBuffer(points.getArrayOfPointColors());

    }

    /**
     *
     * @param index
     * @return
     */
    public MeshPoint getMesh(final int index) {
        return points.getMeshPoint(index);
    }

    /**
     *
     * @param index
     * @param movement
     */
    public void moveVertex(int index, Coordinates movement) {
        points.moveOnIndex(index, movement);

    }

    /**
     *
     * @return
     */
    public Coordinates getWeight() {
        return points.getCenter();
    }

    public void updateVertexColor(int index) {
        points.defaultFaceColor(index);
    }

    public synchronized void refresh(AbstractSnake<?> snake, int id) {
        points = new PointsOfMesh();
        for (AbstractSnaxel snaxel : snake) {
            MeshPoint meshPoint = points.createMeshPoint(snaxel.getPosition());
            meshPoint.setLineColor(new Coordinates(0.d, 0.d, 1.d));
            if (snaxel.getId() == id) {
                meshPoint.setPointColor(new Coordinates(0.d, 1.d, 0.d));
            }
        }
        reBuffer();
    }

    public synchronized void refresh(AbstractSnake snake) {
        refresh(snake, -1);
    }

}
