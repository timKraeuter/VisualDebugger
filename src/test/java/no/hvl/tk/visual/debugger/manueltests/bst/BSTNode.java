package no.hvl.tk.visual.debugger.manueltests.bst;

public class BSTNode<T extends Comparable<T>> {
  private final T value;
  private BSTNode<T> left;
  private BSTNode<T> right;

  public BSTNode(T value) {
    this.value = value;
  }

  public void insert(T newValue) {
    if (greater(newValue)) {
      if (this.right == null) {
        this.right = new BSTNode<>(newValue);
        return;
      }
      this.right.insert(newValue);
      return;
    }
    if (this.left == null) {
      this.left = new BSTNode<>(newValue);
      return;
    }
    this.left.insert(newValue);
  }

  private boolean greater(T newValue) {
    return newValue.compareTo(value) > 0;
  }

  public static void main(String[] args) {
    BSTNode<Integer> root = new BSTNode<>(50);
    root.insert(70);
    root.insert(30);
    root.insert(20);
    root.insert(40);
    root.insert(80);
    root.insert(60);
    System.out.println(root);
  }
}
