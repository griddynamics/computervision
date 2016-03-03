
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import ui.FileChooserDemo;

import java.io.File;

public class Launcher {

    static {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("opencv_java310");
    }

    public static void main(String[] args) {

        FileChooserDemo fc = new FileChooserDemo();
        fc.start();

        File file = new File("/Users/abelyakov/Development/pet/Sneakers/Blue/1643364_fpx.jpg");

//        File folder = new File(("/Users/abelyakov/Development/pet/Sneakers/Blue"));
//        File[] listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++) {
//
//            File file = listOfFiles[i];
//            file = new File("/Users/abelyakov/Development/pet/Sneakers/Blue/1643364_fpx.jpg");
//            if (file.isFile() && file.toString(endsWith(".jpg")) {
//                Mat image = Imgcodecs.imread(file.getAbsolutePath());
//                Mat original = image.clone();
//                Mat original2 = image.clone();
//                Mat mask = Mask.getMask(image);
//                Core.bitwise_and(original, mask, original);
//                new Cluster().segmentation(original, mask, original2, file);
//            }
//        }
    }
}
