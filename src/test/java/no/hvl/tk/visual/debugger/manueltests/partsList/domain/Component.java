package no.hvl.tk.visual.debugger.manueltests.partsList.domain;

public interface Component {

    /**
     * Returns true if and only if the receiver directly or indirectly
     * contains the given component.
     */
    boolean contains(Component component);

    int getOverallCost();

    void accept(ComponentVisitor v);

    String getName();
}
