package com.griddynamics.functions;

import com.griddynamics.SqlQueryDataCollectionJob;
import com.griddynamics.computervision.ColorDescription;
import com.griddynamics.computervision.ColorsRecognitionUtil;
import com.griddynamics.computervision.ShapeRecognition;
import com.griddynamics.computervision.Shapes;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.pojo.starsDomain.ICategory;
import com.griddynamics.pojo.starsDomain.ShapeDetectionStrategy;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import com.griddynamics.pojo.dataProcessing.Image;
import scala.Tuple2;

import java.io.File;
import java.math.BigDecimal;
import java.util.TreeSet;


/**
 * Created by npakhomova on 3/12/16.
 */
public class ProcessImagesFunction implements PairFunction<Row, Integer, Image> {
    private final String path;
    private final ICategory category;

    public ProcessImagesFunction(String path, ICategory category) {
        this.path = path;
        this.category = category;
    }

    @Override
    public Tuple2<Integer, Image> call(Row row) throws Exception {
        Integer image_id = row.<BigDecimal>getAs("IMAGE_ID").intValue();
        ImageRoleType imageRoleType = ImageRoleType.valueOf(row.<String>getAs("COLORWAY_IMAGE_ROLE_TYPE"));
        String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), imageRoleType.getSuffix());
        File picture = DataCollectionJobUtils.downOrloadImage(urlString, path + SqlQueryDataCollectionJob.DOWNLOAD_IMAGES_FOLDER);
//        if (picture != null) {
//            try {
//                TreeSet<ColorDescription> colorDescriptions = ColorsRecognitionUtil.getColorDescriptions(picture, category.isBodyContains());
//                Image image = new Image(image_id, colorDescriptions, urlString, imageRoleType);
//                ShapeDetectionStrategy shapeDetectionStrategy = category.getShapeDetectionStrategy();
//                if (shapeDetectionStrategy != null && shapeDetectionStrategy.equals(ShapeDetectionStrategy.RUGS)) {
//                    Shapes shape =  ShapeRecognition.getShape(picture);
////                    image.setShape(shape);
//                }
//
//                return new Tuple2(image_id, image);
//            } catch (Exception ex) {
//                System.out.println("unable to process " + urlString);
//            }
//        }

        return null;

    }
}
