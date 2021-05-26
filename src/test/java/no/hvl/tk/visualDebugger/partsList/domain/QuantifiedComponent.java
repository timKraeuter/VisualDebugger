package no.hvl.tk.visualDebugger.partsList.domain;

public class QuantifiedComponent {

    public static QuantifiedComponent create(final int quantity, final Component component) {
        return new QuantifiedComponent(quantity, component);
    }

    private int quantity;
    final private Component component;

    private QuantifiedComponent(final int quantity, final Component component) {
        this.quantity = quantity;
        this.component = component;
    }

    public Component getComponent() {
        return this.component;
    }

    int getQuantity() {
        return this.quantity;
    }

    private void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(final int quantity) {
        this.setQuantity(this.getQuantity() + quantity);
    }

    @Override
    public boolean equals(final Object argument) {
        return super.equals(argument);
    }

    public boolean contains(final Component part) {
        return this.getComponent().contains(part);
    }

    @Override
    public String toString() {
        return String.format("%s x %s", this.getQuantity(), this.component.getName());
    }

    public int getPrice() {
        return this.quantity * this.component.getOverallCost();
    }
}
