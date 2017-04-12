package cz.fidentis.model;

// Model.java
// Based on OBJLoader/parser by Andrew Davison, February 2007, ad@fivedots.coe.psu.ac.th

/* Load the OBJ model, move to offset position, scale.

 The model can have vertices, normals and tex coordinates, and
 refer to materials in a MTL file.
 */
import cz.fidentis.model.corner_table.CornerTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína furmanová.
 */
public class Model {
    // collection of vertices, normals and texture coords for the model

    private ArrayList<Vector3f> verts;
    private ArrayList<Vector3f> normals;
    private ArrayList<Vector3f> texCoords;
    private boolean hasTCs3D = true;
    // whether the model uses 3D or 2D tex coords
    private boolean flipTexCoords = false;
    // whether tex coords should be flipped around the y-axis
    private Faces faces;              // model faces
//    private FaceMaterials faceMats;   // materials used by faces
    private Materials materials;      // materials defined in MTL file
    private Dimensions modelDims;  // model dimensions
    private CornerTable cornerTable;
    //face part

    private String name;
    private String directoryPath;
    private File modelFile;

    /**
     * Initialize model data.
     */
    public Model() {
        initModelData();
    }

    private void initModelData() {
        verts = new ArrayList<Vector3f>();
        //  originalVerts = new ArrayList<Tuple3>();
        normals = new ArrayList<Vector3f>();
        texCoords = new ArrayList<Vector3f>();

        faces = new Faces(verts, normals, texCoords);
        //   faceMats = new FaceMaterials();
        modelDims = new Dimensions();
    }  // end of initModelData()

    public void setModelDims(Dimensions modelDims) {
        this.modelDims = modelDims;
        
    }

    
    
    //
    /**
     * Move center of the model to 0,0,0
     */
    public void centralize() {
        // get the model's center point
        Vector3f center = modelDims.getOriginalCenter();
        Vector3f vert;
        float x, y, z;

        for (int i = 0; i < verts.size(); i++) {
            vert = verts.get(i);
            x = vert.getX() - center.getX();
            verts.get(i).setX(x);
            y = vert.getY() - center.getY();
            verts.get(i).setY(y);
            z = vert.getZ() - center.getZ();
            verts.get(i).setZ(z);

        }

    } // end of centralize()
    
    //centralize vertices same way as model
    public void centralize(List<Vector3f> vertices){
       // get the model's center point
        Vector3f center = modelDims.getOriginalCenter();
        Vector3f vert;
        float x, y, z;

        for (int i = 0; i < vertices.size(); i++) {
            vert = vertices.get(i);
            x = vert.getX() - center.getX();
            vertices.get(i).setX(x);
            y = vert.getY() - center.getY();
            vertices.get(i).setY(y);
            z = vert.getZ() - center.getZ();
            vertices.get(i).setZ(z);

        } 
    }

    //decentralize passed verticies based on centralization of this model
    public void decentralize(List<Vector3f> vertices) {
        Vector3f center = modelDims.getOriginalCenter();
        Vector3f vert;
        float x, y, z;

        for (int i = 0; i < vertices.size(); i++) {
            vert = vertices.get(i);
            x = vert.getX() + center.getX();
            vertices.get(i).setX(x);
            y = vert.getY() + center.getY();
            vertices.get(i).setY(y);
            z = vert.getZ() + center.getZ();
            vertices.get(i).setZ(z);

        }
    }

    /*  public void drawMesh(GL2 gl, int mode)
     {
     gl.glPushMatrix();
     materials.clearUsedMaterials();
     for (int i = 0; i < faces.getNumFaces(); i++) {

     }
     }
     */
    public void drawForShaders(GL2 gl, ArrayList<VertexInfo> info, int shaderVertices, int shaderNormals, int shaderPrincipal, int shaderSecondary) {

        gl.glPushMatrix();
        for (int i = 0; i < faces.getNumFaces(); i++) {
            faces.renderFace(i, gl, info, shaderVertices, shaderNormals, shaderPrincipal, shaderSecondary);
        }
        gl.glPopMatrix();

    }

    /**
     *
     * @param gl
     */
    public void drawWithoutTextures(GL2 gl) {
        drawWithoutTextures(gl, null);
    }

    /**
     *
     * @param gl
     * @param dropFaces faces to be hidden when rendering model.
     */
    public void drawWithoutTextures(GL2 gl, ArrayList<Integer> dropFaces) {

        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);
        
        gl.glBegin(GL2.GL_TRIANGLES);
        
