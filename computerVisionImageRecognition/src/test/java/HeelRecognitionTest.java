import com.griddynamics.computervision.DataCollectionJobUtils;
import com.griddynamics.computervision.HeelRecognitionUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;

/**
 * Created by npakhomova on 4/18/16.
 */
public class HeelRecognitionTest {

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void haarTest() throws IOException {

        String cascadePath = "src/main/resources/cascades/cascade.xml";
        String flatHeel = "http://raymcompreviewprod/00/88/69/03/final/886903-214x261.jpg";
        String highHeel = "http://raymcompreviewprod/01/38/61/86/final/1386186-214x261.jpg";
        File flat = DataCollectionJobUtils.downOrloadImage(flatHeel, new File(".").getCanonicalPath());
        File high = DataCollectionJobUtils.downOrloadImage(highHeel, new File(".").getCanonicalPath());

        Assert.assertTrue(HeelRecognitionUtil.isHighHeelByHaar(high, cascadePath));
        Assert.assertFalse(HeelRecognitionUtil.isHighHeelByHaar(flat, cascadePath));
    }

    @Test
    @Ignore
    public void heelTestFalse() throws IOException {
        String flatHeel = "http://raymcompreviewprod/00/88/69/03/final/886903-214x261.jpg";
        File imageFile = DataCollectionJobUtils.downOrloadImage(flatHeel, new File(".").getCanonicalPath());


//        BodyRecognitionResult bodyRecognitionResult = BodyRecognition.detectFace(imageFile);
        System.out.println("Is Boot with heel " + HeelRecognitionUtil.defineHeelHeight(imageFile));


    }

    @Test
    @Ignore
    public void heelTestTrue() throws IOException {
        String heightHeel = "http://raymcompreviewprod/00/98/91/28/final/989128-214x261.jpg";
        File imageFile = DataCollectionJobUtils.downOrloadImage(heightHeel, new File(".").getCanonicalPath());


//        BodyRecognitionResult bodyRecognitionResult = BodyRecognition.detectFace(imageFile);
        System.out.println("Is Boot with heel " + HeelRecognitionUtil.defineHeelHeight(imageFile));


    }


    @Test
//    @Ignore
    public void heelHeighPlaform() throws IOException {
        String heightHeel = "http://raymcompreviewprod/03/32/70/69/final/3327069-214x261.jpg";
        File imageFile = DataCollectionJobUtils.downOrloadImage(heightHeel, new File(".").getCanonicalPath());


//        BodyRecognitionResult bodyRecognitionResult = BodyRecognition.detectFace(imageFile);
        System.out.println("Is Boot with heel " + HeelRecognitionUtil.defineHeelHeight(imageFile));


    }

}
