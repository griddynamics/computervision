package com.griddynamics.pojo.starsDomain;

import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by npakhomova on 4/12/16.
 */
public class Category implements ICategory {
    private static Joiner joiner = Joiner.on(", ");

    private Set<Integer> categoryIds = new HashSet<Integer>();
    private String categoryName;

    public Category(String categoryName, Integer categoryId) {
        this.categoryName = categoryName;
        this.categoryIds.add(categoryId);
    }

    @Override
    public String getCategoriesJoinedString() {
        //
        return joiner.join(categoryIds);
    }

    @Override
    public ShapeDetectionStrategy getShapeDetectionStrategy() {
        return null;
    }

    @Override
    public String getCategoryName() {
        return categoryName.replaceAll(" ","_");
    }

    public Set<Integer> getCategoryIds() {
        return categoryIds;
    }

    @Override
    public boolean isBodyContains() {
        return false;
    }

}
