package processing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static utils.ImageShow.imshow;

public class Mask {

    public static int x, y, h, w = 0;

    public static double percents = 1;

    public static Mat getMask(Mat image, boolean isItBoots) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.threshold(image, image, 250, 255, Imgproc.THRESH_BINARY_INV);

        Mat kernel = Mat.ones(new Size(3, 3), Imgproc.MORPH_OPEN);

        Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel);

        Mat kernelClose = Mat.ones(new Size(5, 5), Imgproc.MORPH_CLOSE);
        Imgproc.morphologyEx(image, image, Imgproc.MORPH_CLOSE, kernelClose);
        Mat morph = image.clone();
        Imgproc.findContours(morph, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat mask = Mat.zeros(image.rows(), image.cols(), CvType.CV_8U);
        int max = 0;

        ArrayList<Integer> indexesForDraw = new ArrayList<>();
        double maxArea = 0;
        if (!contours.isEmpty()) {
            max = 0;
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    max = contours.indexOf(contour);
                }
            }

            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if ((area / maxArea * 100) > percents) {
                    indexesForDraw.add(contours.indexOf(contour));
                }
            }

            Rect boundingRect = Imgproc.boundingRect(contours.get(max));
            x = boundingRect.x;
            y = boundingRect.y;
            h = boundingRect.height;
            w = boundingRect.width;
        } else {
            throw new IllegalStateException();
        }

        for (Integer index : indexesForDraw) {
            if (Imgproc.contourArea(contours.get(index)) == maxArea) {
                Imgproc.drawContours(mask, contours, index, new Scalar(255), -1);
            } else {
                Imgproc.drawContours(mask, contours, index, new Scalar(0), -1);
            }
        }

//        imshow(mask, "MASK");

//        Core.bitwise_and(image, mask, mask);

//        if (isItBoots) {
//            Mat rect = Mat.zeros(image.size(), image.type());
//            Imgproc.rectangle(rect, new Point(x, y), new Point(x + w, y + (int) (h * 0.8)), new Scalar(255, 255, 255), -1);
//            Core.bitwise_and(mask, rect, mask);
//        }

//        image.convertTo(image, CvType.CV_8UC3);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);

        return mask;
    }
}
