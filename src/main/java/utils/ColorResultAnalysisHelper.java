package utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Author: Eugene Steinberg
 * Date: 3/12/16
 */
public class ColorResultAnalysisHelper {

    /**
     * This method employ basic descriptive statistics to determine color "outlier", if any
     *
     * @param colorWithPercent mapping from color name to percentage of this color within image
     * @return dominant color key from the provided color to percentage map or "multi" if no dominant can be found
     */
    public static String dominantColor(Map<String, Integer> colorWithPercent) {
        if (colorWithPercent.size() == 0)
            throw new IllegalArgumentException("empty map");

        if (colorWithPercent.size() == 1)
            return colorWithPercent.keySet().iterator().next();

        double threshold = getOutlierThreshold(toIntArray(colorWithPercent.values()));
        for (Map.Entry<String, Integer> colorPercentageEntry : colorWithPercent.entrySet()) {
            if (colorPercentageEntry.getValue() > threshold)
                return colorPercentageEntry.getKey();
        }
        return "multi";
    }

    private static double getOutlierThreshold(int[] percentages) {
        int[] q13 = quartiles13(percentages);
        double iqr = q13[1] - q13[0];
        return q13[1] + iqr * 1.5;
    }

    private static int[] toIntArray(Collection<Integer> values) {
        int[] result = new int[values.size()];
        int i = 0;
        for (int value : values)
            result[i++] = value;
        return result;
    }

    private static int[] quartiles13(int[] values) {
        Arrays.sort(values);
        int n = values.length - 1;
        return new int[]{values[n / 4], values[n * 3 / 4]};
    }

}

