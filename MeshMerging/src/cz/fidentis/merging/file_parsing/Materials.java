package cz.fidentis.merging.file_parsing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author matej
 */
public class Materials implements Iterable<Map.Entry<String, MaterialBuilder>> {

    private final HashMap<String, MaterialBuilder> materialBuilders = new HashMap<>();
    private MaterialBuilder current;

    /**
     *
     * @param color
     */
    public final void setAmbientColor(final double[] color) {
        current.setAmbientColor(color);
    }

    /**
     *
     * @param color
     */
    public final void setDiffuseColor(final double[] color) {
        current.setDiffuseColor(color);
    }

    /**
     *
     * @param color
     */
    public final void setSpecularColor(final double[] color) {
        current.setSpecularColor(color);
    }

    /**
     *
     * @param value
     */
    public final void setDisolveFactor(final double value) {
        current.setDisolveFactor(value);
    }

    /**
     *
     * @param value
     */
    public final void setIllumination(final double value) {
        current.setIllumination(value);
    }

    /**
     *
     * @param value
     */
    public final void setPhongSpecularComponent(final double value) {
        current.setPhongSpecularComponent(value);
    }

    /**
     *
     * @param value
     */
    public final void setRefractioIndex(final double value) {
        current.setRefractioIndex(value);
    }

    /**
     *
     * @param name
     */
    public final void setTextureName(final String name) {
        current.setTextureName(name);
    }

    /**
     *
     * @param filter
     */
    public final void setTransmisionFilter(final double[] filter) {
        current.setTransmisionFilter(filter);
    }

    public void addNewMaterial(String value) {
        current = new MaterialBuilder(value);
        materialBuilders.put(value, current);
    }

    public void setEmmision(final double[] points) {
        current.setEmmision(points);
    }

    @Override
    public Iterator<Map.Entry<String, MaterialBuilder>> iterator() {
        return materialBuilders.entrySet().iterator();
    }

}
