package processing;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.DeltaE;
import utils.ImageShow;
import utils.MapUtils;
import utils.ColorAndPercents;

import static utils.ImageShow.imshow;
import static utils.ImageShow.showExpl;

import static utils.Colors.*;

public class Cluster {

    File pathForSort = new File("/Users/abelyakov/Development/pet/sort/sneakers/blue");

    static Mat cl = new Mat();
    static Mat center = new Mat();
    static Mat label = new Mat();
    static int k = 5;
    static Map<Integer, Integer> counts = new HashMap<>();
    static List<ColorAndPercents> colorByLabel = new ArrayList<>();

    boolean isHorizontal = false;

    ArrayList<String> colorNames;
    ArrayList<ColorCode> colorCodes;

    public Image segmentation(File file) {

        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        Mat imageForSegmentation = image.clone();
        Mat original = image.clone();
        Mat mask = Mask.getMask(image);
        Core.bitwise_and(imageForSegmentation, mask, imageForSegmentation);

        System.out.println("\n" + file.getName());

        Imgproc.medianBlur(imageForSegmentation, imageForSegmentation, 3);
        Imgproc.cvtColor(imageForSegmentation, imageForSegmentation, Imgproc.COLOR_BGR2Lab);

        Mat imageInARow = imageForSegmentation.reshape(1, mask.cols() * mask.rows());

        ArrayList<Integer> nonZeroIndexes = getNonZeroIndexes(imageInARow);

        Mat imagePreparedByMask = getMatPreparedByNonZeroIndexes(imageInARow, nonZeroIndexes);

        cluster(imagePreparedByMask, mask, k, nonZeroIndexes);

        ArrayList<double[]> colors = new ArrayList<>();

        for (int x = 0; x < center.rows(); x++) {
            colors.add(new double[]{center.get(x, 0)[0],
                    center.get(x, 1)[0],
                    center.get(x, 2)[0]});
        }

        double dist;
        double min = Double.MAX_VALUE;
        double[] key = null;

        colorNames = new ArrayList<String>();
        colorCodes = new ArrayList<ColorCode>();

        for (int j = 0; j < colors.size(); j++) {
            for (double[] doubles : COLORS.keySet()) {

                double[] lab1 = new double[]{
                        colors.get(j)[0] / 2.55,
                        colors.get(j)[1] - 128,
                        colors.get(j)[2] - 128
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
                }
            }
            if (key != null) {
                colorNames.add(COLORS.get(key));
                colorCodes.add(new ColorCode(COLORS.get(key), colors.get(j)));
                min = Double.MAX_VALUE;
                key = null;
            }
        }

        for (ColorCode colorCode : colorCodes) {
            System.out.print("\n" + colorCode.getName() + " ");
            System.out.print("{" + (int) (colorCode.getCode()[0] / 2.55) + ",  " + (int) (colorCode.getCode()[1] - 128) + ",  " + (int) (colorCode.getCode()[2] - 128) + "}");
        }
        System.out.println();


        double sumOfPixels = 0;
        for (Integer integer : counts.values()) {
            sumOfPixels = sumOfPixels + integer;
        }

        HashMap<String, Integer> nameAndPercents = new HashMap<String, Integer>();

        for (Integer index : counts.keySet()) {
            String name = colorNames.get(index);
            int percent = (int) (counts.get(index) / sumOfPixels * 100);
            if (nameAndPercents.containsKey(name)) {
                System.out.println(name + " " + percent + "%" + "(merged) common val:" + (nameAndPercents.get(name) + percent) + "%");
                nameAndPercents.put(name, nameAndPercents.get(name) + percent);
            } else {
                nameAndPercents.put(name, percent);
                System.out.println(name + " " + percent + "%");
            }
        }

        Map<String, Integer> SortedByPercent = MapUtils.sortByValue(nameAndPercents);

        Iterator<Map.Entry<String, Integer>> iterator = SortedByPercent.entrySet().iterator();
        Map.Entry<String, Integer> first = null;
        Map.Entry<String, Integer> second = null;
        Map.Entry<String, Integer> third = null;
        Map.Entry<String, Integer> fourth = null;
        Map.Entry<String, Integer> fifth = null;
        if (iterator.hasNext()) first = iterator.next();
        if (iterator.hasNext()) second = iterator.next();
        if (iterator.hasNext()) third = iterator.next();
        if (iterator.hasNext()) fourth = iterator.next();
        if (iterator.hasNext()) fifth = iterator.next();

        if (first == null)
            throw new IllegalArgumentException("No colors found");

        SortedByPercent = MapUtils.sortByValue(nameAndPercents);

        iterator = SortedByPercent.entrySet().iterator();
        ArrayList<Map.Entry<String, Integer>> colorArea = new ArrayList<>();

        while ((iterator.hasNext())) colorArea.add(iterator.next());

        if (colorArea.isEmpty()) throw new IllegalArgumentException("No colors were found");

        int sumWithoutDominantAndWhite = 0;

        for (int i = 1; i < colorArea.size(); i++) {
            if (!colorArea.get(i).getKey().equals("white")) {
                sumWithoutDominantAndWhite = sumWithoutDominantAndWhite + colorArea.get(i).getValue();
            }
        }

//        if (colorArea.isEmpty()) throw new IllegalArgumentException("No colors were found");
//
//        int sumWithoutDominantAndWhite = 0;
//
//        for (int i = 1; i < colorArea.size(); i++) {
//            if (!colorArea.get(i).getKey().equals("white")) {
//                sumWithoutDominantAndWhite = sumWithoutDominantAndWhite + colorArea.get(i).getValue();
//            }
//        }

//        if (colorArea.get(0).getValue() >= 70) {
//            writeOriginalToPath(original, file, colorArea.get(0).getKey());
//        } else if (colorArea.get(0).getValue() >= 55 && !colorArea.get(0).getKey().equals("white")) {
//            writeOriginalToPath(original, file, colorArea.get(0).getKey());
//        } else if (colorArea.get(0).getValue() >= 55 && colorArea.get(0).getKey().equals("white")) {
//            if (colorArea.size() >=2 && colorArea.get(1).getValue() >= 45) {
//                writeOriginalToPath(original, file, colorArea.get(1).getKey());
//            } else if (colorArea.size() >=3 && colorArea.get(2).getValue() >= 45) {
//                if (DeltaE.deltaE2000())
//            }
//        }

        for (Integer index : counts.keySet()) {
            int x = (int) center.get(index, 2)[0];
            int y = (int) center.get(index, 1)[0];
            int z = (int) center.get(index, 0)[0];
            colorByLabel.add(new ColorAndPercents(index, new double[]{x, y, z}, counts.get(index) / sumOfPixels * 100));
        }

        Mat crop = original.submat(Mask.y, Mask.y + Mask.h, Mask.x, Mask.x + Mask.w);


//        if (first.getValue() >= 60 || (first.getValue() >= 55 && !first.getKey().equals("white"))) {
//            writeOriginalToPath(original, file, first.getKey());
//        } else if (first.getValue() >= 50 && (first.getKey().equals("black") || first.getKey().equals("grey"))) {
//            writeOriginalToPath(original, file, first.getKey());
//        } else if (first.getValue() >= 50 && !first.getKey().equals("white") && (second != null)) {
//            if (second.getKey().equals("white") || second.getValue() <= 20) {
//                writeOriginalToPath(original, file, first.getKey());
//            }
//        } else if (first.getValue() >= 20 &&
//                second != null && second.getValue() >= 20 &&
//                third != null && third.getValue() >= 20) {
//            writeOriginalToPath(original, file, "multi");
//        } else if (first.getValue() >= 40 && !first.getKey().equals("white")) {
//            if (second != null && second.getValue() >= 10 &&
//                    third != null && third.getValue() >= 10 &&
//                    fourth != null && fourth.getValue() >= 10 &&
//                    fifth != null && fifth.getValue() >= 10) {
//                writeOriginalToPath(original, file, "multi");
//            } else if (second != null && second.getValue() >= 20 && !second.getKey().equals("white")) {
//                writeOriginalToPath(original, file, "multi");
//            } else {
//                writeOriginalToPath(original, file, first.getKey());
//            }
//        } else if (first.getValue() >= 40) {
//            if (second != null && second.getValue() >= 10 &&
//                    third != null && third.getValue() >= 10 &&
//                    fourth != null && fourth.getValue() >= 10 &&
//                    fifth != null && fifth.getValue() >= 10) {
//                writeOriginalToPath(original, file, "multi");
//            } else if (second != null && second.getValue() >= 30) {
//                writeOriginalToPath(original, file, second.getKey());
//            } else {
//                writeOriginalToPath(original, file, "multi");
//            }
//        } else if (first.getValue() >= 30 && !first.getKey().equals("white")) {
//            if (second != null && second.getKey().equals("white") && second.getValue() >= 20) {
//                if (third != null && third.getValue() >= 15) {
//                    writeOriginalToPath(original, file, "multi");
//                } else {
//                    writeOriginalToPath(original, file, first.getKey());
//                }
//            }
//        } else if (colorByLabel.size() == 5) {
//            writeOriginalToPath(original, file, "multi");
//        } else {
//            writeOriginalToPath(original, file, "multi");
//        }

        Imgproc.cvtColor(cl, cl, Imgproc.COLOR_Lab2BGR);

        Mat cropCl = cl.submat(Mask.y, Mask.y + Mask.h, Mask.x, Mask.x + Mask.w);

        cropCl = checkAndResize(cropCl);

        crop = checkAndResize(crop);

        Mat colorExp = Mat.zeros(40, isHorizontal ? cropCl.width() * 2 : cropCl.width(), CvType.CV_8UC3);

        Collections.sort(colorByLabel);

        Iterator it = colorByLabel.iterator();

        int x = 0;
        int yd = colorExp.height();
        int xd = 0;
        while (it.hasNext()) {
            Mat colToBGR = Mat.zeros(1, 1, cl.type());
            ColorAndPercents cp = (ColorAndPercents) it.next();
            double[] labColors = new double[]{cp.getColorCode()[2], cp.getColorCode()[1], cp.getColorCode()[0]};
            Imgproc.rectangle(colToBGR, new Point(0, 0), new Point(1, 1), new Scalar(labColors), -1);
            Imgproc.cvtColor(colToBGR, colToBGR, Imgproc.COLOR_Lab2BGR);
            xd = x + (int) ((colorExp.width() / 100d) * cp.getPercent());
            double[] bgrColors = colToBGR.get(0, 0);
            if (!it.hasNext()) {
                xd = colorExp.width();
            }
            Imgproc.rectangle(colorExp, new Point(x, 0), new Point(xd, yd), new Scalar(bgrColors), -1);
            x = xd;
        }

//        imshow(cropCl, colorExp, "segmented");

//        imshow(colorExp, "Colour explanation");

//        showExpl(colorExp, colorByLabel);

        Imgproc.cvtColor(imageForSegmentation, imageForSegmentation, Imgproc.COLOR_Lab2BGR);

//        imshow(imageForSegmentation, colorExp, "segmented");

//        imshow(crop, colorExp, file.getName());

        imshow(crop, cropCl, colorExp, file.getName(), isHorizontal);

        cl = new Mat();
        center = new Mat();
        label = new Mat();
        counts = new HashMap<Integer, Integer>();
        colorByLabel = new ArrayList<ColorAndPercents>();

        return ImageShow.toBufferedImage(colorExp);
    }

