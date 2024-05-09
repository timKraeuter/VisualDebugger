package no.hvl.tk.visual.debugger.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public record ODPrimitiveRootValue(
    @JsonProperty String variableName, @JsonProperty String type, @JsonProperty String value)
    implements Comparable<ODPrimitiveRootValue> {

  @Override
  public int compareTo(@NotNull final ODPrimitiveRootValue other) {
    final int varNameComparison = this.variableName().compareTo(other.variableName());
    if (varNameComparison != 0) {
      return varNameComparison;
    }
    // Null-safe since values could be null.
    final int valueComparison = StringUtils.compare(this.value(), other.value());
    if (valueComparison != 0) {
      return valueComparison;
    }
    return this.type().compareTo(other.type());
  }
}
