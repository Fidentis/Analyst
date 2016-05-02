package cz.fidentis.comparison.procrustes;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.jogamp.graph.math.Quaternion;
import cz.fidentis.comparison.icp.ICPTransformation;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.utils.MathUtils;
import cz.fidentis.utils.MeshUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector3f;

/**
 * This class remembers 1 configuration of Feature points and implements
 * Procrustes Analysis
 *
 * @author Zuzana LÝOVÁ
 * @version 2014
 */
public class ProcrustesAnalysis implements Serializable {

    //private Matrix config;
    private Map<FacialPointType, FacialPoint> config;
    private Matrix visMatrix;
    private Matrix vertices;        //to be able to register models with points -- change to list?

    private static final float SIZE_SCALE = 200f;
    
    public ProcrustesAnalysis() {
        //config = new Matrix(8, 3);
        config = new HashMap<>();
    }

  

    public ProcrustesAnalysis(List<FacialPoint> fps) {
        //config = new Matrix(fps.size(), 3);
        this();
        FacialPointType type;
        FacialPoint fp;
        int j = 8;

        for (int i = 0; i < fps.size(); i++) {
            type = fps.get(i).getType();
            fp = fps.get(i);
            
            if(type == FacialPointType.unspecified){
                //don't include unspecified points into analysis
                continue;
            }
            
            config.put(type, fp);

            /*switch (type) {
                case EX_R:
                    config.set(0, 0, fp.getPosition().x);
                    config.set(0, 1, fp.getPosition().y);
                    config.set(0, 2, fp.getPosition().z);
                    break;

                case EX_L:
                    config.set(1, 0, fp.getPosition().x);
                    config.set(1, 1, fp.getPosition().y);
                    config.set(1, 2, fp.getPosition().z);
                    break;
                case EN_R:
                    config.set(2, 0, fp.getPosition().x);
                    config.set(2, 1, fp.getPosition().y);
                    config.set(2, 2, fp.getPosition().z);
                    break;
                case EN_L:
                    config.set(3, 0, fp.getPosition().x);
                    config.set(3, 1, fp.getPosition().y);
                    config.set(3, 2, fp.getPosition().z);
                    break;
                case PRN:
                    config.set(4, 0, fp.getPosition().x);
                    config.set(4, 1, fp.getPosition().y);
                    config.set(4, 2, fp.getPosition().z);
                    break;
                case STO:
                    config.set(5, 0, fp.getPosition().x);
                    config.set(5, 1, fp.getPosition().y);
                    config.set(5, 2, fp.getPosition().z);
                    break;
                case CH_R:
                    config.set(6, 0, fp.getPosition().x);
                    config.set(6, 1, fp.getPosition().y);
                    config.set(6, 2, fp.getPosition().z);
                    break;
                case CH_L:
                    config.set(7, 0, fp.getPosition().x);
                    config.set(7, 1, fp.getPosition().y);
                    config.set(7, 2, fp.getPosition().z);
                    break;
                default: 
                    config.set(j, 0, fp.getPosition().x);
                    config.set(j, 1, fp.getPosition().y);
                    config.set(j, 2, fp.getPosition().z);
                    j++;
                    break;    
                    
            }*/

        }
        
        for(int i = fps.size() - 1; i >= 0 ; i--){
            if(fps.get(i).getType() == FacialPointType.unspecified){
                fps.remove(i);
            }
        }
        
        setVisMatrix();
    }
    
      public ProcrustesAnalysis(List<FacialPoint> fps, List<Vector3f> verts) {
          this(fps);
            if (verts != null) {
                vertices = new Matrix(verts.size(), 3);
                for (int i = 0; i < verts.size(); i++) {
                    vertices.set(i, 0, verts.get(i).x);
                    vertices.set(i, 1, verts.get(i).y);
                    vertices.set(i, 2, verts.get(i).z);
                }
            }

        
          
    }

    /*public ProcrustesAnalysis(Matrix matrix) {
        config = new Matrix(matrix.getRowDimension(), matrix.getColumnDimension());

        for (int i = 0; i < matrix.getRowDimension(); i++) {
            config.set(i, 0, matrix.get(i, 0));
            config.set(i, 1, matrix.get(i, 1));
            config.set(i, 2, matrix.get(i, 2));
        }
    }*/
     
