package com.griddynamics.computervision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;




/**
 * Created by npakhomova on 3/16/16.
 */
public class ColorsRecognitionUtil {



    public static TreeSet<ColorDescription> getColorDescriptions(File imageFile, boolean isBodyCutNecessary) {

        Mat imageForSegmentation = Imgcodecs.imread(imageFile.getAbsolutePath());
        if (isBodyCutNecessary){
            imageForSegmentation = detectBody(imageForSegmentation);
        }
//        ImageShow.imshow(imageForSegmentation,"image");
        Mat mask = Mask.getMask(imageForSegmentation, false); // todo get deal with strategies latter
        Core.bitwise_and(imageForSegmentation, mask, imageForSegmentation);
        Imgproc.medianBlur(imageForSegmentation, imageForSegmentation, 3);
        Imgproc.cvtColor(imageForSegmentation, imageForSegmentation, Imgproc.COLOR_BGR2Lab);
        Mat imageInARow = imageForSegmentation.reshape(1, mask.cols() * mask.rows());
        ArrayList<Integer> nonZeroIndexes = getNonZeroIndexes(imageInARow);
        Mat imagePreparedByMask = getMatPreparedByNonZeroIndexes(imageInARow, nonZeroIndexes);

        // build clusterisation for image
        ImageClusterResult cluster = cluster(imagePreparedByMask, mask, 5, nonZeroIndexes);
        return processClusterisationResult(cluster);
    }

    private static TreeSet<ColorDescription> processClusterisationResult(ImageClusterResult cluster) {
        //aaa must be done in more efficient way

        HashMap<String, ColorDescription> colorDescriptionsMap = new HashMap<String, ColorDescription>();
        ArrayList<double[]> colors = new ArrayList<double[]>();

        for (int x = 0; x < cluster.getCenter().rows(); x++) {
            colors.add(new double[]{cluster.getCenter().get(x, 0)[0],
                    cluster.getCenter().get(x, 1)[0],
                    cluster.getCenter().get(x, 2)[0]});
        }

        double dist;
        double min = Double.MAX_VALUE;
        double[] key = null;
        // AAA refactor it. More efficient structure for Palette!!!
        // we are in this cycle Palete.size * #colorInPicture * #Pictures
        for (int j = 0; j < colors.size(); j++) {
            double[] color = colors.get(j);
            String colorNameForDescription = null;
            double[] lab1 = new double[]{
                    (int)(color[0] / 2.55),
                    color[1] - 128,
                    color[2] - 128
            };
            for (String colorName : Colors.COLORS_PALETTE.keySet()) {
                for (double[] realColor : Colors.COLORS_PALETTE.get(colorName)) {


                    double[] lab2 = new double[]{
                            realColor[0],
                            realColor[1],
                            realColor[2]
                    };

                    dist = DeltaE.deltaE2000(lab1, lab2);

                    if (min > dist) {
                        min = dist;
                        key = realColor;
                        colorNameForDescription = colorName;
                    }
                }
            }
            if (key != null) {
                double sumOfPixels = 0;
                for (Integer integer : cluster.getCounts().values()) {
                    sumOfPixels = sumOfPixels + integer;
                }

                int persent = (int) (cluster.getCounts().get(j) / sumOfPixels * 100);

                ColorDescription colorDescription = new ColorDescription(colorNameForDescription, lab1, persent);
                if (colorDescriptionsMap.containsKey(colorDescription.getName())) {
                    colorDescriptionsMap.get(colorDescription.getName()).merge(colorDescription);
                } else {
                    colorDescriptionsMap.put(colorDescription.getName(), colorDescription);
                }
                min = Double.MAX_VALUE;
                key = null;
            }
        }


        TreeSet<ColorDescription> colorDescriptions = new TreeSet<ColorDescription>();
        colorDescriptions.addAll(colorDescriptionsMap.values());
        return colorDescriptions;
    }

