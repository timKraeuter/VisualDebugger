package sample;

public interface ComponentVisitor {
	void handle(Material material);
	
	void handle(Product product);
}
