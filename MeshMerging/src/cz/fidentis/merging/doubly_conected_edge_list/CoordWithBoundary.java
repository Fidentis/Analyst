package cz.fidentis.merging.doubly_conected_edge_list;

import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.LinkedList;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class CoordWithBoundary {

    private final Boundary boundary;
    private final double[] weights;
    private final double[] lengthsToCenter;
    private final double[] halfTangents;
    private final Vector3[] vectorsToCenter;
    private final Vertex center;
    private double sumOfWeights = 0.d;

    /**
     *
     * @param boundary
     * @param center
     */
    public CoordWithBoundary(Boundary boundary, Vertex center) {
        this.boundary = boundary;
        this.center = center;
        weights = new double[boundary.getVertexCount() + 2];
        lengthsToCenter = new double[weights.length];
        vectorsToCenter = new Vector3[weights.length];
        halfTangents = new double[weights.length];
        populate(center.position());
    }

    private void populate(Coordinates center) {
        int i = 0;
        for (Coordinates coordinates : boundary) {
            Vector3 toCenter = new Vector3(coordinates, center);
            vectorsToCenter[i] = toCenter.normalized();
            lengthsToCenter[i] = toCenter.getLength();
            i++;
        }
        int vertexCount = boundary.getVertexCount();
        for (int j = 0; j <= vertexCount; j++) {
            halfTangents[j] = getHalfAngleTan(j);
        }
        for (int j = 1; j <= vertexCount; j++) {
            double tan = halfTangents[j - 1] + halfTangents[j];
            weights[j] = tan / lengthsToCenter[j];
            sumOfWeights += weights[j];
        }
    }

    public void moveForDifernece(Boundary diff) {
        LinkedList<Coordinates> toSum = new LinkedList<>();
        for (int j = 1; j <= diff.getVertexCount(); j++) {
            double lambda = weights[j] / sumOfWeights;
            Coordinates scaled = diff.getVertex(j).scaled(lambda);
            toSum.add(scaled);
        }
        Coordinates movement = Coordinates.sum(toSum);
        center.move(movement);
    }

    private double getHalfAngleTan(int i) {
        double dotProduct = vectorsToCenter[i].dotProduct(vectorsToCenter[i + 1]);
        if (dotProduct > 1.d) {
            dotProduct = 1.d;
        }

        double crossProductMagnitude;
        crossProductMagnitude = vectorsToCenter[i].crossProductMagnitude(vectorsToCenter[i + 1]);
        return crossProductMagnitude / dotProduct;

    }

}
