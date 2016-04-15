package com.griddynamics;

import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import com.griddynamics.computervision.Shapes;
import com.griddynamics.functions.ProcessImagesFunction;
import com.griddynamics.functions.ProcessRowToFlatProductUpcItemFunction;
import com.griddynamics.functions.RowCategoryFunction;
import com.griddynamics.pojo.dataProcessing.Image;
import com.griddynamics.pojo.dataProcessing.Product;
import com.griddynamics.pojo.dataProcessing.Statistic;
import com.griddynamics.pojo.dataProcessing.Upc;
import com.griddynamics.pojo.starsDomain.Category;
import com.griddynamics.pojo.starsDomain.ICategory;
import com.griddynamics.services.AttributeService;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;
import scala.Tuple2;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by npakhomova on 3/9/16.
 */
public class SqlQueryDataCollectionJob {

    public static final String ROOT_FOLDER = "rootFolder/";
    public static final String DOWNLOAD_IMAGES_FOLDER = "/downloadedImages";
    public static final Gson gson = new Gson();
    public static final int COLOR_SIMILARITY_TRESHOLD = 11;

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }

    public static final String SELECT_CATEGORY_QUERY = "select distinct\n" +
            "\n" +
            " CATEGORY.CATEGORY_ID,\n" +
            " CATEGORY.CATEGORY_NAME, \n" +
            " mod(CATEGORY.CATEGORY_ID, %d) AS ID_MOD \n" +
            ""+
            "\n" +
            "\n" +
            "from PRODUCT\n" +
            " join UPC on UPC.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
            " join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
            " join CATEGORY on CATEGORY.CATEGORY_ID= PRODUCT.CATEGORY_ID\n" +
            " join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            " join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID\n" +
            " join PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n" +
            " join CATEGORY on CATEGORY.CATEGORY_ID = PRODUCT.CATEGORY_ID\n" +
            " join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID and UPC_FEATURE.COLOR_NORMAL_ID is not null\n" +
            " ";

    public static final String SELECT_QUERY = "select distinct\n" +
            "\n" +
            " PRODUCT.PRODUCT_ID,\n" +
            " PRODUCT.PRODUCT_DESC,\n"+
            " UPC.UPC_ID,\n" +
            " UPC.UPC_DESC,\n" +
            " UPC.COLOR_DESC,\n" +
            " PRODUCT_COLORWAY.DISPLAY_COLOR_NAME,\n" +
            " UPC_FEATURE.COLOR_NORMAL_ID,\n" +
            " PRODUCT_COLORWAY_IMAGE.COLORWAY_IMAGE_ROLE_TYPE,\n" +
            " PRODUCT_IMAGE.IMAGE_ID,\n" +
            " CATEGORY.CATEGORY_ID,\n" +
            " CATEGORY.CATEGORY_NAME, " +
            " mod(PRODUCT.PRODUCT_ID, %d) AS ID_MOD \n" +

            "from PRODUCT\n" +
            "join UPC on UPC.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
            "join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
            "join CATEGORY on CATEGORY.CATEGORY_ID= PRODUCT.CATEGORY_ID\n" +
            "join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            "join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID\n" +
            "JOIN PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n" +
            "join CATEGORY on CATEGORY.CATEGORY_ID = PRODUCT.CATEGORY_ID\n" +
            "join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID and UPC_FEATURE.COLOR_NORMAL_ID is not null \n"+
            "WHERE  CATEGORY.CATEGORY_ID in (%s)  ";


    public static void main(String[] args) throws IOException {

        boolean debugMode = false;
        final AttributeService starsService = new AttributeService("http://11.120.149.99:8888");

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
        options.put("url", "jdbc:oracle:thin:@//mdc2vr4230:1521/starsdev"); //mcom
//        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01"); //mcom
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));



        //createRootFolder
        createRootFolder();


        // get all categories:
        String categoryQuery = String.format(SELECT_CATEGORY_QUERY, partitions);
        DataFrame categories = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + categoryQuery + ")").load();
        categories.cache();
        Collection<Category> categoriesCollection = categories.toJavaRDD().map(new RowCategoryFunction()).collect();

        System.out.println("Going to process " + categoriesCollection.size() + " categories");


        for (final ICategory category : categoriesCollection) {
            final String path = ROOT_FOLDER + category.getCategoryName();
            DataCollectionJobUtils.checkFolderExistance(path);

            String productsQuery = String.format(SELECT_QUERY, partitions, category.getCategoriesJoinedString());
            DataFrame selectDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + productsQuery + ")").load();
            selectDataFrame.cache();

            // save it to be able to rerun without connection to DB
            if (debugMode) {
                selectDataFrame.toJSON().saveAsTextFile(path + "/joinOnProductsReturn");
                selectDataFrame.show(10);
            }


            //download all images and calculate computer vision result
            // this is done for IMAGE_ID distinct field, because images could be dublicated for different upc
            JavaPairRDD<Integer, Image> imagesRecognitionResult =
                    selectDataFrame.select("IMAGE_ID","COLORWAY_IMAGE_ROLE_TYPE").distinct().toJavaRDD().
                            mapToPair(new ProcessImagesFunction(path, category)).filter(new Function<Tuple2<Integer, Image>, Boolean>() {
                        // sometimes open cv fails, don't know why,  previous transformation just return null in this case
                        public Boolean call(Tuple2<Integer, Image> v1) throws Exception {
                            return v1 != null;
                        }
                    }).cache();

            // save it to be able to rerun without connection to DB
            if (debugMode) {
                imagesRecognitionResult.saveAsObjectFile(path + "/imageRecognitionResults");
            }

            JavaPairRDD<Integer, Upc> primaryPictureIdUpcRDD = selectDataFrame.select("UPC_ID", "IMAGE_ID", "COLOR_NORMAL_ID", "DISPLAY_COLOR_NAME").distinct().toJavaRDD()
                    .mapToPair(new PairFunction<Row, Integer, Upc>() {
                        @Override
                        public Tuple2<Integer, Upc> call(Row row) throws Exception {
                            Upc upc = new Upc();
                            upc.setUpcId(row.<BigDecimal>getAs("UPC_ID").intValue());

                            BigDecimal color_normal_id = row.<BigDecimal>getAs("COLOR_NORMAL_ID");
                            upc.setColorNormalId(color_normal_id != null ? color_normal_id.intValue() : null);

                            // set for color normal value
                            if (upc.getColorNormalId() != null) {
                                upc.setColorNormal(starsService.getNormalColorById(BigDecimal.valueOf(upc.getColorNormalId())));
                            }
                            upc.setDisplayColorName(row.<String>getAs("DISPLAY_COLOR_NAME"));


                            return new Tuple2(row.<BigDecimal>getAs("IMAGE_ID").intValue(), upc);
                        }
                    }).join(imagesRecognitionResult).mapValues(new Function<Tuple2<Upc, Image>, Upc>() {
                        @Override
                        public Upc call(Tuple2<Upc, Image> v1) throws Exception {
                            Upc upc = v1._1();
                            upc.addImage(v1._2());

                            return upc;
                        }
                    }).values().cache().mapToPair(new PairFunction<Upc, Integer, Upc>() {
                        @Override
                        public Tuple2<Integer, Upc> call(Upc upc) throws Exception {
                            return new Tuple2<Integer, Upc>(upc.getUpcId(), upc);
                        }
                    }).reduceByKey(new Function2<Upc, Upc, Upc>() {
                        @Override
                        public Upc call(Upc v1, Upc v2) throws Exception {
                            v1.merge(v2);
                            return v1;
                        }
                    }).values().mapToPair(new PairFunction<Upc, Integer, Upc>() {
                        @Override
                        public Tuple2<Integer, Upc> call(Upc upc) throws Exception {
                            return new Tuple2(upc.getPrimaryImageId(), upc);

                        }
                    }).reduceByKey(new Function2<Upc, Upc, Upc>() {
                        @Override
                        public Upc call(Upc v1, Upc v2) throws Exception {
                            // no need dublicated pictures
                            return v1;
                        }
                    }).cache();
            System.out.println("Size of upc is" + primaryPictureIdUpcRDD.values().collect().size());


            //convert denormalized result to flat denormalized Pojo
            //imageid on flatproduct
            //JavaPairRDD<Integer, FlatProductImageUpc> denormalizedPojo =
            JavaPairRDD<Integer, Product> mergedProducts = selectDataFrame.javaRDD().mapToPair(new ProcessRowToFlatProductUpcItemFunction(starsService))
                    .join(primaryPictureIdUpcRDD).mapValues(new Function<Tuple2<Product, Upc>, Product>() {
                        @Override
                        public Product call(Tuple2<Product, Upc> v1) throws Exception {
                            v1._1().addUpc(v1._2());
                            return v1._1();
                        }
                    }).values().mapToPair(new PairFunction<Product, Integer, Product>() {
                        @Override
                        public Tuple2<Integer, Product> call(Product product) throws Exception {
                            return new Tuple2(product.getProductID(), product);
                        }
                    }).groupByKey().mapValues(new Function<Iterable<Product>, Product>() {
                        @Override
                        public Product call(Iterable<Product> v1) throws Exception {
                            Iterator<Product> iterator = v1.iterator();
                            Product product = iterator.next();
                            while (iterator.hasNext()) {
                                product.merge(iterator.next());
                            }
                            return product;
                        }
                    }).cache();



            Statistic statistic = calculateStatistic(category, primaryPictureIdUpcRDD.values());


            //in real world this loads to OOM
            List<Product> result = mergedProducts.values().collect();


            try {
                //write converted json data to a file named "result.json"
                writeToJson(path + "/result.json", gson.toJson(result));

                //write converted json data to a file named "statistic.json"
                writeToJson(path + "/statistic.json", gson.toJson(statistic));

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

                writeToJson(ROOT_FOLDER + "categories.json", gson.toJson(Collections2.transform(categoriesCollection, new com.google.common.base.Function<Category, String>() {
                    @Nullable
                    @Override
                    public String apply(Category input) {
                        return input.getCategoryName();
                    }
                })));



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

    private static void writeToJson(String fileName, String str) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(str);
        writer.close();
    }

    private static boolean isIdenticalShapes(String originalShape, Shapes shape) {
        // comparison process
        return false;
    }

    private static Statistic calculateStatistic(ICategory category, JavaRDD<Upc> upc) {
        // calculate some statistic
        long amountOfUpc = upc.count();

        JavaRDD<Upc> suspiciousItems = upc.filter(new Function<Upc, Boolean>() {
            public Boolean call(Upc v1) throws Exception {
                return !v1.getComputerVisionRecognition().equals(0);
            }
        });
        long amountOfSuspiciousUpc = suspiciousItems.cache().count();
        // 1 COLOR_NORMAL is not MULTI and we have no dominant
        long amountOfSuspiciousMulti = suspiciousItems.filter(new Function<Upc, Boolean>() {
            public Boolean call(Upc v1) throws Exception {
                return v1.getComputerVisionRecognition().equals(1);
            }
        }).count();

        long amountOfColorNormalIsNoDominant = suspiciousItems.filter(new Function<Upc, Boolean>() {
            public Boolean call(Upc v1) throws Exception {
                return v1.getComputerVisionRecognition().equals(2);
            }
        }).count();

        long amountOfColorNormalIsNotInList = suspiciousItems.filter(new Function<Upc, Boolean>() {
            public Boolean call(Upc v1) throws Exception {
                return v1.getComputerVisionRecognition().equals(3);
            }
        }).count();

        return new Statistic(category.getCategoryName(), category.getCategoriesJoinedString(),
                amountOfUpc,
                amountOfSuspiciousUpc,
                amountOfSuspiciousMulti,
                amountOfColorNormalIsNoDominant,
                amountOfColorNormalIsNotInList);
    }

}
