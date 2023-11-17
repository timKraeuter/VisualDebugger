package no.hvl.tk.visual.debugger.manueltests.partsList.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import no.hvl.tk.visual.debugger.manueltests.partsList.domain.exception.CycleException;

/** Represent a product in a parts lists, which has an assembly cost and a set of components. */
public class Product extends Component {

  public static Product create(final String name, final int assemblyCost) {
    return new Product(name, assemblyCost, new HashSet<>());
  }

  private final Set<QuantifiedComponent> components;

  protected Product(
      final String name, final int assemblyCost, final Set<QuantifiedComponent> components) {
    super(name, assemblyCost);
    this.components = components;
  }

  /**
   * Adds the given number of components as a part to the receiver.
   *
   * @throws CycleException If adding the part would result in a cyclic parts list.
   */
  public void addPart(final Component part, final int amount) {
    if (part.contains(this)) {
      throw new CycleException();
    }
    final Optional<QuantifiedComponent> componentIfExists =
        this.components.stream()
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
    int ret = this.getComponentCost();
    for (final QuantifiedComponent current : this.components) {
      ret += current.getPrice();
    }
    return ret;
  }
}
