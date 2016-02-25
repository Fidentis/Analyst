package cz.fidentis.merging.mesh.buffers;

import java.nio.Buffer;

import javax.media.opengl.GL;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 *
 * @param <T>
 */
public abstract class AbstractBuffer<T extends Buffer> {

    /**
     *
     */
    protected static final int NO_STRIDE = 0;

    /**
     *
     */
    protected static final int COORD_3D = 3;

    private T buffer;
    private boolean isBuffered = false;
    private int nameOfBuffer;

    /**
     *
     * @return
     */
    protected final T getBuffer() {
        return buffer;
    }

    /**
     *
     * @param bufferToSet
     */
    protected final void setBuffer(final T bufferToSet) {
        buffer = bufferToSet;
    }
    
    

    protected int getBufferCapacity() {
        return buffer.capacity();
    }

    /**
     *
     * @param openGL
     */
    protected final void asureIsBuffered(final GL openGL) {

        if (isBuffered) {

            openGL.glBindBuffer(target(), nameOfBuffer);
            return;
        }

        final int size = typeSize() * buffer.capacity();
        creatBuffer(openGL);
        openGL.glBindBuffer(target(), nameOfBuffer);
        openGL.glBufferData(target(), size, buffer, GL.GL_STATIC_DRAW);
        isBuffered = true;
    }

    private void creatBuffer(final GL openGL) {
        final int[] temp = new int[1];
        openGL.glGenBuffers(1, temp, 0);
        nameOfBuffer = temp[0];
    }

    /**
     *
     * @return
     */
    protected abstract int target();

    /**
     *
     * @return
     */
    protected abstract int typeSize();

}