    public ProcrustesAnalysis(Map<FacialPointType, FacialPoint> config){
          this.config = config;
          setVisMatrix();
      }

    
    private ProcrustesAnalysis(Map<FacialPointType, FacialPoint> fps, Matrix verts){
        this.config = fps;
        this.vertices = verts;
    }

    public /*Matrix*/ Map<FacialPointType, FacialPoint> getConfig() {
        return config;
    }
    
    public List<FacialPoint> getFacialPoints(){
        List<FacialPoint> fp = new LinkedList<>();
        
        fp.addAll(config.values());

        return fp;
    }

    
    public boolean containsPoint(FacialPointType ft){
        return config.containsKey(ft);
    }
    
    public Vector3f getFPposition(FacialPointType ft){
        FacialPoint fp = config.get(ft);
        
        if(fp == null){
            return null;
        }
        
        return config.get(ft).getPosition();
    }
    
    private List<FacialPointType> getFPtypeCorrespondence(ProcrustesAnalysis pa){
        List<FacialPointType> correspondence = new ArrayList<>();
        
        for(FacialPointType ft : config.keySet()){
            if(pa.containsPoint(ft)){
                correspondence.add(ft);
            }
        }
        
        return correspondence;
    }
    
    public Matrix createCorespondingMatrix(List<FacialPointType> cor){        
        Matrix mat = new Matrix(cor.size(), 3);
        
        for(int i = 0; i < cor.size(); i++){
            Vector3f pos = config.get(cor.get(i)).getPosition();
            mat.set(i, 0, pos.x);
            mat.set(i, 1, pos.y);
            mat.set(i, 2, pos.z);
        }
        
        return mat;
    }

    public void setConfig(/*Matrix*/Map<FacialPointType, FacialPoint>  config) {
        this.config = config;
    }


    public ArrayList<Vector3f> getVertices() {
        ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
        for (int i = 0; i < vertices.getRowDimension(); i++) {
            verts.add(new Vector3f((float) vertices.get(i, 0), (float) vertices.get(i, 1), (float) vertices.get(i, 2)));        
        }
        return verts;
    }

    public void updateFacialPoints(List<FacialPoint> fp) {
        int j = 8;
        
        
        for (FacialPoint fp1 : fp) {
            if(fp1.getType() == FacialPointType.unspecified){
                continue;
            }
            
            config.put(fp1.getType(), fp1);
            /*switch (fp1.getType()) {
                case EX_R:
                    setFPCoords(0, fp1);
                    break;
                case EX_L:
                    setFPCoords(1, fp1);
                    break;
                case EN_R:
                    setFPCoords(2, fp1);
                    break;
                case EN_L:
                    setFPCoords(3, fp1);
                    break;
                case PRN:
                    setFPCoords(4, fp1);
                    break;
                case STO:
                    setFPCoords(5, fp1);
                    break;
                case CH_R:
                    setFPCoords(6, fp1);
                    break;
                case CH_L:
                    setFPCoords(7, fp1);
                    break;
                default:
                    setFPCoords(j, fp1);
                    j++;
                    break;
            }*/
        }
        
        setVisMatrix();
    }
    
    public Matrix getVisualMatrix(){   
        return this.visMatrix;  
    }
    
    /**
     * Creates matrix to be used for visualisation.
     * Points: 
     * 0 - EX_R
     * 1 - EX_L
     * 2 - EN_R
     * 3 - EN_L
     * 4 - PRN
     * 5 - STO
     * 6 - CH_R
     * 7 - CH_L
     * 8 - N
     * 9 - G
     * 10 - LS
     * 11 - LI
     * 12 - SL
     * 13 - PG
     */
    public void setVisMatrix(){
        Matrix visMat = new Matrix(14,3);
        
        setVisMatPoint(visMat, 0, config.get(FacialPointType.EX_R));
        setVisMatPoint(visMat, 1, config.get(FacialPointType.EX_L));
        setVisMatPoint(visMat, 2, config.get(FacialPointType.EN_R));
        setVisMatPoint(visMat, 3, config.get(FacialPointType.EN_L));
        setVisMatPoint(visMat, 4, config.get(FacialPointType.PRN));
        setVisMatPoint(visMat, 5, config.get(FacialPointType.STO));
        setVisMatPoint(visMat, 6, config.get(FacialPointType.CH_R));
        setVisMatPoint(visMat, 7, config.get(FacialPointType.CH_L));
        setVisMatPoint(visMat, 8, config.get(FacialPointType.N));
        setVisMatPoint(visMat, 9, config.get(FacialPointType.G));
        setVisMatPoint(visMat, 10, config.get(FacialPointType.LS));
        setVisMatPoint(visMat, 11, config.get(FacialPointType.LI));
        setVisMatPoint(visMat, 12, config.get(FacialPointType.SL));
        setVisMatPoint(visMat, 13, config.get(FacialPointType.PG));

        
        this.visMatrix = visMat;
    }
    
