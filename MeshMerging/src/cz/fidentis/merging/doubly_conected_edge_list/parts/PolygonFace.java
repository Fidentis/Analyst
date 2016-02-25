package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;

/**
 *
 * @author matej
 */
public class PolygonFace extends AbstractFace {

    protected PolygonFace(Faces faces, HalfEdgeId incidentEdge, MeshFace meshFace) {
        super(faces, incidentEdge, meshFace);
    }

    @Override
    public AbstractIntersection project(Vertex lastHit, Vector3 direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractIntersection getIntersection(Ray ray) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isOuterFace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractIntersection projectDisplacment(Coordinates original, Vector3 displacment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
