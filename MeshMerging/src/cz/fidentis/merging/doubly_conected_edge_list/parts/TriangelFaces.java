package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.MeshFace;

public class TriangelFaces extends Faces<TriangleFace> {

    public TriangelFaces(AbstractDcel owner) {
        super(owner);
    }

    @Override
    TriangleFace createFace(HalfEdgeId forHalfEdgeId, MeshFace meshFace) {
        return new TriangleFace(this, forHalfEdgeId, meshFace);
    }

}
