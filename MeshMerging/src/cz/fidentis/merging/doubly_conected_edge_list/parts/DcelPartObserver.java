
package cz.fidentis.merging.doubly_conected_edge_list.parts;

import java.util.Collection;

/**
 *
 * @author matej
 * @param <T>
 */
public interface DcelPartObserver<T extends AbstractDcelPart> {
    void partWasReplace(T originalPart, Collection<T> newParts);
}
