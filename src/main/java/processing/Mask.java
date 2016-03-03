package processing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static utils.ImageShow.imshow;

public class Mask {

    public static int x, y, h, w = 0;

    public static Mat getMask(Mat image, boolean isItBoots) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        List<MatOfPoint> countours = new ArrayList<MatOfPoint>();

        Imgproc.threshold(image, image, 250, 255, Imgproc.THRESH_BINARY_INV);

        Mat kernel = Mat.ones(new Size(3, 3), Imgproc.MORPH_OPEN);

        Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel);

        Imgproc.findContours(image, countours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat mask = Mat.zeros(image.rows(), image.cols(), CvType.CV_8U);

        if (!countours.isEmpty()) {
            int max = 0;
            for (int i = 1; i < countours.size(); i++) {
                if (countours.get(max).height() < countours.get(i).height() ||
                        countours.get(max).width() < countours.get(i).width()) {
                    max = i;
                }
            }
            Rect boundingRect = Imgproc.boundingRect(countours.get(max));
            x = boundingRect.x;
            y = boundingRect.y;
            h = boundingRect.height;
            w = boundingRect.width;
        } else {
            throw new IllegalStateException();
        }

//        imshow(rect, "rect");

        Imgproc.drawContours(mask, countours, -1, new Scalar(255), -1);

//        if (isItBoots) {
//            Mat rect = Mat.zeros(image.size(), image.type());
//            Imgproc.rectangle(rect, new Point(x, y), new Point(x + w, y + (int) (h * 0.8)), new Scalar(255, 255, 255), -1);
//            Core.bitwise_and(mask, rect, mask);
//        }

//        imshow(mask, "cont");

        image.convertTo(image, CvType.CV_8UC3);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);

        return mask;
    }
}
