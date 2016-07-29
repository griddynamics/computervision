package com.griddynamics.computervision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeelRecognitionUtil {
    private static final double FACTOR = 0.06;

    public static boolean isHighHeelByHaar(File imageFile, String cascadePath) throws IOException {
        boolean result = false;
        Mat image = Preparation.resizeFile(imageFile);
//        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        Mat grey = new Mat();
        Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2GRAY);
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        cascadeClassifier.load(cascadePath);
        MatOfRect matOfRect = new MatOfRect();
        cascadeClassifier.detectMultiScale(grey, matOfRect);

        if (matOfRect.toList().size() > 0) {
            result = true;
        }
        return result;
    }

    public static HeightHeelValueResult defineHeelHeight(File file) {

//        System.out.println(file.getName());
        Mat image = Imgcodecs.imread(file.getAbsolutePath());

        Mat mask = Mask.getMask(image, false);
//        ImageShow.imshow(mask, "mask");

//        ImageShow.imshow(crop(mask), "mask");

        Mat cropped = crop(mask);



//        ImageShow.imshow(cropped, "cropped" + countOfCropping);
        double ratio = (double) cropped.rows() / cropped.cols();
        System.out.println("Ration is : " + ratio);
        if (ratio > 0.6 ) {
            // could be both...
            return getHeightHeelValueResult(cropped,  ratio);
        }
        return new HeightHeelValueResult(HeelHeightMCOMValue.Flat, ratio);

    }

    public static HeightHeelValueResult getHeightHeelValueResult(Mat cropped,  double ratio) {
        int blc = 0;
        int countOfCropping = 1;
        int ws;
        int we;
        boolean stop = false;
        int whce = 0;
        int whcs = 0;
        while (!stop) {
            for (int i = 0; i < cropped.cols() / 4; i++) {
                double[] pointF = cropped.get(cropped.rows() - 1, i);
                double[] pointL = cropped.get(cropped.rows() - 1, cropped.cols() / 2 - i - 1);
                if (pointF[0] != 0 & pointF[1] != 0 & pointF[2] != 0) {
                    if (whcs == 0) {
                        ws = i;
                    }
                    whcs++;
                } else if (whcs != 0) {
                    blc++;
                }
                if (pointL[0] != 0 & pointL[1] != 0 & pointL[2] != 0) {
                    if (whce == 0) {
                        we = i;
                    }
                    whce++;
                } else {
                    blc++;
                }
            }
            if ((blc < (double) (cropped.width() / 10) || (blc != 0 && whcs == 0)) && countOfCropping < 3) {
                cropped = crop(cropped);
                whcs = 0;
                whce = 0;
                blc = 0;
                ws = 0;
                we = 0;
                countOfCropping++;
//                ImageShow.imshow(cropped, "cropped" + countOfCropping);
            } else {
                stop = true;
            }
        }

        if (blc < (cropped.width() / 10)) {
            return new HeightHeelValueResult(HeelHeightMCOMValue.Flat, ratio);
        } else {
            return new HeightHeelValueResult(HeelHeightMCOMValue.Ultra_High,ratio);
        }
    }

    public static Mat crop(Mat image) {

        Mat result = image.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(result, result, Imgproc.COLOR_BGR2GRAY);
        Imgproc.findContours(result, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;
        if (!contours.isEmpty()) {
            int max = 0;
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    max = contours.indexOf(contour);
                }
            }
            Rect boundingRect = Imgproc.boundingRect(contours.get(max));
            Rect croppedRect = null;
            if (boundingRect.width > boundingRect.height) {
                croppedRect = new Rect(boundingRect.x, boundingRect.y, boundingRect.width, (int) (boundingRect.height * (1 - FACTOR)));
            } else {
                croppedRect = new Rect(boundingRect.x, boundingRect.y + (boundingRect.height - boundingRect.width), boundingRect.width, (int) (boundingRect.width * (1 - FACTOR)));
            }
            return new Mat(image, croppedRect);
        } else {
            return image;
        }
    }
}
