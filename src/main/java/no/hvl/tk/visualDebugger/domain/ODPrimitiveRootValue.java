package no.hvl.tk.visualDebugger.domain;

public class ODPrimitiveRootValue {
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
}
