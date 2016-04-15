package com.griddynamics.functions;

import com.griddynamics.pojo.starsDomain.Category;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;

import java.math.BigDecimal;

/**
 * Created by npakhomova on 4/14/16.
 */
public class RowCategoryFunction implements Function<Row, Category> {
    @Override
    public Category call(Row row) throws Exception {

        return new Category(row.<String>getAs("CATEGORY_NAME"), row.<BigDecimal>getAs("CATEGORY_ID").intValue());
    }
}
