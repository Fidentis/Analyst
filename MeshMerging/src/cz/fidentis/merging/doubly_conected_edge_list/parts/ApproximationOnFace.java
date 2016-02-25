package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.Vector3;

/**
 *
 * @author matej
 */
public class ApproximationOnFace {

    private final AbstractFace originalFace;
    private final Vertex vertexA;
    private final Vertex vertexB;
    private final Vertex vertexC;
    private final HalfEdge AB;
    private final HalfEdge BC;
    private final HalfEdge CA;
    private final Vector3 originalVertical;
    private final double originalVerticalSqueredLength;
    private double weightA;
    private double weightB;
    private double weightC;

    public ApproximationOnFace(AbstractFace face) {
        originalFace = face;
        originalVertical = face.getVertcial();
        originalVerticalSqueredLength = originalVertical.getSqaureLength();
        AB = face.getIncidentHalfEdge();
        BC = AB.getNext();
        CA = BC.getNext();
        vertexA = AB.getBegining();
        vertexB = BC.getBegining();
        vertexC = CA.getBegining();

    }

    private double getWeight(HalfEdge halfEdge, Coordinates toAprroximate) {
        Vector3 vector1 = new Vector3(toAprroximate, halfEdge.getEndPosition());
        Vector3 vector2 = new Vector3(toAprroximate, halfEdge.getBeginingPosition());
        Vector3 newVertical = vector1.crossProduct(vector2);
        double dotProduct = originalVertical.dotProduct(newVertical);
        return Math.abs(dotProduct / originalVerticalSqueredLength);
    }

    public Vector3 getWeightedNormal() {
        Vector3 normalA = vertexA.getNormal();
        Vector3 normalB = vertexB.getNormal();
        Vector3 normalC = vertexC.getNormal();
        return new Vector3(normalA, weightA, normalB, weightB, normalC, weightC);
    }

    public Vertex createApproximatedVertex(Coordinates coordinates) {
        weightA = getWeight(BC, coordinates);
        weightB = getWeight(CA, coordinates);
        weightC = getWeight(AB, coordinates);

        AbstractDcel dcel = originalFace.getDCEL();
        Vertex newVertex = dcel.createVertex(coordinates, getWeightedNormal());
        MeshFace face = originalFace.getMeshFace();
        if (originalFace.haveTextures()) {
            Coordinates texture = getWeightedTextureCoordinates(face);
            newVertex.createTextureCoordinate(texture, face);
        }
        return newVertex;
    }

    public Coordinates getWeightedTextureCoordinates(MeshFace meshFace) {
        Coordinates textureA = vertexA.getTextureCoordinates(meshFace);
        Coordinates textureB = vertexB.getTextureCoordinates(meshFace);
        Coordinates textureC = vertexC.getTextureCoordinates(meshFace);

        return new Coordinates(textureA, weightA, textureB, weightB, textureC, weightC);

    }

}
