package pojo;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Created by npakhomova on 3/16/16.
 */
public class Upc implements Serializable {
    Integer upcId;
    Integer imageId;
    Integer colrNormalId;
    String colorNormal;
    String displayColorName;


    String imageUrl;
    TreeSet<ColorDescription> computerVisionResult = new TreeSet<>();
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


    public void setComputerVisionResult(TreeSet<ColorDescription> computerVisionResult) {
        this.computerVisionResult.addAll(computerVisionResult);

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
