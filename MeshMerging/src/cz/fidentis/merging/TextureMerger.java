/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging;

import cz.fidentis.model.Faces;
import cz.fidentis.model.Material;
import cz.fidentis.model.Model;
import cz.fidentis.utils.FileUtils;
import cz.fidentis.utils.MathUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import ij.plugin.filter.RankFilters;
import java.awt.Image;

/**
 * Used to update 3D models with multiple materials to only have one such material,
 * which will use single texture image.
 * @author Marek Zuzi
 */
public class TextureMerger {
    private final Model model;
    
    public TextureMerger(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
    
    /**
     * Merges all materials in specified model to a single material and updates
     * model accordingly.
     * @throws IOException if any IO exception occurs during loading and saving
     * texture images.
     */
    public void mergeModel() throws IOException {
        ArrayList<Material> materials = model.getMatrials().getMatrials();
        
        Material mat0 = materials.get(0);
        Color backColor = null;
        Material mergedMaterial = new Material("mergedMaterial");
        if(mat0.getTextureFile() == null) {
            Vector3f defaultColor = mat0.getKd();
            backColor = new Color(defaultColor.x, defaultColor.y, defaultColor.z);
            mergedMaterial.setKa(new Vector3f(0.55f, 0.55f, 0.55f));
            mergedMaterial.setKd(new Vector3f(0.55f, 0.55f, 0.55f));
            mergedMaterial.setKs(new Vector3f(0.0f, 0.0f, 0.0f));
            mergedMaterial.setNs(30);
            mergedMaterial.setD(1);
            mergedMaterial.setIllum(2);
        } else {
            BufferedImage tex = ImageIO.read(new File(mat0.getTextureFile()));
            backColor = getMeanColor(tex);
            mergedMaterial.setKa(mat0.getKa());
            mergedMaterial.setKd(mat0.getKd());
            mergedMaterial.setKs(mat0.getKs());
            mergedMaterial.setNs(mat0.getNs());
            mergedMaterial.setD(mat0.getD());
            mergedMaterial.setIllum(mat0.getIllum());
        }
        
        HashMap<String, Material> matsByName = new HashMap<>(materials.size());
        for (Material mat : materials) {
            matsByName.put(mat.getName(), mat);
        }
        HashMap<Material, ArrayList<Integer>> parts = new HashMap<>(materials.size());
        Faces faces = model.getFaces();
        
        // select which face belongs to which material
        Material currentMaterial = null;
        for(int i=0;i<faces.getNumFaces();i++) {
            if(faces.findMaterial(i) != null) {
                currentMaterial = matsByName.get(faces.findMaterial(i));
                if(currentMaterial.getTextureFile() != null) faces.addMaterialUse(i, mergedMaterial.getName());
            }
            if(!parts.containsKey(currentMaterial)) {
                parts.put(currentMaterial, new ArrayList<Integer>());
            }
            ArrayList<Integer> idxList = parts.get(currentMaterial);
            idxList.add(i);
        }
        
        int resultW = 0;
        int resultH = 0;
        ArrayList<MergeInfo> texturesToMerge = new ArrayList<>();
        for(Material mat : parts.keySet()) {
            if(mat.getTextureFile() != null) {
                materials.remove(mat);
                BufferedImage textureImage = ImageIO.read(new File(mat.getTextureFile()));
                texturesToMerge.add(new MergeInfo(mat, textureImage));
                resultW += textureImage.getWidth();
                resultH = Math.max(resultH, textureImage.getHeight());
            }
        }
        int currentX = 0;
        BufferedImage result = new BufferedImage(resultW, resultH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        for(MergeInfo info : texturesToMerge) {
            BufferedImage mask = new BufferedImage(info.image.getWidth(), info.image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mask.createGraphics();
            
            HashSet<Integer> visited = new HashSet<>();
            for (Integer mat11 : parts.get(info.material)) {
                int[] index = faces.getFaceTexIdxs(mat11);
                drawPoly(info.image, g2, index);
                for(int j=0;j<index.length;j++) {
                    int vtIdx = index[j];
                    if(visited.contains(vtIdx)) continue;
                    visited.add(vtIdx);
                    
                    Vector3f vt = model.getTexCoords().get(vtIdx-1);
                    recompute(info.image, result, currentX, 0, vt);
                }
            }
            g2.dispose();
            //if(info.material != mat0) {
                ImagePlus maskOrigIj = new ImagePlus("maskOrig", mask);
                ImagePlus maskIj = maskOrigIj.duplicate();
                ImageProcessor processor = maskIj.getProcessor();
                RankFilters filter = new RankFilters();
                double size = Math.min(info.image.getWidth(), info.image.getHeight()) * 0.03333;
                filter.rank(processor, size, RankFilters.MIN);
                ImageCalculator calc = new ImageCalculator();
                ImagePlus contour = calc.run("Subtract create", maskOrigIj, maskIj);
                adjustColor(info.image, contour.getBufferedImage(), backColor);

                processor.blurGaussian(size);
                mask = maskIj.getBufferedImage();
                doBorders(info.image, mask, backColor);
            //}

            g.drawImage(info.image, currentX, 0, null);
            currentX += info.image.getWidth();
        }
        g.dispose();
        
        // save result texture and modify original model materials
        File textureFile = new File(FileUtils.instance().getTempDirectoryPath() + File.separator + "texture.jpg");
        ImageIO.write(resizeToFit(result, 2048, 2048), "jpg", textureFile);
        mergedMaterial.setTextureFile(textureFile.getAbsolutePath());
        model.getMatrials().getMatrials().add(mergedMaterial);
    }
    
    /**
     * Gets mean color of given image by simply averaging all pixel colors.
     * @param img input image.
     * @return mean color of input image.
     */
    private Color getMeanColor(BufferedImage img) {
        long sumR = 0, sumG = 0, sumB = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color px = new Color(img.getRGB(x, y));
                sumR += px.getRed();
                sumG += px.getGreen();
                sumB += px.getBlue();
            }
        }
        int pixels = img.getWidth() * img.getHeight();
        return new Color((int)(sumR / pixels), (int)(sumG / pixels), (int)(sumB / pixels));
    }
    
    /**
     * Gets mean color of portion of input image where mask is nonzero.
     * @param img input image
     * @param mask mask defining which pixels should be taken in account
     * @return average color over region given by mask
     */
    private Color getMeanColor(BufferedImage img, BufferedImage mask) {
        long sumR = 0, sumG = 0, sumB = 0;
        int count = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color maskPx = new Color(mask.getRGB(x, y));
                if(maskPx.getRed() > 0) {
                    Color px = new Color(img.getRGB(x, y));
                    sumR += px.getRed();
                    sumG += px.getGreen();
                    sumB += px.getBlue();
                    count++;
                }
            }
        }
        return new Color((int)(sumR / count), (int)(sumG / count), (int)(sumB / count));
    }
    
    /*private double getColorDifference(Color c1, Color c2) {
        int dR = c1.getRed() - c2.getRed();
        int dG = c1.getGreen() - c2.getGreen();
        int dB = c1.getBlue() - c2.getBlue();
        double distance = Math.sqrt(dR * dR + dG * dG + dB * dB);
        return distance;
    }*/
    
    /**
     * Adjusts color of given image to be closer to specified color by adding
     * difference of mean color and specified target color to each pixel.
     * @param img input image
     * @param contour mask to be used while getting mean color of input image
     * @param toColor target color
     */
    private void adjustColor(BufferedImage img, BufferedImage contour, Color toColor) {
        Color mean = getMeanColor(img, contour);
        int dr = mean.getRed() - toColor.getRed();
        int dg = mean.getGreen() - toColor.getGreen();
        int db = mean.getBlue() - toColor.getBlue();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color px = new Color(img.getRGB(x, y));
                //double dist = getColorDifference(px, toColor);
                int newR = (int) MathUtils.instance().clamp(px.getRed()-dr, 0, 255);
                int newG = (int) MathUtils.instance().clamp(px.getGreen()-dg, 0, 255);
                int newB = (int) MathUtils.instance().clamp(px.getBlue()-db, 0, 255);
                Color changed = new Color(newR, newG, newB);
                img.setRGB(x, y, changed.getRGB());
            }
        }
    }
    

    
    /**
     * Resizes input image to have maximum dimensions of maxWidth x maxHeight.
     * Keeps aspect ratio of image. If image already fits maximum dimensions no
     * change is done and original image is returned.
     * @param img input image to resize
     * @param maxWidth maximum width of resized image
     * @param maxHeight maximum height of resized image
     * @return image that fits into maxWidth x maxHeight dimensions
     */
    private BufferedImage resizeToFit(BufferedImage img, int maxWidth, int maxHeight) {
        double w = img.getWidth();
        double h = img.getHeight();
        if(w < maxWidth && h < maxHeight) return img;
        
        double ratio = Math.min(maxWidth / w, maxHeight / h);
        int newW = (int)Math.floor(w * ratio);
        int newH = (int)Math.floor(h * ratio);
        Image temp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        
        BufferedImage result = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = result.createGraphics();
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        
        return result;
    }
    
    /**
     * Recomputes original texture coordinates from original image to target image,
     * assuming that original image is drawn in target image on coordinates x0 y0.
     * @param origImg original texture
     * @param targetImg image containing original texture on coordinates x0, y0
     * @param x0 x coordinate of top-left corner of original image in target image
     * @param y0 y coordinate of top-left corner of original image in target image
     * @param origCoord texture coordinates in original image. Will be updated
     * with new values pointing to according place in target image.
     */
    private void recompute(BufferedImage origImg, BufferedImage targetImg, int x0, int y0, Vector3f origCoord) {
        float x = origCoord.x;
        int srcW = origImg.getWidth();
        int dstW = targetImg.getWidth();
        x = (x*srcW + x0) / dstW;

        float y = origCoord.y;
        int srcH = origImg.getHeight();
        int dstH = targetImg.getHeight();
        int diff = dstH-srcH-y0;
        y = (y*srcH + diff) / dstH;

        origCoord.setX(x);
        origCoord.setY(y);
    }
    
    /**
     * Draws given polygon of model to input image
     * @param image texture image
     * @param g used to draw image
     * @param vtInds texture coordinates of face
     */
    private void drawPoly(BufferedImage image, Graphics2D g, int[] vtInds) {
        Polygon p = new Polygon();
        for(int i=0;i<vtInds.length;i++) {
            Vector3f a = model.getTexCoords().get(vtInds[i]-1);
            p.addPoint((int)(a.getX()*image.getWidth()), (int)((1-a.getY())*image.getHeight()));
        }
        g.fillPolygon(p);
    }
    
    /**
     * 
     * @param img
     * @param mask
     * @param edgeColor 
     */
    private void doBorders(BufferedImage img, BufferedImage mask, Color edgeColor) {
        for(int i=0; i<img.getWidth();i++) {
            for(int j=0; j<img.getHeight(); j++) {
                Color maskColor = new Color(mask.getRGB(i, j));
                Color imgColor = new Color(img.getRGB(i, j));
                float amount = maskColor.getRed() / 255.0f;
                img.setRGB(i, j, blendColor(imgColor, edgeColor, amount).getRGB());
            }
        }
    }
    
    /**
     * Blends colors src and dst in specified ratio of 0 to 1
     * @param src source color
     * @param dst destination color
     * @param amount ratio of blending, should be between 0 and 1
     * @return linearly blended source and destination color. If ratio is 1,
     * output is source color.
     */
    private Color blendColor(Color src, Color dst, float amount) {
        int r = (int)(dst.getRed() * (1-amount) + src.getRed() * amount);
        int g = (int)(dst.getGreen() * (1-amount) + src.getGreen() * amount);
        int b = (int)(dst.getBlue() * (1-amount) + src.getBlue() * amount);
        return new Color(r, g, b);
    }
    
    private class MergeInfo {
        public Material material;
        public BufferedImage image;
        
        public MergeInfo(Material m, BufferedImage i) {
            material = m;
            image = i;
        }
    }
}
