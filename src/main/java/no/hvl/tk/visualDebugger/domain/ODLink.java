package no.hvl.tk.visualDebugger.domain;

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

    public ODLink(String type, ODObject from, ODObject to) {
        this.type = type;
        this.from = from;
        this.to = to;
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
}
