package no.hvl.tk.visual.debugger.domain;

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ODPrimitiveRootValue implements Comparable<ODPrimitiveRootValue> {
  @XmlAttribute private final String variableName;
  @XmlAttribute private final String type;
  @XmlAttribute private final String value;

  public ODPrimitiveRootValue(final String variableName, final String type, final String value) {
    this.variableName = variableName;
    this.type = type;
    this.value = value;
  }

  public String getVariableName() {
    return this.variableName;
  }

  public String getType() {
    return this.type;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public int compareTo(@NotNull final ODPrimitiveRootValue other) {
    final int varNameComparison = this.getVariableName().compareTo(other.getVariableName());
    if (varNameComparison != 0) {
      return varNameComparison;
    }
    // Null-safe since values could be null.
    final int valueComparison = StringUtils.compare(this.getValue(), other.getValue());
    if (valueComparison != 0) {
      return valueComparison;
    }
    return this.getType().compareTo(other.getType());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ODPrimitiveRootValue that)) return false;
    return Objects.equal(variableName, that.variableName)
        && Objects.equal(type, that.type)
        && Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(variableName, type, value);
  }
}
