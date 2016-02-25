package cz.fidentis.merging.mesh_cutting.snake;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractFace;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author matej
 */
public class PositionOnFace extends PositionOnMesh<AbstractFace> {

    public PositionOnFace(Coordinates position, AbstractFace part) {
        super(position, part);
    }

    @Override
    Vector3 externalDisplacment(SnaxelOnMesh snaxel) {
        LinkedList<Vector3> gradients = new LinkedList<>();
        LinkedList<Double> displacments = new LinkedList<>();
        double displacmentSum = 0.d;
        Coordinates originalPosition = snaxel.getPosition();
        for (Vertex vertex : getUnderlyingPart().incidentVertices()) {
            Vector3 displacment;
            displacment = new Vector3(vertex.position(), originalPosition);
            final double length = displacment.getLength();
            displacmentSum += length;
            displacments.addLast(length);

            //Vector3 gradient = snaxel.potentialGradient(extreme);
            Vector3 gradient = vertex.getNormal();
            gradients.addLast(gradient);
        }

        LinkedList<Vector3> scaledGradients = new LinkedList<>();
        while (gradients.size() > 0) {
            Vector3 gradient = gradients.removeFirst();
            Double displacment = displacments.removeFirst();
            Vector3 scaledGradient = gradient.scaleUp(displacment / displacmentSum);
            scaledGradients.add(scaledGradient);
        }

        return Vector3.sumToVector(scaledGradients);
    }

    @Override
    Collection<AbstractFace> getFacesForDisplacment(Vector3 displacment) {
        LinkedList<AbstractFace> result = new LinkedList<>();
        result.add(getUnderlyingPart());
        return result;
    }

}
