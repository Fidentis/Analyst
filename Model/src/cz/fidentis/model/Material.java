package cz.fidentis.model;


// Material.java
// Andrew Davison, November 2006, ad@fivedots.coe.psu.ac.th

/* A Material object holds colour and texture information
   for a named material.

   The Material object also manages the rendering using its
   colours (see setMaterialColors()). The rendering using the
   texture is done by the Materials object.

*/




import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.*;

import javax.media.opengl.*;
import javax.vecmath.Vector3f;



/**
 *
 * @author Katarína Furmanová
 */
public class Material
{
  private String name;

  // colour info
  private Vector3f ka, kd, ks;   // ambient, diffuse, specular colours
  private float ns, d,  illum;  // shininess and alpha

  // texture info
  private String texFnm;
  private Texture texture;


    /**
     *
     * @param nm name of the material
     */
    public Material(String nm)
  {
    name = nm;

    d = 1.0f; ns = 0.0f; illum = 2f;
    ka = null; kd = null; ks = null;

    texFnm = null;
    texture = null;
  }  // end of Material()
    @Override
   public String toString()
    {
        String material = "";
        material = material + "  Ns " + ns + "\n";
        material = material + "  d " + d + "\n";
         material = material + "  illum " + illum + "\n";
        if (ka != null) {
            material = material + "  Ka " + ka.getX() +" "+ ka.getY()+" "+ ka.getZ()+"\n";
        }
        if (kd != null) {
            material = material + "  Kd " + kd.getX() +" "+ kd.getY()+" "+ kd.getZ() + "\n";
        }
        if (ks != null) {
            material = material + "  Ks " + ks.getX() +" "+ ks.getY()+" "+ ks.getZ() + "\n";
        }

        return material;
    }


    /**
     *
     * @param nm name of material
     * @return true if material name is same as provided string.
     */
    public boolean hasName(String nm)
  {  return name.equals(nm);  } 

    /**
     *
     * @return name of the material.
     */
    public String getName() {
        return name;
    }

  

  // --------- set/get methods for colour info --------------

    /**
     *
     * @param val
     */
    public void setIllum(float val)
  {  illum = val;  }

    /**
     *
     * @return
     */
    public float getIllum()
  {  return illum;  }
  
    /**
     *
     * @param val
     */
    public void setD(float val)
  {  d = val;  }

    /**
     *
     * @return
     */
    public float getD()
  {  return d;  }


    /**
     *
     * @param val
     */
    public void setNs(float val)
  {  ns = val;  }

    /**
     *
     * @return
     */
    public float getNs()
  {  return ns;  }


    /**
     *
     * @param t
     */
    public void setKa(Vector3f t)
  {  ka = t;  }

    /**
     *
     * @return
     */
    public Vector3f getKa()
  {  return ka;  }


    /**
     *
     * @param t
     */
    public void setKd(Vector3f t)
  {  kd = t;  }

    /**
     *
     * @return
     */
    public Vector3f getKd()
  {  return kd;  }


    /**
     *
     * @param t
     */
    public void setKs(Vector3f t)
  {  ks = t;  }

    /**
     *
     * @return
     */
    public Vector3f getKs()
  {  return ks;  }


    /**
     *
     * @param gl
     */
    public void setMaterialColors(GL2 gl)
  // start rendering using this material's colour information
  {
    if (ka != null) {   // ambient color
      float[] colorKa = {  ka.getX(), ka.getY(),  ka.getZ(), 1.0f };
      gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, colorKa, 0);
    }
    if (kd != null) {  // diffuse color
      float[] colorKd = {  kd.getX(), kd.getY(), kd.getZ(), 1.0f };
      gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, colorKd, 0);
    }
    if (ks != null) {   // specular color
      float[] colorKs = { ks.getX(), ks.getY(), ks.getZ(), 1.0f };
      gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, colorKs, 0);
    }

    if (ns != 0.0f) {   // shininess
      gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, ns);
    }

    if (d != 1.0f) {   // alpha
      // not implemented
    }
  } // end of setMaterialColors()


  // --------- set/get methods for texture info --------------


    /**
     *
     * @param fnm
     * @param gl
     */
    public void loadTexture(String fnm, GL2 gl)
  {
    try {
      texFnm = fnm;
      texture = TextureIO.newTexture( new File(texFnm), false);
   //   texture.setTexParameteri(null, parameterName, value)
      texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    }
    catch(Exception e)
    { 
        System.out.println("Error loading texture " + texFnm + e);  
    }
  }  // end of loadTexture()


    /**
     *
     * @param t
     */
    public void setTexture(Texture t)
  {  texture = t;  }

    /**
     *
     * @return
     */
    public Texture getTexture()
  {  return texture;  }

    /**
     *
     * @param fnm
     */
    public void setTextureFile(String fnm)
  {
      texFnm = fnm;
  }

    /**
     *
     * @return
     */
    public String getTextureFile()
  {
      return texFnm;
  }
}  // end of Material class
