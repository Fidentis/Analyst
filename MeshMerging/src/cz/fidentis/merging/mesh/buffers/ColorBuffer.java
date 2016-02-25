package cz.fidentis.merging.mesh.buffers;

import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class ColorBuffer extends AbstractDoubleCordinateBuffer {
    private final int dimension = 3;

    /**
     *
     * @param toBuffer
     */
    public ColorBuffer(final double[] toBuffer) {
        super(toBuffer);
    }

    /**
     *
     * @return
     */
    @Override
    protected final int target() {
        return GL2.GL_COLOR_ARRAY;
    }

    /**
     *
     * @param openGl
     */
    @Override
    protected final void setPointer(final GL2 openGl) {
        openGl.glEnableClientState(target());
        openGl.glColorPointer(dimension, GL2.GL_DOUBLE, NO_STRIDE, getBuffer());
    }

}
