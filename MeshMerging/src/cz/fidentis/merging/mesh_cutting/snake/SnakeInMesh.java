package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.SourceTargetMapping;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author matej
 */
public class SnakeInMesh extends AbstractSnake<SnaxelInMesh> {

    static public SnakeInMesh insertIntoMesh(SnakeOnMesh snakeOnMesh) {
        SnakeInMesh snakeInMesh = new SnakeInMesh();
        snakeInMesh.setLoop(snakeOnMesh.getLoop());
        for (SnaxelOnMesh snaxelOnMesh : snakeOnMesh) {
            snakeInMesh.copySnaxel(snaxelOnMesh);
        }
        snakeInMesh.insertIntoSurface(snakeOnMesh.getFirstPositionOnMesh());
        return snakeInMesh;
    }

    private SnakeInMesh() {
        super();
    }

    private void insertIntoSurface(PositionOnMesh startingPart) {
        getFirst().insertSnaxelOn(startingPart);
        for (SnaxelInMesh snaxel : this) {
            refresh(snaxel.getId());
            snaxel.insertOutgoingEdge();
        }
    }

    public Set<HalfEdge> getBorders() {
        HashSet<HalfEdge> borders = new HashSet<>();
        for (SnaxelInMesh snaxel : this) {
            HalfEdge halfEdge = snaxel.getTargetEdge();
            borders.add(halfEdge);
        }
        return borders;
    }

    public AbstractDcel getSource() {
        return getFirst().getSource();
    }

    public AbstractDcel getTarget() {
        return getFirst().getTarget();
    }

    public SourceTargetMapping getMapping() {
        return new SourceTargetMapping(this);
    }

    private void copySnaxel(SnaxelOnMesh snaxelOnMesh) {
        addSnaxelAt(snaxelOnMesh.getPositionOnMesh(), snaxelOnMesh.getSourceVertex());
    }

    @Override
    protected SnaxelInMesh createFirst(PositionOnMesh hit, Vertex orignal) {
        return SnaxelInMesh.getFirst(this, orignal, hit.getPosition());
    }

    @Override
    protected SnaxelInMesh createSnaxel(SnaxelInMesh previos, PositionOnMesh hit, Vertex vertex) {
        return new SnaxelInMesh(previos, vertex, hit.getPosition());
    }

}
