package cz.fidentis.merging.file_parsing;

import cz.fidentis.model.Material;
import javax.vecmath.Vector3f;

/**
 * @author Matej Lobodáš <lobodas.m at gmail.com>
 */
public class MaterialBuilder {

    private double[] ambient;
    private final String materialName;
    private double[] diffuse;
    private double[] specular;
    private double disolveFactor;
    private double illumination;
    private double specularComponet;
    private double refractionIndex;
    private String textureName;
    private double[] transmissionFilter;
    private double[] emmision;

    public MaterialBuilder(Material material) {
        materialName = material.getName();
        diffuse = convert(material.getKd());
        specular = convert(material.getKs());
        ambient = convert(material.getKa());
        textureName = material.getTextureFile();
        illumination = material.getIllum();
        disolveFactor = material.getD();
    }

    private double[] convert(Vector3f vec) {
        if(vec == null) return null;
        return new double[]{vec.x, vec.y, vec.z};
    }

    public double[] getEmmision() {
        return emmision;
    }

    public double[] getAmbient() {
        return ambient;
    }

    public String getMaterialName() {
        return materialName;
    }

    public double[] getDiffuse() {
        return diffuse;
    }

    public double[] getSpecular() {
        return specular;
    }

    public double getDisolveFactor() {
        return disolveFactor;
    }

    public double getIllumination() {
        return illumination;
    }

    public double getSpecularComponet() {
        return specularComponet;
    }

    public double getRefractionIndex() {
        return refractionIndex;
    }

    public String getTextureName() {
        return textureName;
    }

    public double[] getTransmissionFilter() {
        return transmissionFilter;
    }

    /**
     *
     * @param name
     */
    public MaterialBuilder(String name) {
        materialName = name;
    }

    /**
     *
     * @param color
     */
    public final void setAmbientColor(final double[] color) {
        ambient = color.clone();
    }

    /**
     *
     * @param color
     */
    public final void setDiffuseColor(final double[] color) {
        diffuse = color.clone();
    }

    /**
     *
     * @param color
     */
    public final void setSpecularColor(final double[] color) {
        specular = color.clone();
    }

    /**
     *
     * @param value
     */
    public final void setDisolveFactor(final double value) {
        disolveFactor = value;
    }

    /**
     *
     * @param value
     */
    public final void setIllumination(final double value) {
        illumination = value;
    }

    /**
     *
     * @param value
     */
    public final void setPhongSpecularComponent(final double value) {
        specularComponet = value;
    }

    /**
     *
     * @param value
     */
    public final void setRefractioIndex(final double value) {
        refractionIndex = value;
    }

    /**
     *
     * @param name
     */
    public final void setTextureName(final String name) {
        textureName = name;
    }

    /**
     *
     * @param filter
     */
    public final void setTransmisionFilter(final double[] filter) {
        transmissionFilter = filter.clone();
    }

    public void setEmmision(final double[] emmis) {
        emmision = emmis.clone();
    }

}
