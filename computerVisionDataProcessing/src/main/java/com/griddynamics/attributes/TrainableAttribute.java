package com.griddynamics.attributes;

import akka.dispatch.Foreach;
import org.apache.spark.sql.Row;

import java.io.Serializable;

/**
 * Created by npakhomova on 4/19/16.
 */
public interface TrainableAttribute extends Serializable{

    /**
     *
     * @return
     * @param partitions
     */
    String getPositiveGetSqlQuery(int partitions);

    Foreach<Row> getPicturesDownloadFunction(final String rootFolder, String suffix);




}
