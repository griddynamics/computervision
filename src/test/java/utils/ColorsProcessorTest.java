package utils;

import org.junit.Test;
import org.opencv.core.Core;
import pojo.ColorDescription;
import processing.ColorsProcessor;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by npakhomova on 3/16/16.
 */
public class ColorsProcessorTest {

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }


    public static final String JPG = "jpg";

    @Test
    public  void test1() throws IOException {
        File greenTshort = new File("/Users/npakhomova/codebase/css/computervisionSource/src/test/resources/GreenTshort.jpg");
        File pinkSmth = new File("/Users/npakhomova/codebase/css/computervisionSource/src/test/resources/PinkTShort.jpg");


//        final BufferedImage img = ImageIO.read(imageFile);

//        File preprocessedImage=  DataCollectionJobUtils.getTempFileByName(imageFile.getParentFile().getPath(), "decoded"+imageFile.getName(), img, "jpg");


//        File preprocessedImage = DataCollectionJobUtils.getTempFileByName(imageFile.getParentFile().getPath(), "GreenTshort" + "processed.jpg", ImageIO.read(imageFile), "jpg");

        TreeSet<ColorDescription> greenTshortrDescriptions = ColorsProcessor.getColorDescriptions(greenTshort);
        TreeSet<ColorDescription> pinkTshortrDescriptions = ColorsProcessor.getColorDescriptions(pinkSmth);

        assertEquals(3, greenTshortrDescriptions.size());
        //check dominant color
        assertEquals("green",greenTshortrDescriptions.iterator().next().getName());

        assertEquals(3, pinkTshortrDescriptions.size());
        //check dominant color
        assertEquals("pink",pinkTshortrDescriptions.iterator().next().getName());

        printColorDistance(greenTshortrDescriptions, "green");
        printColorDistance(pinkTshortrDescriptions,"pink");


//        double green = ColorsProcessor.countDistance("Green", greenTshort );

    }

    private void printColorDistance(TreeSet<ColorDescription> colorDescription, String color) {
        for (ColorDescription description : colorDescription){
            description.setDistanceFromColorNormal(Colors.CatalogPalette.get(color));
            System.out.println(description.getName() + " distance  " + description.getDistanceFromColorNormal() + " descriptionLAB " +description.getLabColorDesctiption());
        }
    }


}
