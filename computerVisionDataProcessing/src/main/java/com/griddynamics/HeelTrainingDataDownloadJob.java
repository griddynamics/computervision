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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 5/16/16.
 */
public class HeelTrainingDataDownloadJob {


    public static final Gson gson = new Gson();
    public static final Joiner joiner = Joiner.on("\n");


    public static final String ROOT_FOLDER = "shopByLidsCategoryMCOM/";

    public static void main(String[] args) throws IOException {

        JavaSparkContext sparkContext = createSqlContext();
        SQLContext sqlContext = new SQLContext(sparkContext);

        Map<String, String> options = prepareOptions(sparkContext);
        createRootFolder();

        String mcomSqlQuery = "select DISTINCT\n" +
                "  PRODUCT_COLORWAY.PRODUCT_ID,\n" +
                "  PRODUCT_COLORWAY.DISPLAY_COLOR_NAME,\n" +
                "  PRODUCT_IMAGE.IMAGE_ID,\n" +
                "  UPC_FEATURE.COLOR_NORMAL_ID, \n" +
                "  mod(PRODUCT_IMAGE.PRODUCT_ID, %d) AS ID_MOD \n" +
                "from PRODUCT_COLORWAY\n" +
                "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID and PRODUCT_IMAGE.IMAGE_ATTRIBUTE_TYPE = 'PRODUCT_PORTRAIT_IMAGE'\n" +
                "  join UPC on UPC.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID\n" +
                "  JOIN PRODUCT on   PRODUCT.PRODUCT_ID = PRODUCT_COLORWAY.PRODUCT_ID\n" +
                "join CATEGORY on CATEGORY.CATEGORY_ID= PRODUCT.CATEGORY_ID and CATEGORY.CATEGORY_NAME like '%%Shop By Lids%%'\n" +
                "where UPC_FEATURE.COLOR_NORMAL_ID = %d and ROWNUM<=200";

        String bcomSqlQuery ="select DISTINCT\n" +
                "  PRODUCT_COLORWAY.PRODUCT_ID,\n" +
                "  PRODUCT_COLORWAY.DISPLAY_COLOR_NAME,\n" +
                "  PRODUCT_IMAGE.IMAGE_ID,\n" +
                "  UPC_FEATURE.COLOR_NORMAL_ID,\n" +
                "  mod(PRODUCT_IMAGE.PRODUCT_ID, %d) AS ID_MOD \n" +
                "from PRODUCT_COLORWAY\n" +
                "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID and PRODUCT_IMAGE.IMAGE_ATTRIBUTE_TYPE = 'PRODUCT_PORTRAIT_IMAGE'\n" +
                "  join UPC on UPC.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID\n" +
                "  JOIN PRODUCT on   PRODUCT.PRODUCT_ID = PRODUCT_COLORWAY.PRODUCT_ID and PRODUCT.PRODUCT_DESC like '%%Dress%%' \n"+
                "  where UPC_FEATURE.COLOR_NORMAL_ID = %d and  ROWNUM <=1000";;


        for (final Colors color : Colors.values()) {
            final String path = ROOT_FOLDER + "/" + color.name();
            DataCollectionJobUtils.checkFolderExistance(path, false);
            String formatedQuery = String.format(mcomSqlQuery, sparkContext.defaultParallelism(), color.mcomColorId);
            DataFrame selectPositiveDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + formatedQuery + ")").load();
            // download pictures

            JavaRDD<ProductWithUrlAndColor> productWithUrlAndColorJavaRDD = selectPositiveDataFrame.toJavaRDD().map(new Function<Row, ProductWithUrlAndColor>() {
                @Override
                public ProductWithUrlAndColor call(Row row) throws Exception {
                    Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
                    String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), ImageRoleType.CPRI.getSuffix());
                    File picture = DataCollectionJobUtils.downOrloadImage(urlString, path);
                    ProductWithUrlAndColor result = new ProductWithUrlAndColor();
                    result.productId = row.<BigDecimal>getAs("PRODUCT_ID").intValue();
                    if (picture != null) {
                        result.imageName = picture.getAbsolutePath();
                    }
                    result.colorNormalId = row.<BigDecimal>getAs("COLOR_NORMAL_ID").intValue();
                    result.imageUrl = urlString;

                    return result;
                }
            }).filter(new Function<ProductWithUrlAndColor, Boolean>() {
                @Override
                public Boolean call(ProductWithUrlAndColor v1) throws Exception {
                    return v1.imageName != null;
                }
            });


            writeToJson(path + "product.json", gson.toJson(productWithUrlAndColorJavaRDD.collect()));


        }


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


    private enum MCOMBootCategories {
        //    "Finish Line Athletic Shoes",63268
        Boots(25122),
        //        Sneakers(26499),
//        Slippers(16108),
//        Flats(50295),
        Sandals(17570),
        Pumps(26481);
//        Shoes(13247);

        private int categoryId;

        MCOMBootCategories(int categoryId) {

            this.categoryId = categoryId;
        }

        public int getCategoryId() {
            return categoryId;
        }
    }
    private static class ProductWithUrlAndColor implements Serializable {
        Integer productId;
        String imageUrl;
        String imageName;
        int colorNormalId;

    }

    private enum Colors {

        Purple(4990,8851),
        Tan_Beige(4908, 8838),
        Black(4909,8839),
        Blue(4910,8840),
        Brown(4911,8841),
        Ivory_Cream(4912,8842),
        Gold(4913,8843),
        Green(4914,8844),
        Gray(4915,8845),
        Multi(4916,8846),
        Orange(4917,8847),
        Pink(4918,8848),
        Red(4991,8852),
        Silver(4992,8853),
        White(4993,8854),
        Yellow(4994,8855);

        public int getMcomColorId() {
            return mcomColorId;
        }

        public int getBcomColorId() {
            return bcomColorId;
        }

        private int mcomColorId;
        private int bcomColorId;

        Colors(int categoryId, int bcomColorId) {

            this.mcomColorId = categoryId;
            this.bcomColorId = bcomColorId;
        }
    }
}
