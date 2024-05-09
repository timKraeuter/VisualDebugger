package no.hvl.tk.visual.debugger.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ObjectDiagram {
  @JsonProperty private final Set<ODObject> objects;

  @JsonProperty private final Set<ODLink> links;

  @JsonProperty private final Set<ODPrimitiveRootValue> primitiveRootValues;

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
