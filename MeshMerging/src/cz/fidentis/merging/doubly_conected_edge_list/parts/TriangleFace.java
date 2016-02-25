package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.Ray;
import cz.fidentis.merging.mesh_cutting.TriangelForInnerVector;
import cz.fidentis.merging.mesh_cutting.TriangelForRayCasting;

/**
 *
 * @author matej
 */
public class TriangleFace extends AbstractFace {

    private TriangelForRayCasting triangel;

    protected TriangleFace(Faces faces, HalfEdgeId incident, MeshFace meshFace) {
        super(faces, incident, meshFace);
    }

    @Override
    public AbstractIntersection getIntersection(Ray ray) {
        if (triangel == null) {
            triangel = new TriangelForRayCasting(this);
        }
        return triangel.intersectWith(ray);
    }

    /**
     *
     * @param start
     * @return
     */
    private TriangelForInnerVector getTriangelFor(Vertex start) {

        HalfEdge startHalfEdge = null;
        for (HalfEdge halfEdge : incidentEdges()) {
            if (halfEdge.haveOrigin(start)) {
                startHalfEdge = halfEdge;
                break;
            }
        }
        // HalfEdge next = startHalfEdge.getNext();
        //next.getBegining().getMeshPoint().setLineColor(new Vector3(0.d, 1.d, 0.d));
        //next.getEnd().getMeshPoint().setLineColor(new Vector3(0.d, 1.d, 0.d));
        //next.getDCEL().refresh();
        return new TriangelForInnerVector(this, startHalfEdge);
    }

    /**
     *
     * @param lastHit
     * @param direction
     * @return
     */
    @Override
    public AbstractIntersection project(Vertex lastHit, Vector3 direction) {
        Vector3 projected = projectOn(direction);
        TriangelForInnerVector triangelFor = getTriangelFor(lastHit);
        AbstractIntersection intresection = triangelFor.intresection(projected);
        /*for (HalfEdge halfEdge : incidentEdges()) {
         halfEdge.getBegining().getMeshPoint().setDefaultLineColor();
         }*/
        //getDCEL().refresh();
        return intresection;
    }

    @Override
    public boolean isOuterFace() {
        return false;
    }

    @Override
    public AbstractIntersection projectDisplacment(Coordinates original, Vector3 displacment) {
        TriangelForInnerVector triangle = new TriangelForInnerVector(this);
        return triangle.intresection(original, displacment.translate(original));
    }

    public AbstractIntersection projectCoordinates(Coordinates original) {
        TriangelForInnerVector triangle = new TriangelForInnerVector(this);
        return triangle.intresection(original);
    }

}
