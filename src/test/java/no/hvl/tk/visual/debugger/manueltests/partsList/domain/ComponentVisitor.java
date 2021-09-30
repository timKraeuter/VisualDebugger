package no.hvl.tk.visual.debugger.manueltests.partsList.domain;

public interface ComponentVisitor {
    void handle(Material material);

    void handle(Product product);
}
