package cz.fidentis.featurepoints.texture;

import static java.io.File.separatorChar;
import java.io.IOException;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 *
 * @author Galvanizze
 */
public class HaarCascade {

    private String haarcascadePath;

    // Oci
    private static final String HAARCASCADE_EYES = "haarcascade_eye.xml";
    private static final String HAARCASCADE_EYE_L_2SPLITS = "haarcascade_lefteye_2splits.xml";
    private static final String HAARCASCADE_EYE_R_2SPLITS = "haarcascade_righteye_2splits.xml";
    // nie prilis presne
    private static final String HAARCASCADE_EYE_L = "haarcascade_mcs_lefteye.xml";
    private static final String HAARCASCADE_EYE_R = "haarcascade_mcs_righteye.xml";
    private static final String HAARCASCADE_EYE_PAIR_BIG = "haarcascade_mcs_eyepair_big.xml";
    private static final String HAARCASCADE_EYE_PAIR_SMALL = "haarcascade_mcs_eyepair_small.xml";

    // Nos
    private static final String HAARCASCADE_NOSE = "haarcascade_mcs_nose.xml";

    // Usta
    private static final String HAARCASCADE_MOUTH = "haarcascade_mcs_mouth.xml";
    // Usmev - nepresne
    private static final String HAARCASCADE_SMILE = "haarcascade_smile.xml";

    // Usi
    private static final String HAARCASCADE_EAR_L = "haarcascade_mcs_leftear.xml";
    private static final String HAARCASCADE_EAR_R = "haarcascade_mcs_rightear.xml";

    private final Mat image;
    
    public HaarCascade(Mat image) {
        this.image = image;
         haarcascadePath = "";
        try {
            haarcascadePath = new java.io.File(".").getCanonicalPath();
        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
        }
        if (!haarcascadePath.equals("")) {
            haarcascadePath = haarcascadePath + separatorChar + "models" + separatorChar + "resources" + separatorChar + "haarcascades" + separatorChar;
        }
    }

    public Rect detectEyes() {
        return getFirstRect(detect(HAARCASCADE_EYES));
    }

    public Rect detectRightEye() {
        System.out.print("Right eye: ");
//        return getFirstRect(detect(HAARCASCADE_EYE_R_2SPLITS));
        return getFirstRect(detect(HAARCASCADE_EYE_R));
    }

    public Rect detectLeftEye() {
        System.out.print("Left eye: ");
//        return getFirstRect(detect(HAARCASCADE_EYE_L_2SPLITS));
        return getFirstRect(detect(HAARCASCADE_EYE_L));
    }

    public Rect detectMouth() {
        System.out.print("Mouth: ");
        return getFirstRect(detect(HAARCASCADE_MOUTH));
    }

    public Rect detectNose() {
        System.out.print("Nose: ");

        List<Rect> detections = detect(HAARCASCADE_NOSE);
        // Nos zvolime ako najvacsi najdeny region
        Rect nose = new Rect();
        double area = Double.NEGATIVE_INFINITY;

        if (detections == null || detections.isEmpty()) {
            return null;
        }

        for (Rect detection : detections) {
            double detectionArea = detection.area();
            if (detectionArea > area) {
                area = detectionArea;
                nose = detection;
            }
        }

        return nose;
    }

    public List<Rect> detect(String cascade) {
        MatOfRect detections = new MatOfRect();
        String cascadePath = getHaarCascade(cascade);
        MatOfInt num = new MatOfInt(1);

        CascadeClassifier detector = new CascadeClassifier(cascadePath);

//        detector.detectMultiScale2(image, detections, num);
        // teoreticky nemusi byt rozpoznany ten objekt, ktory chceme
        // ak algoritmus nebude spravne fungovat na vacsej mnozine dat
        // tak je nutne doplnit dalsie parametre pre metodu detectMUltiscale
        // alebo doplnit logiku po spracovani
        // pre nos zatial najlepsie:
//                detector.detectMultiScale2(image, detections, num, 10, 1, Objdetect.CASCADE_DO_ROUGH_SEARCH,
//                new Size(image.width() / 4, image.width() / 4), new Size(image.width()/2, image.width()/2));
        detector.detectMultiScale2(image, detections, num, 10, 2, Objdetect.CASCADE_DO_CANNY_PRUNING,
                new Size(image.width() / 4, image.width() / 4), new Size(image.width() / 2, image.width() / 2));
        // Kontrolne zobrazenie najden\ch objektov
//        drawEllipsesFromRects(detections, new Scalar(0, 255, 0));
        // Zo zoznamu rozpoznanych objektov vybrat len ten prvy
        // aj toto moze sposobit chybovost, objektov moze byt viac
        List<Rect> detectionsList = detections.toList();

        System.out.println(detectionsList.size());

        return detectionsList;
    }

    private Rect getFirstRect(List<Rect> rects) {
        return rects.isEmpty() ? null : rects.get(0);
    }

    public void drawEllipsesFromRects(MatOfRect rects, Scalar color) {
        for (Rect rect : rects.toArray()) {
            OCVutils.drawEllipseFromRect(image, rect, color);
        }
    }

    private void drawRectangles(MatOfRect rects, Scalar color) {
        for (Rect rect : rects.toArray()) {
            OCVutils.drawRectangle(image, rect, color);
        }
    }

    private String getHaarCascade(String cascade) {
        return haarcascadePath + cascade;
    }

}
