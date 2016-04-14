/*
 * Facial comparison and alignment algorithms based on FIDENTIS (http://fidents.cz) software,
 * developed at Laboratory of Human Computer Interaction, Department of Computer Graphics and Design, 
 * Faculty of Informatics, Masaryk University, Brno (http://decibel.fi.muni.cz) 
 */

package cz.fidentis.comparison.icp;

import Jama.Matrix;
import com.jogamp.graph.math.Quaternion;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.MeshUtils;
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
import org.netbeans.api.progress.ProgressHandle;



/**
 * Created with IntelliJ IDEA. User: Zuzana Ferkova Date: 2/22/13 Time: 6:56 PM 
 * 
 * Class to align meshes using ICP algorithm. Customized for multi-threading.
 */
public class Icp {

   // private static int maxIteration = 50;
    private static Icp unique;
    private ProgressHandle p;
    private static int USED_THREADS = 1;
   // private boolean scale = false;


    public static Icp instance() {
        if (unique == null) {
            unique = new Icp();
        }
        return unique;
    }

    private Icp() {
    }

    //progress bar of application, currently not used
    public void setP(ProgressHandle p) {
        this.p = p;
    }

    /**
     * Computes relative coordinates for given mesh. 
     * Relative coordinates mark distance from the center of the mesh 
     * for each vertex, rather than its coordinates in world space.
     * 
     * @param mesh - point cloud representation of mesh for which we want to compute relative coordinates
     * @param centroid - center of given mesh
     * @return list of relative coordinates
     */
    private List<Vector3f> relativeCord(List<Vector3f> mesh, float[] centroid) {
        List<Vector3f> relative = new ArrayList<Vector3f>(mesh.size());
        float x, y, z;

        for (Vector3f p : mesh) {
            x = p.getX() - centroid[0];
            y = p.getY() - centroid[1];
            z = p.getZ() - centroid[2];

            relative.add(new Vector3f(x, y, z));
        }

        return relative;
    }

    /**
     * Computes sum matrix for each vertex of compared face. For more inforamtion
     * read original paper on ICP.
     * 
     * @param p - vertex from compared mesh
     * @return sum matrix for given point
     */
    private Matrix sumMatrixComp(Vector3f p) {
        return new Matrix(new double[][]{{0, -p.getX(), -p.getY(), -p.getZ()},
            {p.getX(), 0, p.getZ(), -p.getY()},
            {p.getY(), -p.getZ(), 0, p.getX()},
            {p.getZ(), p.getY(), -p.getX(), 0}});
    }

    /**
     * Computes sum matrix for each vertex of main face. For more information
     * read original paper on ICP.
     * 
     * @param p - vertex from main mesh
     * @return sum matrix for given point.
     */
    private Matrix sumMatrixMain(Vector3f p) {
        return new Matrix(new double[][]{{0, -p.getX(), -p.getY(), -p.getZ()},
            {p.getX(), 0, -p.getZ(), p.getY()},
            {p.getY(), p.getZ(), 0, -p.getX()},
            {p.getZ(), -p.getY(), p.getX(), 0}});
    }

    /**
     * Conjugates given quaternion.
     * 
     * @param q - quaternion to be conjugate
     * @return conjugated quaternion
     */
    private Quaternion conjugateQ(Quaternion q) {
        return new Quaternion(-q.getX(), -q.getY(), -q.getZ(), q.getW());
    }


    /**
     * ICP with rotations, transformations and scaling points in compF as close to mainF as
     * possible. Uses k-D Tree to search for nearest neighbours, and to find the
     * pairing between both meshes. Uses quaternions for rotations.
     * 
     * Preliminary allows to only use certain amount of samples from compF to computed 
     * ICP on (hence user doesn't need to use all vertices).
     *
     * Modifies the points in parameter Lists.
     *
     * @param mainF - Main Face in KdTree data structure
     * @param compF - Compared Face which will be aligned to Main Face
     * @param samples - vertices to compute transformation on
     * @param error - When difference of two meshes drops below the error rate,
     * the computation stops.
     */
    public List<ICPTransformation> icp(KdTree mainF, List<Vector3f> compF, List<Vector3f> samples, float error, int maxIteration, boolean scale) {
        
        Float prevMean = null;
        int currentIteration = 0;
        float unit = 100 / (maxIteration + 1);
        ICPTransformation transformation = null;
        List<ICPTransformation> trans = new ArrayList<ICPTransformation>(maxIteration);
        
        do {
            if(maxIteration > 0){
                if(p != null){
                    p.progress((int) (unit*currentIteration));
                }
            }
            
            if(transformation != null){
                prevMean = transformation.getMeanD();
            }
            
            transformation = icpIteration(mainF, samples, scale);
            trans.add(transformation);
            
            applyTransformation(compF, transformation, scale);
           
             
            currentIteration++;
          } while (currentIteration <= maxIteration
                && (prevMean == null || Math.abs(prevMean - transformation.getMeanD()) > error)
                && !Thread.currentThread().isInterrupted());
        
        return trans;
    }
    
