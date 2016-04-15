package com.griddynamics.pojo.dataProcessing;

import com.griddynamics.computervision.ColorDescription;
import com.griddynamics.computervision.Shapes;
import com.griddynamics.utils.VisualRecognitionUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by npakhomova on 3/16/16.
 */
public class Upc implements Serializable {

    // usially one upc has 2 images - swatch and primary
    Map<ImageRoleType,Image> images = new HashMap<>();


    Integer upcId;

    Integer colorNormalId;
    String colorNormal;
    String displayColorName;


    Integer computerVisionRecognition = -1 ;

    String description;


    public void merge(Upc upc) {
        assert upcId.equals(upc.upcId);
        images.putAll(upc.images);
        this.computerVisionRecognition = Math.max(computerVisionRecognition, upc.computerVisionRecognition);


    }


    public Map<ImageRoleType, Image> getImages() {
        return images;
    }

    public Integer getUpcId() {
        return upcId;
    }

    public Integer getColorNormalId() {
        return colorNormalId;
    }

    public String getColorNormal() {
        return colorNormal;
    }

    public String getDisplayColorName() {
        return displayColorName;
    }

    public Integer getComputerVisionRecognition() {
        return computerVisionRecognition;
    }

    public String getDescription() {
        return description;
    }

    public void setColorNormalId(Integer colorNormalId) {
        this.colorNormalId = colorNormalId;
    }

    public void setComputerVisionRecognition(Integer computerVisionRecognition) {
        this.computerVisionRecognition = computerVisionRecognition;
    }

    public void setUpcId(Integer upcId) {
        this.upcId = upcId;
    }


    public void setColorNormal(String colorNormal) {
        this.colorNormal = colorNormal;
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
        if (colorNormalId != null ? !colorNormalId.equals(upc.colorNormalId) : upc.colorNormalId != null) return false;
        if (colorNormal != null ? !colorNormal.equals(upc.colorNormal) : upc.colorNormal != null) return false;
        if (displayColorName != null ? !displayColorName.equals(upc.displayColorName) : upc.displayColorName != null)
            return false;
        return !(description != null ? !description.equals(upc.description) : upc.description != null);

    }

    @Override
    public int hashCode() {
        int result = upcId != null ? upcId.hashCode() : 0;
        result = 31 * result + (colorNormalId != null ? colorNormalId.hashCode() : 0);
        result = 31 * result + (colorNormal != null ? colorNormal.hashCode() : 0);
        result = 31 * result + (displayColorName != null ? displayColorName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }


    public void addImage(Image processedImageResult) {
        this.computerVisionRecognition = Math.max(computerVisionRecognition, VisualRecognitionUtil.evaluateRecognitionResult(colorNormal, processedImageResult.getComputerVisionResult()));
        this.images.put(processedImageResult.imageRoleType, processedImageResult);

    }

    public Integer getPrimaryImageId() {
        if (images.containsKey(ImageRoleType.CPRI)) {
            return images.get(ImageRoleType.CPRI).getImageId();
        }
        return -1;
    }
}
