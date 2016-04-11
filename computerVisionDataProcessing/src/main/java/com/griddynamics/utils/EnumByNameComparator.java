package com.griddynamics.utils;

import java.util.Comparator;

/**
 * Created by npakhomova on 4/11/16.
 */
public class EnumByNameComparator implements Comparator<Enum<?>> {

    public static final Comparator<Enum<?>> INSTANCE = new EnumByNameComparator();

    public int compare(Enum<?> enum1, Enum<?> enum2) {
        return enum1.name().compareTo(enum2.name());
    }

}
