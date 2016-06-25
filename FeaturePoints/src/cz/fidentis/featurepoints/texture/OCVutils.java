package cz.fidentis.featurepoints.texture;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Galvanizze
 */
public class OCVutils {

    public static final int DEFAULT_IMAGE_WIDTH = 800;
    public static final Scalar RED = new Scalar(0, 0, 255);
    public static final Scalar GREEN = new Scalar(0, 255, 0);
    public static final Scalar BLUE = new Scalar(255, 0, 0);

    public static double resize(Mat image) {
        // Vypocet pomeru stran podla sirky
        Size inputSize = image.size();
        double ratio = DEFAULT_IMAGE_WIDTH / inputSize.width;

        resize(image, (int) (inputSize.width * ratio), (int) (inputSize.height * ratio));

        return ratio;
    }

    public static void resize(Mat image, int width, int height) {
        Mat output = new Mat();
        Imgproc.resize(image, output, new Size(width, height));

        output.copyTo(image);
    }

    public static void rotate_90n(Mat image, int angle) {
        Mat output = new Mat();

        switch (angle) {
            case 270:
            case -90:
                Core.flip(image.t(), output, 0);
                break;
            case 180:
            case -180:
                Core.flip(image, output, -1);
                break;
            case 90:
            case -270:
                Core.flip(image.t(), output, 1);
                break;
            case 360:
            case 0:
                return;
        }

        output.copyTo(image);
    }

    public static void bilateralFilter(Mat image) {
        Mat output = new Mat();
        Imgproc.bilateralFilter(image, output, -1, 20, 20);

        output.copyTo(image);
    }

    public static void sobel(Mat image, int aperture, int dx, int dy) {
        Mat output = new Mat();
        // x - 0, y - 2
        Imgproc.Sobel(image, output, -1, dx, dy, aperture, 1.0, 0.0);

        output.copyTo(image);
    }

    public static void laplacian(Mat image, int aperture) {
        Mat output = new Mat();
        Imgproc.Laplacian(image, output, -1, aperture, 1.0, 0.0);

        output.copyTo(image);
    }

    public static void threshold(Mat image, int threshold) {
        Mat output = new Mat(image.size(), CvType.CV_8UC1);
        Imgproc.threshold(image, output, threshold, 255, Imgproc.THRESH_BINARY);
        // Imgproc.adaptiveThreshold(image, outputImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 50);

        output.copyTo(image);
    }

    public static void drawPoint(Mat image, Point center, Scalar color) {
        Imgproc.circle(image, center, 5, color, -1, 8, 0);
    }

    public static void drawCircle(Mat image, Point center, Scalar color) {
        Imgproc.circle(image, center, 5, color, 1);
    }

    public static void drawCircle(Mat image, Point center, Scalar color, int radius) {
        drawCircle(image, center, color, radius, 1);
    }
    
    public static void drawCircle(Mat image, Point center, Scalar color, int radius, int thickness) {
        Imgproc.circle(image, center, radius, color, thickness);
    }

    public static void drawEllipseFromRect(Mat image, Rect rect, Scalar color) {
        if (rect != null) {
            Imgproc.ellipse(image, new Point(rect.x + rect.width / 2, rect.y + rect.height / 2),
                    new Size(rect.width / 2, rect.height / 2), 0, 0, 360, color);
        }
    }

    public static void drawRectangle(Mat image, Rect rect, Scalar color) {
        if (rect != null) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height), color);
        }
    }

    public static void open(Mat image, int elementSize, int elementShape) {
        Mat output = new Mat();
        Mat element = getKernelFromShape(elementSize, elementShape);
        Imgproc.morphologyEx(image, output, Imgproc.MORPH_OPEN, element);

        output.copyTo(image);
    }

    private static Mat getKernelFromShape(int elementSize, int elementShape) {
        return Imgproc.getStructuringElement(elementShape, new Size(elementSize * 2 + 1, elementSize * 2 + 1),
                new Point(elementSize, elementSize));
    }

    public static void saveImage(Mat image, String filepath) {
        boolean imwrite = Imgcodecs.imwrite(filepath, image);
    }
}
