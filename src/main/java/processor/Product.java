package processor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    public static class FlatProductImageUpc implements Serializable{
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
        Map<String, Integer> computerVisionResult = new HashMap<>();
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

        public Map<String, Integer> getComputerVisionResult() {
            return computerVisionResult;
        }

        public void setComputerVisionResult(Map<String, Integer> computerVisionResult) {
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

    public static class Category implements Serializable{
        Integer categoryId;
        String categoryName;

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
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

    public static class Image implements Serializable{
        Integer imageId;
        String url;
        Map<String, Integer> computerVisionResult;

        public Image(Integer imageId, Map<String, Integer> computerVisionResult, String url) {
            this.imageId = imageId;
            this.computerVisionResult = computerVisionResult;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, Integer> getComputerVisionResult() {
            return computerVisionResult;
        }





        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            if (imageId != null ? !imageId.equals(image.imageId) : image.imageId != null) return false;
            if (url != null ? !url.equals(image.url) : image.url != null) return false;
            return !(computerVisionResult != null ? !computerVisionResult.equals(image.computerVisionResult) : image.computerVisionResult != null);

        }

        @Override
        public int hashCode() {
            int result = imageId != null ? imageId.hashCode() : 0;
            result = 31 * result + (url != null ? url.hashCode() : 0);
            result = 31 * result + (computerVisionResult != null ? computerVisionResult.hashCode() : 0);
            return result;
        }
    }

    public static class Upc implements Serializable{
        Integer upcId;
        Integer imageId;
        Integer colrNormalId;
        String colorNormal;
        String displayColorName;



        String imageUrl;
        TreeMap<String, Integer> computerVisionResult = new TreeMap<>();
        Integer computerVisionRecognition;
        private String description;

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setColrNormalId(Integer colrNormalId) {
            this.colrNormalId = colrNormalId;
        }

        public void setComputerVisionRecognition(Integer computerVisionRecognition) {
            this.computerVisionRecognition = computerVisionRecognition;
        }

        public void setUpcId(Integer upcId) {
            this.upcId = upcId;
        }

        public void setImageId(Integer imageId) {
            this.imageId = imageId;
        }

        public void setColorNormal(String colorNormal) {
            this.colorNormal = colorNormal;
        }


        public void setComputerVisionResult(Map<String, Integer> computerVisionResult) {
            this.computerVisionResult.putAll(computerVisionResult);

        }

        public void setDisplayColorName(String displayColorName) {
            this.displayColorName = displayColorName;
        }

        public void setDescription(String description) {
            this.description = description;
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

    public static class Statistic {
        String categoryName;
        long totalUpc;
        long suspicious;
        private long amountOfSuspiciousMulti;
        private long amountOfColorNormalIsNoDominant;
        private long amountOfColorNormalIsNotInList;

        public Statistic(String categoryName,
                         long totalUpc,
                         long suspicious,
                         long amountOfSuspiciousMulti,
                         long amountOfColorNormalIsNoDominant,
                         long amountOfColorNormalIsNotInList) {
            this.categoryName = categoryName;
            this.totalUpc = totalUpc;
            this.suspicious = suspicious;
            this.amountOfSuspiciousMulti = amountOfSuspiciousMulti;
            this.amountOfColorNormalIsNoDominant = amountOfColorNormalIsNoDominant;
            this.amountOfColorNormalIsNotInList = amountOfColorNormalIsNotInList;
        }
    }
}
