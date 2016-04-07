package com.griddynamics.computervision;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapeRecognition {

    public static Shapes getShape(File imageFile) {
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        List<MatOfPoint> contours = getContours(image);
        MatOfPoint contourWithMaxArea = getContourWithMaxArea(contours);
        MatOfPoint2f matOfPoint2fContour = new MatOfPoint2f(contourWithMaxArea.toArray());
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        double epsilon = 0.01 * Imgproc.arcLength(matOfPoint2fContour, true);
        Imgproc.approxPolyDP(matOfPoint2fContour, approxContour2f, epsilon, true);
        int countOfSides = approxContour2f.toArray().length;
        Shapes shape = defineShape(approxContour2f, contourWithMaxArea, countOfSides);

        return shape;
    }

    public static Rational getRatio(File imageFile){
        return getRatio(imageFile, getShape(imageFile));
    }

    public static Rational getRatio(File imageFile, Shapes shape) {
        if (!shape.equals(Shapes.RECTANGLE)) {
            throw new IllegalArgumentException(shape.getName() + " doesn't support calculation of ratio");
        }

        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        List<MatOfPoint> contours = getContours(image);
        MatOfPoint contourWithMaxArea = getContourWithMaxArea(contours);
        Rect r = Imgproc.boundingRect(contourWithMaxArea);

        return getRatio((double) r.width / r.height);
    }

    private static List<MatOfPoint> getContours(Mat image) {
        Mat cont = Mask.getMask(image, false);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.cvtColor(cont, cont, Imgproc.COLOR_BGR2GRAY);

        Imgproc.findContours(cont, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static Shapes defineShape(MatOfPoint2f approxContour2f, MatOfPoint contourWithMaxArea, int countOfSides) {

        Rect r = Imgproc.boundingRect(contourWithMaxArea);

        if (countOfSides == 3) {
            return Shapes.TRIANGLE;
        } else if (countOfSides >= 4 && countOfSides <= 6) {
            int vtc = approxContour2f.toArray().length;

            List<Double> cos = new ArrayList<>();

            for (int j = 2; j < vtc + 1; j++) {
                Point[] approx = approxContour2f.toArray();
                cos.add(angle(approx[j % vtc], approx[j - 2], approx[j - 1]));
            }

            Collections.sort(cos);

            double mincos = cos.get(0);
            double maxcos = cos.get(cos.size() - 1);

            if (vtc == 4 && mincos >= -0.1 && maxcos <= 0.3) {
                double ratio = Math.abs(1 - (double) r.width / r.height);
                if (ratio <= 0.02) {
                    return Shapes.SQUARE;
                } else {
                    Rational ratioV = getRatio((double) r.width / r.height);
                    return Shapes.RECTANGLE;
                }
            } else if ((vtc == 5 && mincos >= -0.34 && maxcos <= -0.27)) {
                return Shapes.PENTA;
            } else if (vtc == 6 && mincos >= -0.55 && maxcos <= -0.45) {
                return Shapes.HEXA;
            }
        } else {
            // Detect and label circles
            double area = Imgproc.contourArea(contourWithMaxArea);
            int radius = r.width / 2;

            if (Math.abs(1 - ((double) r.width / r.height)) <= 0.2 &&
                    Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))) <= 0.2) {
                return Shapes.ROUNDED;
            }
        }
        return Shapes.UNDEFINED;
    }

    private static MatOfPoint getContourWithMaxArea(List<MatOfPoint> contours) {
        int indexOfMaxAreaContour = 0;
        for (int i = 0; i < contours.size(); i++) {
            double maxContourArea = Imgproc.contourArea(contours.get(indexOfMaxAreaContour));
            double contourArea = Imgproc.contourArea(contours.get(i));
            if (maxContourArea < contourArea) {
                indexOfMaxAreaContour = i;
            }
        }
        return contours.get(indexOfMaxAreaContour);
    }

    static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private static Rational getRatio(double x) {
        double epsilon = 1E-2;
        Rational left = new Rational(0, 1);
        Rational right = new Rational(1, 0);
        Rational best = left;
        double bestError = Math.abs(x);

        // do Stern-Brocot binary search
        while (bestError > epsilon) {

            // compute next possible rational approximation
            Rational mediant = Rational.mediant(left, right);
            if (x < mediant.toDouble())
                right = mediant;              // go left
            else
                left = mediant;              // go right

            // check if better and update champion
            double error = Math.abs(mediant.toDouble() - x);
            if (error < bestError) {
                best = mediant;
                bestError = error;
            }
        }
        return best;
    }

}
