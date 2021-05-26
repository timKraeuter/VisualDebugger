package no.hvl.tk.visual.debugger.partsList.domain;

public abstract class ComponentCommon implements Component {
    private final String name;
    protected int cost;

    protected ComponentCommon(final String name, final int cost) {
        this.name = name;
        this.cost = cost;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
