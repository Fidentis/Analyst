/*package cz.fidentis.featurepoints.texture;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.face.detection.CLMDetectedFace;
import org.openimaj.image.processing.face.detection.CLMFaceDetector;
import org.openimaj.image.processing.face.util.CLMDetectedFaceRenderer;
import org.openimaj.image.processing.transform.AffineSimulation;

/**
 *
 * @author Galvanizze
 */
/*public class TextureAnalyzer {

    private FImage textureIntensityImage;
    private MBFImage textureRGBImage;
    
    public TextureAnalyzer(String path) {
        
    }
    
    private void loadImage(String path) {
        
    }
    
    private void obsoleteFPanalyzer(String path) {

        try {
            textureRGBImage = ImageUtilities.readMBF(new File(path));
            textureIntensityImage = ImageUtilities.readF(new File(path));
        } catch (IOException ex) {
             Logger.getLogger(TextureAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        textureIntensityImage = AffineSimulation.transformImage(textureIntensityImage, 1.6f, 1);
        textureRGBImage = AffineSimulation.transformImage(textureRGBImage, 1.6f, 1);

        // ine metody detekcie bodov
//        FacialKeypointExtractor extractor = new FacialKeypointExtractor();
//        FacialKeypoint[] haarFacialKeypoints = extractor.extractFacialKeypoints(textureIntensityImage);
//
//        FaceDetector<DetectedFace, FImage> haarDetector = new HaarCascadeDetector();
//        DetectedFace haarFace = haarDetector.detectFaces(textureIntensityImage).get(0);
//        FaceDetector<KEDetectedFace, FImage> fkeDetector = new FKEFaceDetector(2000);
//        KEDetectedFace fkeFace;
//        try {
//            fkeFace = fkeDetector.detectFaces(textureIntensityImage).get(0);
//        } catch (IndexOutOfBoundsException ex){
//            System.out.println("Face not found!");
//            return;
//        }
//        
//        FacialKeypoint[] fkeKeypoints = fkeFace.getKeypoints();
//        
//        List keypoints = new ArrayList<Point2dImpl>();
//        for (FacialKeypoint keypoint : fkeKeypoints) {
//            keypoints.add(keypoint.position);
//        }
//        
//        
//        Float[] color = {1.0f, 0.0f, 0.0f};
//        textureRGBImage.drawPoints(keypoints, color, 25);
//        
//  
        CLMFaceDetector clmDetector = new CLMFaceDetector();
        CLMDetectedFace clmFace = clmDetector.detectFaces(textureIntensityImage).get(0);

        CLMDetectedFaceRenderer renderer = new CLMDetectedFaceRenderer();
        renderer.drawDetectedFace(textureRGBImage, 6, clmFace);

        String newPath = path.replace(".jpg", "_test.jpg");
        File file = new File(newPath);
        try {
            ImageUtilities.write(textureRGBImage, file);
        } catch (IOException ex) {
            Logger.getLogger(TextureAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Texture processing done!");

    }

}*/
