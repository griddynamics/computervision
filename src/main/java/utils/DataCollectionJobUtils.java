package utils;

import job.SqlQueryDataCollectionJob;
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

    public static final String SUFFIX_SMALL = "214x261.jpg";
    public static final String SUFFIX_BIG = "3000x3000.jpg"; // to long to process
    public static String STARS_SERVICE_PREFIX = "https://stars.macys.com/preview";

    static {

        // this is some magic that makes download from https works  just googled and copypasted
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

    public static File getTempFileByName(String path, String fileName, BufferedImage img, String imageFormat) throws IOException {
        ImageIO.write(img, imageFormat, new File(path, fileName));
        return new File(path, fileName).isFile() ? new File(path, fileName) : null;
    }

    public static File downOrloadImage(String urlString, String folder) {

        try {
            return getFileAndDownload(urlString, folder);
        } catch (Exception e) {
            System.out.println("Incorrect unable to download image " + urlString);
        }


        return null;
    }

    private static File getFileAndDownload(String urlString, String folder) {
        try {
            String[] splittedUrl = urlString.split("/");
            String fileName = splittedUrl[splittedUrl.length - 1];
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
//            connection.setRequestProperty(
//                    "User-Agent",
//                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            String mimeType = connection.getContentType();
            final BufferedImage img = ImageIO.read(connection.getInputStream());
            if (img != null) {
                return getTempFileByName(folder, fileName, img, "jpg");
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
        for (int i = 1; i < 9; i++) {

            builder.append(imageId % (int) Math.pow(10, i) / (int) Math.pow(10, i - 1));
            if ((i % 2) == 0) {
                builder.append("/");
            }
        }
        return STARS_SERVICE_PREFIX + String.format("%sfinal/%d-" + SUFFIX_SMALL, builder.reverse().toString(), imageId);
    }

    public static void checkFolderExistance(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
            System.out.println("ALARM!!! remove working folder: " + path);
            FileUtils.deleteDirectory(file);
        }
        file.mkdir();
        File downloads = new File(path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);
        downloads.mkdir();


        System.out.println("Working Folder: " + file.getAbsolutePath());
    }
}
