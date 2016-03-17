package pojo;

import java.io.Serializable;

/**
 * Created by npakhomova on 3/16/16.
 */
public class Category implements Serializable {
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
