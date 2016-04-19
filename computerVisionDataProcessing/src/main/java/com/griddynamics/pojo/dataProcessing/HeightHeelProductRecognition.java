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
    private HeelHeightValue isHeelHeightRecognizedValue;

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

    public void setIsHeelHeightRecognizedValue(HeelHeightValue isHeelHeightRecognizedValue) {
        this.isHeelHeightRecognizedValue = isHeelHeightRecognizedValue;
    }

    // todo rename fields
    public void calculateWarningLevel() {
        assert isHeelHeightRecognizedValue !=null && heelAttributeValue!=null;
        if (heelAttributeValue.equals(isHeelHeightRecognizedValue)){
            this.heelHeightAttributeVerification =0;
        } else {
            this.heelHeightAttributeVerification =3;

        }

    }

    public Integer getProductId() {
        return productId;
    }
}
