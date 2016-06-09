package com.griddynamics;

import com.griddynamics.attributes.SanDalTypeBootAttribute;
import com.griddynamics.computervision.Preparation;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 4/19/16.
 */
public class AttributesNeuralNetworkTrainingJob {

    public static final String ROOT_FOLDER = "heelsInputDataSetTraining/";
//
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

        processAttribute(sparkContext, sqlContext, options, SanDalTypeBootAttribute.TallAttribute);




    }

    private static void processAttribute(JavaSparkContext sparkContext, SQLContext sqlContext, Map<String, String> options, SanDalTypeBootAttribute attribute) throws IOException {

        attribute.createAllFolders(ROOT_FOLDER);

        String getPositiveSqlQuery = attribute.getPositiveGetSqlQuery(sparkContext.defaultParallelism());
        DataFrame selectPositiveDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + getPositiveSqlQuery + ")").load();
        // download pictures

        selectPositiveDataFrame.foreach(attribute.getPicturesDownloadFunction(ROOT_FOLDER,"positive" ));


        String getNegativeSqlQuery = attribute.getNegativeSqlQuery(sparkContext.defaultParallelism());
        DataFrame selectNegativeDataFrame = sqlContext.read().format("jdbc").options(options).option("dbtable", "(" + getNegativeSqlQuery + ")").load();
        // download pictures

        selectNegativeDataFrame.foreach(attribute.getPicturesDownloadFunction(ROOT_FOLDER, "negative" ));
        // todo do it not in driver. do it before downloading
        Preparation.prepare(attribute.getDownloadedImagesPath(ROOT_FOLDER, "positive"), attribute.getResizedImagesPath(ROOT_FOLDER,"positive"), true);
        Preparation.prepare(attribute.getDownloadedImagesPath(ROOT_FOLDER, "negative"), attribute.getResizedImagesPath(ROOT_FOLDER,"negative"), false);


        File positive = new File(attribute.getAttributeContexFolder(ROOT_FOLDER)+"/positive.dat");
        File negative = new File(attribute.getAttributeContexFolder(ROOT_FOLDER)+"/negative.dat");

        File positiveSource = new File(attribute.getResizedImagesPath(ROOT_FOLDER, "positive"));
        File negativeSource = new File(attribute.getResizedImagesPath(ROOT_FOLDER, "negative"));


        Preparation.fillFileContainer(positiveSource, positive,  "positive/", true);
        Preparation.fillFileContainer(negativeSource, negative,  "negative/", false);

        new File(attribute.getAttributeContexFolder(ROOT_FOLDER)+"/haarcascade").mkdir();


    }

    private static JavaSparkContext createSqlContext() {
        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("SqlQueryDataCollectionJob");
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
//        jdbc:oracle:thin:@dml1-scan:1521/bpmstg01 //bcom

        options.put("partitionColumn", "ID_MOD");
        options.put("lowerBound", "1");
        options.put("upperBound", String.valueOf(partitions));
        options.put("numPartitions", String.valueOf(partitions));
        return options;
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
