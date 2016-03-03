package utils;

public class ColorAndPercents implements Comparable<ColorAndPercents> {

    private double[] color;
    private double percent;
    private int index;

    public ColorAndPercents(int index, double[] color, double percent) {
        this.color = color;
        this.percent = percent;
        this.index = index;
    }

    public double[] getColorCode() {
        return color;
    }

    public double getPercent() {
        return percent;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int compareTo(ColorAndPercents o) {
        return o.getPercent() > this.getPercent() ? 1 : -1;
    }
}
