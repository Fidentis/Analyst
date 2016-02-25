package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnHalfEdge;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnMesh;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class HittedEdge extends AbstractHit<HalfEdge> {

    private static final Coordinates green = new Coordinates(0.d, 1.d, 0.d);

    /**
     *
     * @param hit
     * @param halfEdge
     */
    public HittedEdge(final Coordinates hit, final HalfEdge halfEdge) {
        super(hit, halfEdge);
    }

    /**
     *
     * @param hit
     * @param halfEdge
     * @param goesOut
     */
    public HittedEdge(final Coordinates hit, final HalfEdge halfEdge, final boolean goesOut) {
        super(hit, halfEdge, goesOut);
    }

    /**
     *
     * @return
     */
    @Override
    protected Coordinates getColor() {
        return green;
    }

    @Override
    public AbstractFace getFaceForDisplacment(Vector3 displacment) {
        AbstractFace incidentFace = getPartHitted().getIncidentFace();
        AbstractFace twinsFace = getPartHitted().getTwinsFace();

        Vector3 incidentNormal = incidentFace.getNormal();
        Vector3 twinNormal = twinsFace.getNormal();
        if (returnIncident(displacment, twinNormal, incidentNormal)) {
            return incidentFace;
        } else {
            return twinsFace;
        }
    }

    private static boolean returnIncident(Vector3 displacment, Vector3 twinNormal,
            Vector3 incidentNormal) {
        return displacment.cosineOfAngel(twinNormal) > displacment.cosineOfAngel(incidentNormal);
    }

    @Override
    public PositionOnMesh getPositionOnMesh() {
        return new PositionOnHalfEdge(getPosition(), getPartHitted());
    }

}
