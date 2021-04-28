package no.hvl.tk.visualDebugger.sample;

public interface ComponentVisitor {
	void handle(Material material);
	
	void handle(Product product);
}
