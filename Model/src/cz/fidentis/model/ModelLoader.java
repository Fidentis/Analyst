/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.model;

import cz.fidentis.model.corner_table.CornerTable;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import static java.io.File.separatorChar;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class ModelLoader {
    private static ModelLoader instance;
    
    private Model model;
    private static final float DUMMY_Z_TC = -5.0f;

    private ModelLoader() {
    }
    
    public static ModelLoader instance(){
        if(instance == null)
            instance = new ModelLoader();
        
        return instance;
    }    

    public Model loadModel(File modelFile, Boolean createDcel, Boolean centralize) {
        model = new Model();
        //System.out.println("Loading" + modelFile.getName());

        String fnm = modelFile.getPath();
        model.setFile(modelFile);
        model.setDirectoryPath(modelFile.getParent() + File.separator);
        model.setName(modelFile.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(fnm));) {

            if (fnm.substring(fnm.lastIndexOf(".") + 1).equalsIgnoreCase("obj")) {
                readObjModel(br, modelFile);
            }
            if (fnm.substring(fnm.lastIndexOf(".") + 1).equalsIgnoreCase("stl")) {
                String line;
                line = br.readLine().trim().toLowerCase();
                if (line.startsWith("solid")) {
                    readStlModel(br);
                } else {
                    readStlModel(modelFile);
                }
            }
            if (fnm.substring(fnm.lastIndexOf(".") + 1).equalsIgnoreCase("ply")) {
                readPlyModel(br, modelFile);
            }

            //br.close();
        } catch (IOException e) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, e);
        }

        if (centralize) {
            model.centralize();
        }

        // TODO - flag in constructor
        model.setCornerTable(new CornerTable(model));

        return model;

    }  // end of loadModel()

    private void readStlModel(BufferedReader br) // parse the STL file line-by-line
    {
        boolean isLoaded = true;
        ArrayList<Vector3f> normals = new ArrayList<>();
        String line;
        boolean isFirstCoord = true;

        try {
            line = br.readLine();
            while (((line) != null) && isLoaded) {
                if (line.length() > 0) {
                    line = line.trim().toLowerCase();

                    if (line.startsWith("facet normal")) {   // facet normal
                        line = line.substring(6);
                        Vector3f normal = readTuple3(line);

                        int i1 = 0;
                        int i2 = 0;
                        int i3 = 0;

                        for (int i = 0; i < 3; i++) {
                            while (!line.startsWith("vertex")) {
                                line = br.readLine().trim().toLowerCase();
                            }
                            Vector3f vert = readTuple3(line);
                            switch (i) {
                                case 0:
                                    i1 = addSTLVert(vert, isFirstCoord, normals, normal);
                                    break;
                                case 1:
                                    i2 = addSTLVert(vert, isFirstCoord, normals, normal);
                                    break;
                                case 2:
                                    i3 = addSTLVert(vert, isFirstCoord, normals, normal);
                                    break;

                            }
                            line = br.readLine().trim().toLowerCase();
                            isFirstCoord = false;
                        }
                        List<Integer> ind = new ArrayList<Integer>();
                        ind.add(i1);
                        ind.add(i2);
                        ind.add(i3);
                        model.getFaces().addFace(ind);
                    }
                }
                line = br.readLine();
            }
            model.getNormals().clear();
            addSTLNormals(normals);
            model.setHasTCs3D(false);
            // br.close();
        } catch (IOException e) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, e);
        }

        if (!isLoaded) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, "Error loading model");
        } else {
            model.getModelDims().setBoundingBox(model.getModelDims().getCentralizedBoundingBox());
        }
    }

    private void readStlModel(File modelFile) // parse the STL file line-by-line
    {
        ArrayList<Vector3f> normals = new ArrayList<>();
        boolean isFirstCoord = true;
        try {
            FileInputStream is = new FileInputStream(modelFile);
            DataInputStream inputStream = new DataInputStream(is);
            //int faceCounter = 0;
            try {
                //int n = 0;
                byte[] buffer = new byte[4];
                inputStream.skip(80);
                // n =
                inputStream.read(buffer);
                int size = java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
                //    List<Float> vertexList = new ArrayList<Float>();
                for (int i = 0; i < size; i++) {
                    //normal
                    inputStream.read(buffer);
                    float nx = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
                    inputStream.read(buffer);
                    float ny = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
                    inputStream.read(buffer);
                    float nz = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());

                    int i1 = 0;
                    int i2 = 0;
                    int i3 = 0;

                    for (int j = 0; j < 3; j++) {
                        inputStream.read(buffer);
                        float x = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
                        inputStream.read(buffer);
                        float y = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
                        inputStream.read(buffer);
                        float z = Float.intBitsToFloat(java.nio.ByteBuffer.wrap(buffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
                        Vector3f vert = new Vector3f(x, y, z);
                        switch (j) {
                            case 0:
                                i1 = addSTLVert(vert, isFirstCoord, normals, new Vector3f(nx, ny, nz));
                                break;
                            case 1:
                                i2 = addSTLVert(vert, isFirstCoord, normals, new Vector3f(nx, ny, nz));
                                break;
                            case 2:
                                i3 = addSTLVert(vert, isFirstCoord, normals, new Vector3f(nx, ny, nz));
                                break;
                        }

                        isFirstCoord = false;
                    }
                    inputStream.skip(2);
                    List<Integer> ind = new ArrayList<Integer>();
                    ind.add(i1);
                    ind.add(i2);
                    ind.add(i3);
                    model.getFaces().addFace(ind);
                }
                model.getNormals().clear();
                addSTLNormals(normals);
                model.setHasTCs3D(false);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                try {
                    inputStream.close();

                } catch (IOException e) {
                } finally {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (FileNotFoundException e) {
        }
    }

    private void readObjModel(BufferedReader br, File modelFile) // parse the OBJ file line-by-line
    {
        boolean isLoaded = true;
        String line;
        boolean isFirstCoord = true;
        boolean isFirstTC = true;
        int numFaces = 0;

        try {
            String currentMaterial = "default";
            String matLib = "";
            while (((line = br.readLine()) != null) && isLoaded) {
                if (line.length() > 0) {
                    line = line.trim();

                    if (line.startsWith("v ")) {   // vertex
                        isLoaded = addVert(line, isFirstCoord);
                        if (isFirstCoord) {
                            isFirstCoord = false;
                        }
                    } else if (line.startsWith("vt")) {   // tex coord
                        isLoaded = addTexCoord(line, isFirstTC);
                        if (isFirstTC) {
                            isFirstTC = false;
                        }
                    } else if (line.startsWith("vn")) {
                        isLoaded = addNormal(line);
                    } else if (line.startsWith("f ")) {  // face
                        isLoaded = model.getFaces().addFace(line);
                        model.getFaces().addMaterialUse(numFaces, currentMaterial);
                        numFaces++;
                    } else if (line.startsWith("mtllib ")) {   // load material
                        matLib = line.substring(7);
                        model.setMaterials(new Materials(matLib, modelFile));
                    } else if (line.startsWith("usemtl ")) {
                        String materialFileId = matLib.replace(".mtl", "");
                        currentMaterial = materialFileId + line.substring(7);
                    }
                }
            }
            //closed in method from which it has been called
            //br.close();
        } catch (IOException e) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, e);
        }

        if (!isLoaded) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, "Error loading model");
        } else {
            model.getModelDims().setBoundingBox(model.getModelDims().getCentralizedBoundingBox());
        }
    } // end of readObjModel()

    private int addSTLVert(Vector3f vert, boolean isFirstCoord, ArrayList<Vector3f> normals, Vector3f normal) /* Add vertex  to vert ArrayList,
     and update the model dimension's info. */ {
        ArrayList<Vector3f> verts = model.getVerts();
        int index = verts.indexOf(vert);
        if (index > -1) {
            normals.get(index).add(normal);
            return index + 1;
        }

        model.getVerts().add(vert);
        normals.add(normal);
        if (isFirstCoord) {
            model.getModelDims().set(vert);
        } else {
            model.getModelDims().update(vert);
        }
        return model.getVerts().size();

    } // end of addVert()

    private boolean addVert(String line, boolean isFirstCoord) /* Add vertex from line "v x y z" to vert ArrayList,
     and update the model dimension's info. */ {
        Vector3f vert = readTuple3(line);
        if (vert != null) {
            model.getVerts().add(vert);
            if (isFirstCoord) {
                model.getModelDims().set(vert);
            } else {
                model.getModelDims().update(vert);
            }
            return true;
        }
        return false;
    } // end of addVert()

    private Vector3f readTuple3(String line) /* The line starts with an OBJ word ("v" or "vn"), followed
     by three floats (x, y, z) separated by spaces
     */ {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        tokens.nextToken();    // skip the OBJ word

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

    private boolean addTexCoord(String line, boolean isFirstTC) /* Add the texture coordinate from the line "vt x y z" to
     the texCoords ArrayList. There may only be two tex coords
     on the line, which is determined by looking at the first
     tex coord line. */ {
        if (isFirstTC) {
            model.setHasTCs3D(checkTC3D(line));
        }

        Vector3f texCoord = readTCTuple(line);
        if (texCoord != null) {
            model.getTexCoords().add(texCoord);
            return true;
        }
        return false;
    }  // end of addTexCoord()

    private boolean checkTC3D(String line) /* Check if the line has 4 tokens, which will be
     the "vt" token and 3 tex coords in this case. */ {
        String[] tokens = line.split(separatorChar + "s+");
        return (tokens.length == 4);
    }  // end of checkTC3D()

    private Vector3f readTCTuple(String line) /* The line starts with a "vt" OBJ word and
     two or three floats (x, y, z) for the tex coords separated
     by spaces. If there are only two coords, then the z-value
     is assigned a dummy value, DUMMY_Z_TC.
     */ {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        tokens.nextToken();    // skip "vt" OBJ word

        try {
            float x = Float.parseFloat(tokens.nextToken());
            float y = Float.parseFloat(tokens.nextToken());

            float z = DUMMY_Z_TC;
            if (model.hasTCs3D()) {
                z = Float.parseFloat(tokens.nextToken());
            }

            return new Vector3f(x, y, z);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        return null;   // means an error occurred
    }  // end of readTCTuple()

    private void addSTLNormals(ArrayList<Vector3f> normals) {
        for (int i = 0; i < normals.size(); i++) {
            normals.get(i).normalize();
            model.getNormals().add(normals.get(i));
        }
    }

    private boolean addNormal(String line) // add normal from line "vn x y z" to the normals ArrayList
    {
        Vector3f normCoord = readTuple3(line);
        if (normCoord != null) {
            model.getNormals().add(normCoord);
            return true;
        }
        return false;
    }  // end of addNormal()

    private void readPlyModel(BufferedReader br, File modelFile) {
        String line;
        int numVertices;
        int numVertexProp = 0;
        int numFaces;
        String type = "";
        int length = 0;
        List<Integer> byts = new ArrayList<Integer>();
        DataInputStream inputStream = null;

        FileInputStream is = null;
        try {
            line = br.readLine();
            length += line.getBytes().length;
            line = br.readLine();
            length += line.getBytes().length;
            line = line.trim().toLowerCase();
            if (line.contains("ascii")) {
                type = "ascii";
            }
            if (line.contains("little_endian")) {
                type = "little_endian";
            }
            if (line.contains("big_endian")) {
                type = "big_endian";
            }

            while (!line.startsWith("element vertex")) {
                line = br.readLine();
                length += line.getBytes().length;
                line = line.trim().toLowerCase();
            }
            numVertices = Integer.valueOf(line.substring(line.lastIndexOf(" ") + 1));

            while (!line.startsWith("element face")) {
                numVertexProp++;
                line = br.readLine();
                length += line.getBytes().length;
                line = line.trim().toLowerCase();
                byts.add(getBytesPerProperty(line));
            }

            numVertexProp--;
            numFaces = Integer.valueOf(line.substring(line.lastIndexOf(" ") + 1));
            line = br.readLine();
            length += line.getBytes().length;
            line = line.trim().toLowerCase();
            String polytypeId = line.split(" ")[2];
            String indexType = line.split(" ")[3];

            while (!line.contains("end_header")) {
                line = br.readLine();
                length += line.getBytes().length;
                line = line.toLowerCase();
            }

            if (type.equals("ascii")) {
                for (int i = 0; i < numVertices; i++) {
                    line = br.readLine().trim();
                    addPLYVert(line, i == 0);
                }
                for (int i = 0; i < numFaces; i++) {
                    line = br.readLine().trim();
                    addPLYFace(line);
                }

                for (int i = 0; i < model.getNormals().size(); i++) {
                    model.getNormals().get(i).normalize();
                }
                br.close();
            } else {
                ByteOrder order = java.nio.ByteOrder.BIG_ENDIAN;

                if (type.equals("little_endian")) {
                    order = java.nio.ByteOrder.LITTLE_ENDIAN;
                }
                is = new FileInputStream(modelFile);
                inputStream = new DataInputStream(is);

                byte[] header = new byte[length];
                inputStream.read(header);
                while (!new String(header).contains("end_header")) {
                    length++;
                    byte one = inputStream.readByte();
                    byte[] headerAll = new byte[length];
                    System.arraycopy(header, 0, headerAll, 0, header.length);
                    headerAll[length - 1] = one;
                    header = headerAll;
                }
                inputStream.skip(1);

                for (int i = 0; i < numVertices; i++) {
                    Vector3f v = new Vector3f();
                    for (int j = 0; j < numVertexProp; j++) {
                        byte[] buffer = new byte[byts.get(j)];

                        inputStream.read(buffer);
                        if (j < 3) {
                            float size = java.nio.ByteBuffer.wrap(buffer).order(order).getFloat();
                            switch (j) {
                                case 0:
                                    v.setX(size);
                                    break;
                                case 1:
                                    v.setY(size);
                                    break;
                                case 2:
                                    v.setZ(size);
                                    break;
                            }
                        }
                    }
                    model.getVerts().add(v);
                    model.getNormals().add(new Vector3f());
                    if (i == 0) {
                        model.getModelDims().set(v);
                    } else {
                        model.getModelDims().update(v);
                    }

                }
                for (int i = 0; i < numFaces; i++) {
                    byte[] buffer = new byte[getBytesPerProperty(polytypeId)];
                    inputStream.read(buffer);
                    int polytype = 0;
                    polytype = getIntByType(polytypeId, buffer, order);

                    List<Integer> ind = new ArrayList<Integer>();
                    for (int j = 0; j < polytype; j++) {
                        buffer = new byte[getBytesPerProperty(indexType)];
                        inputStream.read(buffer);
                        int index = getIntByType(indexType, buffer, order);

                        ind.add(index + 1);
                    }

                    model.getFaces().addFace(ind);
                    computeFaceNormal(ind, polytype);
                }

                for (int i = 0; i < model.getNormals().size(); i++) {
                    model.getNormals().get(i).normalize();
                    model.getNormals().get(i).negate();
                }
                is.close();
                inputStream.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean addPLYVert(String line, boolean isFirstCoord) /* Add vertex  to vert ArrayList,
     and update the model dimension's info. */ {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        Vector3f vert = null;
        try {
            float x = Float.parseFloat(tokens.nextToken());
            float y = Float.parseFloat(tokens.nextToken());
            float z = Float.parseFloat(tokens.nextToken());
            vert = new Vector3f(x, y, z);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        if (vert != null) {
            model.getVerts().add(vert);
            model.getNormals().add(new Vector3f());
            if (isFirstCoord) {
                model.getModelDims().set(vert);
            } else {
                model.getModelDims().update(vert);
            }
            return true;
        }
        return false;
    } // end of addVert()

    private void addPLYFace(String line) {
        StringTokenizer tokens = new StringTokenizer(line, " ");
        try {
            int polytype = Integer.parseInt(tokens.nextToken());
            List<Integer> ind = new ArrayList<Integer>();

            for (int i = 0; i < polytype; i++) {
                ind.add(Integer.parseInt(tokens.nextToken()) + 1);
            }
            model.getFaces().addFace(ind);

            computeFaceNormal(ind, polytype);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

    }

    private void computeFaceNormal(List<Integer> ind, int polytype) {
        Vector3f u = new Vector3f(model.getVerts().get(ind.get(1) - 1));
        u.sub(model.getVerts().get(ind.get(0) - 1));
        Vector3f v = new Vector3f(model.getVerts().get(ind.get(2) - 1));
        v.sub(model.getVerts().get(ind.get(0) - 1));

        u.cross(u, v);
        u.negate();

        for (int i = 0; i < polytype; i++) {
            model.getNormals().get(ind.get(i) - 1).add(u);
        }
    }

    private int getBytesPerProperty(String line) {
        if (line.contains("float") || line.contains("uint") || line.contains("int")) {
            return 4;
        }
        if (line.contains("char") || line.contains("uchar")) {
            return 1;
        }
        if (line.contains("short") || line.contains("ushort")) {
            return 2;
        }
        if (line.contains("double")) {
            return 8;
        }
        return 4;
    }

    private int getIntByType(String type, byte[] buffer, ByteOrder order) {
        int i = 0;
        switch (type) {
            case "char":
            case "uchar":
                i = java.nio.ByteBuffer.wrap(buffer).order(order).get();
                break;
            case "short":
            case "ushort":
                i = java.nio.ByteBuffer.wrap(buffer).order(order).getShort();
                break;
            case "float":
                i = (int) java.nio.ByteBuffer.wrap(buffer).order(order).getFloat();
                break;
            case "int":
            case "uint":
                i = java.nio.ByteBuffer.wrap(buffer).order(order).getInt();
                break;
            case "double":
                i = (int) java.nio.ByteBuffer.wrap(buffer).order(order).getDouble();
                break;

        }
        return i;
    }

}
