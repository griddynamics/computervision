package com.griddynamics.computervision;

/**
 * Created by npakhomova on 4/18/16.
 */
public enum HeelHeightValue {

    Flat("Flat 0-1\"", false),
//    High("High 3-4\"", true),
//    Mid("Mid 2-3\"", true),
//    Low("Low 1-2\"", false),
    Ultra_High("Ultra High 4\" & over", true);

    private String value;
    private boolean isHeelExist;

    HeelHeightValue(String mid, boolean isHeelExist) {

        this.value = mid;
        this.isHeelExist = isHeelExist;
    }

    public boolean isHeelExist() {
        return isHeelExist;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return this.getValue();
    }

    public static HeelHeightValue getEnum(String value) {
        for (HeelHeightValue v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
