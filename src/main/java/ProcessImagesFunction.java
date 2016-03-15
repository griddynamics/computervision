import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import org.opencv.imgcodecs.Imgcodecs;
import processing.Cluster;
import processor.Product;
import scala.Tuple2;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by npakhomova on 3/12/16.
 */
public class ProcessImagesFunction implements PairFunction<Row, Integer, Product.Image> {
    private final String path;
    private final Categories category;

    public ProcessImagesFunction(String path, Categories category) {
        this.path = path;
        this.category = category;
    }

    @Override
    public Tuple2<Integer, Product.Image> call(Row row) throws Exception {
        Cluster cluster = new Cluster();
        Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
        String urlString = DataCollectionJobUtils.buildURL(image_id.intValue());
        File picture = DataCollectionJobUtils.downOrloadImage(urlString, path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);
        if (picture != null) {
            try {
                Cluster.ImageProcessingResult segmentationResult = cluster.segmentation(picture, false, category.getCategoryId());
                Map<String,Integer> segmentationMap = segmentationResult.getSortedByPercent();
                Imgcodecs.imwrite(picture.getParentFile() + "/procesed_" + segmentationResult.getName(), segmentationResult.getCropCl());
                Product.Image image = new Product.Image(image_id, segmentationMap, urlString);
                return new Tuple2(image_id, image);
            } catch (Exception ex) {
                System.out.println("unable to process " + urlString);
            }
        }

        return null;

    }
}