    private Mat checkAndResize(final Mat imageForResizing) {

        Mat resized = new Mat();

        Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        double rectHeight = rect.getHeight();
        double rectWidth = rect.getWidth();
        double maxHeight = rectHeight * 0.80;
        double maxWidth = rectWidth * 0.90;

        int imWidth = imageForResizing.width();
        int imHeight = imageForResizing.height();

        if (rectWidth - (imWidth * 2) > rectHeight - (imHeight * 2)) {
            isHorizontal = true;
        }

        if ((!isHorizontal && maxHeight < imHeight * 2) ||
                (isHorizontal && maxWidth < imWidth * 2)) {
                if (!isHorizontal) {
                    if (maxHeight < imHeight * 2) {
                        double coefficient = maxHeight / (imHeight * 2);
                        int rWidth = (int) (imWidth * coefficient);
                        int rHeight = (int) (imHeight * coefficient);
                        Imgproc.resize(imageForResizing, resized, new Size(rWidth, rHeight));
                        imHeight = resized.height();
                        imWidth = resized.width();
                    }
                    if (maxWidth < imWidth) {
                        double coefficient = maxWidth / imWidth;
                        int rWidth = (int) (imWidth * coefficient);
                        int rHeight = (int) (imHeight * coefficient);
                        Imgproc.resize(imageForResizing, resized, new Size(rWidth, rHeight));
                    }
                } else {
                    if (maxHeight < imHeight) {
                        double coefficient = maxHeight / imHeight;
                        int rWidth = (int) (imWidth * coefficient);
                        int rHeight = (int) (imHeight * coefficient);
                        Imgproc.resize(imageForResizing, resized, new Size(rWidth, rHeight));
                        imHeight = resized.height();
                        imWidth = resized.width();
                    }
                    if (maxWidth < imWidth * 2) {
                        double coefficient = maxWidth / (imWidth * 2);
                        int rWidth = (int) (imWidth * coefficient);
                        int rHeight = (int) (imHeight * coefficient);
                        Imgproc.resize(imageForResizing, resized, new Size(rWidth, rHeight));
                    }
                }
            return resized;
        } else {
            return imageForResizing;
        }
    }

