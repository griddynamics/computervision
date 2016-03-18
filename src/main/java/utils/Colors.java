package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Colors {

    private final static HashMap<String, List<double[]>> REAL_COLORS_PALETTE = new HashMap<String, List<double[]>>();
    private final static HashMap<String, List<double[]>> ADDITIONAL_PALETTE = new HashMap<String, List<double[]>>();
    private final static HashMap<String, List<double[]>> COMMON_PALETTE = setCommonPalette();

    public static final String YELLOW = "yellow";

    public static final String TAN_BEIGE = "tan/beige";

    public static final String ORANGE = "orange";

    public static final String RED = "red";

    public static final String BLUE = "blue";

    public static final String GREEN = "green";

    public static final String GRAY = "gray";

    public static final String BROWN = "brown";

    public static final String WHITE = "white";

    public static final String BLACK = "black";

    public static final String IVORY_CREAM = "ivory/cream";

    public static final String PINK = "pink";

    public static final String PURPLE = "purple";

    public static final String SILVER = "silver";

    public static final String GOLD = "gold";

    static {
        ADDITIONAL_PALETTE.put(TAN_BEIGE, Arrays.asList(
                new double[]{+90.58, +04.00, +23.00},
                new double[]{+89.80, +04.00, +27.00},
                new double[]{+91.37, +04.00, +21.00},
                new double[]{+92.15, +03.00, +20.00},
                new double[]{+93.33, +02.00, +18.00}
        ));

        ADDITIONAL_PALETTE.put(GRAY, Arrays.asList(
                new double[]{+88.23, +00.00, +00.00},
                new double[]{+81.56, +00.00, +00.00},
                new double[]{+83.92, +00.00, +00.00},
                new double[]{+86.27, +00.00, +00.00}

        ));

        ADDITIONAL_PALETTE.put(WHITE, Arrays.asList(
                new double[]{+100.0, +00.00, +00.00}));

        ADDITIONAL_PALETTE.put(BLACK, Arrays.asList(
                new double[]{+00.00, +00.00, +00.00}));

        ADDITIONAL_PALETTE.put(IVORY_CREAM, Arrays.asList(
                new double[]{+98.82, -03.00, +11.00},
                new double[]{+98.03, -03.00, +10.00},
                new double[]{+98.43, -04.00, +12.00},
                new double[]{+96.86, -03.00, +08.00},
                new double[]{+97.64, -03.00, +10.00}));

        ADDITIONAL_PALETTE.put(SILVER, Arrays.asList(
                new double[]{+77.64, +00.00, +00.00},
                new double[]{+93.33, +00.00, +00.00},
                new double[]{+85.49, +00.00, +00.00},
                new double[]{+74.90, +00.00, +00.00},
                new double[]{+71.37, +00.00, +00.00}));

        ADDITIONAL_PALETTE.put(GOLD, Arrays.asList(
                new double[]{+85.09, -03.00, +52.00},
                new double[]{+83.52, -03.00, +68.00},
                new double[]{+72.15, +02.00, +74.00},
                new double[]{+85.09, -03.00, +61.00},
                new double[]{+68.62, +04.00, +72.00}));
    }

    static {
        REAL_COLORS_PALETTE.put(YELLOW, Arrays.asList(
                new double[]{+88.00, -09.14, +74.44},
                new double[]{+78.00, +09.51, +90.50},
                new double[]{+96.00, -20.00, +85.00},
                new double[]{+78.00, +09.51, +90.50},
                new double[]{+97.00, -22.00, +94.00},
                new double[]{+89.07, -06.00, +52.70},
                new double[]{+93.72, -17.00, +92.00},
                new double[]{+94.90, -18.00, +93.00},
                new double[]{+94.50, -17.00, +93.00},
                new double[]{+91.76, -14.00, +90.00},
                new double[]{+92.94, -15.00, +91.00},
                new double[]{+97.0, -2.0, 13.0},
                new double[]{+66 ,10 ,50},
                new double[]{70,7,52}));

        REAL_COLORS_PALETTE.put(TAN_BEIGE, Arrays.asList(
                new double[]{+76.10, -00.56, +26.69},
                new double[]{+73.80, +05.24, +25.77},
                new double[]{+60.51, +06.02, +08.65},
                new double[]{+90.00, +04.96, +16.72},

                new double[]{+90.58, +04.00, +23.00},
                new double[]{+89.80, +04.00, +27.00},
                new double[]{+91.37, +04.00, +21.00},
                new double[]{+92.15, +03.00, +20.00},
                new double[]{+93.33, +02.00, +18.00}
                ));



        REAL_COLORS_PALETTE.put(RED, Arrays.asList(
                new double[]{+38.00, +48.91, +32.99},
                new double[]{+37.00, +45.90, +26.50},
                new double[]{+37.00, +45.90, +26.50},
                new double[]{+30.00, +43.30, +25.00},
                new double[]{+28.00, +31.52, +12.74},
                new double[]{+41.00, +56.63, +41.14},
                new double[]{+59.00, +80.09, +58.19},
                new double[]{+59.00, +77.71, +62.93},
                new double[]{+58.00, +62.00, +26.00},
                new double[]{+38.00, +33.38, +06.49},
                new double[]{+49.01, +69.00, +51.00},
                new double[]{+49.01, +69.00, +51.00},
                new double[]{+47.84, +68.00, +51.00},
                new double[]{+46.27, +68.00, +55.00},
                new double[]{+48.62, +69.00, +51.00},
                new double[]{+47.45, +69.00, +54.00}));

        REAL_COLORS_PALETTE.put(BLUE, Arrays.asList(
                new double[]{+35.00, -02.30, -21.88},
                new double[]{+31.00, -09.64, -19.77},
                new double[]{+29.00, +09.92, -39.78},
                new double[]{+24.00, -00.73, -20.99},
                new double[]{+34.80, -06.64, -35.48},
                new double[]{+45.00, -06.62, -23.07},
                new double[]{+26.90, -02.79, -07.50},
                new double[]{+38.00, -10.80, -20.31},
                new double[]{+30.90, -07.51, -33.88},
                new double[]{+54.00, -15.21, -32.63},
                new double[]{+22.00, +02.30, -21.88},
                new double[]{+52.00, -02.95, -16.74},
                new double[]{+49.60, -13.63, -35.69},
                new double[]{+36.90, -12.85, -34.91},
                new double[]{+37.00, -11.95, -29.14},
                new double[]{+24.10, +08.89, -25.39},
                new double[]{+46.00, -03.60, -22.72},
                new double[]{+60.00, -10.18, -18.37},
                new double[]{+76.43, -24.19, -10.56},
                new double[]{+73.74, -14.77, -13.93},
                new double[]{+76.89, -04.20, -16.01},
                new double[]{+66.29, -07.19, -18.02},
                new double[]{+77.20, -07.84, -17.77},
                new double[]{+32.31, -02.64, -16.06},
                new double[]{+32.31, -02.64, -16.06},
                new double[]{+29.62, +04.84, -16.26},
                new double[]{+12.76, +01.95, -18.03},
                new double[]{+23.89, +05.26, -18.11},
                new double[]{+86.87, -14.16, -05.88},
                new double[]{+88.00, -13.43, -01.33},
                new double[]{+32.00, -14.00, -07.00},
                new double[]{+28.00, -01.00, -08.00},
                new double[]{+28.00, +03.05, -12.13},
                new double[]{+36.00, -03.05, -09.13},
                new double[]{+35.00, -00.59, -13.16},
                new double[]{+59.00, -02.59, -12.00},
                new double[]{+14.00, +03.59, -14.00},
                new double[]{+09.40, +02.59, -13.00},
                new double[]{+12.90, +01.40, -07.00},
                new double[]{+51.37, +11.00, -45.00},
                new double[]{+45.88, +14.00, -46.00},
                new double[]{+47.84, +12.00, -45.00},
                new double[]{+49.41, +11.00, -45.00},
                new double[]{+43.92, +15.00, -46.00}));

        REAL_COLORS_PALETTE.put(GREEN, Arrays.asList(
                new double[]{+47.00, -22.50, +04.78},
                new double[]{+42.00, -26.35, +19.86},
                new double[]{+35.00, -24.52, +22.08},
                new double[]{+28.00, -17.47, -04.35},
                new double[]{+25.70, -19.35, +03.69},
                new double[]{+24.00, -07.31, +03.25},
                new double[]{+43.00, -23.42, +26.01},
                new double[]{+51.00, -12.31, +15.76},
                new double[]{+40.00, -36.75, +06.94},
                new double[]{+51.00, -24.93, +28.68},
                new double[]{+59.00, -34.77, +40.00},
                new double[]{+30.00, -07.22, +08.30},
                new double[]{+80.00, -12.96, +13.90},
                new double[]{+62.00, -11.70, +14.97},
                new double[]{+48.60, -45.77, +18.31},
                new double[]{+45.00, -13.91, +23.14},
                new double[]{+34.60, -36.70, +00.13},
                new double[]{+35.00, -15.30, +04.68},
                new double[]{+42.00, -43.88, +16.84},
                new double[]{+46.40, -41.20, +18.09},
                new double[]{+89.00, -30.00, +54.00},
                new double[]{+37.00, -12.13, +12.89},
                new double[]{+73.47, -24.11, +18.34},
                new double[]{+85.00, -13.39, +06.85},
                new double[]{+84.00, -06.39, +05.85},
                new double[]{+46.23, -03.37, +08.00},
                new double[]{+30.75, -01.89, +09.82},
                new double[]{+77.00, -06.26, +07.00},
                new double[]{+61.96, -56.00, +42.00},
                new double[]{+63.92, -57.00, +41.00},
                new double[]{+65.09, -57.00, +40.00},
                new double[]{+59.60, -55.00, +41.00},
                new double[]{+65.88, -57.00, +40.00}));

        REAL_COLORS_PALETTE.put(SILVER, Arrays.asList(
                new double[]{+77.64, +00.00, +00.00},
                new double[]{+93.33, +00.00, +00.00},
                new double[]{+85.49, +00.00, +00.00},
                new double[]{+74.90, +00.00, +00.00},
                new double[]{+71.37, +00.00, +00.00}

//                new double[]{+57.00, -03.36, -04.97},
//                new double[]{+62.00, -02.42, -04.37},
//                new double[]{+65.00, +00.07, -01.00},
//                new double[]{+48.00, -01.89, +00.65},
//                new double[]{+39.00, -02.87, +04.10},
//                new double[]{+40.00, -02.30, +01.93},
//                new double[]{+39.00, -02.18, -03.35},
//                new double[]{+41.00, -02.23, -02.01},
//                new double[]{+54.00, -01.00, -00.03},
//                new double[]{+62.00, -01.90, -00.62},
//                new double[]{+36.00, -02.00, -00.03},
//                new double[]{+61.00, -01.63, -02.52},
//                new double[]{+56.00, -01.88, -03.53},
//                new double[]{+32.00, +00.00, -00.00},
//                new double[]{+73.00, -02.00, +08.00},
//                new double[]{+73.60, -03.41, -04.59},
//                new double[]{+32.11, +01.26, -02.84},
//                new double[]{+33.33, +01.00, -07.00},
//                new double[]{+49.41, +00.00, -08.00}
        ));


        REAL_COLORS_PALETTE.put(GRAY, Arrays.asList(
                new double[]{+57.00, -03.36, -04.97},
                new double[]{+62.00, -02.42, -04.37},
                new double[]{+65.00, +00.07, -01.00},
                new double[]{+48.00, -01.89, +00.65},
                new double[]{+39.00, -02.87, +04.10},
                new double[]{+40.00, -02.30, +01.93},
                new double[]{+39.00, -02.18, -03.35},
                new double[]{+41.00, -02.23, -02.01},
                new double[]{+54.00, -01.00, -00.03},
                new double[]{+62.00, -01.90, -00.62},
                new double[]{+36.00, -02.00, -00.03},
                new double[]{+61.00, -01.63, -02.52},
                new double[]{+56.00, -01.88, -03.53},
                new double[]{+32.00, +00.00, -00.00},
                new double[]{+73.00, -02.00, +08.00},
                new double[]{+73.60, -03.41, -04.59},
                new double[]{+32.11, +01.26, -02.84},
                new double[]{+33.33, +01.00, -07.00},
                new double[]{+49.41, +00.00, -08.00},

                new double[]{+88.23, +00.00, +00.00},
                new double[]{+81.56, +00.00, +00.00},
                new double[]{+83.92, +00.00, +00.00},
                new double[]{+86.27, +00.00, +00.00},

                new double[]{+77.64, +00.00, +00.00},
                new double[]{+93.33, +00.00, +00.00},
                new double[]{+85.49, +00.00, +00.00},
                new double[]{+74.90, +00.00, +00.00},
                new double[]{+71.37, +00.00, +00.00}
                ));

        REAL_COLORS_PALETTE.put(BROWN, Arrays.asList(
                new double[]{+48.00, +06.77, +27.17},
                new double[]{+49.00, +17.53, +35.95},
                new double[]{+39.00, +15.83, +15.28},
                new double[]{+41.00, +16.78, +24.87},
                new double[]{+41.00, +25.18, +24.31},
                new double[]{+36.00, +13.98, +20.73},
                new double[]{+37.00, +11.84, +24.27},
                new double[]{+25.00, +06.30, +11.37},
                new double[]{+40.00, +12.86, +19.07},
                new double[]{+43.00, +08.39, +12.44},
                new double[]{+29.00, +06.70, +11.14},
                new double[]{+29.00, +07.00, +08.00},
                new double[]{+29.00, +07.00, +08.00},
                new double[]{+15.00, +05.00, +03.23},
                new double[]{+21.50, +04.86, +03.23},
                new double[]{+61.00, +15.00, +39.00},
                new double[]{+58.61, +01.00, +12.07},
                new double[]{+76.08, +08.00, +51.00},
                new double[]{+28.62, +14.00, +33.00},
                new double[]{+30.98, +13.00, +34.00},
                new double[]{+32.15, +13.00, +35.00},
                new double[]{+29.80, +14.00, +33.00},
                new double[]{+27.05, +13.00, +32.00}));

        REAL_COLORS_PALETTE.put(WHITE, Arrays.asList(
                new double[]{+90.00, +00.61, +06.97},
                new double[]{+85.00, -00.97, +03.88},
                new double[]{+94.00, -00.95, +00.31},
                new double[]{+94.00, -00.49, +03.97},
                new double[]{+93.00, -00.72, +01.87},
                new double[]{+80.00, +02.00, +02.00},

                new double[]{+100.0, +00.00, +00.00}
                ));

        REAL_COLORS_PALETTE.put(BLACK, Arrays.asList(
                new double[]{+10.00, +00.81, -00.59},
                new double[]{+04.70, -00.29, -00.53},
                new double[]{+16.00, -00.17, -00.98},
                new double[]{+05.00, +00.00, +00.00},
                new double[]{+15.00, +02.00, -01.00},
                new double[]{+20.00, +02.00, -01.00},
                new double[]{+20.00, +02.00, -05.00},
                new double[]{+21.00, +00.00, -00.00},
                new double[]{+10.00, +02.00, -05.00},
                new double[]{+11.00, +02.00, -05.00},

                new double[]{30.0, 0.0, 0.0}, //????

                new double[]{+00.00, +00.00, +00.00}
                ));

        REAL_COLORS_PALETTE.put(IVORY_CREAM, Arrays.asList(
                new double[]{+81.00, +02.40, +22.87},
                new double[]{+86.00, +02.23, +15.84},

                new double[]{+98.82, -03.00, +11.00},
                new double[]{+98.03, -03.00, +10.00},
                new double[]{+98.43, -04.00, +12.00},
                new double[]{+96.86, -03.00, +08.00},
                new double[]{+97.64, -03.00, +10.00}
                ));

        REAL_COLORS_PALETTE.put(PINK, Arrays.asList(
                new double[]{+59.00, +33.61, +12.90},
                new double[]{+72.00, +20.61, +04.01},
                new double[]{+80.00, +27.21, +17.21},
                new double[]{+55.00, +43.57, -06.12},
                new double[]{+78.43, +33.00, -12.00},
                new double[]{+73.72, +39.00, -13.00},
                new double[]{+76.86, +36.00, -12.00},
                new double[]{+75.29, +37.00, -12.00},
                new double[]{+72.54, +40.00, -12.00}));


        REAL_COLORS_PALETTE.put(PURPLE, Arrays.asList(
                new double[]{+30.00, +59.00, -36.00},
                new double[]{+26.30, +31.37, +01.31},
                new double[]{+23.00, +17.74, -06.81},
                new double[]{+42.00, +31.03, -20.15},
                new double[]{+59.00, +10.52, -03.22},
                new double[]{+29.41, +46.00, -51.00},
                new double[]{+33.33, +49.00, -56.00},
                new double[]{+30.98, +47.00, -53.00},
                new double[]{+34.90, +49.00, -56.00},
                new double[]{+32.54, +48.00, -54.00},
                new double[]{+83.0, +16.0, -17.0 },
                new double[]{+80.0, 12.0, -9.0},
                new double[]{+70.0, 36.0, -16.0},
                new double[]{46,14,-30}));

        REAL_COLORS_PALETTE.put(ORANGE, Arrays.asList(
                new double[]{+59.00, +34.91, +62.97},
                new double[]{+72.00, +41.94, +64.58},
                new double[]{+61.00, +39.14, +58.03},
                new double[]{+52.41, +51.75, +51.31},
                new double[]{+63.92, +40.00, +71.00},
                new double[]{+60.00, +47.00, +69.00},
                new double[]{+62.74, +43.00, +70.00},
                new double[]{+58.82, +50.00, +68.00},
                new double[]{+61.17, +45.00, +69.00},
                new double[]{+63.0, +35.0, +20.0},
                new double[]{ 79.0, 14.0, 65.0}));




        REAL_COLORS_PALETTE.put(GOLD, Arrays.asList(
                new double[]{+85.09, -03.00, +52.00},
                new double[]{+83.52, -03.00, +68.00},
                new double[]{+72.15, +02.00, +74.00},
                new double[]{+85.09, -03.00, +61.00},
                new double[]{+68.62, +04.00, +72.00}));
    }


    private static HashMap<String, List<double[]>> setCommonPalette() {
        HashMap<String, List<double[]>> CommonPalette = new HashMap<String, List<double[]>>();
        CommonPalette.putAll(REAL_COLORS_PALETTE);

        for (String color : ADDITIONAL_PALETTE.keySet()) {
            if (REAL_COLORS_PALETTE.containsKey(color)) {
                REAL_COLORS_PALETTE.get(color).addAll(ADDITIONAL_PALETTE.get(color));
            } else {
                REAL_COLORS_PALETTE.put(color, ADDITIONAL_PALETTE.get(color));
            }
        }

        return ADDITIONAL_PALETTE;
    }

    public static HashMap<String, List<double[]>> getAdditionalPalette() {
        return ADDITIONAL_PALETTE;
    }

    public static HashMap<String, List<double[]>> getCommonPalette() {
        return COMMON_PALETTE;
    }

    public static HashMap<String, List<double[]>> getRealColorsPalette() {
        return REAL_COLORS_PALETTE;
    }
}

