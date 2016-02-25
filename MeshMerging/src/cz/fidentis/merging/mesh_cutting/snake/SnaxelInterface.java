/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.mesh_cutting.snake;

/**
 *
 * @author matej
 * @param <T>
 */
public interface SnaxelInterface<T extends SnaxelInterface> {

    T getPrevios();

    T getNext();
}
