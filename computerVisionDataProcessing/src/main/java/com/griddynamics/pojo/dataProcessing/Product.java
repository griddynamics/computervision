package com.griddynamics.pojo.dataProcessing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by npakhomova on 3/6/16.
 */
public class Product implements Serializable {

    Integer productID;
    Map<Integer, Upc> upcMap = new HashMap<>();
    Category category;
    private String productDescription;

    public Integer getProductID() {
        return productID;
    }


    public Category getCategory() {
        return category;
    }


    public void merge(Product next) {
        assert this.getProductID().equals(next.getProductID());
        assert this.getCategory().equals(next.getCategory());
        upcMap.putAll(next.upcMap);
    }

    public void setCategory(Category category) {
        this.category = category;

    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void addUpc(Upc upc) {
        upcMap.put(upc.upcId, upc);
    }
}
