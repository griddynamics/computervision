package com.griddynamics.functions;

import com.griddynamics.pojo.dataProcessing.Category;
import com.griddynamics.pojo.dataProcessing.Product;
import com.griddynamics.services.AttributeService;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.math.BigDecimal;

/**
 * Created by npakhomova on 3/12/16.
 */
public class ProcessRowToFlatProductUpcItemFunction implements PairFunction<Row, Integer, Product> {
    private final AttributeService starsService;

    public ProcessRowToFlatProductUpcItemFunction(AttributeService starsService) {
        this.starsService = starsService;
    }

    @Override
    public Tuple2<Integer, Product> call(Row row) throws Exception {
        Product product = new Product();

        Category category = new Category();
        category.setCategoryId(row.<BigDecimal>getAs("CATEGORY_ID").intValue());
        category.setCategoryName(row.<String>getAs("CATEGORY_NAME"));

        product.setCategory(category);



        product.setProductID(row.<BigDecimal>getAs("PRODUCT_ID").intValue());
        product.setProductDescription(row.<String>getAs("PRODUCT_DESC"));

        return new Tuple2(row.<BigDecimal>getAs("IMAGE_ID").intValue(), product);
    }
}



