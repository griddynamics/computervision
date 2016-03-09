import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import processing.Cluster;
import processor.OutPutResult;
import scala.Tuple2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by npakhomova on 3/8/16.
 */
public class ProcessImagesJob {

    public static final String DOWNLOAD_IMAGES_FOLDER = DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER + "/downloadedImages";

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }



    public static void main(String[] args) throws Exception {
        final AttributeService starsService = new AttributeService("http://11.120.149.228:8888");
        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("ProcessImagesJob");
        JavaSparkContext context = new JavaSparkContext(config);
        SQLContext sqlContext = new SQLContext(context);

        DataFrame joinedData = sqlContext.read().json(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER+"/queryJoinResult");
        joinedData.show(10);
         createFolder(); // create downloaded images folder

       //convert denormalized products to pojo object
        JavaPairRDD<Double, OutPutResult> stringOutPutResultJavaPairRDD = joinedData.toJavaRDD().mapToPair(new PairFunction<Row, Double, OutPutResult>() {
            @Override
            public Tuple2<Double, OutPutResult> call(Row row) throws Exception {
                OutPutResult.Category category = new OutPutResult.Category();

                // todo aa this mess with custing. I WANT SCALA!!!
                category.setCategoryId(row.getAs("CATEGORY_ID"));
                category.setCategoryName((String)row.getAs("CATEGORY_NAME"));

                OutPutResult.Upc upc = new OutPutResult.Upc();
                upc.setColrNormalId(row.<Double>getAs("COLOR_NORMAL_ID"));
                upc.setDisplayColorName((String)row.getAs("DISPLAY_COLOR_NAME"));
                upc.setImageId(row.<Double>getAs("IMAGE_ID"));
                upc.setDescription((String) row.getAs("UPC_DESC"));
                upc.setUpcId(row.<Double>getAs("UPC_ID"));


                OutPutResult product = new OutPutResult();
                product.setProductID(row.<Double>getAs("PRODUCT_ID"));
                product.setProductDescription(row.<String>getAs("PRODUCT_DESC"));

                product.addUpc(upc);
                product.setCategory(category);
                return new Tuple2<Double, OutPutResult>(product.getProductID(), product);
            }
        });

//        // merge denormalized values together
        JavaPairRDD<Double, Iterable<OutPutResult>> stringIterableJavaPairRDD = stringOutPutResultJavaPairRDD.groupByKey();
        JavaPairRDD<Double, OutPutResult> bigDecimalOutPutResultJavaPairRDD = stringIterableJavaPairRDD.mapValues(new org.apache.spark.api.java.function.Function<Iterable<OutPutResult>, OutPutResult>() {
            @Override
            public OutPutResult call(Iterable<OutPutResult> v1) throws Exception {
                Iterator<OutPutResult> iterator = v1.iterator();
                OutPutResult result = iterator.hasNext() ? iterator.next() : null;
                while (iterator.hasNext()) {
                    OutPutResult next = iterator.next();
                    result.merge(next);
                }
                return result;
            }
        });

        // download nad process images
        JavaRDD<OutPutResult> values = bigDecimalOutPutResultJavaPairRDD.values().cache();
        System.out.println("Goint to process "+ values.count()+ " products");
        List<OutPutResult> collectedResult = values.map(new Function<OutPutResult, OutPutResult>() {
            @Override
            public OutPutResult call(OutPutResult outPutResult) throws Exception {
                Cluster cluster = new Cluster(); // local variable, really?
                for (OutPutResult.Upc upc : outPutResult.getUpcSet()) {

                    File image = DataCollectionJobUtils.downOrloadImage(DataCollectionJobUtils.buildURL(upc.getImageId().intValue()), DOWNLOAD_IMAGES_FOLDER);
                    if (image != null) {

                        Cluster.ImageProcessingResult segmentationResult = cluster.segmentation(image, false);
                        Map<String, Integer> segmentationMap = segmentationResult.getSortedByPercent();
                        upc.setComputerVisionResult(segmentationMap);
                        Imgcodecs.imwrite(image.getParentFile() + "/procesed_" + segmentationResult.getName(), segmentationResult.getCropCl());
                    }
                    if (upc.getColrNormalId() != null) {
                        upc.setColorNormal(starsService.getNormalColorById(BigDecimal.valueOf(upc.getColrNormalId()).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()));
                    }
                }
                return outPutResult;
            }
        }).collect(); //gather all collection on driver - OOM! tadadaaam

        Gson gson = new Gson();
        String json = gson.toJson(collectedResult);
//
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(DataCollectionJob.DATA_COLLECTION_JON_OUTPUT_FOLDER+"/result.json");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String createFolder() {
        String folder = DOWNLOAD_IMAGES_FOLDER;
        File file = new File(folder);
        if (!file.exists()){file.mkdir();}
        return folder;
    }
}
