package no.hvl.tk.visualDebugger.sample;

import java.util.Vector;

public interface Component {
	/**
	 * * Adds amount pieces of the component part as subparts of the receiver. *
	 * * @throws Exception * If adding part as subpart of whole violates the
	 * hierarchy * contraint of the partslist represented by the receiver.
	 */
    void addPart(Component part, int amount) throws UnknownComponentException;
	
	/**
	 * * Returns true if and only if the receiver directly or indirectly
	 * contains * the given component.
	 */
    boolean contains(Component component);
	
	/**
	 * * Returns the list of all quantified parts that are direct sub-parts of
	 * the * receiver.
	 */
	Vector<QuantifiedComponent> getDirectParts();
	
	/**
	 * * Returns the total number of all materials that are directly or
	 * indirectly * parts of the receiver.
	 */
	int getNumberOfMaterials();
	
	String getName();
	
	int getOverallPrice();
	
	void changePrice(int newPrice);
	
	void accept(ComponentVisitor v);
	
	MaterialList getMaterialList();
}
