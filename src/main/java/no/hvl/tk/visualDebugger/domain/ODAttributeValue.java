package no.hvl.tk.visualDebugger.domain;

import com.google.common.base.Objects;

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

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ODAttributeValue that = (ODAttributeValue) o;
        return Objects.equal(attributeName, that.attributeName) && Objects.equal(attributeType, that.attributeType) && Objects.equal(attributeValue, that.attributeValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeName, attributeType, attributeValue);
    }
}
