package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnFace;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnMesh;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class HittedFace extends AbstractHit<AbstractFace> {

    private static final Coordinates blue = new Coordinates(0.d, 0.d, 1.d);

    /**
     *
     * @param impact
     * @param face
     */
    public HittedFace(final Coordinates impact, final AbstractFace face) {
        super(impact, face, false);
    }

    /**
     *
     * @return
     */
    @Override
    protected Coordinates getColor() {
        return blue;
    }

    @Override
    public AbstractFace getFaceForDisplacment(Vector3 displacment) {
        return getPartHitted();
    }

    @Override
    public PositionOnMesh getPositionOnMesh() {
        return new PositionOnFace(getPosition(), getPartHitted());
    }
}
