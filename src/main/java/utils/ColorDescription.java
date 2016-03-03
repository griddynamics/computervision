package utils;

import java.util.ArrayList;

public class ColorDescription implements Comparable<ColorDescription> {

    private double[] color;
    private double percent;
    private String name;
    private int index;
    private ArrayList<double[]> colors = new ArrayList<>();

    public ColorDescription(String name, double[] color, double percent) {
        this.color = color;
        this.percent = percent;
        this.colors.add(color);
        this.name = name;
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

    public ArrayList<double[]> getColors() {
        return colors;
    }

    public void addColorCode(double[] d) {
        colors.add(d);
    }

    public void addPercent(double p) {
        percent = percent + p;
    }

    @Override
    public int compareTo(ColorDescription o) {
        return o.getPercent() > this.getPercent() ? 1 : -1;
    }
}