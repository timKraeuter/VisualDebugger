package no.hvl.tk.visual.debugger.domain;

import org.apache.commons.lang3.StringUtils;
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
        return this.variableName;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int compareTo(@NotNull final ODPrimitiveRootValue other) {
        final int varNameComparison = this.getVariableName().compareTo(other.getVariableName());
        if (varNameComparison != 0) {
            return varNameComparison;
        }
        // Null-safe since values could be null.
        final int valueComparison = StringUtils.compare(this.getValue(), other.getValue());
        if (valueComparison != 0) {
            return valueComparison;
        }
        return this.getType().compareTo(other.getType());
    }
}
