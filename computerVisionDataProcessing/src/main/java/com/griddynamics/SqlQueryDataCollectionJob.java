package com.griddynamics;

import com.google.gson.Gson;
import com.griddynamics.computervision.Shapes;
import com.griddynamics.functions.ProcessImagesFunction;
import com.griddynamics.functions.ProcessRowToFlatProductUpcItemFunction;
import com.griddynamics.pojo.dataProcessing.FlatProductImageUpc;
import com.griddynamics.pojo.dataProcessing.Image;
import com.griddynamics.pojo.dataProcessing.Product;
import com.griddynamics.pojo.dataProcessing.Statistic;
import com.griddynamics.pojo.starsDomain.Categories;
import com.griddynamics.services.AttributeService;
import com.griddynamics.utils.DataCollectionJobUtils;
import com.griddynamics.utils.VisualRecognitionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;
import scala.Tuple2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public static final String SELECT_QUERY= "select distinct\n" +
            "  --   *\n" +
            "  PRODUCT_COLORWAY.PRODUCT_ID,\n" +
            "  PRODUCT_COLORWAY.DISPLAY_COLOR_NAME,\n" +
            "  PRODUCT_IMAGE.IMAGE_ID,\n" +
            "  UPC_FEATURE.COLOR_NORMAL_ID,\n" +
            "  UPC.UPC_ID,\n" +
            "  CATEGORY.CATEGORY_ID,\n" +
            "  CATEGORY.CATEGORY_NAME,\n" +
            "  PRODUCT.PRODUCT_DESC,\n" +
            "  UPC.UPC_DESC,\n" +
            "  mod(PRODUCT_COLORWAY.PRODUCT_ID, %d) AS ID_MOD \n"+

            "from PRODUCT_COLORWAY\n" +
            "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            "  join PRODUCT_IMAGE on PRODUCT_IMAGE.PRODUCT_IMAGE_ID = PRODUCT_COLORWAY_IMAGE.PRODUCT_IMAGE_ID and PRODUCT_IMAGE.IMAGE_ATTRIBUTE_TYPE = 'PRODUCT_PORTRAIT_IMAGE'\n" +
            "  join UPC on UPC.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
            "  join UPC_FEATURE on UPC_FEATURE.UPC_ID = UPC.UPC_ID\n" +
            "  join PRODUCT on PRODUCT.PRODUCT_ID = PRODUCT_IMAGE.PRODUCT_ID\n" +
            "  join CATEGORY on CATEGORY.CATEGORY_ID= PRODUCT.CATEGORY_ID\n" +
            "  JOIN PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT_COLORWAY.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n"+
            "WHERE PRODUCT_IMAGE.IMAGE_ID is not null and PRODUCT.STATE_ID = 2 and CATEGORY.CATEGORY_ID in (%s) and ROWNUM <=%d";


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
        options.put("url", "jdbc:oracle:thin:@//dml1-scan.federated.fds:1521/dpmstg01");

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));

        final int processedRowPerCategory = 1000;


        //createRootFolderAndCategorySubFolders
        createRootFolderAndCategorySubFolders();
        writeToJson(ROOT_FOLDER+"categories.json", gson.toJson(Categories.values()));


