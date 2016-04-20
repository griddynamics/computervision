package com.griddynamics.computervision;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

/**
 * Created by npakhomova on 4/18/16.
 */
public enum HeelHeightValue {

    Flat("Flat 0-1\"", new FlatAttributeRecognitionStrategy(), " " + Constants.FLAT_EXCLUSION),
    High("High 3-4\"", new HeelAttributeRecognitionStrategy(0.43),Constants.PLATFORM_ANKLE_SNEAKERS_MULE_EXCLUSION),
    Mid("Mid 2-3\"", new HeelAttributeRecognitionStrategy(0.35),Constants.PLATFORM_ANKLE_SNEAKERS_MULE_EXCLUSION),
    Low("Low 1-2\"", new FlatAttributeRecognitionStrategy(),Constants.FLAT_EXCLUSION),
    Ultra_High("Ultra High 4\" & over", new HeelAttributeRecognitionStrategy(0.43),
            Constants.PLATFORM_ANKLE_SNEAKERS_MULE_EXCLUSION);

    private String value;
    private AttributeStrategy attributeStrategy;
    private String exclusionCondition;

    HeelHeightValue(String mid, AttributeStrategy attributeStrategy) {
        this.value = mid;
        this.attributeStrategy = attributeStrategy;
    }

    HeelHeightValue(String mid, AttributeStrategy attributeStrategy, String exclusionCondition) {
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

    public static HeelHeightValue getEnum(String value) {
        for (HeelHeightValue v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

    private static class FlatAttributeRecognitionStrategy implements AttributeStrategy {
        @Override
        public boolean doesApply(File picture) {
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
                HeightHeelValueResult heightHeelValueResult = HeelRecognitionUtil.getHeightHeelValueResult(cropped, ratio);
                return heightHeelValueResult.getValue().equals(Flat);
            }

        }
    }

    private static class HeelAttributeRecognitionStrategy implements AttributeStrategy {
        private double ratioBoundary;

        public HeelAttributeRecognitionStrategy(double ratio) {
            this.ratioBoundary = ratio;
        }

        @Override
        public boolean doesApply(File picture) {
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
        "  select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in ( 'Sneakers', ' Mules' , 'Mule')))\n" +
        "      and PRODUCT.PRODUCT_ID NOT IN (\n" +
        "  select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where (PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('813')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE in ( 'Ankle', 'Tall') )                                                                                                                                                                                                           --(PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID in ('814','1078')  and PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Sneakers' )\n" +
        ")";
        public static final String FLAT_EXCLUSION = "and PRODUCT.PRODUCT_ID NOT IN (" +
                "select distinct PRODUCT_ATTRIBUTE.PRODUCT_ID from PRODUCT_ATTRIBUTE where  PRODUCT_ATTRIBUTE.ATTRIBUTE_TYPE_ID='818' and " +
                " PRODUCT_ATTRIBUTE.VARCHAR_VALUE = 'Gladiator' " +
                ")";
    }
}
