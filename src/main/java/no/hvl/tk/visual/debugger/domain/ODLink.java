package no.hvl.tk.visual.debugger.domain;

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;

/** Represents a link between two objects in an object diagram. */
public class ODLink implements Comparable<ODLink> {

  /** Name of the association this link is typed in. */
  @XmlAttribute private final String type;

  @XmlIDREF @XmlAttribute private final ODObject from;

  @XmlIDREF @XmlAttribute private final ODObject to;

  public ODLink(final ODObject from, final ODObject to, final String type) {
    this.from = from;
    this.to = to;
    this.type = type;
  }

  public String getType() {
    return this.type;
  }

  public ODObject getFrom() {
    return this.from;
  }

  public ODObject getTo() {
    return this.to;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "Link[", "]")
        .add("type='" + this.type + "'")
        .add("from=" + this.from.getVariableName())
        .add("to=" + this.to.getVariableName())
        .toString();
  }

  @Override
  public int compareTo(@NotNull final ODLink odLink) {
    final int fromComparison = this.from.compareTo(odLink.from);
    if (fromComparison != 0) {
      return fromComparison;
    }
    final int toComparison = this.to.compareTo(odLink.to);
    if (toComparison != 0) {
      return toComparison;
    }
    return this.type.compareTo(odLink.type);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final ODLink odLink)) {
      return false;
    }
    return Objects.equal(this.type, odLink.type)
        && Objects.equal(this.from, odLink.from)
        && Objects.equal(this.to, odLink.to);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.type, this.from, this.to);
  }

  @XmlID
  @XmlAttribute
  public String getId() {
    return "Link_"
        + this.from.getId()
        + "_to_"
        + this.to.getId()
        + "_type_"
        // Ids are not allowed to contain "$".
        + this.type.replace('$', '_');
  }
}
