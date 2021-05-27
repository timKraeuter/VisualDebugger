package no.hvl.tk.visual.debugger.domain;

import java.util.HashSet;
import java.util.Set;

public enum PrimitiveTypes {
    BYTE("byte", "java.lang.Byte"),
    SHORT("short", "java.lang.Short"),
    INT("int", "java.lang.Integer"),
    LONG("long", "java.lang.Long"),
    FLOAT("float", "java.lang.Float"),
    DOUBLE("double", "java.lang.Double"),
    CHAR("char", "java.lang.Character"),
    BOOLEAN("boolean", "java.lang.Boolean"),
    STRING("java.lang.String", "");

    private final String firstTypeName;
    private final String secondTypeName;

    PrimitiveTypes(final String typeName, final java.lang.String secondName) {
        this.firstTypeName = typeName;
        this.secondTypeName = secondName;
    }

    public static boolean isBoxedPrimitiveType(String typeName) {
        Set<java.lang.String> typeNames = new HashSet<>();
        for (final PrimitiveTypes primitiveType : PrimitiveTypes.values()) {
            if (!primitiveType.secondTypeName.isEmpty()) {
                typeNames.add(primitiveType.secondTypeName);
            }
        }
        return typeNames.contains(typeName);
    }

    public static boolean isNonBoxedPrimitiveType(String typeName) {
        Set<java.lang.String> typeNames = new HashSet<>();
        for (final PrimitiveTypes primitiveType : PrimitiveTypes.values()) {
            typeNames.add(primitiveType.firstTypeName);
        }
        return typeNames.contains(typeName);
    }
}
