package no.hvl.tk.visual.debugger.domain;

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

    public ODAttributeValue(final String attributeName, final String attributeType, final String attributeValue) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Attribute:[", "]")
                .add("name='" + this.attributeName + "'")
                .add("type='" + this.attributeType + "'")
                .add("value='" + this.attributeValue + "'")
                .toString();
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public String getAttributeValue() {
        return this.attributeValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ODAttributeValue that = (ODAttributeValue) o;
        return Objects.equal(this.attributeName, that.attributeName) && Objects.equal(this.attributeType, that.attributeType) && Objects.equal(this.attributeValue, that.attributeValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.attributeName, this.attributeType, this.attributeValue);
    }
}