        for (int i = 0; i < faces.getNumFaces(); i++) {
            faces.renderFace(i, flipTexCoords, gl);
        }
        
        
        gl.glEnd();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
        gl.glPopMatrix();

    }

    /**
     *
     * @param gl
     */
    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        if (materials != null) {
            materials.clearUsedMaterials();
        }
        // render the model face-by-face
        String faceMat;
        
        
        //render material
        for (int i = 0; i < faces.getNumFaces(); i++) {

            faceMat = faces.findMaterial(i);      // get material used by face i
            if (faceMat != null && materials != null) {
                flipTexCoords = materials.renderWithMaterial(faceMat, gl);
            }  // render using that material
        }
        
        //render geometry
        gl.glBegin(GL2.GL_TRIANGLES);

        for (int i = 0; i < faces.getNumFaces(); i++) {

            faceMat = faces.findMaterial(i);      // get material used by face i
            if (faceMat != null && materials != null) {
                flipTexCoords = materials.renderWithMaterial(faceMat, gl);
            }  // render using that material

            faces.renderFace(i, flipTexCoords, gl);                  // draw face i
        }
        
        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glPopAttrib();
        gl.glPopMatrix();

    }
    
    public double getVertAngle(int faceIdx, int vertIdx) {
        List<Vector3f> neighborVerts = new ArrayList<>();

        for (int vert : faces.getFaceVertIdxs(faceIdx)) {
            if (vert != vertIdx) {
                neighborVerts.add(verts.get(vert));
            }
        }

        Vector3f angleVert = verts.get(vertIdx);

        Vector3f vectorBA = new Vector3f(angleVert.x - neighborVerts.get(0).x,
                                         angleVert.y - neighborVerts.get(0).y,
                                         angleVert.z - neighborVerts.get(0).z);
        Vector3f vectorBC = new Vector3f(angleVert.x - neighborVerts.get(1).x,
                                         angleVert.y - neighborVerts.get(1).y,
                                         angleVert.z - neighborVerts.get(1).z);

        return vectorBA.angle(vectorBC);
    }
    
    public double getAreaOfElement(int faceIdx){
        int[] faceVerts = faces.getFaceVertIdxs(faceIdx);
        
        Vector3f pa = verts.get(faceVerts[0]);
        Vector3f pb = verts.get(faceVerts[1]);
        Vector3f pc = verts.get(faceVerts[2]);
        
        Vector3f vectorAB = new Vector3f(pb.x - pa.x, pb.y - pa.y, pb.z - pa.z);
        Vector3f vectorAC = new Vector3f(pc.x - pa.x, pc.y - pa.y, pc.z - pa.z);
        
        return 0.5 * Math.sqrt( Math.pow(vectorAB.y * vectorAC.z - vectorAB.z - vectorAC.y, 2) + 
                                Math.pow(vectorAB.z * vectorAC.x - vectorAB.x - vectorAC.z, 2) +
                                Math.pow(vectorAB.x * vectorAC.y - vectorAB.y - vectorAC.x, 2) );
    }



    /**
     *
     * @return model dimensions
     */
    public Dimensions getModelDims() {
        return modelDims;
    }

    /**
     *
     * @return model materials
     */
    public Materials getMatrials() {
        return materials;
    }

    /**
     *
     * @return model vertices
     */
    public ArrayList<Vector3f> getVerts() {
        return verts;
    }

    /**
     *
     * @return model normals
     */
    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    /**
     *
     * @return modle texture coordinates
     */
    public ArrayList<Vector3f> getTexCoords() {
        return texCoords;
    }

    public Vector3f getVertexNormal(int vertexIndex) {
        for (int i = 0; i < faces.getNumFaces(); i++) {
            int[] vertIdx = faces.getFaceVertIdxs(i);
            for (int j = 0; j < vertIdx.length; j++) {
                if (vertIdx[j] - 1 == vertexIndex) {
                    return normals.get(faces.getFaceNormalIdxs(i)[j] - 1);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param hasTCs3D set true, if model has 3D texture coordinates.
     */
    public void setHasTCs3D(boolean hasTCs3D) {
        this.hasTCs3D = hasTCs3D;
    }

    public void setFile(File file) {
        modelFile = file;
    }

    public File getFile() {
        return modelFile;
    }

    /**
     *
     * @return true, if model has 3D texture coordinates.
     */
    public boolean hasTCs3D() {
        return hasTCs3D;
    }

    /**
     *
     * @param materials model Materials.
     */
    public void setMaterials(Materials materials) {
        this.materials = materials;
    }

    /**
     *
     * @return model faces.
     */
    public Faces getFaces() {
        return faces;
    }
    
    public void setFaces(Faces f) {
        this.faces = f;
    }

    /**
     *
     * @param verts model vertices.
     */
    public void setVerts(ArrayList<Vector3f> verts) {

        this.verts = verts;
        faces.setVerts(verts);
    }

    public CornerTable getCornerTable() {
        return cornerTable;
    }

    public void setCornerTable(CornerTable cornerTable) {
        this.cornerTable = cornerTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
        faces.setNormals(normals);
    }

    public void setTextures(ArrayList<Vector3f> textures) {
        this.texCoords = textures;
        faces.setTextures(texCoords);
    }
    
    public Model copy(){
        Model model = new Model();
        model.setFile(modelFile);
        model.setDirectoryPath(modelFile.getParent() + File.separator);
        model.setName(modelFile.getName());
        
        List<Vector3f> tmpList = new ArrayList<>(verts.size());
        for(Vector3f v : verts){
            tmpList.add(new Vector3f(v.x, v.y, v.z));
        }
        model.setVerts((ArrayList<Vector3f>) tmpList); 
        
        tmpList = new ArrayList<>(normals.size());
        for(Vector3f v : normals){
            tmpList.add(new Vector3f(v.x, v.y, v.z));
        }
        model.setNormals((ArrayList<Vector3f>) tmpList);
        
        tmpList = new ArrayList<>(texCoords.size());
        for(Vector3f v : texCoords){
            tmpList.add(new Vector3f(v.x, v.y, v.z));
        }
        model.setTextures((ArrayList<Vector3f>) tmpList);
        
        //need to create deep copy from rest too
        model.setHasTCs3D(hasTCs3D);
        model.setMaterials(this.getMatrials());
        model.setModelDims(modelDims);
        model.setCornerTable (new CornerTable(model));
        model.setFaces(faces);
        model.getModelDims().setBoundingBox(new ArrayList<Vector3f>(model.getModelDims().getBoundingBox()));
        
         return model ;
    }

    public void drawForShaders(GL2 gl, int curDistanceAttrib,List<Float> dist) {
        gl.glPushMatrix();
        for (int i = 0; i < faces.getNumFaces(); i++) {
            faces.renderFace(i, gl, curDistanceAttrib,dist);
        }
        gl.glPopMatrix();
    }

} // end of Model class
