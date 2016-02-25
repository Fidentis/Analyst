package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcelPart;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import cz.fidentis.merging.mesh_cutting.HittedEdge;
import cz.fidentis.merging.scene.Log;

/**
 *
 * @author matej
 */
public class SnaxelInMesh extends AbstractSnaxel<SnaxelInMesh, SnakeInMesh> {

    static SnaxelInMesh getFirst(SnakeInMesh isPartOf, Vertex orignal, Coordinates position) {
        return new SnaxelInMesh(isPartOf, orignal, position);
    }

    private Vertex targetVertex;
    private Coordinates position;
    private Vertex sourceVertex;

    public void setTargetVertex(Vertex targetVertex) {
        this.targetVertex = targetVertex;
        position = targetVertex.position();
    }

    protected static SnaxelInMesh getFirst(SnakeInMesh isPartOf, SnaxelOnMesh first) {
        return new SnaxelInMesh(isPartOf, first);
    }

    public SnaxelInMesh(SnakeInMesh isPartOf, SnaxelOnMesh snaxel) {
        this(isPartOf, snaxel.getSourceVertex(), snaxel.getPosition());
    }

    SnaxelInMesh(SnakeInMesh isPartOf, Vertex orignal, Coordinates position) {
        super(isPartOf);
        sourceVertex = orignal;
        this.position = position;
    }

    SnaxelInMesh(SnaxelInMesh previos, Vertex vertex, Coordinates position) {
        super(previos);
        sourceVertex = vertex;
        this.position = position;
    }

    public Vertex getTargetVertex() {
        return targetVertex;
    }

    void insertOutgoingEdge() {
        try {
            if (edgeToNextExists()) {
                return;
            }
            insertInCorrectFace();

        } catch (Exception e) {
            Log.log(e);
            Log.log(String.valueOf(getId()));
            throw e;
        }
    }

    private void insertInCorrectFace() {
        for (AbstractFace face : targetVertex.getNeighboringFaces()) {
            if (tryInsertNextInFace(face)) {
                return;
            }
        }
    }

    private boolean tryInsertNextInFace(AbstractFace face) {
        Vector3 vector = getVectorToNext();
        AbstractIntersection project = face.project(targetVertex, vector);
        if (project.successful()) {
            procesSuccesfulHit((AbstractHit) project);
            return true;
        }
        return false;
    }

    HalfEdge getHalfEdge() {
        return targetVertex.getOwnersHalfEdge(getNext().getTargetVertex(), targetVertex);
    }

    AbstractDcel getTarget() {
        return targetVertex.getDCEL();
    }

    private boolean edgeToNextExists() {
        Vertex nextTargetVertex = getNext().getTargetVertex();
        if (nextTargetVertex == null) {
            return false;
        }
        AbstractDcel target = getTarget();
        return target.haveEdge(targetVertex, nextTargetVertex);
    }

    private void procesSuccesfulHit(AbstractHit hit) {
        Vertex newTargetVertex = hit.insertVertexOnHit();
        //targetVertex.getDCEL().refresh();
        if (hit.goesOut()) {
            insertNewSnaxel(hit, newTargetVertex);
        }
        SnaxelInMesh next = getNext();
        next.setTargetVertex(newTargetVertex);
        /*if (targetVertex.equals(newTargetVertex)) {
            System.out.println("a");
        }*/
    }

    void insertNewSnaxel(AbstractHit hit, Vertex newTargetVertex) {
        HittedEdge projectEdgeOn = null;
        projectEdgeOn = projectEdgeOn(getTargetVertex(), newTargetVertex);
        getSnake().addSnaxel(this, hit.getPositionOnMesh(), projectEdgeOn.insertVertexOnHit());
    }

    HalfEdge getTargetEdge() {
        return targetVertex.getOwnersHalfEdge(getNext().targetVertex, targetVertex);
    }

    private Vector3 getVectorToNext() {
        return new Vector3(getPosition(), getNext().getPosition());
    }

    AbstractDcel getSource() {
        return sourceVertex.getDCEL();
    }

    HittedEdge projectEdgeOn(Vertex lastHit, Vertex newProjected) {
        HalfEdge snaxelsEdge = getHalfEdgeTo();
        return snaxelsEdge.projectEdgeOn(lastHit, newProjected);
    }

    HalfEdge getHalfEdgeTo() {
        Vertex sourceVertexOfNext = getNext().getSourceVertex();
        return sourceVertex.getOwnersHalfEdge(sourceVertex, sourceVertexOfNext);
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    @Override
    public Coordinates getPosition() {
        return position;
    }

    void insertSnaxelOn(PositionOnMesh startingPoint) {
        AbstractDcelPart underlyingPart = startingPoint.getUnderlyingPart();
        position = startingPoint.getPosition();
        AbstractHit hit = AbstractHit.getHit(underlyingPart, position);
        setTargetVertex(hit.insertVertexOnHit());
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        super.appendTo(sb);
        sb.append(" Source: ");
        sb.append(getPositionIfAny(sourceVertex));
        sb.append(" Target: ");
        sb.append(getPositionIfAny(targetVertex));
        sb.append(System.lineSeparator());
    }

    private String getPositionIfAny(Vertex vertex) {
        return vertex == null ? "" : vertex.position().toString();
    }

}
