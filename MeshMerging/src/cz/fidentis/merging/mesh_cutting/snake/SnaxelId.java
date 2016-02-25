package cz.fidentis.merging.mesh_cutting.snake;

import java.util.Objects;

/**
 *
 * @author matej
 */
public class SnaxelId {

    private final AbstractSnaxel one;
    private final AbstractSnaxel two;
    private final int hash;

    public SnaxelId(AbstractSnaxel one, AbstractSnaxel two) {
        this.one = one;
        this.two = two;
        hash = 7 + Objects.hashCode(this.one) + Objects.hashCode(this.two);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SnaxelId other = (SnaxelId) obj;
        if (one.equals(other.one) && two.equals(other.two)) {
            return true;
        }
        return one.equals(other.two) && two.equals(other.one);
    }

}
