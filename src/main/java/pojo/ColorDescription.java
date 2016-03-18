package pojo;


import com.google.common.base.Joiner;
import org.apache.commons.lang.ArrayUtils;
import utils.DeltaE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ColorDescription implements Comparable<ColorDescription> , Serializable {

    // list of arrays of primitive. face palm
    private List<double[]> colors;
    private int percent;
    private String name;
    private double distanceFromColorNormal;
    private String labColorDesctiption;

    private static Joiner joiner = Joiner.on(", ");

    public ColorDescription(String name, double[] colorLab, int percent) {
        this.colors = new ArrayList<double[]>();
        this.colors.add(colorLab);
        this.percent = percent;
        this.name = name;
        this.labColorDesctiption = "[ "+ joiner.join( ArrayUtils.toObject(colorLab) ) +" ]";
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
        colors.addAll(colorDescription.getColors());
        // in case of merge there are only 1 element in array
        assert colorDescription.getColors().size()==1;
        labColorDesctiption = labColorDesctiption + "[ "+joiner.join( ArrayUtils.toObject(colorDescription.getColors().iterator().next()) ) +" ]";
    }

    public List<double[]> getColors() {
        return colors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorDescription that = (ColorDescription) o;

        if (percent != that.percent) return false;
        if (Double.compare(that.distanceFromColorNormal, distanceFromColorNormal) != 0) return false;
        if (colors != null ? !colors.equals(that.colors) : that.colors != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(labColorDesctiption != null ? !labColorDesctiption.equals(that.labColorDesctiption) : that.labColorDesctiption != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = colors != null ? colors.hashCode() : 0;
        result = 31 * result + percent;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(distanceFromColorNormal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (labColorDesctiption != null ? labColorDesctiption.hashCode() : 0);
        return result;
    }

    public void setDistanceFromColorNormal(List<double[]> colorNormalVariations) {
        double min = Double.MAX_VALUE;
        for ( double[] color: colors) {
        for (double[] colorNormal : colorNormalVariations){
            double diff = DeltaE.deltaE2000(color, colorNormal);
            min = diff < min? diff : min;

            }
        }
        BigDecimal bigDecimal = new BigDecimal(min);
        this.distanceFromColorNormal= bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getDistanceFromColorNormal() {
        return distanceFromColorNormal;
    }

    public String getLabColorDesctiption() {
        return labColorDesctiption;
    }

    public String getLabColorDesctiption() {
        return labColorDesctiption;
    }

//    public static class ColorPoint{
//        double[] colorCode;
//        int percenct;
//        double
//
//
//    }
}