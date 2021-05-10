package no.hvl.tk.visualDebugger.domain;

import java.util.StringJoiner;

/**
 * Represent the attribute value of an object in an object diagram.
 */
public class ODAttributeValue {
    // Maybe add type here

    private final String attributeName;
    private final String attributeType;
    private final String attributeValue;

    public ODAttributeValue(String attributeName, String attributeType, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Attribute:[", "]")
                .add("name='" + attributeName + "'")
                .add("type='" + attributeType + "'")
                .add("value='" + attributeValue + "'")
                .toString();
    }
}
