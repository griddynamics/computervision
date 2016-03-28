package com.griddynamics.pojo.dataProcessing;

import com.griddynamics.computervision.ColorDescription;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Created by npakhomova on 3/16/16.
 */
public class FlatProductImageUpc implements Serializable {
    // product rows
    Integer productID;
    private String productDescription;

    //UpcImageRows
    Integer upcId;
    Integer imageId;
    Integer colrNormalId;
    String colorNormal;
    String displayColorName;
    String imageUrl;
    TreeSet<ColorDescription> computerVisionResult = new TreeSet<>();
    Integer computerVisionRecognition;
    private String description;

    //category rows
    Integer categoryId;
    String categoryName;

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getUpcId() {
        return upcId;
    }

    public void setUpcId(Integer upcId) {
        this.upcId = upcId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getColrNormalId() {
        return colrNormalId;
    }

    public void setColrNormalId(Integer colrNormalId) {
        this.colrNormalId = colrNormalId;
    }

    public String getColorNormal() {
        return colorNormal;
    }

    public void setColorNormal(String colorNormal) {
        this.colorNormal = colorNormal;
    }

    public String getDisplayColorName() {
        return displayColorName;
    }

    public void setDisplayColorName(String displayColorName) {
        this.displayColorName = displayColorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public TreeSet<ColorDescription> getComputerVisionResult() {
        return computerVisionResult;
    }

    public void setComputerVisionResult(TreeSet<ColorDescription> computerVisionResult) {
        this.computerVisionResult = computerVisionResult;
    }

    public Integer getComputerVisionRecognition() {
        return computerVisionRecognition;
    }

    public void setComputerVisionRecognition(Integer computerVisionRecognition) {
        this.computerVisionRecognition = computerVisionRecognition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlatProductImageUpc that = (FlatProductImageUpc) o;

        if (productID != null ? !productID.equals(that.productID) : that.productID != null) return false;
        if (productDescription != null ? !productDescription.equals(that.productDescription) : that.productDescription != null)
            return false;
        if (upcId != null ? !upcId.equals(that.upcId) : that.upcId != null) return false;
        if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) return false;
        if (colrNormalId != null ? !colrNormalId.equals(that.colrNormalId) : that.colrNormalId != null)
            return false;
        if (colorNormal != null ? !colorNormal.equals(that.colorNormal) : that.colorNormal != null) return false;
        if (displayColorName != null ? !displayColorName.equals(that.displayColorName) : that.displayColorName != null)
            return false;
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null) return false;
        if (computerVisionResult != null ? !computerVisionResult.equals(that.computerVisionResult) : that.computerVisionResult != null)
            return false;
        if (computerVisionRecognition != null ? !computerVisionRecognition.equals(that.computerVisionRecognition) : that.computerVisionRecognition != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return !(categoryName != null ? !categoryName.equals(that.categoryName) : that.categoryName != null);

    }

    @Override
    public int hashCode() {
        int result = productID != null ? productID.hashCode() : 0;
        result = 31 * result + (productDescription != null ? productDescription.hashCode() : 0);
        result = 31 * result + (upcId != null ? upcId.hashCode() : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (colrNormalId != null ? colrNormalId.hashCode() : 0);
        result = 31 * result + (colorNormal != null ? colorNormal.hashCode() : 0);
        result = 31 * result + (displayColorName != null ? displayColorName.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (computerVisionResult != null ? computerVisionResult.hashCode() : 0);
        result = 31 * result + (computerVisionRecognition != null ? computerVisionRecognition.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        return result;
    }
}
