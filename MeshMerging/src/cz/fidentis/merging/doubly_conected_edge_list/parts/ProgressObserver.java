/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.doubly_conected_edge_list.parts;

/**
 *
 * @author matej
 */
public interface ProgressObserver {

    void updateTotalUnits(int units);

    void updateProgress(String description, int currentUnits);

    void setMeasurement(StringBuilder measurement);

    String getMeasurement();

}
