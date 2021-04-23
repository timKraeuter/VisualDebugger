package domain;

/**
 * Represent the attribute value of an object in an object diagram.
 */
public class ODAttributeValue {
    // Maybe add type here

    private final String attributeName;
    private final String attributeValue;

    public ODAttributeValue(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }
}
