package com.griddynamics.computervision;

public class DeltaE {

    private static double e94(double[] lab1, double[] lab2) {

        checkLabs(lab1, lab2);

        double l1 = lab1[0];
        double a1 = lab1[1];
        double b1 = lab1[2];

        double l2 = lab2[0];
        double a2 = lab2[1];
        double b2 = lab2[2];

        double ld = l1 - l2;

        double c1 = Math.sqrt(Math.pow(a1,2) + Math.pow(b1,2));
        double c2 = Math.sqrt(Math.pow(a2,2) + Math.pow(b2,2));
        double cd = c1 - c2;

        double ad = a1 - a2;
        double bd = b1 - b2;

        double kl = 1;
        double kc = 0.045;
        double kh = 0.015;

        double sl = 1;
        double sc = 1+ kc * c1;
        double sh = 1+ kh * c1;

        double hd = Math.sqrt(Math.pow(ad,2) + Math.pow(bd,2) + Math.pow(cd,2));

        double e94 = Math.sqrt(Math.pow((ld/(kl * sl)),2) + Math.pow((cd/(kc*sc)),2) + Math.pow(hd/(kh * sh),2));

        return e94;
    }

    private static void checkLabs(double[] lab1, double[] lab2) {
        if (lab1.length != 3 || lab2.length != 3) {
            throw new IllegalArgumentException("Length of Labs arrays must be 3");
        }

        if (lab1[0] > 100 || lab2[0] > 100) {
            throw new IllegalArgumentException("L in Labs arrays must be in range from 0 to 100 (CIE L*a*b* format)");
        }

        if (lab1[1] > 127 || lab2[1] > 127 || lab2[2] > 127 || lab2[2] > 127) {
            throw new IllegalArgumentException("a and b in Labs arrays must be in range from 0 to 127 (CIE L*a*b* format)");
        }
    }

