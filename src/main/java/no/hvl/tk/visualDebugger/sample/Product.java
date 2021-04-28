package no.hvl.tk.visualDebugger.sample;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class Product extends ComponentCommon {
	private static final String CycleMessage = "Zyklen sind in der Aufbaustruktur nicht erlaubt!";
	
	public static Product create(final String name, final int price) {
		return new Product(name, price, new HashMap<>());
	}
	
	private final HashMap<String, QuantifiedComponent> components;
	
	protected Product(final String name, final int price, final HashMap<String, QuantifiedComponent> components) {
		super(name, price);
		this.components = components;
	}
	
	@Override
	public void addPart(final Component part, final int amount) {
		if (part.contains(this)) {
			throw new CycleException(CycleMessage);
		}
		final String partName = part.getName();
		if (this.getComponents().containsKey(partName)) {
			final QuantifiedComponent oldQuantification = this.getComponents().get(partName);
			oldQuantification.addQuantity(amount);
		} else {
			this.getComponents().put(partName, QuantifiedComponent.createQuantifiedComponent(amount, part));
		}
	}
	
	private HashMap<String, QuantifiedComponent> getComponents() {
		return this.components;
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
        for (final QuantifiedComponent current : this.getComponents().values()) {
            if (current.contains(component)) {
                return true;
            }
        }
		return false;
	}
	
	@Override
	public Vector<QuantifiedComponent> getDirectParts() {
		return new Vector<>(this.getComponents().values());
	}
	
	@Override
	public int getNumberOfMaterials() {
		int result = 0;
        for (final QuantifiedComponent current : this.getComponents().values()) {
            result = result + current.getNumberOfMaterials();
        }
		return result;
	}
	
	@Override
	public int getOverallPrice() {
		int ret = this.price;
		final Collection<QuantifiedComponent> qComponents = this.components.values();
		for (final QuantifiedComponent current : qComponents) {
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
		for (final QuantifiedComponent current : this.components.values()) {
			current.getComponent().accept(new ComponentVisitor() {
				@Override
				public void handle(final Product product) {
					materialList.add(product.getMaterialList());
				}
				
				@Override
				public void handle(final Material material) {
					materialList.add(material, current.getQuantity());
				}
			});
		}
		return materialList;
	}
}
