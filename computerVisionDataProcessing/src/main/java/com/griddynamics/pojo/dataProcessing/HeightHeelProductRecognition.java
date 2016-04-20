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
    HeelHeightValue originalHeelAttributeValue;
    boolean passedRecognition;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setOriginalHeelAttributeValue(HeelHeightValue heelAttributeValue) {
        this.originalHeelAttributeValue = heelAttributeValue;
    }


    public Integer getProductId() {
        return productId;
    }


    public HeelHeightValue getOriginalHeelAttributeValue() {
        return originalHeelAttributeValue;
    }

    public void setPassedRecognition(boolean passedRecognition) {
        this.passedRecognition = passedRecognition;
    }

    public boolean isPassedRecognition() {
        return passedRecognition;
    }
}
