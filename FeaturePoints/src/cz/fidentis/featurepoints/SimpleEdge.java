package cz.fidentis.featurepoints;

/**
 *
 * @author Marek Galvanek
 *
 * Class represents edge with two points
 */
public class SimpleEdge {

    private int p1, p2;

    public SimpleEdge(int p1, int p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void setPoints(int p1, int p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getFirstPoint() {
        return p1;
    }

    public int getSecondPoint() {
        return p2;
    }

    @Override
    public int hashCode() {
        int small, big;
        if (p1 < p2) {
            small = p1;
            big = p2;
        } else {
            small = p2;
            big = p1;
        }

        return 7919 * small + 5171 * big;
    }

    @Override
    public boolean equals(Object obj) {

        final SimpleEdge other = (SimpleEdge) obj;
        if ((obj != null) && (getClass() == obj.getClass())
                && (((this.p1 == other.p1) && (this.p2 == other.p2))
                || ((this.p1 == other.p2) && (this.p2 == other.p1)))) {
            return true;
        }
        return false;
    }
}
