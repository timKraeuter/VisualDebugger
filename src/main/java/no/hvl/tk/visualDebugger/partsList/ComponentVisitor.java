package no.hvl.tk.visualDebugger.partsList;

public interface ComponentVisitor {
	void handle(Material material);
	
	void handle(Product product);
}
