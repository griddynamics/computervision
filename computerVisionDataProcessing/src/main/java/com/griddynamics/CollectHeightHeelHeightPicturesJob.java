package com.griddynamics;

import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 4/15/16.
 */
public class CollectHeightHeelHeightPicturesJob {


    public static final String ROOT_FOLDER = "heightHellPictures/";

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
        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01"); //mcom
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));

        //createRootFolder
        createRootFolder();

        final String query ="select\n" +
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
                "join PRODUCT_ATTRIBUTE on   PRODUCT.PRODUCT_ID = PRODUCT_ATTRIBUTE.PRODUCT_ID and PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='422'\n" +
                "where PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE ='ADD' and  PRODUCT_ATTRIBUTE.VARCHAR_VALUE like '%s'";

        for (HeelHeightValue value : HeelHeightValue.values()){
            String format = String.format(query,partitions, value.attrValue);
            final String path = ROOT_FOLDER + value;
            DataCollectionJobUtils.checkFolderExistance(path);

            DataFrame selectDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + format + ")").load();
            selectDataFrame.toJavaRDD().foreach(new VoidFunction<Row>() {
                @Override
                public void call(Row row) throws Exception {
                    Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
                    ImageRoleType imageRoleType = ImageRoleType.valueOf(row.<String>getAs("COLORWAY_IMAGE_ROLE_TYPE"));
                    String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), imageRoleType.getSuffix());
                    File picture = DataCollectionJobUtils.downOrloadImage(urlString, path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);

                }
            });



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

     static enum HeelHeightValue {

       Flat("Flat%"),
       High("High%"),
       Mid("Mid%"),
       Low("Low%"),
       Ultra_High("Ultra%");

         private String attrValue;

         HeelHeightValue(String mid) {

             this.attrValue = mid;
         }

         public String getAttrValue() {
             return attrValue;
         }
     }
}