    private static ImageClusterResult cluster(Mat imagePreparedByMask, Mat mask, int k, ArrayList<Integer> nonZeroIndexes) {
        // don't understand it
        Mat samples32f = new Mat();
        imagePreparedByMask.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
        if (imagePreparedByMask.height() != 0) {
            Mat labels = new Mat();
            TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
            Mat centers = new Mat();
            Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

            Map<Integer, Integer> counts = new HashMap<Integer, Integer>();

            centers.convertTo(centers, CvType.CV_8UC1, 255.0);
            centers.reshape(3);
            List<Mat> clusters = new ArrayList<Mat>();
            Mat cluster = Mat.zeros(mask.rows(), mask.cols(), mask.type());
            for (int i = 0; i < centers.rows(); i++) {
                clusters.add(Mat.zeros(mask.size(), mask.type()));
                Imgproc.cvtColor(clusters.get(i), clusters.get(i), Imgproc.COLOR_BGR2Lab);
            }
            for (int i = 0; i < centers.rows(); i++) counts.put(i, 0);
            int index = 0;
            int nums = 0;

            for (int y = 0; y < mask.rows(); y++) {
                for (int x = 0; x < mask.cols(); x++) {
                    if (nums > nonZeroIndexes.get(nonZeroIndexes.size() - 1) || nums < nonZeroIndexes.get(index)) {
                        int ch1 = 0;
                        int ch2 = 128;
                        int ch3 = 128;
                        for (Mat mat : clusters) {
                            mat.put(y, x, ch1, ch2, ch3);
                            cluster.put(y, x, ch1, ch2, ch3);
                        }
                    } else if (nums == nonZeroIndexes.get(index)) {
                        int label = (int) labels.get(index, 0)[0];
                        int b = (int) centers.get(label, 0)[0];
                        int g = (int) centers.get(label, 1)[0];
                        int r = (int) centers.get(label, 2)[0];
                        clusters.get(label).put(y, x, b, g, r);
                        cluster.put(y, x, b, g, r);
                        index++;
                        try {
                            counts.put(label, (counts.get(label) + 1));
                        } catch (Exception ex) {
                            System.out.println("Smth happends" + counts);
                        }

                    }
                    nums++;
                }
            }
            return new ImageClusterResult(centers, cluster, counts);

        } else {
            return null;
        }
    }

    public static ArrayList<Integer> getNonZeroIndexes(Mat imageInARow) {
        ArrayList<Integer> nonZeroIndexes = new ArrayList<Integer>();

        for (int j = 0; j < imageInARow.rows(); j++) {
            if (imageInARow.get(j, 0)[0] != 0 ||
                    imageInARow.get(j, 1)[0] - 128 != 0 ||
                    imageInARow.get(j, 2)[0] - 128 != 0) {
                nonZeroIndexes.add(j);
            }
        }
        return nonZeroIndexes;
    }

    public static Mat getMatPreparedByNonZeroIndexes(Mat imageInARow, ArrayList<Integer> nonZeroIndexes) {
        Mat imagePreparedByMask = Mat.zeros(nonZeroIndexes.size(), 3, imageInARow.type());

        for (int j = 0; j < nonZeroIndexes.size(); j++) {
            imagePreparedByMask.put(j, 0, imageInARow.get(nonZeroIndexes.get(j), 0));
            imagePreparedByMask.put(j, 1, imageInARow.get(nonZeroIndexes.get(j), 1));
            imagePreparedByMask.put(j, 2, imageInARow.get(nonZeroIndexes.get(j), 2));
        }
        return imagePreparedByMask;
    }

    public static Mat detectBody(Mat image) {
//        Mat image = Imgcodecs.imread(file.getAbsolutePath());
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
        Mat submat = image.clone();
        if (foundLocations.rows() > 0) {
            framesWithPeople++;
            List<Double> weightList = foundWeights.toList();
            List<Rect> rectList = foundLocations.toList();
            int i = 0;

            for (Rect rect : rectList) {
                submat = image.submat(rect);

            }
        }
        return submat;
//        ImageShow.imshow(submat, "image");
    }
}

