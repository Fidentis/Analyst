package cz.fidentis.featurepoints.texture;

import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.featurepoints.FacialPointType;
import cz.fidentis.featurepoints.FpModel;
import cz.fidentis.model.Model;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Galvanizze
 */
public class ImageAnalyzer {

    private static final int EYE_WIDTH = 180;
    private static final int EYE_HEIGHT = 60;
    private static final int NOSE_WIDTH = 100;
    private static final int NOSE_HEIGHT = 100;

    private double resizeRatio;
    
    private HaarCascade haarDetector;
    private final Mat originalImage;
    private Mat workingImage;
    private Mat pupilaHoughCircles;
    private final ImageViewer imageViewer;
    private TextureMapper textureMapper;

    private FpModel objFPmodel;
    private List<TextureFP> textureFPs;
    // Len pomocne premenne pre body, su aj v zozname listov
    private TextureFP pupilaLFP;
    private TextureFP pupilaRFP;

    private Rect leftEye;
    private Rect rightEye;
    private Rect nose;
    private Rect mouth;

    private boolean doRotation;

    public ImageAnalyzer(Model model) {
        // zaujimava je len prva textura
        this(model.getMatrials().getMatrials().get(0).getTextureFile());
        this.textureMapper = new TextureMapper(model);
    }

    public ImageAnalyzer(String filePath) {
        // this(Imgcodecs.imread(filePath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE));
        this(Imgcodecs.imread(filePath));
    }

    public ImageAnalyzer(Mat image) {
        this.originalImage = image;
        this.workingImage = new Mat();
        this.originalImage.copyTo(workingImage);
        this.imageViewer = new ImageViewer();
        this.pupilaHoughCircles = new Mat();
        this.textureFPs = new ArrayList<>();

        this.doRotation = true;
    }

    public Mat analyze() throws NullPointerException {
        // pokracovat iba ak je obrazok validny
        if (!isValid()) {
            return null;
        }

        // Zmensenie snimky na defaultnu velkost - pracujeme s kopiou
        // original ostava nezmeneny
        // Pre potreby prepoctu bodov na povodny obrazok si musime ponechat
        // aj pomer zmeny velkosti obrazka
        resizeRatio = OCVutils.resize(workingImage);

        // Textury su otocene o 90° doprava, preto je potrebne otocit ich naspat
        if (doRotation) {
            OCVutils.rotate_90n(workingImage, -90);
        }

        // Detekcia bodov Pupila na oboch ociach
        findHoughCircles();

        // Vytvorenie Haar detectora
        haarDetector = new HaarCascade(workingImage);
        // Segmentacia regionov pomocou Haar detektorov
//        leftEye = haarDetector.detectLeftEye();
//        rightEye = haarDetector.detectRightEye();
//        // haarDetector.detectEyes();
        nose = haarDetector.detectNose();
        mouth = haarDetector.detectMouth();
        // Detekcia bodov
        pointsDetection();

        // Vykreslenie vsetkych objektov
        drawObjects(workingImage);

        return workingImage;
    }

    public void pointsDetection() {

        // Vytvorenie kopie na spracovanie
        Mat imageTmp = new Mat();
        workingImage.copyTo(imageTmp);

        // Uprava obrazka
        Imgproc.cvtColor(imageTmp, imageTmp, Imgproc.COLOR_BGR2GRAY);
        OCVutils.bilateralFilter(imageTmp);
        OCVutils.laplacian(imageTmp, 5); // parameter - aperture
        OCVutils.threshold(imageTmp, 220); // parameter - threshold
        OCVutils.open(imageTmp, 1, Imgproc.CV_SHAPE_ELLIPSE);

        // Spracovanie oci
        // Body na ociach
        // if (leftEye != null) 
        // Namiesto regiona detekovaneho Haar detektorom pouzivame region
        // urceny podla stredu oka
        Rect leftEyeRegion = new Rect((int) pupilaLFP.x - (EYE_WIDTH / 2), (int) pupilaLFP.y - (EYE_HEIGHT / 2), EYE_WIDTH, EYE_HEIGHT);
        Point exL = getLeftPoint(imageTmp, leftEyeRegion);
        Point enL = getRightPoint(imageTmp, leftEyeRegion);
        textureFPs.add(new TextureFP(FacialPointType.EX_L, exL));
        textureFPs.add(new TextureFP(FacialPointType.EN_L, enL));

        // if (rightEye != null) {
        Rect rightEyeRegion = new Rect((int) pupilaRFP.x - (EYE_WIDTH / 2), (int) pupilaRFP.y - (EYE_HEIGHT / 2), EYE_WIDTH, EYE_HEIGHT);
        Point enR = getLeftPoint(imageTmp, rightEyeRegion);
        Point exR = getRightPoint(imageTmp, rightEyeRegion);
        textureFPs.add(new TextureFP(FacialPointType.EN_R, enR));
        textureFPs.add(new TextureFP(FacialPointType.EX_R, exR));

        // Region nosa
        // Pouzitie modelu docasne zakomentovane
        // Point pronasaleFP = getTextureFPfromModel(FacialPointType.PRN);
        // if (pronasaleFP != null) {

//            Rect noseRegion = new Rect((int) pronasaleFP.x - (NOSE_WIDTH / 2), (int) pronasaleFP.y - (NOSE_HEIGHT / 2), NOSE_WIDTH, NOSE_HEIGHT);

            // Body na nose
            nose = haarDetector.detectNose();
          if (nose != null) {
            Point alL = getLeftPoint(imageTmp, nose);
            Point alR = getRightPoint(imageTmp, nose);

//          textureFPs.add(new TextureFP(FacialPointType.AL_L, alL));
//          textureFPs.add(new TextureFP(FacialPointType.AL_R, alR));
            // Body musia byt symetricke, preto y suradnicu vypocitat ako priemer bodov
            double y = (alL.y + alR.y) / 2;

            textureFPs.add(new TextureFP(FacialPointType.AL_L, new Point(alL.x, y)));
            textureFPs.add(new TextureFP(FacialPointType.AL_R, new Point(alR.x, y)));
          }

//            OCVutils.drawRectangle(workingImage, nose, OCVutils.BLUE);

        // Kontrolone vykreslenie regionov
        OCVutils.drawRectangle(workingImage, leftEyeRegion, OCVutils.GREEN);
        OCVutils.drawRectangle(workingImage, rightEyeRegion, OCVutils.GREEN);

//        // Pomocne zobrazenie upraveneho - binarneho! obrazku
//        imageViewer.show(imageTmp, "Thresholded image");
    }

