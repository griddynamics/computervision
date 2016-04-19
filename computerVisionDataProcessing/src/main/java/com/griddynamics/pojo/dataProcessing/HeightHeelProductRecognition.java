package com.griddynamics.pojo.dataProcessing;

import com.griddynamics.computervision.HeelHeightValue;

import java.io.Serializable;

/**
 * Created by npakhomova on 4/18/16.
 */
public class HeightHeelProductRecognition implements Serializable {

    Integer productId;
    String productDescription;
    String imageURL;
    HeelHeightValue heelAttributeValue;

    Integer heelHeightAttributeVerification; // 0 is ok. 3 is false
    HeelHeightValue cvRecognizedValue;
    private double dimentionsRatio;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setHeelAttributeValue(HeelHeightValue heelAttributeValue) {
        this.heelAttributeValue = heelAttributeValue;
    }

    public void setCvRecognizedValue(HeelHeightValue cvRecognizedValue) {
        this.cvRecognizedValue = cvRecognizedValue;
    }

    // todo rename fields
    public void calculateWarningLevel() {
        assert cvRecognizedValue !=null && heelAttributeValue!=null;
        if (heelAttributeValue.equals(cvRecognizedValue)){
            this.heelHeightAttributeVerification =0;
        } else {
            this.heelHeightAttributeVerification =3;

        }

    }

    public Integer getProductId() {
        return productId;
    }

    public void setDimentionsRatio(double dimentionsRatio) {
        this.dimentionsRatio = dimentionsRatio;
    }

    public double getDimentionsRatio() {
        return dimentionsRatio;
    }

    public HeelHeightValue getHeelAttributeValue() {
        return heelAttributeValue;
    }
}
