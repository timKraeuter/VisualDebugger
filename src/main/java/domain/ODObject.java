package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an Object in an object diagram.
 */
public class ODObject {

    /**
     * All attributes of this object.
     */
    private final Set<ODAttributeValue> attributeValues;

    /**
     * All links coming from this object.
     */
    private final Set<ODLink> links;

    public ODObject() {
        attributeValues = new HashSet<>();
        links = new HashSet<>();
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
}
