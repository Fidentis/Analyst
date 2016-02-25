package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import cz.fidentis.merging.mesh_cutting.AbstractHit;
import cz.fidentis.merging.mesh_cutting.AbstractIntersection;
import java.util.Collection;

/**
 *
 * @author matej
 */
public final class SnaxelOnMesh
        extends AbstractSnaxel<SnaxelOnMesh, SnakeOnMesh> {

    protected static SnaxelOnMesh getFirst(SnakeOnMesh isPartOf, PositionOnMesh intesection,
            Vertex originalVertex) {
        SnaxelOnMesh snaxel = new SnaxelOnMesh(isPartOf, intesection, originalVertex);
        snaxel.setNext(snaxel);
        snaxel.setPrevios(snaxel);
        return snaxel;
    }

    private PositionOnMesh laysOn;
    private final Vertex sourceVertex;

    public SnaxelOnMesh(SnaxelOnMesh previos, PositionOnMesh hit, Vertex sourceVertex) {
        super(previos);
        this.laysOn = hit;
        this.sourceVertex = sourceVertex;
    }

    private SnaxelOnMesh(SnakeOnMesh partOf, PositionOnMesh intesection, Vertex sourceVertex) {
        super(partOf);
        this.laysOn = intesection;
        this.sourceVertex = sourceVertex;
    }

    @Override
    public Coordinates getPosition() {
        return laysOn.getPosition();
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    void evolve() {
        Vector3 displacment = getDisplacment();
        evolve(displacment);
    }

    void evolve(Vector3 displacment) {

        if (displacment.equals(Vector3.ZERO_VECTOR)) {
            return;
        }

        Collection<AbstractFace> projectOn;
        projectOn = laysOn.getFacesForDisplacment(displacment);
        evolveOn(displacment, projectOn);

    }

    private void evolveOn(Vector3 displacment, Collection<AbstractFace> faces) {

        Coordinates oldPosition = laysOn.getPosition();
        AbstractHit newIntersection = null;
        Vector3 projectedDisplacmnet = null;
        for (AbstractFace face : faces) {
            projectedDisplacmnet = face.projectOn(displacment);
            if (projectedDisplacmnet.equals(Vector3.ZERO_VECTOR)) {
                return;
            }
            AbstractIntersection projection;
            projection = face.projectDisplacment(oldPosition, projectedDisplacmnet);

            if (projection.successful()) {
                newIntersection = (AbstractHit) projection;
                break;
            }
        }
        if (newIntersection == null || projectedDisplacmnet == null) {
            return;
        }

        if (newIntersection.getPosition().equals(oldPosition, 0.001d)) {
            return;
        }
        laysOn = newIntersection.getPositionOnMesh();
        getSnake().refresh(getId());
        if (newIntersection.goesOut()) {
            Vector3 dipslacmentDone;
            dipslacmentDone = new Vector3(oldPosition, laysOn.getPosition());
            Vector3 remainsOfProjected;
            remainsOfProjected = Vector3.subtract(projectedDisplacmnet, dipslacmentDone);
            double scale = displacment.getLength() / projectedDisplacmnet.getLength();
            Vector3 scaleUp = remainsOfProjected.scaleUp(scale);
            evolve(scaleUp);
        }
    }

    Vector3 getDisplacment() {
        Vector3 internalDisplacment = getInternalDisplacment();
        final Vector3 externaltDisplacment = getExternaltDisplacment();
        return Vector3.add(internalDisplacment, externaltDisplacment);
    }

    /*private Vector3 getInternalDisplacment() {
     double meanSquareDisyance = getSnake().getGlobalMeanSquereDistance();
     ArrayList<Vector3> amendedVectors = new ArrayList<>();
     for (SnaxelOnMesh snaxelOnMesh : getSnake()) {
     if (this.equals(snaxelOnMesh)) {
     continue;
     }
     Vector3 toOther = getVectorTo(snaxelOnMesh);
     double sqaureLength = toOther.getSqaureLength();
     double scale = meanSquareDisyance - sqaureLength;
     Vector3 scaled = toOther.normalized().scaleUp(scale);
     amendedVectors.add(scaled);
     }

     Vector3 meanAmendment = Vector3.weightedSumVector(amendedVectors);
     ArrayList<Vector3> midpoints = new ArrayList<>();
     for (SnaxelOnMesh snaxelOnMesh : getSnake()) {
     midpoints.add(snaxelOnMesh.getMidpointVector());
     }
     Vector3 midPoint = Vector3.weightedSumVector(midpoints);
     Vector3 internalDisplacment = Vector3.add(meanAmendment, midPoint);
     return internalDisplacment;

     }*/
    private Vector3 getInternalDisplacment() {
        double meanSquareDistance = getSnake().getGlobalMeanSquereDistance();
        Vector3 fromNext = getScaledVectorFrom(getNext(), meanSquareDistance);
        Vector3 fromPrevious;
        fromPrevious = getScaledVectorFrom(getPrevios(), meanSquareDistance);
        Vector3 midpointVector = getMidpointVector();
        return midpointVector;
        //return Vector3.add(midpointVector, Vector3.add(fromNext, fromPrevious));
    }

    private Vector3 getExternaltDisplacment() {
        return laysOn.externalDisplacment(this);
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        super.appendTo(sb);
        sb.append(" Source: ");
        sb.append(getIndexIfAny(sourceVertex));
        sb.append(" Position: ");
        sb.append(laysOn.getPosition());
        sb.append(System.lineSeparator());
    }

    PositionOnMesh getPositionOnMesh() {
        return laysOn;
    }

    double getSquareDistanceTo(SnaxelOnMesh other) {
        Vector3 toOther = new Vector3(getPosition(), other.getPosition());
        return toOther.getSqaureLength();
    }

    double getSquareDistanceFrom(SnaxelOnMesh other) {
        Vector3 toOther = new Vector3(other.getPosition(), getPosition());
        return toOther.getSqaureLength();
    }

    private Vector3 getVectorTo(SnaxelOnMesh snaxelOnMesh) {
        return new Vector3(getPosition(), snaxelOnMesh.getPosition());
    }

    private Vector3 getScaledVectorFrom(SnaxelOnMesh other, double meanSquareDistance) {
        Vector3 fromOther = other.getVectorTo(this);
        double sqaureLength = fromOther.getSqaureLength();
        return fromOther.normalized().scaleUp(meanSquareDistance - sqaureLength);
    }

    private Vector3 getMidpointVector() {
        Vector3 vectorToNext = getVectorTo(getNext());
        Vector3 vectorToPrevious = getVectorTo(getPrevios());
        return Vector3.add(vectorToNext, vectorToPrevious).scaleDown(2.0);
    }
}
