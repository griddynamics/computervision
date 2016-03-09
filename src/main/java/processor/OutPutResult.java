package processor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by npakhomova on 3/6/16.
 */
public class OutPutResult implements Serializable {

    Double productID;
    Set<Upc> upcSet = new HashSet<>();

    Category category;
    private String productDescription;
//    String FOB; // where to get???
    //    String pictureName;

    public Double getProductID() {
        return productID;
    }

    public void setProductID(Double productID) {
        this.productID = productID;
    }


    public Set<Upc> getUpcSet() {
        return upcSet;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public void merge(OutPutResult next) {
        assert this.getProductID().equals(next.getProductID());
        assert this.getCategory().equals(next.getCategory());

        upcSet.addAll(next.getUpcSet());
    }

    public void addUpc(Upc upc) {
        upcSet.add(upc);

    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public static class Category implements Serializable{
        Object categoryId;
        String categoryName;

        public void setCategoryId(Object categoryId) {
            this.categoryId = categoryId;
        }

        public Object getCategoryId() {
            return categoryId;
        }


        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Category category = (Category) o;

            if (categoryId != null ? !categoryId.equals(category.categoryId) : category.categoryId != null)
                return false;
            return !(categoryName != null ? !categoryName.equals(category.categoryName) : category.categoryName != null);

        }

        @Override
        public int hashCode() {
            int result = categoryId != null ? categoryId.hashCode() : 0;
            result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
            return result;
        }
    }

    public static class Upc implements Serializable{
        Double upcId;
        Double imageId;
        Double colrNormalId;
        String colorNormal;
        String displayColorName;
        Map<String, Integer> computerVisionResult = new HashMap<>();
        private String description;


        public Double getUpcId() {
            return upcId;
        }

        public void setUpcId(Double upcId) {
            this.upcId = upcId;
        }

        public Double getImageId() {
            return imageId;
        }

        public void setImageId(Double imageId) {
            this.imageId = imageId;
        }

        public Double getColrNormalId() {
            return colrNormalId;
        }

        public void setColrNormalId(Double colrNormalId) {
            this.colrNormalId = colrNormalId;
        }

        public String getColorNormal() {
            return colorNormal;
        }

        public void setColorNormal(String colorNormal) {
            this.colorNormal = colorNormal;
        }

        public Map<String, Integer> getComputerVisionResult() {
            return computerVisionResult;
        }

        public void setComputerVisionResult(Map<String, Integer> computerVisionResult) {
            this.computerVisionResult.putAll(computerVisionResult);

        }

        public void setDisplayColorName(String displayColorName) {
            this.displayColorName = displayColorName;
        }

        public String getDisplayColorName() {
            return displayColorName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Upc upc = (Upc) o;

            if (upcId != null ? !upcId.equals(upc.upcId) : upc.upcId != null) return false;
            if (imageId != null ? !imageId.equals(upc.imageId) : upc.imageId != null) return false;
            if (colrNormalId != null ? !colrNormalId.equals(upc.colrNormalId) : upc.colrNormalId != null) return false;
            if (colorNormal != null ? !colorNormal.equals(upc.colorNormal) : upc.colorNormal != null) return false;
            if (displayColorName != null ? !displayColorName.equals(upc.displayColorName) : upc.displayColorName != null)
                return false;
            if (computerVisionResult != null ? !computerVisionResult.equals(upc.computerVisionResult) : upc.computerVisionResult != null)
                return false;
            return !(description != null ? !description.equals(upc.description) : upc.description != null);

        }

        @Override
        public int hashCode() {
            int result = upcId != null ? upcId.hashCode() : 0;
            result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
            result = 31 * result + (colrNormalId != null ? colrNormalId.hashCode() : 0);
            result = 31 * result + (colorNormal != null ? colorNormal.hashCode() : 0);
            result = 31 * result + (displayColorName != null ? displayColorName.hashCode() : 0);
            result = 31 * result + (computerVisionResult != null ? computerVisionResult.hashCode() : 0);
            result = 31 * result + (description != null ? description.hashCode() : 0);
            return result;
        }
    }
}
