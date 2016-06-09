package com.griddynamics;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 6/7/16.
 */
public class WearToWorkDownloadTrainigSetJob {

    public static final Gson gson = new Gson();
    public static final Joiner joiner = Joiner.on("\n");


    public static final String ROOT_FOLDER = "wearToWorkJob2/";

    private static String query = "select distinct\n" +
            "  PRODUCT.PRODUCT_ID,\n" +
            "  PRODUCT.PRODUCT_DESC,\n" +
            "  PRODUCT.VENDOR_CODE,\n" +
            "  PRODUCT_IMAGE.IMAGE_ID,\n" +
            "\n" +
            "  PRODUCT_TYPE.PRODUCT_TYPE_NAME,\n" +
            "  PRODUCT_TYPE.SHORT_DESCRIPTION,\n" +
            "  PRODUCT_TYPE.PRODUCT_TYPE_DESCRIPTION,\n" +
            "  mod(PRODUCT_IMAGE.PRODUCT_ID, %d) AS ID_MOD \n" +
            "\n" +
            "from PRODUCT\n" +
           " join PRODUCT_TYPE on PRODUCT.PRODUCT_TYPE_ID = PRODUCT_TYPE.PRODUCT_TYPE_ID\n" +
            "  join UPC on UPC.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
            "  join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
            "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            "\n" +
            "  join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID "+
            "where CATEGORY_ID = 39096";

    private static String query2 = "select distinct\n" +
            "  PRODUCT.PRODUCT_ID,\n" +
            "  PRODUCT_IMAGE.IMAGE_ID,\n" +
            "\n" +
            "  PRODUCT_TYPE.PRODUCT_TYPE_NAME,\n" +
            "  PRODUCT.CATEGORY_ID,\n" +
            "  mod(PRODUCT_IMAGE.PRODUCT_ID,  %d) AS ID_MOD\n" +
            "\n" +
            "from PRODUCT\n" +
            "  join PRODUCT_TYPE on PRODUCT.PRODUCT_TYPE_ID = PRODUCT_TYPE.PRODUCT_TYPE_ID\n" +
            "  join PRODUCT_IMAGE  on PRODUCT_IMAGE.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT_IMAGE.IMAGE_ID is not null\n" +
            "where PRODUCT_TYPE.PRODUCT_TYPE_NAME='SUIT'";


    public static void main(String[] args) throws IOException {

        JavaSparkContext sparkContext = createSqlContext();
        SQLContext sqlContext = new SQLContext(sparkContext);

        Map<String, String> options = prepareOptions(sparkContext);
        createRootFolder();


            String formatedQuery = String.format(query2, sparkContext.defaultParallelism());
            DataFrame selectPositiveDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + formatedQuery + ")").load();
            // download pictures

            JavaRDD<DressesOnColorsPictureDownloadJob.ProductWithUrlAndColor> productWithUrlAndColorJavaRDD = selectPositiveDataFrame.toJavaRDD().map(new Function<Row, DressesOnColorsPictureDownloadJob.ProductWithUrlAndColor>() {
                @Override
                public DressesOnColorsPictureDownloadJob.ProductWithUrlAndColor call(Row row) throws Exception {
                    Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
                    DressesOnColorsPictureDownloadJob.ProductWithUrlAndColor result = new DressesOnColorsPictureDownloadJob.ProductWithUrlAndColor();
                    result.productType = row.<String>getAs("PRODUCT_TYPE_NAME");
                    int category_id = row.<BigDecimal>getAs("CATEGORY_ID").intValue();
                    final String path;
                    if (category_id == 39096){
                      path  = ROOT_FOLDER + "/wearToWorkSuit";
                    } else {
                        path =  ROOT_FOLDER + "/NoWearToWorkSuit";
                    }


                    DataCollectionJobUtils.checkFolderExistance(path, false);

                    String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), ImageRoleType.CPRI.getSuffix());
                    File picture = DataCollectionJobUtils.downOrloadImage(urlString, path);

                    result.productId = row.<BigDecimal>getAs("PRODUCT_ID").intValue();
                    if (picture != null) {
                        result.imageName = picture.getAbsolutePath();
                    }
//                    result.colorNormalId = row.<BigDecimal>getAs("COLOR_NORMAL_ID").intValue();
                    result.imageUrl = urlString;


                    return result;
                }
            });
            productWithUrlAndColorJavaRDD.collect();


//            writeToJson(path + "product.json", gson.toJson(productWithUrlAndColorJavaRDD.collect()));




    }

    public static void writeToJson(String fileName, String str) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(str);
        writer.close();
    }

    private static void createRootFolder() throws IOException {
        File file = new File(ROOT_FOLDER);
        if (file.exists()) {
            FileUtils.cleanDirectory(file);
            System.out.println("ALARM!!! remove working folder: " + ROOT_FOLDER);
        } else {
            file.mkdir();
        }

    }


    private static JavaSparkContext createSqlContext() {
        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName(HeelAssignmentJob.class.getSimpleName());
        JavaSparkContext sparkContext = new JavaSparkContext(config);

        return sparkContext;
    }

    private static Map<String, String> prepareOptions(JavaSparkContext sparkContext) {
        Map<String, String> options = new HashMap<String, String>();

        int partitions = sparkContext.defaultParallelism();
        options.put("driver", "oracle.jdbc.OracleDriver");
        options.put("user", "macys");
        options.put("password", "macys");
        //jdbc:oracle:thin:@mdc2vr4230:1521/starsdev - 1% database
//        options.put("url", "jdbc:oracle:thin:@//mdc2vr4230:1521/starsdev"); //mcom
        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01"); //mcom
//        options.put("url", "jdbc:oracle:thin:@//dml1-scan:1521/bpmstg01"); //bcom
        //jdbc:oracle:thin:@//dml1-scan:1521/bpmstg01
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));
        return options;
    }
}