    private Mat getMatPreparedByNonZeroIndexes(Mat imageInARow, ArrayList<Integer> nonZeroIndexes) {
        Mat imagePreparedByMask = Mat.zeros(nonZeroIndexes.size(), 3, imageInARow.type());

        for (int j = 0; j < nonZeroIndexes.size(); j++) {
            imagePreparedByMask.put(j, 0, imageInARow.get(nonZeroIndexes.get(j), 0));
            imagePreparedByMask.put(j, 1, imageInARow.get(nonZeroIndexes.get(j), 1));
            imagePreparedByMask.put(j, 2, imageInARow.get(nonZeroIndexes.get(j), 2));
        }
        return imagePreparedByMask;
    }

    private ArrayList<Integer> getNonZeroIndexes(Mat imageInARow) {
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

    private void writeOriginalToPath(Mat original, File file, String nameOfDominant) {
        File path = new File(pathForSort.getAbsolutePath() + File.separator + nameOfDominant);
        if (!path.exists()) path.mkdirs();
        Imgcodecs.imwrite(path + File.separator + file.getName(), original);
    }

    public static List<Mat> cluster(Mat cutout, Mat mask, int k, ArrayList<Integer> integers) {
        Mat samples32f = new Mat();
        cutout.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
        label = labels;
        return showClusters(cutout, mask, labels, centers, integers);
    }

    private static List<Mat> showClusters(Mat cutout, Mat mask, Mat labels, Mat centers, ArrayList<Integer> integers) {
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
                if (nums > integers.get(integers.size() - 1) || nums < integers.get(index)) {
                    int ch1 = 0;
                    int ch2 = 128;
                    int ch3 = 128;
                    for (Mat mat : clusters) {
                        mat.put(y, x, ch1, ch2, ch3);
                        cluster.put(y, x, ch1, ch2, ch3);
                    }
                } else if (nums == integers.get(index)) {
                    int label = (int) labels.get(index, 0)[0];
                    int b = (int) centers.get(label, 0)[0];
                    int g = (int) centers.get(label, 1)[0];
                    int r = (int) centers.get(label, 2)[0];
                    clusters.get(label).put(y, x, b, g, r);
                    cluster.put(y, x, b, g, r);
                    index++;
                    counts.put(label, (counts.get(label) + 1));
                }
                nums++;
            }
        }

        center = centers;
        cl = cluster;

        return clusters;
    }

    class ColorCode {

        private String name;
        private double[] code;

        public ColorCode(String name, double[] code) {
            this.code = code;
            this.name = name;
        }

        public double[] getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

}