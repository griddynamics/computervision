import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 3/7/16.
 */
public class DataCollectionJob {
    // THIS iS loNG JOB, written not in optimal way. takes a lot of time. do nit rerun without necessity


    public static final String DATA_COLLECTION_JON_OUTPUT_FOLDER = "dataCollectionJonOutput";

    public static void main(String[] args) throws Exception {
        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("DataCollectionJob");
        JavaSparkContext context = new JavaSparkContext(config);
        SQLContext sqlContext = new SQLContext(context);
        Map<String, String> options = new HashMap<>();
        options.put("driver", "oracle.jdbc.OracleDriver");
        options.put("user", "macys");
        options.put("password", "macys");
        options.put("url", "jdbc:oracle:thin:@//mdc2vr4230.federated.fds:1521/starsdev");

        //ALARM!!! REMOVE FOLDER WITH PREVISOUR RESULT
        DataCollectionJobUtils.checkFolderExistance();


        sqlContext.read().format("jdbc").options(options).option("dbtable", "PRODUCT_COLORWAY").load()
                .select("PRODUCT_ID", "DISPLAY_COLOR_NAME", "PRODUCT_COLORWAY_ID")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/productColorway");
//        productColorway.show(10);
        sqlContext.read().format("jdbc").options(options).option("dbtable", "PRODUCT_COLORWAY_IMAGE").load()
                .select("PRODUCT_COLORWAY_ID", "PRODUCT_IMAGE_ID")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/productColorwayImage");
        sqlContext.read().format("jdbc").options(options).option("dbtable", "PRODUCT_IMAGE").load()
                .select("IMAGE_ID", "PRODUCT_IMAGE_ID", "IMAGE_ATTRIBUTE_TYPE").filter("IMAGE_ATTRIBUTE_TYPE = 'PRODUCT_PORTRAIT_IMAGE'").filter("IMAGE_ID is not null")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/productImage");
        sqlContext.read().format("jdbc").options(options).option("dbtable", "UPC").load()
                .select("PRODUCT_COLORWAY_ID", "UPC_ID", "UPC_DESC")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/upc");
        sqlContext.read().format("jdbc").options(options).option("dbtable", "UPC_FEATURE").load()
                .select("COLOR_NORMAL_ID", "UPC_ID")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/upcFeature");
        sqlContext.read().format("jdbc").options(options).option("dbtable", "PRODUCT").load()
                .select("PRODUCT_DESC", "PRODUCT_ID", "CATEGORY_ID")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/product");
        sqlContext.read().format("jdbc").options(options).option("dbtable", "CATEGORY").load()
                .select("CATEGORY_ID", "CATEGORY_NAME")
                .toJSON().saveAsTextFile(DATA_COLLECTION_JON_OUTPUT_FOLDER + "/category");
    }

}

