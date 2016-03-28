package com.griddynamics.pojo.dataProcessing;

/**
 * Created by npakhomova on 3/16/16.
 */
public class Statistic {
    String categoryName;
    private int categoryId;
    long totalUpc;
    long suspicious;
    private long amountOfSuspiciousMulti;
    private long amountOfColorNormalIsNoDominant;
    private long amountOfColorNormalIsNotInList;

    public Statistic(String categoryName,
                     int categoryId,
                     long totalUpc,
                     long suspicious,
                     long amountOfSuspiciousMulti,
                     long amountOfColorNormalIsNoDominant,
                     long amountOfColorNormalIsNotInList) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.totalUpc = totalUpc;
        this.suspicious = suspicious;
        this.amountOfSuspiciousMulti = amountOfSuspiciousMulti;
        this.amountOfColorNormalIsNoDominant = amountOfColorNormalIsNoDominant;
        this.amountOfColorNormalIsNotInList = amountOfColorNormalIsNotInList;
    }
}