    /**
     * Performs single ICP iteration and returns computed transformation (translation, rotation, scale and meanD(istance) value.
     * 
     * @param mainF - kdTree containing vertices of mesh to which compF will be aligned to
     * @param compF - vertices of mesh to be aligned
     * @return computed transformation (translation, rotation, scale and meanD(istance)) for single ICP iteration
     */
    public ICPTransformation icpIteration(KdTree mainF, List<Vector3f> compF, boolean scale) {
        ICPTransformation transformations;

        float meanX, meanY, meanZ, meanD, x, y, z;
        float sxUp, sxDown;
        float scaleFactor;

        List<Vector3f> near = new ArrayList<Vector3f>(compF.size());
        List<Vector3f> comp2 = new ArrayList<Vector3f>(compF.size());
        ExecutorService executor = Executors.newFixedThreadPool(USED_THREADS);
        List<Future<Vector3f>> findNear = new LinkedList<Future<Vector3f>>();

        meanX = 0;
        meanY = 0;
        meanZ = 0;
        meanD = 0;
        sxUp = 0;
        sxDown = 0;
        scaleFactor = 0;


        for (Vector3f compF1 : compF) {
            //computes nearest neighbors
            Future<Vector3f> nn = executor.submit(new NearestNeighborCallable(mainF, compF1));
            findNear.add(nn);
            //Vector3f nn = mainF.nearestNeighbour(compF1);
            /*x = MathUtils.instance().distanceCoordinates(compF1.getX(), nn.getX());
            y = MathUtils.instance().distanceCoordinates(compF1.getY(), nn.getY());
            z = MathUtils.instance().distanceCoordinates(compF1.getZ(), nn.getZ());
            meanX += x;
            meanY += y;
            meanZ += z;
            meanD += (float) MathUtils.instance().distancePoints(compF1, nn);
            near.add(nn);       //create corespondence between nearest neighbor and points in compF
            comp2.add(compF1);*/
        }
        
        for(int i = 0; i < compF.size();i++){
            try {
                Vector3f nn = findNear.get(i).get();
                x = MathUtils.instance().distanceCoordinates(compF.get(i).getX(), nn.getX());
                y = MathUtils.instance().distanceCoordinates(compF.get(i).getY(), nn.getY());
                z = MathUtils.instance().distanceCoordinates(compF.get(i).getZ(), nn.getZ());
                meanX += x;
                meanY += y;
                meanZ += z;
                meanD += (float) MathUtils.instance().distancePoints(compF.get(i), nn);
                near.add(nn);       //create corespondence between nearest neighbor and points in compF
                comp2.add(compF.get(i));
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Icp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        executor.shutdown();

        meanX /= compF.size();
        meanY /= compF.size();
        meanZ /= compF.size();
        meanD /= compF.size();
        //end computing translation paramters

        //start computing rotations
        float[] mainCenter, compCenter;

        mainCenter = MeshUtils.instance().computeCentroid(near);
        compCenter = MeshUtils.instance().computeCentroid(comp2);

        List<Vector3f> relativeM = relativeCord(near, mainCenter);
        List<Vector3f> relativeC = relativeCord(comp2, compCenter);

        Matrix m = new Matrix(4, 4);
        Matrix tmp;

        for (int i = 0; i < relativeC.size(); i++) {
            tmp = sumMatrixComp(relativeC.get(i)).transpose().times(sumMatrixMain(relativeM.get(i)));
            m = m.plus(tmp);
        }

        Matrix eigD = m.eig().getD();
        Matrix eigM = m.eig().getV();

        int max = 0;
        for (int i = 0; i < 4; i++) {
            if (eigD.get(max, max) <= eigD.get(i, i)) {
                max = i;
            }
        }

        Quaternion q = new Quaternion((float) eigM.get(1, max), (float) eigM.get(2, max), (float) eigM.get(3, max), (float) eigM.get(0, max));
        q.normalize();
        //end computing rotations

        //start computing scale parameters
        if (scale) {
            Matrix rotationMatrix = MathUtils.instance().quaternionToMatrix(q);
            Matrix matrixPointMain, matrixPointCompare;

            for (int i = 0; i < relativeM.size(); i++) {
                matrixPointMain = MathUtils.instance().pointToMatrix(relativeM.get(i));
               // matrixPointCompare = MathUtils.instance().pointToMatrix(relativeC.get(i)).times(rotationMatrix);
                matrixPointCompare = MathUtils.instance().pointToMatrix(relativeC.get(i)).times(rotationMatrix);

                sxUp += matrixPointMain.transpose().times(matrixPointCompare).get(0, 0);
                sxDown += matrixPointCompare.transpose().times(matrixPointCompare).get(0, 0);
            }

            scaleFactor = sxUp / sxDown;
        }
            //end computing scale paramters

        transformations = new ICPTransformation(new Vector3f(meanX, meanY, meanZ), scaleFactor, q, meanD);

        return transformations;
    }
    
    /**
     * Applies computed transformation to given model. Only applies scale if it's allowed in ICP class.
     * 
     * @param compF - mesh to apply the transformation to
     * @param transformation - computed transformations to be applied
     */
    public void applyTransformation(List<Vector3f> compF, ICPTransformation transformation, boolean scale){
        Vector3f p1;
        Quaternion qCopy;
        /*Quaternion qq = new Quaternion(transformation.getRotation().getX(), transformation.getRotation().getY(), transformation.getRotation().getZ(), transformation.getRotation().getW());
        qq.inverse();*/
        
        //apply rotation, translation and scale (if allowed)
            for (Vector3f compF1 : compF) {
                p1 = compF1;
                
                
                Quaternion point = new Quaternion(p1.getX(), p1.getY(), p1.getZ(), 1);
                
                if(compF.size() > 1){
                    qCopy = MathUtils.instance().multiply(conjugateQ(transformation.getRotation()), point);
                    qCopy = MathUtils.instance().multiply(qCopy, transformation.getRotation());
                    //qCopy = MathUtils.instance().multiply(qq,point);
                }else{
                    qCopy = point;
                }
                
                if(scale && !Float.isNaN(transformation.getScaleFactor())){
                p1.setX(qCopy.getX() * transformation.getScaleFactor() + transformation.getTranslation().x);
                p1.setY(qCopy.getY() * transformation.getScaleFactor() + transformation.getTranslation().y);
                p1.setZ(qCopy.getZ() * transformation.getScaleFactor() + transformation.getTranslation().z);
                }else{
                p1.setX(qCopy.getX() + transformation.getTranslation().x);
                p1.setY(qCopy.getY() + transformation.getTranslation().y);
                p1.setZ(qCopy.getZ() + transformation.getTranslation().z);
                }
            }
    }
    
    /***
     * Reverse all transformations computed in given ICPTransformation class on verticies.
     * 
     * @param trans - computed transformations
     * @param verticies - vertices to be reverted
     * @param scale - whether scale was used 
     */
    public void reverseTransformations(ICPTransformation trans, List<Vector3f> verticies, boolean scale){
        if(scale){
            for(Vector3f v : verticies){
                v.setX((v.x - trans.getTranslation().x) / trans.getScaleFactor());
                v.setY((v.y - trans.getTranslation().y) / trans.getScaleFactor());
                v.setZ((v.z - trans.getTranslation().z) / trans.getScaleFactor());
            }
        }else{
           for(Vector3f v : verticies){
                v.setX(v.x - trans.getTranslation().x);
                v.setY(v.y - trans.getTranslation().y);
                v.setZ(v.z - trans.getTranslation().z);
            } 
        }
        
        //Quaternion reverse = conjugateQ(trans.getRotation());
        Quaternion reverse = new Quaternion(trans.getRotation().getX(), trans.getRotation().getY(), trans.getRotation().getZ(), trans.getRotation().getW());
        //reverse.inverse();
        Quaternion reverCon = conjugateQ(reverse);
        
        Quaternion oldV;
        
        for (Vector3f v : verticies) {
           oldV = new Quaternion(v.x, v.y, v.z, 1);
           /*oldV = MathUtils.instance().multiply(reverse, oldV);
           oldV = MathUtils.instance().multiply(oldV, trans.getRotation());*/
           oldV = MathUtils.instance().multiply(trans.getRotation(), oldV);
           oldV = MathUtils.instance().multiply(oldV, reverCon);
           //oldV = MathUtils.instance().multiply(trans.getRotation(), oldV);
           
           v.setX(oldV.getX());
           v.setY(oldV.getY());
           v.setZ(oldV.getZ());
        }
    }
    
    /***
     * Reverse whole list of transformations, with first transformation in list being the first one computed.
     * 
     * @param trans - list of computed transformations
     * @param verticies - verticies to be reverted
     * @param scale - whether scale was used
     */
    public void reverseAllTransformations(List<ICPTransformation> trans, List<Vector3f> verticies, boolean scale){
        for(int i = trans.size() - 1; i >= 0; i--){
            reverseTransformations(trans.get(i), verticies, scale);
        }
        
        /*ICPTransformation finalTrans = createFinalTrans(trans, scale);
        reverseTransformations(finalTrans, verticies, scale);*/
    }
    
    /**
     * Create final transformation combining all transformations in the list.
     * 
     * @param trans - transformations to combine
     * @param scale - whether scale was used
     * @return final transformation combining all listed transformations
     */
    public ICPTransformation createFinalTrans(List<ICPTransformation> trans, boolean scale){
        float s = 1f;
        Vector3f t = new Vector3f();
        Quaternion r = new Quaternion(0,0,0,1);
        
        for(int i = trans.size() - 1; i >= 0; i--/*ICPTransformation tran : trans*/){
            ICPTransformation tran = trans.get(i);
            
            if(scale)
                s *= tran.getScaleFactor();
            
            t.add(tran.getTranslation());            
            
            Quaternion q = tran.getRotation();
            r.mult(q);
        }
        
        return new ICPTransformation(t, s, r, 0.0f);
    }
}
