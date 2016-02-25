package cz.fidentis.merging.mesh.buffers;

import javax.media.opengl.GL2;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class VerticesBuffer extends AbstractDoubleCordinateBuffer {

    /**
     *
     * @param toBuffer
     */
    public VerticesBuffer(final double[] toBuffer) {
        super(toBuffer);
    }

    /**
     *
     * @return
     */
    @Override
    protected final int target() {
        return GL2.GL_VERTEX_ARRAY;
    }

    /**
     *
     * @param openGl
     */
    @Override
    protected final void setPointer(final GL2 openGl) {
        openGl.glEnableClientState(target());
        openGl.glVertexPointer(COORD_3D, GL2.GL_DOUBLE, NO_STRIDE, getBuffer());
    }

}
