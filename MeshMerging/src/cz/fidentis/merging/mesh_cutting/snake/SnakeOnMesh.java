package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.bounding.bvh.BVH;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.LineLoop;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.LoopForProjection;
import cz.fidentis.merging.mesh_cutting.Ray;

/**
 *
 * @author matej
 */
public class SnakeOnMesh extends AbstractSnake<SnaxelOnMesh> {

    private double globalMeanSquereDistance;
    private boolean noDistance = true;

    public SnakeOnMesh(LoopForProjection edgesToProject, LineLoop loop) {

        BVH bvh = edgesToProject.getBvh();

        setLoop(loop);
        AbstractHit hit;
        for (Ray ray : edgesToProject) {
            AbstractIntersection intersection = bvh.getNearestHit(ray);
            if (intersection.successful()) {
                hit = (AbstractHit) intersection;
                addSnaxelAt(hit.getPositionOnMesh(), edgesToProject.getCurrentVertex());
                edgesToProject.initFirstProjection();
            } else {
                edgesToProject.removeCurrentFromLoop();
            }
        }
        refreshLoop();
    }

    public void evolveOverMesh() {
        for (SnaxelOnMesh snaxel : this) {
            snaxel.evolve();
        }
    }

    public double getGlobalMeanSquereDistance() {

        double totalSum = 0.d;
        for (SnaxelOnMesh snaxel : this) {
            totalSum += snaxel.getSquareDistanceFrom(snaxel.getNext());
        }
        return totalSum / (double) getSize();

    }

    @Override
    protected SnaxelOnMesh createFirst(PositionOnMesh hit, Vertex orignal) {
        return SnaxelOnMesh.getFirst(this, hit, orignal);
    }

    @Override
    protected SnaxelOnMesh createSnaxel(SnaxelOnMesh previos, PositionOnMesh hit, Vertex vertex) {
        return new SnaxelOnMesh(previos, hit, vertex);
    }

    PositionOnMesh getFirstPositionOnMesh() {
        return getFirst().getPositionOnMesh();
    }

}
