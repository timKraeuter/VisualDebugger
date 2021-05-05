package no.hvl.tk.visualDebugger.partsList;

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
	public MaterialList getMaterialList() {
		final MaterialList ret = MaterialList.create();
		ret.add(QuantifiedComponent.create(1, this));
		return ret;
	}
	
	@Override
	public void accept(final ComponentVisitor v) {
		v.handle(this);
	}

}
