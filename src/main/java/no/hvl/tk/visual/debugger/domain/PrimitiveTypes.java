package no.hvl.tk.visual.debugger.domain;

import java.util.HashSet;
import java.util.Set;

public enum PrimitiveTypes {
    Byte("byte", "java.lang.Byte"),
    Short("short", "java.lang.Short"),
    Int("int", "java.lang.Integer"),
    Long("long", "java.lang.Long"),
    Float("float", "java.lang.Float"),
    Double("double", "java.lang.Double"),
    Char("char", "java.lang.Character"),
    Boolean("boolean", "java.lang.Boolean"),
    String("java.lang.String", "");

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

    private String getFirstTypeName() {
        return this.firstTypeName;
    }
}
