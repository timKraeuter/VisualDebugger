package no.hvl.tk.visual.debugger.manueltests.partsList.domain;

/** Represents a component in parts lists, which has a name and cost. */
public abstract class Component {
  private final String name;
  private final int cost;

  protected Component(final String name, final int cost) {
    this.name = name;
    this.cost = cost;
  }

  /**
   * Returns true if and only if the receiver directly or indirectly contains the given component.
   */
  public abstract boolean contains(Component component);

  /** Get the overall cost of the component including material and assembly cost. */
  public abstract int getOverallCost();

  /** Get the name of the component. */
  public String getName() {
    return this.name;
  }

  public int getComponentCost() {
    return cost;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
