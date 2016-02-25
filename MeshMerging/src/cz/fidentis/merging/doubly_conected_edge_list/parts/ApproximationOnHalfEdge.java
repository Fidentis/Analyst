package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public class ApproximationOnHalfEdge {

    private final HalfEdge halfEdge;
    private final Coordinates position;
    private final double originWeight;
    private final double endWeight;

    public ApproximationOnHalfEdge(HalfEdge originalValues, Coordinates toAppr) {
        halfEdge = originalValues;
        position = toAppr;
        double originDistance = distance(halfEdge.getBegining(), toAppr);
        double endDistance = distance(halfEdge.getEnd(), toAppr);
        double total = originDistance + endDistance;
        originWeight = originDistance / total;
        endWeight = endDistance / total;
    }

    private double distance(Vertex from, Coordinates to) {
        Vector3 vector = new Vector3(from.position(), to);
        return vector.getLength();
    }

    private Coordinates getApproximatedTextureCoordinates(MeshFace meshFace) {
        Coordinates originTextures = getOriginTextures(meshFace);
        Coordinates endTextures = getEndTextures(meshFace);
        return new Coordinates(originTextures, originWeight, endTextures, endWeight);
    }

    private Coordinates getOriginTextures(MeshFace meshFace) {
        return halfEdge.getBegining().getTextureCoordinates(meshFace);
    }

    private Coordinates getEndTextures(MeshFace meshFace) {
        return halfEdge.getEnd().getTextureCoordinates(meshFace);
    }

    Vertex aproximateNewVertex() {
        AbstractDcel owner = halfEdge.getDCEL();
        Vertex newVertex = owner.createVertex(position, getApproximatedNormal());
        addTextures(halfEdge.getIncidentFace(), newVertex);
        addTextures(halfEdge.getTwin().getIncidentFace(), newVertex);
        return newVertex;
    }

    private void addTextures(AbstractFace face, Vertex vertex) {
        if (face.isOuterFace()) {
            return;
        }
        MeshFace meshFace = face.getMeshFace();
        if (hasTexturesFor(meshFace)) {
            Coordinates texture = getApproximatedTextureCoordinates(meshFace);
            vertex.createTextureCoordinate(texture, meshFace);
        }
    }

    private Vector3 getApproximatedNormal() {
        Vector3 originNormal = halfEdge.getBegining().getNormal();
        Vector3 endNormal = halfEdge.getEnd().getNormal();
        return new Vector3(originNormal, originWeight, endNormal, endWeight);
    }

    private boolean hasTexturesFor(MeshFace meshFace) {
        return halfEdge.getBegining().haveTexturesFor(meshFace)
                && halfEdge.getEnd().haveTexturesFor(meshFace);
    }

}
