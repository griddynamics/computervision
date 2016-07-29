package com.griddynamics;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.griddynamics.computervision.ColorDescription;
import com.griddynamics.computervision.ColorsRecognitionUtil;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.services.AttributeService;
import com.griddynamics.utils.DataCollectionJobUtils;
import com.griddynamics.utils.VisualRecognitionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by npakhomova on 7/25/16.
 */
public class CustomUpcColorRecognitionJob {

    public static final String ROOT_FOLDER = "CustomUpcColorRecognitionJob/";
    public static final String DOWNLOAD_IMAGES_FOLDER = "";
    public static final Gson gson = new Gson();
    public static final int COLOR_SIMILARITY_TRESHOLD = 11;

    public static String query = "select DISTINCT \n" +
           "PRODUCT_IMAGE.IMAGE_ID,\n" +
            "  UPC.UPC_ID,\n" +
            "  UPC_FEATURE.COLOR_NORMAL_ID, \n"+
            "PRODUCT_COLORWAY_IMAGE.COLORWAY_IMAGE_ROLE_TYPE, \n"+
            " mod(UPC.UPC_ID, %d) AS ID_MOD \n" +
            "FROM PRODUCT_COLORWAY\n" +
            "join PRODUCT_COLORWAY_IMAGE on  PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID \n" +
            "join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID  and PRODUCT_IMAGE.IMAGE_ID is not null\n" +
            "join UPC on UPC.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            "join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID\n"+
            "where UPC.UPC_CODE in (%s)";

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
        createRootFolder();

        final AttributeService starsService = new AttributeService("http://11.120.149.99:8888");

        SparkConf config = new SparkConf();
        config.setMaster("local");
        config.setAppName("CustomUpcLoadCheck");

        JavaSparkContext sparkContext = new JavaSparkContext(config);
        SQLContext sqlContext = new SQLContext(sparkContext);

        JavaRDD<String> javaRDD = sparkContext.textFile("/Users/npakhomova/codebase/css/computerVisionGDSource/computerVisionDataProcessing/src/main/resources/product_purchased_20160716.csv");
        JavaRDD<String> upcIdForProcessing = javaRDD.map(new Function<String, String>() {
            @Override
            public String call(String v1) throws Exception {
                String[] split = v1.split(",");
                return split[3];
            }
        }).distinct();
        String upc = Joiner.on(",").join(upcIdForProcessing.collect());
        System.out.println("We have unique upc: "+upc.length());

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


        String upcQuery = String.format(query, partitions, upc);
        DataFrame upcWithColors = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + upcQuery + ")").load();
        JavaRDD<Record> result = upcWithColors.toJavaRDD().map(new Function<Row, Record>() {

            @Override
            public Record call(Row row) throws Exception {
                Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
                Integer upc_id = row.<BigDecimal>getAs("UPC_ID").intValue();
                String roleType = row.<String>getAs("COLORWAY_IMAGE_ROLE_TYPE");
                String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), ImageRoleType.valueOf(roleType).getSuffix());
                File picture = DataCollectionJobUtils.downOrloadImage(urlString, ROOT_FOLDER + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);

                Record record = new Record(upc_id);


                if (picture != null) {
                    TreeSet<ColorDescription> colorDescriptions = ColorsRecognitionUtil.getColorDescriptions(picture, false);
                    record.setColordescription(colorDescriptions);

                }


                record.setImgaeUrls(urlString);

                BigDecimal color_normal_id = row.<BigDecimal>getAs("COLOR_NORMAL_ID");
                if (color_normal_id != null) {
                    record.setColorNormal(starsService.getNormalColorById(color_normal_id));
                }

                record.setRecognitionResult(VisualRecognitionUtil.evaluateRecognitionResult(record.colorNormal, record.colordescription));

                return record;
            }
        });
        System.out.println(result.collect().size());

        writeToJson(ROOT_FOLDER + "/result.json", gson.toJson(result.collect()));


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

    public static void writeToJson(String fileName, String str) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(str);
        writer.close();
    }

    private static class Record implements Serializable {
         Integer upc_id;
         TreeSet<ColorDescription> colordescription;
         String imgaeUrls;
         String colorNormal;
         Integer recognitionResult;

        public Record(Integer upc_id) {
            this.upc_id = upc_id;
        }

        public void setColordescription(TreeSet<ColorDescription> colordescription) {
            this.colordescription = colordescription;
        }

        public void setImgaeUrls(String imgaeUrls) {
            this.imgaeUrls = imgaeUrls;
        }

        public void setColorNormal(String colorNormal) {
            this.colorNormal = colorNormal;
        }

        public void setRecognitionResult(Integer recognitionResult) {
            this.recognitionResult = recognitionResult;
        }

        public Integer getRecognitionResult() {
            return recognitionResult;
        }
    }
}
