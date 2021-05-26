package no.hvl.tk.visual.debugger.domain;

import org.jetbrains.annotations.NotNull;

public class ODPrimitiveRootValue implements Comparable<ODPrimitiveRootValue> {
    private final String variableName;
    private final String type;
    private final String value;

    public ODPrimitiveRootValue(final String variableName, final String type, final String value) {
        this.variableName = variableName;
        this.type = type;
        this.value = value;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull ODPrimitiveRootValue other) {
        final int varNameComparison = this.getVariableName().compareTo(other.getVariableName());
        if (varNameComparison != 0) {
            return varNameComparison;
        }
        final int valueComparison = this.getValue().compareTo(other.getValue());
        if (valueComparison != 0) {
            return valueComparison;
        }
        return this.getType().compareTo(other.getType());
    }
}
