package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnMesh;
import cz.fidentis.merging.mesh_cutting.snake.PositionOnVertex;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class HittedVertex extends AbstractHit<Vertex> {

    private static final Coordinates blue = new Coordinates(0.d, .5d, .5d);

    /**
     *
     * @param vertex
     * @param goesOut
     */
    public HittedVertex(final Vertex vertex, final boolean goesOut) {
        super(vertex.position(), vertex, goesOut);
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
        AbstractFace toProjectOn;
        toProjectOn = getPartHitted().getIncidentHalfEdge().getIncidentFace();
        double smallest = Double.MAX_VALUE;
        for (AbstractFace neighboringFace : getNeighboringFaces()) {
            Vector3 normal = neighboringFace.getNormal();
            double cosineOfAngel = displacment.cosineOfAngel(normal);
            if (smallest > cosineOfAngel) {
                smallest = cosineOfAngel;
                toProjectOn = neighboringFace;
            }
        }
        return toProjectOn;
    }

    @Override
    public PositionOnMesh getPositionOnMesh() {
        return new PositionOnVertex(getPosition(), getPartHitted());
    }

}
