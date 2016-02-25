package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.EmptyIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;

/**
 *
 * @author matej
 */
class OuterFace extends AbstractFace {

    public OuterFace(Faces faces, HalfEdgeId incidentEdge) {
        super(faces, incidentEdge, null);
    }

    @Override
    public AbstractIntersection getIntersection(Ray ray) {
        return new EmptyIntersection();
    }

    @Override
    public AbstractIntersection project(Vertex lastHit, Vector3 direction) {
        return new EmptyIntersection();
    }

    @Override
    public boolean isOuterFace() {
        return true;
    }

    @Override
    public AbstractIntersection projectDisplacment(Coordinates original, Vector3 displacment) {
        return new EmptyIntersection();
    }

    @Override
    public void removeFromDcel() {

    }

}
