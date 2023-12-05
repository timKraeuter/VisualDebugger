package no.hvl.tk.visual.debugger.manueltests.bst;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BSTNode {
  private int value;
  private BSTNode left;
  private BSTNode right;

  public BSTNode(int value) {
    this.value = value;
  }

  public void insert(int newValue) {
    if (newValue > value) {
      if (this.right == null) {
        this.right = new BSTNode(newValue);
        return;
      }
      this.right.insert(newValue);
      return;
    }
    if (this.left == null) {
      this.left = new BSTNode(newValue);
      return;
    }
    this.left.insert(newValue);
  }

  @Override
  public String toString() {
    List<Integer> values = new ArrayList<>();
    this.addValuesInOrder(values);
    return values.stream().map(Object::toString).collect(Collectors.joining(", "));
  }

  private void addValuesInOrder(List<Integer> values) {
    if (left != null) {
      this.left.addValuesInOrder(values);
    }
    values.add(value);
    if (right != null) {
      this.right.addValuesInOrder(values);
    }
  }

  public void setValue(int newValue) {
    this.value = newValue;
  }
}
