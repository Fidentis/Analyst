/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.kdTree;

import cz.fidentis.comparison.icp.KdNode;
import cz.fidentis.model.Faces;
import cz.fidentis.utils.MathUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author xferkova
 */
public class KDTreeFaces implements KdTree{

    private KDTreeIndexed root;
    private List<Vector3f> indicies;
    private Faces faces;
    
    
    public KDTreeFaces(List<Vector3f> points, Faces faces){
        this.indicies = points;
        this.faces = faces;
        
        List<Vector3f> mids = faceMids(points, faces.getFacesVertIdxs());
        
        root = new KDTreeIndexed(mids);        
    }
    
    //find middles of faces
    private List<Vector3f> faceMids(List<Vector3f> indices, ArrayList<int[]> faces){
        List<Vector3f> mids = new LinkedList<>();
        
        for(int[] f: faces){
            Vector3f mid = new Vector3f();
            
            for(int i : f){
                mid.add(indices.get(i - 1));
            }
            
            mids.add(MathUtils.instance().divideVectorByNumber(mid, f.length));
        }
        
        return mids;
    }  
    
    /**
	 * Find KD-tree node whose key is nearest neighbor to key. Implements the
	 * Nearest Neighbor algorithm (Table 6.4) of
	 * 
	 * <PRE>
	 * &#064;techreport{AndrewMooreNearestNeighbor,
	 *   author  = {Andrew Moore},
	 *   title   = {An introductory tutorial on kd-trees},
	 *   institution = {Robotics Institute, Carnegie Mellon University},
	 *   year    = {1991},
	 *   number  = {Technical Report No. 209, Computer Laboratory, 
	 *              University of Cambridge},
	 *   address = {Pittsburgh, PA}
	 * }
	 * </PRE>
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @return object at node nearest to key, or null on failure
	 * 
	 * @throws KeySizeException
	 *             if key.length mismatches K
	 */
	public Object nearest(double[] key) {

		Object[] nbrs = nearest(key, 1);
		return nbrs[0];
	}

	/**
	 * Find KD-tree nodes whose keys are <I>n</I> nearest neighbors to key. Uses
	 * algorithm above. Neighbors are returned in ascending order of distance to
	 * key.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * @param n
	 *            how many neighbors to find
	 * 
	 * @return objects at node nearest to key, or null on failure
	 * 
	 * @throws KeySizeException
	 *             if key.length mismatches K
	 * @throws IllegalArgumentException
	 *             if <I>n</I> is negative or exceeds tree size
	 */
	public Object[] nearest(double[] key, int n) {

		if (n < 0 || n > root.getMcount()) {
			throw new IllegalArgumentException("Number of neighbors ("+n+") cannot"
					+ " be negative or greater than number of nodes ("+root.getMcount()+").");
		}

		if (key.length != root.getM_K()) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		Object[] nbrs = new Object[n];
		NearestNeighborList nnl = new NearestNeighborList(n);

		// initial call is with infinite hyper-rectangle and max distance
		HRect hr = HRect.infiniteHRect(key.length);
		double max_dist_sqd = Double.MAX_VALUE;
		HPointFace keyp = new HPointFace(key);

		KDNode.nnbr(root.getM_root(), keyp, hr, max_dist_sqd, 0, root.getM_K(), nnl);

		for (int i = 0; i < n; ++i) {
			KDNode kd = (KDNode) nnl.removeHighest();
			nbrs[n - i - 1] = kd.v;
		}

		return nbrs;
	}

    @Override
    public Vector3f nearestNeighbour(Vector3f p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KdNode nearestNeighborNode(Vector3f p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double nearestDistance(Vector3f p, Vector3f pNormal, boolean useRelative) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
