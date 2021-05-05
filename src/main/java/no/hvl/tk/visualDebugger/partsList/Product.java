package no.hvl.tk.visualDebugger.partsList;

import no.hvl.tk.visualDebugger.partsList.exception.CycleException;

import java.util.*;

public class Product extends ComponentCommon {
	private static final String CycleMessage = "Cycles are not allowed in a parts list!";
	
	public static Product create(final String name, final int assemblyCost) {
		return new Product(name, assemblyCost, new HashSet<>());
	}
	
	private final Set<QuantifiedComponent> components;
	
	protected Product(final String name, final int assemblyCost, final Set<QuantifiedComponent> components) {
		super(name, assemblyCost);
		this.components = components;
	}
	
	/**
	 * Adds the given number of components as a part to the receiver. *
	 * @throws CycleException If adding the part would result in a cyclic parts list.
	 */
	public void addPart(final Component part, final int amount) {
		if (part.contains(this)) {
			throw new CycleException(CycleMessage);
		}
		final Optional<QuantifiedComponent> componentIfExists = this.components.stream()
				.filter(component -> component.getComponent().equals(part))
				.findFirst();
		if (componentIfExists.isPresent()) {
			componentIfExists.get().addQuantity(amount);
		} else {
			this.components.add(QuantifiedComponent.create(amount, part));
		}
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean contains(final Component component) {
		if (this.equals(component)) {
			return true;
		}
		return this.components.stream()
				.anyMatch(quantifiedComponent -> quantifiedComponent.contains(component));
	}
	
	@Override
	public int getOverallCost() {
		int ret = this.cost;
		for (final QuantifiedComponent current : this.components) {
			ret += current.getPrice();
		}
		return ret;
	}
	
	@Override
	public void accept(final ComponentVisitor v) {
		v.handle(this);
	}
	
	@Override
	public MaterialList getMaterialList() {
		final MaterialList materialList = MaterialList.create();
		for (final QuantifiedComponent current : this.components) {
			// TODO Multiplication of material lists is missing here!
			current.getComponent().accept(new ComponentVisitor() {
				@Override
				public void handle(final Product product) {
					materialList.add(product.getMaterialList());
				}
				
				@Override
				public void handle(final Material material) {
					materialList.add(current);
				}
			});
		}
		return materialList;
	}
}
