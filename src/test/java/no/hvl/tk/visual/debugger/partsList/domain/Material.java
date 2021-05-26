package no.hvl.tk.visual.debugger.partsList.domain;

public class Material extends ComponentCommon {
    public static Material create(final String name, final int materialCost) {
        return new Material(name, materialCost);
    }

    private Material(final String name, final int materialCost) {
        super(name, materialCost);
    }

    @Override
    public boolean contains(final Component component) {
        return this.equals(component);
    }

    @Override
    public int getOverallCost() {
        return this.cost;
    }

    @Override
    public void accept(final ComponentVisitor v) {
        v.handle(this);
    }

}
