package cz.fidentis.merging.mesh;

import cz.fidentis.merging.file_parsing.MaterialBuilder;

/**
 *
 * @author matej
 */
public class Material {

    private final double[] ambient;
    private final String materialName;
    private final double[] diffuse;
    private final double[] specular;
    private final double disolveFactor;
    private final double illumination;
    private final double specularComponet;
    private final double refractionIndex;
    private final String textureName;
    private final double[] transmissionFilter;
    private final double[] emmision;

    public Material(MaterialBuilder builder) {
        String name = builder.getMaterialName();
        materialName = name == null ? "default" : name;
        ambient = builder.getAmbient();
        diffuse = builder.getDiffuse();
        specular = builder.getSpecular();
        disolveFactor = builder.getDisolveFactor();
        illumination = builder.getIllumination();
        specularComponet = builder.getSpecularComponet();
        refractionIndex = builder.getRefractionIndex();
        textureName = builder.getTextureName();
        transmissionFilter = builder.getTransmissionFilter();
        emmision = builder.getEmmision();
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

}
