package no.hvl.tk.visual.debugger.domain;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an Object in an object diagram.
 */
public class ODObject implements Comparable<ODObject> {

    private final long id;

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

    public ODObject(long id, final String type, final String variableName) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public Optional<ODAttributeValue> getAttributeByName(String attributeName) {
        return this.attributeValues.stream()
                                   .filter(odAttributeValue -> odAttributeValue.getAttributeName().equals(attributeName))
                                   .findFirst();
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

    @Override
    public int compareTo(@NotNull ODObject object) {
        return Long.compare(this.id, object.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ODObject odObject = (ODObject) o;
        return id == odObject.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }
}
