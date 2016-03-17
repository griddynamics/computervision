package processing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.Cluster;
import processing.Mask;
import pojo.ColorDescription;
import utils.DeltaE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static utils.Colors.getRealColorsPalette;

/**
 * Created by npakhomova on 3/16/16.
 */
public class ColorsProcessor {


    public static TreeSet<ColorDescription> getColorDescriptions(File imageFile) {
        Mat imageForSegmentation = Imgcodecs.imread(imageFile.getAbsolutePath());
        Mat mask = Mask.getMask(imageForSegmentation, false); // todo get deal with strategies latter

        // Some magic
        Core.bitwise_and(imageForSegmentation, mask, imageForSegmentation);
        Imgproc.medianBlur(imageForSegmentation, imageForSegmentation, 3);
        Imgproc.cvtColor(imageForSegmentation, imageForSegmentation, Imgproc.COLOR_BGR2Lab);
        Mat imageInARow = imageForSegmentation.reshape(1, mask.cols() * mask.rows());
        ArrayList<Integer> nonZeroIndexes = Cluster.getNonZeroIndexes(imageInARow);
        Mat imagePreparedByMask = Cluster.getMatPreparedByNonZeroIndexes(imageInARow, nonZeroIndexes);

        // build clusterisation for image
        ImageClusterResult cluster = cluster(imagePreparedByMask, mask, 5, nonZeroIndexes);
        return processClusterisationResult(cluster);
    }

    private static TreeSet<ColorDescription> processClusterisationResult(ImageClusterResult cluster) {
        //aaa must be done in more efficient way

        HashMap<String, ColorDescription> colorDescriptionsMap = new HashMap<>();
        ArrayList<double[]> colors = new ArrayList<>();

        for (int x = 0; x < cluster.center.rows(); x++) {
            colors.add(new double[]{cluster.center.get(x, 0)[0],
                    cluster.center.get(x, 1)[0],
                    cluster.center.get(x, 2)[0]});
        }

        double dist;
        double min = Double.MAX_VALUE;
        double[] key = null;
        // AAA refactor it. More efficient structure for Palette!!!
        // we are in this cycle Palete.size * #colorInPicture * #Pictures
        for (int j = 0; j < colors.size(); j++) {
            double[] color = colors.get(j);
            String colorNameForDescription = null;
            for (String colorName : getRealColorsPalette().keySet()) {
                for (double[] doubles : getRealColorsPalette().get(colorName)) {
                    double[] lab1 = new double[]{
                            color[0] / 2.55,
                            color[1] - 128,
                            color[2] - 128
                    };

                    double[] lab2 = new double[]{
                            doubles[0],
                            doubles[1],
                            doubles[2]
                    };

                    dist = DeltaE.deltaE2000(lab1, lab2);

                    if (min > dist) {
                        min = dist;
                        key = doubles;
                        colorNameForDescription = colorName;
                    }
                }
            }
            if (key != null) {
                double sumOfPixels = 0;
                for (Integer integer : cluster.counts.values()) {
                    sumOfPixels = sumOfPixels + integer;
                }

                int persent = (int) (cluster.counts.get(j) / sumOfPixels * 100);

                double[] lab1 = new double[]{
                        color[0] / 2.55,
                        color[1] - 128,
                        color[2] - 128
                };

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


        TreeSet<ColorDescription> colorDescriptions = new TreeSet<>();
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

            Map<Integer, Integer> counts = new HashMap<>();

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

    public static class ImageClusterResult {
        Mat center;
        Mat cluster;
        private Map<Integer, Integer> counts;


        public ImageClusterResult(Mat center, Mat cluster, Map<Integer, Integer> counts) {
            this.center = center;
            this.cluster = cluster;
            this.counts = counts;
        }
    }

}