    private void setVisMatPoint(Matrix visMat, int line, FacialPoint point){
        if(point == null){
            //error
            return;
        }
        
        Vector3f pos = point.getPosition();
        
        visMat.set(line, 0, pos.x);
        visMat.set(line, 1, pos.y);
        visMat.set(line, 2, pos.z);
    }

    
    private void setFPCoords(/*int index,*/ FacialPoint fp){
        //fp.setCoords(new Vector3f((float) config.get(index, 0), (float) config.get(index, 1), (float) config.get(index, 2)));
        Vector3f pos = config.get(fp.getType()).getPosition();
        
        if(pos == null){
            return;
        }
        
        fp.setCoords(pos);
    }

    @Override
    public String toString() {
        /*String pa = "";

        for (int i = 0; i < config.getRowDimension(); i++) {
            pa = pa.concat(Double.toString(config.get(i, 0)) + " ");
            pa = pa.concat(Double.toString(config.get(i, 1)) + " ");
            pa = pa.concat(Double.toString(config.get(i, 2)) + " ");
        }

        return pa;*/
        
        StringBuilder sb = new StringBuilder("");
        
        for(FacialPointType ft : config.keySet()){
            Vector3f pos = config.get(ft).getPosition();
            sb.append(ft).append(pos).append(" ");
        }
        
        return sb.toString();
    }


    /**
     * This mehtod finds center of this configuration's centroid
     *
     * @return coordinates of the center of this configuration's centroid
     */
    private Vector3f findCentroid() {
        /*Vector3f cs = new Vector3f();
        float x = 0f;
        float y = 0f;
        float z = 0f;
        int count = 0;

        for (int i = 0; i < config.getRowDimension(); i++) {
            x += (float) config.get(i, 0);
            y += (float) config.get(i, 1);
            z += (float) config.get(i, 2);
            count += 1;
        }

        cs.setX(x / count);
        cs.setY(y / count);
        cs.setZ(z / count);

        return cs;*/
        
        List<Vector3f> points = getFpPositions();
        
        float[] centroid = MeshUtils.instance().computeCentroid(points);
        return new Vector3f(centroid[0], centroid[1], centroid[2]);
    }

    private List<Vector3f> getFpPositions(){
        List<Vector3f> points = new LinkedList<>();
        for(FacialPoint fp : config.values()){
            points.add(fp.getPosition());
        }
        
        return points;
    }
    
    /**
     * This method translate this configuration's center to the origin of a
     * coordinate system
     *
     * @param cs coordinates of the center of this configuration's centroid
     */
    private void centerConfigToOrigin(Vector3f cs) {
        /*Matrix centeredMat = new Matrix(config.getRowDimension(), config.getColumnDimension());

        for (int i = 0; i < config.getRowDimension(); i++) {

            centeredMat.set(i, 0, (config.get(i, 0) - cs.getX()));
            centeredMat.set(i, 1, (config.get(i, 1) - cs.getY()));
            centeredMat.set(i, 2, (config.get(i, 2) - cs.getZ()));

        }*/
        
        List<Vector3f> points = getFpPositions();
        
        for(Vector3f p : points){
            p.x -= cs.x;
            p.y -= cs.y;
            p.z -= cs.z;
        }
        
        if (vertices != null) {
            Matrix centeredVerts = new Matrix(vertices.getRowDimension(), vertices.getColumnDimension());
            for (int i = 0; i < vertices.getRowDimension(); i++) {

                centeredVerts.set(i, 0, (vertices.get(i, 0) - cs.getX()));
                centeredVerts.set(i, 1, (vertices.get(i, 1) - cs.getY()));
                centeredVerts.set(i, 2, (vertices.get(i, 2) - cs.getZ()));

            }
            vertices = centeredVerts;
        }
        
        //config = centeredMat;
    }


