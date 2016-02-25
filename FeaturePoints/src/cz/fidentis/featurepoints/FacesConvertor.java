package cz.fidentis.featurepoints;

import cz.fidentis.model.Faces;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Vector3f;
import jv.geom.PgElementSet;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;

/**
 *
 * @author Marek Galvanek
 *
 * This class converts imported dataset to element set from JavaView libraries
 */
public final class FacesConvertor {

    private PgElementSet elementSet;
    private Set<SimpleEdge> boundaryEdges;
    private Set<Integer> boundaryVertices;

    public FacesConvertor() {
        boundaryEdges = new HashSet<SimpleEdge>();
        boundaryVertices = new HashSet<Integer>();
    }

    public PgElementSet convert(Faces faces, ArrayList<Vector3f> verts) {

        elementSet = new PgElementSet();
        elementSet.setNumElements(faces.getNumFaces());
        System.out.println("converting model to element set");
        System.out.println("converting vertices");
        setVertices(verts);
        System.out.println("converting faces");
        setFaces(faces);
        System.out.println("converting done");

        return elementSet;
    }

    private void setVertices(ArrayList<Vector3f> verts) {
        for (Vector3f p : verts) {
            elementSet.addVertex(new PdVector(p.getX(), p.getY(), p.getZ()));
        }
    }

    private void setFaces(Faces faces) {
        int[] faceIndexes;

        for (int i = 0; i < faces.getNumFaces(); i++) {
            faceIndexes = faces.getFaceVertIdxs(i);//?? indexy?
            elementSet.setElement(i, new PiVector(faceIndexes[0] - 1, faceIndexes[1] - 1, faceIndexes[2] - 1));

            setBoundaryEdges(elementSet.getElement(i));
        }
        setBoundaryVertices();

    }
    int iter2 = 1;

    public void setBoundaryEdges(PiVector elem) {
        int k;
        SimpleEdge edge;
        for (int j = 0; j < 3; j++) {
            if (j < 2) {
                k = j + 1;
            } else {
                k = 0;
            }

            edge = new SimpleEdge(elem.getEntry(j), elem.getEntry(k));

            if (!boundaryEdges.add(edge)) {
                boundaryEdges.remove(edge);

            }
        }
    }

    public Set<SimpleEdge> getBoundaryEdges() {
        return boundaryEdges;
    }

    public Set<Integer> getBoundaryVertices() {
        return boundaryVertices;
    }

    public void setBoundaryVertices() {

        for (SimpleEdge edge : boundaryEdges) {
            boundaryVertices.add(edge.getFirstPoint());
            boundaryVertices.add(edge.getSecondPoint());

        }

    }

    public void printVertsAndFaces() {
        for (int i = 0; i < elementSet.getNumVertices(); i++) {
            System.out.println("vert no. " + i + ": " + elementSet.getVertex(i).getEntries()[0] + ", "
                    + elementSet.getVertex(i).getEntries()[1] + ", "
                    + elementSet.getVertex(i).getEntries()[2]);
        }

        for (int j = 0; j < elementSet.getNumElements(); j++) {
            System.out.println("element no." + j + ": " + elementSet.getElement(j).getEntries()[0] + ", "
                    + elementSet.getElement(j).getEntries()[1] + ", "
                    + elementSet.getElement(j).getEntries()[2]);
        }
    }

    public PgElementSet getElementSet() {
        //printVertsAndFaces();
        return elementSet;
    }
}
