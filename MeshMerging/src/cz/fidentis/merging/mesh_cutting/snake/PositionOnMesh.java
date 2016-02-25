package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcelPart;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;

/**
 *
 * @author matej
 * @param <T>
 */
public abstract class PositionOnMesh<T extends AbstractDcelPart> {

    private final Coordinates position;
    private final T underlyingPart;

    public PositionOnMesh(Coordinates position, T part) {
        this.position = position;
        this.underlyingPart = part;
    }

    public Coordinates getPosition() {
        return position;
    }

    public T getUnderlyingPart() {
        return underlyingPart;
    }

    abstract Collection<AbstractFace> getFacesForDisplacment(Vector3 displacment);

    abstract Vector3 externalDisplacment(SnaxelOnMesh snaxel);

}
