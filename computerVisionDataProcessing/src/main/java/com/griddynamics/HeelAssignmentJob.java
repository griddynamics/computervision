package com.griddynamics;

import com.google.gson.Gson;
import com.griddynamics.computervision.HeelRecognitionUtil;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by npakhomova on 4/20/16.
 */
public class HeelAssignmentJob {

    public static final Gson gson = new Gson();


    public static final String ROOT_FOLDER = "heelAssignment/";

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

        JavaSparkContext sparkContext = createSqlContext();
        SQLContext sqlContext = new SQLContext(sparkContext);

        Map<String, String> options = prepareOptions(sparkContext);
        createRootFolder();

        String sqlQuery = "select\n" +
                "  DISTINCT\n" +
                "  PRODUCT_IMAGE.PRODUCT_ID,\n" +
                "  PRODUCT_IMAGE.IMAGE_ID,\n" +
                "  mod(PRODUCT_IMAGE.PRODUCT_ID, %d) AS ID_MOD \n" +
                "from PRODUCT_IMAGE\n" +
                "join PRODUCT_ATTRIBUTE on PRODUCT_ATTRIBUTE.PRODUCT_ID =PRODUCT_IMAGE.PRODUCT_ID --and PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID != '422' --and PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Tall'\n" +
                "  join UPC on UPC.PRODUCT_ID = PRODUCT_IMAGE.PRODUCT_ID\n" +
                "  join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT on PRODUCT_IMAGE.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
                "  JOIN PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n" +
                "  join CATEGORY on CATEGORY.CATEGORY_ID = PRODUCT.CATEGORY_ID AND  CATEGORY.CATEGORY_ID = %d\n" +
                "where PRODUCT_IMAGE.PRODUCT_IMAGE_SEQUENCE = 0 and PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE = 'ADD'\n" +
                "      and PRODUCT_IMAGE.PRODUCT_ID not in (\n" +
                "  select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('1528')                                                                                                                                                                                                            --(PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Sneakers' )\n" +
                ")";


        for (final BCOMBootCategories category : BCOMBootCategories.values()) {
            final String path = ROOT_FOLDER + "/" + category.name();
            DataCollectionJobUtils.checkFolderExistance(path, false);
            String formatedQuery = String.format(sqlQuery, sparkContext.defaultParallelism(), category.categoryId);
            DataFrame selectPositiveDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + formatedQuery + ")").load();
            // download pictures

            List<HeelCandidateProduct> heelCandidateProducts = selectPositiveDataFrame.toJavaRDD().map(new Function<Row, HeelCandidateProduct>() {
                @Override
                public HeelCandidateProduct call(Row v1) throws Exception {
                    Integer image_id = v1.<BigDecimal>getAs("IMAGE_ID").intValue();
                    Integer product_id = v1.<BigDecimal>getAs("PRODUCT_ID").intValue();
                    String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), ImageRoleType.CPRI.getSuffix());


                    File picture = DataCollectionJobUtils.downOrloadImage(urlString, path);
                    if (picture != null) {
                        URL resource = this.getClass().getClassLoader().getResource("cascades/cascade.xml");
                        boolean highHeelByHaar = HeelRecognitionUtil.isHighHeelByHaar(picture, resource.getPath());
                        return new HeelCandidateProduct(product_id, urlString, highHeelByHaar);
                    }
                    return null;



                }
            }).filter(new Function<HeelCandidateProduct, Boolean>() {
                @Override
                public Boolean call(HeelCandidateProduct v1) throws Exception {
                    return v1 != null;
                }
            }).collect();
            try {
                //write converted json data to a file named "result.json"
                SqlQueryDataCollectionJob.writeToJson(path+ "/resutl.json", gson.toJson(heelCandidateProducts));


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
//        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01"); //mcom
        options.put("url", "jdbc:oracle:thin:@//dml1-scan:1521/bpmstg01"); //bcom
        //jdbc:oracle:thin:@//dml1-scan:1521/bpmstg01
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));
        return options;
    }

    private static class HeelCandidateProduct implements Serializable {
        Integer productId;
        String url;
        boolean containsHeel;

        public HeelCandidateProduct(Integer productId, String url, boolean containsHeel) {
            this.productId = productId;
            this.url = url;
            this.containsHeel = containsHeel;
        }
    }

    private enum MCOMBootCategories {
        //    "Finish Line Athletic Shoes",63268
        Boots(25122),
        Sneakers(26499),
        Slippers(16108),
        Flats(50295),
        Sandals(17570),
        Pumps(26481),
        Shoes(13247);

        private int categoryId;

        MCOMBootCategories(int categoryId) {

            this.categoryId = categoryId;
        }

        public int getCategoryId() {
            return categoryId;
        }
    }

    private enum BCOMBootCategories{

        Shoes(16961),
        Salvatore_Ferragamo_Women_Shoes(19210);

        public int getCategoryId() {
            return categoryId;
        }

        private int categoryId;

        BCOMBootCategories(int categoryId) {

            this.categoryId = categoryId;
        }
    }
}
