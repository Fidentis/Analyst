package cz.fidentis.featurepoints.curvature;

/*
 Copyright 2011 Milian Wolff <mail@milianw.de>
	
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of 
 the License, or (at your option) any later version.
	
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
	
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.Color;
import java.util.Set;
import jv.geom.PgElementSet;

/**
 *
 * Class observe computation of curvatures
 *
 */
public class CurvatureControl {

    private boolean hasNegative;
    private Set<Integer> boundaryVertices;
    private PgElementSet geometry;

    public CurvatureControl(Set<Integer> boundaryVertices, PgElementSet elementSet) {
        this.geometry = elementSet;
        this.boundaryVertices = boundaryVertices;
    }

    private double absMax(double[] l) {
        assert l.length > 0;
        double max = l[0];
        for (int i = 1; i < l.length; ++i) {
            max = Math.max(max, Math.abs(l[i]));
        }
        return max;
    }

    /**
     * given curvature values
     *
     * @param curvature, set vertex colors of
     * @param geometry, by mapping the curvature values to the HSV color space.
     * This is done by normalizing the (possibly negative values, see
     * @hasNegative) to the range [0-1] by first finding the absolute maximum of
     * the curvature values, then dividing each entry by that value.
     *
     * It turns out that this is a not-so-good color scheme, see
     * setColorsFromDeviation for a better alternative.
     *
     * @param geometry
     * @param curvature
     * @param hasNegative must be true if the values passed can be negative,
     * e.g. for gaussian or minimum curvatures
     */
    public PgElementSet setColorsFromMaxAbs(double[] curvature, boolean hasNegative) {
        assert curvature.length == geometry.getNumVertices();
        // find maximum
        double max = absMax(curvature);
        assert max > 0;
        // assign colors
        for (int i = 0; i < curvature.length; ++i) {
            double mean = curvature[i];
            assert mean <= max;
            assert mean < Double.POSITIVE_INFINITY;
            float normalized = (float) (mean / max);
            if (hasNegative) {
                assert normalized >= -1;
                assert normalized <= 1;
                normalized = (normalized + 1.0f) / 2.0f;
            } else {
                assert mean >= 0;
            }
            assert normalized >= 0;
            assert normalized <= 1;
            geometry.setVertexColor(i,
                    Color.getHSBColor(normalized, 1.0f, 1.0f));
        }
        System.out.println("max curvature: " + max);
        return geometry;
    }

    /**
     * Set vertex colors of
     *
     * @param geometry based on associated entry in
     * @param curvature. Again, we get colors from the HSV color space, but this
     * time we normalize the values into the range [0-1] by calculating the
     * standard deviation of the calculated curvatures. Then we interpolate the
     * values to our desired range by capping the value at the tripled standard
     * deviation interval around the mean value. Values above or below are set
     * the hue = 0 = 1 = red.
     *
     * @param geometry
     * @param curvature
     * @param hasNegative
     */
    public PgElementSet setColorsFromDeviation(double[] curvature) {
        assert curvature.length == geometry.getNumVertices();
        assert curvature.length > 1;
        double mean = 0;
        for (int i = 0; i < curvature.length; i++/*double c : curvature*/) {
            if (!boundaryVertices.contains(i)) {
                mean += curvature[i];
            }
        }

        mean /= (curvature.length - boundaryVertices.size());
        // now calculate variance
        double variance = 0;
        for (int i = 0; i < curvature.length; i++/*double c : curvature*/) {
            if (!boundaryVertices.contains(i)) {
                variance += Math.pow(curvature[i] - mean, 2);
            }
        }

        variance /= curvature.length - boundaryVertices.size() - 1;
        double standardDeviation = Math.sqrt(variance);
        // now set colors based on deviation:
        // zero deviation is hue of 0.5
        // deviation is normalized to +- 0.5 in the tripled standard deviation interval
        // anything higher just gets the maximum hue of 1 or 0 (both are red)
        for (int i = 0; i < curvature.length; ++i) {
            if (!boundaryVertices.contains(i)) {
                double c = curvature[i];
                double deviation = c - mean;
                float hue = ((float) deviation / (3.0f * (float) standardDeviation) + 1.0f) / 2.0f;
                if (hue > 1) {
                    hue = 1;
                } else if (hue < 0) {
                    hue = 0;
                }
                geometry.setVertexColor(i, Color.getHSBColor(hue, 1.0f, 1.0f));
            } else {
                geometry.setVertexColor(i, new Color(1.0f, 1.0f, 1.0f));
            }
        }

        return geometry;
    }
    // cache
    private Curvature m_lastCurvature;

