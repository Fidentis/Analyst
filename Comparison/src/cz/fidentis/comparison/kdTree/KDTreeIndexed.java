/**
 * %SVN.HEADER%
 * 
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package cz.fidentis.comparison.kdTree;

import cz.fidentis.comparison.icp.KdNode;
import cz.fidentis.utils.MathUtils;
import java.util.List;
import java.util.Vector;
import javax.vecmath.Vector3f;

/**
 * KDTree is a class supporting KD-tree insertion, deletion, equality search,
 * range search, and nearest neighbor(s) using double-precision floating-point
 * keys. Splitting dimension is chosen naively, by depth modulo K. Semantics are
 * as follows:
 * 
 * <UL>
 * <LI>Two different keys containing identical numbers should retrieve the same
 * value from a given KD-tree. Therefore keys are cloned when a node is
 * inserted. <BR>
 * <BR>
 * <LI>As with Hashtables, values inserted into a KD-tree are <I>not</I> cloned.
 * Modifying a value between insertion and retrieval will therefore modify the
 * value stored in the tree.
 *</UL>
 * 
 * @author Simon Levy, Bjoern Heckel
 * @version %I%, %G%
 * @since JDK1.2
 */
public class KDTreeIndexed implements KdTree{

	// K = number of dimensions
	private int m_K;

	// root of KD-tree
	private KDNode m_root;

	// count of nodes
	private int m_count;

        private KDTreeIndexed(int k, List<Vector3f> points){
            m_K = k;
            m_root = null;
            for(int i = 0; i < points.size(); i++){
                Vector3f v = points.get(i);
                double[] key = new double[]{v.x, v.y, v.z};
                KdNode info = new KdNode(0, v, i, null);
                insert(key, info);
            }
        }
        
        public KDTreeIndexed(List<Vector3f> points){
            this(3, points);
        }

	/**
	 * Insert a node in a KD-tree. Uses algorithm translated from 352.ins.c of
	 * 
	 * <PRE>
	 *   &#064;Book{GonnetBaezaYates1991,                                   
	 *     author =    {G.H. Gonnet and R. Baeza-Yates},
	 *     title =     {Handbook of Algorithms and Data Structures},
	 *     publisher = {Addison-Wesley},
	 *     year =      {1991}
	 *   }
	 * </PRE>
	 * 
	 * @param key
	 *            key for KD-tree node
	 * @param value
	 *            value at that key
	 * 
	 * @throws KeySizeException
	 *             if key.length mismatches K
	 * @throws KeyDuplicateException
	 *             if key already in tree
	 */
	public void insert(double[] key, Object value) {

		if (key.length != m_K) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		else
			m_root = KDNode.ins(new HPoint(key), value, m_root, 0, m_K);

		m_count++;
	}

	/**
	 * Find KD-tree node whose key is identical to key. Uses algorithm
	 * translated from 352.srch.c of Gonnet & Baeza-Yates.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @return object at key, or null if not found
	 * 
	 * @throws KeySizeException
	 *             if key.length mismatches K
	 */
	public Object search(double[] key) {

		if (key.length != m_K) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		KDNode kd = KDNode.srch(new HPoint(key), m_root, m_K);

		return (kd == null ? null : kd.v);
	}

	/**
	 * Delete a node from a KD-tree. Instead of actually deleting node and
	 * rebuilding tree, marks node as deleted. Hence, it is up to the caller to
	 * rebuild the tree as needed for efficiency.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @throws KeySizeException
	 *             if key.length mismatches K
	 * @throws KeyMissingException
	 *             if no node in tree has key
	 */
	public void delete(double[] key) {

		if (key.length != m_K) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		else {

			KDNode t = KDNode.srch(new HPoint(key), m_root, m_K);
			if (t == null) {
				throw new RuntimeException("KDTree: key missing!");
			} else {
				t.deleted = true;
			}

			m_count--;
		}
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

		if (n < 0 || n > m_count) {
			throw new IllegalArgumentException("Number of neighbors ("+n+") cannot"
					+ " be negative or greater than number of nodes ("+m_count+").");
		}

		if (key.length != m_K) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		Object[] nbrs = new Object[n];
		NearestNeighborList nnl = new NearestNeighborList(n);

		// initial call is with infinite hyper-rectangle and max distance
		HRect hr = HRect.infiniteHRect(key.length);
		double max_dist_sqd = Double.MAX_VALUE;
		HPoint keyp = new HPoint(key);

		KDNode.nnbr(m_root, keyp, hr, max_dist_sqd, 0, m_K, nnl);

		for (int i = 0; i < n; ++i) {
			KDNode kd = (KDNode) nnl.removeHighest();
			nbrs[n - i - 1] = kd.v;
		}

		return nbrs;
	}

