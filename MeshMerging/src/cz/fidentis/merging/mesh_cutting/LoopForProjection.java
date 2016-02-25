package cz.fidentis.merging.mesh_cutting;

import cz.fidentis.merging.bounding.bvh.BVH;
import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.HalfEdge;
import cz.fidentis.merging.doubly_conected_edge_list.parts.Vertex;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public final class LoopForProjection implements Iterable<Ray> {

    private HalfEdge edgeForLooping;
    private Vertex firstProjected;
    private final BVH bvh;
    private final double centerX;
    private final double centerZ;
    private final boolean isCurved;
    private final Vector3 mainNormal;

    public LoopForProjection(AbstractDcel source, AbstractDcel target) {
        bvh = new BVH(target.getFaces());
        centerX = (bvh.getMaxX() + bvh.getMinX()) / 2.d;
        centerZ = (bvh.getMaxZ() + bvh.getMinZ()) / 2.d;
        edgeForLooping = source.getOuterEdge();

        Collection<Coordinates> relatedPositions;
        relatedPositions = source.getOuterFace().getRelatedPositions();
        LinkedList<Coordinates> contour = new LinkedList<Coordinates>(relatedPositions);
        Coordinates center = Coordinates.weightedSum(relatedPositions);
        Coordinates previos = contour.getLast();
        LinkedList<Vector3> normals = new LinkedList();
        for (Coordinates current : contour) {
            Vector3 left = new Vector3(center, previos);
            Vector3 right = new Vector3(center, current);
            normals.add(left.crossProduct(right));
            previos = current;
        }

        mainNormal = Vector3.sumToVector(normals).normalized();

        double sum = 0;
        for (Vector3 normal : normals) {
            sum += mainNormal.cosineOfAngel(normal);
        }
        double deviation = sum / normals.size();
        isCurved = deviation < 0.6d;

    }

    public BVH getBvh() {
        return bvh;
    }

    private Vector3 getProjectionDirection(Vertex source) {
        if (isCurved) {
            Coordinates position = source.position();
            return new Vector3(centerX - position.getX(), 0.d, centerZ - position.getZ());
        } else {
            return mainNormal;
        }
    }

    public Vertex getCurrentVertex() {
        return edgeForLooping.getBegining();
    }

    public boolean hasUnprojected() {
        return !firstProjected.equals(getCurrentVertex());
    }

    public Ray getCurrentRay() {
        Vertex end = edgeForLooping.getEnd();
        return new Ray(end.position(), getProjectionDirection(end));
    }

    public void initFirstProjection() {
        if (firstProjected == null) {
            firstProjected = edgeForLooping.getBegining();
        }
    }

    @Override
    public Iterator<Ray> iterator() {
        return new Iterator<Ray>() {

            @Override
            public boolean hasNext() {
                return !edgeForLooping.getEnd().equals(firstProjected);
            }

            @Override
            public Ray next() {
                Ray currentRay = getCurrentRay();
                edgeForLooping = edgeForLooping.getNext();
                return currentRay;
            }

            @Override
            public void remove() {
               // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    public void removeCurrentFromLoop() {
        HalfEdge previous = edgeForLooping.getPrevious();
        HalfEdge prePrevious = previous.getPrevious();
        Vertex end = previous.getEnd();
        if (end.haveOnlyTwoOutgoing()) {
            end.removeFromDcel();
        } else {
            previous.removeFromDcelAndReconect();
        }
        edgeForLooping = prePrevious.getNext();
    }

}
