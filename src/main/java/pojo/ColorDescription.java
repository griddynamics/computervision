package pojo;


import utils.DeltaE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class ColorDescription implements Comparable<ColorDescription> , Serializable {

    private double[] color;
    private int percent;
    private String name;
    private double distanceFromColorNormal;

    public ColorDescription(String name, double[] color, int percent) {
        this.color = color;
        this.percent = percent;
        this.color =color;
        this.name = name;
    }

    public double[] getColorInLab() {
        double[] lab1 = new double[]{
                color[0] / 2.55,
                color[1] - 128,
                color[2] - 128
        };
        return lab1;
    }

    public double getPercent() {
        return percent;
    }

    public String getName() {
        return name;
    }


    @Override
    public int compareTo(ColorDescription o) {
        return o.getPercent() > this.getPercent() ? 1 : -1;
    }

    public void merge(ColorDescription colorDescription) {
        assert name.equals(colorDescription.name);
        percent = percent + colorDescription.percent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorDescription that = (ColorDescription) o;

        if (Double.compare(that.percent, percent) != 0) return false;
        if (!Arrays.equals(color, that.color)) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = color != null ? Arrays.hashCode(color) : 0;
        temp = Double.doubleToLongBits(percent);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public void setDistanceFromColorNormal(List<double[]> colorNormalVariations) {
        double min = Double.MAX_VALUE;
        for (double[] colorNormal : colorNormalVariations){
            double diff = DeltaE.deltaE2000(color, colorNormal);
            min = diff < min? diff : min;

        }
        BigDecimal bigDecimal = new BigDecimal(min);
        this.distanceFromColorNormal= bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getDistanceFromColorNormal() {
        return distanceFromColorNormal;
    }
}