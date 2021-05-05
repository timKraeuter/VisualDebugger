package no.hvl.tk.visualDebugger.partsList;

public abstract class ComponentCommon implements Component {
	private final String name;
	protected int cost;
	
	protected ComponentCommon(final String name, final int cost) {
		this.name = name;
		this.cost = cost;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public void changeCost(final int newCost) {
		this.cost = newCost;
	}
}
