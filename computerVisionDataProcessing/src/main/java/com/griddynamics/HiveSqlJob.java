package com.griddynamics;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 5/13/16.
 */
public class HiveSqlJob {

    public static void main(String[] args) {

        SparkConf config = new SparkConf();
        config.setMaster("local[16]");
        config.setAppName("HiveSqlJob");
        JavaSparkContext sparkContext = new JavaSparkContext(config);


        // sc is an existing JavaSparkContext.
        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(sparkContext);
        hiveContext.setConf("hive.metastore.uris", "thrift://mdc2pr205:10000");
//        hiveContext.setConf("javax.jdo.option.ConnectionURL","jdbc:hive://mdc2pr205:10000/default" );
        hiveContext.setConf("spark.sql.hive.thriftServer.singleSession", "true");
//        hiveContext.setConf("javax.jdo.option.ConnectionDriverName","org.apache.hadoop.hive.jdbc.HiveDriver");
        hiveContext.setConf("javax.jdo.option.ConnectionUserName","yc14ng1");
        hiveContext.setConf("javax.jdo.option.ConnectionPassword","Ybyjxrf29!");
        hiveContext.setConf("spark.driver.extraJavaOptions","-XX:MaxPermSize=1024m -XX:PermSize=256m");



//        hiveContext.sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING)");
//        hiveContext.sql("LOAD DATA LOCAL INPATH 'examples/src/main/resources/kv1.txt' INTO TABLE src");

// Queries are expressed in HiveQL.
//        Row[] results = hiveContext.sql("FROM src SELECT key, value").collect();
       hiveContext.sql("show databases").show(10);
//        for (Row row : collect){
//            System.out.println(row);
//        }
    }
}
