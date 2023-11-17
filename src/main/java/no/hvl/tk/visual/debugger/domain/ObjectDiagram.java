package no.hvl.tk.visual.debugger.domain;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlRootElement
public class ObjectDiagram {
  @XmlElement(name = "object")
  private final Set<ODObject> objects;

  @XmlElement(name = "link")
  private final Set<ODLink> links;

  @XmlElement(name = "primitiveRootValue")
  private final Set<ODPrimitiveRootValue> primitiveRootValues;

  public ObjectDiagram() {
    this.objects = new LinkedHashSet<>();
    this.links = new LinkedHashSet<>();
    this.primitiveRootValues = new LinkedHashSet<>();
  }

  public Set<ODObject> getObjects() {
    return Collections.unmodifiableSet(this.objects);
  }

  public Set<ODLink> getLinks() {
    return links;
  }

  public Set<ODPrimitiveRootValue> getPrimitiveRootValues() {
    return Collections.unmodifiableSet(this.primitiveRootValues);
  }

  public void addObject(final ODObject obj) {
    this.objects.add(obj);
  }

  public void addLink(final ODLink link) {
    this.links.add(link);
  }

  public void addPrimitiveRootValue(final ODPrimitiveRootValue primitiveRootValue) {
    this.primitiveRootValues.add(primitiveRootValue);
  }

  public boolean isEmpty() {
    return this.objects.isEmpty() && this.links.isEmpty() && this.primitiveRootValues.isEmpty();
  }
}
