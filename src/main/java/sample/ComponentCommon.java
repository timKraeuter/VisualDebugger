package sample;

public abstract class ComponentCommon implements Component {
	private final String name;
	protected int price;
	
	protected ComponentCommon(final String name, final int price) {
		this.name = name;
		this.price = price;
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
	public boolean equals(final Object argument) {
		return super.equals(argument);
	}
	
	@Override
	public void changePrice(final int newPrice) {
		this.price = newPrice; // TODO observer anpassen spater
	}
}
