package cz.fidentis.merging.storing;

import cz.fidentis.merging.mesh.MeshFace;
import java.util.Objects;

/**
 *
 * @author matej
 */
public class PointIndexWithFace {

    private final Integer meshPointIndex;
    private final MeshFace textureOwner;

    public PointIndexWithFace(Integer meshPointIndex, MeshFace textureOwner) {
        this.meshPointIndex = meshPointIndex;
        this.textureOwner = textureOwner;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.meshPointIndex);
        hash = 31 * hash + Objects.hashCode(this.textureOwner);
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
        final PointIndexWithFace other = (PointIndexWithFace) obj;
        if (!Objects.equals(this.meshPointIndex, other.meshPointIndex)) {
            return false;
        }
        return Objects.equals(this.textureOwner, other.textureOwner);
    }

}
