package cz.fidentis.model;

// Faces.java
// Andrew Davison, February 2007, ad@fivedots.coe.psu.ac.th

/* Faces stores the information for each face of a model.

 A face is represented by three arrays of indicies for
 the vertices, normals, and tex coords used in that face.

 facesVertIdxs, facesTexIdxs, and facesNormIdxs are ArrayLists of
 those arrays; one entry for each face.

 renderFace() is supplied with a face index, looks up the
 associated vertices, normals, and tex coords indicies arrays,
 and uses those arrays to access the actual vertices, normals,
 and tex coords data for rendering the face.

 CHANGES (Feb 2007)
 - changed renderFace() to flip tex coords if necessary

 */
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class Faces {

    private static final float DUMMY_Z_TC = -5.0f;

    /* indicies for vertices, tex coords, and normals used
     by each face */
    private ArrayList<int[]> facesVertIdxs;
    private ArrayList<int[]> facesTexIdxs;
    private ArrayList<int[]> facesNormIdxs;
    // references to the model's vertices, normals, and tex coords
    private ArrayList<Vector3f> verts;
    private ArrayList<Vector3f> normals;
    private ArrayList<Vector3f> texCoords;
    // for reporting
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
    private HashMap<Integer, String> faceMats;

    /**
     *
     * @param vs vertices
     * @param ns normals
     * @param ts texture coordinates
     */
    public Faces(ArrayList<Vector3f> vs, ArrayList<Vector3f> ns,
            ArrayList<Vector3f> ts) {
        verts = vs;
        normals = ns;
        texCoords = ts;

        facesVertIdxs = new ArrayList<int[]>();
        facesTexIdxs = new ArrayList<int[]>();
        facesNormIdxs = new ArrayList<int[]>();

        faceMats = new HashMap<Integer, String>();
    }  // end of Faces()

    public void setVerts(ArrayList<Vector3f> vs) {
        verts = vs;
    }

    void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
    }

    void setTextures(ArrayList<Vector3f> texCoords) {
        this.texCoords = texCoords;
    }

    /**
     * Add face by parsing line from obj file. Indexing of vertex, normal and
     * texture coordinates strats with 1(comes from obj format). A correction of
     * -1 is needed to find a correct coordinates in the list (where indexing
     * strats with 0), whene using these indeces.
     *
     * @param indices
     * @param line line to parse
     * @return true if line vas successfully parsed and face was added.
     */
    public boolean addFace(List<Integer> indices) {
        int v[] = new int[indices.size()];
        for (int i = 0; i < v.length; i++) {
            v[i] = indices.get(i);
        }
        int vt[] = new int[indices.size()];
        facesVertIdxs.add(v);
        facesNormIdxs.add(v);
        facesTexIdxs.add(vt);
        return true;
    }

    public boolean addFace(List<Integer> indicesV, List<Integer> indicesT) {
        int v[] = new int[indicesV.size()];
        for (int i = 0; i < v.length; i++) {
            v[i] = indicesV.get(i);
        }
        int vt[] = new int[indicesT.size()];
        for (int i = 0; i < vt.length; i++) {
            vt[i] = indicesT.get(i);
        }
        facesVertIdxs.add(v);
        facesNormIdxs.add(v);
        facesTexIdxs.add(vt);
        return true;
    }

    public boolean addFace(String line) /* get this face's indicies from line "f v/vt/vn ..."
     with vt or vn index values perhaps being absent. */ {
        try {
            line = line.substring(2);   // skip the "f "
            StringTokenizer st = new StringTokenizer(line, " ");
            int numTokens = st.countTokens();   // number of v/vt/vn tokens
            // create arrays to hold the v, vt, vn indicies
            int v[] = new int[numTokens];
            int vt[] = new int[numTokens];
            int vn[] = new int[numTokens];

            for (int i = 0; i < numTokens; i++) {
                String faceToken = addFaceVals(st.nextToken());  // get a v/vt/vn token
                // System.out.println(faceToken);

                StringTokenizer st2 = new StringTokenizer(faceToken, "/");
                int numSeps = st2.countTokens();  // how many '/'s are there in the token

                v[i] = Integer.parseInt(st2.nextToken());
                vt[i] = (numSeps > 1) ? Integer.parseInt(st2.nextToken()) : 0;
                vn[i] = (numSeps > 2) ? Integer.parseInt(st2.nextToken()) : 0;
                // add 0's if the vt or vn index values are missing;
                // 0 is a good choice since real indicies start at 1
            }
            // store the indicies for this face
            facesVertIdxs.add(v);
            facesTexIdxs.add(vt);
            facesNormIdxs.add(vn);

        } catch (NumberFormatException e) {
            System.out.println("Incorrect face index");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }  // end of addFace()

    private String addFaceVals(String faceStr) /* A face token (v/vt/vn) may be missing vt or vn
     index values; add 0's in those cases.
     */ {
        char chars[] = faceStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        char prevCh = 'x';   // dummy value

        for (int k = 0; k < chars.length; k++) {
            if (chars[k] == '/' && prevCh == '/') {
                sb.append('0');
            }   // add a '0'
            prevCh = chars[k];
            sb.append(prevCh);
        }
        return sb.toString();
    }  // end of addFaceVals()

     /**
     * Renter face
     *
     * @param i index of face
     * @param gl
     * @param info
     * @param shaderVertices
     * @param shaderNumVertices
     */
    public void renderFace(int i, GL2 gl, ArrayList<VertexInfo> info, int shaderVertices, int shaderNormals,int shaderPrincipal,int shaderSecondary) {
        if (i >= facesVertIdxs.size()) {
            return;
        }

        int[] vertIdxs = facesVertIdxs.get(i);
        // get the vertex indicies for face i

        int polytype;
        if (vertIdxs.length == 3) {
            polytype = GL2.GL_TRIANGLES;
        } else if (vertIdxs.length == 4) {
            polytype = GL2.GL_QUADS;
        } else {
            polytype = GL2.GL_POLYGON;
        }

        float vertices[] = new float[9];
        float normals_sh[] = new float[9];
        float principal[] = new float[9];
        float secondary[] = new float[9];
        
        for (int f = 0; f < 3; f++) {
            Vector3f v = info.get(vertIdxs[f] - 1).getSampleVertex();
            vertices[f * 3] = v.x;
            vertices[f * 3 + 1] = v.y;
            vertices[f * 3 + 2] = v.z;
            Vector3f n = info.get(vertIdxs[f] - 1).getSampleNormal();
            normals_sh[f * 3] = n.x;
            normals_sh[f * 3 + 1] = n.y;
            normals_sh[f * 3 + 2] = n.z;
            Vector3f p = info.get(vertIdxs[f] - 1).getSamplePrincipalCurvature();
            principal[f * 3] = p.x;
            principal[f * 3 + 1] = p.y;
            principal[f * 3 + 2] = p.z;
            Vector3f s = info.get(vertIdxs[f] - 1).getSampleSecondaryCurvature();
            secondary[f * 3] = s.x;
            secondary[f * 3 + 1] = s.y;
            secondary[f * 3 + 2] = s.z;
        }
        FloatBuffer a = FloatBuffer.wrap(vertices);
        gl.glUniform3fv(shaderVertices, 9, a);
        FloatBuffer b = FloatBuffer.wrap(normals_sh);
        gl.glUniform3fv(shaderNormals, 9, b);
        FloatBuffer c = FloatBuffer.wrap(principal);
        gl.glUniform3fv(shaderPrincipal, 9, c);
        FloatBuffer d = FloatBuffer.wrap(secondary);
        gl.glUniform3fv(shaderSecondary, 9, d);

        gl.glBegin(polytype);
        // get the normal and tex coords indicies for face i
        int[] normIdxs = facesNormIdxs.get(i);

        Vector3f vert, norm;
        for (int f = 0; f < vertIdxs.length; f++) {
            if (normIdxs[f] != 0) {  // if there are normals, render them
                norm = normals.get(normIdxs[f] - 1);
                gl.glNormal3d(norm.getX(), norm.getY(), norm.getZ());
            }

            vert = verts.get(vertIdxs[f] - 1);  // render the vertices

            gl.glVertex3d(vert.getX(), vert.getY(), vert.getZ());

        }

        gl.glEnd();
    } // end of renderFace()
    
    /**
     * Renter face
     *
     * @param i index of face
     * @param flipTexCoords true if the tex coords need flipping
     * @param gl
     */
    public void renderFace(int i, boolean flipTexCoords, GL2 gl) /* Render the ith face by getting the vertex, normal, and tex
     coord indicies for face i. Use those indicies to access the
     actual vertex, normal, and tex coord data, and render the face.

     Each face uses 3 array of indicies; one for the vertex
     indicies, one for the normal indicies, and one for the tex
     coord indicies.

     If the model doesn't use normals or tex coords then the indicies
     arrays will contain 0's.

     If the tex coords need flipping then the t-values are changed.
     */ {
        if (i >= facesVertIdxs.size()) {
            return;
        }

        int[] vertIdxs = facesVertIdxs.get(i);
        // get the vertex indicies for face i

        int polytype;
        if (vertIdxs.length == 3) {
            polytype = GL2.GL_TRIANGLES;
        } else if (vertIdxs.length == 4) {
            polytype = GL2.GL_QUADS;
        } else {
            polytype = GL2.GL_POLYGON;
        }
        
        //gl.Begin(polytype);

        // get the normal and tex coords indicies for face i
        int[] normIdxs = facesNormIdxs.get(i);
        int[] texIdxs = facesTexIdxs.get(i);

        /* render the normals, tex coords, and vertices for face i
         by accessing them using their indicies */
        Vector3f vert, norm, texCoord;
        double yTC;
        for (int f = 0; f < vertIdxs.length; f++) {
            if (normIdxs[f] != 0) {  // if there are normals, render them
                norm = normals.get(normIdxs[f] - 1);
                gl.glNormal3d(norm.getX(), norm.getY(), norm.getZ());
            }

            if (texIdxs[f] != 0) {
                // if there are tex coords, render them
                texCoord = texCoords.get(texIdxs[f] - 1);
                yTC = texCoord.getY();
                if (flipTexCoords) {
                    yTC = 1.0f - yTC;
                }

                if (texCoord.getZ() == DUMMY_Z_TC) {
                    gl.glTexCoord2d(texCoord.getX(), yTC);
                } else {
                    gl.glTexCoord3d(texCoord.getX(), yTC, texCoord.getZ());
                }
            }
            
            //TODO make sure you only draw triangle, if there is something else than triangle, triangulate

            vert = verts.get(vertIdxs[f] - 1);  // render the vertices
            gl.glVertex3d(vert.getX(), vert.getY(), vert.getZ());

        }

        //gl.glEnd();
    } // end of renderFace()

    /**
     * Store the face index and the material it uses
     *
     * @param faceIdx index of face
     * @param matName material used
     */
    public void addMaterialUse(int faceIdx, String matName) {
        // store the face index and the material it uses

        faceMats.put(faceIdx, matName);
    }

    /**
     *
     * @param faceIdx face index.
     * @return Material of given face.
     */
    public String findMaterial(int faceIdx) {
        return faceMats.get(faceIdx);
    }

    /**
     *
     * @return Number of faces of model.
     */
    public int getNumFaces() {
        return facesVertIdxs.size();
    }

    //return intexes of face verts
    /**
     *
     * @param i index of face.
     * @return Indexes of vertices of given face.
     */
    public int[] getFaceVertIdxs(int i) {
        int[] f = facesVertIdxs.get(i);
        return f;
    }

    public ArrayList<int[]> getFacesVertIdxs() {
        return facesVertIdxs;
    }

    public ArrayList<int[]> getFacesNormIdxs() {
        return facesNormIdxs;
    }
    
    

    /**
     *
     * @param i index of face.
     * @return Indexes of normals of given face.
     */
    public int[] getFaceNormalIdxs(int i) {
        int[] f = facesNormIdxs.get(i);

        return f;
    }

    /**
     *
     * @param i index of face.
     * @return Indexes of texture coordinates of given face.
     */
    public int[] getFaceTexIdxs(int i) {
        int[] f = facesTexIdxs.get(i);

        return f;
    }
    
    public void clearFaces() {
        this.facesNormIdxs.clear();
        this.facesTexIdxs.clear();
        this.facesVertIdxs.clear();
    }

    void renderFace(int i, GL2 gl, int curDistanceAttrib, List<Float> dist) {
        if (i >= facesVertIdxs.size()) {
            return;
        }

        int[] vertIdxs = facesVertIdxs.get(i);
        // get the vertex indicies for face i

        int polytype;
        if (vertIdxs.length == 3) {
            polytype = GL2.GL_TRIANGLES;
        } else if (vertIdxs.length == 4) {
            polytype = GL2.GL_QUADS;
        } else {
            polytype = GL2.GL_POLYGON;
        }
        
  
       
        gl.glBegin(polytype);
        // get the normal and tex coords indicies for face i
        int[] normIdxs = facesNormIdxs.get(i);

        Vector3f vert, norm;
        for (int f = 0; f < vertIdxs.length; f++) {
            if (normIdxs[f] != 0) {  // if there are normals, render them
                norm = normals.get(normIdxs[f] - 1);
                gl.glNormal3d(norm.getX(), norm.getY(), norm.getZ());
            }

             gl.glVertexAttrib1f(curDistanceAttrib,dist.get(vertIdxs[f] - 1));

            vert = verts.get(vertIdxs[f] - 1);  // render the vertices

            gl.glVertex3d(vert.getX(), vert.getY(), vert.getZ());

        }

        gl.glEnd();
    }

    public void setFacesVertIdxs(int i, int[] indexes) {
        this.facesVertIdxs.add(i, indexes);
    }

}  // end of Faces class
