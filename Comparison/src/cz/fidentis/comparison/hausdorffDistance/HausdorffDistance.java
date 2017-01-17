package cz.fidentis.comparison.hausdorffDistance;

import cz.fidentis.comparison.kdTree.KdTree;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.SortUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;

/**
 * Created with IntelliJ IDEA. 
 * User: Zuzana Ferkova Date: 10.3.2013 Time: 2:12
 * Class computing Hausdorff Distance between 2 meshes.
 */
public class HausdorffDistance {

    private static HausdorffDistance unique;
    private static final int USED_THREADS = Runtime.getRuntime().availableProcessors();

    public static HausdorffDistance instance() {
        if (unique == null) {
            unique = new HausdorffDistance();
        }
        return unique;
    }

    private HausdorffDistance() {

    }

    /**
     * Method computing HD distances between main face and compared mesh. Uses
     * KdTree data structure for main face, to find the nearest neighbours of
     * each point in compared face efectively.
     *
     * It finds nearest neighbour for each point
     * and then adds the distance between the point and its neighbour into the
     * list.
     * 
     * If user chooses to use relative coordinates, it will compute distance with
     * sign, minus (-) if given point is 'behind' mesh and plus (+) if given
     * point is 'in front' of the mesh. To compute the location of point
     * normals of vertices of compared face are used.
     *
     * @param mainF - KdTree contaning points of main mesh
     * @param comparedF - list of points of compared mesh
     * @param comparedFnormals - list containing normals of vertices of compared face
     * @param useRelative - defines whether to return relative or absolute values
     * @return - list of HD, point in index 0 in comparedF has its HD in 0th
     * position in the list
     */
    public List<Float> hDistance(KdTree mainF, List<Vector3f> comparedF, List<Vector3f> comparedFnormals, boolean useRelative) {
        List<Future<Float>> computeDist = new LinkedList<>();
        ExecutorService executor = Executors.newFixedThreadPool(USED_THREADS);
        List<Float> distance = new ArrayList<>(comparedF.size());

        for (int i = 0; i < comparedF.size(); i++) {
            Future<Float> dist = executor.submit(new SignedNearestNeighborCallable(mainF, comparedF.get(i), comparedFnormals.get(i), useRelative));
            computeDist.add(dist);
            
            /*neighbour = mainF.nearestNeighbour(comparedF.get(i));

            
            if (comparedFnormals != null && useRelative) {
                sign = getSign(comparedF.get(i), neighbour, comparedFnormals.get(i));
            }
            distance.add(new Float(sign * MathUtils.instance().distancePoints(comparedF.get(i), neighbour)));*/
        }
        
         executor.shutdown();
        
        for(Future<Float> f : computeDist){
            try {
                distance.add(f.get());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(HausdorffDistance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       

        return distance;
    }

}