//        //ALARM!!! REMOVE FOLDER WITH PREVISOUR RESULT
        for (final Categories category : Categories.values()) {
            DataCollectionJobUtils.checkFolderExistance(ROOT_FOLDER + category.name());
        }

        for (final Categories category : Categories.values()){
            final String path = ROOT_FOLDER +category.name();

            String query = String.format(SELECT_QUERY,partitions, category.getCategoriesList(), processedRowPerCategory);
            DataFrame selectDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" +query + ")").load();
            selectDataFrame.cache();

            // save it to be able to rerun without connection to DB
            if (debugMode){
                selectDataFrame.toJSON().saveAsTextFile(path + "/joinOnProductsReturn");
                selectDataFrame.show(10);
            }


            //download all images and calculate computer vision result
            // this is done for IMAGE_ID distinct field, because images could be dublicated for different upc
            JavaPairRDD<Integer, Image> imagesRecognitionResult =
                    selectDataFrame.select("IMAGE_ID").distinct().toJavaRDD().
                            mapToPair(new ProcessImagesFunction(path, category)).filter(new Function<Tuple2<Integer, Image>, Boolean>() {
                // sometimes open cv fails, don't know why,  previous transformation just return null in this case
                public Boolean call(Tuple2<Integer, Image> v1) throws Exception {
                    return v1 != null;
                }
            });

            // save it to be able to rerun without connection to DB
            if(debugMode){
                imagesRecognitionResult.saveAsObjectFile(path + "/imageRecognitionResults");
            }



            //convert denormalized result to flat denormalized Pojo
            JavaPairRDD<Integer, FlatProductImageUpc> denormalizedPojo =
                    selectDataFrame.javaRDD().mapToPair(new ProcessRowToFlatProductUpcItemFunction(starsService))
                            .reduceByKey(new Function2<FlatProductImageUpc, FlatProductImageUpc, FlatProductImageUpc>() {
                                public FlatProductImageUpc call(FlatProductImageUpc v1, FlatProductImageUpc v2) throws Exception {
                                    // we don't need dublicated images.
                                    // in case if image are the same - color normal must be the same as well // todo check it?
                                    return v1;

                                }
                            });

            //process color to upc and set status
            JavaRDD<FlatProductImageUpc> productJavaRDD = denormalizedPojo.join(imagesRecognitionResult).map(new Function<Tuple2<Integer, Tuple2<FlatProductImageUpc, Image>>, FlatProductImageUpc>() {
                public FlatProductImageUpc call(Tuple2<Integer, Tuple2<FlatProductImageUpc, Image>> v1) throws Exception {
                    FlatProductImageUpc product = v1._2()._1();
                    Image image = v1._2()._2();
                    product.setShape(image.getShape());
                    product.setIdenticalShapes(isIdenticalShapes(product.getOriginalShape(), image.getShape()));
                    product.setComputerVisionResult(image.getComputerVisionResult());
                    product.setComputerVisionRecognition(VisualRecognitionUtil.evaluateRecognitionResult(product.getColorNormal(), image.getComputerVisionResult()));
                    product.setImageUrl(image.getUrl());
                    return product;
                }
            }).cache();

            Statistic statistic = calculateStatistic(category, productJavaRDD);

            // group upc/images with products together
            JavaRDD<Product> combinedProducts = productJavaRDD.mapToPair(new PairFunction<FlatProductImageUpc, Integer, FlatProductImageUpc>() {
                public Tuple2<Integer, FlatProductImageUpc> call(FlatProductImageUpc product) throws Exception {
                    return new Tuple2(product.getProductID(), product);
                }
            }).groupByKey().mapValues(new Function<Iterable<FlatProductImageUpc>, Product>() {
                public Product call(Iterable<FlatProductImageUpc> v1) throws Exception {
                    Iterator<FlatProductImageUpc> iterator = v1.iterator();
                    FlatProductImageUpc flatProduct = iterator.hasNext() ? iterator.next() : null;
                    Product product = new Product(flatProduct);
                    while (iterator.hasNext()) {
                        product.merge(new Product(iterator.next()));
                    }
                    return product;

                }
            }).values().cache();

            if (debugMode){
                combinedProducts.saveAsObjectFile(path + "/hierarchicalOutput");
            }


            //in real world this loads to OOM
            List<Product> result = combinedProducts.collect();



            try {
                //write converted json data to a file named "result.json"
                writeToJson(path + "/result.json", gson.toJson(result));

                //write converted json data to a file named "statistic.json"
                writeToJson(path+"/statistic.json", gson.toJson(statistic));

            } catch (IOException e) {
                e.printStackTrace();
            }


        }



    }

    private static void createRootFolderAndCategorySubFolders() throws IOException {
        File file = new File(ROOT_FOLDER);
        if (file.exists()){
            FileUtils.cleanDirectory(file);
            System.out.println("ALARM!!! remove working folder: "+ ROOT_FOLDER);
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

    private static Statistic calculateStatistic(Categories category, JavaRDD<FlatProductImageUpc> productJavaRDD) {
        // calculate some statistic
        long amountOfUpc = productJavaRDD.count();

        JavaRDD<FlatProductImageUpc> suspiciousItems = productJavaRDD.filter(new Function<FlatProductImageUpc, Boolean>() {
            public Boolean call(FlatProductImageUpc v1) throws Exception {
                return !v1.getComputerVisionRecognition().equals(0);
            }
        });
        long amountOfSuspiciousUpc = suspiciousItems.cache().count();
        // 1 COLOR_NORMAL is not MULTI and we have no dominant
        long amountOfSuspiciousMulti = suspiciousItems.filter(new Function<FlatProductImageUpc, Boolean>() {
            public Boolean call(FlatProductImageUpc v1) throws Exception {
                return v1.getComputerVisionRecognition().equals(1);
            }
        }).count();

        long amountOfColorNormalIsNoDominant = suspiciousItems.filter(new Function<FlatProductImageUpc, Boolean>() {
            public Boolean call(FlatProductImageUpc v1) throws Exception {
                 return v1.getComputerVisionRecognition().equals(2);
            }
        }).count();

        long amountOfColorNormalIsNotInList = suspiciousItems.filter(new Function<FlatProductImageUpc, Boolean>() {
            public Boolean call(FlatProductImageUpc v1) throws Exception {
                return v1.getComputerVisionRecognition().equals(3);
            }
        }).count();

        return new Statistic(category.name(),category.getCategoriesList(),
                amountOfUpc,
                amountOfSuspiciousUpc,
                amountOfSuspiciousMulti,
                amountOfColorNormalIsNoDominant,
                amountOfColorNormalIsNotInList);
    }

}
