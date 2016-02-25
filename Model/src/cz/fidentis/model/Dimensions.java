package cz.fidentis.model;

// Dimensions.java

import java.util.ArrayList;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class Dimensions {
    // edge coordinates

    private double leftPt, rightPt;   // on x-axis
    private double topPt, bottomPt;   // on y-axis
    private double farPt, nearPt;     // on z-axis
    private ArrayList<Vector3f> boundingBox = new ArrayList<Vector3f>();

    // for reporting
    // private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
    /**
     *
     */
    public Dimensions() {
        leftPt = 0.0f;
        rightPt = 0.0f;
        topPt = 0.0f;
        bottomPt = 0.0f;
        farPt = 0.0f;
        nearPt = 0.0f;
    }  // end of Dimensions()

    /**
     * Initialize the model's edge coordinates
     * @param vert
     */
    public void set(Vector3f vert) // initialize the model's edge coordinates
    {
        rightPt = vert.getX();
        leftPt = vert.getX();

        topPt = vert.getY();
        bottomPt = vert.getY();

        nearPt = vert.getZ();
        farPt = vert.getZ();
    }  // end of set()

    /**
     * Update the edge coordinates using vert.
     * @param vert
     */
    public void update(Vector3f vert) // update the edge coordinates using vert
    {
        if (vert.getX() > rightPt) {
            rightPt = vert.getX();
        }
        if (vert.getX() < leftPt) {
            leftPt = vert.getX();
        }

        if (vert.getY() > topPt) {
            topPt = vert.getY();
        }
        if (vert.getY() < bottomPt) {
            bottomPt = vert.getY();
        }

        if (vert.getZ() > nearPt) {
            nearPt = vert.getZ();
        }
        if (vert.getZ() < farPt) {
            farPt = vert.getZ();
        }
    }  // end of update()

 
    /**
     *
     * @return original center of the model.
     */
    public Vector3f getOriginalCenter() {
        float xc = (float) (rightPt + leftPt) / 2.0f;
        float yc = (float) (topPt + bottomPt) / 2.0f;
        float zc = (float) (nearPt + farPt) / 2.0f;
        return new Vector3f(xc, yc, zc);
    } // end of getCenter()

    
    /**
     *
     * @return centralized Bounding box of the model.
     */
    public ArrayList<Vector3f> getCentralizedBoundingBox() {
        ArrayList<Vector3f> originalBoundingBox = new ArrayList<Vector3f>();
        Vector3f center = getOriginalCenter();
        originalBoundingBox.add(new Vector3f((float) rightPt - center.getX(), (float) topPt - center.getY(), (float) farPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) rightPt - center.getX(), (float) topPt - center.getY(), (float) nearPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) rightPt - center.getX(), (float) bottomPt - center.getY(), (float) nearPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) rightPt - center.getX(), (float) bottomPt - center.getY(), (float) farPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) leftPt - center.getX(), (float) bottomPt - center.getY(), (float) farPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) leftPt - center.getX(), (float) topPt - center.getY(), (float) farPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) leftPt - center.getX(), (float) topPt - center.getY(), (float) nearPt - center.getZ()));
        originalBoundingBox.add(new Vector3f((float) leftPt - center.getX(), (float) bottomPt - center.getY(), (float) nearPt - center.getZ()));

        return originalBoundingBox;
    }
    
        public ArrayList<Vector3f> getOriginalBoundingBox() {
        ArrayList<Vector3f> originalBoundingBox = new ArrayList<Vector3f>();
        Vector3f center = getOriginalCenter();
        originalBoundingBox.add(new Vector3f((float) rightPt, (float) topPt, (float) farPt));
        originalBoundingBox.add(new Vector3f((float) rightPt, (float) topPt, (float) nearPt));
        originalBoundingBox.add(new Vector3f((float) rightPt, (float) bottomPt, (float) nearPt));
        originalBoundingBox.add(new Vector3f((float) rightPt, (float) bottomPt, (float) farPt));
        originalBoundingBox.add(new Vector3f((float) leftPt, (float) bottomPt, (float) farPt));
        originalBoundingBox.add(new Vector3f((float) leftPt, (float) topPt, (float) farPt));
        originalBoundingBox.add(new Vector3f((float) leftPt, (float) topPt, (float) nearPt));
        originalBoundingBox.add(new Vector3f((float) leftPt, (float) bottomPt, (float) nearPt));

        return originalBoundingBox;
    }
    

    /**
     *
     * @return bounding box of a model.
     */
    public ArrayList<Vector3f> getBoundingBox() {
        return boundingBox;
    }

    /**
     *
     * @param boundingBox bounding box of a model.
     */
    public void setBoundingBox(ArrayList<Vector3f> boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    /**
     *
     * @return triangulated bounding box - list of triples of vertices.
     */
    public ArrayList<Vector3f[]> triangulateBoundingBox() {
        ArrayList<Vector3f[]> bbTriangelated = new ArrayList<Vector3f[]>();
        Vector3f[] t = new  Vector3f[3];
        t[0] = boundingBox.get(0);t[1] =boundingBox.get(1);t[2] =boundingBox.get(2);
        bbTriangelated.add(t);
        t= new  Vector3f[3]; t[0] = boundingBox.get(0);t[1] =boundingBox.get(2);t[2] =boundingBox.get(3);
        bbTriangelated.add(t);
        t= new  Vector3f[3];t[0] = boundingBox.get(1);t[1] =boundingBox.get(7);t[2] =boundingBox.get(2);
        bbTriangelated.add(t);
        t= new  Vector3f[3];t[0] = boundingBox.get(1);t[1] =boundingBox.get(7);t[2] =boundingBox.get(6);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(6);t[1] =boundingBox.get(4);t[2] =boundingBox.get(5);
        bbTriangelated.add(t);
        t= new  Vector3f[3];t[0] = boundingBox.get(6);t[1] =boundingBox.get(4);t[2] =boundingBox.get(7);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(3);t[1] =boundingBox.get(5);t[2] =boundingBox.get(4);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(3);t[1] =boundingBox.get(5);t[2] =boundingBox.get(0);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(1);t[1] =boundingBox.get(5);t[2] =boundingBox.get(0);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(1);t[1] =boundingBox.get(5);t[2] =boundingBox.get(6);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(2);t[1] =boundingBox.get(4);t[2] =boundingBox.get(3);
        bbTriangelated.add(t);
       t= new  Vector3f[3]; t[0] = boundingBox.get(2);t[1] =boundingBox.get(4);t[2] =boundingBox.get(7);
        bbTriangelated.add(t);
        
        return bbTriangelated;
    }
    
}  // end of Dimensions class