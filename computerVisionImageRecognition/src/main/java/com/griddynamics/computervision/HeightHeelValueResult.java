package com.griddynamics.computervision;

/**
 * Created by npakhomova on 4/18/16.
 */
public class HeightHeelValueResult {

    HeelHeightValue value;
    double heigthWithDimention;

    public HeightHeelValueResult(HeelHeightValue value, double heigthWithDimention) {
        this.value = value;
        this.heigthWithDimention = heigthWithDimention;
    }

    public HeelHeightValue getValue() {
        return value;
    }

    public double getHeigthWithDimention() {
        return heigthWithDimention;
    }
}
