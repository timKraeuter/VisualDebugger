package no.hvl.tk.visual.debugger.domain;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

/**
 * Represents a link in an object diagram.
 */
public class ODLink implements Comparable<ODLink> {

    /**
     * Name of the association this link is typed in.
     */
    private final String type;

    private final ODObject from;
    private final ODObject to;

    public ODLink(ODObject from, ODObject to, String type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ODObject getFrom() {
        return from;
    }

    public ODObject getTo() {
        return to;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Link[", "]")
                .add("type='" + type + "'")
                .add("from=" + from.getVariableName())
                .add("to=" + to.getVariableName())
                .toString();
    }

    @Override
    public int compareTo(@NotNull ODLink odLink) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ODLink odLink = (ODLink) o;
        return Objects.equal(type, odLink.type) && Objects.equal(from, odLink.from) && Objects.equal(to, odLink.to);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, from, to);
    }
}
