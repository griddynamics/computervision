package job;

import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import pojo.Image;
import scala.Tuple2;
import pojo.ColorDescription;
import processing.ColorsProcessor;
import utils.DataCollectionJobUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.TreeSet;


/**
 * Created by npakhomova on 3/12/16.
 */
public class ProcessImagesFunction implements PairFunction<Row, Integer, Image> {
    private final String path;
    private final Categories category;

    public ProcessImagesFunction(String path, Categories category) {
        this.path = path;
        this.category = category;
    }

    @Override
    public Tuple2<Integer, Image> call(Row row) throws Exception {
//        Cluster cluster = new Cluster();
        Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
        String urlString = DataCollectionJobUtils.buildURL(image_id.intValue());
        File picture = DataCollectionJobUtils.downOrloadImage(urlString, path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);
        if (picture != null) {
            try {
                TreeSet<ColorDescription> colorDescriptions = ColorsProcessor.getColorDescriptions(picture);
//                Imgcodecs.imwrite(picture.getParentFile() + "/procesed_" + segmentationResult.getName(), segmentationResult.getCropCl());
                Image image = new Image(image_id, colorDescriptions, urlString);
                return new Tuple2(image_id, image);
            } catch (Exception ex) {
                System.out.println("unable to process " + urlString);
            }
        }

        return null;

    }
}
