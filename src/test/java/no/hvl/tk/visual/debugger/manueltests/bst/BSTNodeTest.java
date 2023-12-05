package no.hvl.tk.visual.debugger.manueltests.bst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BSTNodeTest {
  @Test
  void insertTest() {
    BSTNode root = new BSTNode(50);
    root.insert(30);
    root.insert(70);
    root.insert(60);
    root.insert(80);
    root.setValue(49);
    String treeAsString = root.toString();
    assertEquals("30, 49, 60, 70, 80", treeAsString);
    root.insert(99);
  }
}
