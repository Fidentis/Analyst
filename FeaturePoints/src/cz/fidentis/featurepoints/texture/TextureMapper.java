package cz.fidentis.featurepoints.texture;

import cz.fidentis.model.Faces;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 *
 * @author Galvanizze
 */
public class TextureMapper {

    private final ArrayList<Vector3f> verts;
    private final ArrayList<Vector3f> texCoords;
    private final Map<Integer, Integer> texVertMap;
    private final Map<Integer, Integer> vertTexMap;

    public TextureMapper(Model model) {
        this.verts = model.getVerts();
        this.texCoords = model.getTexCoords();
        
        this.texVertMap = new HashMap<>();
        this.vertTexMap = new HashMap<>();

        createMaps(model.getFaces());
    }
    
    public Vector3f getModelVertFromTextureCoord(Point p, Mat image) {
        return getModelTexIndex((int)p.x, (int)p.y, image.width(), image.height());
    }

    public Vector3f getModelTexIndex(int imageX, int imageY, int imageWidth, int imageHeight) {
        // TODO: zaokruhlit desatinne cisla na spravny pocet des. miest
        double texX = (imageX / (double) imageWidth);
        double texY = 1 - (imageY / (double) imageHeight);

        int nearestCoordIndex = 0;
        int indexCounter = 0;

        double minDistance = Double.POSITIVE_INFINITY;

        for (Vector3f texCoord : texCoords) {
            Point2d tex = new Point2d(texX, texY);
            Point2d texCoord2d = new Point2d(texCoord.x, texCoord.y);
            double distance = tex.distance(texCoord2d);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCoordIndex = indexCounter;
                if (distance == 0) {
                    break;
                }
            }
            indexCounter++;
        }

        return verts.get(texVertMap.get(nearestCoordIndex));
    }
    
    public Point getImagePoint(Point3d p) {
        float[] coords = getImageCoord(p);
        return new Point(coords[0], coords[1]);
    }

    public float[] getImageCoord(Point3d p) {

        int nearestIndex = 0;
        int indexCounter = 0;

        double minDistance = Double.POSITIVE_INFINITY;

        for (Vector3f vert : verts) {
            double distance = new Point3d(p).distance(new Point3d(vert));
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = indexCounter;
                if (distance == 0) {
                    break;
                }
            }
            indexCounter++;
        }

        Vector3f coordVector = texCoords.get(vertTexMap.get(nearestIndex));

        return new float[]{coordVector.x, coordVector.y};
    }

    public int[][] getMidlineImageCoords(List<Point3d> midlinePoints, int width, int height) {
        int[][] imageCoords = new int[midlinePoints.size()][2];

        int i = 0;
        for (Point3d p : midlinePoints) {
            float[] tmpCoords = getImageCoord(p);
            imageCoords[i][0] = (int) (tmpCoords[0] * width);
            imageCoords[i][1] = (int) (tmpCoords[1] * height);
            i++;
        }

        return imageCoords;
    }
    
    public int[][] getMidlineImageLineCoords(List<Point3d> midlinePoints, int width, int height, int offset) {
        int[][] imageCoords = new int[2][2];
        
        // najvyssi bod + offset
        float[] tmpCoords = getImageCoord(midlinePoints.get(offset));
        imageCoords[0][0] = (int) (tmpCoords[0] * width);
        imageCoords[0][1] = (int) (tmpCoords[1] * height);
        
        // najnizsi bod - offset
        tmpCoords = getImageCoord(midlinePoints.get(midlinePoints.size() - offset));
        imageCoords[1][0] = (int) (tmpCoords[0] * width);
        imageCoords[1][1] = (int) (tmpCoords[1] * height);        
        
        return imageCoords;
    }

    private double distance2D(float x1, float x2, float y1, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void createMaps(Faces faces) {

        for (int i = 0; i < faces.getNumFaces(); i++) {
            int[] faceVertIdxs = faces.getFaceVertIdxs(i);
            int[] faceTexIdxs = faces.getFaceTexIdxs(i);

            for (int j = 0; j < faceVertIdxs.length; j++) {
                texVertMap.put(faceTexIdxs[j] - 1, faceVertIdxs[j] - 1);
                vertTexMap.put(faceVertIdxs[j] - 1, faceTexIdxs[j] - 1);
            }
        }
    }

}
