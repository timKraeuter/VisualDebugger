package no.hvl.tk.visualDebugger.partsList;

public interface Component {

	/**
	 * Returns true if and only if the receiver directly or indirectly
	 * contains the given component.
	 */
    boolean contains(Component component);

	int getOverallCost();

	void changeCost(int newCost);

	MaterialList getMaterialList();

	void accept(ComponentVisitor v);

	String getName();
}
