package processing;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.ImageShow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HeelRecognition{

    private static final double FACTOR = 0.06;

    public static boolean isBootWithHeel(File file) {

        System.out.println(file.getName());
        Mat image = Imgcodecs.imread(file.getAbsolutePath());

        Mat mask = Mask.getMask(image, false);
//        ImageShow.imshow(mask, "mask");

//        ImageShow.imshow(crop(mask), "mask");

        Mat cropped = crop(mask);
        int whcs = 0;
        int whce = 0;
        int blc = 0;
        int ws = 0;
        int we = 0;
        int countOfCropping = 1;
//        ImageShow.imshow(cropped, "cropped" + countOfCropping);

        boolean stop = false;
        while (!stop) {
            for (int i = 0; i < cropped.cols() / 4; i++) {
                double[] pointF = cropped.get(cropped.rows() - 1, i);
                double[] pointL = cropped.get(cropped.rows() - 1, cropped.cols() /2 - i - 1);
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
            if ((blc < (double) (cropped.width() / 10)  || (blc != 0 && whcs == 0)) && countOfCropping < 3) {
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
            return false;
        } else {
            return true;
        }
    }

    private static Mat crop(Mat image) {

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
