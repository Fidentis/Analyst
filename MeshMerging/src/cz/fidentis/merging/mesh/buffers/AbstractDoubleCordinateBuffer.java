package cz.fidentis.merging.mesh.buffers;

import com.jogamp.common.nio.Buffers;
import java.nio.DoubleBuffer;
import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public abstract class AbstractDoubleCordinateBuffer
        extends AbstractBuffer<DoubleBuffer> {

    /**
     *
     * @param toBuffer
     */
    public AbstractDoubleCordinateBuffer(final double[] toBuffer) {
        super();
        setFloatBuffert(toBuffer);
    }

    /**
     *
     * @param floats
     */
    public final void setFloatBuffert(final double[] floats) {
        final DoubleBuffer buffer;
        buffer = Buffers.newDirectDoubleBuffer(floats.length);
        buffer.put(floats);
        buffer.rewind();
        setBuffer(buffer);
    }

    /**
     *
     * @param openGl
     */
    public final void bindBuffer(final GL2 openGl) {
        asureIsBuffered(openGl);
        setPointer(openGl);
    }

    /**
     *
     * @param buffer
     * @param openGl
     */
    public static void bindIfExists(final AbstractDoubleCordinateBuffer buffer,
            final GL2 openGl) {
        if (buffer != null) {
            buffer.bindBuffer(openGl);
        }
    }

    /**
     *
     * @param buffer
     * @param openGl
     */
    public static void unBindExists(final AbstractDoubleCordinateBuffer buffer,
            final GL2 openGl) {
        if (buffer != null) {
            buffer.unBindBuffer(openGl);
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected final int typeSize() {
        return Buffers.SIZEOF_FLOAT;
    }

    /**
     *
     * @param openGl
     */
    protected abstract void setPointer(final GL2 openGl);

    private void unBindBuffer(final GL2 openGl) {
        openGl.glBindBuffer(target(), 0);
    }

}
