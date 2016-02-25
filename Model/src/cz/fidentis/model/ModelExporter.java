/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.model;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katarína Furmanová
 */
public class ModelExporter {

    ArrayList<Model> models = new ArrayList<Model>();

    /**
     *
     * @param models model to be exported.
     */
    public ModelExporter(ArrayList<Model> models) {
        this.models = models;
    }

    public ModelExporter(Model model) {
        models.add(model);
    }

    /**
     * Export model to OBJ file and materials to mtl. files.
     *
     * @param exportFile file for exporting.
     * @param exportTextures true, if textures should be also exported.
     */
    public void exportModelToObj(File exportFile, Boolean exportTextures) {
        int formatIndex = exportFile.getName().lastIndexOf(".");
        String fileName;
        
        if(formatIndex < 0){
            fileName = exportFile.getName();
        }else{
            fileName = exportFile.getName().substring(0, formatIndex);
        }
        File exportDirectory = new File(exportFile.getParent().toString() + File.separator + fileName);
        exportDirectory.mkdir();

        File mtlFile = new File(exportDirectory + File.separator + fileName + ".mtl");

        ArrayList<Material> materials = new ArrayList<Material>();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getMatrials() != null) {
                materials.addAll(models.get(i).getMatrials().getMatrials());
            }

        }

        for (int i = 0; i < materials.size(); i++) {
            if (exportTextures) {
                if (materials.get(i).getTextureFile() != null) {
                    //    new File(exportFile.getParent().toString() + File.separator + fileName + "_tex").mkdir();
                    File texFile1 = new File(materials.get(i).getTextureFile());
                    File texFile2 = new File(exportDirectory + File.separator + "texture" + i + ".jpg");
                    try (InputStream in = new FileInputStream(texFile1);
                            OutputStream out = new FileOutputStream(texFile2);) {

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.flush();

                        /*in.close();
                         out.close();*/
                    } catch (Exception e) {
                    }

                }

            }
        }

        try (FileWriter fstream = new FileWriter(mtlFile);
                BufferedWriter out = new BufferedWriter(fstream);) {
            for (int i = 0; i < materials.size(); i++) {

                Material mat = materials.get(i);
                out.write("newmtl " + mat.getName() + "\n");
                out.write(materials.get(i).toString());
                if (exportTextures && materials.get(i).getTextureFile() != null) {
                    out.write("map_Kd " + "texture" + i + ".jpg" + "\n");
                }
            }

            out.flush();

            /*out.close();
             fstream.close();*/
        } catch (Exception e) {
        }

        exportFile = new File(exportDirectory + File.separator + fileName + ".obj");
        try (FileWriter fstream = new FileWriter(exportFile);
                BufferedWriter out = new BufferedWriter(fstream);) {
            // ExportBar.setVisible(true);

            int offsetVerts = 0;
            int offsetNormals = 0;
            int offsetTextC = 0;

            out.write("mtllib " + fileName + ".mtl" + "\n");

            for (int i = 0; i < models.size(); i++) {
                Model model = models.get(i);

                for (int j = 0; j < model.getVerts().size(); j++) {
                    out.write("v " + model.getVerts().get(j).getX() + " " + model.getVerts().get(j).getY() + " " + model.getVerts().get(j).getZ() + "\n");
                }
                for (int j = 0; j < model.getNormals().size(); j++) {
                    out.write("vn " + model.getNormals().get(j).getX() + " " + model.getNormals().get(j).getY() + " " + model.getNormals().get(j).getZ() + "\n");
                }
                for (int j = 0; j < model.getTexCoords().size(); j++) {
                    out.write("vt " + model.getTexCoords().get(j).getX() + " " + model.getTexCoords().get(j).getY() + " " + model.getTexCoords().get(j).getZ() + "\n");
                }
                String previousMaterialName = "";

                for (int j = 0; j < model.getFaces().getNumFaces(); j++) {
                    String currentMaterialName;
                    currentMaterialName = model.getFaces().findMaterial(j);
                    if (!previousMaterialName.equals(currentMaterialName)) {
                        previousMaterialName = currentMaterialName;
                        out.write("usemtl " + currentMaterialName + "\n");
                        out.write("g " + currentMaterialName + "\n" + "s 1" + "\n");
                    }
                    out.write("f ");
                    for (int k = 0; k < model.getFaces().getFaceVertIdxs(j).length; k++) {
                        if (model.getFaces().getFaceTexIdxs(j).length > 0) {
                            out.write((model.getFaces().getFaceVertIdxs(j)[k] + offsetVerts) + "/" + (model.getFaces().getFaceTexIdxs(j)[k] + offsetTextC) + "/" + (model.getFaces().getFaceNormalIdxs(j)[k] + offsetNormals) + " ");
                        } else {
                            out.write((model.getFaces().getFaceVertIdxs(j)[k] + offsetVerts) + "/" + "/" + (model.getFaces().getFaceNormalIdxs(j)[k] + offsetNormals) + " ");

                        }

                    }
                    out.write("\n");
                }

                offsetVerts = offsetVerts + model.getVerts().size();
                offsetNormals = offsetNormals + model.getNormals().size();
                offsetTextC = offsetTextC + model.getTexCoords().size();
            }

            out.flush();

            /* out.close();
             fstream.close();*/
        } catch (Exception e) {
        }
    }

    public void exportModelToStl(File exportFile, Boolean binary) {
        if (!binary) {
            try (FileWriter fstream = new FileWriter(exportFile);
                    BufferedWriter out = new BufferedWriter(fstream);) {

                out.write("solid " + exportFile.getName() + "\n");

                for (Model model : models) {
                    for (int j = 0; j < model.getFaces().getNumFaces(); j++) {
                        Vector3f n = new Vector3f();
                        for (int k = 0; k < model.getFaces().getFaceNormalIdxs(j).length; k++) {
                            n.add(model.getNormals().get(model.getFaces().getFaceNormalIdxs(j)[k] - 1));
                        }
                        n.normalize();

                        out.write("facet normal  " + n.getX() + " " + n.getY() + " " + n.getZ() + "\n");
                        out.write("outer loop" + "\n");

                        Vector3f v = new Vector3f();
                        for (int k = 0; k < model.getFaces().getFaceVertIdxs(j).length; k++) {
                            v = model.getVerts().get(model.getFaces().getFaceVertIdxs(j)[k] - 1);
                            out.write("vertex " + v.getX() + " " + v.getY() + " " + v.getZ() + "\n");
                        }
                        out.write("endloop" + "\n");
                        out.write("endfacet" + "\n");

                    }
                }
                out.write("endsolid " + exportFile.getName() + "\n");

                out.flush();
                /*out.close();
                 fstream.close();*/

            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            try (FileOutputStream fos = new FileOutputStream(exportFile);
                    DataOutputStream out = new DataOutputStream(fos);) {

                String s = "Fidentis Analyst STL export.";
                out.write(s.getBytes());

                byte[] b = new byte[80 - s.getBytes().length];
                out.write(b, 0, 80 - s.getBytes().length);
                int numFaces = 0;
                for (Model model : models) {
                    numFaces += model.getFaces().getNumFaces();
                }
                ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                bb.putInt(numFaces);

                out.write(bb.array(), 0, 4);

                for (Model model : models) {
                    for (int j = 0; j < model.getFaces().getNumFaces(); j++) {
                        Vector3f n = new Vector3f();
                        for (int k = 0; k < model.getFaces().getFaceNormalIdxs(j).length; k++) {
                            n.add(model.getNormals().get(model.getFaces().getFaceNormalIdxs(j)[k] - 1));
                        }
                        n.normalize();
                        out.write(setBuffer(bb, n.getX()).array(), 0, 4);
                        out.write(setBuffer(bb, n.getY()).array(), 0, 4);
                        out.write(setBuffer(bb, n.getZ()).array(), 0, 4);

                        Vector3f v;
                        for (int k = 0; k < 3; k++) {
                            v = model.getVerts().get(model.getFaces().getFaceVertIdxs(j)[k] - 1);
                            out.write(setBuffer(bb, v.getX()).array(), 0, 4);
                            out.write(setBuffer(bb, v.getY()).array(), 0, 4);
                            out.write(setBuffer(bb, v.getZ()).array(), 0, 4);
                        }
                        b = new byte[2];
                        out.write(b, 0, 2);

                    }
                }

                out.flush();

                /* out.close();
                 fos.close();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ByteBuffer setBuffer(ByteBuffer bb, float f) {
        bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.putFloat(f);
        return bb;
    }

    public void exportModelToPLY(File exportFile) {
        try (FileOutputStream fos = new FileOutputStream(exportFile);
                DataOutputStream binaryOut = new DataOutputStream(fos);) {

            binaryOut.writeBytes("ply" + "\n");
            binaryOut.writeBytes("format binary_little_endian 1.0\n");

            int vertices = 0;
            int faces = 0;
            for (Model model : models) {
                vertices += model.getVerts().size();
                faces += model.getFaces().getNumFaces();
            }
            binaryOut.writeBytes("element vertex " + vertices + "\n");
            binaryOut.writeBytes("property float x\n");
            binaryOut.writeBytes("property float y\n");
            binaryOut.writeBytes("property float z\n");
            binaryOut.writeBytes("element face " + faces + "\n");
            binaryOut.writeBytes("property list uchar int vertex_indices\n");
            binaryOut.writeBytes("end_header\n");

            for (Model model : models) {
                for (int i = 0; i < model.getVerts().size(); i++) {
                    ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                    binaryOut.write(setBuffer(bb, model.getVerts().get(i).x).array(), 0, 4);
                    binaryOut.write(setBuffer(bb, model.getVerts().get(i).y).array(), 0, 4);
                    binaryOut.write(setBuffer(bb, model.getVerts().get(i).z).array(), 0, 4);
                }
            }
            int offsetVerts = 0;
            for (Model model : models) {
                for (int j = 0; j < model.getFaces().getNumFaces(); j++) {
                    binaryOut.write((byte) model.getFaces().getFaceVertIdxs(j).length);
                    for (int k = 0; k < model.getFaces().getFaceVertIdxs(j).length; k++) {
                        ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                        bb.putInt(model.getFaces().getFaceVertIdxs(j)[k] - 1 + offsetVerts);
                        binaryOut.write(bb.array(), 0, 4);
                    }
                }
                offsetVerts = offsetVerts + model.getVerts().size();
            }
            //  out.close();

            binaryOut.flush();

            /*binaryOut.close();
             fos.close();*/
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
