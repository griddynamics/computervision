package com.griddynamics.computervision;

/**
 * Created by npakhomova on 4/18/16.
 */
public class HeightHeelValueResult {

    HeelHeightMCOMValue value;
    double heigthWithDimention;

    public HeightHeelValueResult(HeelHeightMCOMValue value, double heigthWithDimention) {
        this.value = value;
        this.heigthWithDimention = heigthWithDimention;
    }

    public HeelHeightMCOMValue getValue() {
        return value;
    }

    public double getHeigthWithDimention() {
        return heigthWithDimention;
    }
}
