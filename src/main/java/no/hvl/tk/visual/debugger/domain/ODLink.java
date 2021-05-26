package no.hvl.tk.visual.debugger.domain;

import java.util.StringJoiner;

/**
 * Represents a link in an object diagram.
 */
public class ODLink {

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
}
