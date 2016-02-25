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
public class PositionOnVertex extends PositionOnMesh<Vertex> {

    public PositionOnVertex(Coordinates position, Vertex part) {
        super(position, part);
    }

    @Override
    Vector3 externalDisplacment(SnaxelOnMesh snaxel) {
        return getUnderlyingPart().getNormal();
    }

    @Override
    public Collection<AbstractFace> getFacesForDisplacment(Vector3 displacment) {

        double smallest = Double.MAX_VALUE;
        Iterable<AbstractFace> toCheck;
        toCheck = getUnderlyingPart().getNeighboringFaces();
        LinkedList<AbstractFace> withSame = new LinkedList<>();
        for (AbstractFace neighboringFace : toCheck) {

            Vector3 normal = neighboringFace.getNormal();
            double cosineOfAngel = displacment.cosineOfAngel(normal);
            if (smallest > cosineOfAngel) {
                smallest = cosineOfAngel;
                withSame.clear();
                withSame.add(neighboringFace);
            } else if (smallest == cosineOfAngel) {
                withSame.add(neighboringFace);
            }
        }
        return withSame;
    }

}