    /**
     * this method counts size of a centroid
     *
     * @param cs coordinates of the center of this configuration's centroid
     * @return size of the centroid
     */
    private float countSize(Vector3f cs) {
        float size = 0;
        float x = cs.getX();
        float y = cs.getY();
        float z = cs.getZ();

        /*for (int i = 0; i < config.getRowDimension(); i++) {
            size += (float) ((config.get(i, 0) - x) * (config.get(i, 0) - x));
            size += (float) ((config.get(i, 1) - y) * (config.get(i, 1) - y));
            size += (float) ((config.get(i, 2) - z) * (config.get(i, 2) - z));
        }*/
        
        List<Vector3f> points = getFpPositions();
        
        for(Vector3f p : points){
            size += (p.x - x) * (p.x - x);
            size += (p.y - y) * (p.y - y);
            size += (p.z - z) * (p.z - z);
        }

        size = (float) Math.sqrt(size);

        return size;
    }


    /**
     * This method changes size of this configuration according to given
     * centroid's size
     *
     * @param size size of the centroid
     */
    private void setSizeTo1(float size) {

        /*for (int i = 0; i < config.getRowDimension(); i++) {
            config.set(i, 0, config.get(i, 0) / size);
            config.set(i, 1, config.get(i, 1) / size);
            config.set(i, 2, config.get(i, 2) / size);

        }*/
        
        List<Vector3f> points = getFpPositions();
        
        for(Vector3f p : points){
            p.x /= size;
            p.y /= size;
            p.z /= size;
        }

        if (vertices != null) {
            for (int i = 0; i < vertices.getRowDimension(); i++) {
                vertices.set(i, 0, vertices.get(i, 0) / size);
                vertices.set(i, 1, vertices.get(i, 1) / size);
                vertices.set(i, 2, vertices.get(i, 2) / size);

            }
        }

    }


    /**
     * This method moves this configuration to the center and normalize its size
     *
     * To be able to visually analyse the results within the software, results are scaled to 100 instead of 1
     * @param scaling says if algorithm should set size to 1 or keep it
     */
    public void normalize(boolean scaling) {
        if (scaling) {
            Vector3f cs = this.findCentroid();
            float size = this.countSize(cs);

            //this.centerConfigToOrigin(cs);      //not required?

        
            this.setSizeTo1(size / SIZE_SCALE);
            //change scale to size / 100 to get proper size of model
        }
    }

   /**
     * This method approximates this configuratin to another configuration It
     * solves Orthogonal Procrustes problem with use of Singular Value
     * Decomposition described here:
     * http://en.wikipedia.org/wiki/Orthogonal_Procrustes_problem
     *
     * @param pa2 another configuration
     */
    public Quaternion rotate(ProcrustesAnalysis pa2) {
        Matrix transConf2;
        Matrix origConf1;
        Matrix svdMat;
        Matrix u;
        Matrix v;
        Matrix r;
        Matrix transU;
        List<FacialPointType> cor = getFPtypeCorrespondence(pa2);
        
        if(cor.isEmpty()){
            return null;
        }

        //transConf2 = pa2.getConfig().transpose();
        transConf2 = pa2.createCorespondingMatrix(cor).transpose();
        origConf1 = this.createCorespondingMatrix(cor);
        //svdMat = transConf2.times(config);
        svdMat = transConf2.times(origConf1);

        SingularValueDecomposition svd = new SingularValueDecomposition(svdMat);
        u = svd.getU();
        v = svd.getV();
        transU = u.transpose();
        r = v.times(transU);
        //config = config.times(r);
        origConf1 = origConf1.times(r);
        
        
        for(int i = 0; i < cor.size(); i++){
            FacialPoint newPoint = new FacialPoint(cor.get(i), new Vector3f((float) origConf1.get(i, 0), (float) origConf1.get(i, 1), (float) origConf1.get(i, 2)));
            config.put(cor.get(i), newPoint);
        }

        if (vertices != null) {
            vertices = vertices.times(r);
        }
        
        return MathUtils.instance().matrixToQuaternion(r);
    }

