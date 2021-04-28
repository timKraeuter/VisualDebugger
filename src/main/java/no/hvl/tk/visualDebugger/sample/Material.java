package no.hvl.tk.visualDebugger.sample;

import java.util.Vector;

public class Material extends ComponentCommon {
	private static final String UnstructuredMaterialMessage = "Materialien haben keine Struktur!";
	
	public static Material create(final String name, final int price) {
		return new Material(name, price);
	}
	
	private Material(final String name, final int price) {
		super(name, price);
	}
	
	@Override
	public void addPart(final Component part, final int amount) throws UnknownComponentException {
		throw new UnknownComponentException(UnstructuredMaterialMessage);
	}
	
	@Override
	public boolean contains(final Component component) {
		return this.equals(component);
	}
	
	@Override
	public Vector<QuantifiedComponent> getDirectParts() {
		return new Vector<>();
	}
	
	@Override
	public int getNumberOfMaterials() {
		return 1;
	}
	
	@Override
	public int getOverallPrice() {
		return this.price;
	}
	
	@Override
	public void accept(final ComponentVisitor v) {
		v.handle(this);
	}
	
	@Override
	public MaterialList getMaterialList() {
		final MaterialList ret = MaterialList.create();
		ret.add(this, 1);
		return ret;
	}
}
