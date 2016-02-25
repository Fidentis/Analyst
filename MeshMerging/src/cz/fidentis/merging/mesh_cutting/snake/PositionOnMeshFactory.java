package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.mesh_cutting.HittedEdge;
import cz.fidentis.merging.mesh_cutting.HittedFace;
import cz.fidentis.merging.mesh_cutting.HittedVertex;

/**
 *
 * @author matej
 */
public class PositionOnMeshFactory {

    public PositionOnMesh cretePosition(HittedFace hit) {
        throw new AbstractMethodError();
    }

    public PositionOnMesh cretePosition(HittedEdge hit) {
        throw new AbstractMethodError();
    }

    public PositionOnMesh cretePosition(HittedVertex hit) {
        throw new AbstractMethodError();
    }
}
