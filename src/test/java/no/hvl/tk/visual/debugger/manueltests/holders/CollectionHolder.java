package no.hvl.tk.visual.debugger.manueltests.holders;

import java.util.List;

public class CollectionHolder<T> {
  private final List<T> list;

  public CollectionHolder(final List<T> list) {
    this.list = list;
  }
}
