package com.griddynamics.pojo.dataProcessing;

import java.io.Serializable;

/**
 * Created by npakhomova on 4/14/16.
 */
public enum ImageRoleType implements Serializable{

    CPRI("214x261.jpg"),
    CSW("214x214.jpg"),
    CADD("214x261.jpg");

    private String suffix;

    ImageRoleType(String suffix) {

        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
