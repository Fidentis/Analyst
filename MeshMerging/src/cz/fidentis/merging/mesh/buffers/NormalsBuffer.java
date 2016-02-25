package cz.fidentis.merging.mesh.buffers;

import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class NormalsBuffer extends AbstractDoubleCordinateBuffer {

    /**
     *
     * @param toBuffer
     */
    public NormalsBuffer(final double[] toBuffer) {
        super(toBuffer);
    }

    /**
     *
     * @return
     */
    @Override
    protected final int target() {
        return GL2.GL_NORMAL_ARRAY;
    }

    /**
     *
     * @param openGl
     */
    @Override
    protected final void setPointer(final GL2 openGl) {
        openGl.glEnable(target());
        openGl.glNormalPointer(GL2.GL_DOUBLE, NO_STRIDE, getBuffer());
    }
}
