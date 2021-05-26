package no.hvl.tk.visualDebugger.partsList.domain;

public interface ComponentVisitor {
    void handle(Material material);

    void handle(Product product);
}
