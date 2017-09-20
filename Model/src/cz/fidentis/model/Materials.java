package cz.fidentis.model;

// Materials.java
// Andrew Davison, February 2007, ad@fivedots.coe.psu.ac.th

/* This class does two main tasks:
 * it loads the material details from the MTL file, storing
 them as Material objects in the materials ArrayList.

 * it sets up a specified material's colours or textures
 to be used when rendering -- see renderWithMaterial()

 CHANGES (Feb 2007)
 - a flipTexCoords global
 - renderWithMaterial() sets and returns flipTexCoords
 */
import com.jogamp.opengl.util.texture.Texture;
import java.io.BufferedReader;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class Materials {

    private ArrayList<Material> materials;
     // stores the Material objects built from the MTL file data

    // for storing the material currently being used for rendering
    private String renderMatName;
    private boolean flipTexCoords;
    private String directory;
    private String materialFileName;
    // whether tex coords should be flipped around the y-axis

    /**
     *
     * @param mtlFnm material file name
     * @param model model file
     */
    public Materials(String mtlFnm, File model) {
        renderMatName = null;
        flipTexCoords = false;
        materials = new ArrayList<Material>();
        materialFileName = mtlFnm;
        directory = model.getParent() + File.separator;
        String mfnm = directory + mtlFnm;

        try {
            //System.out.println("Loading material from " + mfnm);
            BufferedReader br = new BufferedReader(new FileReader(mfnm));
            readMaterials(br);
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    } // end of Materials()

    /**
     *
     * @param mtlFnm material file name
     * @param model model file
     */
    public Materials(List<String> mtlFnm, List<File> model) {
        renderMatName = null;
        flipTexCoords = false;
        materials = new ArrayList<Material>();
        for (int i = 0; i < mtlFnm.size(); i++) {
            materialFileName = mtlFnm.get(i);
            directory = model.get(i).getParent() + File.separator;
            String mfnm = directory + materialFileName;
            try {
                //System.out.println("Loading material from " + mfnm);
                BufferedReader br = new BufferedReader(new FileReader(mfnm));
                readMaterials(br);
                br.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public String getMaterialFileName() {
        return materialFileName;
    }

    private void readMaterials(BufferedReader br) /* Parse the MTL file line-by-line, building Material
     objects which are collected in the materials ArrayList. */ {

        String line;
        Material currMaterial = null;  // current material
        Boolean added = false;
        String materialFileId = materialFileName.replace(".mtl", "");

        try {
            while (((line = br.readLine()) != null)) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }

                if (line.startsWith("newmtl ")) {  // new material
                    currMaterial = new Material(materialFileId + line.substring(7));
                    materials.add(currMaterial);
                } else if (line.startsWith("map_Kd ")) {  // texture filename

                    String fileName;
                    if (line.contains(Character.toString(separatorChar))) {
                        fileName = directory + line.substring(line.lastIndexOf(separatorChar) + 1);
                    } else {
                        fileName = directory + line.substring(7);
                    }
                    currMaterial.setTextureFile(fileName);

                } else if (line.startsWith("Ka ")) {
                    currMaterial.setKa(readTuple3(line));
                } else if (line.startsWith("Kd ")) {
                    currMaterial.setKd(readTuple3(line));
                } else if (line.startsWith("Ks ")) {
                    currMaterial.setKs(readTuple3(line));
                } else if (line.startsWith("Ns ")) {  // shininess
                    float val = Float.valueOf(line.substring(3)).floatValue();
                    currMaterial.setNs(val);
                } else if (line.charAt(0) == 'd') {    // alpha
                    float val = Float.valueOf(line.substring(2)).floatValue();
                    currMaterial.setD(val);
                } else if (line.startsWith("illum ")) { // illumination model
                    float val = Float.valueOf(line.substring(6)).floatValue();
                    currMaterial.setIllum(val);
                } else if (line.charAt(0) == '#') {
                    continue;
                }

            }

            //  if (currMaterial != null) {
            //          materials.add(currMaterial);
            //      }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    } // end of readMaterials()

    private Vector3f readTuple3(String line) /* The line starts with an MTL word such as Ka, Kd, Ks, and
     the three floats (x, y, z) separated by spaces
     */ {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        tokens.nextToken();    // skip MTL word

        try {
            float x = Float.parseFloat(tokens.nextToken());
            float y = Float.parseFloat(tokens.nextToken());
            float z = Float.parseFloat(tokens.nextToken());

            return new Vector3f(x, y, z);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        return null;   // means an error occurred
    }  // end of readTuple3()

    // ----------------- using a material at render time -----------------
    /**
     *
     * @param faceMat name of the material to be used
     * @param gl
     * @return Return true if the texture coords need flipping.
     */
    public boolean renderWithMaterial(String faceMat, GL2 gl) /* Render using the texture associated with the
     material, faceMat. But only change things if faceMat is
     different from the current rendering material, whose name
     is stored in renderMatName.

     Return true/false if the texture coords need flipping,
     and store the current value in a global
     */ {

        if (renderMatName == null || !faceMat.equals(renderMatName)) {   // is faceMat is a new material?
            renderMatName = faceMat;
            Material mat = null;
            for (int i = 0; i < materials.size(); i++) {
                if (materials.get(i).hasName(renderMatName)) {
                    mat = materials.get(i);
                }
            }
            if (mat != null) {
                switchOffTex(gl);
                // set up new rendering material
                Texture tex = getTexture(renderMatName, gl);
                if (tex != null) {   // use the material's texture
                    flipTexCoords = tex.getMustFlipVertically();
                    switchOnTex(tex, gl);

                } else {
                    switchOffTex(gl);
                    mat.setMaterialColors(gl);
                }

            }
        }
        return flipTexCoords;
    }  // end of renderWithMaterial()

    private void switchOffTex(GL gl) // switch texturing off and put the lights on;
    {
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_LIGHTING);
        // }
    } // end of resetMaterials()

    private void switchOnTex(Texture tex, GL2 gl) // switch the lights off, and texturing on
    {
        gl.glEnable(GL2.GL_LIGHTING);
        useWhiteMtl(gl);
        tex.enable(gl);
        tex.bind(gl);

    } // end of resetMaterials()

    private void useWhiteMtl(GL2 gl) {
        float[] color = {1f, 1f, 1f, 1f};
        //  float[] color = {0.868f, 0.64f, 0.548f, 1f};
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
        float[] colorKs = {0, 0, 0, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
    }

    private Texture getTexture(String matName, GL2 gl) // return the texture associated with the material name
    {
        for (int i = 0; i < materials.size(); i++) {
            Material m = materials.get(i);
            if (m.hasName(matName)) {
                if (m.getTexture() == null) {
                    if (m.getTextureFile() != null) {
                        m.loadTexture(m.getTextureFile(), gl);
                    }
                }
                return m.getTexture();
            }

        }
        return null;
    } // end of getTexture()

    /*
     private void setMaterialColors(String matName, GL gl)
     // start rendering using the colours specifies by the named material
     {
     Material m;
     for (int i = 0; i < materials.size(); i++) {
     m = (Material) materials.get(i);
     if (m.hasName(matName))
     m.setMaterialColors(gl);
     }
     }  // end of setMaterialColors()
     */
    /**
     *
     * @param gl
     */
    public void reloadTextures(GL2 gl) {

        for (int i = 0; i < materials.size(); i++) {
            Material m = materials.get(i);
            if (m.getTextureFile() != null) {
                m.loadTexture(m.getTextureFile(), gl);
            }
        }
    }

    /**
     *
     * @return List of materials.
     */
    public ArrayList<Material> getMatrials() {
        return materials;
    }

    /**
     *
     */
    public void clearUsedMaterials() {
        renderMatName = null;
    }
} // end of Materials class
