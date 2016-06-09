package com.griddynamics.computervision;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by npakhomova on 4/22/16.
 */
public enum HeelHeightBCOMValue {
    /**"3""-4"" High Heel"
     "<1"" Flat"
     Low
     High
     "Very High"
     "1""-2"" Low Heel"
     "2""-3"" Mid Heel"
     ">4"" Ultra High Heel"
     Mid*/

    Flat("<1\" Flat", new FlatAttributeRecognitionStrategy()),
    Low("1\"-2\" Low Heel", new FlatAttributeRecognitionStrategy()),
    Mid("2\"-3\" Mid Heel", new HeelAttributeRecognitionStrategy(0.35)),
    High("3\"-4\" High Heel", new HeelAttributeRecognitionStrategy(0.43)),
    Ultra_High(">4\" Ultra High Heel", new HeelAttributeRecognitionStrategy(0.43));

    private String value;
    private AttributeStrategy attributeStrategy;
    private String exclusionCondition;

    HeelHeightBCOMValue(String mid, AttributeStrategy attributeStrategy) {
        this.value = mid;
        this.attributeStrategy = attributeStrategy;
    }

    HeelHeightBCOMValue(String mid, AttributeStrategy attributeStrategy, String exclusionCondition) {
        this.value = mid;
        this.attributeStrategy = attributeStrategy;
        this.exclusionCondition = exclusionCondition;
    }

    public String getValue() {
        return value;
    }

    public AttributeStrategy getAttributeStrategy() {
        return attributeStrategy;
    }

    public String getExclusionCondition() {
        return exclusionCondition;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static HeelHeightBCOMValue getEnum(String value) {
        for (HeelHeightBCOMValue v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

    private static class FlatAttrRecognitionHaar implements AttributeStrategy{

        @Override
        public boolean doesApply(File picture) throws IOException {
            return !HeelRecognitionUtil.isHighHeelByHaar(picture,"/Users/npakhomova/codebase/css/computerVisionGDSource/computerVisionDataProcessing/src/main/resources/cascades/cascade.xml");
        }
    }

    private static class HeelAttrRecognitionHaar implements AttributeStrategy{

        @Override
        public boolean doesApply(File picture) throws IOException {
            return HeelRecognitionUtil.isHighHeelByHaar(picture,"/Users/npakhomova/codebase/css/computerVisionGDSource/computerVisionDataProcessing/src/main/resources/cascades/cascade.xml");
        }
    }

    private static class FlatAttributeRecognitionStrategy implements AttributeStrategy {
        @Override
        public boolean doesApply(File picture) throws IOException {
            //try to use neural network
            URL resource = this.getClass().getClassLoader().getResource("cascades/cascade.xml");
            boolean highHeelByHaar = HeelRecognitionUtil.isHighHeelByHaar(picture, resource.getPath());
            if ( highHeelByHaar  ) return false;

            // try to define, does shoe is flat
            Mat image = Imgcodecs.imread(picture.getAbsolutePath());
            Mat mask = Mask.getMask(image, false);
            // crop image
            Mat cropped = HeelRecognitionUtil.crop(mask);

            // define dimentions ratioBoundary
            double ratio = (double) cropped.rows() / cropped.cols();
            if (ratio < 0.55){ // this is definitelly flat
                return true;
            } else { // try another approach
//                HeightHeelValueResult heightHeelValueResult = HeelRecognitionUtil.getHeightHeelValueResult(cropped, ratio);
//                return heightHeelValueResult.getValue().equals(Flat);
                return false;
            }

        }
    }

    private static class HeelAttributeRecognitionStrategy implements AttributeStrategy {
        private double ratioBoundary;

        public HeelAttributeRecognitionStrategy(double ratio) {
            this.ratioBoundary = ratio;
        }

        @Override
        public boolean doesApply(File picture) throws IOException {
            URL resource = this.getClass().getClassLoader().getResource("cascades/cascade.xml");
            // try to find heel by neural network
            boolean result = HeelRecognitionUtil.isHighHeelByHaar(picture, resource.getPath());
            if (result) return result;

            // try to define, does shoe is flat
            Mat image = Imgcodecs.imread(picture.getAbsolutePath());
            Mat mask = Mask.getMask(image, false);
            // crop image
            Mat cropped = HeelRecognitionUtil.crop(mask);

            // define dimentions ratioBoundary
            double ratio = (double) cropped.rows() / cropped.cols();
            if (ratio < ratioBoundary){ // this is definitelly flat
                return false;
            } else { // try another approach
                HeightHeelValueResult heightHeelValueResult = HeelRecognitionUtil.getHeightHeelValueResult(cropped, ratio);
                return heightHeelValueResult.getValue().equals(Ultra_High);
            }

        }
    }


    private static class Constants {
        //        public static final String PLATFORM_ANKLE_SNEAKERS_MULE_EXCLUSION = " and PRODUCT.PRODUCT_ID NOT IN (select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where  PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='818' and " +
//                "PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Platform' " +
//                "or PRODUCT_ATTRIBUTE.VARCHAR_VALUE like 'Wedge%') ";
        public static final String PLATFORM_ANKLE_SNEAKERS_MULE_EXCLUSION = "   and PRODUCT.PRODUCT_ID NOT IN (\n" +
                "select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where  (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='818' and PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Platform' or PRODUCT_ATTRIBUTE.VARCHAR_VALUE like 'Wedge%'))\n" +
                "      and PRODUCT.PRODUCT_ID NOT IN (\n" +
                "  select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in ( 'Sneakers', ' Mules' , 'Mule', 'Boots')))\n" +
                "      and PRODUCT.PRODUCT_ID NOT IN (\n" +
                "  select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('813')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in ( 'Ankle', 'Tall') )                                                                                                                                                                                                           --(PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Sneakers' )\n" +
                ")";
        public static final String FLAT_EXCLUSION = "and PRODUCT.PRODUCT_ID NOT IN (" +
                "select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where  PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='818' and " +
                " PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Gladiator' " +
                ")" +
                " and PRODUCT.PRODUCT_ID NOT IN (\n" +
                " select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in ( 'Sneakers', ' Mules' , 'Mule', 'Boots')))";
    }
}