	/**
	 * Range search in a KD-tree. Uses algorithm translated from 352.range.c of
	 * Gonnet & Baeza-Yates.
	 * 
	 * @param lowk
	 *            lower-bounds for key
	 * @param uppk
	 *            upper-bounds for key
	 * 
	 * @return array of Objects whose keys fall in range [lowk,uppk]
	 * 
	 * @throws KeySizeException
	 *             on mismatch among lowk.length, uppk.length, or K
	 */
	public Object[] range(double[] lowk, double[] uppk) {

		if (lowk.length != uppk.length) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		else if (lowk.length != m_K) {
			throw new RuntimeException("KDTree: wrong key size!");
		}

		else {
			Vector<KDNode> v = new Vector<KDNode>();
			KDNode.rsearch(new HPoint(lowk), new HPoint(uppk), m_root, 0, m_K, v);
			Object[] o = new Object[v.size()];
			for (int i = 0; i < v.size(); ++i) {
				KDNode n = v.elementAt(i);
				o[i] = n.v;
			}
			return o;
		}
	}

	public String toString() {
		return m_root.toString(0);
	}

    @Override
    public Vector3f nearestNeighbour(Vector3f p) {
        KdNode info = nearestNeighborNode(p);
        
        return info.getId() ;
    }

    private KdNode nearestNeighborNode(Vector3f p) {
        double[] key = new double[]{p.x, p.y, p.z};
        return (KdNode) nearest(key);
    }

    @Override
    public double nearestDistance(Vector3f p, Vector3f pNormal, boolean useRelative) {
       Vector3f found = nearestNeighbour(p);
        
        if(found == null){
            //error
            return Double.MAX_VALUE;
        }
        
        float sign = 1f;
        
        if(useRelative){
           sign = getSign(p, found, pNormal);
        }
        
        return sign * MathUtils.instance().distancePoints(found, p);
    }
    
    /**
     * Returns the sign for given points, depending on whether the point is
     * 'ín front' (+) or 'behind' (-) the mesh.
     * 
     * @param point - vertex for which we want to get the sign
     * @param nearest - nearest neighbour for 'point' in reference mesh
     * @param pointNormal - normal of 'point'
     * @return sign, either plus (+) if vertex is 'in front' of reference mesh or minus (-) if 
     *          vertex is 'behind' reference mesh.
     */
    private float getSign(Vector3f point, Vector3f nearest, Vector3f pointNormal){
        Vector3f pointToNearest = new Vector3f(point.x - nearest.x, point.y - nearest.y, point.z - nearest.z);
        
        return Math.signum(pointToNearest.dot(pointNormal));
    }
    
    
    /**
      * Finds nearest neighbor of Point p in KdTree given by root.
      * 
      * @param p - point of which we want to find nearest neighbor in the tree.
      * @return  - index representing nearest neighbor of point p in original point list from which kdtree was created
      */
    public Integer nearestIndex(Vector3f p){
        KdNode nn = nearestNeighborNode(p);
        
        if(nn == null){
            return null;
        }
        
        return nn.getIndex();
    }
    
    /**
     * Check if tree contains given point
     * @param p - check point in kdtree
     * @return true if p is in kdtree, false otherwise
     */
    public boolean containPoint(Vector3f p){
        Vector3f found = nearestNeighbour(p);
        
        if(found == null){
            return false;
        }
        
        return found.equals(p);
    }
    
    protected int getMcount(){
        return m_count;
    }

    protected int getM_K() {
        return m_K;
    }

    public KDNode getM_root() {
        return m_root;
    }
    
       
    
}
