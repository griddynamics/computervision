package com.griddynamics;

import com.google.gson.Gson;
import com.griddynamics.computervision.HeelHeightBCOMValue;
import com.griddynamics.computervision.HeelHeightMCOMValue;
import com.griddynamics.pojo.dataProcessing.HeightHeelProductRecognition;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by npakhomova on 4/15/16.
 */
public class HeelRecognitionPicturesJob {
    public static final Gson gson = new Gson();


    public static final String ROOT_FOLDER = "heightHellPictures/";

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {


        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("SqlQueryDataCollectionJob");
        JavaSparkContext sparkContext = new JavaSparkContext(config);
        SQLContext sqlContext = new SQLContext(sparkContext);
        Map<String, String> options = new HashMap<String, String>();

        int partitions = sparkContext.defaultParallelism();
        options.put("driver", "oracle.jdbc.OracleDriver");
        options.put("user", "macys");
        options.put("password", "macys");
        //jdbc:oracle:thin:@mdc2vr4230:1521/starsdev - 1% database
//        options.put("url", "jdbc:oracle:thin:@//mdc2vr4230:1521/starsdev"); //mcom
//        options.put("url", "jdbc:oracle:thin:@//mdc2vr4230:1521/starsdev"); //mcom
//        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01"); //mcom
        options.put("url", "jdbc:oracle:thin:@//dml1-scan:1521/bpmstg01"); //bcom
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));

        //createRootFolder
        createRootFolder();

        final String query = "select distinct\n" +
                "\n" +
                "   PRODUCT.PRODUCT_ID,\n" +
                "   PRODUCT.PRODUCT_DESC,\n" +
                "   PRODUCT_COLORWAY_IMAGE.COLORWAY_IMAGE_ROLE_TYPE,\n" +
                "  PRODUCT_IMAGE.IMAGE_ID,\n" +
                "  PRODUCT_ATTRIBUTE.VARCHAR_VALUE,\n" +
                "  PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE, \n" +
                "  mod(PRODUCT.PRODUCT_ID, %d) AS ID_MOD \n" +
                "\n" +
                "from PRODUCT\n" +
                "join UPC on UPC.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
                "join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
                "join CATEGORY on CATEGORY.CATEGORY_ID= PRODUCT.CATEGORY_ID\n" +
                "join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID\n  and PRODUCT_IMAGE.PRODUCT_IMAGE_SEQUENCE = 0 \n" +
                "JOIN PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n" +
                "join CATEGORY on CATEGORY.CATEGORY_ID = PRODUCT.CATEGORY_ID\n" +
                "join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID and UPC_FEATURE.COLOR_NORMAL_ID is not null\n" +
                "join PRODUCT_ATTRIBUTE on   PRODUCT.PRODUCT_ID = PRODUCT_ATTRIBUTE.PRODUCT_ID and PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='1528'\n" +
                "where PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE ='ADD' and  PRODUCT_ATTRIBUTE.VARCHAR_VALUE like '%s' ";

        for (final HeelHeightBCOMValue attributeValue : HeelHeightBCOMValue.values()) {
            String queryString = String.format(query, partitions, attributeValue.getValue());
            // todo do it in buildes
            if (attributeValue.getExclusionCondition()!=null){
                queryString = queryString + attributeValue.getExclusionCondition();
            }
            System.out.println(queryString);

            final String path = ROOT_FOLDER + attributeValue.name();
            DataCollectionJobUtils.checkFolderExistance(path);

            DataFrame selectDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + queryString + ")").load();
            List<HeightHeelProductRecognition> result = selectDataFrame.distinct().toJavaRDD().mapToPair(new PairFunction<Row, Integer, HeightHeelProductRecognition>() {
                @Override
                public Tuple2<Integer, HeightHeelProductRecognition> call(Row v1) throws Exception {
                    HeightHeelProductRecognition result = new HeightHeelProductRecognition();
                    Integer image_id = v1.<BigDecimal>getAs("IMAGE_ID").intValue();
                    ImageRoleType imageRoleType = ImageRoleType.valueOf(v1.<String>getAs("COLORWAY_IMAGE_ROLE_TYPE"));
                    String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), imageRoleType.getSuffix());
                    result.setImageURL(urlString);
                    result.setProductDescription(v1.<String>getAs("PRODUCT_DESC"));
                    result.setProductId(v1.<BigDecimal>getAs("PRODUCT_ID").intValue());
                    result.setOriginalHeelAttributeValue(HeelHeightBCOMValue.getEnum(v1.<String>getAs("VARCHAR_VALUE")));

                    File picture = DataCollectionJobUtils.downOrloadImage(urlString, path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);

                    result.setPassedRecognition(attributeValue.getAttributeStrategy().doesApply(picture));
                    return new Tuple2<Integer, HeightHeelProductRecognition>(result.getProductId(), result);
                }
            }).reduceByKey(new Function2<HeightHeelProductRecognition, HeightHeelProductRecognition, HeightHeelProductRecognition>() {
                @Override
                public HeightHeelProductRecognition call(HeightHeelProductRecognition v1, HeightHeelProductRecognition v2) throws Exception {
                    return v1;
                }
            }).values().collect();
            try {
                //write converted json data to a file named "result.json"
                SqlQueryDataCollectionJob.writeToJson(path + "/result.json", gson.toJson(result));


            } catch (IOException e) {
                e.printStackTrace();
            }



        }

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


}
