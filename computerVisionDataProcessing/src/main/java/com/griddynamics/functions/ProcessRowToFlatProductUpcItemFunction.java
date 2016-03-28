package com.griddynamics.functions;

import com.griddynamics.pojo.dataProcessing.FlatProductImageUpc;
import com.griddynamics.services.AttributeService;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.math.BigDecimal;

/**
 * Created by npakhomova on 3/12/16.
 */
public class ProcessRowToFlatProductUpcItemFunction implements PairFunction<Row, Integer, FlatProductImageUpc> {
    private final AttributeService starsService;

    public ProcessRowToFlatProductUpcItemFunction(AttributeService starsService) {
        this.starsService = starsService;
    }

    @Override
    public Tuple2<Integer, FlatProductImageUpc> call(Row row) throws Exception {
        FlatProductImageUpc flatProductImageUpc = new FlatProductImageUpc();

        // todo aa this mess with custing. I WANT SCALA!!!
        flatProductImageUpc.setCategoryId(row.<BigDecimal>getAs("CATEGORY_ID").intValue());
        flatProductImageUpc.setCategoryName(row.<String>getAs("CATEGORY_NAME"));

        BigDecimal color_normal_id = row.<BigDecimal>getAs("COLOR_NORMAL_ID");
        flatProductImageUpc.setColrNormalId(color_normal_id != null ? color_normal_id.intValue() : null);
        flatProductImageUpc.setDisplayColorName(row.<String>getAs("DISPLAY_COLOR_NAME"));
        flatProductImageUpc.setImageId(row.<BigDecimal>getAs("IMAGE_ID").intValue());
        flatProductImageUpc.setDescription(row.<String>getAs("UPC_DESC"));
        flatProductImageUpc.setUpcId(row.<BigDecimal>getAs("UPC_ID").intValue());

        // set for color normal value
        if (flatProductImageUpc.getColrNormalId() != null) {
            flatProductImageUpc.setColorNormal(starsService.getNormalColorById(BigDecimal.valueOf(flatProductImageUpc.getColrNormalId())));
        }

        flatProductImageUpc.setProductID(row.<BigDecimal>getAs("PRODUCT_ID").intValue());
        flatProductImageUpc.setProductDescription(row.<String>getAs("PRODUCT_DESC"));


        return new Tuple2(flatProductImageUpc.getImageId(), flatProductImageUpc);
    }
}
