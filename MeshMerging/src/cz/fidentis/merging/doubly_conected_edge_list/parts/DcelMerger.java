package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.doubly_conected_edge_list.SourceTargetMapping;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.LineLoop;
import cz.fidentis.merging.mesh_cutting.HoleCutter;
import cz.fidentis.merging.mesh_cutting.LoopForProjection;
import cz.fidentis.merging.mesh_cutting.snake.SnakeInMesh;
import cz.fidentis.merging.mesh_cutting.snake.SnakeOnMesh;
import cz.fidentis.merging.scene.PartOfHead;
import java.util.List;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class DcelMerger {

    private final AbstractDcel target;
    private MergingProgres progres = new MergingProgres(1, 1);

    public DcelMerger(AbstractDcel target) {
        this.target = target;
    }

    public synchronized void merge(List<TriangularDCEL> parts, ProgressObserver o) {
        progres = new MergingProgres(parts.size(), 5);
        progres.setObserver(o);
        for (TriangularDCEL part : parts) {
            progres.processingNext();
            try {
                merge(part);
            } catch (Exception e) {
            }
        }
    }

    public synchronized void merge(PartOfHead part) {
        SnakeOnMesh snakeOnMesh = createSanke(part);
        mergeOnSnake(snakeOnMesh);
    }

    public synchronized void merge(AbstractDcel part) {
        progres.nextStage("Projecting contour.");
        progres.startMeasurement("Start Proj");
        SnakeOnMesh snakeOnMesh = createSanke(part);
        progres.finishMeasurement();
        mergeOnSnake(snakeOnMesh);
    }

    private SnakeOnMesh createSanke(PartOfHead part) {
        return createSanke(part.getDCEL(), part.getLoop());
    }

    private boolean mergeOnSnake(SnakeOnMesh snakeOnMesh) {
        if (snakeOnMesh.isEmpty()) {
            return true;
        }
        progres.startMeasurement("Start Evo");
        snakeOnMesh.evolveOverMesh();
        progres.finishMeasurement();
        progres.nextStage("Inserting contour.");
        progres.startMeasurement("Start Ins");
        SnakeInMesh snakeInMesh = SnakeInMesh.insertIntoMesh(snakeOnMesh);
        progres.finishMeasurement();
        progres.nextStage("Creating opening on head.");
        progres.startMeasurement("Start Hole");
        cutHole(snakeInMesh);
        progres.finishMeasurement();
        SourceTargetMapping mapping = snakeInMesh.getMapping();
        progres.nextStage("Aligning part with head.");
        progres.startMeasurement("Start All");
        mapping.allignMapped();
        progres.finishMeasurement();
        progres.nextStage("Stitching part with head.");
        progres.startMeasurement("Start stich");
        mapping.sewTogether();
        progres.finishMeasurement();
        return false;
    }

    private SnakeOnMesh createSanke(AbstractDcel source) {
        return new SnakeOnMesh(getProjectionLoop(source), null);
    }

    private SnakeOnMesh createSanke(AbstractDcel source, LineLoop loop) {
        return new SnakeOnMesh(getProjectionLoop(source), loop);
    }

    private LoopForProjection getProjectionLoop(AbstractDcel source) {
        return new LoopForProjection(source, target);
    }

    public void cutHole(SnakeInMesh snake) {
        HoleCutter cutter = new HoleCutter(target);
        cutter.cut(snake);
    }

    public GraphicMesh getResult() {
        return target.getGraphicMesh();
    }

}
