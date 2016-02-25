package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class PositionOnHalfEdge extends PositionOnMesh<HalfEdge> {

    public PositionOnHalfEdge(Coordinates position, HalfEdge part) {
        super(position, part);
    }

    @Override
    Vector3 externalDisplacment(SnaxelOnMesh snaxel) {

        HalfEdge halfEdge = getUnderlyingPart();
        double vectorLength = halfEdge.vector().getLength();

        Vector3 beginingGradien;
        final Vertex begining = halfEdge.getBegining();
        beginingGradien = scaledGradient(snaxel, begining, vectorLength);

        final Vertex end = halfEdge.getEnd();
        Vector3 endGradient = scaledGradient(snaxel, end, vectorLength);

        return Vector3.add(beginingGradien, endGradient);
    }

    private double invertedDelta(Coordinates original, Coordinates endPoint,
            double edgeLength) {

        Vector3 displacment = new Vector3(original, endPoint);
        return displacment.getLength() / edgeLength;
    }

    private Vector3 scaledGradient(SnaxelOnMesh snaxel, Vertex endPoint,
            double length) {

        //Vector3 potentialGradient = snaxel.potentialGradient(endPoint);
        Vector3 potentialGradient = endPoint.getNormal();
        double invertedDelta;
        invertedDelta = invertedDelta(snaxel.getPosition(), endPoint.position(), length);
        return potentialGradient.scaleUp(invertedDelta);
    }

    @Override
    public Collection<AbstractFace> getFacesForDisplacment(Vector3 displacment) {

        AbstractFace incidentFace = getUnderlyingPart().getIncidentFace();
        AbstractFace twinsFace = getUnderlyingPart().getTwinsFace();

        Vector3 incidentNormal = incidentFace.getNormal();
        Vector3 twinNormal = twinsFace.getNormal();
        double cosineOfTwint = displacment.cosineOfAngel(twinNormal);
        double cosineOfIncident = displacment.cosineOfAngel(incidentNormal);
        LinkedList<AbstractFace> withSame = new LinkedList<>();
        if (cosineOfIncident < cosineOfTwint) {
            withSame.add(incidentFace);
        } else if (cosineOfIncident == cosineOfTwint) {
            withSame.add(twinsFace);
            withSame.add(incidentFace);
        } else {
            withSame.add(twinsFace);
        }
        return withSame;
    }

    private static boolean returnIncident(Vector3 displacment, Vector3 twinNormal,
            Vector3 incidentNormal) {
        double cosineOfTwint = displacment.cosineOfAngel(twinNormal);
        double cosineOfIncident = displacment.cosineOfAngel(incidentNormal);
        return Math.cos(cosineOfIncident) > Math.cos(cosineOfTwint);
    }
}