    /**
     * This method superimpose this and another configuration
     *
     * @param pa2 another configuration
     * @param scaling says if algorithm should set size to 1 or keep it
     */
    private ICPTransformation superimpose(ProcrustesAnalysis pa2, boolean scaling) {
       this.normalize(scaling);
       pa2.normalize(scaling);

       Quaternion q = pa2.rotate(this);
       ICPTransformation trans = new ICPTransformation(new Vector3f(), 1.0f, q, 0.0f);      //procrustes only uses rotation matrix, scaling can be infered
       
       return trans;
    }



    

    /**
     * This method counts Procrustes distance between this and another
     * configuration (without superimposing)
     *
     * @param config2 another configuration
     * @return Procrustes distance
     */
    public double countDistance(ProcrustesAnalysis config2, boolean scaling) {
        double distance = 0;

        /*for (int i = 0; i < config.getRowDimension(); i++) {
            distance = distance + (config.get(i, 0) - config2.getConfig().get(i, 0))
                    * (config.get(i, 0) - config2.getConfig().get(i, 0));
            distance = distance + (config.get(i, 1) - config2.getConfig().get(i, 1))
                    * (config.get(i, 1) - config2.getConfig().get(i, 1));
            distance = distance + (config.get(i, 2) - config2.getConfig().get(i, 2))
                    * (config.get(i, 2) - config2.getConfig().get(i, 2));
        }*/
        
        for(FacialPointType ft: config.keySet()){
            Vector3f conf2Pos = config2.getFPposition(ft);
            Vector3f conf1Pos = config.get(ft).getPosition();
            
            if(conf2Pos == null){
                continue;
            }
            
            /*distance += (conf1Pos.x - conf2Pos.x) * (conf1Pos.x - conf2Pos.x);
            distance += (conf1Pos.y - conf2Pos.y) * (conf1Pos.y - conf2Pos.y);
            distance += (conf1Pos.z - conf2Pos.z) * (conf1Pos.z - conf2Pos.z);*/
            
            distance += countAxisDistance(conf1Pos.x, conf2Pos.x, scaling);
            distance += countAxisDistance(conf1Pos.y, conf2Pos.y, scaling);
            distance += countAxisDistance(conf1Pos.z, conf2Pos.z, scaling);
        }

        /*if(scaling){
            distance /= (SIZE_SCALE * SIZE_SCALE);
        }*/
        
        return Math.sqrt(distance);
    }
    
    private float countAxisDistance(float p1, float p2, boolean scaling){
        float distance = (p1 - p2) * (p1 - p2);
        
        if(scaling){
            distance /= (SIZE_SCALE * SIZE_SCALE);
        }
        
        return distance;
    }


    /**
     * This method do algorithm Procrustes analysis for this and another
     * configuration (with superimposing both configuration) and it counts
     * Procrustes distance which is then used for comparision of 2 models
     *
     * this method exists just for testing
     *
     * @param config2 another configuration
     * @param scaling says if algorithm should set size to 1 or keep it
     * @return distance Procrustes distance
     */
    public List<ICPTransformation> doProcrustesAnalysis(ProcrustesAnalysis config2, boolean scaling) {
        float distance;
        List<ICPTransformation> t = new LinkedList<>();

        ICPTransformation trans = this.superimpose(config2, scaling);
        setVisMatrix();
        config2.setVisMatrix();
        //distance = this.countDistance(config2);

        //return distance;
        t.add(trans);
        
        return t;
    }


    public ProcrustesAnalysis copy(){
        ProcrustesAnalysis copy;
        Matrix copyVerts = null;
        Map<FacialPointType, FacialPoint> copyFps = new HashMap<>();
        
        for(FacialPointType fpt: config.keySet()){
            Vector3f pos = config.get(fpt).getPosition();
            FacialPoint newFp = new FacialPoint(fpt, new Vector3f(pos.x, pos.y, pos.z));
            
            copyFps.put(fpt, newFp);
        }
         
        if(vertices != null){
            copyVerts = new Matrix(vertices.getRowDimension(), 3);
            
            for(int i = 0; i < vertices.getRowDimension(); i++){
            copyVerts.set(i, 0, vertices.get(i, 0));
            copyVerts.set(i, 1, vertices.get(i, 1));
            copyVerts.set(i, 2, vertices.get(i, 2));
            }
        }
        
        copy = new ProcrustesAnalysis(copyFps, copyVerts);
        return copy;
    } 
    
}
