package processing;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BodyRecognition {

    public static BodyRecognitionResult detectFace(File file) {
        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        Mat imageWithBounds = image.clone();
        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("src/main/resources/cascades/haarcascade_frontalface_alt.xml");

        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayImage, grayImage);

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(image, faces);
        System.out.println(String.format("Detected %s faces",  faces.toArray().length));
        List<Mat> croppedImage = new ArrayList<>();
        for (Rect rect : faces.toArray()) {
            Imgproc.rectangle(imageWithBounds, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            croppedImage.add(new Mat(image, new Rect(rect.x, rect.y, rect.width, rect.height)));
        }
        return new BodyRecognitionResult(BodyParts.FACE, croppedImage, imageWithBounds);
    }

    // doesn't work, in development
    public static void detectBody(File file) {
        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        Mat imageWithBounds = image.clone();
        final HOGDescriptor hog = new HOGDescriptor();
        final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
        hog.setSVMDetector(descriptors);
        final MatOfRect foundLocations = new MatOfRect();
        final MatOfDouble foundWeights = new MatOfDouble();
        final Size winStride = new Size(8, 8);
        final Size padding = new Size(32, 32);
        final Point rectPoint1 = new Point();
        final Point rectPoint2 = new Point();
        final Point fontPoint = new Point();
        int frames = 0;
        int framesWithPeople = 0;
        final Scalar rectColor = new Scalar(0, 255, 0);
        final Scalar fontColor = new Scalar(255, 255, 255);
        final long startTime = System.currentTimeMillis();

        hog.detectMultiScale(image, foundLocations, foundWeights, 0.0, winStride, padding, 1.05, 2.0, false);
        // CHECKSTYLE:ON MagicNumber
        if (foundLocations.rows() > 0) {
            framesWithPeople++;
            List<Double> weightList = foundWeights.toList();
            List<Rect> rectList = foundLocations.toList();
            int i = 0;
            for (Rect rect : rectList) {
                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                // Draw rectangle around fond object
                Imgproc.rectangle(image, rectPoint1, rectPoint2, rectColor, 2);
                fontPoint.x = rect.x;
                // CHECKSTYLE:OFF MagicNumber - Magic numbers here for
                // illustration
                fontPoint.y = rect.y - 4;
                // CHECKSTYLE:ON MagicNumber
                // Print weight
                // CHECKSTYLE:OFF MagicNumber - Magic numbers here for
                // illustration
                Imgproc.putText(image, String.format("%1.2f", weightList.get(i)), fontPoint, Core.FONT_HERSHEY_PLAIN,
                        1.5, fontColor, 2, Core.LINE_AA, false);
                // CHECKSTYLE:ON MagicNumber
                i++;
            }
        }
//        ImageShow.imshow(image, "image");
    }
}