    public void setCurvatureColors(CurvatureType type, ColorType colorType) {
        assert type == CurvatureType.Mean
                || type == CurvatureType.Gaussian
                || type == CurvatureType.Minimum
                || type == CurvatureType.Maximum;

        boolean wasCached = true;
        if (m_lastCurvature == null || m_lastCurvature.geometry() != geometry) {
            m_lastCurvature = new Curvature(geometry);
        }
        System.out.println("setting colors: " + colorType + ", " + type);
        Curvature.VertexCurvature[] curvature = m_lastCurvature.curvatures();
        double values[] = new double[curvature.length];
        double totalGaussian = 0;
        for (int i = 0; i < curvature.length; ++i) {
            Curvature.VertexCurvature c = curvature[i];
            if (c == null) {
                values[i] = 0;
                continue;
            }
            assert c.area > 0;
            if (type == CurvatureType.Mean) {
                values[i] = c.meanCurvature();
                assert values[i] >= 0;
            } else if (type == CurvatureType.Minimum) {
                values[i] = c.minimumCurvature();
            } else if (type == CurvatureType.Maximum) {
                values[i] = c.maximumCurvature();
                assert values[i] >= 0;
            } else {
                assert type == CurvatureType.Gaussian;
                values[i] = c.gaussianCurvature();
            }
            totalGaussian += Math.toRadians(c.gaussian);

            //System.out.println(i + ".value: " + values[i]);
        }

//        findBoundaryVertices(values, AutoThresholder.Method.Default);

        hasNegative = type == CurvatureType.Gaussian || type == CurvatureType.Minimum;
        switch (colorType) {
            case Deviation:
                setColorsFromDeviation(values);
                break;
            case Maximum:
                setColorsFromMaxAbs(values, hasNegative);
                break;
            case NoColors:
                break;
        }
        System.out.println("sum of theta_ij: " + totalGaussian);
        System.out.println("divided by 2pi: " + (totalGaussian / (2.0d * Math.PI)));
        System.out.println("vertices: " + geometry.getNumVertices());
        System.out.println("faces: " + geometry.getNumElements());
        System.out.println("edges: " + geometry.makeEdgeStars().length);
        System.out.println("total angle deficit:" + (geometry.getNumVertices() - totalGaussian / (2.0d * Math.PI)));
        System.out.println("euler characteristic:" + geometry.getEulerCharacteristic());
        System.out.println("done");
    }

    public double[] computeCurvature(CurvatureType type) {
        assert type == CurvatureType.Mean
                || type == CurvatureType.Gaussian
                || type == CurvatureType.Minimum
                || type == CurvatureType.Maximum;

        boolean wasCached = true;
        if (m_lastCurvature == null || m_lastCurvature.geometry() != geometry) {
            m_lastCurvature = new Curvature(geometry);
        }
        System.out.println("computing curvature: " + type);
        Curvature.VertexCurvature[] curvature = m_lastCurvature.curvatures();
        double values[] = new double[curvature.length];
        double totalGaussian = 0;
        for (int i = 0; i < curvature.length; ++i) {
            Curvature.VertexCurvature c = curvature[i];
            if (c == null) {
                values[i] = 0;
                continue;
            }
            assert c.area > 0;
            if (type == CurvatureType.Mean) {
                values[i] = c.meanCurvature();
                assert values[i] >= 0;
            } else if (type == CurvatureType.Minimum) {
                values[i] = c.minimumCurvature();
            } else if (type == CurvatureType.Maximum) {
                values[i] = c.maximumCurvature();
                assert values[i] >= 0;
            } else {
                assert type == CurvatureType.Gaussian;
                values[i] = c.gaussianCurvature();
            }
            totalGaussian += Math.toRadians(c.gaussian);

            //System.out.println(i + ".value: " + values[i]);
        }
        hasNegative = type == CurvatureType.Gaussian || type == CurvatureType.Minimum;

        System.out.println("sum of theta_ij: " + totalGaussian);
        System.out.println("divided by 2pi: " + (totalGaussian / (2.0d * Math.PI)));
        System.out.println("vertices: " + geometry.getNumVertices());
        System.out.println("faces: " + geometry.getNumElements());
        System.out.println("edges: " + geometry.makeEdgeStars().length);
        System.out.println("total angle deficit:" + (geometry.getNumVertices() - totalGaussian / (2.0d * Math.PI)));
        System.out.println("euler characteristic:" + geometry.getEulerCharacteristic());
        System.out.println("done");

        return values;

    }

    public void clearCurvature() {
        geometry.removeElementColors();
        geometry.removeVertexColors();
        geometry.showElementFromVertexColors(false);
        geometry.removeAllVectorFields();
    }

    public boolean gethasNegative() {
        return hasNegative;
    }

    public void setGeometry(PgElementSet geometry) {
        this.geometry = geometry;
    }

    public PgElementSet getGeometry() {
        return geometry;
    }
    /**
     * update view after settings changed, i.e. new color scheme or similar
     */
//    private void updateView() {
//        PgElementSet geometry = currentGeometry();
//        if (geometry == null) {
//            return;
//        }
//        clearCurvature(geometry);
//        switch (m_curvatureType) {
//            case Gaussian:
//            case Mean:
//            case Minimum:
//            case Maximum:
//                setCurvatureColors(geometry, m_curvatureType, m_colorType);
//                break;
//        }
//    }
}