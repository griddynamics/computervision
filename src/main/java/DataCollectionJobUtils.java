import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by npakhomova on 3/7/16.
 */
public class DataCollectionJobUtils {

    public static final String SUFFIX = "214x261.jpg";
    public static String STARS_SERVICE_PREFIX ="https://stars.macys.com/preview";
    static {
        someMagicToMakeSshWorks();
    }

    public static File getTempFileByName(String path, String fileName, BufferedImage img, String imageFormat) throws IOException {
        File file;
        file = new File(path, fileName);
        ImageIO.write(img, imageFormat, file);
        return file.isFile() ? file : null;
    }

    public static File downOrloadImage(String urlString, String folder){
        File file = new File(folder, urlString + SUFFIX);
        if (file.exists() && file.isFile()){
            System.out.println("File already exist, No Download Necessary for " + urlString);
            return file;
        }


        try {
                 return getFileAndDownload(urlString+ SUFFIX, folder);
            } catch (Exception e){
                System.out.println("Incorrect unable to download image " +urlString);
        }



        return null;
    }

    private static void someMagicToMakeSshWorks() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static File getFileAndDownload(String urlString, String folder) {
        try {
            String[] splittedUrl = urlString.split("/");
            String fileName = splittedUrl[splittedUrl.length - 1];
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            String mimeType = connection.getContentType();
            final BufferedImage img = ImageIO.read(connection.getInputStream());
            if (img != null) {
//                String folder = "downloaded";
                return getTempFileByName(folder,fileName, img, "jpg");
            } else {
                System.out.println("Incorrect URL of image. Mime type:" + mimeType);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
    }


    public static String buildURL(int imageId) {
        StringBuilder builder = new StringBuilder("/");
        for(int i=1; i<9; i++){

            builder.append(imageId % (int)Math.pow(10,i) /(int) Math.pow(10,i-1));
            if ((i % 2) == 0){
                builder.append("/");
            }
        }
        return STARS_SERVICE_PREFIX+String.format("%sfinal/%d-",builder.reverse().toString(),imageId);
    }

    static void checkFolderExistance() throws IOException {
        File file = new File(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER);
        if (file.exists()){
            System.out.println("ALARM!!! remove working folder: " + DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER);
            FileUtils.deleteDirectory(file);
        } else {
            file.mkdir();
            System.out.println("Working Folder: "+ file.getAbsolutePath());
        }
    }
}