    /**
     * Compute the CIEDE2000 color-difference between the sample color with
     * CIELab coordinates 'sample' and a standard color with CIELab coordinates
     * 'std'
     *
     * Based on the article:
     * "The CIEDE2000 Color-Difference Formula: Implementation Notes,
     * Supplementary Test Data, and Mathematical Observations,", G. Sharma,
     * W. Wu, E. N. Dalal, submitted to Color Research and Application,
     * January 2004.
     * available at http://www.ece.rochester.edu/~gsharma/ciede2000/
     */
    public static double deltaE2000(double[] lab1, double[] lab2)
    {
        checkLabs(lab1, lab2);

        double L1 = lab1[0];
        double a1 = lab1[1];
        double b1 = lab1[2];

        double L2 = lab2[0];
        double a2 = lab2[1];
        double b2 = lab2[2];

        // Cab = sqrt(a^2 + b^2)
        double Cab1 = Math.sqrt(a1 * a1 + b1 * b1);
        double Cab2 = Math.sqrt(a2 * a2 + b2 * b2);

        // CabAvg = (Cab1 + Cab2) / 2
        double CabAvg = (Cab1 + Cab2) / 2;

        // G = 1 + (1 - sqrt((CabAvg^7) / (CabAvg^7 + 25^7))) / 2
        double CabAvg7 = Math.pow(CabAvg, 7);
        double G = 1 + (1 - Math.sqrt(CabAvg7 / (CabAvg7 + 6103515625.0))) / 2;

        // ap = G * a
        double ap1 = G * a1;
        double ap2 = G * a2;

        // Cp = sqrt(ap^2 + b^2)
        double Cp1 = Math.sqrt(ap1 * ap1 + b1 * b1);
        double Cp2 = Math.sqrt(ap2 * ap2 + b2 * b2);

        // CpProd = (Cp1 * Cp2)
        double CpProd = Cp1 * Cp2;

        // hp1 = atan2(b1, ap1)
        double hp1 = Math.atan2(b1, ap1);
        // ensure hue is between 0 and 2pi
        if (hp1 < 0) {
            // hp1 = hp1 + 2pi
            hp1 += 6.283185307179586476925286766559;
        }

        // hp2 = atan2(b2, ap2)
        double hp2 = Math.atan2(b2, ap2);
        // ensure hue is between 0 and 2pi
        if (hp2 < 0) {
            // hp2 = hp2 + 2pi
            hp2 += 6.283185307179586476925286766559;
        }

        // dL = L2 - L1
        double dL = L2 - L1;

        // dC = Cp2 - Cp1
        double dC = Cp2 - Cp1;

        // computation of hue difference
        double dhp = 0.0;
        // set hue difference to zero if the product of chromas is zero
        if (CpProd != 0) {
            // dhp = hp2 - hp1
            dhp = hp2 - hp1;
            if (dhp > Math.PI) {
                // dhp = dhp - 2pi
                dhp -= 6.283185307179586476925286766559;
            } else if (dhp < -Math.PI) {
                // dhp = dhp + 2pi
                dhp += 6.283185307179586476925286766559;
            }
        }

        // dH = 2 * sqrt(CpProd) * sin(dhp / 2)
        double dH = 2 * Math.sqrt(CpProd) * Math.sin(dhp / 2);

        // weighting functions
        // Lp = (L1 + L2) / 2 - 50
        double Lp = (L1 + L2) / 2 - 50;

        // Cp = (Cp1 + Cp2) / 2
        double Cp = (Cp1 + Cp2) / 2;

        // average hue computation
        // hp = (hp1 + hp2) / 2
        double hp = (hp1 + hp2) / 2;

        // identify positions for which abs hue diff exceeds 180 degrees
        if (Math.abs(hp1 - hp2) > Math.PI) {
            // hp = hp - pi
            hp -= Math.PI;
        }
        // ensure hue is between 0 and 2pi
        if (hp < 0) {
            // hp = hp + 2pi
            hp += 6.283185307179586476925286766559;
        }

        // LpSqr = Lp^2
        double LpSqr = Lp * Lp;

        // Sl = 1 + 0.015 * LpSqr / sqrt(20 + LpSqr)
        double Sl = 1 + 0.015 * LpSqr / Math.sqrt(20 + LpSqr);

        // Sc = 1 + 0.045 * Cp
        double Sc = 1 + 0.045 * Cp;

        // T = 1 - 0.17 * cos(hp - pi / 6) +
        //       + 0.24 * cos(2 * hp) +
        //       + 0.32 * cos(3 * hp + pi / 30) -
        //       - 0.20 * cos(4 * hp - 63 * pi / 180)
        double hphp = hp + hp;
        double T = 1 - 0.17 * Math.cos(hp - 0.52359877559829887307710723054658)
                + 0.24 * Math.cos(hphp)
                + 0.32 * Math.cos(hphp + hp + 0.10471975511965977461542144610932)
                - 0.20 * Math.cos(hphp + hphp - 1.0995574287564276334619251841478);

        // Sh = 1 + 0.015 * Cp * T
        double Sh = 1 + 0.015 * Cp * T;

        // deltaThetaRad = (pi / 3) * e^-(36 / (5 * pi) * hp - 11)^2
        double powerBase = hp - 4.799655442984406;
        double deltaThetaRad = 1.0471975511965977461542144610932 * Math.exp(-5.25249016001879 * powerBase * powerBase);

        // Rc = 2 * sqrt((Cp^7) / (Cp^7 + 25^7))
        double Cp7 = Math.pow(Cp, 7);
        double Rc = 2 * Math.sqrt(Cp7 / (Cp7 + 6103515625.0));

        // RT = -sin(delthetarad) * Rc
        double RT = -Math.sin(deltaThetaRad) * Rc;

        // de00 = sqrt((dL / Sl)^2 + (dC / Sc)^2 + (dH / Sh)^2 + RT * (dC / Sc) * (dH / Sh))
        double dLSl = dL / Sl;
        double dCSc = dC / Sc;
        double dHSh = dH / Sh;
        return Math.sqrt(dLSl * dLSl + dCSc * dCSc + dHSh * dHSh + RT * dCSc * dHSh);
    }
}
