package no.hvl.tk.visualDebugger.domain;

import java.util.StringJoiner;

/**
 * Represents a link in an object diagram.
 */
public class ODLink {

    /**
     * Name of the association this link is typed in.
     */
    private final String type;
    private final String variableName;

    private final ODObject from;
    private final ODObject to;

    public ODLink(ODObject from, ODObject to, String type, String variableName) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
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
                .add("variableName='" + variableName + "'")
                .add("from=" + from.getVariableName())
                .add("to=" + to.getVariableName())
                .toString();
    }
}
