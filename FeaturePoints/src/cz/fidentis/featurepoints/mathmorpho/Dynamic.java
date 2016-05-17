package cz.fidentis.featurepoints.mathmorpho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Galvanizze
 */
public class Dynamic extends MorphoOperator {

    // Pozor, v pripade, ze bude hodnota kroku prilis nizka
    // mozu sa stracat dolezite extremy - pretoze kluce
    // (hodnoty dynamik) budu rovnake
    // Resp. porozmyslat nad zmenou struktury na ukladanie
    // zoradenych dynamik! (aktualne TreeMap)
    private static final double STEP_COUNT = 100.0;

    private double[] dynamics;
    private Map<Double, Integer> sortedMapDynamics;
    private List<Integer> sortedDynamics;

    public Dynamic(double[] signal) {
        this(signal, false);
    }

    public Dynamic(double[] signal, boolean normalize) {
        super(signal, normalize);
        init();
    }

    public double[] dynMax() {
        return dynamics(DynamicType.max, autoHStep(), Integer.MAX_VALUE);
    }

    public double[] dynMax(double hStep) {
        return dynamics(DynamicType.max, hStep, Integer.MAX_VALUE);
    }

    public double[] dynMax(int count) {
        return dynamics(DynamicType.max, autoHStep(), count);
    }

    public double[] dynMax(double hStep, int count) {
        return dynamics(DynamicType.max, hStep, count);
    }

    public double[] dynMin() {
        return dynamics(DynamicType.min, autoHStep(), Integer.MAX_VALUE);
    }

    public double[] dynMin(double hStep) {
        return dynamics(DynamicType.min, hStep, Integer.MAX_VALUE);
    }

    public double[] dynMin(int count) {
        return dynamics(DynamicType.min, autoHStep(), count);
    }

    public double[] dynMin(double hStep, int count) {
        return dynamics(DynamicType.min, hStep, count);
    }

    private double[] dynamics(DynamicType type, double hStep, int count) {
        // nastavit najvyssiu dynamiku funkcie
        double h = max - min;

        // inicializacia pred samotnym vypoctom
        init();

        double[] hCon;
        HExtrema hExtrema = new HExtrema(signal);

        do {
            if (type == DynamicType.max) {
                hCon = hExtrema.hConvexThreshold(h);
            } else {
                hCon = hExtrema.hConcaveThreshold(h);
            }

            setHdynamics(hCon, count);

            h -= hStep;
        } while (h >= min); // (h >= 0);

        return dynamics;
    }

    private void setHdynamics(double[] hCon, int count) {
        // funkcia je v hranicnych bodoch 0, preto ich ignorujem
        // 0 hranicne hodnoty su sposobene vypoctom zakrivenia
        // chcelo by to upravit vseobecnejsie, aby sa napr.
        // nepracovalo s NaN hodnotami, resp. hranicnymu hodnotami 
        // a tuto moznost nastavovat uz v predkovi MorphoOperator
//        for (int i = 0; i < hCon.length; i++) {
        for (int i = 1; i < hCon.length - 1; i++) {
            // hodnotu pridat len ak uz neexistuje (s vacsou dynamikou)
            if (dynamics[i] == 0 && hCon[i] != 0) {
                // obmedzit pocet hodnot na parameter
                if (sortedMapDynamics.size() < count) {
                    dynamics[i] = hCon[i];

                    sortedMapDynamics.put(hCon[i], i);
                    sortedDynamics.add(i);
                }
            }
        }
    }

    private double autoHStep() {
        return Math.abs(max - min) / STEP_COUNT;
    }

    private void init() {
        dynamics = new double[signal.length];
        sortedMapDynamics = new TreeMap<>();
        sortedDynamics = new ArrayList<>();
    }

    public double[] getDynamics() {
        return dynamics;
    }

    public Map<Double, Integer> getSortedMapDynamics() {
        return sortedMapDynamics;
    }

    public List<Integer> getSortedDynamics() {
        Collections.sort(sortedDynamics);
        return sortedDynamics;
    }

    private enum DynamicType {
        max,
        min;
    }

}
