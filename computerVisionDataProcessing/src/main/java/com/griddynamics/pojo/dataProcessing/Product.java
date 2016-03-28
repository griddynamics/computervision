package com.griddynamics.pojo.dataProcessing;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by npakhomova on 3/6/16.
 */
public class Product implements Serializable {

    Integer productID;
    Set<Upc> upcSet = new HashSet<>();
    Category category;
    private String productDescription;

    public Product(FlatProductImageUpc flatProduct) {
        productID = flatProduct.getProductID();
        productDescription = flatProduct.getProductDescription();

        category = new Category();
        category.setCategoryId(flatProduct.getCategoryId());
        category.setCategoryName(flatProduct.getCategoryName());

        Upc upc = new Upc();
        upc.setImageId(flatProduct.getImageId());
        upc.setUpcId(flatProduct.getUpcId());
        upc.setColorNormal(flatProduct.getColorNormal());
        upc.setColrNormalId(flatProduct.getColrNormalId());
        upc.setComputerVisionRecognition(flatProduct.getComputerVisionRecognition());
        upc.setComputerVisionResult(flatProduct.getComputerVisionResult());
        upc.setDescription(flatProduct.getDescription());
        upc.setImageUrl(flatProduct.getImageUrl());
        upc.setDisplayColorName(flatProduct.getDisplayColorName());

        upcSet.add(upc);

    }

    public Integer getProductID() {
        return productID;
    }


    public Set<Upc> getUpcSet() {
        return upcSet;
    }

    public Category getCategory() {
        return category;
    }


    public void merge(Product next) {
        assert this.getProductID().equals(next.getProductID());
        assert this.getCategory().equals(next.getCategory());

        upcSet.addAll(next.getUpcSet());
    }

}
