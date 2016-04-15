package com.griddynamics.pojo.starsDomain;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by npakhomova on 4/12/16.
 */
public interface ICategory extends Serializable {

    public String getCategoriesJoinedString();

    public ShapeDetectionStrategy getShapeDetectionStrategy();

    public String getCategoryName();

    public Set<Integer> getCategoryIds() ;

    boolean isBodyContains();
}
