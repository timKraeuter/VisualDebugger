package no.hvl.tk.visual.debugger.manueltests.bst;

public class BSTNode {
  private final int value;
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


  public static void main(String[] args) {
    BSTNode root = new BSTNode(50);
    root.insert(70);
    root.insert(30);
    root.insert(20);
    root.insert(40);
    root.insert(80);
    root.insert(60);
    System.out.println(root);
  }
}
