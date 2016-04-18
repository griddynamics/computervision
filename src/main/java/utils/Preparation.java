package utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.Mask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Preparation{

    private static final int TARGET_DIM = 100;

    public static void prepare(String source, String destination) {
        File sourceDir = new File(source);
        final File[] files = sourceDir.listFiles();
        if (sourceDir.isDirectory() && files != null && files.length != 0) {
            for (File image : files) {
                final BufferedImage img;
                if (image.isDirectory()) {
                    continue;
                }
                try {
                    img = ImageIO.read(image);
                    if (img != null) {
                        image = getTempFileByName(image.getName(), img, "jpg");
                        Mat imageMat = Imgcodecs.imread(image.getAbsolutePath());
                        imageMat = cropByContour(imageMat, Mask.getMask(imageMat, false));
                        if (!checkSize(imageMat)) {
                            imageMat = resize(imageMat);
                        }
                        Imgcodecs.imwrite(destination + File.separator + image.getName(), imageMat);

                    }
                } catch (IOException e) {
                    continue;
                }
            }
        }
    }

    private static File getTempFileByName(String fileName, BufferedImage img, String imageFormat) throws IOException {
        File file;
        file = new File(System.getProperty("java.io.tmpdir"), fileName);
        ImageIO.write(img, imageFormat, file);
        return file.isFile() ? file : null;
    }

    private static boolean checkSize(Mat image) {
        if (image.height() > TARGET_DIM || image.width() > TARGET_DIM) {
            return false;
        } else {
            return true;
        }
    }

    private static Mat resize(Mat image){
        int h = image.height();
        int w = image.width();
        int max_dim = ( w >= h ) ? w : h;
        float scale = ( ( float ) TARGET_DIM ) / max_dim;

        if ( w >= h ) {
            w = TARGET_DIM;
            h = (int) (h * scale);
        }
        else {
            h = TARGET_DIM;
            w = (int) (w * scale);
        }
        Mat result = new Mat();
        Imgproc.resize(image, result, new Size(w,h));

        return result;
    }

    private static Mat cropByContour(Mat image, Mat mask) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_BGR2GRAY);
        Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
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
            return new Mat(image, boundingRect);
        } else {
            return image;
        }
    }
}