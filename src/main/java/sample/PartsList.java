package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * * Represents a hierarchical partslist as a mapping from unique part names to
 * * components.
 */
public class PartsList {
	private static final String DoubleDefinitionMessage = "Name bereits vorhanden!";
	private static final String UnknownComponentMessage = "Unbekannte Komponente: ";
	
	/** * @return An empty partslist. */
	public static PartsList create() {
		return new PartsList(new HashMap<>());
	}
	
	final private Map<String, Component> componentsMap;
	
	private PartsList(final Map<String, Component> componentsMap) {
		this.componentsMap = componentsMap;
	}
	
	@Override
	public boolean equals(final Object argument) {
		return super.equals(argument);
	}
	
	private Map<String, Component> getComponentsMap() {
		return this.componentsMap;
	}
	
	/**
	 * * Creates a new material with the given name and price as a component of
	 * * the receiver. * * @return * * @throws Exception * If the provided name
	 * is already used for another component of * the receiver.
	 */
	public Material createMaterial(final String name, final int price) throws Exception {
		if (this.getComponentsMap().containsKey(name)) {
			throw new Exception(DoubleDefinitionMessage);
		}
		final Material newMaterial = Material.create(name, price);
		this.getComponentsMap().put(name, newMaterial);
		return newMaterial;
	}
	
	/**
	 * * Creates a new product with the given name and price as a component of
	 * the * receiver. * * @return * * @throws Exception * If the provided name
	 * is already used for another component of * the receiver.
	 */
	public Product createProduct(final String name, final int price) throws CycleException {
		if (this.getComponentsMap().containsKey(name)) {
			throw new CycleException(DoubleDefinitionMessage);
		}
		final Product newProduct = Product.create(name, price);
		this.getComponentsMap().put(name, newProduct);
		return newProduct;
	}
	
	/**
	 * * Adds amount pieces of the component part as subparts of the component *
	 * whole. * * @throws Exception *
	 * <ol>
	 * *
	 * <li>If whole or part are not contained in the partslist of * the
	 * receiver.</li> *
	 * <li>If adding part as subpart of whole violates the hierarchy * contraint
	 * of the partslist represented by the receiver.</li> *
	 * </ol>
	 */
	public void addPart(final Component whole, final Component part, final int amount)
			throws UnknownComponentException {
		if (!this.getComponentsMap().containsValue(whole)) {
			throw new UnknownComponentException(UnknownComponentMessage + whole.getName());
		}
		if (!this.getComponentsMap().containsValue(part)) {
			throw new UnknownComponentException(UnknownComponentMessage + part.getName());
		}
		whole.addPart(part, amount);
	}
	
	/**
	 * * Returns the number of materials that are directly or indirectly parts
	 * of * the given component. * * @throws Exception * If component is not
	 * contained in the partslist of the * receiver.
	 */
	public int getMaterialCount(final Component component) throws Exception {
		if (!this.getComponentsMap().containsValue(component)) {
			throw new Exception(UnknownComponentMessage + component.getName());
		}
		return component.getNumberOfMaterials();
	}
	
	public Vector<Component> getComponents() {
		return new Vector<>(this.getComponentsMap().values());
	}
	
	/**
	 * * Returns the list of quantified parts that are the direct subparts of *
	 * component. * * @throws Exception * If component is not contained in the
	 * partslist of the * receiver.
	 */
	public Vector<QuantifiedComponent> getParts(final Component component) throws Exception {
		if (!this.getComponentsMap().containsValue(component)) {
			throw new Exception(UnknownComponentMessage + component.getName());
		}
		return component.getDirectParts();
	}
	
	/** * liefert die MaterialList von {@code component} */
	public Vector<QuantifiedComponent> getMaterialList(final Component component) {
		final MaterialList materialList = component.getMaterialList();
		return materialList.toLoeweList();
	}
	
	/** * liefert den Gesamtpreis von {@code component} */
	public int getOverallPrice(final Component component) {
		return component.getOverallPrice();
	}
	
	/** * ändert den Preis der {@code component} auf {@code newPrice}} */
	public void changePrice(final Component component, final int newPrice) {
		component.changePrice(newPrice);
	}
	
	public Component getComponent(final String name) {
		final Component component = this.componentsMap.get(name);
		if (component == null) {
			throw new UnknownComponentException(UnknownComponentMessage);
		}
		return component;
	}
}
