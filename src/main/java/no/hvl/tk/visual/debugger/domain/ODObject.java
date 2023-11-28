package no.hvl.tk.visual.debugger.domain;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import java.util.*;
import org.jetbrains.annotations.NotNull;

/** Represents an object in an object diagram. */
public class ODObject implements Comparable<ODObject> {

  public static final String OBJECT_ID_PREFIX = "Object_";
  private final long id;

  @XmlAttribute private final String type;
  @XmlAttribute private final String variableName;

  /** All attributes of this object. */
  @XmlElement(name = "attributeValue")
  private final List<ODAttributeValue> attributeValues;

  /** All links coming from this object. */
  @XmlIDREF
  @XmlElement(name = "link")
  private final Set<ODLink> links;

  public ODObject(final long id, final String type, final String variableName) {
    this.id = id;
    this.type = type;
    this.variableName = variableName;
    this.attributeValues = new ArrayList<>();
    this.links = new HashSet<>();
  }

  /** Returns a sorted list of the objects attributes. */
  public List<ODAttributeValue> getAttributeValues() {
    this.attributeValues.sort(Comparator.comparing(ODAttributeValue::getName));
    return attributeValues;
  }

  /** Returns a read-only set of this objects links. */
  public Set<ODLink> getLinks() {
    return Collections.unmodifiableSet(this.links);
  }

  public void addLink(final ODLink linkToAdd) {
    this.links.add(linkToAdd);
  }

  public void addAttribute(final ODAttributeValue attributeToAdd) {
    this.attributeValues.add(attributeToAdd);
  }

  public String getVariableName() {
    return this.variableName;
  }

  public String getType() {
    return this.type;
  }

  public Optional<ODAttributeValue> getAttributeByName(final String attributeName) {
    return this.attributeValues.stream()
        .filter(odAttributeValue -> odAttributeValue.getName().equals(attributeName))
        .findFirst();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "Object[", "]")
        .add("type='" + this.type + "'")
        .add("variableName='" + this.variableName + "'")
        .add("attributeValues=" + this.attributeValues)
        .add("links=" + this.links)
        .toString();
  }

  @Override
  public int compareTo(@NotNull final ODObject object) {
    return Long.compare(this.id, object.id);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final ODObject odObject)) {
      return false;
    }
    return this.id == odObject.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(this.id);
  }

  @XmlID
  @XmlAttribute
  public String getId() {
    return OBJECT_ID_PREFIX + this.id;
  }
}
