package cz.fidentis.featurepoints;

import cz.fidentis.model.corner_table.Corner;
import cz.fidentis.model.corner_table.CornerTable;
import jv.geom.PgElementSet;
import jv.object.PsDebug;
import jv.vecmath.PdVector;
import jvx.geom.PwSmooth;

/**
 *
 * @author Marek Galvanek
 *
 * Feature preserve smoothing
 */
public class BilateralDenoise {

    private CornerTable cornerTable;
    private PgElementSet elementSet;

    public BilateralDenoise(PgElementSet elementSet, CornerTable cornerTable) {
        this.elementSet = elementSet;
        this.cornerTable = cornerTable;
    }

    /*
     * One step of Fleishman Bilateral mesh denoising
     */
    public PgElementSet denoise(double sigmaC, double sigmaS) {

        elementSet.makeVertexNormals();
        cornerTable = new CornerTable(elementSet);

        double sum;
        double normalizer;
        double sumNorm;
        double t;
        double h;
        double wc;
        double ws;
        PdVector vert;
        PdVector norm;
        Corner[] neighbors;
        PdVector neighbor;

        PgElementSet newElementSet = new PgElementSet();
        newElementSet.copyElementSet(elementSet);

        for (Corner corner : cornerTable.corners()) {
            neighbors = corner.vertexNeighbors();
            vert = elementSet.getVertex(corner.vertex);
            norm = elementSet.getVertexNormal(corner.vertex);
            sum = 0;
            normalizer = 0;
            for (int i = 0; i < neighbors.length; i++) {
                neighbor = elementSet.getVertex(neighbors[i].vertex);
                t = VectorSize(Subtract(vert, neighbor));
                h = InnerProduct(norm, Subtract(neighbor, vert)); //poradie neighbor a vert
                wc = Math.exp(Math.pow(t, 2) / (2 * Math.pow(sigmaC, 2))); //znamienko -
                ws = Math.exp(Math.pow(h, 2) / (2 * Math.pow(sigmaS, 2))); //znamienko -
                sum += (wc * ws) * h;
                normalizer += wc * ws;
            }

            sumNorm = sum / normalizer;
            newElementSet.setVertex(corner.vertex, new PdVector(vert.getEntry(0) + (norm.getEntry(0) * sumNorm),
                    vert.getEntry(1) + (norm.getEntry(1) * sumNorm),
                    vert.getEntry(2) + (norm.getEntry(2) * sumNorm)));
        }
        return newElementSet;
    }

    /*
     * Fleishman Bilateral mesh denoising
     */
    public PgElementSet denoise(double sigmaC, double sigmaS, double iter) {

        double sum;
        double normalizer;
        double sumNorm;
        double t;
        double h;
        double wc;
        double ws;
        PdVector vert;
        PdVector norm;
        Corner[] neighbors;
        PdVector neighbor;

        PgElementSet newElementSet = new PgElementSet();
        newElementSet.copyElementSet(elementSet);

        for (int j = 0; j < iter; j++) {
            for (Corner corner : cornerTable.corners()) {
                neighbors = corner.vertexNeighbors();
                vert = elementSet.getVertex(corner.vertex);
                norm = elementSet.getVertexNormal(corner.vertex);
                sum = 0;
                normalizer = 0;
                for (int i = 0; i < neighbors.length; i++) {
                    neighbor = elementSet.getVertex(neighbors[i].vertex);
                    t = VectorSize(Subtract(vert, neighbor));
                    h = InnerProduct(norm, Subtract(neighbor, vert)); //poradie neighbor a vert
                    wc = Math.exp(Math.pow(t, 2) / (2 * Math.pow(sigmaC, 2))); //znamienko -
                    ws = Math.exp(Math.pow(h, 2) / (2 * Math.pow(sigmaS, 2))); //znamienko -
                    sum += (wc * ws) * h;
                    normalizer += wc * ws;
                }

                sumNorm = sum / normalizer;
                newElementSet.setVertex(corner.vertex, new PdVector(vert.getEntry(0) + (norm.getEntry(0) * sumNorm),
                        vert.getEntry(1) + (norm.getEntry(1) * sumNorm),
                        vert.getEntry(2) + (norm.getEntry(2) * sumNorm)));
            }

            elementSet.copyElementSet(newElementSet);
            elementSet.makeVertexNormals();
        }
        elementSet.makeElementNormals();

        return elementSet;
    }

    /*
     * Anisotropic and Ansio precribed smoothing
     */
    public PgElementSet anisotropicDenoise(int iter, int method, double featureDetect, boolean keepBoundary) {
        PwSmooth smooth = new PwSmooth();
        smooth.setGeometry(elementSet);
        smooth.setKeepBoundary(keepBoundary);
        smooth.setFeatureDetect(featureDetect);
        smooth.setMethod(method);
        
        for (int i = 0; i < iter; i++) {
            smooth.smoothingStep();
        }

        elementSet = (PgElementSet) smooth.getGeometry();

        return elementSet;
    }
    
    public PgElementSet anisotropicDenoise2(int numOfLoops, int method, double featureDetect, boolean keepBoundary) {
       
        //methods to avoid popup javaview windows
        PsDebug.setDebug(false);
        PsDebug.setError(false);
        PsDebug.setWarning(false);
        PsDebug.setMessage(false);
        
        PwSmooth smooth = new PwSmooth();
        smooth.setGeometry(elementSet);
        smooth.setKeepBoundary(keepBoundary);
        smooth.setFeatureDetect(featureDetect);
        smooth.setMaxNumLoops(numOfLoops);
        //smooth.setStepWidth(0.1);
        
        switch (method) {
            case 0:
                smooth.setMethod(PwSmooth.METHOD_ANISOTROPIC);
                break;
            case 1:
                smooth.setMethod(PwSmooth.METHOD_ANSIO_PRECRIBED);
                break;                
        }
        
        for (int i = 0; i < numOfLoops; i++) {
            smooth.smoothingStep();
        }
               
        PsDebug.getConsole().setVisible(false);

        //elementSet = (PgElementSet) smooth.getGeometry();

        return (PgElementSet) smooth.getGeometry();
    }
    

    
    public void setElementSet(PgElementSet elementSet) {
        this.elementSet = elementSet;
    }

    public PdVector Subtract(PdVector v1, PdVector v2) {
        return new PdVector(v1.getEntry(0) - v2.getEntry(0),
                v1.getEntry(1) - v2.getEntry(1),
                v1.getEntry(2) - v2.getEntry(2));
    }

    public double InnerProduct(PdVector v1, PdVector v2) {
        return (v1.getEntry(0) * v2.getEntry(0)
                + v1.getEntry(1) * v2.getEntry(1)
                + v1.getEntry(2) * v2.getEntry(2));
    }

    public double VectorSize(PdVector v) {
        return Math.sqrt(Math.pow(v.getEntry(0), 2)
                + Math.pow(v.getEntry(1), 2)
                + Math.pow(v.getEntry(2), 2));
    }
}
