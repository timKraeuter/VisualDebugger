package no.hvl.tk.visualDebugger.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents an Object in an object diagram.
 */
public class ODObject {

    private final String type;
    private final String variableName;

    /**
     * All attributes of this object.
     */
    private final Set<ODAttributeValue> attributeValues;

    /**
     * All links coming from this object.
     */
    private final Set<ODLink> links;

    public ODObject(final String type, final String variableName) {
        this.type = type;
        this.variableName = variableName;
        this.attributeValues = new HashSet<>();
        this.links = new HashSet<>();
    }

    /**
     * Returns a read-only set of this objects attributes.
     */
    public Set<ODAttributeValue> getAttributeValues() {
        return Collections.unmodifiableSet(attributeValues);
    }

    /**
     * Returns a read-only set of this objects links.
     */
    public Set<ODLink> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    public void addLink(ODLink linkToAdd) {
        this.links.add(linkToAdd);
    }

    public void addAttribute(ODAttributeValue attributeToAdd) {
        this.attributeValues.add(attributeToAdd);
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Object[", "]")
                .add("type='" + type + "'")
                .add("variableName='" + variableName + "'")
                .add("attributeValues=" + attributeValues)
                .add("links=" + links)
                .toString();
    }
}
