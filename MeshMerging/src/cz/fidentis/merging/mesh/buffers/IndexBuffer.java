package cz.fidentis.merging.mesh.buffers;

import com.jogamp.common.nio.Buffers;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class IndexBuffer extends AbstractBuffer<IntBuffer> {

    private final int primitive;

    /**
     *
     * @param indecies
     */
    private IndexBuffer(final int[] indecies, final int type) {
        super();
        setIndexes(indecies);
        primitive = type;
    }

    public static IndexBuffer GetInedeexBufferForTriangels(final int[] indecies) {
        return new IndexBuffer(indecies, GL.GL_TRIANGLES);
    }

    public static IndexBuffer GetInedeexBufferForLines(final int[] indecies) {
        return new IndexBuffer(indecies, GL.GL_LINE_LOOP);
    }

    /**
     *
     * @param indices
     */
    public final void setIndexes(final int[] indices) {
        final IntBuffer buffer = Buffers.newDirectIntBuffer(indices.length);
        buffer.put(indices);
        buffer.rewind();
        setBuffer(buffer);
    }

    /**
     *
     * @param openGL
     * @param polygonMode
     * @param sides
     */
    public final void draw(final GL2 openGL, final int polygonMode,
            final int sides) {

        asureIsBuffered(openGL);
        openGL.glEnableClientState(GL.GL_ELEMENT_ARRAY_BUFFER);
        openGL.glPolygonMode(sides, polygonMode);

        openGL.glDrawElements(primitive, getBufferCapacity(),
                GL.GL_UNSIGNED_INT, 0);

        openGL.glBindBuffer(target(), 0);
    }

    /**
     *
     * @param openGL
     * @param lineMode
     * @param sides
     */
    public final void drawLine(final GL2 openGL, final int lineMode,
            final int sides) {

        asureIsBuffered(openGL);
        openGL.glEnableClientState(GL.GL_ELEMENT_ARRAY_BUFFER);

        openGL.glDrawElements(lineMode, getBufferCapacity(),
                GL.GL_UNSIGNED_INT, 0);

        openGL.glBindBuffer(target(), 0);
    }

    /**
     *
     * @return
     */
    @Override
    protected final int target() {
        return GL.GL_ELEMENT_ARRAY_BUFFER;
    }

    /**
     *
     * @return
     */
    @Override
    protected final int typeSize() {
        return Buffers.SIZEOF_INT;
    }

}
