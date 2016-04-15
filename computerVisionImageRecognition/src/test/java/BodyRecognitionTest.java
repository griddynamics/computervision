import com.griddynamics.computervision.BodyRecognition;
import com.griddynamics.computervision.BodyRecognitionResult;
import com.griddynamics.computervision.DataCollectionJobUtils;
import com.griddynamics.computervision.ImageShow;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by npakhomova on 4/13/16.
 */
public class BodyRecognitionTest {

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
    public void bodyDetection() throws IOException {
        String face = "https://stars.macys.com/preview/01/82/24/69/final/1822469-214x261.jpg";

        //dood dresses
        String dress = "https://stars.macys.com/preview/03/08/77/28/final/f6e235f96dc04e18a4332439991ea8088e25fc8b-350x350.jpg";
        String dress8 ="http://slimages.macysassets.com/is/image/MCY/products/9/optimized/3474859_fpx.tif";
        String dress5="http://slimages.macysassets.com/is/image/MCY/products/2/optimized/1529102_fpx.tif";

//        String dresses3 ="https://stars.macys.com/preview/02/28/04/64/final/2280464-214x261.jpg";
// knees only
        String dress7="http://slimages.macysassets.com/is/image/MCY/products/5/optimized/3004705_fpx.tif";


        // long dress without face
        String dress6="http://slimages.macysassets.com/is/image/MCY/products/9/optimized/3006479_fpx.tif";
        //bad dresses
        String dress2 ="https://stars.macys.com/preview/03/10/43/90/final/3104390-214x261.jpg";
        File imageFile = DataCollectionJobUtils.downOrloadImage(dress, new File(".").getCanonicalPath());


//        BodyRecognitionResult bodyRecognitionResult = BodyRecognition.detectFace(imageFile);
        BodyRecognition.detectBody(imageFile);
//        ImageShow.imshow(bodyRecognitionResult.getBoundedPart(), "image");

    }

    @Test
    @Ignore
    public void faceDetection() throws IOException {
        String face = "https://stars.macys.com/preview/01/82/24/69/final/1822469-214x261.jpg";
        String face2 ="https://stars.macys.com/preview/01/93/94/29/final/1939429-214x261.jpg";
//        String dress = "https://stars.macys.com/preview/03/08/77/28/final/f6e235f96dc04e18a4332439991ea8088e25fc8b-350x350.jpg";
//        String dress2 ="https://stars.macys.com/preview/03/10/43/90/final/3104390-214x261.jpg";
        File imageFile = DataCollectionJobUtils.downOrloadImage(face, new File(".").getCanonicalPath());


        BodyRecognitionResult bodyRecognitionResult = BodyRecognition.detectFace(imageFile);
//        BodyRecognition.detectBody(imageFile);
        ImageShow.imshow(bodyRecognitionResult.getBoundedPart(), "image");

    }

    private File getImageFile(String fileName) throws IOException {
        InputStream resourceAsStream = BodyRecognitionTest.class.getClassLoader().getResourceAsStream(fileName);

        BufferedImage read = ImageIO.read(resourceAsStream);
        String testPicture = "testPicture";
        ImageIO.write(read, "jpg", new File(testPicture));
        return new File(testPicture).isFile() ? new File(testPicture) : null;
    }

}
