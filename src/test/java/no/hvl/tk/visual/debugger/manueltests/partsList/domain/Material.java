package no.hvl.tk.visual.debugger.manueltests.partsList.domain;

/**
 * Represent a material which can be part of a parts list.
 * A material has an associated material cost and name.
 */
public class Material extends Component {
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
        return this.getComponentCost();
    }

}
