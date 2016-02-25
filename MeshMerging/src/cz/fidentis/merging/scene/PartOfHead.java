package cz.fidentis.merging.scene;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangularDCEL;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.LineLoop;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class PartOfHead {

    private final AbstractDcel dcel;
    private MeshDisplacment displacment;
    private LineLoop loop;

    /**
     *
     * @param mesh
     * @param displacment
     */
    public PartOfHead(TriangularDCEL mesh, MeshDisplacment displacment) {
        this.dcel = mesh;
        this.displacment = displacment;
    }

    /**
     *
     * @return
     */
    public GraphicMesh getMesh() {
        return dcel.getGraphicMesh();
    }

    /**
     *
     * @return
     */
    public MeshDisplacment getDisplacment() {
        return displacment;
    }

    public LineLoop getLoop() {
        return loop;
    }

    public void setLoop(LineLoop loop) {
        this.loop = loop;
    }

    void resetDisplacment() {
        displacment = new MeshDisplacment();
    }

    public AbstractDcel getDCEL() {
        return dcel;
    }

}
