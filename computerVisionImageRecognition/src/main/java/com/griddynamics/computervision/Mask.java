package com.griddynamics.computervision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class Mask {

    public static int x, y, h, w = 0;

    public static double percents = 1;

    public static Mat getMask(Mat image, boolean isBoots) {

        Mat threeChannel = new Mat();
        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(threeChannel, threeChannel, 250, 255, Imgproc.THRESH_BINARY);

        Mat fg = new Mat(image.size(),CvType.CV_8U);
        Imgproc.erode(threeChannel,fg,new Mat(),new Point(-1,-1),2);

        Mat bg = new Mat(image.size(),CvType.CV_8U);

        Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 2);
        Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);
        Mat markers = new Mat(image.size(),CvType.CV_8U, new Scalar(0));
        Core.add(fg, bg, markers);

        WatershedSegmenter segmenter = new WatershedSegmenter();
        segmenter.setMarkers(markers);
        Mat result = segmenter.process(image);

        Imgproc.threshold(result, result, 250, 255, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat cont = result.clone();
        Imgproc.findContours(cont, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        int max = 0;

        ArrayList<Integer> indexesForDraw = new ArrayList<Integer>();
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
            x = 0;
            y = 0;
            h = image.height();
            w = image.width();
        }

        Imgproc.cvtColor(result,result,Imgproc.COLOR_GRAY2BGR);

         if (isBoots) {
            Mat rect = Mat.zeros(image.size(), image.type());
            Imgproc.rectangle(rect, new Point(x, y), new Point(x + w, y + (int) (h * 0.8)), new Scalar(255, 255, 255), -1);
            Core.bitwise_and(result, rect, result);
        }

        return result;
    }

    static class WatershedSegmenter{
        public Mat markers = new Mat();

        public void setMarkers(Mat markerImage)
        {
            markerImage.convertTo(markers, CvType.CV_32S);
        }

        public Mat process(Mat image)
        {
            Imgproc.watershed(image, markers);
            markers.convertTo(markers,CvType.CV_8U);
            return markers;
        }
    }
}

