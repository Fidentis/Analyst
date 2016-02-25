package cz.fidentis.merging.doubly_conected_edge_list.parts;

import cz.fidentis.merging.doubly_conected_edge_list.Boundary;
import cz.fidentis.merging.doubly_conected_edge_list.SourceTargetMapping;
import cz.fidentis.merging.mesh.Coordinates;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.MeshFace;
import cz.fidentis.merging.mesh.MeshFaceTwinSplit;
import cz.fidentis.merging.mesh.MeshPoint;
import cz.fidentis.merging.mesh.Vector3;
import java.util.Collection;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 * @param <F>
 */
public abstract class AbstractDcel<F extends AbstractFace> {

    private final Vertices vertices = new Vertices(this);
    private final HalfEdges halfEdges = new HalfEdges(this);
    private final Faces<F> faces = createFaces();
    private final GraphicMesh underlayingMesh;

    /**
     *
     * @param mesh
     */
    public AbstractDcel(GraphicMesh mesh) {

        underlayingMesh = mesh;

        for (MeshPoint point : underlayingMesh.getPoints()) {
            vertices.addVertex(point.getIndex());
        }
        createFaces(underlayingMesh.getMeshFaces());
        faces.updateOuterFace(halfEdges);
        recalculateNormals();
        refresh();
    }

    /**
     *
     * @param source
     * @param mapping
     */
    public void append(AbstractDcel<F> source, SourceTargetMapping mapping) {

        for (Vertex v : source.vertices) {
            Vertex targetVertex = mapping.getTarget(v);
            if (targetVertex != null) {
                continue;
            }
            Vertex newVertex = createVertex(v.getMeshPoint());
            mapping.addMapping(v, newVertex);
        }

        for (F sourceFace : source.faces.withoutOuterFace()) {
            MeshFace sourceMeshFace = sourceFace.getMeshFace();
            MeshFace meshFace = underlayingMesh.createMeshFace(sourceMeshFace.getMaterial());
            for (Integer sourceIndex : sourceMeshFace) {
                Vertex target;
                target = mapping.getTarget(source.getVertex(sourceIndex));
                Integer targetIndex = target.getIndex();
                meshFace.addMeshpointIndex(targetIndex);
                MeshPoint targetMeshPoint;
                targetMeshPoint = underlayingMesh.getMeshPoint(targetIndex);
                MeshPoint sourceMeshPoint;
                sourceMeshPoint = source.underlayingMesh.getMeshPoint(sourceIndex);
                Coordinates textures;
                textures = sourceMeshPoint.getTextureCoordinates(sourceMeshFace);
                targetMeshPoint.addTexture(textures, meshFace);
            }
            faces.constructFace(meshFace);
        }
        faces.updateOuterFace(halfEdges);
        refresh();
    }

