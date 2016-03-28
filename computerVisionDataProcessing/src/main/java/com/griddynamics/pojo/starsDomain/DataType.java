package com.griddynamics.pojo.starsDomain;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * If referring to the value persisted in the db, use {@link DataType#getValue()} in case one day the enum names
 * don't exactly match the persisted values.
 * <p>
 *
 * <p>
 * Breaks ALLCAPS convention for enum names to match values in db. Easiest way for Hibernate to map enum fields on
 * domain objects.
 * </p>
 *
 * @author ksmirnov@griddynamics.com
 */
public enum DataType {
    Checkbox("Checkbox", Boolean.class),
    Combo("Combo", String.class),
    Date("Date", String.class),
    Image("Image", String.class),
    LargeText("LargeText", String.class),
    Number("Number", Integer.class),
    Double("Double", BigDecimal.class),
    SingleLine("SingleLine", String.class),
    Table("Table", String.class),
    Text("Text", String.class),
    Range("Range", String.class);

    private static final Map<String, DataType> reverseMap;
    static {
        Map<String, DataType> m = new HashMap<String, DataType>();
        for (DataType dt : DataType.values()) {
            m.put(dt.value, dt);
        }
        reverseMap = Collections.unmodifiableMap(m);
    }

    private final String value;
    private final Class type;

    private DataType(String value, Class type) {
        this.value = value;
        this.type = type;
    }

    /**
     * @return Specifically returns the persisted value, though same as {@link #name()}. Exists anyways, in case the
     *         enum name one day differs from the actual persisted value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Extrapolated from various getTypeFromDataType(DataType) methods now removed from all locations in code.
     *
     * <table>
     * <tr><th>{@link DataType}</th><th>{@link Type} class</th></tr>
     * <tr><td>{@link #Checkbox}</td><td>{@link Boolean}.class</td></tr>
     * <tr><td>{@link #Number}</td><td>{@link Integer}.class</td></tr>
     * <tr><td>(everything else...)</td><td>{@link String}.class</td></tr>
     * </table>
     *
     * @return {@link Type} of data in java terms
     */
    public Class getType() {
        return type;
    }

    public static DataType getDataTypeByValue(String val) {
        return reverseMap.get(val);
    }

    public boolean isHandledInMassChange() {
        switch(this) {
            case Range:
            case Table:
            case Image:
                return false;
            default:
                return true;
        }
    }
}
