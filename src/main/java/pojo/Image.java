package pojo;


import processing.Shapes;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Created by npakhomova on 3/16/16.
 */
public class Image implements Serializable {
    Integer imageId;
    String url;
    private Shapes shape;
    TreeSet<ColorDescription> computerVisionResult;


    public Image(Integer imageId, TreeSet<ColorDescription> computerVisionResult, String url) {
        this.imageId = imageId;
        this.computerVisionResult = computerVisionResult;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public TreeSet<ColorDescription> getComputerVisionResult() {
        return computerVisionResult;
    }

    public Shapes getShape() {
        return shape;
    }

    public void setShape(Shapes shape) {
        this.shape = shape;
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
