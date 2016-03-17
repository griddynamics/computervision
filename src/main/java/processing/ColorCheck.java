package processing;

import utils.Colors;
import utils.DeltaE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static utils.MapUtils.getKeysByValue;

public class ColorCheck {

    private static final int DISTANCE_THRESHOLD = 10;

    private static Map<double[], String> nameOfColorByCode;
    private static Map<String, Integer> sortedByPercent;

    public static void setSortedByPercent(Map<String, Integer> sortedByPercent) {
        ColorCheck.sortedByPercent = sortedByPercent;
    }

    public static void setNameOfColorByCode(Map<double[], String> nameOfColorByCode) {
        ColorCheck.nameOfColorByCode = nameOfColorByCode;
    }

    /**
     * Returns <tt>true</tt> if mismatch is real (not false positive)
     *
     * @param nameOfColorNormal color for checking
     */
    public static boolean checkMismatch(String nameOfColorNormal) {
        if (sortedByPercent == null) throw new IllegalStateException("sortedByPercent is null");
        if (nameOfColorByCode == null) throw new IllegalStateException("nameOfColorByCode is null");
        if (nameOfColorNormal == null) throw new IllegalStateException("nameOfColorNormal is null");

        String dominantColor = sortedByPercent.entrySet().iterator().next().getKey();

        boolean isRealMismatch = false;
        String nameOfColorNormalLower = nameOfColorNormal.toLowerCase();

        Set<double[]> codesFromResult = getKeysByValue(nameOfColorByCode, dominantColor);

//        Set<double[]> codesFromAllPalette = getKeysByValue(Colors.AdditionalPalette, nameOfColorNormalLower);
        Set<double[]> codesFromAllPalette = new HashSet<>();
//        codesFromAllPalette.addAll(getKeysByValue(Colors.Palette, nameOfColorNormalLower));

        double minDistanse = Double.MAX_VALUE;
        double sumMinDist = 0;
        double averageMin = 0;

//        System.out.println("colorNormal: " + nameOfColorNormal);

//        double[] minPalCode = new double[3];
//        double[] minResCode = new double[3];

        for (double[] resCodes : codesFromResult) {
            for (double[] palCodes : codesFromAllPalette) {
                double dist = DeltaE.deltaE2000(resCodes, palCodes);
                if (minDistanse > dist) {
                    minDistanse = dist;
//                    minPalCode = palCodes;
//                    minResCode = resCodes;
                }
            }
            sumMinDist += minDistanse;
        }

        averageMin = sumMinDist / codesFromResult.size();

//        System.out.println("real MIN DIST: " + minDistanse);
//        System.out.println("pal CODE: " + minPalCode[0] + ", " + minPalCode[1] + ", " + minPalCode[2]);
//        System.out.println("res CODE: " + minResCode[0] + ", " + minResCode[1] + ", " + minResCode[2]);

        if (averageMin > DISTANCE_THRESHOLD) {
            isRealMismatch = true;
        }

//        System.out.println("dominant: " + dominantColor + ", dist: " + averageMin);
//        System.out.println("isRealMismatch?:" + isRealMismatch);
//        System.out.println("---");

        return isRealMismatch;
    }

}
