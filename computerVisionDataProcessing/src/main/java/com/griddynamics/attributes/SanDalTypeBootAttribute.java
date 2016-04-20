package com.griddynamics.attributes;

import akka.dispatch.Foreach;
import com.griddynamics.SqlQueryDataCollectionJob;
import com.griddynamics.pojo.dataProcessing.ImageRoleType;
import com.griddynamics.utils.DataCollectionJobUtils;
import org.apache.spark.sql.Row;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by npakhomova on 4/19/16.
 */
public enum SanDalTypeBootAttribute implements TrainableAttribute{
    //http://11.120.149.228:8888/StarsAttributeService/attributeType/818

    TallAttribute("813","\'Over the Knee\',\'Tall\'" , "\'Ankle\'"),
    GaldiatorSandalTypeAttribute("818","Gladiator", "Ankle");


    private String attributeId;
    private String positiveAttrSet;
    private String negativeAttrSet;

    SanDalTypeBootAttribute(String attributeId, String attributeValue, String negativeAttrSet) {

        this.attributeId = attributeId;
        this.positiveAttrSet = attributeValue;
        this.negativeAttrSet = negativeAttrSet;
    }

    @Override
    public String getPositiveGetSqlQuery(int partitions) {
        return  String.format(Constants.IMAGES_DOWNLOAD_QUERY, partitions, attributeId, positiveAttrSet);
    }

    public Foreach<Row> getPicturesDownloadFunction(final String rootFolder, String suffix) {
        return new RowForeach(rootFolder,suffix);
    }

    public String getDownloadedImagesPath(String rootFolder, String suffix) {
        return getAttributeContexFolder(rootFolder) +"/"+suffix;
    }

    public String getAttributeContexFolder(String rootFolder) {
        return rootFolder + name();
    }

    public void createAllFolders(String rootFolder) throws IOException {
        //todo fix staff with downloaded images
        DataCollectionJobUtils.checkFolderExistance(getAttributeContexFolder(rootFolder));
        DataCollectionJobUtils.checkFolderExistance(getResizedImagesPath(rootFolder, "positive"));
        DataCollectionJobUtils.checkFolderExistance(getResizedImagesPath(rootFolder, "negative"));

    }

    public String getNegativeSqlQuery(Integer partitions) {
        return  String.format(Constants.IMAGES_DOWNLOAD_QUERY, partitions, attributeId, negativeAttrSet);

    }

    private static class Constants {
        public static final String IMAGES_DOWNLOAD_QUERY = "select\n" +
                "  distinct\n" +
                "  PRODUCT_IMAGE.PRODUCT_ID,\n" +
                "  PRODUCT_IMAGE.IMAGE_ID,\n" +
//                "  PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE,\n" +
//                "  PRODUCT_COLORWAY_IMAGE.COLORWAY_IMAGE_ROLE_TYPE,\n" +
//                "  PRODUCT_IMAGE.PRODUCT_IMAGE_SEQUENCE,\n" +
                "  mod(PRODUCT_IMAGE.PRODUCT_ID, %d) AS ID_MOD \n" +
                "from PRODUCT_IMAGE\n" +
                "join PRODUCT_ATTRIBUTE on PRODUCT_ATTRIBUTE.PRODUCT_ID =PRODUCT_IMAGE.PRODUCT_ID and PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID = '%s' and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in (%s)\n" +
                "  join UPC on UPC.PRODUCT_ID = PRODUCT_IMAGE.PRODUCT_ID --and PRODUCT.STATE_ID = 2\n" +
                "  join PRODUCT_COLORWAY on PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID =UPC.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT_COLORWAY_IMAGE on PRODUCT_COLORWAY_IMAGE.PRODUCT_COLORWAY_ID = PRODUCT_COLORWAY.PRODUCT_COLORWAY_ID\n" +
                "  join PRODUCT on PRODUCT_IMAGE.PRODUCT_ID = PRODUCT.PRODUCT_ID and PRODUCT.STATE_ID = 2\n" +
                "  JOIN PRODUCT_DESTINATION_CHANNEL ON PRODUCT_DESTINATION_CHANNEL.PRODUCT_ID = PRODUCT.PRODUCT_ID AND PRODUCT_DESTINATION_CHANNEL.PUBLISH_FLAG='Y' AND PRODUCT_DESTINATION_CHANNEL.CURRENT_FLAG='Y'\n" +
                "where PRODUCT_IMAGE.PRODUCT_IMAGE_SEQUENCE = 0 and PRODUCT_IMAGE.PRODUCT_IMAGE_ROLE_TYPE ='ADD'";
    }

    private class RowForeach extends Foreach<Row> implements Serializable{
        private final String rootFolder;
        private String suffix;

        public RowForeach(String rootFolder, String suffix) {
            this.rootFolder = rootFolder;
            this.suffix = suffix;
        }

        @Override
        public void each(Row v1) throws Throwable {

            Integer image_id = v1.<BigDecimal>getAs("IMAGE_ID").intValue();
//            ImageRoleType imageRoleType = ImageRoleType.valueOf(v1.<String>getAs("COLORWAY_IMAGE_ROLE_TYPE"));
            String urlString = DataCollectionJobUtils.buildURL(image_id.intValue(), ImageRoleType.CPRI.getSuffix());

            File picture = DataCollectionJobUtils.downOrloadImage(urlString, getDownloadedImagesPath(rootFolder, suffix));

//            Preparation.prepare

        }
    }

    public String getResizedImagesPath(String rootFolder, String positive) {
        return rootFolder+name()+"/"+positive;
    }
}