    /**
     *
     * @param index
     * @return
     */
    public final HalfEdge getHalfEdge(final HalfEdgeId index) {
        return halfEdges.get(index);
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public final HalfEdge getHalfEdge(final Vertex from, final Vertex to) {
        return halfEdges.get(new HalfEdgeId(from, to));
    }

    /**
     *
     * @param index
     * @param face
     * @return
     */
    protected final boolean addNewEdge(final HalfEdgeId index, final AbstractFace face) {
        if (halfEdges.contains(index)) {
            return false;
        }
        addNewEdge(index, face, faces.getOuterFace());
        return true;
    }

    /**
     *
     * @param halfEdgeId
     * @param face
     * @param twinFace
     * @return
     */
    protected final HalfEdge addNewEdge(HalfEdgeId halfEdgeId, AbstractFace face, AbstractFace twinFace) {
        assert face != null;
        assert twinFace != null;

        assert vertices.canSupport(halfEdgeId);

        if (halfEdges.contains(halfEdgeId)) {
            return halfEdges.get(halfEdgeId);
        }

        final HalfEdge halfEdge = new HalfEdge(halfEdgeId, face, twinFace);
        halfEdges.addEdgeFor(halfEdge);
        return halfEdge;
    }

    /**
     *
     * @return
     */
    public HalfEdge getOuterEdge() {
        return halfEdges.get(faces.getOuterFace().getIncidentHalfEdgeId());
    }

    /**
     *
     * @return
     */
    public AbstractFace getOuterFace() {
        return faces.getOuterFace();
    }

    /**
     *
     * @param face
     * @return
     */
    public boolean isOuter(F face) {
        return faces.isOuter(face);
    }

    /**
     *
     * @return
     */
    public final int numberOfVertecies() {
        return vertices.count();
    }

    Vertex getVertex(int fromIndex) {
        return vertices.get(fromIndex);
    }

    /**
     *
     * @param index
     * @return
     */
    public Coordinates getVertexPosition(int index) {
        return underlayingMesh.getPosition(index);
    }

    /**
     *
     * @param index
     * @return
     */
    public Vector3 getVertexNormal(int index) {
        return underlayingMesh.getNormal(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Faces");
        sb.append(System.lineSeparator());
        faces.appendsTo(sb);
        sb.append("Vertices");
        sb.append(System.lineSeparator());
        vertices.appendsTo(sb);
        sb.append("Halfedges");
        sb.append(System.lineSeparator());
        halfEdges.appentTo(sb);
        return sb.toString();
    }

    public GraphicMesh getGraphicMesh() {
        recalculateNormals();
        return underlayingMesh;
    }

    F addFace(HalfEdgeId forHalfEdgeId, MeshFace meshFace) {
        return faces.createFace(forHalfEdgeId, meshFace);
    }

    public void remove(F face) {
        underlayingMesh.remove(face.getMeshFace());
        faces.remove(face);
    }

    void remove(Vertex vertex) {
        vertices.remove(vertex.getIndex());
    }

    void remove(HalfEdge halfEdge) {
        halfEdges.removeEdgeOf(halfEdge.getId());
    }

    /**
     *
     * @param meshPoint
     * @return
     */
    Vertex createVertex(Coordinates coordinates, Vector3 normal) {
        Integer index = underlayingMesh.createMeshPoint(coordinates, normal);
        return vertices.addVertex(index);
    }

    private Vertex createVertex(MeshPoint meshPoint) {
        return createVertex(meshPoint.getPosition(), meshPoint.getNormal());
    }

    /**
     *
     * @param edgeId
     * @return
     */
    public boolean haveEdge(HalfEdgeId edgeId) {
        return halfEdges.contains(edgeId);
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public boolean haveEdge(Vertex from, Vertex to) {
        return halfEdges.contains(new HalfEdgeId(from, to));
    }

    /**
     *
     * @return
     */
    public Collection<F> getFaces() {
        return faces.withoutOuterFace();
    }

    Iterable<Vertex> getVerecies() {
        return vertices;
    }

    MeshPoint getMeshPoint(int index) {
        return underlayingMesh.getMeshPoint(index);
    }

    public void allignAccordingMapping(SourceTargetMapping mapping) {
        Boundary sourceBoundary = mapping.getSourceBoundary();
        Boundary diff = mapping.getDiffBoundary();
        for (Vertex vertex : vertices) {
            if (mapping.isMapped(vertex)) {
                continue;
            }
            sourceBoundary.moveForDiference(diff, vertex);
        }

    }

    /**
     *
     */
    public final void refresh() {
        underlayingMesh.reBuffer();
    }

    /**
     *
     * @return
     */
    public Coordinates getWeight() {
        return underlayingMesh.getWeight();
    }

    void moveVertex(int index, Coordinates movement) {
        underlayingMesh.moveVertex(index, movement);
    }

    void setVertexColorDefault(int index) {
        underlayingMesh.updateVertexColor(index);
    }

    void addTextureCoordinates(int index, Coordinates texture, MeshFace face) {
        underlayingMesh.addTextures(index, texture, face);
    }

    protected abstract Faces<F> createFaces();

    void replaceBySplit(AbstractFace toSplit, Vertex splitingVertex) {
        Iterable<MeshFace> split;
        split = underlayingMesh.split(toSplit.getMeshFace(), splitingVertex.getIndex());
        toSplit.removeFromDcel();
        createFaces(split);
    }

    private void createFaces(Iterable<MeshFace> split) {
        for (MeshFace newFace : split) {
            faces.constructFace(newFace);
        }
    }

    void replaceBySplit(MeshFaceTwinSplit twinSplit) {
        Iterable<MeshFace> split = underlayingMesh.split(twinSplit);
        for (AbstractFace toRemove : twinSplit) {
            toRemove.removeFromDcel();
        }
        HalfEdge toRemove = halfEdges.get(twinSplit.getSplitingHalfEdge());
        toRemove.removeFromDcel();
        createFaces(split);
    }

    public Iterable<Vertex> getVertecies() {
        return vertices.getVertecies();
    }

    private void recalculateNormals() {
        for (Vertex vertice : vertices) {
            vertice.recalcNormal();
        }
    }

    void SetVertexNormal(Integer index, Vector3 newNormal) {
        getMeshPoint(index).setNormal(newNormal);
    }

}
