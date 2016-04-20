package com.griddynamics.computervision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Preparation {

    private static final int OBJECTS_COUNT = 1;

    private static final int RECTANGLE_X = 0;

    private static final int RECTANGLE_Y = 0;

    private static final int TARGET_DIM_WITH = 80;
    private static final int TARGET_DIM_HEIGHT = 100;


    public static void prepare(String source, String destination, boolean checkRatio) {
        File sourceDir = new File(source);
        final File[] files = sourceDir.listFiles();
        if (sourceDir.isDirectory() && files != null && files.length != 0) {
            for (File image : files) {
                if (image.isDirectory()) {
                    continue;
                }
                try {
                    resizeFile(image, destination, true, checkRatio);
                } catch (IOException e) {
                    continue;
                }
            }
        }
    }

    public static Mat resizeFile(File image) throws IOException {
        return resizeFile(image, "", false, false);
    }

    private static Mat resizeFile(File image, String destination, boolean writeFile, boolean checkRatio) throws IOException {
        final BufferedImage img = ImageIO.read(image);
        Mat imageMat = null;
        if (img != null) {
            image = getTempFileByName(image.getName(), img, "jpg");
            imageMat = Imgcodecs.imread(image.getAbsolutePath());
            imageMat = cropByContour(imageMat, Mask.getMask(imageMat, false));
            if (checkRatio && !checkRatio(imageMat)){
                image.delete();
                return null;
            }
            if (!checkSize(imageMat)) {
                imageMat = resize(imageMat);
            }
            if (writeFile) {
                Imgcodecs.imwrite(destination + File.separator + image.getName(), imageMat);
            }
        }
        return imageMat;
    }

    private static boolean checkRatio(Mat imageMat) {
        return imageMat.height()/imageMat.width() <= 3;
    }

    private static File getTempFileByName(String fileName, BufferedImage img, String imageFormat) throws IOException {
        File file;
        file = new File(System.getProperty("java.io.tmpdir"), fileName);
        ImageIO.write(img, imageFormat, file);
        return file.isFile() ? file : null;
    }

    private static boolean checkSize(Mat image) {
        if (image.height() > TARGET_DIM_HEIGHT || image.width() > TARGET_DIM_WITH) {
            return false;
        } else {
            return true;
        }
    }

    private static Mat resize(Mat image) {
        int h = image.height();
        int w = image.width();

        int max_dim = (w >= h) ? w : h;


        if (w >= h) {
            float scale = ((float) TARGET_DIM_WITH) / w;
            w = TARGET_DIM_WITH;
            h = (int) (h * scale);
        } else {
            float scale = ((float) TARGET_DIM_HEIGHT) / h;
            h = TARGET_DIM_HEIGHT;
            w = (int) (w * scale);
        }
        Mat result = new Mat();
        Imgproc.resize(image, result, new Size(w, h));

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
//            if (boundingRect.width > boundingRect.height) {
//                boundingRect = new Rect(boundingRect.x, boundingRect.y, boundingRect.width, (int) (boundingRect.height * (1 - FACTOR)));
//            } else {
//                boundingRect = new Rect(boundingRect.x, boundingRect.y + (boundingRect.height - boundingRect.width), boundingRect.width, (int) (boundingRect.width * (1 - FACTOR)));
//            }
            return new Mat(image, boundingRect);
        } else {
            return image;
        }
    }

    public static void fillFileContainer(File sourceFolder, File targetFile, String relativePathToFile, boolean isPositive) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, true));
        for (File file : sourceFolder.listFiles()) {
            if (file.isFile()) {
                BufferedImage bimg = ImageIO.read(file);
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                StringBuilder stringBuilder = new StringBuilder(relativePathToFile).append(file.getName());
                if (isPositive) {
                    stringBuilder.append("  ").append(OBJECTS_COUNT)
                            .append("  ").append(RECTANGLE_X)
                            .append(" ").append(RECTANGLE_Y)
                            .append(" ").append(width).append(" ").append(height);
                }
                writer.write(stringBuilder.toString());
                writer.newLine();
            }
        }
        writer.close();
    }
}