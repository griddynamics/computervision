import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 3/8/16.
 */
public class DataMakeJoinJob {


    public static final int CATEGORY_ID = 65563; // this is category for casual shirts

    public static void main(String[] args) throws Exception {
        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("DataMakeJoinJob");
        JavaSparkContext context = new JavaSparkContext(config);
        SQLContext sqlContext = new SQLContext(context);

        DataFrame productColorway = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/productColorway");
        DataFrame productColorwayImage = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/productColorwayImage");
        DataFrame productImage = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/productImage");
        DataFrame upc = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/upc");
        DataFrame upcFeature = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/upcFeature");
        DataFrame product = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/product");

//        DataFrame category = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/category").filter("CATEGORY_ID=" + CATEGORY_ID);
        DataFrame category = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER +"/category").filter("CATEGORY_NAME='Dresses'");

        FileUtils.deleteDirectory(new File(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER + "/queryJoinResult"));

                productColorway.join(productColorwayImage, "PRODUCT_COLORWAY_ID")
                        .join(productImage, "PRODUCT_IMAGE_ID")
                        .join(upc, "PRODUCT_COLORWAY_ID")
                        .join(upcFeature, "UPC_ID")
                        .join(product, "PRODUCT_ID")
                        .join(category, "CATEGORY_ID")
                        .toJSON().saveAsTextFile(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER + "/queryJoinResult");
    }
}
