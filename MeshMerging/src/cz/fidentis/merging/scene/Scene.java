package cz.fidentis.merging.scene;

import cz.fidentis.merging.doubly_conected_edge_list.parts.AbstractDcel;
import cz.fidentis.merging.doubly_conected_edge_list.parts.DcelMerger;
import cz.fidentis.merging.doubly_conected_edge_list.parts.TriangularDCEL;
import cz.fidentis.merging.file_parsing.ObjFileParsingException;
import cz.fidentis.merging.file_parsing.ObjLoader;
import cz.fidentis.merging.mesh.GraphicMesh;
import cz.fidentis.merging.mesh.LineLoop;
import cz.fidentis.merging.storing.ObjWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;

/**
 *
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class Scene {

    private final HashMap<String, PartOfHead> parts = new HashMap<>();
    private final ObjLoader loader = new ObjLoader();
    private String currentPart;

    /**
     *
     * @return
     */
    public Camera getCamera() {
        return camera;
    }

    private final Camera camera;

    /**
     *
     * @param camera
     */
    public Scene(Camera camera) {
        this.camera = camera;
    }

    /**
     *
     * @param partName
     */
    public void setCurrent(String partName) {
        currentPart = partName;
    }

    /**
     *
     * @param name
     * @return
     */
    public MeshDisplacment getDisplacment(String name) {
        return parts.get(name).getDisplacment();
    }

    /**
     *
     * @param partName
     * @param fileName
     */
    public void loadPart(String partName, String fileName) {
        try {
            GraphicMesh currentMesh;
            currentMesh = loader.loadMeshFromObjFile(fileName);
            TriangularDCEL dcel = TriangularDCEL.fromMesh(currentMesh);
            addPart(partName, dcel);
        } catch (FileNotFoundException | ObjFileParsingException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addPart(String partName, TriangularDCEL dcel) {
        PartOfHead withDisplacment;
        withDisplacment = new PartOfHead(dcel, new MeshDisplacment());
        parts.put(partName, withDisplacment);
    }

    /**
     *
     * @param openGL
     */
    public void drawParts(GL2 openGL) {
        camera.useCamera(openGL);
        for (Map.Entry<String, PartOfHead> entry : parts.entrySet()) {
            PartOfHead part = entry.getValue();
            if (entry.getKey().equals(HEAD)) {
                part.getDisplacment().displace(openGL);
                part.getMesh().draw(openGL);
                part.getDisplacment().putBack(openGL);
            } else {
                LineLoop loop = part.getLoop();
                if (loop != null) {
                    loop.draw(openGL);
                }
                part.getDisplacment().displace(openGL);
                part.getMesh().draw(openGL);
                part.getDisplacment().putBack(openGL);
            }
        }
    }

    /**
     *
     */
    public void project() {

        try {
            PartOfHead part = parts.get(currentPart);
            DcelMerger merger = new DcelMerger(getHead());
            merger.merge(part);
            parts.remove(currentPart);
        } catch (Exception e) {
            Log.log(e);
        }

    }

    public void merge(String fromFile) {

        try {
            String fileName = getDirectoryPath() + fromFile;
            Deque<GraphicMesh> loadedMeshes = loadMeshes(fileName);
            if (loadedMeshes.size() < 2) {
                Log.log("Nothing to merge");
                return;
            }
            TriangularDCEL head = merge(loadedMeshes);
            storeMerged(fromFile, head);
        } catch (Exception e) {
            Log.log(e);
        }

    }

    private void storeMerged(String fromFile, TriangularDCEL head) throws IOException {
        String mergedFile = fromFile.substring(0, fromFile.length() - 4);
        mergedFile = mergedFile + "Merged";
        ObjWriter writer = new ObjWriter(mergedFile);
        writer.write(head.getGraphicMesh());
    }

    public TriangularDCEL merge(Deque<GraphicMesh> loadedMeshes) {
        TriangularDCEL head = TriangularDCEL.fromMesh(loadedMeshes.removeFirst());
        addPart(HEAD, head);
        DcelMerger merger = new DcelMerger(head);
        for (GraphicMesh currentMesh : loadedMeshes) {
            TriangularDCEL source = TriangularDCEL.fromMesh(currentMesh);
            addPart("source", source);
            PartOfHead get = parts.get("source");
            get.setLoop(new LineLoop());
            merger.merge(get);
            parts.remove("Source");
        }
        return head;
    }

    /**
     *
     */
    protected static final String HEAD = "head";

    /**
     *
     * @return
     */
    public AbstractDcel getHead() {
        return parts.get(HEAD).getDCEL();
    }

    private static Deque<GraphicMesh> loadMeshes(String fileName) {
        ObjLoader loader = new ObjLoader();
        try {
            return loader.loadMeshesFromObjFile(fileName);
        } catch (FileNotFoundException | ObjFileParsingException ex) {
            System.out.println(ex);
        }
        return new LinkedList<>();
    }

    protected static String getDirectoryPath() throws IOException {
        String thisDirectory;
        thisDirectory = new File(".").getCanonicalPath();
        thisDirectory += "/ToCompose/";
        return thisDirectory;
    }

}
