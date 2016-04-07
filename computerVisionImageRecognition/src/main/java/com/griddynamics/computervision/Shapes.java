package com.griddynamics.computervision;

public enum Shapes {

    RECTANGLE("RECTANGLE"),
    SQUARE("SQUARE"),
    ROUNDED("ROUNDED"),
    TRIANGLE("TRIANGLE"),
    PENTA("PENTA"),
    HEXA("HEXA"),
    UNDEFINED("UNDEFINED");

    String name;

    Shapes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}