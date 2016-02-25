package cz.fidentis.model;

/**
 *
 * @author mlobodas
 */
public final class MeshPoint {

    private final Coordinates posiotion;
    private final Coordinates normal;
    private final Coordinates color;

    public MeshPoint(final Coordinates at, final Coordinates normal,
            final Coordinates color) {

        this.posiotion = at;
        this.normal = normal;
        this.color = color;
    }

    public Coordinates getPosiotion() {
        return posiotion;
    }

    public Coordinates getNormal() {
        return normal;
    }

    public Coordinates getColor() {
        return color;
    }
}
