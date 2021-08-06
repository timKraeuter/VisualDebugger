package no.hvl.tk.visual.debugger.domain;

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Represents a link in an object diagram.
 */
public class ODLink implements Comparable<ODLink> {
    @XmlID
    public final String id;

    /**
     * Name of the association this link is typed in.
     */
    @XmlElement
    private final String type;

    @XmlIDREF
    private final ODObject from;

    @XmlIDREF
    private final ODObject to;

    public ODLink(final ODObject from, final ODObject to, final String type) {
        this.id = UUID.randomUUID().toString();
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
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final var odLink = (ODLink) o;
        return Objects.equal(this.type, odLink.type) && Objects.equal(this.from, odLink.from) && Objects.equal(this.to, odLink.to);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.type, this.from, this.to);
    }
}
