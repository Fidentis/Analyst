package cz.fidentis.merging.doubly_conected_edge_list.parts;

/**
 *
 * @author matej
 */
public class MergingProgres {

    private final int toProcess;
    private int current;
    private String prefix;
    private final int numberOfStages;
    private int currentStage;
    private ProgressObserver observer;
    private StringBuilder measurement = new StringBuilder();
    private long start;

    public MergingProgres(int toProcess, int numberOfStages) {
        this.toProcess = toProcess;
        this.current = 0;
        this.prefix = "";
        this.numberOfStages = numberOfStages;
        this.currentStage = 0;
    }

    public void processingNext() {
        current++;
        currentStage = 0;
        prefix = String.format("Procesing part %d of %d ", current, toProcess);
        if (observer != null) {
            observer.updateProgress(prefix, getCurrentUnits());
        }
    }

    public void nextStage(String description) {
        if (observer != null) {
            observer.updateProgress(prefix + description, getCurrentUnits());
        }
        currentStage++;
    }

    private int getCurrentUnits() {
        return (current - 1) * numberOfStages + currentStage;
    }

    public void setObserver(ProgressObserver o) {
        observer = o;
        observer.updateTotalUnits(numberOfStages * toProcess);
        observer.setMeasurement(measurement);
    }

    public void startMeasurement(String decription) {
        start = System.currentTimeMillis();
        measurement.append(decription);
        measurement.append(' ');
    }

    public void finishMeasurement() {
        long timeSpawn = System.currentTimeMillis() - start;
        measurement.append(String.valueOf(timeSpawn));
        measurement.append('\n');
    }

}
