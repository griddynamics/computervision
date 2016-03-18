package utils;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Author: Eugene Steinberg
 * Date: 3/12/16
 */
public class ColorResultAnalysisHelperTest {
    Map<String, Integer> colorPercentages = new HashMap<>();
    String result;

    @Test
    @Ignore
    public void dominantColorTest() {

        colorPercentages.clear();

        colorPercentages.put("white", 5);
        colorPercentages.put("black", 95);

        result = ColorResultAnalysisHelper.dominantColor(colorPercentages);

        assertEquals("black", result);

        colorPercentages.clear();

        colorPercentages.put("white", 4);
        colorPercentages.put("black", 13);
        colorPercentages.put("gray", 81);

        result = ColorResultAnalysisHelper.dominantColor(colorPercentages);

        assertEquals("gray", result);

        colorPercentages.clear();

        colorPercentages.put("black", 3);
        colorPercentages.put("brown", 8);
        colorPercentages.put("gray", 19);
        colorPercentages.put("white", 30);
        colorPercentages.put("green", 37);

        result = ColorResultAnalysisHelper.dominantColor(colorPercentages);

        assertEquals("multi", result);


        colorPercentages.clear();

        colorPercentages.put("white", 3);
        colorPercentages.put("brown", 6);
        colorPercentages.put("pink", 13);
        colorPercentages.put("red", 76);

        result = ColorResultAnalysisHelper.dominantColor(colorPercentages);

        assertEquals("red", result);

        colorPercentages.clear();

        colorPercentages.put("white", 3);
        colorPercentages.put("yellow", 5);
        colorPercentages.put("black", 39);
        colorPercentages.put("gray", 50);

        result = ColorResultAnalysisHelper.dominantColor(colorPercentages);

        assertEquals("multi", result);

    }
}