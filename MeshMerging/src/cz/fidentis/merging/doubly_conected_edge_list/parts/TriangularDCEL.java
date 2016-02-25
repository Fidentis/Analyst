package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.GraphicMesh;

/**
 *
 * @author matej
 */
public class TriangularDCEL extends AbstractDcel<TriangleFace> {

    public static TriangularDCEL fromMesh(GraphicMesh currentMesh) {
        currentMesh.triangulate();
        return new TriangularDCEL(currentMesh);
    }

    private TriangularDCEL(GraphicMesh mesh) {
        super(mesh);
    }

    @Override
    protected Faces<TriangleFace> createFaces() {
        return new TriangelFaces(this);
    }

}