    private void findHoughCircles() {
        Mat imageGray = new Mat();
        Mat houghCircles = new Mat();
        // Imgproc.Canny(originalImage, imageGray, 10, 50, aperture, false);
        Imgproc.cvtColor(workingImage, imageGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(imageGray, imageGray, new Size(3, 3));

        int lowThreshold = 20;
        while (houghCircles.cols() < 3 && lowThreshold > 0) {
            houghCircles = new Mat();
            Imgproc.HoughCircles(imageGray, houghCircles, Imgproc.CV_HOUGH_GRADIENT, 1, imageGray.rows() / 8, 200, lowThreshold, 10, 50);
            lowThreshold--;
        }

        // Body musia byt presne dva, ak nebudu, tak vybrat take, ktore su
        // najviac v strede oka a ktore maju najmensi rozptyl od seba
        // a zaroven sa nachadzaju v hornej casti tvare
        double minDistance = Double.POSITIVE_INFINITY;
        Point pupilaL = new Point();
        Point pupilaR = new Point();
        int p1index = -1;
        int p2index = -1;

        for (int i = 0; i < houghCircles.cols() - 1; i++) {
            Point p1 = new Point(houghCircles.get(0, i)[0], houghCircles.get(0, i)[1]);
//          if (pupila1.y > (workingImage.height() / 2)) {
            for (int j = i + 1; j < houghCircles.cols(); j++) {
                Point p2 = new Point(houghCircles.get(0, j)[0], houghCircles.get(0, j)[1]);
//              if (pupila2.y > (workingImage.height() / 2)) {
                double pupilasDistance = Math.abs(p1.y - p2.y);
                if (pupilasDistance < minDistance) {
                    minDistance = pupilasDistance;
                    pupilaL = p1.x < p2.x ? p2 : p1;
                    pupilaR = p1.x < p2.x ? p1 : p2;
                    p1index = i;
                    p2index = j;
                }
//              }
//          }
            }
        }

        pupilaHoughCircles = new Mat(1, 2, CvType.CV_32FC3);
        pupilaHoughCircles.put(0, 0, houghCircles.get(0, p1index));
        pupilaHoughCircles.put(0, 1, houghCircles.get(0, p2index));

//        // Ulozit este rozpoznane kruznice, len kvoli vizualizacii
//        for (int i = 0; i < houghCircles.cols(); i++) {
//            if (i != p1index && i != p2index) {
//                Mat col = houghCircles.col(i);
//            }
//        }
//        houghCircles.copyTo(pupilaHoughCircles);
//        assert circles.cols() == 2;
//        Point p1 = new Point(pupilaHoughCircles.get(0, 0)[0], pupilaHoughCircles.get(0, 0)[1]);
//        Point p2 = new Point(pupilaHoughCircles.get(0, 1)[0], pupilaHoughCircles.get(0, 1)[1]);
//        // Vytvorit andtropometricke body pupila, podla umiestnenia
//        if (p1.x < p2.x) {
//            pupilaRFP = new TextureFP(FacialPointType.P_R, p1);
//            pupilaLFP = new TextureFP(FacialPointType.P_L, p2);
//        } else {
//            pupilaRFP = new TextureFP(FacialPointType.P_R, p2);
//            pupilaLFP = new TextureFP(FacialPointType.P_L, p1);
//        }
        pupilaLFP = new TextureFP(FacialPointType.P_L, pupilaL);
        textureFPs.add(pupilaLFP);

        pupilaRFP = new TextureFP(FacialPointType.P_R, pupilaR);
        textureFPs.add(pupilaRFP);
    }

    private boolean isValid() {
        if (workingImage.dataAddr() == 0) {
            System.out.println("Couldn't open image file.");
        }
        return workingImage.dataAddr() != 0;
    }

    private static Point getRightPoint(Mat image, Rect rect) {
        for (int i = rect.x; i < rect.x + rect.width; i++) {
            for (int j = rect.y; j < rect.y + rect.height; j++) {
                double[] point = image.get(j, i);
//                System.out.println("point " + i + ", " + j + " = " + point[0]);
                if (point[0] == 255) {
//                    OCVutils.drawCircle(image, p, new Scalar(255, 255, 255));
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    private static Point getLeftPoint(Mat image, Rect rect) {
        for (int i = rect.x + rect.width; i > rect.x; i--) {
            for (int j = rect.y; j < rect.y + rect.height; j++) {
                double[] point = image.get(j, i);
                if (point[0] == 255) {
//                    OCVutils.drawCircle(image, new Point(i, j), new Scalar(255, 255, 255));
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public void setObjFPmodel(FpModel fpModel) {
        this.objFPmodel = fpModel;
    }

    private Point getTextureFPfromModel(FacialPointType type) {
        if (objFPmodel == null) {
            return null;
        }
        FacialPoint fp = objFPmodel.getFacialPoint(type);
        Point imagePoint = textureMapper.getImagePoint(fp.getCoords());
        imagePoint.x *= originalImage.height();
        imagePoint.y = imagePoint.y;
        imagePoint.y *= originalImage.width();
        imagePoint.x *= resizeRatio;
        imagePoint.y *= resizeRatio;

        Point rotatedPoint = rotatePoint(imagePoint, 90, workingImage);
        OCVutils.drawPoint(workingImage, rotatedPoint, OCVutils.GREEN);
        return imagePoint;
    }

    private void drawTextureFPs(Mat image) {
        for (TextureFP fp : textureFPs) {
            OCVutils.drawPoint(image, fp.getPoint(), OCVutils.RED);
        }
    }

    private void drawHoughCircles(Mat image) {
        for (int i = 0; i < pupilaHoughCircles.cols(); i++) {
            Point center = new Point(pupilaHoughCircles.get(0, i)[0], pupilaHoughCircles.get(0, i)[1]);
            int radius = (int) Math.round(pupilaHoughCircles.get(0, i)[2]);

            OCVutils.drawCircle(image, center, OCVutils.BLUE, radius, 3);
        }
    }

    public void drawObjects(Mat image) {
//        drawTextureFPs(image);
//        drawHoughCircles(image);
//        OCVutils.drawRectangle(image, leftEye, OCVutils.GREEN);
//        OCVutils.drawRectangle(image, rightEye, OCVutils.GREEN);
        OCVutils.drawRectangle(image, nose, OCVutils.GREEN);
        OCVutils.drawRectangle(image, mouth, OCVutils.GREEN);
    }

    public void showWorkingImage() {
        showImage(workingImage);
    }

    public void showImage(Mat image) {
        imageViewer.show(image, "Texture image");
    }

    public List<FacialPoint> get3DfacialPoints() {
        assert textureMapper != null;

        List<FacialPoint> facialPoints = new ArrayList<>();

        rotateAndResizeFPs();

        for (TextureFP textureFP : textureFPs) {
            Vector3f modelVert = textureMapper.getModelVertFromTextureCoord(textureFP.getPoint(), originalImage);
            facialPoints.add(new FacialPoint(textureFP.getType(), modelVert));
        }

        return facialPoints;
    }

    private void rotateAndResizeFPs() {
        for (TextureFP textureFP : textureFPs) {
            textureFP.x /= resizeRatio;
            textureFP.y /= resizeRatio;
            textureFP.setPoint(rotatePoint(textureFP.getPoint(), -90, originalImage));
        }
    }

    public FpModel get3DfpModel() {
        assert textureMapper != null;

        FpModel fpModel = new FpModel();
        fpModel.setFacialpoints(get3DfacialPoints());
        return fpModel;
    }

//    public void setTextureMapper(TextureMapper textureMapper) {
//        this.textureMapper = textureMapper;
//    }
    public boolean isDoRotation() {
        return doRotation;
    }

    public void setDoRotation(boolean doRotation) {
        this.doRotation = doRotation;
    }

    private Point rotatePoint(Point p, double angle, Mat image) {
        double theta = Math.toRadians(angle);
        Point o = new Point(image.height() / 2, image.width() / 2);
        double x = Math.cos(theta) * (p.x - o.x) + Math.sin(theta) * (p.y - o.y) + o.y;
        double y = Math.sin(theta) * -(p.x - o.x) + Math.cos(theta) * (p.y - o.y) + o.x;

        return new Point(x, y);
    }

}
