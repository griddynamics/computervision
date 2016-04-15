package com.griddynamics.computervision;

/**
 * Created by npakhomova on 4/13/16.
 */
public enum BodyParts {
    FACE("FACE"),
    BODY("BODY");

    private final String part;

    BodyParts(String part) {
        this.part = part;
    }

    @Override
    public String toString() {
        return part;
    }
}
