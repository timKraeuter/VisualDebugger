package no.hvl.tk.visual.debugger.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import java.util.StringJoiner;

/** Represents the attribute value of an object in an object diagram. */
public class ODAttributeValue {

  @JsonProperty private final String name;
  @JsonProperty private final String type;
  @JsonProperty private final String value;

  public ODAttributeValue(final String attributeName, final String type, final String value) {
    this.name = attributeName;
    this.type = type;
    this.value = value;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "Attribute:[", "]")
        .add("name='" + this.name + "'")
        .add("type='" + this.type + "'")
        .add("value='" + this.value + "'")
        .toString();
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final ODAttributeValue that)) {
      return false;
    }
    return Objects.equal(this.name, that.name)
        && Objects.equal(this.type, that.type)
        && Objects.equal(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name, this.type, this.value);
  }
}
